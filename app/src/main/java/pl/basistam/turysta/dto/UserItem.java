package pl.basistam.turysta.dto;

public class UserItem {
    private String name;
    private String login;
    private boolean status;

    public UserItem(String name, String login, boolean status) {
        this.name = name;
        this.login = login;
        this.status = status;
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

    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFullName() {
        return this.name + " (" + this.login + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserItem)) {
            return false;
        }
        UserItem another = (UserItem) obj;
        return login.equals(another.login);
    }
}
