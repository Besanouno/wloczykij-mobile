package pl.basistam.turysta.actions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.enums.EventUserStatus;
import retrofit2.Converter;

public class EventUsersDataSet extends UsersDataSet {

    private List<EventUserDto> eventUsers;

    public EventUsersDataSet(Context context, List<EventUserDto> eventUsers) {
        super(context);
        this.eventUsers = eventUsers;
    }

    public List<EventUserDto> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        return convertFriendsToUserItems(friends);
    }

    private List<EventUserDto> convertFriendsToUserItems(List<UserDto> friends) {
        ArrayList<EventUserDto> result = new ArrayList<>(friends.size());
        for (UserDto u: friends) {
            result.add(convertUserDtoToEventUserDto(u));
        }
        return result;
    }

    private String getStatus(String login) {
        for (EventUserDto e: eventUsers) {
            if (e.getLogin().equals(login)) {
                return e.getStatus();
            }
        }
        return EventUserStatus.NONE.name();
    }

    public Page<EventUserDto> getAllUsers(String authToken, String pattern, int page, int size) {
        Page<UserDto> users = downloadUsersByPattern(authToken, pattern, page, size);
        return convertUsersPageToUserItemsPage(users);
    }

    private Page<EventUserDto> convertUsersPageToUserItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, EventUserDto>() {
            @Override
            public EventUserDto convert(@NonNull UserDto u) throws IOException {
                return convertUserDtoToEventUserDto(u);
            }
        });
    }

    private EventUserDto convertUserDtoToEventUserDto(UserDto u) {
        return new EventUserDto(u.getLogin(), u.getFirstName() + " " + u.getLastName(),  getStatus(u.getLogin()));
    }
}
