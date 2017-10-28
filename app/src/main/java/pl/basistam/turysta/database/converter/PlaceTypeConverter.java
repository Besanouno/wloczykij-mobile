package pl.basistam.turysta.database.converter;

import android.arch.persistence.room.TypeConverter;

import pl.basistam.turysta.database.type.PlaceType;

public class PlaceTypeConverter {
    @TypeConverter
    public int fromPlaceType(PlaceType placeType) {
        return placeType != null ? placeType.getValue() : 0;
    }

    @TypeConverter
    public PlaceType fromNumber(int number) {
        return PlaceType.fromValue(number);
    }
}
