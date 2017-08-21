package com.applozic.mobicomkit.api.account.user;

/**
 * Created by Aman on 7/12/2015.
 */

import android.content.Context;
import android.os.AsyncTask;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask {

    private final TaskListener taskListener;
    private final Context context;
    private User user;
    private Exception mException;
    private RegistrationResponse registrationResponse;
    private UserClientService userClientService;
    private RegisterUserClientService registerUserClientService;
    private UserService userService;
    private boolean aBoolean;

    public UserLoginTask(User user, TaskListener listener, Context context) {
        this.taskListener = listener;
        this.context = context;
        this.user = user;
        this.userClientService = new UserClientService(context);
        this.registerUserClientService = new RegisterUserClientService(context);
        this.userService = UserService.getInstance(context);
    }


    @Override
    protected Object doInBackground(Object[] params) {
        try {
            userClientService.clearDataAndPreference();
            registrationResponse = registerUserClientService.createAccount(user);
            userService.processPackageDetail();
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            aBoolean =  false;
        }
        aBoolean = true;
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (aBoolean && this.taskListener != null) {
            this.taskListener.onSuccess(registrationResponse, context);

        } else if (mException != null && this.taskListener != null) {
            this.taskListener.onFailure(registrationResponse, mException);
        }
    }


    public interface TaskListener {
        void onSuccess(RegistrationResponse registrationResponse, Context context);

        void onFailure(RegistrationResponse registrationResponse, Exception exception);

    }

}
