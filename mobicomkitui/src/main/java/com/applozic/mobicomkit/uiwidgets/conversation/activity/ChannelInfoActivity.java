package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.feed.ChannelName;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.alphanumbericcolor.AlphaNumberColorUtil;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.commons.image.ImageLoader;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.people.channel.ChannelUtils;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sunil on 7/3/16.
 */
public class ChannelInfoActivity extends AppCompatActivity {

    private static final String TAG = "ChannelInfoActivity";
    private ActionBar mActionBar;
    private ImageLoader contactImageLoader;
    public static final String CHANNEL_KEY = "CHANNEL_KEY";
    private BaseContactService contactService;
    private List<ChannelUserMapper> channelUserMapperList;
    private Channel channel;
    private static final String SUCCESS= "success" ;
    private ImageView channelImage;
    public static final String USERID = "USERID";
    private TextView createdBy;
    protected ListView mainListView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    public static final String CHANNEL_NAME = "CHANNEL_NAME";
    protected ContactsAdapter contactsAdapter;
    private Button exitChannelButton, deleteChannelButton;
    private RelativeLayout channelDeleteRelativeLayout, channelExitRelativeLayout;
    private Integer channelKey;
    public static final int REQUEST_CODE_FOR_CONTACT = 1;
    public static final int REQUEST_CODE_FOR_CHANNEL_NEW_NAME = 2;
    boolean isUserPresent;
    Contact contact;
    BaseContactService baseContactService;
    MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_info_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        baseContactService = new AppContactService(this);
        channelImage = (ImageView) findViewById(R.id.channelImage);
        createdBy = (TextView) findViewById(R.id.created_by);
        exitChannelButton = (Button) findViewById(R.id.exit_channel);
        deleteChannelButton = (Button) findViewById(R.id.delete_channel_button);
        channelDeleteRelativeLayout = (RelativeLayout) findViewById(R.id.channel_delete_relativeLayout);
        channelExitRelativeLayout = (RelativeLayout) findViewById(R.id.channel_exit_relativeLayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mainListView = (ListView) findViewById(R.id.mainList);
        mainListView.setLongClickable(true);
        mainListView.setSmoothScrollbarEnabled(true);
        if (Utils.hasLollipop()) {
            mainListView.setNestedScrollingEnabled(true);
        }
        mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this);

        registerForContextMenu(mainListView);

