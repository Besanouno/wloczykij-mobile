package pl.basistam.wloczykij.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import pl.basistam.wloczykij.R;
import pl.basistam.wloczykij.adapters.MessagesAdapter;
import pl.basistam.wloczykij.auth.LoggedUser;
import pl.basistam.wloczykij.dto.MessageDto;
import pl.basistam.wloczykij.dto.Page;
import pl.basistam.wloczykij.errors.ErrorMessages;
import pl.basistam.wloczykij.service.MessagesService;
import retrofit2.Response;

public class MessagesFragment extends Fragment {

    private MessagesAdapter adapter;
    private int page = 0;
    private final int SIZE = 15;
    private String eventGuid;
    private String eventName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        prepareArguments();
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(eventName);
        adapter = new MessagesAdapter(getActivity(), new ArrayList<MessageDto>());
        ListView listView = view.findViewById(R.id.lv_messages);
        listView.setAdapter(adapter);
        downloadAndShowMessages();
    }

    private void prepareArguments() {
        if (getArguments() != null) {
            eventGuid = getArguments().getString("eventGuid");
            eventName = getArguments().getString("eventName");
        }
    }

    private void downloadAndShowMessages() {
        LoggedUser.getInstance().sendAuthorizedRequest(getActivity().getBaseContext(),
                new AsyncTask<String, Void, Page<MessageDto>>() {

                    @Override
                    protected Page<MessageDto> doInBackground(String... params) {
                        final String authtoken = params[0];
                        return downloadPageAndIncreaseNumber(authtoken);
                    }

                    @Override
                    protected void onPostExecute(Page<MessageDto> users) {
                        if (users != null) {
                            adapter.addAll(users.getContent());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), ErrorMessages.OFFLINE_MODE, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private Page<MessageDto> downloadPageAndIncreaseNumber(String authToken) {
        try {
            Response<Page<MessageDto>> response = MessagesService.getInstance()
                    .messageService()
                    .getPage(authToken, eventGuid, page++, SIZE)
                    .execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
