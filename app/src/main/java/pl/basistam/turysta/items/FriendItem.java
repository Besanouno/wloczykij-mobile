package pl.basistam.turysta.items;

public class FriendItem {
    private String name;
    private String login;
    private boolean friend;

    public FriendItem(String name, String login, boolean friend) {
        this.name = name;
        this.login = login;
        this.friend = friend;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isFriend() {
        return friend;
    }
    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getFullName() {
        return this.name + " (" + this.login + ")";
    }
}
