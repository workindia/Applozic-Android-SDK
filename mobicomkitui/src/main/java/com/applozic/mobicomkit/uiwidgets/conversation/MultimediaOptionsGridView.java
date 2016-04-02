package com.applozic.mobicomkit.uiwidgets.conversation;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by reytum on 19/3/16.
 */
public class MultimediaOptionsGridView {
    private Uri capturedImageUri;
    public PopupWindow showPopup;
    FragmentActivity context;
    GridView multimediaOptions;

    public MultimediaOptionsGridView(FragmentActivity context, GridView multimediaOptions) {
        this.context = context;
        this.multimediaOptions = multimediaOptions;
    }

    public void setMultimediaClickListener() {
        capturedImageUri = null;
        multimediaOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        ((ConversationActivity) context).processLocation();
                        break;
                    case 1:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (intent.resolveActivity(context.getApplicationContext().getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile;

                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";

                            photoFile = FileClientService.getFilePath(imageFileName, context, "image/jpeg");

                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                capturedImageUri = Uri.fromFile(photoFile);
                                ConversationActivity.setCapturedImageUri(capturedImageUri);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                                context.startActivityForResult(intent, MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO);
                            }
                        }
                        break;
                    case 2:

                        Intent intentPick = new Intent(context, MobiComAttachmentSelectorActivity.class);
                        context.startActivityForResult(intentPick, MultimediaOptionFragment.REQUEST_MULTI_ATTCAHMENT);
                        break;
                    case 3:
                        ((ConversationActivity) context).showAudioRecordingDialog();
                        break;
                    case 4:

                        // create new Intentwith with Standard Intent action that can be
                        // sent to have the camera application capture an video and return it.
                        intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "VID_" + timeStamp + "_" + ".mp4";

                        File fileUri = FileClientService.getFilePath(imageFileName, context, "video/mp4");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileUri));
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        ((ConversationActivity) context).setVideoFileUri(Uri.fromFile(fileUri));
                        context.startActivityForResult(intent, MultimediaOptionFragment.REQUEST_CODE_CAPTURE_VIDEO_ACTIVITY);
                        break;

                    case 5:
                        //Sharing contact.
                        intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                        context.startActivityForResult(intent, MultimediaOptionFragment.REQUEST_CODE_CONTACT_SHARE);
                        break;

                    case 6:
                        new ConversationUIService(context).sendPriceMessage();
                        break;
                    default:

                }
                multimediaOptions.setVisibility(View.GONE);
            }
        });
    }

}
