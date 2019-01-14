package pl.basistam.wloczykij.fragments.events;

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

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.dto.EventSimpleDetails;
import pl.basistam.wloczykij.fragments.events.enums.GuestType;
import pl.basistam.wloczykij.fragments.events.tabs.ArchivalEventsDataSet;
import pl.basistam.wloczykij.fragments.events.tabs.InvitationsDataSet;
import pl.basistam.wloczykij.fragments.events.tabs.TabDataSet;
import pl.basistam.wloczykij.fragments.events.tabs.UpcomingEventsDataSet;

public class EventsFragment extends Fragment {

    private static final String UPCOMING_EVENTS = "Nowe";
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
        initUpcomingEvents(view, host);
        initInvitations(view, host);
        initArchivalEvents(view, host);
        //Tab 3

        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                TabDataSet tabDataSet = tabsDataSets.get(tabId);
                if (!tabDataSet.isUpdated()) {
                    tabDataSet.updateView(view);
                }
            }
        });
    }

    private void initArchivalEvents(View view, TabHost host) {
        TabHost.TabSpec spec = host.newTabSpec(ARCHIVAL_EVENTS);
        spec.setContent(R.id.tab_archival_events);
        spec.setIndicator(ARCHIVAL_EVENTS);
        host.addTab(spec);

        ListView lvArchivalEvents = view.findViewById(R.id.lv_archival_events);
        lvArchivalEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                ArchivalEventFragment fragment = new ArchivalEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                fragment.setArguments(args);
                showDetailsFragment(fragment);
            }
        });
    }

    private void initUpcomingEvents(View view, TabHost host) {
        TabHost.TabSpec spec = host.newTabSpec(UPCOMING_EVENTS);
        spec.setContent(R.id.tab_upcoming_events);
        spec.setIndicator(UPCOMING_EVENTS);
        host.addTab(spec);
        tabsDataSets.get(UPCOMING_EVENTS).updateView(view);

        FloatingActionButton btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpcomingEventFragment fragment = new UpcomingEventFragment();
                Bundle args = new Bundle();
                args.putBoolean("isAdmin", true);
                fragment.setArguments(args);
                showDetailsFragment(fragment);
            }
        });

        ListView lvUpcomingEvents = view.findViewById(R.id.lv_upcoming_events);
        lvUpcomingEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                UpcomingEventFragment fragment = new UpcomingEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                args.putBoolean("isAdmin", LoggedUser.getInstance().getLogin().equals(item.getInitiator()));
                fragment.setArguments(args);
                showDetailsFragment(fragment);
            }
        });
    }

    private void initInvitations(View view, TabHost host) {
        TabHost.TabSpec spec = host.newTabSpec(INVITATIONS);
        spec.setContent(R.id.tab_invitations);
        spec.setIndicator(INVITATIONS);
        host.addTab(spec);

        ListView lvInvitations = view.findViewById(R.id.lv_invitations);
        lvInvitations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventSimpleDetails item = (EventSimpleDetails) parent.getItemAtPosition(position);
                GuestEventFragment fragment = new GuestEventFragment();
                Bundle args = new Bundle();
                args.putString("guid", item.getGuid());
                args.putSerializable("type", GuestType.INVITED);
                fragment.setArguments(args);
                showDetailsFragment(fragment);
            }
        });
    }

    private void showDetailsFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }
}



