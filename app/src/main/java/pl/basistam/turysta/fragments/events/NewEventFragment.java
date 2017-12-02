package pl.basistam.turysta.fragments.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.basistam.turysta.R;

public class NewEventFragment extends EventFragment {

//    private NewEventManager usersFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initBtnFriends(view);
    }

    private void initBtnFriends(View view) {
        /*final ImageButton btnFriends = view.findViewById(R.id.ib_add_participant);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareUsersView();
            }

            private void prepareUsersView() {
                List<String> allUsers = new ArrayList<>();
                for (EventUserItem userItem : groups.get(0).getChildren()) {
                        allUsers.add(userItem.getLogin());
                }
                for (EventUserItem userItem : groups.get(1).getChildren()) {
                        allUsers.add(userItem.getLogin());
                }
                usersFragmentManager = new NewEventManager(getActivity().getBaseContext(), eventGuid, allUsers);
                OldUsersFragment usersFragment = OldUsersFragment.newInstance(usersFragmentManager);
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .add(R.id.content, usersFragment)
                        .commit();
            }
        });*/
    }
}
