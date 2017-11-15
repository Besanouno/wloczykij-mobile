package pl.basistam.turysta.json;

import lombok.Data;

@Data
public class UserInputJson {
    private String email;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String city;
}
