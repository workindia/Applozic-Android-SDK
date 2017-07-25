package com.applozic.mobicomkit.api.account.register;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.account.user.ApplozicUser;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.ConversationIntentService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.exception.InvalidApplicationException;
import com.applozic.mobicomkit.exception.UnAuthoriseException;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.gson.Gson;

import java.net.ConnectException;
import java.util.TimeZone;

/**
 * Created by devashish on 2/2/15.
 */
public class RegisterUserClientService extends MobiComKitClientService {

    public static final String CREATE_ACCOUNT_URL = "/rest/ws/register/client?";
    public static final String UPDATE_ACCOUNT_URL = "/rest/ws/register/update?";
    public static final String CHECK_PRICING_PACKAGE = "/rest/ws/application/pricing/package";
    public static final Short MOBICOMKIT_VERSION_CODE = 109;
    private static final String TAG = "RegisterUserClient";
    private static final String INVALID_APP_ID = "INVALID_APPLICATIONID";
    private HttpRequestUtils httpRequestUtils;

    public RegisterUserClientService(Context context) {
        this.context = context.getApplicationContext();
        this.httpRequestUtils = new HttpRequestUtils(context);
    }

    public String getCreateAccountUrl() {
        return getBaseUrl() + CREATE_ACCOUNT_URL;
    }

    public String getPricingPackageUrl() {
        return getBaseUrl() + CHECK_PRICING_PACKAGE;
    }

    public String getUpdateAccountUrl() {
        return getBaseUrl() + UPDATE_ACCOUNT_URL;
    }


//    final RegistrationResponse registrationResponse = createAccount(user);
//    Intent intent = new Intent(context, ApplozicMqttIntentService.class);
//    intent.putExtra(ApplozicMqttIntentService.CONNECTED_PUBLISH,true);
//    context.startService(intent);
//    return registrationResponse;


    public RegistrationResponse createAccount(ApplozicUser applozicUser) throws Exception {

        applozicUser.setDeviceType(Short.valueOf("1"));
        applozicUser.setPrefContactAPI(Short.valueOf("2"));
        applozicUser.setTimezone(TimeZone.getDefault().getID());
        applozicUser.setEnableEncryption(applozicUser.isEnableEncryption());


        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);

        Gson gson = new Gson();
        applozicUser.setAppVersionCode(MOBICOMKIT_VERSION_CODE);
        applozicUser.setApplicationId(getApplicationKey(context));
        applozicUser.setRegistrationId(mobiComUserPreference.getDeviceRegistrationId());

        if (getAppModuleName(context) != null) {
            applozicUser.setAppModuleName(getAppModuleName(context));
        }

        Utils.printLog(context,TAG, "Net status" + Utils.isInternetAvailable(context.getApplicationContext()));

        if (!Utils.isInternetAvailable(context.getApplicationContext())) {
            throw new ConnectException("No Internet Connection");
        }

//        Log.i(TAG, "App Id is: " + getApplicationKey(context));
        Utils.printLog(context,TAG, "Registration json " + gson.toJson(applozicUser));
        String response = httpRequestUtils.postJsonToServer(getCreateAccountUrl(), gson.toJson(applozicUser));

        Utils.printLog(context,TAG, "Registration response is: " + response);

        if (TextUtils.isEmpty(response) || response.contains("<html")) {
            throw new Exception("503 Service Unavailable");
//            return null;
        }
        if (response.contains(INVALID_APP_ID)) {
            throw new InvalidApplicationException("Invalid Application Id");
        }
        final RegistrationResponse registrationResponse = gson.fromJson(response, RegistrationResponse.class);

