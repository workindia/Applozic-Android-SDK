package com.applozic.mobicomkit.uiwidgets.conversation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.attachment.FileMeta;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.contact.MobiComVCFParser;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelInfoActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity;
import com.applozic.mobicommons.commons.core.utils.LocationInfo;
import com.applozic.mobicommons.commons.core.utils.Support;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.commons.image.ImageUtils;
import com.applozic.mobicommons.file.FilePathFinder;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUtils;
import com.applozic.mobicommons.people.contact.Contact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ConversationUIService {

    public static final int REQUEST_CODE_CONTACT_GROUP_SELECTION = 101;
    public static final String CONVERSATION_FRAGMENT = "ConversationFragment";
    public static final String QUICK_CONVERSATION_FRAGMENT = "QuickConversationFragment";
    public static final String DISPLAY_NAME = "displayName";
    public static final String USER_ID = "userId";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String FIRST_TIME_MTEXTER_FRIEND = "firstTimeMTexterFriend";
    public static final String CONTACT_ID = "contactId";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String APPLICATION_ID = "applicationId";
    public static final String DEFAULT_TEXT = "defaultText";
    public static final String FINAL_PRICE_TEXT = "Final agreed price ";
    public static final String PRODUCT_TOPIC_ID = "topicId";
    public static final String PRODUCT_IMAGE_URL = "productImageUrl";
    private static final String TAG = "ConversationUIService";
    private static final String APPLICATION_KEY_META_DATA = "com.applozic.application.key";
    public static final String GROUP = "group-";
    private FragmentActivity fragmentActivity;
    private BaseContactService baseContactService;

    public ConversationUIService(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        this.baseContactService = new AppContactService(fragmentActivity);
    }

    public MobiComQuickConversationFragment getQuickConversationFragment() {

        MobiComQuickConversationFragment quickConversationFragment = (MobiComQuickConversationFragment) UIService.getFragmentByTag(fragmentActivity, QUICK_CONVERSATION_FRAGMENT);

        if (quickConversationFragment == null) {
            quickConversationFragment = new MobiComQuickConversationFragment();
            ConversationActivity.addFragment(fragmentActivity, quickConversationFragment, QUICK_CONVERSATION_FRAGMENT);
        }
        return quickConversationFragment;
    }

    public ConversationFragment getConversationFragment() {

        ConversationFragment conversationFragment = (ConversationFragment) UIService.getFragmentByTag(fragmentActivity, CONVERSATION_FRAGMENT);

        if (conversationFragment == null) {
            Contact contact = ((ConversationActivity) fragmentActivity).getContact();
            Channel channel = ((ConversationActivity) fragmentActivity).getChannel();
            conversationFragment = new ConversationFragment(contact, channel);
            ConversationActivity.addFragment(fragmentActivity, conversationFragment, CONVERSATION_FRAGMENT);
        }
        return conversationFragment;
    }

    public void onQuickConversationFragmentItemClick(View view, Contact contact) {
        TextView textView = (TextView) view.findViewById(R.id.unreadSmsCount);
        textView.setVisibility(View.GONE);
        openConversationFragment(contact);
    }

    public void openConversationFragment(final Contact contact) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ConversationFragment conversationFragment = (ConversationFragment) UIService.getFragmentByTag(fragmentActivity, CONVERSATION_FRAGMENT);
                if (conversationFragment == null) {
                    conversationFragment = new ConversationFragment(contact, null);
                    ((MobiComKitActivityInterface) fragmentActivity).addFragment(conversationFragment);
                } else {
                    conversationFragment.loadConversation(contact);
                }
            }
        });
    }

    public void openConversationFragment(final Channel channel) {
        ConversationFragment conversationFragment = (ConversationFragment) UIService.getFragmentByTag(fragmentActivity, CONVERSATION_FRAGMENT);
        if (conversationFragment == null) {
            conversationFragment = new ConversationFragment(null, channel);
            ((MobiComKitActivityInterface) fragmentActivity).addFragment(conversationFragment);
        } else {
            conversationFragment.loadConversation(channel);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if ((requestCode == MultimediaOptionFragment.REQUEST_CODE_ATTACH_PHOTO ||
                    requestCode == MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO)
                    && resultCode == Activity.RESULT_OK) {
                Uri selectedFileUri = (intent == null ? null : intent.getData());
                if (selectedFileUri == null) {

                    selectedFileUri = ((ConversationActivity) fragmentActivity).getCapturedImageUri();
                    ImageUtils.addImageToGallery(FilePathFinder.getPath(fragmentActivity, selectedFileUri), fragmentActivity);
                }

                if (selectedFileUri == null) {
                    Bitmap photo = (Bitmap) (intent != null ? intent.getExtras().get("data") : null);
                    selectedFileUri = ImageUtils.getImageUri(fragmentActivity, photo);
                }
                getConversationFragment().loadFile(selectedFileUri);
                Log.i(TAG, "File uri: " + selectedFileUri);
            }

            if (requestCode == REQUEST_CODE_CONTACT_GROUP_SELECTION && resultCode == Activity.RESULT_OK) {
                checkForStartNewConversation(intent);
            }
            if (requestCode == MultimediaOptionFragment.REQUEST_CODE_CAPTURE_VIDEO_ACTIVITY && resultCode == Activity.RESULT_OK) {

                Uri selectedFilePath = ((ConversationActivity) fragmentActivity).getVideoFileUri();
                if (selectedFilePath != null) {
                    getConversationFragment().loadFile(selectedFilePath);
                }
                getConversationFragment().sendMessage("", Message.ContentType.VIDEO_MSG.getValue());
            }

            if (requestCode == MultimediaOptionFragment.REQUEST_CODE_CONTACT_SHARE && resultCode == Activity.RESULT_OK) {

                try {
                    File vCradFile = vCard(intent);

                    if (vCradFile != null) {
                        getConversationFragment().loadFile(Uri.fromFile(vCradFile));
                        getConversationFragment().sendMessage("", Message.ContentType.CONTACT_MSG.getValue());

                    }

                } catch (Exception e) {
                    Toast.makeText(fragmentActivity, "Failed to load Contact: ", Toast.LENGTH_SHORT).show();
                    Log.e("Exception::", "Exception", e);
                }
            }
            if (requestCode == MultimediaOptionFragment.REQUEST_MULTI_ATTCAHMENT && resultCode == Activity.RESULT_OK) {

                ArrayList<Uri> attachmentList = intent.getParcelableArrayListExtra(MobiComAttachmentSelectorActivity.MULTISELECT_SELECTED_FILES);
                String messageText = intent.getStringExtra(MobiComAttachmentSelectorActivity.MULTISELECT_MESSAGE);

                //TODO: check performance, we might need to put in each posting in separate thread.

                for (Uri info : attachmentList) {
                    getConversationFragment().loadFile(info);
                    getConversationFragment().sendMessage(messageText, Message.ContentType.ATTACHMENT.getValue());
                }

            }

            if (requestCode == MultimediaOptionFragment.REQUEST_CODE_SEND_LOCATION && resultCode == Activity.RESULT_OK) {
                Log.i("test", "posi");
                Double latitude = intent.getDoubleExtra("latitude", 0);
                Double longitude = intent.getDoubleExtra("longitude", 0);
                //TODO: put your location(lat/lon ) in constructor.
                LocationInfo info = new LocationInfo(latitude, longitude);
                String locationInfo = GsonUtils.getJsonFromObject(info, LocationInfo.class);
                sendLocation(locationInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteConversationThread(final Contact contact, final Channel channel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity).
                setPositiveButton(R.string.delete_conversation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteConversationAsyncTask(new MobiComConversationService(fragmentActivity), contact, channel, fragmentActivity).execute();

                    }
                });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        String name = "";
        if (channel != null) {
            name = ChannelUtils.getChannelTitleName(channel, MobiComUserPreference.getInstance(fragmentActivity).getUserId());
        } else {
            name = contact.getDisplayName();
        }
        alertDialog.setTitle(fragmentActivity.getString(R.string.dialog_delete_conversation_title).replace("[name]", name));
        alertDialog.setMessage(fragmentActivity.getString(R.string.dialog_delete_conversation_confir).replace("[name]", name));
        alertDialog.setCancelable(true);
        alertDialog.create().show();
    }

    public void updateLatestMessage(Message message, String formattedContactNumber) {
        if (!BroadcastService.isQuick()) {
            return;
        }
        getQuickConversationFragment().updateLatestMessage(message, formattedContactNumber);
    }

    public void removeConversation(Message message, String formattedContactNumber) {
        if (!BroadcastService.isQuick()) {
            return;
        }
        getQuickConversationFragment().removeConversation(message, formattedContactNumber);
    }

    public void addMessage(Message message) {
        if (!BroadcastService.isQuick()) {
            return;
        }

        MobiComQuickConversationFragment fragment = (MobiComQuickConversationFragment) UIService.getFragmentByTag(fragmentActivity, QUICK_CONVERSATION_FRAGMENT);
        if (fragment != null) {
            fragment.addMessage(message);
        }

    }

    public void updateLastMessage(String keyString, String userId) {
        if (!BroadcastService.isQuick()) {
            return;
        }
        getQuickConversationFragment().updateLastMessage(keyString, userId);
    }

    public boolean isBroadcastedToGroup(Integer channelKey) {
        if (!BroadcastService.isIndividual()) {
            return false;
        }
        return getConversationFragment().isBroadcastedToChannel(channelKey);
    }

    public void syncMessages(Message message, String keyString) {
        String userId = message.getContactIds();
        if (BroadcastService.isIndividual()) {
            ConversationFragment conversationFragment = getConversationFragment();
            if (!TextUtils.isEmpty(userId) && userId.equals(conversationFragment.getCurrentUserId()) ||
                    conversationFragment.isBroadcastedToChannel(message.getGroupId())) {
                conversationFragment.addMessage(message);
            }
        }

        if (message.getGroupId() == null) {
            updateLastMessage(keyString, userId);
        }
    }

    public void downloadConversations(boolean showInstruction) {
        if (!BroadcastService.isQuick()) {
            return;
        }
        getQuickConversationFragment().downloadConversations(showInstruction);
    }

    public void setLoadMore(boolean loadMore) {
        if (!BroadcastService.isQuick()) {
            return;
        }
        getQuickConversationFragment().setLoadMore(loadMore);
    }

    public void updateMessageKeyString(Message message) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        String userId = message.getContactIds();
        ConversationFragment conversationFragment = getConversationFragment();
        if (!TextUtils.isEmpty(userId) && conversationFragment.getContact() != null && userId.equals(conversationFragment.getContact().getUserId()) ||
                conversationFragment.isBroadcastedToChannel(message.getGroupId())) {
            conversationFragment.updateMessageKeyString(message);
        }
    }

    public void deleteMessage(Message message, String keyString, String formattedContactNumber) {
        if (PhoneNumberUtils.compare(formattedContactNumber, BroadcastService.currentUserId)) {
            getConversationFragment().deleteMessageFromDeviceList(keyString);
        } else {
            updateLastMessage(keyString, formattedContactNumber);
        }
    }

    public void updateLastSeenStatus(String contactId) {
        if (BroadcastService.isQuick()) {
            getQuickConversationFragment().updateLastSeenStatus(contactId);
            return;
        }
        ConversationFragment conversationFragment = getConversationFragment();
        if (conversationFragment.getContact() != null && contactId.equals(conversationFragment.getContact().getContactIds())) {
            conversationFragment.updateLastSeenStatus();
        }
    }

    public void updateDeliveryStatusForContact(String contactId) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        ConversationFragment conversationFragment = getConversationFragment();
        if (!TextUtils.isEmpty(contactId) && conversationFragment.getContact() != null && contactId.equals(conversationFragment.getContact().getContactIds())) {
            conversationFragment.updateDeliveryStatusForAllMessages();
        }
    }

    public void updateDeliveryStatus(Message message, String formattedContactNumber) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        ConversationFragment conversationFragment = getConversationFragment();
        if (conversationFragment.getContact() != null && formattedContactNumber.equals(conversationFragment.getContact().getContactIds()) || conversationFragment.getChannel() != null && message.getGroupId() != null && message.getGroupId().equals(conversationFragment.getChannel().getKey())) {
            conversationFragment.updateDeliveryStatus(message);
        }
    }

    public void deleteConversation(Contact contact, Integer channelKey, String response) {
        if (BroadcastService.isIndividual()) {
            getConversationFragment().clearList();
        }
        if (BroadcastService.isQuick()) {
            getQuickConversationFragment().removeConversation(contact, channelKey, response);
        }
    }

    public void updateUploadFailedStatus(Message message) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        getConversationFragment().updateUploadFailedStatus(message);
    }

    public void updateDownloadFailed(Message message) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        getConversationFragment().downloadFailed(message);
    }

    public void updateDownloadStatus(Message message) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        getConversationFragment().updateDownloadStatus(message);
    }

    public void updateName(Integer channelKey) {
        if (BroadcastService.isIndividual()) {
            return;
        }
        getQuickConversationFragment().updateUserName(channelKey);
    }

    public void updateTypingStatus(String userId, String isTypingStatus) {
        if (!BroadcastService.isIndividual()) {
            return;
        }
        ConversationFragment conversationFragment = getConversationFragment();
        Log.i(TAG, "Received typing status for: " + userId);
        if (conversationFragment.getContact() != null && userId.equals(conversationFragment.getContact().getContactIds())) {
            conversationFragment.updateUserTypingStatus(userId, isTypingStatus);
        }

    }

    public void updateChannelSync() {
        ((ChannelInfoActivity)fragmentActivity).updateChannelList();
    }

    public void startContactActivityForResult(Intent intent, Message message, String messageContent) {
        if (message != null) {
            intent.putExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE, GsonUtils.getJsonFromObject(message, message.getClass()));
        }
        if (messageContent != null) {
            intent.putExtra(MobiComKitPeopleActivity.SHARED_TEXT, messageContent);
        }

        fragmentActivity.startActivityForResult(intent, REQUEST_CODE_CONTACT_GROUP_SELECTION);
    }

    public void startContactActivityForResult() {
        startContactActivityForResult(null, null);
    }

    public void startContactActivityForResult(Message message, String messageContent) {

        //Todo: Change this to driver list fragment or activity
        Intent intent = new Intent(fragmentActivity, MobiComKitPeopleActivity.class);

        startContactActivityForResult(intent, message, messageContent);
    }

    public void sendPriceMessage() {

        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity);
            alertDialog.setTitle("Price");
            alertDialog.setMessage("Enter your amount");

            final EditText inputText = new EditText(fragmentActivity);
            inputText.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            inputText.setLayoutParams(linearParams);
            alertDialog.setView(inputText);

            alertDialog.setPositiveButton(fragmentActivity.getString(R.string.send_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!TextUtils.isEmpty(inputText.getText().toString())) {
                                getConversationFragment().sendMessage(inputText.getText().toString(), Message.ContentType.PRICE.getValue());
                            }
                        }
                    });

            alertDialog.setNegativeButton(fragmentActivity.getString(R.string.cancel_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            if (!fragmentActivity.isFinishing()) {
                alertDialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendAudioMessage(String selectedFilePath) {

        Log.i("ConversationUIService:", "Send audio message ...");

        if (selectedFilePath != null) {
            Uri uri = Uri.fromFile(new File(selectedFilePath));
            getConversationFragment().loadFile(uri);
        }
        getConversationFragment().sendMessage("", Message.ContentType.AUDIO_MSG.getValue());

    }

    public void checkForStartNewConversation(Intent intent) {
        Contact contact = null;
        Channel channel = null;

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    startContactActivityForResult(null, sharedText);
                }
            } else if (intent.getType().startsWith("image/")) {
                //Todo: use this for image forwarding
            }
        }

        final Uri uri = intent.getData();
        if (uri != null) {
            //Note: This is used only for the device contacts
            Long contactId = intent.getLongExtra(CONTACT_ID, 0);
            if (contactId == 0) {
                //Todo: show warning that the user doesn't have any number stored.
                return;
            }
            contact = baseContactService.getContactById(String.valueOf(contactId));
        }

        Integer channelKey = intent.getIntExtra(GROUP_ID, -1);
        String channelName = intent.getStringExtra(GROUP_NAME);

        if (channelKey != -1 && channelKey != null && channelKey != 0) {
            channel = ChannelService.getInstance(fragmentActivity).getChannel(channelKey);
        }

        if (channel != null && !TextUtils.isEmpty(channelName) && TextUtils.isEmpty(channel.getName())) {
            channel.setName(channelName);
            ChannelService.getInstance(fragmentActivity).updateChannel(channel);
        }

        String contactNumber = intent.getStringExtra(CONTACT_NUMBER);

        boolean firstTimeMTexterFriend = intent.getBooleanExtra(FIRST_TIME_MTEXTER_FRIEND, false);
        if (!TextUtils.isEmpty(contactNumber)) {
            contact = baseContactService.getContactById(contactNumber);
            if (BroadcastService.isIndividual()) {
                getConversationFragment().setFirstTimeMTexterFriend(firstTimeMTexterFriend);
            }
        }

        String userId = intent.getStringExtra(USER_ID);
        if (TextUtils.isEmpty(userId)) {
            userId = intent.getStringExtra("contactId");
        }

        if (!TextUtils.isEmpty(userId)) {
            contact = baseContactService.getContactById(userId);
        }

        String applicationId = intent.getStringExtra(APPLICATION_ID);
        if (contact != null) {
            contact.setApplicationId(applicationId);
            baseContactService.upsert(contact);
        }
        String fullName = intent.getStringExtra(DISPLAY_NAME);
        if (contact != null && TextUtils.isEmpty(contact.getFullName()) && !TextUtils.isEmpty(fullName)) {
            contact.setFullName(fullName);
            baseContactService.upsert(contact);
            new UserClientService(fragmentActivity).updateUserDisplayName(userId, fullName);
        }
        String messageJson = intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT);
        if (!TextUtils.isEmpty(messageJson)) {
            Message message = (Message) GsonUtils.getObjectFromJson(messageJson, Message.class);
            if (message.getGroupId() != null) {
                channel = ChannelService.getInstance(fragmentActivity).getChannelByChannelKey(message.getGroupId());
            } else {
                contact = baseContactService.getContactById(message.getContactIds());
            }
        }

        boolean support = intent.getBooleanExtra(Support.SUPPORT_INTENT_KEY, false);
        if (support) {
            contact = new Support(fragmentActivity).getSupportContact();
        }

        if (contact != null) {
            openConversationFragment(contact);
        }

        if (channel != null) {
            openConversationFragment(channel);
        }

        String forwardMessage = intent.getStringExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE);
        if (!TextUtils.isEmpty(forwardMessage)) {
            Message messageToForward = (Message) GsonUtils.getObjectFromJson(forwardMessage, Message.class);
            getConversationFragment().forwardMessage(messageToForward, contact);
        }

        String sharedText = intent.getStringExtra(MobiComKitPeopleActivity.SHARED_TEXT);
        if (!TextUtils.isEmpty(sharedText)) {
            getConversationFragment().sendMessage(sharedText);
        }

        String defaultText = intent.getStringExtra(ConversationUIService.DEFAULT_TEXT);
        if (!TextUtils.isEmpty(defaultText)) {
            getConversationFragment().setDefaultText(defaultText);
        }

        String productTopicId = intent.getStringExtra(ConversationUIService.PRODUCT_TOPIC_ID);
        String productImageUrl = intent.getStringExtra(ConversationUIService.PRODUCT_IMAGE_URL);
        if (!TextUtils.isEmpty(productTopicId) && !TextUtils.isEmpty(productImageUrl)) {
            try {
                FileMeta fileMeta = new FileMeta();
                fileMeta.setContentType("image");
                fileMeta.setBlobKeyString(productImageUrl);
                getConversationFragment().sendProductMessage(productTopicId, fileMeta, contact, Message.ContentType.TEXT_URL.getValue());
            } catch (Exception e) {
            }
        }

    }

    /**
     * @param data
     * @return
     */
    public File vCard(Intent data) throws Exception {
        Uri contactData = data.getData();

        Cursor cursor = fragmentActivity.getContentResolver().query(contactData, null, null, null, null);
        cursor.moveToFirst();
        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

        AssetFileDescriptor fd;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CONTACT_" + timeStamp + "_" + ".vcf";

        File outputFile = FileClientService.getFilePath(imageFileName, fragmentActivity, "text/x-vcard");

        fd = fragmentActivity.getContentResolver().openAssetFileDescriptor(uri, "r");

        FileInputStream fis = fd.createInputStream();
        byte[] buf = new byte[(int) fd.getDeclaredLength()];
        fis.read(buf);
        String cvFdata = new String(buf);
        if (!MobiComVCFParser.validateData(cvFdata)) {
            Log.i("vCard ::", cvFdata.toString());
            throw new Exception("contact exported is not proper in proper format");
        }
        Log.i(" data:", new String(buf));
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile.getAbsoluteFile());
        fileOutputStream.write(buf);
        fileOutputStream.close();
        return outputFile;
    }


    public void reconnectMQTT() {
        try {
            if (((MobiComKitActivityInterface) fragmentActivity).getRetryCount() <= 3) {
                if (Utils.isInternetAvailable(fragmentActivity)) {
                    Log.i(TAG, "Reconnecting to mqtt.");
                    ((MobiComKitActivityInterface) fragmentActivity).retry();
                    Intent intent = new Intent(fragmentActivity, ApplozicMqttIntentService.class);
                    intent.putExtra(ApplozicMqttIntentService.SUBSCRIBE, true);
                    fragmentActivity.startService(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLocation(String position) {
        getConversationFragment().sendMessage(position, Message.ContentType.LOCATION.getValue());
    }

}