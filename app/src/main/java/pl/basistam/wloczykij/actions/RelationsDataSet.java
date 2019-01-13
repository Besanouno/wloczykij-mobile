package pl.basistam.wloczykij.actions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.items.RelationItem;
import pl.basistam.wloczykij.dto.UserDto;
import retrofit2.Converter;

public class RelationsDataSet extends UsersDataSet {

    private List<String> friendsLogins;

    public RelationsDataSet(Context context) {
        super(context);
    }

    public List<RelationItem> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        keepFriendsLogins(friends);
        return convertFriendsToItems(friends);
    }


    private void keepFriendsLogins(List<UserDto> friends) {
        friendsLogins = new ArrayList<>(friends.size());
        for (UserDto u : friends) {
            friendsLogins.add(u.getLogin());
        }
    }

    private List<RelationItem> convertFriendsToItems(List<UserDto> friends) {
        List<RelationItem> result = new ArrayList<>(friends.size());
        for (UserDto u : friends) {
            result.add(new RelationItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), true));
        }
        return result;
    }

    public Page<RelationItem> getAllUsers(String authToken, String pattern, int pageNumber, int size) {
        verifyFriendsLogins(authToken);
        Page<UserDto> page = downloadUsersByPattern(authToken, pattern, pageNumber, size);
        return convertUsersPageToItemsPage(page);
    }

    private void verifyFriendsLogins(String authToken) {
        if (friendsLogins == null) {
            getFriends(authToken);
        }
    }

    private Page<RelationItem> convertUsersPageToItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, RelationItem>() {
            @Override
            public RelationItem convert(@NonNull UserDto u) throws IOException {
                return new RelationItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), friendsLogins.contains(u.getLogin()));
            }
        });
    }
}
