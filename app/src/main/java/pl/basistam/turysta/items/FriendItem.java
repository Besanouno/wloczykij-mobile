package pl.basistam.turysta.items;

public class FriendItem {
    private String name;
    private boolean friend;

    public FriendItem(String name, boolean friend) {
        this.name = name;
        this.friend = friend;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isFriend() {
        return friend;
    }
    public void setFriend(boolean friend) {
        this.friend = friend;
    }
}