        if (getIntent().getExtras() != null) {
            channelKey = getIntent().getIntExtra(CHANNEL_KEY, 0);
            channel = ChannelService.getInstance(this).getChannelByChannelKey(channelKey);
            isUserPresent = ChannelService.getInstance(this).processIsUserPresentInChannel(channelKey);
            if (channel != null) {
                String title = ChannelUtils.getChannelTitleName(channel, MobiComUserPreference.getInstance(getApplicationContext()).getUserId());
                contact = new AppContactService(this).getContactById(channel.getAdminKey());
                 mActionBar.setTitle(title);
                if(MobiComUserPreference.getInstance(this).getUserId().equals(contact.getUserId())){
                    createdBy.setText(getString(R.string.channel_created_by) + " " +getString(R.string.you_string));
                }else {
                    createdBy.setText(getString(R.string.channel_created_by) + " " + contact.getDisplayName());
                }
                if (!isUserPresent) {
                    channelExitRelativeLayout.setVisibility(View.GONE);
                    channelDeleteRelativeLayout.setVisibility(View.VISIBLE);
                }
            }
        }
        contactService = new AppContactService(this);
        contactImageLoader = new ImageLoader(this, getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return contactService.downloadContactImage(getApplicationContext(), (Contact) data);
            }
        };
        contactImageLoader.setLoadingImage(R.drawable.applozic_ic_contact_picture_holo_light);
        contactImageLoader.addImageCache(this.getSupportFragmentManager(), 0.1f);
        contactImageLoader.setImageFadeIn(false);
        channelUserMapperList = ChannelService.getInstance(this).getListOfUsersFromChannelUserMapper(channel.getKey());

        contactsAdapter = new ContactsAdapter(this);
        mainListView.setAdapter(contactsAdapter);

        mainListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause image loader to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    contactImageLoader.setPauseWork(true);
                } else {
                    contactImageLoader.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            }
        });
        exitChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveChannel(channel);
            }
        });

        deleteChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelService.getInstance(ChannelInfoActivity.this).processChannelDeleteConversation(channel.getKey(), ChannelInfoActivity.this);
                if(ConversationActivity.conversationActivity != null){
                    ConversationActivity.conversationActivity.finish();
                }
                Intent intent = new Intent(ChannelInfoActivity.this, ConversationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
        BroadcastService.currentInfoId = null;
        contactImageLoader.setPauseWork(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
        if (channel != null) {
            BroadcastService.currentInfoId = String.valueOf(channel.getKey());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        boolean isUserAlreadyPresent;
        if (data != null) {
            if (requestCode == REQUEST_CODE_FOR_CONTACT && resultCode == Activity.RESULT_OK) {
                isUserAlreadyPresent =  ChannelService.getInstance(this).isUserAlreadyPresentInChannel(channel.getKey(),data.getExtras().getString(USERID));
                if(!isUserAlreadyPresent){
                    addChannelUser(data.getExtras().getString(USERID), channel);
                }else {
                    Toast toast=  Toast.makeText(this, getString(R.string.user_is_already_exists), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
            if (requestCode == REQUEST_CODE_FOR_CHANNEL_NEW_NAME && resultCode == Activity.RESULT_OK) {
                ChannelName channelName = new ChannelName(data.getExtras().getString(ChannelNameActivity.CHANNEL_NAME), channel.getKey());
                new ChannelAsync(channelName, ChannelInfoActivity.this).execute();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        if (channelUserMapperList.size() <= position) {
            return true;
        }

        if (channel == null) {
            return true;
        }

        ChannelUserMapper channelUserMapper = channelUserMapperList.get(position);
        if (MobiComUserPreference.getInstance(this).getUserId().equals(channelUserMapper.getUserKey())) {
            return true;
        }
        switch (item.getItemId()) {
            case 0:
                removeChannelUser(channel, channelUserMapper);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_menu_option, menu);
        if (!ChannelUtils.isAdminUserId(MobiComUserPreference.getInstance(ChannelInfoActivity.this).getUserId(), channel)) {
            menu.removeItem(R.id.add_member_to_channel);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int positionInList = info.position;
        if (positionInList < 0 || channelUserMapperList.isEmpty()) {
            return;
        }
        ChannelUserMapper channelUserMapper = channelUserMapperList.get(positionInList);
        if (ChannelUtils.isAdminUserId(MobiComUserPreference.getInstance(ChannelInfoActivity.this).getUserId(), channel)) {
            isUserPresent = ChannelService.getInstance(this).processIsUserPresentInChannel(channelKey);
            if (!ChannelUtils.isAdminUserId(channelUserMapper.getUserKey(), channel)  &&  isUserPresent ) {
                menu.add(Menu.NONE, Menu.NONE, 0, "Remove");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        boolean isUserPresent = false;
        if (channel != null) {
            isUserPresent = ChannelService.getInstance(this).processIsUserPresentInChannel(channel.getKey());
        }
        if (id == R.id.add_member_to_channel) {
            if (isUserPresent) {
                Intent addMemberIntent = new Intent(ChannelInfoActivity.this, ContactSelectionActivity.class);
                addMemberIntent.putExtra(ContactSelectionActivity.CHECK_BOX, true);
                addMemberIntent.putExtra(ContactSelectionActivity.CHANNEL_OBJECT, channel);
                startActivityForResult(addMemberIntent, REQUEST_CODE_FOR_CONTACT);
            } else {
                Toast.makeText(this, getString(R.string.channel_add_alert), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.edit_channel_name) {
            if (isUserPresent) {
                Intent editChannelNameIntent = new Intent(ChannelInfoActivity.this, ChannelNameActivity.class);
                editChannelNameIntent.putExtra(ChannelNameActivity.CHANNEL_NAME, channel.getName());
                startActivityForResult(editChannelNameIntent, REQUEST_CODE_FOR_CHANNEL_NEW_NAME);
            } else {
                Toast.makeText(this, getString(R.string.channel_edit_alert), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }


    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);
        final DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) typedValue.getDimension(metrics);
    }


    public void updateChannelList() {
        if (contactsAdapter != null && channel != null) {
            channelUserMapperList.clear();
            channelUserMapperList = ChannelService.getInstance(this).getListOfUsersFromChannelUserMapper(channel.getKey());
            contactsAdapter.notifyDataSetChanged();
            String oldChannelName = channel.getName();
            channel = ChannelService.getInstance(this).getChannelByChannelKey(channel.getKey());
            if(!oldChannelName.equals(channel.getName())){
              mActionBar.setTitle(channel.getName());
                collapsingToolbarLayout.setTitle(channel.getName());
            }
        }
    }


    private class ContactsAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        Context context;

        public ContactsAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String contactNumber;
            char firstLetter;
            ContactViewHolder holder;
            ChannelUserMapper channelUserMapper = channelUserMapperList.get(position);
            Contact contact = contactService.getContactById(channelUserMapper.getUserKey());
            if (convertView == null) {
                convertView =
                        mInflater.inflate(R.layout.contact_users_layout, parent, false);
                holder = new ContactViewHolder();
                holder.displayName = (TextView) convertView.findViewById(R.id.displayName);
                holder.alphabeticImage = (TextView) convertView.findViewById(R.id.alphabeticImage);
                holder.circleImageView = (CircleImageView) convertView.findViewById(R.id.contactImage);
                holder.adminTextView = (TextView) convertView.findViewById(R.id.adminTextView);
                holder.lastSeenAtTextView = (TextView) convertView.findViewById(R.id.lastSeenAtTextView);
                convertView.setTag(holder);
            } else {
                holder = (ContactViewHolder) convertView.getTag();
            }
            if(MobiComUserPreference.getInstance(context).getUserId().equals(contact.getUserId())){
                holder.displayName.setText(getString(R.string.you_string));
            }else {
                holder.displayName.setText(contact.getDisplayName());
            }
            if (ChannelUtils.isAdminUserId(contact.getUserId(), channel)) {
                holder.adminTextView.setVisibility(View.VISIBLE);
            } else {
                holder.adminTextView.setVisibility(View.GONE);
            }
            if (contact.getLastSeenAt() != 0) {
                if(!MobiComUserPreference.getInstance(context).getUserId().equals(contact.getUserId())){
                    holder.lastSeenAtTextView.setVisibility(View.VISIBLE);
                    holder.lastSeenAtTextView.setText(getString(R.string.subtitle_last_seen_at_time) + " " + String.valueOf(DateUtils.getDateAndTimeForLastSeen(contact.getLastSeenAt())));
                }else {
                    holder.lastSeenAtTextView.setVisibility(View.GONE);
                    holder.lastSeenAtTextView.setText("");
                }
            } else {
                holder.lastSeenAtTextView.setVisibility(View.GONE);
                holder.lastSeenAtTextView.setText("");
            }

            if (contact != null && !TextUtils.isEmpty(contact.getDisplayName())) {
                contactNumber = contact.getContactNumber().toUpperCase();
                firstLetter = contact.getDisplayName().toUpperCase().charAt(0);
                if (firstLetter != '+') {
                    holder.alphabeticImage.setText(String.valueOf(firstLetter));
                } else if (contactNumber.length() >= 2) {
                    holder.alphabeticImage.setText(String.valueOf(contactNumber.charAt(1)));
                }
                Character colorKey = AlphaNumberColorUtil.alphabetBackgroundColorMap.containsKey(firstLetter) ? firstLetter : null;
                GradientDrawable bgShape = (GradientDrawable) holder.alphabeticImage.getBackground();
                bgShape.setColor(context.getResources().getColor(AlphaNumberColorUtil.alphabetBackgroundColorMap.get(colorKey)));
            }
            holder.alphabeticImage.setVisibility(View.GONE);
            holder.circleImageView.setVisibility(View.VISIBLE);
            if(contact != null){
                if (contact.isDrawableResources()) {
                    int drawableResourceId = context.getResources().getIdentifier(contact.getrDrawableName(), "drawable", context.getPackageName());
                    holder.circleImageView.setImageResource(drawableResourceId);
                } else {
                    contactImageLoader.loadImage(contact, holder.circleImageView, holder.alphabeticImage);
                }
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return channelUserMapperList.size();
        }

        @Override
        public Object getItem(int position) {
            return channelUserMapperList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }

    public class ChannelMember extends AsyncTask<Void, Integer, Long> {
        private ChannelUserMapper channelUserMapper;
        private ChannelService channelService;
        private ProgressDialog progressDialog;
        private Context context;
        private Channel channel;
        String responseForRemove;


        public ChannelMember(ChannelUserMapper channelUserMapper, Channel channel, Context context) {
            this.channelUserMapper = channelUserMapper;
            this.channel = channel;
            this.context = context;
            this.channelService = ChannelService.getInstance(context);

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",
                    context.getString(R.string.removing_channel_user), true);
        }

        @Override
        protected Long doInBackground(Void... params) {
            if (channel != null && channelUserMapper != null) {
                responseForRemove = channelService.removeMemberFromChannelProcess(channel.getKey(), channelUserMapper.getUserKey());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(!Utils.isInternetAvailable(context)){
                Toast toast=  Toast.makeText(context, getString(R.string.you_dont_have_any_network_access_info), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (SUCCESS.equals(responseForRemove) && contactsAdapter != null) {
                if (channelUserMapperList != null && channelUserMapperList.size() > 0) {
                    channelUserMapperList.remove(channelUserMapper);
                    contactsAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    public void removeChannelUser(final Channel channel, final ChannelUserMapper channelUserMapper) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).
                setPositiveButton(R.string.remove_member, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ChannelMember(channelUserMapper, channel, ChannelInfoActivity.this).execute();

                    }
                });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        String name = "";
        String channelName = "";
        Contact contact;
        if (!TextUtils.isEmpty(channelUserMapper.getUserKey())) {
            contact = baseContactService.getContactById(channelUserMapper.getUserKey());
            name = contact.getDisplayName();
            channelName = channel.getName();
        }

        alertDialog.setMessage(getString(R.string.dialog_remove_group_user).replace(getString(R.string.user_name_info), name).replace(getString(R.string.group_name_info), channelName));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }


    private class ContactViewHolder {
        public TextView displayName, alphabeticImage, adminTextView, lastSeenAtTextView;
        public CircleImageView circleImageView;

        public ContactViewHolder() {
        }

        public ContactViewHolder(TextView displayName, TextView alphabeticImage, TextView adminTextView, TextView lastSeenAtTextView, CircleImageView circleImageView) {
            this.displayName = displayName;
            this.alphabeticImage = alphabeticImage;
            this.adminTextView = adminTextView;
            this.lastSeenAtTextView = lastSeenAtTextView;
            this.circleImageView = circleImageView;
        }

        public TextView getDisplayName() {
            return displayName;
        }

        public void setDisplayName(TextView displayName) {
            this.displayName = displayName;
        }

        public TextView getAlphabeticImage() {
            return alphabeticImage;
        }

        public void setAlphabeticImage(TextView alphabeticImage) {
            this.alphabeticImage = alphabeticImage;
        }

        public TextView getAdminTextView() {
            return adminTextView;
        }

        public void setAdminTextView(TextView adminTextView) {
            this.adminTextView = adminTextView;
        }

        public CircleImageView getCircleImageView() {
            return circleImageView;
        }

        public void setCircleImageView(CircleImageView circleImageView) {
            this.circleImageView = circleImageView;
        }

        public TextView getLastSeenAtTextView() {
            return lastSeenAtTextView;
        }

        public void setLastSeenAtTextView(TextView lastSeenAtTextView) {
            this.lastSeenAtTextView = lastSeenAtTextView;
        }
    }


    public class ChannelMemberAdd extends AsyncTask<Void, Integer, Long> {
        private ChannelService channelService;
        private ProgressDialog progressDialog;
        private Context context;
        private Channel channel;
        String responseForAdd;
        String userId;


        public ChannelMemberAdd(Channel channel, String userId, Context context) {
            this.channel = channel;
            this.context = context;
            this.userId = userId;
            this.channelService = ChannelService.getInstance(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",
                    context.getString(R.string.adding_channel_user), true);
        }

        @Override
        protected Long doInBackground(Void... params) {
            if (channel != null && !TextUtils.isEmpty(userId)) {
                responseForAdd = channelService.addMemberToChannelProcess(channel.getKey(), userId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(!Utils.isInternetAvailable(context)){
                Toast toast=  Toast.makeText(context, getString(R.string.you_dont_have_any_network_access_info), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (!TextUtils.isEmpty(responseForAdd) && SUCCESS.equals(responseForAdd)) {
                ChannelUserMapper channelUserMapper = new ChannelUserMapper(channel.getKey(), userId);
                channelUserMapperList.add(channelUserMapper);
                contactsAdapter.notifyDataSetChanged();
            }
        }

    }

    public void addChannelUser(final String userId, final Channel channel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).
                setPositiveButton(R.string.add_member, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ChannelMemberAdd(channel, userId, ChannelInfoActivity.this).execute();

                    }
                });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        String name = "";
        String channelName = "";
        Contact contact ;
        if (channel != null) {
            contact = baseContactService.getContactById(userId);
            name = contact.getDisplayName();
            channelName = channel.getName();
        }
        alertDialog.setMessage(getString(R.string.dialog_add_group_user).replace(getString(R.string.user_name_info), name).replace(getString(R.string.group_name_info), channelName));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }


    public void leaveChannel(final Channel channel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this).
                setPositiveButton(R.string.channel_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new ChannelAsync(channel, ChannelInfoActivity.this).execute();
                    }
                });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.setMessage(getString(R.string.leave_channel));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }


    public class ChannelAsync extends AsyncTask<Void, Integer, Long> {
        private ChannelService channelService;
        private ProgressDialog progressDialog;
        private Context context;
        private Channel channel;
        ChannelName channelName;
        String responseForExit;
        String responseForUpdateChannelName;

        public ChannelAsync(Channel channel, Context context) {
            this.channel = channel;
            this.context = context;
            this.channelService = ChannelService.getInstance(context);

        }

        public ChannelAsync(ChannelName channelName, Context context) {
            this.channelName = channelName;
            this.context = context;
            this.channelService = ChannelService.getInstance(context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (channelName != null) {
                progressDialog = ProgressDialog.show(context, "",
                        context.getString(R.string.channel_update), true);
            }
            if (channel != null) {
                progressDialog = ProgressDialog.show(context, "",
                        context.getString(R.string.channel_member_exit), true);
            }

        }

        @Override
        protected Long doInBackground(Void... params) {
            if (channelName != null) {
                responseForUpdateChannelName = channelService.updateNewChannelNameProcess(channelName);
            }
            if (channel != null) {
                responseForExit = channelService.leaveMemberFromChannelProcess(channel.getKey(),MobiComUserPreference.getInstance(context).getUserId());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(channel != null && !Utils.isInternetAvailable(context)){
                Toast toast=  Toast.makeText(context, getString(R.string.failed_to_leave_group), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if(channelName != null && !Utils.isInternetAvailable(context)){
                Toast toast=  Toast.makeText(context, getString(R.string.internet_connection_for_group_name_info), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (!TextUtils.isEmpty(responseForExit) && SUCCESS.equals(responseForExit)) {
                ChannelInfoActivity.this.finish();
            }
            if (!TextUtils.isEmpty(responseForUpdateChannelName) && SUCCESS.equals(responseForUpdateChannelName)) {
                mActionBar.setTitle(channelName.getNewName());
                collapsingToolbarLayout.setTitle(channelName.getNewName());
            }
        }
    }

}