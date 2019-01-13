package pl.basistam.wloczykij.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.adapters.WeatherAdapter;
import pl.basistam.wloczykij.dto.WeatherDto;
import pl.basistam.wloczykij.errors.ErrorMessages;
import pl.basistam.wloczykij.service.WeatherService;
import retrofit2.Response;

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
                    Response<List<WeatherDto>> response = WeatherService.getInstance()
                            .weatherService()
                            .getWeather()
                            .execute();
                    if (!response.isSuccessful()) {
                        return null;
                    }
                    return response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<WeatherDto> weatherDtos) {
                if (weatherDtos != null) {
                    ListView lvWeather = view.findViewById(R.id.lv_weather);
                    lvWeather.setAdapter(new WeatherAdapter(getActivity(), weatherDtos));
                } else {
                    Toast.makeText(getActivity().getBaseContext(), ErrorMessages.OFFLINE_MODE, Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                }
            }
        }.execute();
    }

}
