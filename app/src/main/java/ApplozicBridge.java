import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.commons.core.utils.Utils;

import static com.applozic.mobicomkit.api.account.user.UserLoginTask.*;

/**
 * Created by applozic on 12/5/15.
 */
public class ApplozicBridge {

    public static void startChatActivity(Context context, final String contactId, final String displayName) {
        if (!MobiComUserPreference.getInstance(context).isLoggedIn()) {
            //perform login action first then show chat screen
            //get the email and

            TaskListener listener = new TaskListener() {

                @Override
                public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                    String pushNotificationId = "";//Todo: get pushnotification id.
                    gcmRegister(context, pushNotificationId);
                    showChatActvity(context, contactId, displayName);

                }

                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                }
            };

            String userId = ""; //Todo: fetch this from gmail accounts.
            User user = new User();
            user.setUserId(userId);
            //user.setEmail(userId); //optional

            new UserLoginTask(user, listener, context).execute((Void) null);

        } else {
            showChatActvity(context, contactId, displayName);
        }
    }

    public static void startChatActivity(Context context) {
       startChatActivity(context, null, null);
    }

    public static void showChatActvity(Context context, String userId, String displayName) {
        Intent intent = new Intent(context, ConversationActivity.class);
        if (!TextUtils.isEmpty(userId)) {
            intent.putExtra(ConversationUIService.USER_ID, userId);
        }
        if (!TextUtils.isEmpty(displayName)) {
            intent.putExtra(ConversationUIService.DISPLAY_NAME, displayName);
        }
        context.startActivity(intent);
    }

    public static void gcmRegister(Context context, String pushnotificationId) {
        PushNotificationTask pushNotificationTask = null;
        PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse) {
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
            }
        };

        pushNotificationTask = new PushNotificationTask(pushnotificationId, listener, context);
        pushNotificationTask.execute((Void) null);

    }

}
