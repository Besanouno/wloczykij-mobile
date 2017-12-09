package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.WeatherDto;

public class WeatherAdapter extends BaseAdapter implements Serializable {
    private final List<WeatherDto> weathers;
    private final LayoutInflater inflater;

    public WeatherAdapter(Activity activity, List<WeatherDto> weathers) {
        this.weathers = weathers;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return weathers.size();
    }

    @Override
    public Object getItem(int position) {
        return weathers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_weather, null);
        }
        WeatherDto weather = (WeatherDto) getItem(position);
        ((TextView) convertView.findViewById(R.id.tvTime)).setText(weather.getTime());
        ((TextView) convertView.findViewById(R.id.tv_date)).setText(weather.getDate());
        ((TextView) convertView.findViewById(R.id.tvTempValue)).setText(Double.toString(round(weather.getTemperature(), 1)));
        ((TextView) convertView.findViewById(R.id.tvTempMinMaxValue)).setText(Double.toString(round(weather.getTemperatureMin(), 1)) + " do " + Double.toString(round(weather.getTemperatureMax(), 1)));
        ((TextView) convertView.findViewById(R.id.tvHumidityValue)).setText(Double.toString(weather.getHumidity()));
        ((TextView) convertView.findViewById(R.id.tvPressureValue)).setText(Double.toString(weather.getPressure()));
        ((TextView) convertView.findViewById(R.id.tvDescriptionValue)).setText(weather.getWeatherDescription());
        ((TextView) convertView.findViewById(R.id.tvWindSpeedValue)).setText(Double.toString(weather.getWindSpeed()));
        return convertView;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
