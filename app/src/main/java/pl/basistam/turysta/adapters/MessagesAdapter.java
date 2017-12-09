package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.MessageDto;

public class MessagesAdapter extends BaseAdapter implements Serializable {

    private List<MessageDto> messages;
    private LayoutInflater inflater;

    public MessagesAdapter(Activity activity, List<MessageDto> messages) {
        this.messages = messages;
        this.inflater = activity.getLayoutInflater();
    }

    public void addAll(List<MessageDto> messages) {
        this.messages.addAll(messages);
    }

    public void clear() {
        this.messages.clear();
    }


    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Object getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, null);
        }

        MessageDto message = this.messages.get(position);

        TextView tvLogin = convertView.findViewById(R.id.tv_login);
        tvLogin.setText(message.getUserLogin());

        TextView tvDate = convertView.findViewById(R.id.tv_date);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tvDate.setText(format.format(message.getDate()));

        TextView tvContent = convertView.findViewById(R.id.tv_content);
        tvContent.setText(message.getContent());

        return convertView;
    }
}
