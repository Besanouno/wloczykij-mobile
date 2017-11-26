package pl.basistam.turysta.fragments.events;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.HashMap;
import java.util.Map;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.EventSimpleDetails;
import pl.basistam.turysta.fragments.events.tabs.ArchivalEventsDataSet;
import pl.basistam.turysta.fragments.events.tabs.InvitationsDataSet;
import pl.basistam.turysta.fragments.events.tabs.TabDataSet;
import pl.basistam.turysta.fragments.events.tabs.UpcomingEventsDataSet;

public class EventsFragment extends Fragment {

    private static final String UPCOMING_EVENTS = "Nadchodzące";
    private static final String INVITATIONS = "Zaproszenia";
    private static final String ARCHIVAL_EVENTS = "Archiwalne";

    private Map<String, TabDataSet> tabsDataSets = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tabsDataSets.put(UPCOMING_EVENTS, new UpcomingEventsDataSet(getActivity()));
        tabsDataSets.put(INVITATIONS, new InvitationsDataSet(getActivity()));
        tabsDataSets.put(ARCHIVAL_EVENTS, new ArchivalEventsDataSet(getActivity()));

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        TabHost host = view.findViewById(R.id.tab_host);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(UPCOMING_EVENTS);
        spec.setContent(R.id.tab_upcoming_events);
        spec.setIndicator(UPCOMING_EVENTS);
        host.addTab(spec);
        tabsDataSets.get(UPCOMING_EVENTS).updateView(view);

        //Tab 2
        spec = host.newTabSpec(INVITATIONS);
        spec.setContent(R.id.tab_invitations);
        spec.setIndicator(INVITATIONS);
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec(ARCHIVAL_EVENTS);
        spec.setContent(R.id.tab_archival_events);
        spec.setIndicator(ARCHIVAL_EVENTS);
        host.addTab(spec);

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                TabDataSet tabDataSet = tabsDataSets.get(tabId);
                if (!tabDataSet.isUpdated()) {
                    tabDataSet.updateView(view);
                }
            }
        });

        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content, new UpcomingEventFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        ListView lvUpcomingEvents = view.findViewById(R.id.lv_upcoming_events);
        lvUpcomingEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                UpcomingEventFragment fragment = new UpcomingEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        ListView lvInvitations = view.findViewById(R.id.lv_invitations);
        lvInvitations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                InvitationEventFragment fragment = new InvitationEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ListView lvArchivalEvents = view.findViewById(R.id.lv_archival_events);
        lvArchivalEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                ArchivalEventFragment fragment = new ArchivalEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.content, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}



