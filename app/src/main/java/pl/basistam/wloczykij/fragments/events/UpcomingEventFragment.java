package pl.basistam.wloczykij.fragments.events;


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
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.dto.EventDto;
import pl.basistam.wloczykij.errors.ErrorMessages;
import pl.basistam.wloczykij.fragments.MapViewFragment;
import pl.basistam.wloczykij.fragments.MessagesFragment;
import pl.basistam.wloczykij.items.EventUserItem;
import pl.basistam.wloczykij.enums.EventUserStatus;
import pl.basistam.wloczykij.map.Route;
import pl.basistam.wloczykij.service.EventService;
import pl.basistam.wloczykij.service.Callback;
import pl.basistam.wloczykij.service.retrofit.RetrofitEventService;
import retrofit2.Call;
import retrofit2.Response;

public class UpcomingEventFragment extends EventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            this.eventGuid = getArguments().getString("guid");
            this.isAdmin = getArguments().getBoolean("isAdmin");
            this.route = new Route(getArguments().getIntegerArrayList("trailIds"));
        }else {
            this.route = new Route(null);
        }
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initBtnNotes(view);
        initBtnSave(view);
        initBtnFriends(view);
        initRightButton(view);
        initRouteButton(view);
    }

    private void initRouteButton(View view) {
        final AppCompatImageButton btnRoute = view.findViewById(R.id.ib_route);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapViewFragment fragment = new MapViewFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("route", route);
                bundle.putSerializable("editable", isAdmin);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.content, fragment)
                        .commit();
            }
        });
    }

    private void initRightButton(View view) {
        if (isAdmin) {
            initBtnRemove(view);
        } else {
            initBtnLeave(view);
        }
    }

    private void initBtnRemove(View view) {
        if (eventGuid == null) {
            return;
        }
        final AppCompatImageButton btnRemove = view.findViewById(R.id.ib_remove);
        btnRemove.setVisibility(View.VISIBLE);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Usun wydarzenie")
                        .setMessage("Na pewno chcesz usunąć wydarzenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                                        new AsyncTask<String, Void, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(String... params) {
                                                String authToken = params[0];
                                                return removeEvent(authToken);
                                            }

                                            @Override
                                            protected void onPostExecute(Boolean result) {
                                                if (result) {
                                                    getActivity().getFragmentManager().popBackStack();
                                                } else {
                                                    Toast.makeText(getActivity().getBaseContext(), ErrorMessages.CANNOT_UPDATE_OFFLINE_MODE, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {// do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_warning_sign)
                        .show();

            }
        });
    }


    private boolean removeEvent(String authToken) {
        try {
            Response<Void> response = EventService.getInstance()
                    .eventService()
                    .remove(authToken, eventGuid)
                    .execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initBtnNotes(View view) {
        ImageButton ibNotes = view.findViewById(R.id.ib_notes);
        ibNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesFragment fragment = new MessagesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("eventGuid", eventGuid);
                bundle.putString("eventName", getName());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.content, fragment)
                        .commit();
            }
        });
    }

    private void initBtnLeave(View view) {
        final AppCompatImageButton btnLeave = view.findViewById(R.id.ib_leave);
        btnLeave.setVisibility(View.VISIBLE);
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Opuść wydarzenie")
                        .setMessage("Na pewno chcesz opuścić wydarzenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                                        new AsyncTask<String, Void, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(String... params) {
                                                String authToken = params[0];
                                                return leaveEvent(authToken);
                                            }

                                            @Override
                                            protected void onPostExecute(Boolean result) {
                                                finishAction(result, "Opuściłeś wydarzenie");
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
                        .setIcon(R.drawable.ic_warning_sign)
                        .show();

            }
        });
    }

    private boolean leaveEvent(String authToken) {
        try {
            Response<Void> response = EventService.getInstance()
                    .eventService()
                    .leave(authToken, eventGuid)
                    .execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Zapisz wydarzenie")
                        .setMessage("Na pewno chcesz zapisać wydarzenie?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final EventDto eventDto = prepareEventDto();
                                if (eventDto == null) return;

                                LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                                        new AsyncTask<String, Void, Boolean>() {
                                            @Override
                                            protected Boolean doInBackground(String... params) {
                                                String authToken = params[0];
                                                return saveEvent(authToken, eventDto);
                                            }

                                            @Override
                                            protected void onPostExecute(Boolean result) {
                                                finishAction(result, "Wydarzenie zostało zapisane");
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

    private void finishAction(Boolean result, String successMessage) {
        if (result) {
            if (getView() != null) {
                Snackbar.make(getView(), successMessage, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            getActivity().getFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.CANNOT_UPDATE_OFFLINE_MODE, Toast.LENGTH_LONG).show();
        }
    }

    private boolean saveEvent(String authToken, EventDto eventDto) {
        try {
            RetrofitEventService eventService = EventService.getInstance()
                    .eventService();
            Call<Void> request = (eventGuid == null)
                    ? eventService.saveEvent(authToken, eventDto)
                    : eventService.updateEvent(authToken, eventGuid, eventDto);
            return request.execute().isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
