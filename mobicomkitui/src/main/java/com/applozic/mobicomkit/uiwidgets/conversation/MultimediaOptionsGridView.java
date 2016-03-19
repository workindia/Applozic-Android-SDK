package com.applozic.mobicomkit.uiwidgets.conversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobicomLocationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobicomMultimediaPopupAdapter;
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

    public MultimediaOptionsGridView(FragmentActivity context) {
        this.context = context;
    }

    public void showPopup(View view, String[] multimediaIcons, String[] multimediaText) {
        showPopup = newBasicPopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.mobicom_multimedia_fragment_layout, null);
        GridView multimediaOptions = (GridView) popupView.findViewById(R.id.mobicom_multimedia_options);
        MobicomMultimediaPopupAdapter adap = new MobicomMultimediaPopupAdapter(context, multimediaIcons, multimediaText);
        multimediaOptions.setAdapter(adap);

        showPopup.setContentView(popupView);
        showPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        showPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        showPopup.setAnimationStyle(R.style.Animations_GrowFromTop);
        showPopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        capturedImageUri = null;
        multimediaOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent toMapActivity = new Intent(context, MobicomLocationActivity.class);
                        context.startActivityForResult(toMapActivity, MultimediaOptionFragment.REQUEST_CODE_SEND_LOCATION);
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
                showPopup.dismiss();
            }
        });
    }

    public static PopupWindow newBasicPopupWindow(Context context) {
        final PopupWindow window = new PopupWindow(context);

        window.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    window.dismiss();
                    return true;
                }
                return false;
            }
        });

        window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setOutsideTouchable(true);

        window.setBackgroundDrawable(new BitmapDrawable());

        return window;
    }

}
