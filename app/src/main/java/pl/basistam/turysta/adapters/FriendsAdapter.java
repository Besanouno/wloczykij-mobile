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

import java.util.ArrayList;
import java.util.List;

import pl.basistam.turysta.R;
import pl.basistam.turysta.dto.Relation;
import pl.basistam.turysta.service.interfaces.RelationsChangesHandler;

public class FriendsAdapter extends ArrayAdapter<Relation> {

    private final List<Relation> friends;
    private final RelationsChangesHandler relationsChangesHandler;
    private final Activity context;

    public FriendsAdapter(Activity context, List<Relation> friends, RelationsChangesHandler relationsChangesHandler) {
        super(context, R.layout.friend_item, friends);
        this.friends = friends;
        this.context = context;
        this.relationsChangesHandler = relationsChangesHandler;
    }

    private static class ViewHolder {
        TextView text;
        CheckBox checkBox;
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
                    Relation element = (Relation) viewHolder.checkBox.getTag();
                    element.setFriend(buttonView.isChecked());
                }
            });
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    relationsChangesHandler.registerChange((Relation) viewHolder.checkBox.getTag());
                }
            });
            view.setTag(viewHolder);
            viewHolder.checkBox.setTag(friends.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkBox.setTag(friends.get(position));
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.text.setText(friends.get(position).getFullName());
        viewHolder.checkBox.setChecked(friends.get(position).isFriend());
        return view;
    }
}
