package com.applozic.mobicomkit.sync;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunil on 17/3/16.
 */
public class SyncUserBlockListFeed implements Serializable {

    List<SyncUserBlockFeed>  blockedByUserList;
    List<SyncUserBlockFeed>  blockedToUserList;

    public List<SyncUserBlockFeed> getBlockedByUserList() {
        return blockedByUserList;
    }

    public void setBlockedByUserList(List<SyncUserBlockFeed> blockedByUserList) {
        this.blockedByUserList = blockedByUserList;
    }

    public List<SyncUserBlockFeed> getBlockedToUserList() {
        return blockedToUserList;
    }

    public void setBlockedToUserList(List<SyncUserBlockFeed> blockedToUserList) {
        this.blockedToUserList = blockedToUserList;
    }

    @Override
    public String toString() {
        return "SyncUserBlockListFeed{" +
                "blockedByUserList=" + blockedByUserList +
                ", blockedToUserList=" + blockedToUserList +
                '}';
    }
}
