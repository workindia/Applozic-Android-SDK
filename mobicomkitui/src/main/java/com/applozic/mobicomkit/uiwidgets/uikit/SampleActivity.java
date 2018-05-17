package com.applozic.mobicomkit.uiwidgets.uikit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageBuilder;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.R;

public class SampleActivity extends AppCompatActivity implements AlMessageSenderView.AlMessageViewEvents {

    AlTypingIndicator typingIndicator;
    AlMessageSenderView messageSenderView;
    AlAttachmentView attachmentView;
    LinearLayout snackBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        typingIndicator = findViewById(R.id.alTypingIndicator);
        messageSenderView = findViewById(R.id.alMessageSenderView);
        attachmentView = findViewById(R.id.alAttachmentView);
        snackBarLayout = findViewById(R.id.snackbarLayout);

        messageSenderView.createView(new AppContactService(this).getContactById("reytum6"), null, this);
        attachmentView.createView();
    }

    @Override
    public void onTyping(EditText editText, boolean typingStarted) {

    }

    @Override
    public void onFocus(EditText editText, boolean hasFocus) {
        if (attachmentView != null) {
            attachmentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(EditText editText) {
        if (attachmentView != null) {
            attachmentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachmentButtonClick() {
        if (attachmentView != null) {
            attachmentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSendButtonClicked(EditText editText) {
        if (editText != null) {
            if (!TextUtils.isEmpty(editText.getText().toString())) {
                new MessageBuilder(this).setTo("reytum6").setContentType(Message.ContentType.DEFAULT.getValue()).setMessage(editText.getText().toString()).send();
                editText.setText("");
            }
        }
    }

    @Override
    public void onRecordButtonClicked() {

    }

    @Override
    protected void onPause() {
        Applozic.disconnectPublish(this);
        typingIndicator.unSubscribe(null, new AppContactService(this).getContactById("reytum6"));
        super.onPause();
    }

    @Override
    protected void onResume() {
        Applozic.connectPublish(this);
        typingIndicator.subscribe(null, new AppContactService(this).getContactById("reytum6"));
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AlAttachmentOptions.handleAttachmentOptionsResult(requestCode, resultCode, data,this,"reytum6",null);
        //super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (AlAttachmentOptions.isApplozicPermissionCode(requestCode)) {
            AlAttachmentOptions.onRequestPermissionsResult(requestCode, permissions, grantResults, snackBarLayout, this);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
