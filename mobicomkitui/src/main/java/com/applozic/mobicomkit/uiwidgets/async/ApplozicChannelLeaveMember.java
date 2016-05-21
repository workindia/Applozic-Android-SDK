package com.applozic.mobicomkit.uiwidgets.async;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.uiwidgets.R;

/**
 * Created by sunil on 17/5/16.
 */
public class ApplozicChannelLeaveMember extends AsyncTask<Void, Void, Boolean> {

    Context context;
    Integer channelKey;
    String userId;
    ChannelLeaveMemberListener channelLeaveMemberListener;
    ChannelService channelService;
    Exception exception;
    String leaveResponse;

    public ApplozicChannelLeaveMember(Context context, Integer channelKey, String userId, ChannelLeaveMemberListener channelLeaveMemberListener) {
        this.channelKey = channelKey;
        this.userId = userId;
        this.channelLeaveMemberListener = channelLeaveMemberListener;
        this.context = context;
        this.channelService = ChannelService.getInstance(context);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (!TextUtils.isEmpty(userId) && userId.trim().length() != 0 && channelKey != null) {
                leaveResponse = channelService.leaveMemberFromChannelProcess(channelKey, userId.trim());
                return true;
            } else {
                throw new Exception(context.getString(R.string.applozic_userId_error_info_in_logs));
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean resultBoolean) {
        super.onPostExecute(resultBoolean);

        if (resultBoolean && !TextUtils.isEmpty(leaveResponse) && channelLeaveMemberListener != null) {
            channelLeaveMemberListener.onSuccess(leaveResponse, context);
        } else if (!resultBoolean && exception != null && channelLeaveMemberListener != null) {
            channelLeaveMemberListener.onFailure(leaveResponse,exception, context);
        }
    }

   public interface ChannelLeaveMemberListener {
        void onSuccess(String response, Context context);

        void onFailure(String response,Exception e, Context context);
    }
}
