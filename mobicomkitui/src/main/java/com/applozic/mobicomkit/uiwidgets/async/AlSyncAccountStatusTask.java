package com.applozic.mobicomkit.uiwidgets.async;

import android.content.Context;
import android.os.AsyncTask;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.ApplozicUser;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;


/**
 * Created by sunil on 19/12/2016.
 */

public class AlSyncAccountStatusTask extends AsyncTask<Void, Void, Boolean> {
    Context context;
    RegisterUserClientService registerUserClientService;
    TaskListener taskListener;
    String loggedInUserId;

    public interface TaskListener {
        void onCompletion(Context context);
    }

    public AlSyncAccountStatusTask(Context context, TaskListener taskListener){
        this.context = context;
        this.taskListener = taskListener;
        this.registerUserClientService = new RegisterUserClientService(context);
        this.loggedInUserId = MobiComUserPreference.getInstance(context).getUserId();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ApplozicUser applozicUser = new ApplozicUser();
        applozicUser.setUserId(loggedInUserId);
        try {
            registerUserClientService.updateRegisteredAccount(applozicUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(taskListener != null){
            taskListener.onCompletion(context);
        }
    }
}
