package com.applozic.mobicomkit.api.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.listners.AlCallback;

public class AlAuthService {

    public static boolean isTokenValid(Context context) {
        MobiComUserPreference userPreference = MobiComUserPreference.getInstance(context);
        if (userPreference == null) {
            return false;
        }

        float createdAtTime = userPreference.getTokenCreatedAtTime();
        int validUptoMins = userPreference.getTokenValidUptoMins();

        return createdAtTime > 0
                && validUptoMins > 0
                && (System.currentTimeMillis() - createdAtTime) / 60000 > validUptoMins;
    }

    public static void refreshToken(Context context, AlCallback callback) {
        new RefreshAuthTokenTask(context, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void verifyToken(Activity activity, String loadingMessage) {
        if (activity == null) {
            return;
        }

        if (isTokenValid(activity)) {
            refreshToken(activity, loadingMessage);
        } else {
            String token = MobiComUserPreference.getInstance(activity).getUserAuthToken();
            if (!TextUtils.isEmpty(token)) {
                JWT.parseToken(activity, token);
            } else {
                refreshToken(activity, loadingMessage);
            }
        }
    }

    public static void refreshToken(Activity activity, String loadingMessage) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();

        refreshToken(activity.getApplicationContext(), new AlCallback() {
            @Override
            public void onSuccess(Object response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onError(Object error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
