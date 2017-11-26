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

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.UsersAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventFullDto;
import pl.basistam.turysta.dto.FoundPeopleGroup;
import pl.basistam.turysta.dto.Group;
import pl.basistam.turysta.listeners.EventUsersGroupsListener;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.UsersStatusesChangesHandlerImpl;
import pl.basistam.turysta.service.interfaces.UsersStatusesChangesHandler;
import pl.basistam.turysta.utils.Converter;

public class ArchivalEventFragment extends Fragment {
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
        if (getArguments() != null) {
            String guid = getArguments().getString("guid");
            if (guid != null) {
                this.eventGuid = guid;
            }
        }
        return inflater.inflate(R.layout.fragment_user_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.btn_accept).setVisibility(View.GONE);
        view.findViewById(R.id.btn_reject).setVisibility(View.GONE);

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
        usersAdapter = new UsersAdapter(groups, getActivity(), usersStatusesChangesHandler, false);
        elvParticipants.setAdapter(usersAdapter);

        EventUsersGroupsListener eventUsersGroupsListener = new EventUsersGroupsListener(elvParticipants, groups);
        elvParticipants.setOnGroupExpandListener(eventUsersGroupsListener);
        elvParticipants.setOnGroupCollapseListener(eventUsersGroupsListener);
        initEventIfPresent();
    }

    private void initEventIfPresent() {
        if (eventGuid == null)
            return;
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
                                groups.get(0).setName(groups.get(0).getName() + " (" + eventFullDto.getParticipants().size() + ")");
                                groups.get(1).setName(groups.get(1).getName() + " (" + eventFullDto.getInvited().size() + ")");
                                usersAdapter.notifyDataSetChanged();
                            }
                        });
    }
}
