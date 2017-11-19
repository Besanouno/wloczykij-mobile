package pl.basistam.turysta.fragments;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.IOException;
import java.text.ParseException;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.utils.Converter;
import retrofit2.Response;

public class EventFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
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
                                    try {
                                        Response<Void> res = EventService.getInstance()
                                                .eventService()
                                                .saveEvent(authToken, eventDto)
                                                .execute();
                                        System.out.println(res);
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

                EditText edtName = view.findViewById(R.id.edt_name);
                eventDto.setName(edtName.getText().toString());

                EditText edtPlaceOfMeeting = view.findViewById(R.id.edt_place_of_meeting);
                eventDto.setPlaceOfMeeting(edtPlaceOfMeeting.getText().toString());

                EditText edtStartDate = view.findViewById(R.id.edt_start_date);
                EditText edtStartHour = view.findViewById(R.id.edt_start_hour);
                String startDateTime = edtStartDate.getText().toString() + " " + edtStartHour.getText().toString();
                try {
                    eventDto.setStartDate(Converter.stringToDatetime(startDateTime));
                } catch(ParseException e) {
                    edtStartDate.setError("Błędny format!");
                    edtStartHour.setError("Błędny format!");
                    return null;
                }
                EditText edtEndDate = view.findViewById(R.id.edt_end_date);
                EditText edtEndHour = view.findViewById(R.id.edt_end_hour);
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

                EditText edtParticipantsLimit = view.findViewById(R.id.edt_participants_limit);
                eventDto.setParticipantsLimit(Integer.parseInt(edtParticipantsLimit.getText().toString()));

                CheckBox chbPublicAccess = view.findViewById(R.id.chb_public_access);
                eventDto.setPublicAccess(Boolean.getBoolean(chbPublicAccess.getText().toString()));

                return eventDto;
            }
        });
    }
}
