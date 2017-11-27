package pl.basistam.turysta.fragments.events;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.EventUsersDataSet;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.fragments.OldUsersFragment;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.retrofit.RetrofitEventService;
import retrofit2.Call;

public class UpcomingEventFragment extends AbstractEventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            String guid = getArguments().getString("guid");
            if (guid != null) {
                this.eventGuid = guid;
            }
        }
        return inflater.inflate(R.layout.fragment_admin_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initBtnSave(view);
        initBtnFriends(view);
        initBtnRemove(view);
    }

    private void initBtnRemove(View view) {
        final Button btnRemove = view.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Object>() {
                            @Override
                            protected Object doInBackground(String... params) {
                                String authToken = params[0];
                                removeEvent(authToken);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }

    private void removeEvent(String authToken) {
        try {
            EventService.getInstance()
                    .eventService()
                    .remove(authToken, eventGuid)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                for (EventUserDto e: groups.get(0).getChildren()) {
                    allUsers.add(e.getLogin());
                }
                for (EventUserDto e : groups.get(1).getChildren()) {
                    allUsers.add(e.getLogin());
                }
                OldUsersFragment usersFragment = OldUsersFragment.newInstance(new EventUsersDataSet(getActivity().getBaseContext(), eventGuid, allUsers));
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.content, usersFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });*/
    }

    private void initBtnSave(View view) {
        Button btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EventDto eventDto = prepareEventDto();
                if (eventDto == null) return;

                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Object>() {
                            @Override
                            protected Object doInBackground(String... params) {
                                String authToken = params[0];
                                saveEvent(authToken, eventDto);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }

    private void saveEvent(String authToken, EventDto eventDto) {
        try {
            RetrofitEventService eventService = EventService.getInstance()
                    .eventService();
            Call<Void> request = isEventNew() ?
                    eventService.saveEvent(authToken, eventDto)
                    : eventService.updateEvent(authToken, eventGuid, eventDto);
            request.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEventNew() {
        return eventGuid == null;
    }
}
