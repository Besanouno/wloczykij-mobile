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
import pl.basistam.turysta.actions.EventUsersDataSet;
import pl.basistam.turysta.adapters.EventUsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.EventUsersGroup;
import pl.basistam.turysta.dto.FoundEventUsersGroup;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.service.EventUsersCallback;
import pl.basistam.turysta.service.ParticipantsChangesHandler;

public class EventUsersFragment extends Fragment {

    private EventUsersDataSet dataSet;
    private EventUsersAdapter adapter;
    private ParticipantsChangesHandler participantsChangesHandler;
    private SparseArray<EventUsersGroup> groups = new SparseArray<>();
    private EventUsersCallback callback;
    private final int RELATIONS_GROUP_INDEX = 0;
    private final int FOUND_USERS_GROUP_INDEX = 1;

    public static EventUsersFragment create(ParticipantsChangesHandler participantsChangesHandler, EventUsersCallback callback) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("participantsChangesHandler", participantsChangesHandler);
        bundle.putSerializable("callback", callback);
        EventUsersFragment fragment = new EventUsersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        participantsChangesHandler = (ParticipantsChangesHandler) getArguments().getSerializable("participantsChangesHandler");
        callback = (EventUsersCallback) getArguments().getSerializable("callback");
        dataSet = new EventUsersDataSet(getActivity().getBaseContext(), participantsChangesHandler.getParticipants());
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initAdapter();
        initFriends(view);
        initSearchAction(view);
        initSaveButton(view);
        initListViewScrollListener(view);
    }

    private void initSaveButton(View view) {
        Button btnSave = (Button) view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.run();
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void initAdapter() {
        adapter = new EventUsersAdapter(groups, getActivity(), true, participantsChangesHandler);
    }

    private void initFriends(View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, List<EventUserDto>>() {

                    @Override
                    protected List<EventUserDto> doInBackground(String... params) {
                        String authToken = params[0];
                        return dataSet.getFriends(authToken);
                    }

                    @Override
                    protected void onPostExecute(List<EventUserDto> content) {
                        EventUsersGroup relationsGroup = new EventUsersGroup("Twoi znajomi");
                        relationsGroup.setChildren(content);
                        groups.append(RELATIONS_GROUP_INDEX, relationsGroup);
                        expandableListView.setAdapter(adapter);
                        expandableListView.expandGroup(RELATIONS_GROUP_INDEX);
                    }
                });
    }

    private void initSearchAction(final View view) {
        final AppCompatImageButton btnSearch = view.findViewById(R.id.btn_search);
        final ExpandableListView elvFoundUsers = view.findViewById(R.id.elv_relations);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hide(getActivity().getBaseContext(), view);
                final String pattern = edtSearchField.getText().toString();
                downloadUsersFirstPart(pattern, elvFoundUsers);
            }
        });

    }

    private void downloadUsersFirstPart(final String pattern, final ExpandableListView elvFoundUsers) {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, Page<EventUserDto>>() {

                    @Override
                    protected Page<EventUserDto> doInBackground(String... params) {
                        final String authtoken = params[0];
                        return dataSet.getAllUsers(authtoken, pattern, 0, 15);
                    }

                    @Override
                    protected void onPostExecute(Page<EventUserDto> users) {
                        List<EventUserDto> content = new ArrayList<>(users.getContent());
                        FoundEventUsersGroup group = new FoundEventUsersGroup("Wyszukiwanie");
                        group.setChildren(content);
                        group.setLastPage(users.getNumber());
                        group.setTotalNumber(users.getTotalElements());
                        groups.append(FOUND_USERS_GROUP_INDEX, group);
                        adapter.notifyDataSetChanged();
                        elvFoundUsers.expandGroup(FOUND_USERS_GROUP_INDEX);
                        elvFoundUsers.collapseGroup(RELATIONS_GROUP_INDEX);
                    }
                });
    }

    private void initListViewScrollListener(View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);

        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final FoundEventUsersGroup group = (FoundEventUsersGroup) groups.get(1);
                if (isEndOfListReached() && group != null && group.canDownloadMore()) {
                    final String pattern = edtSearchField.getText().toString();
                    downloadNextPart(group, pattern);
                }
            }

            private void downloadNextPart(final FoundEventUsersGroup group, final String pattern) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<EventUserDto>>() {
                            @Override
                            protected Page<EventUserDto> doInBackground(String... params) {
                                String authtoken = params[0];
                                return dataSet.getAllUsers(authtoken, pattern, group.getAndIncrementLastPage(), 15);
                            }

                            @Override
                            protected void onPostExecute(Page<EventUserDto> users) {
//                                for (RelationItem r : users.getContent()) {
//                                    relationsChangesHandler.adjustRelationToUnsavedChanges(r);
//                                }
                                group.getChildren().addAll(users.getContent());
                                group.setLastPage(users.getNumber());
                                adapter.notifyDataSetChanged();
                            }
                        }
                );
            }

            private boolean isEndOfListReached() {
                return expandableListView.getAdapter() != null &&
                        expandableListView.getLastVisiblePosition() == expandableListView.getAdapter().getCount() - 1 &&
                        expandableListView.getChildAt(expandableListView.getChildCount() - 1).getBottom() <= expandableListView.getHeight();
            }
        });
    }
}