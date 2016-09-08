package com.applozic.mobicomkit.sample.pushnotification;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by sunil on 9/4/16.
 */
public class FcmInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        if(MobiComUserPreference.getInstance(this).isRegistered()){
            FCMRegistrationUtils fcmRegistrationUtils = new FCMRegistrationUtils(this);
            fcmRegistrationUtils.setUpFcmNotification();
        }
    }
}
