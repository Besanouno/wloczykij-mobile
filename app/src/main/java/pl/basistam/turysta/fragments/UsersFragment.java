package pl.basistam.turysta.fragments;

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

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.UsersFragmentManager;
import pl.basistam.turysta.adapters.UsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.FoundPeopleGroup;
import pl.basistam.turysta.dto.Group;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.service.UsersStatusesChangesHandlerImpl;
import pl.basistam.turysta.service.interfaces.UsersStatusesChangesHandler;

public class UsersFragment extends Fragment {

    private final UsersStatusesChangesHandler usersStatusesChangesHandler = new UsersStatusesChangesHandlerImpl();
    private SparseArray<Group> groups = new SparseArray<Group>();
    private UsersAdapter adapter;
    private UsersFragmentManager usersFragmentManager;

    public static UsersFragment newInstance(UsersFragmentManager usersFragmentManager) {
        UsersFragment usersFragment = new UsersFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("usersFragmentManager", usersFragmentManager);
        usersFragment.setArguments(bundle);
        return usersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        usersFragmentManager = (UsersFragmentManager) getArguments().getSerializable("usersFragmentManager");
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

                if (isEndOfListReached() && group != null && group.canDownloadMore()) {

                    final String pattern = edtSearchField.getText().toString();

                    LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                            new AsyncTask<String, Void, Page<UserItem>>() {
                                @Override
                                protected Page<UserItem> doInBackground(String... params) {
                                    String authtoken = params[0];
                                    return usersFragmentManager.getAllUsers(authtoken, pattern, group.getAndIncrementLastPage(), 15);
                                }

                                @Override
                                protected void onPostExecute(Page<UserItem> users) {
                                    List<UserItem> content = new ArrayList<>(users.getSize());
                                    for (UserItem u : users.getContent()) {
                                        UserItem userItem = new UserItem(u.getName(), u.getLogin(), u.getStatus());
                                        usersStatusesChangesHandler.adjustRelationToUnsavedChanges(userItem);
                                        content.add(userItem);
                                    }
                                    group.getChildren().addAll(content);
                                    group.setLastPage(users.getNumber());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                    );
                }
            }

            private boolean isEndOfListReached() {
                return expandableListView.getAdapter() != null &&
                        expandableListView.getLastVisiblePosition() == expandableListView.getAdapter().getCount() - 1 &&
                        expandableListView.getChildAt(expandableListView.getChildCount() - 1).getBottom() <= expandableListView.getHeight();
            }
        });
    }

    private void initFriendList(final View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, List<UserItem>>() {

                    @Override
                    protected List<UserItem> doInBackground(String... params) {
                        String authToken = params[0];
                        return usersFragmentManager.getFriends(authToken);
                    }

                    @Override
                    protected void onPostExecute(List<UserItem> content) {
                        Group group = new Group("Twoi znajomi");
                        group.setChildren(content);
                        groups.append(0, group);
                        adapter = new UsersAdapter(groups, getActivity(), usersStatusesChangesHandler, true);
                        expandableListView.setAdapter(adapter);
                        expandableListView.expandGroup(0);
                    }
                });
    }

    private void initBackButton(final View view) {
        final Button button = view.findViewById(R.id.btn_save);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<UserItem> changes = usersStatusesChangesHandler.getAndClearAllChanges();
                if (usersFragmentManager != null) {
                    usersFragmentManager.postExecute(changes);
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
                final String pattern = edtSearchField.getText().toString();
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<UserItem>>() {

                            @Override
                            protected Page<UserItem> doInBackground(String... params) {
                                final String authtoken = params[0];
                                return usersFragmentManager.getAllUsers(authtoken, pattern, 0, 15);
                            }

                            @Override
                            protected void onPostExecute(Page<UserItem> users) {
                                List<UserItem> content = new ArrayList<>(users.getSize());
                                for (UserItem u : users.getContent()) {
                                    UserItem userItem = new UserItem(u.getName(), u.getLogin(), u.getStatus());
                                    usersStatusesChangesHandler.adjustRelationToUnsavedChanges(userItem);
                                    content.add(userItem);
                                }
                                FoundPeopleGroup group = new FoundPeopleGroup("Wyszukiwanie");
                                group.setChildren(content);
                                group.setLastPage(users.getNumber());
                                group.setTotalNumber(users.getTotalElements());
                                groups.append(1, group);
                                adapter = new UsersAdapter(groups, getActivity(), usersStatusesChangesHandler, true);
                                expandableListView.setAdapter(adapter);
                                expandableListView.expandGroup(1);
                            }
                        });
            }
        });

    }
}