        if (registrationResponse.isPasswordInvalid()) {
            throw new UnAuthoriseException("Invalid uername/password");

        }
        Utils.printLog(context,"Registration response ", "is " + registrationResponse);
        if (registrationResponse.getNotificationResponse() != null) {
            Utils.printLog(context,"Registration response ", "" + registrationResponse.getNotificationResponse());
        }
        mobiComUserPreference.setEncryptionKey(registrationResponse.getEncryptionKey());
        mobiComUserPreference.enableEncryption(applozicUser.isEnableEncryption());
        mobiComUserPreference.setCountryCode(applozicUser.getCountryCode());
        mobiComUserPreference.setUserId(applozicUser.getUserId());
        mobiComUserPreference.setContactNumber(applozicUser.getContactNumber());
        mobiComUserPreference.setEmailVerified(applozicUser.isEmailVerified());
        mobiComUserPreference.setDisplayName(applozicUser.getDisplayName());
        mobiComUserPreference.setMqttBrokerUrl(registrationResponse.getBrokerUrl());
        mobiComUserPreference.setDeviceKeyString(registrationResponse.getDeviceKey());
        mobiComUserPreference.setEmailIdValue(applozicUser.getEmail());
        mobiComUserPreference.setImageLink(applozicUser.getImageLink());
        mobiComUserPreference.setSuUserKeyString(registrationResponse.getUserKey());
        mobiComUserPreference.setLastSyncTime(String.valueOf(registrationResponse.getCurrentTimeStamp()));
        mobiComUserPreference.setLastSeenAtSyncTime(String.valueOf(registrationResponse.getCurrentTimeStamp()));
        mobiComUserPreference.setChannelSyncTime(String.valueOf(registrationResponse.getCurrentTimeStamp()));
        mobiComUserPreference.setUserBlockSyncTime("10000");
        mobiComUserPreference.setPassword(applozicUser.getPassword());
        mobiComUserPreference.setPricingPackage(registrationResponse.getPricingPackage());

        mobiComUserPreference.setAuthenticationType(String.valueOf(applozicUser.getAuthenticationTypeId()));
        if(applozicUser.getUserTypeId() != null){
            mobiComUserPreference.setUserTypeId(String.valueOf(applozicUser.getUserTypeId()));
        }
        if(!TextUtils.isEmpty(applozicUser.getNotificationSoundFilePath())){
            mobiComUserPreference.setNotificationSoundFilePath(applozicUser.getNotificationSoundFilePath());
        }
        Contact contact=  new Contact();
        contact.setUserId(applozicUser.getUserId());
        contact.setFullName(registrationResponse.getDisplayName());
        contact.setImageURL(registrationResponse.getImageLink());
        contact.setContactNumber(registrationResponse.getContactNumber());
        if(applozicUser.getUserTypeId() != null){
            contact.setUserTypeId(applozicUser.getUserTypeId());

        }
        contact.setStatus(registrationResponse.getStatusMessage());
        contact.processContactNumbers(context);
        new AppContactService(context).upsert(contact);
        Intent conversationIntentService = new Intent(context, ConversationIntentService.class);
        conversationIntentService.putExtra(ConversationIntentService.SYNC, false);
        context.startService(conversationIntentService);

