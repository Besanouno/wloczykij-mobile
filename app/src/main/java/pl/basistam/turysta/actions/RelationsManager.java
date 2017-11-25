package pl.basistam.turysta.actions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.service.UserService;
import retrofit2.Converter;

public class RelationsManager extends UsersFragmentManager {

    private List<String> friendsLogins;

    public RelationsManager(Context context) {
        super(context);
    }

    @Override
    public List<UserItem> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        keepFriendsLogins(friends);
        return convertFriendsToUserItems(friends);
    }


    private void keepFriendsLogins(List<UserDto> friends) {
        friendsLogins = new ArrayList<>(friends.size());
        for (UserDto u : friends) {
            friendsLogins.add(u.getLogin());
        }
    }

    private List<UserItem> convertFriendsToUserItems(List<UserDto> friends) {
        List<UserItem> result = new ArrayList<>(friends.size());
        for (UserDto u : friends) {
            result.add(new UserItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), true));
        }
        return result;
    }

    @Override
    public Page<UserItem> getAllUsers(String authToken, String pattern, int pageNumber, int size) {
        verifyFriendsLogins(authToken);
        Page<UserDto> page = downloadUsersByPattern(authToken, pattern, pageNumber, size);
        return convertUsersPageToUserItemsPage(page);
    }

    private void verifyFriendsLogins(String authToken) {
        if (friendsLogins == null) {
            getFriends(authToken);
        }
    }

    private Page<UserItem> convertUsersPageToUserItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, UserItem>() {
            @Override
            public UserItem convert(@NonNull UserDto u) throws IOException {
                return new UserItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), friendsLogins.contains(u.getLogin()));
            }
        });
    }

    @Override
    public void postExecute(final List<UserItem> changes) {
        if (!changes.isEmpty()) {
            LoggedUser.getInstance().sendAuthorizedRequest(context,
                    new AsyncTask<String, Void, Object>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            try {
                                String authtoken = params[0];
                                UserService.getInstance()
                                        .userService()
                                        .updateRelations(authtoken, changes)
                                        .execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
        }
    }
}
