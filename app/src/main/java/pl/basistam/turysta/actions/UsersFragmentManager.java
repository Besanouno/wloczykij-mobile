package pl.basistam.turysta.actions;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.service.UserService;


public abstract class UsersFragmentManager implements Serializable {
    public abstract List<UserItem> getFriends(String authToken);
    public abstract Page<UserItem> getAllUsers(String authToken, String pattern, int page, int size);
    public abstract void postExecute(List<UserItem> changes);


    protected final Context context;

    public UsersFragmentManager(Context context) {
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
