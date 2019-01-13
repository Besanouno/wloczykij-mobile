package pl.basistam.wloczykij.fragments.events;

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

import java.util.Collections;
import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.actions.EventUsersDataSet;
import pl.basistam.wloczykij.adapters.EventUsersAdapter;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.components.utils.KeyboardUtils;
import pl.basistam.wloczykij.items.EventUserItem;
import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.groups.FoundUsersGroup;
import pl.basistam.wloczykij.groups.RelationsGroup;
import pl.basistam.wloczykij.service.EventUsers;
import pl.basistam.wloczykij.service.Callback;

public class EventUsersFragment extends Fragment {

    private EventUsersDataSet dataSet;
    private EventUsersAdapter adapter;
    private EventUsers eventUsers;
    private SparseArray<RelationsGroup<EventUserItem>> groups = new SparseArray<>();
    private Callback callback;
    private final int RELATIONS_GROUP_INDEX = 0;
    private final int FOUND_USERS_GROUP_INDEX = 1;

    private ExpandableListView mainListView;

    public static EventUsersFragment create(EventUsers eventUsers, Callback callback) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("eventUsers", eventUsers);
        bundle.putSerializable("callback", callback);
        EventUsersFragment fragment = new EventUsersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventUsers = (EventUsers) getArguments().getSerializable("eventUsers");
        callback = (Callback) getArguments().getSerializable("callback");
        if (eventUsers == null) {
            eventUsers = new EventUsers(Collections.<EventUserItem>emptyList());
        }
        dataSet = new EventUsersDataSet(getActivity().getBaseContext(), eventUsers.getParticipants());
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mainListView = view.findViewById(R.id.elv_relations);
        initAdapter();
        initFriends(view);
        initSearchAction(view);
        initSaveButton(view);
        initListViewScrollListener(view);
    }

    private void initAdapter() {
        adapter = new EventUsersAdapter(groups, getActivity(), true, eventUsers);
        mainListView.setAdapter(adapter);
    }

    private void initFriends(View view) {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, List<EventUserItem>>() {
                    @Override
                    protected List<EventUserItem> doInBackground(String... params) {
                        String authToken = params[0];
                        return dataSet.getFriends(authToken);
                    }

                    @Override
                    protected void onPostExecute(List<EventUserItem> friends) {
                        createFriendsGroup(friends);
                    }
                });
    }

    private void createFriendsGroup(List<EventUserItem> friends) {
        RelationsGroup<EventUserItem> friendsGroup = new RelationsGroup<>("Twoi znajomi");
        friendsGroup.setChildren(friends);
        groups.append(RELATIONS_GROUP_INDEX, friendsGroup);
        adapter.notifyDataSetChanged();
        mainListView.expandGroup(RELATIONS_GROUP_INDEX);
    }

    private void initSearchAction(final View view) {
        final AppCompatImageButton btnSearch = view.findViewById(R.id.btn_search);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hide(getActivity().getBaseContext(), view);
                downloadUsersFirstPart(edtSearchField.getText().toString());
            }
        });

    }

    private void downloadUsersFirstPart(final String pattern) {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, Page<EventUserItem>>() {

                    @Override
                    protected Page<EventUserItem> doInBackground(String... params) {
                        final String authtoken = params[0];
                        return dataSet.getAllUsers(authtoken, pattern, 0, 15);
                    }

                    @Override
                    protected void onPostExecute(Page<EventUserItem> users) {
                        createFoundUsersGroup(users);
                    }
                });
    }

    private void createFoundUsersGroup(Page<EventUserItem> eventUsers) {
        FoundUsersGroup<EventUserItem> foundUsersGroup = new FoundUsersGroup<>("Wyszukiwanie");
        foundUsersGroup.setChildren(eventUsers.getContent());
        foundUsersGroup.setLastPage(eventUsers.getNumber());
        foundUsersGroup.setTotalNumber(eventUsers.getTotalElements());
        groups.append(FOUND_USERS_GROUP_INDEX, foundUsersGroup);
        adapter.notifyDataSetChanged();
        mainListView.expandGroup(FOUND_USERS_GROUP_INDEX);
        mainListView.collapseGroup(RELATIONS_GROUP_INDEX);
    }


    private void initSaveButton(View view) {
        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.run();
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void initListViewScrollListener(View view) {
        final EditText edtSearchField = view.findViewById(R.id.edt_search);

        mainListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final FoundUsersGroup<EventUserItem> group = (FoundUsersGroup<EventUserItem>) groups.get(1);
                if (isEndOfListReached() && group != null && group.canDownloadMore()) {
                    final String pattern = edtSearchField.getText().toString();
                    downloadNextPart(group, pattern);
                }
            }

            private void downloadNextPart(final FoundUsersGroup<EventUserItem> group, final String pattern) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<EventUserItem>>() {
                            @Override
                            protected Page<EventUserItem> doInBackground(String... params) {
                                String authToken = params[0];
                                return dataSet.getAllUsers(authToken, pattern, group.getAndIncrementLastPage(), 15);
                            }

                            @Override
                            protected void onPostExecute(Page<EventUserItem> users) {
                                group.getChildren().addAll(users.getContent());
                                group.setLastPage(users.getNumber());
                                adapter.notifyDataSetChanged();
                            }
                        }
                );
            }

            private boolean isEndOfListReached() {
                return mainListView.getAdapter() != null &&
                        mainListView.getLastVisiblePosition() == mainListView.getAdapter().getCount() - 1 &&
                        mainListView.getChildAt(mainListView.getChildCount() - 1) != null && mainListView.getChildAt(mainListView.getChildCount() - 1).getBottom() <= mainListView.getHeight();
            }
        });
    }
}