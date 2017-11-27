package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.UsersDataSet;

public class EventUsersFragment extends Fragment {

    private UsersDataSet content;

    public static EventUsersFragment newInstance(UsersDataSet content) {
        EventUsersFragment eventUsersFragment = new EventUsersFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("content", content);
        eventUsersFragment.setArguments(bundle);
        return eventUsersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        content = (UsersDataSet) getArguments().getSerializable("content");
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

   /* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initSearchAction(view);
    }

    private void initSearchAction(View view) {
        final AppCompatImageButton btnSearch = view.findViewById(R.id.btn_search);
        final ExpandableListView elvFoundUsers = view.findViewById(R.id.elv_relations);
        final EditText edtSearchField = view.findViewById(R.id.edt_search);
        KeyboardUtils.hide(getActivity().getBaseContext(), view);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        return content.getAllUsers(authtoken, pattern, 0, 15);
                    }

                    @Override
                    protected void onPostExecute(Page<EventUserDto> users) {
                        List<EventUserDto> content = new ArrayList<>(users.getContent());
                        FoundRelationsGroup group = new FoundRelationsGroup("Wyszukiwanie");
                        group.setChildren(content);
                        group.setLastPage(users.getNumber());
                        group.setTotalNumber(users.getTotalElements());
                        groups.append(1, group);
                        adapter = new RelationsAdapter(groups, getActivity(), usersStatusesChangesHandler, true);
                        elvFoundUsers.setAdapter(adapter);
                        elvFoundUsers.expandGroup(1);
                    }
                });
    }*/
}