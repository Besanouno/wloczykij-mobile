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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.RelationsAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.FoundPeopleGroup;
import pl.basistam.turysta.dto.Group;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.service.RelationsChangesHandlerImpl;
import pl.basistam.turysta.service.UserService;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class RelationsFragment extends Fragment {

    private final RelationsChangesHandler relationsChangesHandler = new RelationsChangesHandlerImpl();
    SparseArray<Group> groups = new SparseArray<Group>();
    RelationsAdapter adapter;

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

                if (isEndOfListReached() && group != null && group.canDownloadMore()) {

                    final String pattern = edtSearchField.getText().toString();

                    LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                            new AsyncTask<String, Void, Page<Relation>>() {
                                @Override
                                protected Page<Relation> doInBackground(String... params) {
                                    try {
                                        String authtoken = params[0];
                                        return UserService.getInstance()
                                                .userService()
                                                .getUserSimpleDetailsByPattern(authtoken, pattern, group.getAndIncrementLastPage(), 15)
                                                .execute()
                                                .body();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }

                                @Override
                                protected void onPostExecute(Page<Relation> users) {
                                    List<Relation> content = new ArrayList<>(users.getSize());
                                    for (Relation u : users.getContent()) {
                                        Relation relation = new Relation(u.getName(), u.getLogin(), u.isFriend());
                                        relationsChangesHandler.adjustRelationToUnsavedChanges(relation);
                                        content.add(relation);
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
                new AsyncTask<String, Void, List<Relation>>() {

                    @Override
                    protected List<Relation> doInBackground(String... params) {
                        try {
                            String authToken = params[0];
                            return UserService.getInstance()
                                    .userService()
                                    .getRelations(authToken)
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
                        adapter = new RelationsAdapter(groups, getActivity(), relationsChangesHandler);
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
                final List<Relation> changes = relationsChangesHandler.getAndClearAllChanges();
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
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            });
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
                        new AsyncTask<String, Void, Page<Relation>>() {

                            @Override
                            protected Page<Relation> doInBackground(String... params) {
                                try {
                                    final String authtoken = params[0];
                                    return UserService.getInstance()
                                            .userService()
                                            .getUserSimpleDetailsByPattern(authtoken, pattern, 0, 15)
                                            .execute()
                                            .body();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(Page<Relation> users) {
                                List<Relation> content = new ArrayList<>(users.getSize());
                                for (Relation u : users.getContent()) {
                                    Relation relation = new Relation(u.getName(), u.getLogin(), u.isFriend());
                                    relationsChangesHandler.adjustRelationToUnsavedChanges(relation);
                                    content.add(relation);
                                }
                                FoundPeopleGroup group = new FoundPeopleGroup("Wyszukiwanie");
                                group.setChildren(content);
                                group.setLastPage(users.getNumber());
                                group.setTotalNumber(users.getTotalElements());
                                groups.append(1, group);
                                adapter = new RelationsAdapter(groups, getActivity(), relationsChangesHandler);
                                expandableListView.setAdapter(adapter);
                                expandableListView.expandGroup(1);
                            }
                        });
            }
        });

    }
}
