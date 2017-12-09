package pl.basistam.turysta.fragments.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.basistam.turysta.R;
import pl.basistam.turysta.fragments.MapViewFragment;
import pl.basistam.turysta.map.Route;

public class ArchivalEventFragment extends EventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            String guid = getArguments().getString("guid");
            if (guid != null) {
                this.eventGuid = guid;
            }
            this.route = new Route(getArguments().getIntegerArrayList("trailIds"));
        } else {
            this.route = new Route(null);
        }
        return inflater.inflate(R.layout.fragment_guest_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        hideAcceptAndRejectButtons(view);
        initView(view);
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
                bundle.putBoolean("editable", false);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.content, fragment)
                        .commit();
            }
        });
    }

    private void hideAcceptAndRejectButtons(View view) {
        view.findViewById(R.id.ib_accept).setVisibility(View.GONE);
        view.findViewById(R.id.ib_reject).setVisibility(View.GONE);
    }
}
