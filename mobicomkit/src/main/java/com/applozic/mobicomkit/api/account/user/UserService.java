package com.applozic.mobicomkit.api.account.user;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.SyncBlockUserApiResponse;
import com.applozic.mobicomkit.sync.SyncUserBlockFeed;
import com.applozic.mobicomkit.sync.SyncUserBlockListFeed;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.List;

/**
 * Created by sunil on 17/3/16.
 */
public class UserService {

    Context context;
    UserClientService userClientService;
    private static UserService userService;
    private MobiComUserPreference userPreference;
    BaseContactService baseContactService;

    private UserService(Context context) {
        this.context = context;
        userClientService = new UserClientService(context);
        userPreference = MobiComUserPreference.getInstance(context);
        baseContactService = new AppContactService(context);

    }

    public static UserService getInstance(Context context) {
        if (userService == null) {
            userService = new UserService(context);
        }
        return userService;
    }

    public synchronized void processSyncUserBlock() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncBlockUserApiResponse apiResponse = userClientService.getSyncUserBlockList(userPreference.getUserBlockSyncTime());
                    if (apiResponse != null && SyncBlockUserApiResponse.SUCCESS.equals(apiResponse.getStatus())) {
                        Contact contact = new Contact();
                        SyncUserBlockListFeed syncUserBlockListFeed = apiResponse.getResponse();
                        if (syncUserBlockListFeed != null) {
                            List<SyncUserBlockFeed> blockedToUserList = syncUserBlockListFeed.getBlockedToUserList();
                            List<SyncUserBlockFeed> blockedByUserList = syncUserBlockListFeed.getBlockedByUserList();
                            if (blockedToUserList != null && blockedToUserList.size() > 0) {
                                for (SyncUserBlockFeed syncUserBlockedFeed : blockedToUserList) {
                                    if(syncUserBlockedFeed.getUserBlocked() != null && !TextUtils.isEmpty(syncUserBlockedFeed.getBlockedTo())){
                                        contact.setBlocked(syncUserBlockedFeed.getUserBlocked());
                                        contact.setUserId(syncUserBlockedFeed.getBlockedTo());
                                        baseContactService.upsert(contact);
                                    }
                                }
                            }
                            if(blockedByUserList != null && blockedByUserList.size()>0){
                                for (SyncUserBlockFeed syncUserBlockByFeed : blockedByUserList) {
                                    if(syncUserBlockByFeed.getUserBlocked() != null && !TextUtils.isEmpty(syncUserBlockByFeed.getBlockedBy())){
                                        contact.setBlockedBy(syncUserBlockByFeed.getUserBlocked());
                                        contact.setUserId(syncUserBlockByFeed.getBlockedBy());
                                        baseContactService.upsert(contact);
                                    }
                                }
                            }
                        }
                        userPreference.setUserBlockSyncTime(apiResponse.getGeneratedAt());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
    }

    public String processUserBlock(String userId) {
        ApiResponse apiResponse = userClientService.userBlock(userId);
        if (apiResponse != null && apiResponse.isSuccess()) {
            baseContactService.updateUserBlocked(userId, true);
            return apiResponse.getStatus();
        }
        return null;
    }

    public String processUserUnBlockUser(String userId) {
        ApiResponse apiResponse = userClientService.userUnBlock(userId);
        if (apiResponse != null && apiResponse.isSuccess()) {
            baseContactService.updateUserBlocked(userId, false);
            return apiResponse.getStatus();
        }
        return null;
    }

}
