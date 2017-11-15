package pl.basistam.turysta.json;

import lombok.Data;

@Data
public class UserSimpleDetails {
    private String login;
    private String firstName;
    private String lastName;
}
