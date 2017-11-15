package pl.basistam.turysta;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.ServerAuthenticateImpl;
import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.json.UserInputJson;

import static pl.basistam.turysta.LoginActivity.ARG_ACCOUNT_TYPE;
import static pl.basistam.turysta.LoginActivity.KEY_ERROR_MESSAGE;

public class SignUpActivity extends AccountAuthenticatorActivity /*implements LoaderCallbacks<Cursor> */{

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        findViewById(R.id.already_member).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.sign_up_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        final String login = ((TextView) findViewById(R.id.login)).getText().toString().trim();
        final String email = ((TextView) findViewById(R.id.email)).getText().toString().trim();
        final String password = ((TextView) findViewById(R.id.password)).getText().toString().trim();
        final String city = ((TextView) findViewById(R.id.city)).getText().toString().trim();
        final String yearOfBirth = ((TextView) findViewById(R.id.year_of_birth)).getText().toString().trim();
        final String firstName = ((TextView) findViewById(R.id.first_name)).getText().toString().trim();
        final String lastName = ((TextView) findViewById(R.id.last_name)).getText().toString().trim();

        new AsyncTask<String, Void, Intent>() {
            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                try {
                    UserInputJson user = new UserInputJson();
                    user.setLogin(login);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setCity(city);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setYearOfBirth(Integer.parseInt(yearOfBirth));

                    ServerAuthenticateImpl.getInstance()
                            .signUp(user);
                } catch (ServerConnectionException e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Rejestracja przebiegła pomyślnie, zaloguj się!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}

