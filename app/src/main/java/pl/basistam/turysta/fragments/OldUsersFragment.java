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
import pl.basistam.turysta.actions.UsersDataSet;
import pl.basistam.turysta.adapters.RelationsAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.FoundRelationsGroup;
import pl.basistam.turysta.dto.RelationsGroup;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.service.RelationsChangesHandlerImpl;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class OldUsersFragment extends Fragment {
/*
    private final RelationsChangesHandler relationsChangesHandler = new RelationsChangesHandlerImpl();
    private SparseArray<RelationsGroup> groups = new SparseArray<RelationsGroup>();
    private RelationsAdapter adapter;
    private UsersDataSet content;

    public static OldUsersFragment newInstance(UsersDataSet content) {
        OldUsersFragment usersFragment = new OldUsersFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("content", content);
        usersFragment.setArguments(bundle);
        return usersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content = (UsersDataSet) getArguments().getSerializable("content");
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initSearchButton(view);
        initBackButton(view);
        initFriends(view);
        initListViewScrollListener(view);
    }

    private void initFriends(final View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, List<EventUserDto>>() {

                    @Override
                    protected List<EventUserDto> doInBackground(String... params) {
                        String authToken = params[0];
                        return content.getFriends(authToken);
                    }

                    @Override
                    protected void onPostExecute(List<EventUserDto> content) {
                        RelationsGroup relationsGroup = new RelationsGroup("Twoi znajomi");
                        relationsGroup.setChildren(content);
                        groups.append(0, relationsGroup);
                        adapter = new RelationsAdapter(groups, getActivity(), relationsChangesHandler, true);
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
                final List<EventUserDto> changes = relationsChangesHandler.getAndClearAllChanges();
                *//*if (content != null) {
                    content.postExecute(changes);
                }*//*
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
                        new AsyncTask<String, Void, Page<EventUserDto>>() {

                            @Override
                            protected Page<EventUserDto> doInBackground(String... params) {
                                final String authtoken = params[0];
                                return content.getAllUsers(authtoken, pattern, 0, 15);
                            }

                            @Override
                            protected void onPostExecute(Page<EventUserDto> users) {
                                List<EventUserDto> content = new ArrayList<>(users.getSize());
                                for (EventUserDto e : users.getContent()) {
                                    EventUserDto eventUser = new EventUserDto(e.getName(), e.getLogin(), e.getStatus());
                                    relationsChangesHandler.adjustRelationToUnsavedChanges(eventUser);
                                    content.add(eventUser);
                                }
                                FoundRelationsGroup group = new FoundRelationsGroup("Wyszukiwanie");
                                group.setChildren(content);
                                group.setLastPage(users.getNumber());
                                group.setTotalNumber(users.getTotalElements());
                                groups.append(1, group);
                                adapter = new RelationsAdapter(groups, getActivity(), relationsChangesHandler, true);
                                expandableListView.setAdapter(adapter);
                                expandableListView.expandGroup(1);
                            }
                        });
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
                final FoundRelationsGroup group = (FoundRelationsGroup) groups.get(1);
                if (isEndOfListReached() && group != null && group.canDownloadMore()) {
                    final String pattern = edtSearchField.getText().toString();
                    downloadNextPart(group, pattern);
                }
            }

            private void downloadNextPart(final FoundRelationsGroup group, final String pattern) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<EventUserDto>>() {
                            @Override
                            protected Page<EventUserDto> doInBackground(String... params) {
                                String authtoken = params[0];
                                return content.getAllUsers(authtoken, pattern, group.getAndIncrementLastPage(), 15);
                            }

                            @Override
                            protected void onPostExecute(Page<EventUserDto> users) {
                                List<EventUserDto> content = new ArrayList<>(users.getSize());
                                for (EventUserDto e : users.getContent()) {
                                    relationsChangesHandler.adjustRelationToUnsavedChanges(e);
                                    content.add(e);
                                }
                                group.getChildren().addAll(content);
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
    }*/
}
