package pl.basistam.turysta.fragments.events;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.fragments.events.enums.GuestType;
import pl.basistam.turysta.service.EventService;

public class GuestEventFragment extends AbstractEventFragment {

    private GuestType guestType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.eventGuid = getArguments().getString("guid");
            this.guestType = (GuestType) getArguments().getSerializable("type");
        }
        return inflater.inflate(R.layout.fragment_user_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        if (guestType == GuestType.INVITED) {
            initAcceptAction(view);
            initRejectAction(view);
        } else {
            initApplicationButton(view);
        }
    }

    private void initApplicationButton(View view) {
        view.findViewById(R.id.btn_reject).setVisibility(View.GONE);
        Button btnAccept = view.findViewById(R.id.btn_accept);
        btnAccept.setText("ZGŁOŚ SIĘ!");
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                String authToken = params[0];
                                acceptInvitation(authToken);
                                return null;
                            }

                            private void acceptInvitation(String authToken) {
                                try {
                                    EventService.getInstance()
                                            .eventService()
                                            .apply(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected void onPostExecute(Void v) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }

    private void initRejectAction(View view) {
        Button btnReject = view.findViewById(R.id.btn_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Object>() {
                            @Override
                            protected Object doInBackground(String... params) {
                                String authToken = params[0];
                                rejectInvitation(authToken);
                                return null;
                            }

                            private void rejectInvitation(String authToken) {
                                try {
                                    EventService.getInstance()
                                            .eventService()
                                            .rejectInvitation(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }

    private void initAcceptAction(View view) {
        Button btnAccept = view.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Void>() {
                            @Override
                            protected Void doInBackground(String... params) {
                                String authToken = params[0];
                                acceptInvitation(authToken);
                                return null;
                            }

                            private void acceptInvitation(String authToken) {
                                try {
                                    EventService.getInstance()
                                            .eventService()
                                            .acceptInvitation(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected void onPostExecute(Void v) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        });
            }
        });
    }
}