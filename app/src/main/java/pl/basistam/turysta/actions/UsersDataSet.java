package pl.basistam.turysta.actions;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.service.UserService;


public abstract class UsersDataSet implements Serializable {

    protected final Context context;

    public UsersDataSet(Context context) {
        this.context = context;
    }

    protected List<UserDto> downloadFriends(String authToken) {
        try {
            return UserService.getInstance()
                    .userService()
                    .getRelations(authToken)
                    .execute()
                    .body();
        } catch (IOException e) {
            Toast.makeText(context, "Błąd połączenia z serwerem", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    protected Page<UserDto> downloadUsersByPattern(String authToken, String pattern, int page, int size) {
        try {
            return UserService.getInstance()
                    .userService()
                    .getUsersByPattern(authToken, pattern, page, size)
                    .execute()
                    .body();
        } catch (IOException e) {
            Toast.makeText(context, "Błąd połączenia z serwerem", Toast.LENGTH_LONG).show();
        }
        return new Page<>();
    }
}
