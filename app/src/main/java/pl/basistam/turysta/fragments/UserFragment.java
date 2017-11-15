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
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import pl.basistam.turysta.R;
import pl.basistam.turysta.auth.AccountGeneral;
import pl.basistam.turysta.auth.LoggedUser;
import pl.basistam.turysta.dto.UserInput;
import pl.basistam.turysta.dto.UserDetails;
import pl.basistam.turysta.service.UserService;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        final EditText edtFirstName = view.findViewById(R.id.first_name);
        final EditText edtLastName = view.findViewById(R.id.last_name);
        final EditText edtCity = view.findViewById(R.id.city);
        final EditText edtYearOfBirth = view.findViewById(R.id.year_of_birth);

        AccountManager accountManager = AccountManager.get(getActivity().getBaseContext());
        accountManager.getAuthToken(LoggedUser.getInstance().getAccount(), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, true,
                new AccountManagerCallback<Bundle>() {
            @Override
            public void run(final AccountManagerFuture<Bundle> future) {
                new AsyncTask<Void, Void, UserDetails>() {
                    @Override
                    protected UserDetails doInBackground(Void... params) {
                        try {
                            final String authToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                            return UserService.getInstance()
                                    .userService()
                                    .getUserDetails("Bearer " + authToken)
                                    .execute()
                                    .body();
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(UserDetails userDetails) {
                        edtFirstName.setText(userDetails.getFirstName());
                        edtLastName.setText(userDetails.getLastName());
                        edtCity.setText(userDetails.getCity());
                        edtYearOfBirth.setText(Integer.toString(userDetails.getYearOfBirth()));
                    }
                }.execute();
            }
        }, null);

        final FloatingActionButton btnEdit = view.findViewById(R.id.btn_edit);
        final FloatingActionButton btnSave = view.findViewById(R.id.btn_save);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                edtFirstName.setEnabled(true);
                edtLastName.setEnabled(true);
                edtCity.setEnabled(true);
                edtYearOfBirth.setEnabled(true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                edtFirstName.setEnabled(false);
                edtLastName.setEnabled(false);
                edtCity.setEnabled(false);
                edtYearOfBirth.setEnabled(false);

                UserInput inputJson = new UserInput();
                inputJson.setFirstName(edtFirstName.getText().toString());
                inputJson.setLastName(edtLastName.getText().toString());
                inputJson.setCity(edtCity.getText().toString());
                inputJson.setYearOfBirth(Integer.parseInt(edtYearOfBirth.getText().toString()));
            }
        });

        final FloatingActionButton btnFriends = view.findViewById(R.id.btn_friends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsFragment friendsFragment = new FriendsFragment();
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.content, friendsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
