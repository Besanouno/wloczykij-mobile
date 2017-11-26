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

import pl.basistam.turysta.R;
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

public abstract class AbstractEventFragment extends Fragment {
    SparseArray<Group> groups = new SparseArray<>();
    private final UsersStatusesChangesHandler usersStatusesChangesHandler = new UsersStatusesChangesHandlerImpl();

    protected String eventGuid = null;
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

    private boolean admin;

    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public abstract void onViewCreated(final View view, @Nullable Bundle savedInstanceState);

    protected void initView(final View view) {
        loadFormFields(view);
        initEventIfPresent();
        initAdapter(admin);
        initElvParticipants();
    }

    protected void initElvParticipants() {
        EventUsersGroupsListener eventUsersGroupsListener = new EventUsersGroupsListener(elvParticipants, groups);
        elvParticipants.setOnGroupExpandListener(eventUsersGroupsListener);
        elvParticipants.setOnGroupCollapseListener(eventUsersGroupsListener);
    }

    protected void initAdapter(boolean isAdmin) {
        Group participantsGroup = new FoundPeopleGroup("Uczestnicy");
        groups.append(0, participantsGroup);

        Group invitedGroup = new FoundPeopleGroup("Zaproszeni");
        groups.append(1, invitedGroup);

        usersAdapter = new UsersAdapter(groups, getActivity(), usersStatusesChangesHandler, isAdmin);
        elvParticipants.setAdapter(usersAdapter);
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

    protected void initEventIfPresent() {
        if (eventGuid == null)
            return;
        LoggedUser.getInstance().sendAuthorizedRequest(
                getActivity().getBaseContext(),
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
                    protected void onPostExecute(EventFullDto event) {
                        admin = LoggedUser.getInstance().getLogin().equals(event.getInitiator());
                        reloadDataOnForm(event);
                    }
                });
    }

    private void reloadDataOnForm(EventFullDto event) {
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
        groups.get(0).getChildren().addAll(event.getParticipants());
        groups.get(0).setName("Uczestnicy (" + event.getParticipants().size() + ")");
        groups.get(1).getChildren().addAll(event.getInvited());
        groups.get(1).setName("Zaproszeni (" + event.getInvited().size() + ")");
        usersAdapter.notifyDataSetChanged();
    }


    protected EventFullDto prepareEventDto() {
        EventFullDto eventDto = new EventFullDto();
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
        ArrayList<UserItem> participants = new ArrayList<>();
        participants.addAll(groups.get(0).getChildren());
        eventDto.setParticipants(participants);
        return eventDto;
    }
}
