package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.RelationsDataSet;
import pl.basistam.turysta.adapters.RelationsAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.items.RelationItem;
import pl.basistam.turysta.groups.FoundUsersGroup;
import pl.basistam.turysta.groups.RelationsGroup;
import pl.basistam.turysta.service.RelationsChangesHandlerImpl;
import pl.basistam.turysta.service.UserService;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class RelationsFragment extends Fragment {

    private RelationsDataSet dataSet;
    private RelationsAdapter adapter;
    private RelationsChangesHandler relationsChangesHandler;

    private SparseArray<RelationsGroup> groups = new SparseArray<>();
    private final int RELATIONS_GROUP_INDEX = 0;
    private final int FOUND_USERS_GROUP_INDEX = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataSet = new RelationsDataSet(getActivity().getBaseContext());
        relationsChangesHandler = new RelationsChangesHandlerImpl();
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initAdapter();
        initFriends(view);
        initSearchAction(view);
        initSaveAction(view);
        initListViewScrollListener(view);
    }

    private void initAdapter() {
        adapter = new RelationsAdapter(groups, getActivity(), relationsChangesHandler);
    }

    private void initFriends(View view) {
        final ExpandableListView expandableListView = view.findViewById(R.id.elv_relations);
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, List<RelationItem>>() {

                    @Override
                    protected List<RelationItem> doInBackground(String... params) {
                        String authToken = params[0];
                        return dataSet.getFriends(authToken);
                    }

                    @Override
                    protected void onPostExecute(List<RelationItem> content) {
                        RelationsGroup<RelationItem> relationsGroup = new RelationsGroup<>("Twoi znajomi");
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
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<RelationItem>>() {

                            @Override
                            protected Page<RelationItem> doInBackground(String... params) {
                                final String authtoken = params[0];
                                return dataSet.getAllUsers(authtoken, pattern, 0, 15);
                            }

                            @Override
                            protected void onPostExecute(Page<RelationItem> users) {
                                for (RelationItem r : users.getContent()) {
                                    relationsChangesHandler.adjustRelationToUnsavedChanges(r);
                                }
                                FoundUsersGroup<RelationItem> group = new FoundUsersGroup<>("Wyszukiwanie");
                                group.setChildren(users.getContent());
                                group.setLastPage(users.getNumber());
                                group.setTotalNumber(users.getTotalElements());
                                groups.append(FOUND_USERS_GROUP_INDEX, group);
                                adapter.notifyDataSetChanged();
                                elvFoundUsers.collapseGroup(RELATIONS_GROUP_INDEX);
                                elvFoundUsers.expandGroup(FOUND_USERS_GROUP_INDEX);
                            }
                        });
            }
        });
    }

    private void initSaveAction(final View view) {
        final Button button = view.findViewById(R.id.btn_save);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveChanges();
                    }
                }
        );
    }

    private void saveChanges() {
        final List<RelationItem> changes = relationsChangesHandler.getAndClearAllChanges();
        if (!changes.isEmpty()) {
            LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                    new AsyncTask<String, Void, Object>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            try {
                                String authtoken = params[0];
                                UserService.getInstance()
                                        .userService()
                                        .updateRelations(authtoken, changes)
                                        .execute();
                                KeyboardUtils.hide(getActivity().getBaseContext(), getView());
                                View view = getView();
                                if (view != null) {
                                    Snackbar.make(getView(), "Pomyślnie zaktualizowano listę znajomych", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                getFragmentManager().popBackStack();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity().getBaseContext(), ErrorMessages.CANNOT_UPDATE_OFFLINE_MODE, Toast.LENGTH_LONG).show();
                            }
                            return null;
                        }
                    });
        }
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
                final FoundUsersGroup<RelationItem> group = (FoundUsersGroup<RelationItem>) groups.get(1);
                if (isEndOfListReached() && group != null && group.canDownloadMore()) {
                    final String pattern = edtSearchField.getText().toString();
                    downloadNextPart(group, pattern);
                }
            }

            private void downloadNextPart(final FoundUsersGroup<RelationItem> group, final String pattern) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Page<RelationItem>>() {
                            @Override
                            protected Page<RelationItem> doInBackground(String... params) {
                                String authtoken = params[0];
                                return dataSet.getAllUsers(authtoken, pattern, group.getAndIncrementLastPage(), 15);
                            }

                            @Override
                            protected void onPostExecute(Page<RelationItem> users) {
                                for (RelationItem r : users.getContent()) {
                                    relationsChangesHandler.adjustRelationToUnsavedChanges(r);
                                }
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
