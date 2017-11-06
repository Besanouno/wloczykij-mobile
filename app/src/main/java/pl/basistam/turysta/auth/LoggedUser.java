package pl.basistam.turysta.auth;

import pl.basistam.turysta.model.UserDetails;

public class LoggedUser {

    private static LoggedUser loggedUser;

    public static LoggedUser getInstance() {
        if (loggedUser == null) {
            synchronized (LoggedUser.class) {
                if (loggedUser == null) {
                    loggedUser = new LoggedUser();
                }
            }
        }
        return loggedUser;
    }

    private UserDetails userDetails;

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
