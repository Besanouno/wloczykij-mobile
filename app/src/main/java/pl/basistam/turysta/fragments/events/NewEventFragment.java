package pl.basistam.turysta.fragments.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.EventManager;
import pl.basistam.turysta.actions.NewEventManager;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.fragments.UsersFragment;

public class NewEventFragment extends AbstractEventFragment {

    private NewEventManager usersFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        hideRemoveButton(view);
        initBtnFriends(view);
    }

    private void initBtnFriends(View view) {
        final ImageButton btnFriends = view.findViewById(R.id.ib_add_participant);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareUsersView();
            }

            private void prepareUsersView() {
                List<String> allUsers = new ArrayList<>();
                for (UserItem userItem : groups.get(0).getChildren()) {
                    if (userItem.getStatus())
                        allUsers.add(userItem.getLogin());
                }
                for (UserItem userItem : groups.get(1).getChildren()) {
                    if (userItem.getStatus())
                        allUsers.add(userItem.getLogin());
                }
                usersFragmentManager = new NewEventManager(getActivity().getBaseContext(), eventGuid, allUsers);
                UsersFragment usersFragment = UsersFragment.newInstance(usersFragmentManager);
                getActivity().getFragmentManager()
                        .beginTransaction()
                        .add(R.id.content, usersFragment)
                        .commit();
            }
        });
    }

    private void hideRemoveButton(View view) {
        view.findViewById(R.id.btn_remove).setVisibility(View.GONE);
    }
}
