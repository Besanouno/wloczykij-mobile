package pl.basistam.turysta;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import pl.basistam.turysta.auth.ServerAuthenticateImpl;
import pl.basistam.turysta.exceptions.ServerConnectionException;
import pl.basistam.turysta.dto.UserInput;

import static pl.basistam.turysta.LoginActivity.KEY_ERROR_MESSAGE;

public class SignUpActivity extends AccountAuthenticatorActivity /*implements LoaderCallbacks<Cursor> */{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.tv_already_member).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.btn_signup).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        TextView edtLogin = findViewById(R.id.edt_login);
        final String login = edtLogin.getText().toString().trim();
        final String email = ((TextView) findViewById(R.id.edt_email)).getText().toString().trim();
        TextView edtPassword = findViewById(R.id.edt_password);
        final String password = edtPassword.getText().toString().trim();
        TextView edtRepassword = findViewById(R.id.edt_repeat_password);
        final String repassword = edtRepassword.getText().toString().trim();
        final String city = ((TextView) findViewById(R.id.edt_city)).getText().toString().trim();
        final String yearOfBirth = ((TextView) findViewById(R.id.edt_year_of_birth)).getText().toString().trim();
        final String firstName = ((TextView) findViewById(R.id.edt_first_name)).getText().toString().trim();
        final String lastName = ((TextView) findViewById(R.id.edt_last_name)).getText().toString().trim();

        if (login.isEmpty()) {
            edtLogin.setText("Login nie może być pusty!");
        } else  if (!password.equals(repassword)) {
            edtPassword.setText("Hasła nie pasują");
            edtRepassword.setText("Hasła nie pasują");
        }
        new AsyncTask<String, Void, Intent>() {
            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                try {
                    UserInput user = new UserInput();
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

