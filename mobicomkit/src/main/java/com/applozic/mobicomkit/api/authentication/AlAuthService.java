package com.applozic.mobicomkit.api.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
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
                && (System.currentTimeMillis() - createdAtTime) / 60000 < validUptoMins;
    }

    public static void refreshToken(Context context, AlCallback callback) {
        new RefreshAuthTokenTask(context, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void verifyToken(Context context, String loadingMessage, AlCallback callback) {
        if (context == null) {
            return;
        }

        if (!isTokenValid(context)) {
            refreshToken(context, loadingMessage, callback);
        } else {
            String token = MobiComUserPreference.getInstance(context).getUserAuthToken();
            if (!TextUtils.isEmpty(token)) {
                JWT.parseToken(context, token);
                if (callback != null) {
                    callback.onSuccess(true);
                }
            } else {
                refreshToken(context, loadingMessage, callback);
            }
        }
    }

    public static void refreshToken(Context context, String loadingMessage, final AlCallback callback) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(context));
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();

        refreshToken(context, new AlCallback() {
            @Override
            public void onSuccess(Object response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }

            @Override
            public void onError(Object error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (callback != null) {
                    callback.onSuccess(error);
                }
            }
        });
    }

    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
