package pl.basistam.turysta.fragments;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.UserDetails;
import pl.basistam.turysta.service.UserService;
import pl.basistam.turysta.utils.Converter;

public class PersonFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final String login = getArguments().getString("login");
        final TextView tvLogin = view.findViewById(R.id.tv_login);
        final TextView tvFirstName = view.findViewById(R.id.tv_first_name);
        final TextView tvLastName = view.findViewById(R.id.tv_last_name);
        final TextView tvEmail = view.findViewById(R.id.tv_email);
        final TextView tvYearOfBirth = view.findViewById(R.id.tv_year_of_birth);
        final TextView tvCity = view.findViewById(R.id.tv_city);
        final TextView tvRegistered = view.findViewById(R.id.tv_registered);

        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, UserDetails>() {
                    @Override
                    protected UserDetails doInBackground(String... params) {
                        try {
                            String authToken = params[0];
                            return UserService.getInstance()
                                    .userService()
                                    .getPersonDetails(authToken, login)
                                    .execute()
                                    .body();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(UserDetails userDetails) {
                        tvLogin.setText(userDetails.getLogin());
                        tvFirstName.setText(userDetails.getFirstName());
                        tvLastName.setText(userDetails.getLastName());
                        tvEmail.setText(userDetails.getEmail());
                        tvYearOfBirth.setText(Integer.toString(userDetails.getYearOfBirth()));
                        tvCity.setText(userDetails.getCity());
                        tvRegistered.setText(Converter.dateToString(userDetails.getCreationDate()));
                    }
                });
    }

}
