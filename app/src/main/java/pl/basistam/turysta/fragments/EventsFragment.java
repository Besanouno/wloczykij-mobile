package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;

import pl.basistam.turysta.R;

public class EventsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TabHost host = (TabHost)view.findViewById(R.id.tab_host);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Nadchodzące");
        spec.setContent(R.id.tab_upcoming_events);
        spec.setIndicator("Nadchodzące");
        host.addTab(spec);
        final ListView lvUpcomingEvents = view.findViewById(R.id.lv_upcoming_events);
        

        //Tab 2
        spec = host.newTabSpec("Zaproszenia");
        spec.setContent(R.id.tab_invitations);
        spec.setIndicator("Zaproszenia");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Archiwalne");
        spec.setContent(R.id.tab_archival_events);
        spec.setIndicator("Archiwalne");
        host.addTab(spec);
    }
}
