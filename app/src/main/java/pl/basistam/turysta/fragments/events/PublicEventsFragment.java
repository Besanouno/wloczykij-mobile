package pl.basistam.turysta.fragments.events;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.EventAdapter;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.fragments.events.enums.GuestType;
import pl.basistam.turysta.service.EventService;

public class PublicEventsFragment extends Fragment {

    private ListView lvPublicEvents;
    private EventAdapter eventAdapter;

    private int page = 0;
    private int totalNumber;
    private final int SIZE = 15;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_public_events, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        eventAdapter = new EventAdapter(new ArrayList<EventSimpleDetails>(), getActivity());
        lvPublicEvents = view.findViewById(R.id.lv_public_events);
        lvPublicEvents.setAdapter(eventAdapter);
        lvPublicEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                GuestEventFragment fragment = new GuestEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                args.putSerializable("type", GuestType.VISITOR);
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .add(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        updateListView();
        initOnScrollListener();
    }

    private void initOnScrollListener() {
        lvPublicEvents.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isEndOfListReached() && canDownloadMore()) {
                    updateListView();
                }
            }

            private boolean isEndOfListReached() {
                return lvPublicEvents.getAdapter() != null &&
                        lvPublicEvents.getLastVisiblePosition() == lvPublicEvents.getAdapter().getCount() - 1 &&
                        (lvPublicEvents.getChildAt(lvPublicEvents.getChildCount() - 1) != null && lvPublicEvents.getChildAt(lvPublicEvents.getChildCount() - 1).getBottom() <= lvPublicEvents.getHeight());
            }
        });
    }

    private boolean canDownloadMore() {
        return eventAdapter.getCount() < totalNumber;
    }

    private void updateListView() {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, Page<EventSimpleDetails>>() {
                    @Override
                    protected Page<EventSimpleDetails> doInBackground(String... params) {
                        String authToken = params[0];
                        try {
                            return EventService.getInstance()
                                    .eventService()
                                    .getPublicEvents(authToken, page, SIZE)
                                    .execute()
                                    .body();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Page<EventSimpleDetails> events) {
                        if (events != null) {
                            eventAdapter.addAll(events.getContent());
                            eventAdapter.notifyDataSetChanged();
                            totalNumber = events.getTotalElements();
                            PublicEventsFragment.this.page++;
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.OFFLINE_MODE, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

