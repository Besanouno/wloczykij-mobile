package pl.basistam.turysta.fragments.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import pl.basistam.turysta.dto.EventDto;
import pl.basistam.turysta.enums.EventUserStatus;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.fragments.MapViewFragment;
import pl.basistam.turysta.fragments.events.enums.GuestType;
import pl.basistam.turysta.items.EventUserItem;
import pl.basistam.turysta.map.Route;
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
            this.route = new Route(getArguments().getIntegerArrayList("trailIds"));
        } else {
            this.route = new Route(null);
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
            initRouteButton(view);
        }
    }

    private void initRouteButton(View view) {
        final AppCompatImageButton btnRoute = view.findViewById(R.id.ib_route);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapViewFragment fragment = new MapViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("route", route);
                bundle.putBoolean("editable", false);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.content, fragment)
                        .commit();
            }
        });
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
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Wycofaj zgłoszenie")
                        .setMessage("Na pewno chcesz wycofać zgłoszenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_ask)
                        .show();

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
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Wyślij zgłoszenie")
                        .setMessage("Na pewno chcesz wysłać zgłoszenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_ask)
                        .show();

            }
        });
    }

    private void initRejectAction(final View view) {
        AppCompatImageButton btnReject = view.findViewById(R.id.ib_reject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Odrzuć zaproszenie")
                        .setMessage("Na pewno chcesz odrzucić zaproszenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_ask)
                        .show();
            }
        });
    }

    private void initAcceptAction(final View view) {
        AppCompatImageButton btnAccept = view.findViewById(R.id.ib_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Zaakceptuj zaproszenie")
                        .setMessage("Na pewno chcesz zaakceptować zaproszenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
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
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_ask)
                        .show();
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
