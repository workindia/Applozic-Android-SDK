package com.applozic.mobicomkit.api.account.register;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.ApplozicMqttService;
import com.applozic.mobicomkit.api.account.user.User;
import com.google.gson.Gson;
import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.exception.InvalidApplicationException;

import com.applozic.mobicommons.commons.core.utils.ContactNumberUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.TimeZone;

/**
 * Created by devashish on 2/2/15.
 */
public class RegisterUserClientService extends MobiComKitClientService {

    private static final String TAG = "RegisterUserClient";
    private static final String INVALID_APP_ID = "INVALID_APPLICATIONID";
    public static final String CREATE_ACCOUNT_URL = "/rest/ws/register/client?";
    public static final Short MOBICOMKIT_VERSION_CODE = 101;


    private HttpRequestUtils httpRequestUtils;

    public RegisterUserClientService(Context context) {
        this.context = context;
        this.httpRequestUtils = new HttpRequestUtils(context);
    }

    public String getCreateAccountUrl() {
        return getBaseUrl() + CREATE_ACCOUNT_URL;
    }

    private RegistrationResponse createAccount(User user) throws Exception {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);

        Gson gson = new Gson();
        user.setAppVersionCode(MOBICOMKIT_VERSION_CODE);
        user.setApplicationId(getApplicationKey(context));
        user.setRegistrationId(mobiComUserPreference.getDeviceRegistrationId());

        Log.i(TAG, "Net status" + Utils.isInternetAvailable(context));

        if (!Utils.isInternetAvailable(context)) {
            throw new ConnectException("No Internet Connection");
        }

//        Log.i(TAG, "App Id is: " + getApplicationKey(context));
        Log.i(TAG, "Registration json " + gson.toJson(user));
        String response = httpRequestUtils.postJsonToServer(getCreateAccountUrl(), gson.toJson(user));

        Log.i(TAG, "Registration response is: " + response);

        if (TextUtils.isEmpty(response) || response.contains("<html")) {
            throw new UnknownHostException("Error 404");
//            return null;
        }
        if (response.contains(INVALID_APP_ID)) {
            throw new InvalidApplicationException("Invalid Application Id");
        }
        final RegistrationResponse registrationResponse = gson.fromJson(response, RegistrationResponse.class);
        Log.i("Registration response ", "is " + registrationResponse);

        mobiComUserPreference.setCountryCode(user.getCountryCode());
        mobiComUserPreference.setUserId(user.getUserId());
        mobiComUserPreference.setContactNumber(user.getContactNumber());
        mobiComUserPreference.setEmailVerified(user.isEmailVerified());
        mobiComUserPreference.setDisplayName(user.getDisplayName());
        mobiComUserPreference.setDeviceKeyString(registrationResponse.getDeviceKey());
        mobiComUserPreference.setEmailIdValue(user.getEmail());
        mobiComUserPreference.setSuUserKeyString(registrationResponse.getUserKey());
        mobiComUserPreference.setLastSyncTime(String.valueOf(registrationResponse.getLastSyncTime()));
        return registrationResponse;
    }


    public RegistrationResponse createAccount(String email, String userId, String phoneNumber, String displayName, String pushNotificationId) throws Exception {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
        mobiComUserPreference.clearAll();

        return updateAccount(email, userId, phoneNumber, displayName, pushNotificationId);
    }

    private RegistrationResponse updateAccount(String email, String userId, String phoneNumber, String displayName, String pushNotificationId) throws Exception {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);

        User user = new User();
        user.setUserId(userId);
        user.setEmail(email);
        user.setDeviceType(Short.valueOf("1"));
        user.setPrefContactAPI(Short.valueOf("2"));
        user.setTimezone(TimeZone.getDefault().getID());
        user.setRegistrationId(pushNotificationId);
        user.setDisplayName(displayName);

        user.setCountryCode(mobiComUserPreference.getCountryCode());
        user.setContactNumber(ContactNumberUtils.getPhoneNumber(phoneNumber, mobiComUserPreference.getCountryCode()));

        final RegistrationResponse registrationResponse = createAccount(user);
        ApplozicMqttService.getInstance(context).connectPublish(registrationResponse.getUserKey(), "1");
        return registrationResponse;
    }

    public RegistrationResponse updatePushNotificationId(final String pushNotificationId) throws Exception {
        MobiComUserPreference pref = MobiComUserPreference.getInstance(context);
        //Note: In case if gcm registration is done before login then only updating in pref

        RegistrationResponse registrationResponse = null;
        if (!TextUtils.isEmpty(pushNotificationId)) {
            pref.setDeviceRegistrationId(pushNotificationId);
        }
        if (pref.isRegistered()) {
            registrationResponse = updateAccount(pref.getEmailIdValue(), pref.getUserId(), pref.getContactNumber(), pref.getDisplayName(), pushNotificationId);
        }
        return registrationResponse;
    }
}
