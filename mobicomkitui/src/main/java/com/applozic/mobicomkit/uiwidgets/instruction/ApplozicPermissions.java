package com.applozic.mobicomkit.uiwidgets.instruction;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;

import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobicomLocationActivity;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;

/**
 * Created by sunil on 22/1/16.
 */
public class ApplozicPermissions {
    private LinearLayout snackBarLayout;
    private Activity activity;

    public ApplozicPermissions(Activity activity, LinearLayout linearLayout) {
        this.activity = activity;
        this.snackBarLayout = linearLayout;
    }

    public void checkRuntimePermissionForStorage() {
        if (PermissionsUtils.checkSelfForStoragePermission(activity)) {
            requestStoragePermissions();
        }
    }

    public void checkRuntimePermissionForLocation() {
        if (PermissionsUtils.checkSelfPermissionForLocation(activity)) {
            requestLocationPermissions();
        } else {
            ((ConversationActivity) activity).processingLocation();
        }
    }

    private void requestStoragePermissions() {
        if (PermissionsUtils.shouldShowRequestForStoragePermission(activity)) {
            showSnackBar(R.string.storage_permission, PermissionsUtils.PERMISSIONS_STORAGE, PermissionsUtils.REQUEST_STORAGE);
        } else {
            PermissionsUtils.requestPermissions(activity, PermissionsUtils.PERMISSIONS_STORAGE, PermissionsUtils.REQUEST_STORAGE);
        }
    }

    public void requestLocationPermissions() {
        if (PermissionsUtils.shouldShowRequestForLocationPermission(activity)) {
            showSnackBar(R.string.location_permission, PermissionsUtils.PERMISSIONS_LOCATION, PermissionsUtils.REQUEST_LOCATION);
        } else {
            PermissionsUtils.requestPermissions(activity, PermissionsUtils.PERMISSIONS_LOCATION, PermissionsUtils.REQUEST_LOCATION);
        }
    }

    public void requestAudio() {
        if (PermissionsUtils.shouldShowRequestForLocationPermission(activity)) {
            showSnackBar(R.string.record_audio, PermissionsUtils.PERMISSIONS_LOCATION, PermissionsUtils.REQUEST_AUDIO_RECORD);
        } else {
            PermissionsUtils.requestPermissions(activity, PermissionsUtils.PERMISSIONS_RECORD_AUDIO, PermissionsUtils.REQUEST_AUDIO_RECORD);
        }
    }

    public void showSnackBar(int resId, final String[] permissions, final int requestCode) {
        Snackbar.make(snackBarLayout, resId,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PermissionsUtils.requestPermissions(activity, permissions, requestCode);
                    }
                }).show();
    }

}