        Intent intent = new Intent(context, ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.CONNECTED_PUBLISH, true);
        context.startService(intent);
        return registrationResponse;
    }


    public RegistrationResponse createAccount(String email, String userId, String phoneNumber, String displayName, String imageLink, String pushNotificationId) throws Exception {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);
        String url = mobiComUserPreference.getUrl();
        mobiComUserPreference.clearAll();
        mobiComUserPreference.setUrl(url);

        return updateAccount(email, userId, phoneNumber, displayName, imageLink, pushNotificationId);
    }

    private RegistrationResponse updateAccount(String email, String userId, String phoneNumber, String displayName, String imageLink, String pushNotificationId) throws Exception {
        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);

        ApplozicUser applozicUser = new ApplozicUser();
        applozicUser.setUserId(userId);
        applozicUser.setEmail(email);
        applozicUser.setImageLink(imageLink);
        applozicUser.setRegistrationId(pushNotificationId);
        applozicUser.setDisplayName(displayName);

        //applozicUser.setCountryCode(mobiComUserPreference.getCountryCode());
       /*if (!TextUtils.isEmpty(phoneNumber)) {
            try {

                    applozicUser.setCountryCode(PhoneNumberUtil.getInstance().getRegionCodeForNumber(PhoneNumberUtil.getInstance().parse(phoneNumber, "")));
                    mobiComUserPreference.setCountryCode(applozicUser.getCountryCode());

            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }*/
        applozicUser.setContactNumber(phoneNumber);

        final RegistrationResponse registrationResponse = createAccount(applozicUser);
        Intent intent = new Intent(context, ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.CONNECTED_PUBLISH, true);
        context.startService(intent);
        return registrationResponse;
    }

    public RegistrationResponse updatePushNotificationId(final String pushNotificationId) throws Exception {
        MobiComUserPreference pref = MobiComUserPreference.getInstance(context);
        //Note: In case if gcm registration is done before login then only updating in pref

        RegistrationResponse registrationResponse = null;
        ApplozicUser applozicUser = getUserDetail();

        if (!TextUtils.isEmpty(pushNotificationId)) {
            pref.setDeviceRegistrationId(pushNotificationId);
        }
        applozicUser.setRegistrationId(pushNotificationId);
        if (pref.isRegistered()) {
            registrationResponse = updateRegisteredAccount(applozicUser);
        }
        return registrationResponse;
    }


    public RegistrationResponse updateRegisteredAccount(ApplozicUser applozicUser) throws Exception {
        RegistrationResponse registrationResponse = null;

        applozicUser.setDeviceType(Short.valueOf("1"));
        applozicUser.setPrefContactAPI(Short.valueOf("2"));
        applozicUser.setTimezone(TimeZone.getDefault().getID());

        MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(context);

        Gson gson = new Gson();

        applozicUser.setEnableEncryption(mobiComUserPreference.isEncryptionEnabled());
        applozicUser.setAppVersionCode(MOBICOMKIT_VERSION_CODE);
        applozicUser.setApplicationId(getApplicationKey(context));
        applozicUser.setAuthenticationTypeId(Short.valueOf(mobiComUserPreference.getAuthenticationType()));
        if (!TextUtils.isEmpty(mobiComUserPreference.getUserTypeId())) {
            applozicUser.setUserTypeId(Short.valueOf(mobiComUserPreference.getUserTypeId()));
        }
        if (getAppModuleName(context) != null) {
            applozicUser.setAppModuleName(getAppModuleName(context));
        }
        if (!TextUtils.isEmpty(mobiComUserPreference.getDeviceRegistrationId())) {
            applozicUser.setRegistrationId(mobiComUserPreference.getDeviceRegistrationId());
        }
        Utils.printLog(context,TAG, "Registration update json " + gson.toJson(applozicUser));
        String response = httpRequestUtils.postJsonToServer(getUpdateAccountUrl(), gson.toJson(applozicUser));

        if (TextUtils.isEmpty(response) || response.contains("<html")) {
            throw null;
        }
        if (response.contains(INVALID_APP_ID)) {
            throw new InvalidApplicationException("Invalid Application Id");
        }

        registrationResponse  = gson.fromJson(response, RegistrationResponse.class);

        if (registrationResponse.isPasswordInvalid()) {
            throw new UnAuthoriseException("Invalid uername/password");
        }

        Utils.printLog(context,TAG, "Registration update response: " + registrationResponse);
        mobiComUserPreference.setPricingPackage(registrationResponse.getPricingPackage());
        if (registrationResponse.getNotificationResponse() != null) {
            Utils.printLog(context,TAG, "Notification response: " + registrationResponse.getNotificationResponse());
        }

        return registrationResponse;

    }

    private ApplozicUser getUserDetail() {

        MobiComUserPreference pref = MobiComUserPreference.getInstance(context);

        ApplozicUser applozicUser = new ApplozicUser();
        applozicUser.setEmail(pref.getEmailIdValue());
        applozicUser.setUserId(pref.getUserId());
        applozicUser.setContactNumber(pref.getContactNumber());
        applozicUser.setDisplayName(pref.getDisplayName());
        applozicUser.setImageLink(pref.getImageLink());
        return applozicUser;
    }

    public void syncAccountStatus() {
        try {
            String response = httpRequestUtils.getResponse(getPricingPackageUrl(), "application/json", "application/json");
            Utils.printLog(context,TAG, "Pricing package response: " + response);
            ApiResponse apiResponse = (ApiResponse) GsonUtils.getObjectFromJson(response, ApiResponse.class);
            if (apiResponse.getResponse() != null) {
                int pricingPackage = Integer.parseInt(apiResponse.getResponse().toString());
                MobiComUserPreference.getInstance(context).setPricingPackage(pricingPackage);
            }
        } catch (Exception e) {
            Utils.printLog(context,TAG, "Account status sync call failed");
        }
    }

}
