package pl.basistam.turysta.actions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.Page;
import pl.basistam.turysta.dto.UserDto;
import pl.basistam.turysta.dto.UserItem;
import pl.basistam.turysta.service.EventService;
import pl.basistam.turysta.service.UserService;
import retrofit2.Converter;

public class InvitationsManager extends UsersFragmentManager {
    private String eventGuid;
    private List<String> participantsLogins;

    public InvitationsManager(Context context, String eventGuid, List<String> participantsLogins) {
        super(context);
        this.eventGuid = eventGuid;
        this.participantsLogins = participantsLogins;
    }

    @Override
    public List<UserItem> getFriends(String authToken) {
        List<UserDto> friends = downloadFriends(authToken);
        return convertFriendsToUserItems(friends);
    }

    private List<UserItem> convertFriendsToUserItems(List<UserDto> friends) {
        ArrayList<UserItem> result = new ArrayList<>(friends.size());
        for (UserDto u: friends) {
            result.add(new UserItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), participantsLogins.contains(u.getLogin())));
        }
        return result;
    }

    @Override
    public Page<UserItem> getAllUsers(String authToken, String pattern, int page, int size) {
        Page<UserDto> users = downloadUsersByPattern(authToken, pattern, page, size);
        return convertUsersPageToUserItemsPage(users);
    }

    private Page<UserItem> convertUsersPageToUserItemsPage(Page<UserDto> page) {
        return page.map(new Converter<UserDto, UserItem>() {
            @Override
            public UserItem convert(@NonNull UserDto u) throws IOException {
                return new UserItem(u.getFirstName() + " " + u.getLastName(), u.getLogin(), participantsLogins.contains(u.getLogin()));
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
    }
}
