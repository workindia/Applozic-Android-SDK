package com.applozic.mobicomkit.uiwidgets.conversation.mentions;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applozic.mobicomkit.uiwidgets.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Default adapter for displaying mention in {@link MentionAutoCompleteTextView}.
 * Note that this adapter is completely optional, any adapter extending
 * {@link android.widget.ArrayAdapter} can be attached to {@link MentionAutoCompleteTextView}.
 */
public class MentionAdapter extends ArrayAdapter<Mention> {
    private int defaultAvatar;

    public MentionAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public MentionAdapter(@NonNull Context context, @DrawableRes int defaultAvatar) {
        this(context, R.layout.layout_mention_item, R.id.mention_user_id);
        this.defaultAvatar = defaultAvatar;
    }

    public MentionAdapter(@NonNull Context context) {
        this(context, R.drawable.applozic_video_default_thumbnail);
    }

    private Filter filter;
    private final List<Mention> tempItems = new ArrayList<>();

    @Override
    public void add(@Nullable Mention object) {
        super.add(object);
        tempItems.add(object);
    }

    @Override
    public void addAll(@NonNull Collection<? extends Mention> collection) {
        super.addAll(collection);
        tempItems.addAll(collection);
    }

    @Override
    public final void addAll(Mention... items) {
        super.addAll(items);
        Collections.addAll(tempItems, items);
    }

    @Override
    public void remove(@Nullable Mention object) {
        super.remove(object);
        tempItems.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
        tempItems.clear();
    }

    @NonNull
    public CharSequence convertToString(Mention object) {
        return object.toString();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MentionFilter();
        }
        return filter;
    }

    @SuppressWarnings("unchecked")
    private class MentionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            final FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(constraint)) {
                results.values = tempItems;
                results.count = tempItems.size();
                return results;
            }
            final List<Mention> filteredItems = new ArrayList<>();
            for (final Mention item : tempItems) {
                if (convertResultToString(item)
                        .toString()
                        .toLowerCase(Locale.getDefault())
                        .contains(constraint.toString().toLowerCase(Locale.getDefault()))) {
                    filteredItems.add(item);
                }
            }
            results.values = filteredItems;
            results.count = filteredItems.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values instanceof List) {
                final List<Mention> list = (List<Mention>) results.values;
                if (results.count > 0) {
                    MentionAdapter.super.clear();
                    for (final Mention object : list) {
                        MentionAdapter.super.add(object);
                    }
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return convertToString((Mention) resultValue);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_mention_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Mention item = getItem(position);
        if (item != null) {
            holder.userIdView.setText(item.getUserId());

            final CharSequence displayname = item.getDisplayName();
            if (!TextUtils.isEmpty(displayname)) {
                holder.displayNameView.setText(displayname);
                holder.displayNameView.setVisibility(View.VISIBLE);
            } else {
                holder.displayNameView.setVisibility(View.GONE);
            }

            final Object avatar = item.getAvatar();
        }
        return convertView;
    }

    private static class ViewHolder {
        private final ImageView avatarView;
        private final ProgressBar loadingView;
        private final TextView userIdView;
        private final TextView displayNameView;

        ViewHolder(View itemView) {
            avatarView = itemView.findViewById(R.id.mention_profile_image);
            loadingView = itemView.findViewById(R.id.mention_loading_bar);
            userIdView = itemView.findViewById(R.id.mention_user_id);
            displayNameView = itemView.findViewById(R.id.mention_display_name);
        }
    }
}