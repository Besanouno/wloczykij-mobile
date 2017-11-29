package pl.basistam.turysta.fragments.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.basistam.turysta.R;

public class ArchivalEventFragment extends EventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            String guid = getArguments().getString("guid");
            if (guid != null) {
                this.eventGuid = guid;
            }
        }
        return inflater.inflate(R.layout.fragment_guest_event, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        hideAcceptAndRejectButtons(view);
        initView(view);
    }

    private void hideAcceptAndRejectButtons(View view) {
        view.findViewById(R.id.ib_accept).setVisibility(View.GONE);
        view.findViewById(R.id.ib_reject).setVisibility(View.GONE);
    }
}
