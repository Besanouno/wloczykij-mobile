package pl.basistam.wloczykij.dto;

import java.util.List;

public class Errors {
    public static final String LOGIN_OCCUPIED = "Podany login jest już zajęty!";
    public static final String EMAIL = "Podany email jest już zajęty!";

    private List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String printable() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < errors.size() - 1; i++) {
            result.append(errors.get(i)).append("\n");
        }
        result.append(errors.get(errors.size() - 1));
        return result.toString();
    }

    public boolean contains(String string) {
        return errors.contains(string);
    }
}
