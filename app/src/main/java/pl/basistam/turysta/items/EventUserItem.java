package pl.basistam.turysta.items;

public class EventUserItem {
    private Long userId;
    private String login;
    private String name;
    private String status;

    public EventUserItem(Long userId, String login, String name, String status) {
        this.userId = userId;
        this.login = login;
        this.name = name;
        this.status = status;
    }

    public EventUserItem() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
