package pl.basistam.turysta.fragments.events;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.items.EventUserItem;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.Callback;
import pl.basistam.turysta.service.retrofit.RetrofitEventService;
import retrofit2.Call;

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
        final AppCompatImageButton btnRemove = view.findViewById(R.id.ib_remove);
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
                        List<EventUserItem> participants = new ArrayList<>();
                        List<EventUserItem> invited = new ArrayList<>();
                        for (EventUserItem e : eventUsers.getParticipants()) {
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
            RetrofitEventService eventService = EventService.getInstance()
                    .eventService();
            Call<Void> request = (eventGuid == null)
                    ? eventService.saveEvent(authToken, eventDto)
                    : eventService.updateEvent(authToken, eventGuid, eventDto);
            request.execute();
            View view = getView();
            if (view != null) {
                Snackbar.make(getView(), "Pomyślnie zapisano wydarzenie", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.CANNOT_UPDATE_OFFLINE_MODE, Toast.LENGTH_LONG).show();
        }
    }
}
