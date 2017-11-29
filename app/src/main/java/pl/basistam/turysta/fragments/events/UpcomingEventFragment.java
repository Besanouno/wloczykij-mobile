package pl.basistam.turysta.fragments.events;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.fragments.EventUsersFragment;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.Callback;

public class UpcomingEventFragment extends EventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.eventGuid = getArguments().getString("guid");
            this.isAdmin = getArguments().getBoolean("isAdmin");
        }
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initBtnSave(view);
        initBtnFriends(view);
        initRightButton(view);
    }

    private void initRightButton(View view) {
        if (isAdmin) {
            initBtnRemove(view);
        } else {
            initBtnLeave(view);
        }
    }

    private void initBtnRemove(View view) {
        final AppCompatImageButton btnRemove = view.findViewById(R.id.ib_leave);
        btnRemove.setVisibility(View.VISIBLE);
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

    private void initBtnLeave(View view) {
        final AppCompatImageButton btnLeave = view.findViewById(R.id.ib_leave);
        btnLeave.setVisibility(View.VISIBLE);
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Object>() {
                            @Override
                            protected Object doInBackground(String... params) {
                                String authToken = params[0];
                                leaveEvent(authToken);
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

    private void leaveEvent(String authToken) {
        try {
            EventService.getInstance()
                    .eventService()
                    .leave(authToken, eventGuid)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initBtnFriends(View view) {
        final AppCompatImageButton btnFriends = view.findViewById(R.id.ib_add_participant);
        if (!isAdmin) {
            btnFriends.setVisibility(View.GONE);
            return;
        }
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareUsersView();
            }

            private void prepareUsersView() {
                EventUsersFragment fragment = EventUsersFragment.create(eventUsers, new Callback() {
                    @Override
                    public void run() {
                        groups.get(PARTICIPANTS_GROUP_INDEX).getChildren().clear();
                        groups.get(INVITED_GROUP_INDEX).getChildren().clear();
                        List<EventUserDto> participants = new ArrayList<>();
                        List<EventUserDto> invited = new ArrayList<>();
                        for (EventUserDto e : eventUsers.getParticipants()) {
                            String status = e.getStatus();
                            if (EventUserStatus.PARTICIPANT.name().equals(status)
                                    || EventUserStatus.ADMIN.name().equals(status)) {
                                participants.add(e);
                            } else if (EventUserStatus.INVITED.name().equals(status)) {
                                invited.add(e);
                            }
                        }
                        groups.get(PARTICIPANTS_GROUP_INDEX).getChildren().addAll(participants);
                        groups.get(INVITED_GROUP_INDEX).getChildren().addAll(invited);
                        adapter.notifyDataSetChanged();
                    }
                });
                getActivity().getFragmentManager().beginTransaction()
                        .add(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void initBtnSave(View view) {
        final AppCompatImageButton btnSave = view.findViewById(R.id.ib_save);
        if (!isAdmin) {
            btnSave.setVisibility(View.GONE);
            return;
        }
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
            EventService.getInstance()
                    .eventService().saveEvent(authToken, eventDto)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
