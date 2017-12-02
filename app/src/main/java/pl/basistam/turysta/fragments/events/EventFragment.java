package pl.basistam.turysta.fragments.events;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.EventUsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.items.EventUserItem;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.groups.RelationsGroup;
import pl.basistam.turysta.listeners.EventUsersGroupsListener;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.EventUsers;
import pl.basistam.turysta.utils.Converter;

public abstract class EventFragment extends Fragment {
    private EditText edtPlaceOfMeeting;
    private EditText edtName;
    private EditText edtStartDate;
    private EditText edtStartHour;
    private EditText edtEndDate;
    private EditText edtEndHour;
    private CheckBox chbPublicAccess;
    private EditText edtParticipantsLimit;
    private ExpandableListView elvParticipants;

    protected boolean isAdmin = false;
    protected EventUsersAdapter adapter;
    protected EventUsers eventUsers;

    protected String eventGuid = null;
    protected final SparseArray<RelationsGroup<EventUserItem>> groups = new SparseArray<>();

    protected static final int PARTICIPANTS_GROUP_INDEX = 0;
    protected static final int INVITED_GROUP_INDEX = 1;
    protected static final int WAITING_GROUP_INDEX = 2;

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public abstract void onViewCreated(final View view, @Nullable Bundle savedInstanceState);

    protected void initView(final View view) {
        loadFormFields(view);
        downloadEventAndFillFields();
    }

    protected void loadFormFields(View view) {
        edtName = view.findViewById(R.id.edt_name);
        edtPlaceOfMeeting = view.findViewById(R.id.edt_place_of_meeting);
        edtStartDate = view.findViewById(R.id.edt_start_date);
        edtStartHour = view.findViewById(R.id.edt_start_hour);
        edtEndDate = view.findViewById(R.id.edt_end_date);
        edtEndHour = view.findViewById(R.id.edt_end_hour);
        edtParticipantsLimit = view.findViewById(R.id.edt_participants_limit);
        chbPublicAccess = view.findViewById(R.id.chb_public_access);
        elvParticipants = view.findViewById(R.id.elv_participants);
    }

    protected void initAdapter() {
        RelationsGroup<EventUserItem> participantsGroup = new RelationsGroup<>("Uczestnicy");
        groups.append(PARTICIPANTS_GROUP_INDEX, participantsGroup);

        RelationsGroup<EventUserItem> invitedGroup = new RelationsGroup<>("Zaproszeni");
        groups.append(INVITED_GROUP_INDEX, invitedGroup);

        if (isAdmin) {
            RelationsGroup<EventUserItem> waitingGroup = new RelationsGroup<>("Oczekujący");
            groups.append(WAITING_GROUP_INDEX, waitingGroup);
        }

        adapter = new EventUsersAdapter(groups, getActivity(), isAdmin, eventUsers);
        elvParticipants.setAdapter(adapter);
        adapter.setAdmin(isAdmin);
    }

    protected void downloadEventAndFillFields() {
        if (eventGuid == null) {
            fillEventUsers(Collections.singletonList(new EventUserItem(1L, LoggedUser.getInstance().getLogin(), "", EventUserStatus.ADMIN.name())));
            return;
        }
        LoggedUser.getInstance().sendAuthorizedRequest(
                getActivity().getBaseContext(),
                new AsyncTask<String, Void, EventDto>() {
                    @Override
                    protected EventDto doInBackground(String... params) {
                        String authToken = params[0];
                        try {
                            return EventService.getInstance()
                                    .eventService()
                                    .getEvent(authToken, eventGuid)
                                    .execute()
                                    .body();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(EventDto event) {
                        fillFields(event);
                    }
                });
    }

    private void fillFields(EventDto event) {
        edtName.setText(event.getName());
        edtParticipantsLimit.setText(Integer.toString(event.getParticipantsLimit()));
        edtPlaceOfMeeting.setText(event.getPlaceOfMeeting());
        edtStartDate.setText(Converter.dateToString(event.getStartDate()));
        edtStartHour.setText(Converter.timeToString(event.getStartDate()));
        if (event.getEndDate() != null) {
            edtEndDate.setText(Converter.dateToString(event.getEndDate()));
            edtEndHour.setText(Converter.timeToString(event.getEndDate()));
        }
        chbPublicAccess.setChecked(event.isPublicAccess());
        fillEventUsers(event.getEventUsers());
    }

    protected void fillEventUsers(List<EventUserItem> eventUserList) {
        List<EventUserItem> participants = new ArrayList<>();
        List<EventUserItem> invited = new ArrayList<>();
        List<EventUserItem> waiting = new ArrayList<>();

        for (EventUserItem e : eventUserList) {
            String status = e.getStatus();
            if (EventUserStatus.PARTICIPANT.name().equals(status)
                    || EventUserStatus.ADMIN.name().equals(status)) {
                participants.add(e);
            } else if (EventUserStatus.INVITED.name().equals(status)) {
                invited.add(e);
            } else if (EventUserStatus.WAITING.name().equals(status)) {
                waiting.add(e);
            }
        }

        eventUsers = new EventUsers(eventUserList);
        initAdapter();
        initElvParticipants();
        switchAdminMode();

        groups.get(PARTICIPANTS_GROUP_INDEX).getChildren().addAll(participants);
        groups.get(INVITED_GROUP_INDEX).getChildren().addAll(invited);
        if (isAdmin) {
            groups.get(WAITING_GROUP_INDEX).getChildren().addAll(waiting);
        }
    }

    protected void switchAdminMode() {
        edtName.setEnabled(isAdmin);
        chbPublicAccess.setEnabled(isAdmin);
        edtEndHour.setEnabled(isAdmin);
        edtParticipantsLimit.setEnabled(isAdmin);
        edtPlaceOfMeeting.setEnabled(isAdmin);
        edtStartDate.setEnabled(isAdmin);
        edtStartHour.setEnabled(isAdmin);
        edtEndDate.setEnabled(isAdmin);
    }


    protected void initElvParticipants() {
        EventUsersGroupsListener eventUsersGroupsListener = new EventUsersGroupsListener(elvParticipants, groups);
        elvParticipants.setOnGroupExpandListener(eventUsersGroupsListener);
        elvParticipants.setOnGroupCollapseListener(eventUsersGroupsListener);
    }

    protected EventDto prepareEventDto() {
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
        if (!edtParticipantsLimit.getText().toString().isEmpty())
            eventDto.setParticipantsLimit(Integer.parseInt(edtParticipantsLimit.getText().toString()));
        eventDto.setPublicAccess(Boolean.getBoolean(chbPublicAccess.getText().toString()));
        ArrayList<EventUserItem> participants = new ArrayList<>();
        participants.addAll(eventUsers.getParticipants());
        eventDto.setEventUsers(participants);
        return eventDto;
    }
}
