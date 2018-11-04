package pl.basistam.wloczykij.auth;

import android.accounts.Account;

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

    private Account account;
    private String email;

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
