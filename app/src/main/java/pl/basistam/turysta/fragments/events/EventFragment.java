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
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.EventUsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.EventUsersGroup;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.listeners.EventUsersGroupsListener;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.ParticipantsChangesHandler;
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
    protected ParticipantsChangesHandler participantsChangesHandler;

    protected String eventGuid = null;
    protected final SparseArray<EventUsersGroup> groups = new SparseArray<>();
    protected final int PARTICIPANTS_GROUP_INDEX = 0;
    protected final int INVITED_GROUP_INDEX = 1;

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public abstract void onViewCreated(final View view, @Nullable Bundle savedInstanceState);

    protected void initView(final View view) {
        loadFormFields(view);
        downloadEventAndFillFields();
        initAdapter();
        initElvParticipants();
        switchAdminMode();
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
        EventUsersGroup participantsGroup = new EventUsersGroup("Uczestnicy");
        groups.append(PARTICIPANTS_GROUP_INDEX, participantsGroup);

        EventUsersGroup invitedGroup = new EventUsersGroup("Zaproszeni");
        groups.append(INVITED_GROUP_INDEX, invitedGroup);

        adapter = new EventUsersAdapter(groups, getActivity(), isAdmin, participantsChangesHandler);
        elvParticipants.setAdapter(adapter);
    }

    protected void downloadEventAndFillFields() {
        if (eventGuid == null)
            return;
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
        List<EventUserDto> participants = new ArrayList<>();
        List<EventUserDto> invited = new ArrayList<>();
        for (EventUserDto e : event.getParticipants()) {
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
        participantsChangesHandler = new ParticipantsChangesHandler(event.getParticipants());
    }

    protected void switchAdminMode() {
        adapter.setAdmin(isAdmin);
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
        eventDto.setParticipantsLimit(Integer.parseInt(edtParticipantsLimit.getText().toString()));
        eventDto.setPublicAccess(Boolean.getBoolean(chbPublicAccess.getText().toString()));
        ArrayList<EventUserDto> participants = new ArrayList<>();
        eventDto.setParticipants(participants);
        return eventDto;
    }
}
