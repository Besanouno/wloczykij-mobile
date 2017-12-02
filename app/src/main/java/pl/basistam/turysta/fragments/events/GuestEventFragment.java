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
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.fragments.events.enums.GuestType;
import pl.basistam.turysta.items.EventUserItem;
import pl.basistam.turysta.service.EventService;
import retrofit2.Response;

public class GuestEventFragment extends EventFragment {

    private GuestType guestType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.eventGuid = getArguments().getString("guid");
            this.guestType = (GuestType) getArguments().getSerializable("type");
        }
        return inflater.inflate(R.layout.fragment_guest_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        if (guestType == GuestType.INVITED) {
            view.findViewById(R.id.rl_invitation_layout).setVisibility(View.VISIBLE);
            initAcceptAction(view);
            initRejectAction(view);
        }
    }

    @Override
    protected void onAfterEventDownload() {
        if (guestType == GuestType.VISITOR) {
            List<EventUserItem> participants = eventUsers.getParticipants();
            for (EventUserItem e : participants) {
                if (e.getLogin().equals(LoggedUser.getInstance().getLogin())
                        && e.getStatus().equals(EventUserStatus.WAITING.name())) {
                    initResignationButton(getView());
                    return;
                }
            }
            initApplicationButton(getView());
        }
    }

    private void initResignationButton(View view) {
        view.findViewById(R.id.rl_resignation_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rl_application_layout).setVisibility(View.GONE);
        AppCompatImageButton btnResign = view.findViewById(R.id.ib_resign);
        btnResign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Response<Void>>() {
                            @Override
                            protected Response<Void> doInBackground(String... params) {
                                String authToken = params[0];
                                return resign(authToken);
                            }

                            private Response<Void> resign(String authToken) {
                                try {
                                    return EventService.getInstance()
                                            .eventService()
                                            .resign(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Response<Void> response) {
                                informAboutResult(v, response, "Pomyślnie wycofano zgłoszenie");
                            }
                        });
            }
        });

    }

    private void initApplicationButton(final View view) {
        view.findViewById(R.id.rl_application_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.rl_resignation_layout).setVisibility(View.GONE);
        AppCompatImageButton btnApply = view.findViewById(R.id.ib_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Response<Void>>() {
                            @Override
                            protected Response<Void> doInBackground(String... params) {
                                String authToken = params[0];
                                return apply(authToken);
                            }

                            private Response<Void> apply(String authToken) {
                                try {
                                    return EventService.getInstance()
                                            .eventService()
                                            .apply(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Response<Void> response) {
                                 informAboutResult(view, response, "Zgłoszenie zostało wysłane!");
                            }
                        });
            }
        });
    }

    private void initRejectAction(final View view) {
        AppCompatImageButton btnReject = view.findViewById(R.id.ib_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Response<Void>>() {
                            @Override
                            protected Response<Void> doInBackground(String... params) {
                                String authToken = params[0];
                                return rejectInvitation(authToken);
                            }

                            private Response<Void> rejectInvitation(String authToken) {
                                try {
                                    return EventService.getInstance()
                                            .eventService()
                                            .rejectInvitation(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Response<Void> response) {
                                informAboutResult(view, response, "Zaproszenie zostało odrzucone!");
                            }
                        });
            }
        });
    }

    private void initAcceptAction(final View view) {
        AppCompatImageButton btnAccept = view.findViewById(R.id.ib_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                        new AsyncTask<String, Void, Response<Void>>() {
                            @Override
                            protected Response<Void> doInBackground(String... params) {
                                String authToken = params[0];
                                return acceptInvitation(authToken);
                            }

                            private Response<Void> acceptInvitation(String authToken) {
                                try {
                                    return EventService.getInstance()
                                            .eventService()
                                            .acceptInvitation(authToken, eventGuid)
                                            .execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Response<Void> response) {
                                informAboutResult(view, response, "Zaproszenie zostało zaakceptowane!");
                            }
                        });
            }
        });
    }

    private void informAboutResult(View view, Response<Void> response, String message) {
        if (response != null && response.isSuccessful()) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            getActivity().getFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.CANNOT_UPDATE_OFFLINE_MODE, Toast.LENGTH_SHORT).show();
        }
    }
}
