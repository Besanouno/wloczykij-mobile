package pl.basistam.turysta.actions;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.dto.EventUserDto;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.RelationItem;
import pl.basistam.turysta.dto.UserDto;
import retrofit2.Converter;

public class EventUsersDataSet extends UsersDataSet {

    private String eventGuid;
    private List<String> participantsLogins;

    public EventUsersDataSet(Context context, String eventGuid, List<String> participantsLogins) {
        super(context);
        this.eventGuid = eventGuid;
        this.participantsLogins = participantsLogins;
    }

    public List<EventUserDto> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        return convertFriendsToUserItems(friends);
    }

    private List<EventUserDto> convertFriendsToUserItems(List<UserDto> friends) {
        ArrayList<EventUserDto> result = new ArrayList<>(friends.size());
        for (UserDto u: friends) {
//            result.add(new EventUserDto(u.getFirstName() + " " + u.getLastName(), u.getLogin()));
        }
        return result;
    }

    public Page<EventUserDto> getAllUsers(String authToken, String pattern, int page, int size) {
        Page<UserDto> users = downloadUsersByPattern(authToken, pattern, page, size);
        //return convertUsersPageToUserItemsPage(users);
        return null;
    }

    private Page<RelationItem> convertUsersPageToUserItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, RelationItem>() {
            @Override
            public RelationItem convert(@NonNull UserDto u) throws IOException {
                return new RelationItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), participantsLogins.contains(u.getLogin()));
            }
        });
    }

    /*@Override
    public void postExecute(final List<RelationItem> changes) {
        if (!changes.isEmpty()) {
            LoggedUser.getInstance().sendAuthorizedRequest(context,
                    new AsyncTask<String, Void, Object>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            try {
                                String authtoken = params[0];
                                EventService.getInstance()
                                        .eventService()
                                        .updateParticipants(authtoken, eventGuid, changes)
                                        .execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
        }
    }*/
}
