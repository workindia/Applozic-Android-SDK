package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by devashish on 02/03/16.
 */
public class AudioMessageActivity extends AppCompatActivity{

    ImageButton  cancel, send;
    TextView txtcount,audioRecordingText;
    ImageButton record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    CountDownTimer t;
    private int cnt;
    private boolean isRecordring;
    private android.support.v7.app.ActionBar mActionBar;




    public static String RECORDED_PATH = "RECORDED_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mobicom_audio_message_layout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Record Message");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mActionBar.setDisplayShowHomeEnabled(true);

        record = (ImageButton) findViewById(R.id.audio_mic_imageview);
        send = (ImageButton) findViewById(R.id.audio_send);
        cancel = (ImageButton) findViewById(R.id.audio_cancel);

        txtcount = (TextView)findViewById(R.id.txtcount);
        audioRecordingText = (TextView)findViewById(R.id.audio_recording_text);


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "AUD_" + timeStamp + "_" + ".m4a";

        outputFile = FileClientService.getFilePath(imageFileName, AudioMessageActivity.this, "audio/m4a").getAbsolutePath();

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setAudioEncodingBitRate(256);
        myAudioRecorder.setAudioChannels(1);
        myAudioRecorder.setAudioSamplingRate(44100);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if(isRecordring){
                        AudioMessageActivity.this.stopRecording();

                    }else{
                        audioRecordingText.setText("Recording..");
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                        isRecordring=true;
                        record.setImageResource(R.drawable.applozic_audio_mic_inverted);
                        t.start();
                        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

                    }

                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecordring) {
                    AudioMessageActivity.this.stopRecording();
                }
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                //IF recording is running stoped it ...
               if(isRecordring){
                    stopRecording();
               }
                Intent data= new Intent();
                data = data.putExtra(RECORDED_PATH, outputFile);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                setResult(MultimediaOptionFragment.REQUEST_CODE_ATTACHE_AUDIO, data);
                Toast.makeText(getApplicationContext(), "sending audio file", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        // Set Timer
        t = new CountDownTimer( Long.MAX_VALUE , 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                cnt++;
                String time = new Integer(cnt).toString();

                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                txtcount.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));

            }

            @Override
            public void onFinish() {            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRecording();
        //TODO: delete the file stored...
    }

    public void stopRecording() {

        if(myAudioRecorder !=null){
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            isRecordring =false;
            record.setImageResource(R.drawable.applozic_audio_normal);
            audioRecordingText.setText("Tap To Record");
            t.cancel();
            Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
        }

    }
}



