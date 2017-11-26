package pl.basistam.turysta.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;

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

    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }

    public String getLogin() {
        return account.name;
    }

    public <T> void sendAuthorizedRequest(Context context, final AsyncTask<String, Void, T> task) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(final AccountManagerFuture<Bundle> future) {
                        Bundle bundle;
                        try {
                            bundle = future.getResult();
                            final String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            task.execute("Bearer " + authtoken);
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
    }
}
