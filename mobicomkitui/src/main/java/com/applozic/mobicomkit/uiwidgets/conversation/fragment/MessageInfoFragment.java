package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.applozic.mobicomkit.api.attachment.AttachmentView;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageInfo;
import com.applozic.mobicomkit.api.conversation.MessageInfoResponse;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.alphanumbericcolor.AlphaNumberColorUtil;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.image.ImageLoader;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageInfoFragment extends Fragment  {

    public static final String MESSAGE_ARGUMENT_KEY= "MESSAGE";
    Message message=null;
    AttachmentView attachmentView;
    MessageInfoResponse messageInfoResponse;
    private ImageLoader contactImageLoader;
    private ListView readListView;
    private  ListView deliveredListView;


    public MessageInfoFragment() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        contactImageLoader = new ImageLoader(getContext(), getListPreferredItemHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                BaseContactService contactService = new AppContactService(getContext());
                return contactService.downloadContactImage(getContext(), (Contact) data);
            }
        };

        contactImageLoader.setLoadingImage(R.drawable.applozic_ic_contact_picture_holo_light);
        contactImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);

        View view = inflater.inflate(R.layout.applozic_message_info, container, false);
        Bundle bundle=getArguments();
        String messageJson=bundle.getString(MESSAGE_ARGUMENT_KEY);
        message = (Message) GsonUtils.getObjectFromJson(messageJson, Message.class);

        AttachmentView attachmentView = (AttachmentView) view.findViewById(R.id.applozic_message_info_attachmentview);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.applozic_message_info_progress_bar);
        attachmentView.setProressBar(progressBar);
        attachmentView.setVisibility(message.hasAttachment() ? View.VISIBLE : View.GONE);

        TextView textView = (TextView) view.findViewById(R.id.applozic_message_info_message_text);
        readListView = (ListView)view.findViewById(R.id.applozic_message_info_read_list);
        deliveredListView =  (ListView)view.findViewById(R.id.applozic_message_info_delivered_list_view);

        attachmentView.setMessage(message);
        textView.setText(message.getMessage());
        //Starting a new Thread to get the Data ...

        new MessageInfoAsyncTask(message.getKeyString()).execute();

        return view;
    }


    public class MessageInfoAsyncTask extends AsyncTask<Void, Integer, Long> {

        String messageKey;
        public MessageInfoAsyncTask(String messageKey) {
          this.messageKey = messageKey;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Long doInBackground(Void... params) {
            Context context = getActivity();
            MobiComMessageService messageService =  new MobiComMessageService(context, MessageIntentService.class);
            messageInfoResponse = messageService.getMessageInfoResponse(messageKey);
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            //Populating view....
            if (messageInfoResponse.getReadByUserList()!=null){
                ContactsAdapter readAdapter = new ContactsAdapter(messageInfoResponse.getReadByUserList());
                readListView.setAdapter(readAdapter);
            }


            if (messageInfoResponse.getDeliverdToUserList()!=null){
                ContactsAdapter deliveredAdapter = new ContactsAdapter(messageInfoResponse.getDeliverdToUserList());
                deliveredListView.setAdapter(deliveredAdapter);
            }


        }

    }

    //Contact Adapter
    private class ContactsAdapter extends BaseAdapter {

        List<MessageInfo> messageInfoList;
        private LayoutInflater mInflater;
        BaseContactService contactService;


        public ContactsAdapter(){

        }

        public ContactsAdapter(List<MessageInfo> messageInfoList){

            this.messageInfoList = messageInfoList;
            this.contactService =  new AppContactService(getContext());
            mInflater = LayoutInflater.from(getContext());


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String contactNumber;
            char firstLetter;
            ContactViewHolder holder;

            MessageInfo messageInfo =  messageInfoList.get(position);
            Contact contact = contactService.getContactById(messageInfo.getUserId());
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

            holder.displayName.setText(contact.getDisplayName());

            long timeStamp = messageInfo.isRead() ? messageInfo.getReadAtTime() :
                    ( messageInfo.getDeliveredAtTime()==null ? 0 : messageInfo.getDeliveredAtTime());
            if (timeStamp !=0 ) {

                holder.lastSeenAtTextView.setVisibility(View.VISIBLE);
                holder.lastSeenAtTextView.setText(String.valueOf(DateUtils.getDateAndTimeForLastSeen(timeStamp)));

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
                bgShape.setColor(getContext().getResources().getColor(AlphaNumberColorUtil.alphabetBackgroundColorMap.get(colorKey)));
            }

            if (contact.isDrawableResources()) {
                int drawableResourceId = getContext().getResources().getIdentifier(contact.getrDrawableName(), "drawable", getContext().getPackageName());
                holder.circleImageView.setImageResource(drawableResourceId);
            } else {
                contactImageLoader.loadImage(contact, holder.circleImageView, holder.alphabeticImage);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            return messageInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return messageInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

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

    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        getActivity().getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);
        final DisplayMetrics metrics = new DisplayMetrics();

        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) typedValue.getDimension(metrics);
    }

}
