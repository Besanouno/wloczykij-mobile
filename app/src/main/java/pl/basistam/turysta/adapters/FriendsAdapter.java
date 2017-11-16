package pl.basistam.turysta.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.items.FriendItem;

public class FriendsAdapter extends ArrayAdapter<FriendItem> {

    private final List<FriendItem> friends;
    private final Activity context;

    public FriendsAdapter(Activity context, List<FriendItem> friends) {
        super(context, R.layout.friend_item, friends);
        this.friends = friends;
        this.context = context;
    }

    static class ViewHolder {
        protected TextView text;
        protected CheckBox checkBox;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.friend_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = view.findViewById(R.id.label);
            viewHolder.checkBox = view.findViewById(R.id.check);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FriendItem element = (FriendItem) viewHolder.checkBox.getTag();
                    element.setFriend(buttonView.isChecked());
                }
            });
            view.setTag(viewHolder);
            viewHolder.checkBox.setTag(friends.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkBox.setTag(friends.get(position));
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.text.setText(friends.get(position).getName());
        viewHolder.checkBox.setChecked(friends.get(position).isFriend());
        return view;
    }
}
