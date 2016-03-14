package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobiComAttachmentGridViewAdapter;
import com.applozic.mobicommons.file.FileUtils;

import java.util.ArrayList;

/**
 *
 */
public class MobiComAttachmentSelectorActivity extends AppCompatActivity  {

    public static final String MULTISELECT_SELECTED_FILES = "multiselect.selectedFiles";
    public static final String MULTISELECT_MESSAGE = "multiselect.message";
    private String TAG = "MultiAttActivity";
    private static int REQUEST_CODE_ATTACH_PHOTO =10;
    private  Button selectAttachment;
    private  Button cancelAttachment;
    private EditText messageEditText;


    private  GridView galleryImagesGridView;
    private  ArrayList<Uri> attachmentFileList = new ArrayList<Uri>();

    private MobiComAttachmentGridViewAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobicom_multi_attachment_activity);
        initViews();
        setUpGridView();

        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        Intent intentPick = Intent.createChooser(getContentIntent, getString(R.string.select_file));
        startActivityForResult(intentPick, REQUEST_CODE_ATTACH_PHOTO);
    }

    /**
     * views initialisation.
     */
    private void initViews() {

        selectAttachment = (Button) findViewById(R.id.mobicom_attachment_select_btn);
        cancelAttachment=  (Button) findViewById(R.id.mobicom_attachment_cancel_btn);
        galleryImagesGridView = (GridView) findViewById(R.id.mobicom_attachment_grid_View);
        messageEditText = (EditText) findViewById(R.id.mobicom_attachment_edit_text);


        cancelAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        selectAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(MULTISELECT_SELECTED_FILES, attachmentFileList);
                intent.putExtra(MULTISELECT_MESSAGE, messageEditText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    /**
     *
     * @param uri
     */
    private void addUri(Uri uri) {

        attachmentFileList.add(uri);
        Log.i(TAG, "attachmentFileList  :: " + attachmentFileList);


    }

    /**
     *
     */
    private void setUpGridView() {
        imagesAdapter = new MobiComAttachmentGridViewAdapter(MobiComAttachmentSelectorActivity.this, attachmentFileList);
        galleryImagesGridView.setAdapter(imagesAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == Activity.RESULT_OK ){
            Uri selectedFileUri = (intent == null ? null : intent.getData());
            Log.i(TAG, "selectedFileUri :: " + selectedFileUri);
            addUri(selectedFileUri);
            imagesAdapter.notifyDataSetChanged();
        }

    }
}