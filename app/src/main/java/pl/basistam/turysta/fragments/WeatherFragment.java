package pl.basistam.turysta.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.adapters.WeatherAdapter;
import pl.basistam.turysta.dto.WeatherDto;
import pl.basistam.turysta.service.WeatherService;

public class WeatherFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        downloadWeather(view);
    }

    private void downloadWeather(final View view) {
        new AsyncTask<Void, Void, List<WeatherDto>>() {

            @Override
            protected List<WeatherDto> doInBackground(Void... params) {
                try {
                    return WeatherService.getInstance()
                            .weatherService()
                            .getWeather()
                            .execute()
                            .body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<WeatherDto> weatherDtos) {
                ListView lvWeather = view.findViewById(R.id.lv_weather);
                lvWeather.setAdapter(new WeatherAdapter(getActivity(), weatherDtos));
            }
        }.execute();
    }

}
