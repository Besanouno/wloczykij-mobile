package pl.basistam.wloczykij.fragments;

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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.adapters.ExpandableListAdapter;
import pl.basistam.wloczykij.auth.AccountGeneral;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.components.utils.KeyboardUtils;
import pl.basistam.wloczykij.dto.FoundPeopleGroup;
import pl.basistam.wloczykij.dto.Group;
import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.dto.Relation;
import pl.basistam.wloczykij.service.RelationsChangesHandlerImpl;
import pl.basistam.wloczykij.service.UserService;
import pl.basistam.wloczykij.service.interfaces.RelationsChangesHandler;

public class RelationsFragment extends Fragment {

    private final RelationsChangesHandler relationsChangesHandler = new RelationsChangesHandlerImpl();
    SparseArray<Group> groups = new SparseArray<Group>();
    ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_relation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initSearchButton(view);
        initBackButton(view);
        initFriendList(view);
        initExpandableListView(view);
    }

    private void initExpandableListView(View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final FoundPeopleGroup group = (FoundPeopleGroup) groups.get(1);
                if (expandableListView.getAdapter() != null) {
                    if (expandableListView.getLastVisiblePosition() == expandableListView.getAdapter().getCount() - 1 &&
                            expandableListView.getChildAt(expandableListView.getChildCount() - 1).getBottom() <= expandableListView.getHeight()
                            && group != null && group.getTotalNumber() >= 15 && group.getChildren().size() < group.getTotalNumber()) {
                        AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
                        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                                new AccountManagerCallback<Bundle>() {
                                    @Override
                                    public void run(final AccountManagerFuture<Bundle> future) {
                                        final String pattern = edtSearchField.getText().toString();
                                        new AsyncTask<Void, Void, Page<Relation>>() {
                                            @Override
                                            protected Page<Relation> doInBackground(Void... params) {
                                                try {
                                                    Bundle bundle = future.getResult();
                                                    final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                                    return UserService.getInstance()
                                                            .userService()
                                                            .getUserSimpleDetailsByPattern("Bearer " + authtoken, pattern, group.getAndIncrementLastPage(), 15)
                                                            .execute()
                                                            .body();
                                                } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                                    e.printStackTrace();
                                                    return null;
                                                }
                                            }

                                            @Override
                                            protected void onPostExecute(Page<Relation> users) {
                                                List<Relation> content = new ArrayList<>(users.getSize());
                                                for (Relation u : users.getContent()) {
                                                    Relation relation = new Relation(u.getName(), u.getLogin(), u.isFriend());
                                                    relationsChangesHandler.adjustRelationToChanges(relation);
                                                    content.add(relation);
                                                }
                                                group.getChildren().addAll(content);
                                                group.setLastPage(users.getNumber());
                                                adapter.notifyDataSetChanged();
                                            }
                                        }.execute();
                                    }
                                }, null);
                    }
                }
            }
        });
    }

    private void initFriendList(final View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);

        AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                            new AsyncTask<Void, Void, List<Relation>>() {

                                @Override
                                protected List<Relation> doInBackground(Void... params) {
                                    try {
                                        return UserService.getInstance()
                                                .userService()
                                                .getRelations("Bearer " + authToken)
                                                .execute()
                                                .body();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return Collections.emptyList();
                                }

                                @Override
                                protected void onPostExecute(List<Relation> relations) {
                                    List<Relation> content = new ArrayList<>(relations.size());
                                    for (Relation r : relations) {
                                        content.add(new Relation(r.getName(), r.getLogin(), true));
                                    }
                                    Group group = new Group("Twoi znajomi");
                                    group.setChildren(content);
                                    groups.append(0, group);
                                    adapter = new ExpandableListAdapter(groups, getActivity(), relationsChangesHandler);
                                    expandableListView.setAdapter(adapter);
                                    expandableListView.expandGroup(0);
                                }
                            }.execute();
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);

    }

    private void initBackButton(final View view) {
        final Button button = view.findViewById(R.id.btn_save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                                UserService.getInstance()
                                                        .userService()
                                                        .updateRelations("Bearer " + authtoken, changes)
                                                        .execute();
                                            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                    }.execute();
                                }
                            }, null);
                }
                KeyboardUtils.hide(getActivity().getBaseContext(), view);
                getFragmentManager().popBackStack();

            }
        });
    }

    private void initSearchButton(View view) {
        AppCompatImageButton btnSearch = view.findViewById(R.id.btn_search);
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);
        KeyboardUtils.hide(getActivity().getBaseContext(), view);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
                accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                        new AccountManagerCallback<Bundle>() {
                            @Override
                            public void run(final AccountManagerFuture<Bundle> future) {
                                final String pattern = edtSearchField.getText().toString();
                                new AsyncTask<Void, Void, Page<Relation>>() {
                                    @Override
                                    protected Page<Relation> doInBackground(Void... params) {
                                        try {
                                            Bundle bundle = future.getResult();
                                            final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                            return UserService.getInstance()
                                                    .userService()
                                                    .getUserSimpleDetailsByPattern("Bearer " + authtoken, pattern, 0, 15)
                                                    .execute()
                                                    .body();
                                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                            e.printStackTrace();
                                            return null;
                                        }
                                    }

                                    @Override
                                    protected void onPostExecute(Page<Relation> users) {
                                        List<Relation> content = new ArrayList<>(users.getSize());
                                        for (Relation u : users.getContent()) {
                                            Relation relation = new Relation(u.getName(), u.getLogin(), u.isFriend());
                                            relationsChangesHandler.adjustRelationToChanges(relation);
                                            content.add(relation);
                                        }
                                        FoundPeopleGroup group = new FoundPeopleGroup("Wyszukiwanie");
                                        group.setChildren(content);
                                        group.setLastPage(users.getNumber());
                                        group.setTotalNumber(users.getTotalElements());
                                        groups.append(1, group);
                                        adapter = new ExpandableListAdapter(groups, getActivity(), relationsChangesHandler);
                                        expandableListView.setAdapter(adapter);
                                        expandableListView.expandGroup(1);
                                    }
                                }.execute();
                            }
                        }, null);
            }
        });

    }
}
