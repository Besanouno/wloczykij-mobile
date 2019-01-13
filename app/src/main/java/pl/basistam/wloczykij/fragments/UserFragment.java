package pl.basistam.wloczykij.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.dto.UserDto;
import pl.basistam.wloczykij.dto.UserInputDto;
import pl.basistam.wloczykij.errors.ErrorMessages;
import pl.basistam.wloczykij.service.UserService;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtCity;
    private EditText edtYearOfBirth;
    private FloatingActionButton btnEdit;
    private FloatingActionButton btnSave;
    private FloatingActionButton btnFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadForm(view);
        downloadDataAndFillForm();
    }

    private void downloadDataAndFillForm() {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, UserDto>() {
                    @Override
                    protected UserDto doInBackground(String... params) {
                        String authToken = params[0];
                        return downloadUserDetails(authToken);
                    }

                    @Override
                    protected void onPostExecute(UserDto userDto) {
                        if (userDto == null) {
                            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.OFFLINE_MODE, Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStack();
                        } else {
                            updateFormFields(userDto);
                        }
                    }
                });
    }

    @Nullable
    private UserDto downloadUserDetails(String authToken) {
        try {
            return UserService.getInstance()
                    .userService()
                    .getUserDetails(authToken)
                    .execute()
                    .body();
        } catch (IOException e) {
            return null;
        }
    }


    private void loadForm(View view) {
        edtFirstName = view.findViewById(R.id.edt_first_name);
        edtLastName = view.findViewById(R.id.edt_last_name);
        edtCity = view.findViewById(R.id.edt_city);
        edtYearOfBirth = view.findViewById(R.id.edt_year_of_birth);

        btnEdit = view.findViewById(R.id.btn_edit);
        btnSave = view.findViewById(R.id.btn_save);
        btnFriends = view.findViewById(R.id.btn_relations);
        initEditAction();
        initSaveAction();
        initFriendsAction();
    }


    private void initEditAction() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEdit.setVisibility(View.GONE);
                btnSave.setVisibility(View.VISIBLE);
                setFieldsEnabled(true);
            }
        });
    }

    private void initSaveAction() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Zapisz dane")
                        .setMessage("Na pewno chcesz zapisać dane?")
                        .setPositiveButton("tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnSave.setVisibility(View.GONE);
                                btnEdit.setVisibility(View.VISIBLE);
                                setFieldsEnabled(false);
                                saveUser();
                            }
                        })
                        .setNegativeButton("nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_ask)
                        .show();
            }
        });
    }

    private void saveUser() {
        final UserInputDto user = prepareUserData();
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            final String authToken = params[0];
                            Response<Void> response = UserService.getInstance()
                                    .userService()
                                    .update(authToken, user)
                                    .execute();
                            if (response.isSuccessful()) {
                                Snackbar.make(getView(), "Poprawnie zapisano dane", Snackbar.LENGTH_SHORT);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
    }

    private void setFieldsEnabled(boolean enabled) {
        edtFirstName.setEnabled(enabled);
        edtLastName.setEnabled(enabled);
        edtCity.setEnabled(enabled);
        edtYearOfBirth.setEnabled(enabled);
    }

    private UserInputDto prepareUserData() {
        final UserInputDto userDto = new UserInputDto();
        userDto.setFirstName(edtFirstName.getText().toString());
        userDto.setLastName(edtLastName.getText().toString());
        userDto.setCity(edtCity.getText().toString());
        userDto.setYearOfBirth(Integer.parseInt(edtYearOfBirth.getText().toString()));
        return userDto;
    }

    private void initFriendsAction() {
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.content, new RelationsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void updateFormFields(UserDto userDto) {
        edtFirstName.setText(userDto.getFirstName());
        edtLastName.setText(userDto.getLastName());
        edtCity.setText(userDto.getCity());
        edtYearOfBirth.setText(Integer.toString(userDto.getYearOfBirth()));
    }
}
