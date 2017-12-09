package pl.basistam.turysta;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

import pl.basistam.turysta.auth.ServerAuthenticateImpl;
import pl.basistam.turysta.components.utils.KeyboardUtils;
import pl.basistam.turysta.dto.Errors;
import pl.basistam.turysta.dto.UserInputDto;
import pl.basistam.turysta.errors.ErrorMessages;
import pl.basistam.turysta.errors.ServerConnectionException;
import retrofit2.Response;

import static pl.basistam.turysta.LoginActivity.KEY_ERROR_MESSAGE;
import static pl.basistam.turysta.LoginActivity.PARAM_USER_PASS;

public class SignUpActivity extends AccountAuthenticatorActivity {
    
    private TextView edtLogin;
    private TextView edtEmail;
    private TextView edtPassword;
    private TextView edtRepassword;
    private TextView edtCity;
    private TextView edtYearOfBirth;
    private TextView edtFirstName;
    private TextView edtLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loadFields();

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
        if (!validateFields()) {
            return;
        }
        new AsyncTask<String, Void, Intent>() {
            @Override
            protected Intent doInBackground(String... params) {
                Bundle data = new Bundle();
                try {
                    UserInputDto user = prepareUser();
                    Response<Void> response = ServerAuthenticateImpl.getInstance().signUp(user);
                    if (!response.isSuccessful()) {
                        if (response.code() == 404) {
                            data.putString(KEY_ERROR_MESSAGE, ErrorMessages.OFFLINE_MODE);
                        } else if (response.code() == 409) {
                            String errorCode = response.errorBody().string();
                            Gson gson = new GsonBuilder().create();
                            Errors errors = gson.fromJson(errorCode, Errors.class);
                            data.putString(KEY_ERROR_MESSAGE, errors.printable());
                        } else {
                            data.putString(KEY_ERROR_MESSAGE, response.message());
                        }
                    }
                } catch (ServerConnectionException e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Rejestracja przebiegła pomyślnie, zaloguj się!", Toast.LENGTH_LONG).show();
                    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, edtLogin.getText().toString());
                    intent.putExtra(PARAM_USER_PASS, edtPassword.getText().toString());
                    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, LoginActivity.ARG_ACCOUNT_TYPE);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @NonNull
    private UserInputDto prepareUser() {
        UserInputDto user = new UserInputDto();
        user.setLogin(edtLogin.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setPassword(edtPassword.getText().toString());
        user.setCity(edtCity.getText().toString());
        user.setFirstName(edtFirstName.getText().toString());
        user.setLastName(edtLastName.getText().toString());
        user.setYearOfBirth(Integer.parseInt(edtYearOfBirth.getText().toString()));
        return user;
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(edtLogin.getText())) {
            edtLogin.setError(getString(R.string.error_field_required));
            edtLogin.requestFocus();
            return false;
        }
        if (edtLogin.getText().length() > 16 || edtLogin.getText().length() < 3){
            edtLogin.setError("To pole musi zawierać minimum 3 i maksimum 16 znaków!");
            edtLogin.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edtEmail.getText())) {
            edtEmail.setError(getString(R.string.error_field_required));
            edtEmail.requestFocus();
            return false;
        }
        if (!edtEmail.getText().toString().contains("@") || !edtEmail.getText().toString().contains(".")) {
            edtEmail.setError("Nieprawidłowy format!");
            edtEmail.requestFocus();
            return false;
        }
        if (edtPassword.getText().toString().length() < 8) {
            edtPassword.setError("Hasło musi zawierać minimum 8 znaków!");
            edtPassword.requestFocus();
            return false;
        }
        if (!edtPassword.getText().toString().equals(edtRepassword.getText().toString())) {
            edtPassword.setError("Podane hasła są różne!");
            edtRepassword.setError("Podane hasła są różne!");
            return false;
        }
        if (TextUtils.isEmpty(edtFirstName.getText())) {
            edtFirstName.setError(getString(R.string.error_field_required));
            edtFirstName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edtLastName.getText())) {
            edtLastName.setError(getString(R.string.error_field_required));
            edtLastName.requestFocus();
            return false;
        }
        if (edtFirstName.getText().length() > 255) {
            edtFirstName.setError("To pole może zawierać maksymalnie 255 znaków");
            edtFirstName.requestFocus();
            return false;
        }
        if (edtLastName.getText().length() > 255) {
            edtLastName.setError("To pole może zawierać maksymalnie 255 znaków");
            edtLastName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edtYearOfBirth.getText())) {
            edtLastName.setError(getString(R.string.error_field_required));
            edtLastName.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    
    private void loadFields() {
        edtLogin = findViewById(R.id.edt_login);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtRepassword = findViewById(R.id.edt_repeat_password);
        edtCity = findViewById(R.id.edt_city);
        edtYearOfBirth = findViewById(R.id.edt_year_of_birth);
        edtFirstName = findViewById(R.id.edt_first_name);
        edtLastName = findViewById(R.id.edt_last_name);
    }
}

