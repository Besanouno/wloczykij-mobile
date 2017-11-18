package pl.basistam.turysta.fragments;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.FriendsAdapter;
import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserSimpleDetails;
import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.service.RelationsChangesHandlerImpl;
import pl.basistam.turysta.service.UserService;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;
import retrofit2.Response;

public class FriendsFragment extends Fragment {

    private boolean searchingMode = false;
    private FriendsAdapter arrayAdapter;
    private final RelationsChangesHandler relationsChangesHandler = new RelationsChangesHandlerImpl();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initSearchButton(view);
        initBackButton(view);
        initFriendList(view);
    }

    private void initFriendList(final View view) {
        AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true, prepareFriendList(view), null);
    }

    private AccountManagerCallback<Bundle> prepareFriendList(final View view) {
        final ListView friendsList = view.findViewById(R.id.friends_list);

        return new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                    new AsyncTask<Void, Void, List<UserSimpleDetails>>() {

                        @Override
                        protected List<UserSimpleDetails> doInBackground(Void... params) {
                            try {
                                return UserService.getInstance()
                                        .userService()
                                        .getFriends("Bearer " + authToken)
                                        .execute()
                                        .body();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return Collections.emptyList();
                        }

                        @Override
                        protected void onPostExecute(List<UserSimpleDetails> friends) {
                            if (!friends.isEmpty()) {
                                view.findViewById(R.id.lblFriends).setVisibility(View.VISIBLE);
                            }
                            List<Relation> content = new ArrayList<>(friends.size());
                            for (UserSimpleDetails u : friends) {
                                content.add(new Relation(u.getFirstName() + " " + u.getLastName(), u.getLogin(), true));
                            }
                            friendsList.setAdapter(new FriendsAdapter(getActivity(), content, relationsChangesHandler));
                        }
                    }.execute();
                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void initBackButton(final View view) {
        final Button button = view.findViewById(R.id.btn_save);
        final EditText edtSearch = view.findViewById(R.id.searchField);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchingMode) {
                    edtSearch.setText("");
                    searchingMode = false;
                    final List<Relation> changes = relationsChangesHandler.getAndClearAllChanges();
                    if (!changes.isEmpty()) {
                        AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
                        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                                new AccountManagerCallback<Bundle>() {
                                    @Override
                                    public void run(final AccountManagerFuture<Bundle> future) {

                                        new AsyncTask<Void, Void, Void>() {
                                            @Override
                                            protected Void doInBackground(Void... params) {
                                                try {
                                                    Bundle bundle = future.getResult();
                                                    final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                                    Response<Void> status = UserService.getInstance()
                                                            .userService()
                                                            .updateFriends("Bearer " + authtoken, changes)
                                                            .execute();
                                                    System.out.println(status);
                                                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                        }.execute();
                                    }
                                }, null);
                    }
                }
                    KeyboardUtils.hide(getActivity().getBaseContext(), view);
                    getFragmentManager().popBackStack();

            }
        });
    }

    private void initSearchButton(View view) {
        AppCompatImageButton btnSearch = view.findViewById(R.id.btn_search);
        final ListView lvFriends = view.findViewById(R.id.friends_list);
        final EditText edtSearchField = view.findViewById(R.id.searchField);

        searchingMode = true;
        view.findViewById(R.id.lblFriends).setVisibility(View.GONE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
                accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(final AccountManagerFuture<Bundle> future) {
                                final String pattern = edtSearchField.getText().toString();
                                new AsyncTask<Void, Void, Page<UserSimpleDetails>>() {
                                    @Override
                                    protected Page<UserSimpleDetails> doInBackground(Void... params) {
                                        try {
                                            Bundle bundle = future.getResult();
                                            final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                            return UserService.getInstance()
                                                    .userService()
                                                    .getUserSimpleDetailsByPattern("Bearer " + authtoken, pattern, 1, 15)
                                                    .execute()
                                                    .body();
                                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(Page<UserSimpleDetails> userDetails) {
                                        List<Relation> content = new ArrayList<>(userDetails.getSize());
                                        for (UserSimpleDetails u : userDetails.getContent()) {
                                            content.add(new Relation(u.getFirstName() + " " + u.getLastName(), u.getLogin(), false));
                                        }
                                        arrayAdapter = new FriendsAdapter(getActivity(), content, relationsChangesHandler);
                                        lvFriends.setAdapter(arrayAdapter);
                                    }
                                }.execute();
                            }
                        }, null);
            }
        });
    }


}
