package pl.basistam.wloczykij.actions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.wloczykij.items.EventUserItem;
import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.dto.UserDto;
import pl.basistam.wloczykij.enums.EventUserStatus;
import retrofit2.Converter;

public class EventUsersDataSet extends UsersDataSet {

    private List<EventUserItem> eventUsers;

    public EventUsersDataSet(Context context, List<EventUserItem> eventUsers) {
        super(context);
        this.eventUsers = eventUsers;
    }

    public List<EventUserItem> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        return convertFriendsToUserItems(friends);
    }

    private List<EventUserItem> convertFriendsToUserItems(List<UserDto> friends) {
        ArrayList<EventUserItem> result = new ArrayList<>(friends.size());
        for (UserDto u: friends) {
            result.add(convertUserDtoToEventUserDto(u));
        }
        return result;
    }

    private String getStatus(String login) {
        for (EventUserItem e: eventUsers) {
            if (e.getLogin().equals(login)) {
                return e.getStatus();
            }
        }
        return EventUserStatus.NONE.name();
    }

    public Page<EventUserItem> getAllUsers(String authToken, String pattern, int page, int size) {
        Page<UserDto> users = downloadUsersByPattern(authToken, pattern, page, size);
        return convertUsersPageToUserItemsPage(users);
    }

    private Page<EventUserItem> convertUsersPageToUserItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, EventUserItem>() {
            @Override
            public EventUserItem convert(@NonNull UserDto u) throws IOException {
                return convertUserDtoToEventUserDto(u);
            }
        });
    }

    private EventUserItem convertUserDtoToEventUserDto(UserDto u) {
        return new EventUserItem(u.getId(), u.getLogin(), u.getFirstName() + " " + u.getLastName(),  getStatus(u.getLogin()));
    }
}
