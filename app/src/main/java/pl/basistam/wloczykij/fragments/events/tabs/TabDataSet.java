package pl.basistam.wloczykij.fragments.events.tabs;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import pl.basistam.wloczykij.adapters.EventAdapter;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.dto.EventSimpleDetails;
import pl.basistam.wloczykij.errors.ErrorMessages;
import retrofit2.Call;

public abstract class TabDataSet {
    private final Activity activity;
    private boolean updated = false;

    TabDataSet(Activity activity) {
        this.activity = activity;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void updateView(View view) {
        final ListView listView = view.findViewById(getListViewId());
        LoggedUser.getInstance()
                .sendAuthorizedRequest(activity.getBaseContext(), prepareTaskToDownloadEventsAndUpdateView(listView));
        updated = true;
    }

    protected abstract Call<List<EventSimpleDetails>> prepareRequest(String authToken);

    protected abstract int getListViewId();

    private AsyncTask<String, Void, List<EventSimpleDetails>> prepareTaskToDownloadEventsAndUpdateView(final ListView listView) {
        return new AsyncTask<String, Void, List<EventSimpleDetails>>() {
            @Override
            protected List<EventSimpleDetails> doInBackground(String... params) {
                String authToken = params[0];
                try {
                    return prepareRequest(authToken)
                            .execute()
                            .body();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<EventSimpleDetails> events) {
                if (events != null)
                    listView.setAdapter(new EventAdapter(events, activity));
                else {
                    Toast.makeText(activity.getBaseContext(), ErrorMessages.OFFLINE_MODE, Toast.LENGTH_LONG).show();
                    activity.getFragmentManager().popBackStack();

                }
            }
        };
    }
}