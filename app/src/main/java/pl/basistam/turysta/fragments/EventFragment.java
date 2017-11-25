package pl.basistam.turysta.fragments;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.actions.InvitationsManager;
import pl.basistam.turysta.actions.RelationsManager;
import pl.basistam.turysta.adapters.UsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventFullDto;
import pl.basistam.turysta.dto.FoundPeopleGroup;
import pl.basistam.turysta.dto.Group;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.listeners.EventUsersGroupsListener;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.UsersStatusesChangesHandlerImpl;
import pl.basistam.turysta.service.interfaces.UsersStatusesChangesHandler;
import pl.basistam.turysta.utils.Converter;

public class EventFragment extends Fragment {
    SparseArray<Group> groups = new SparseArray<Group>();
    private final UsersStatusesChangesHandler usersStatusesChangesHandler = new UsersStatusesChangesHandlerImpl();

    private String eventGuid = null;
    private EditText edtPlaceOfMeeting;
    private EditText edtName;
    private EditText edtStartDate;
    private EditText edtStartHour;
    private EditText edtEndDate;
    private EditText edtEndHour;
    private CheckBox chbPublicAccess;
    private EditText edtParticipantsLimit;
    private ExpandableListView elvParticipants;
    private UsersAdapter usersAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String guid = getArguments().getString("guid");
        if (guid != null) {
            this.eventGuid = guid;
        }
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Button btnSave = view.findViewById(R.id.btn_save);

        edtName = view.findViewById(R.id.edt_name);
        edtPlaceOfMeeting = view.findViewById(R.id.edt_place_of_meeting);
        edtStartDate = view.findViewById(R.id.edt_start_date);
        edtStartHour = view.findViewById(R.id.edt_start_hour);
        edtEndDate = view.findViewById(R.id.edt_end_date);
        edtEndHour = view.findViewById(R.id.edt_end_hour);
        edtParticipantsLimit = view.findViewById(R.id.edt_participants_limit);
        chbPublicAccess = view.findViewById(R.id.chb_public_access);
        elvParticipants = view.findViewById(R.id.elv_participants);

        Group participantsGroup = new FoundPeopleGroup("Uczestnicy");
        Group invitedGroup = new FoundPeopleGroup("Zaproszeni");
        groups.append(0, participantsGroup);
        groups.append(1, invitedGroup);
        usersAdapter = new UsersAdapter(groups, getActivity(), usersStatusesChangesHandler);
        elvParticipants.setAdapter(usersAdapter);

        EventUsersGroupsListener eventUsersGroupsListener = new EventUsersGroupsListener(elvParticipants, groups);
        elvParticipants.setOnGroupExpandListener(eventUsersGroupsListener);
        elvParticipants.setOnGroupCollapseListener(eventUsersGroupsListener);
        initEventIfPresent();

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
                                try {
                                    EventService.getInstance()
                                            .eventService()
                                            .saveEvent(authToken, eventDto)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }

            private EventDto prepareEventDto() {
                EventDto eventDto = new EventDto();
                eventDto.setName(edtName.getText().toString());
                eventDto.setPlaceOfMeeting(edtPlaceOfMeeting.getText().toString());
                String startDateTime = edtStartDate.getText().toString() + " " + edtStartHour.getText().toString();
                try {
                    eventDto.setStartDate(Converter.stringToDatetime(startDateTime));
                } catch (ParseException e) {
                    edtStartDate.setError("Błędny format!");
                    edtStartHour.setError("Błędny format!");
                    return null;
                }
                String endDateTime = edtEndDate.getText().toString() + " " + edtEndHour.getText().toString();
                if (!endDateTime.trim().isEmpty()) {
                    try {
                        eventDto.setEndDate(Converter.stringToDatetime(endDateTime));
                    } catch (ParseException e) {
                        edtEndDate.setError("Błędny format!");
                        edtEndHour.setError("Błędny format!");
                        return null;
                    }
                }
                eventDto.setParticipantsLimit(Integer.parseInt(edtParticipantsLimit.getText().toString()));
                eventDto.setPublicAccess(Boolean.getBoolean(chbPublicAccess.getText().toString()));
                return eventDto;
            }
        });

        final ImageButton btnFriends = view.findViewById(R.id.ib_add_participant);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> allUsers = new ArrayList<>();
                for (UserItem userItem: groups.get(0).getChildren()) {
                    allUsers.add(userItem.getLogin());
                }
                for (UserItem userItem: groups.get(1).getChildren()) {
                    allUsers.add(userItem.getLogin());
                }
                UsersFragment usersFragment = UsersFragment.newInstance(new InvitationsManager(getActivity().getBaseContext(), eventGuid, allUsers));
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.content, usersFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void initEventIfPresent() {
        LoggedUser.getInstance()
                .sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, EventFullDto>() {
                            @Override
                            protected EventFullDto doInBackground(String... params) {
                                String authToken = params[0];
                                try {
                                    return EventService.getInstance()
                                            .eventService()
                                            .getFullEvent(authToken, eventGuid)
                                            .execute()
                                            .body();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(EventFullDto eventFullDto) {
                                edtName.setText(eventFullDto.getName());
                                edtParticipantsLimit.setText(Integer.toString(eventFullDto.getParticipantsLimit()));
                                edtPlaceOfMeeting.setText(eventFullDto.getPlaceOfMeeting());
                                edtStartDate.setText(Converter.dateToString(eventFullDto.getStartDate()));
                                edtStartHour.setText(Converter.timeToString(eventFullDto.getStartDate()));
                                if (eventFullDto.getEndDate() != null) {
                                    edtEndDate.setText(Converter.dateToString(eventFullDto.getEndDate()));
                                    edtEndHour.setText(Converter.timeToString(eventFullDto.getEndDate()));
                                }
                                chbPublicAccess.setChecked(eventFullDto.isPublicAccess());
                                groups.get(0).getChildren().addAll(eventFullDto.getParticipants());
                                groups.get(1).getChildren().addAll(eventFullDto.getInvited());
                                usersAdapter.notifyDataSetChanged();
                            }
                        });
    }
}
