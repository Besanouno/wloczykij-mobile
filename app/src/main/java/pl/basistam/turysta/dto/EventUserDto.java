package pl.basistam.turysta.dto;

public class EventUserDto {
    private String login;
    private String name;
    private String status;

    public EventUserDto(String login, String name, String status) {
        this.login = login;
        this.name = name;
        this.status = status;
    }

    public EventUserDto() {
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
