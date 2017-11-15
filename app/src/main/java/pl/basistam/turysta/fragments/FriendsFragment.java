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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.Page;
import pl.basistam.turysta.json.UserSimpleDetails;
import pl.basistam.turysta.service.UserService;

public class FriendsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        AppCompatImageButton btnSearch = view.findViewById(R.id.searchButton);
        final ListView lvFriends = view.findViewById(R.id.friends_list);
        EditText edtSearchField = view.findViewById(R.id.searchField);
        final String pattern = edtSearchField.getText().toString();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
                accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(final AccountManagerFuture<Bundle> future) {
                        new AsyncTask<Void, Void, Page<UserSimpleDetails>>() {
                            @Override
                            protected Page<UserSimpleDetails> doInBackground(Void... params) {
                                try {
                                    Bundle bundle = future.getResult();
                                    final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                    UserService userService = new UserService();
                                    return userService.obtainUsersByPattern(authtoken, pattern, 1, 15);
                                } catch (OperationCanceledException | IOException | AuthenticatorException | ServerConnectionException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(Page<UserSimpleDetails> userDetails) {
                                /*final List<HashMap<String, String>> items = new ArrayList<>(userDetails.getContent().size());
                                for (UserSimpleDetails u : userDetails.getContent()) {
                                    HashMap<String, String> entry = new HashMap<>();
                                    entry.put("login", u.getLogin());
                                    entry.put("name", u.getFirstName() + " " + u.getLastName());
                                    items.add(entry);
                                }
                                String[] from = {"login", "name"};
                                int[] to = {R.id.flag, R.id.txt};*/

                                List<String> content = new ArrayList<>(userDetails.getSize());
                                for (UserSimpleDetails u: userDetails.getContent()) {
                                    content.add(u.getFirstName() + " " + u.getLastName() + " (" + u.getLogin() + ")");
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        getActivity().getBaseContext(),
                                        R.layout.friends_fragment,
                                        content
                                );
                                lvFriends.setAdapter(arrayAdapter);
                            }
                        }.execute();
                    }
                }, null);

            }
        });
    }
}
