package com.applozic.mobicomkit.channel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.database.MobiComDatabaseHelper;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 28/12/15.
 */
public class ChannelDatabaseService {

    private static final String TAG = "ChannelDatabaseService";
    private static final String CHANNEL = "channel";
    private static final String CHANNEL_USER_X = "channel_User_X";
    private static ChannelDatabaseService channelDatabaseService;
    private Context context;
    private MobiComUserPreference mobiComUserPreference;
    private MobiComDatabaseHelper dbHelper;

    private ChannelDatabaseService(Context context) {
        this.context = context;
        this.mobiComUserPreference = MobiComUserPreference.getInstance(context);
        this.dbHelper = MobiComDatabaseHelper.getInstance(context);
    }

    public synchronized static ChannelDatabaseService getInstance(Context context) {
        if (channelDatabaseService == null) {
            channelDatabaseService = new ChannelDatabaseService(context);
        }
        return channelDatabaseService;
    }

    public static ChannelUserMapper getChannelUser(Cursor cursor) {
        ChannelUserMapper channelUserMapper = new ChannelUserMapper();
        channelUserMapper.setUserKey(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.USERID)));
        channelUserMapper.setKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_KEY)));
        channelUserMapper.setUnreadCount(cursor.getShort(cursor.getColumnIndex(MobiComDatabaseHelper.UNREAD_COUNT)));
        return channelUserMapper;
    }

    public static List<ChannelUserMapper> getListOfUsers(Cursor cursor) {
        List<ChannelUserMapper> channelUserMapper = new ArrayList<ChannelUserMapper>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                channelUserMapper.add(getChannelUser(cursor));
            } while (cursor.moveToNext());
        }
        return channelUserMapper;
    }

    public void addChannel(Channel channel) {
        try {
            ContentValues contentValues = prepareChannelValues(channel);
            dbHelper.getWritableDatabase().insertWithOnConflict(CHANNEL, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public ContentValues prepareChannelValues(Channel channel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME, channel.getName());
        contentValues.put(MobiComDatabaseHelper.CHANNEL_KEY, channel.getKey());
        contentValues.put(MobiComDatabaseHelper.TYPE, channel.getType());
        contentValues.put(MobiComDatabaseHelper.ADMIN_ID, channel.getAdminKey());
        if (channel.getUserCount() != 0) {
            contentValues.put(MobiComDatabaseHelper.USER_COUNT, channel.getUserCount());
        }
        return contentValues;
    }

    public void addChannelUserMapper(ChannelUserMapper channelUserMapper) {
        try {
            ContentValues contentValues = prepareChannelUserMapperValues(channelUserMapper);
            dbHelper.getWritableDatabase().insertWithOnConflict(CHANNEL_USER_X, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public ContentValues prepareChannelUserMapperValues(ChannelUserMapper channelUserMapper) {
        ContentValues contentValues = new ContentValues();
        if (channelUserMapper != null) {
            if (channelUserMapper.getKey() != null) {
                contentValues.put(MobiComDatabaseHelper.CHANNEL_KEY, channelUserMapper.getKey());
            }
            if (channelUserMapper.getUserKey() != null) {
                contentValues.put(MobiComDatabaseHelper.USERID, channelUserMapper.getUserKey());
            }
            if (channelUserMapper.getUserKey() != null) {
                contentValues.put(MobiComDatabaseHelper.UNREAD_COUNT, channelUserMapper.getUnreadCount());
            }
            if (channelUserMapper.getStatus() != 0) {
                contentValues.put(MobiComDatabaseHelper.STATUS, channelUserMapper.getStatus());
            }
        }
        return contentValues;
    }

    public Channel getChannelByChannelKey(final Integer channelKey) {
        Channel channel = null;
        try {
            String structuredNameWhere = MobiComDatabaseHelper.CHANNEL_KEY + " =?";
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(CHANNEL, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    channel = getChannel(cursor);
                }
                cursor.close();

            }
            dbHelper.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    public List<ChannelUserMapper> getChannelUserList(Integer channelKey) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String structuredNameWhere = "";

            structuredNameWhere += "channelKey = ?";
            Cursor cursor = db.query(CHANNEL_USER_X, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);

            List<ChannelUserMapper> channelUserMappers = getListOfUsers(cursor);

            cursor.close();
            dbHelper.close();

            return channelUserMappers;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Channel getChannel(Cursor cursor) {
        Channel channel = new Channel();
        channel.setKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_KEY)));
        channel.setName(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME)));
        channel.setAdminKey(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.ADMIN_ID)));
        channel.setType(cursor.getShort(cursor.getColumnIndex(MobiComDatabaseHelper.TYPE)));
        return channel;
    }

    public List<Channel> getAllChannels() {
        List<Channel> contactList = null;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(CHANNEL, null, null, null, null, null, MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME + " asc");
            contactList = getChannelList(cursor);
            cursor.close();
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactList;
    }

    public List<Channel> getChannelList(Cursor cursor) {

        List<Channel> channelList = new ArrayList<Channel>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                channelList.add(getChannel(cursor));
            } while (cursor.moveToNext());
        }
        return channelList;
    }

    public void updateChannel(Channel channel) {
        ContentValues contentValues = prepareChannelValues(channel);
        dbHelper.getWritableDatabase().update(CHANNEL, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?", new String[]{String.valueOf(channel.getKey())});
        dbHelper.close();
    }

    public void updateChannel(ChannelUserMapper channelUserMapper) {
        ContentValues contentValues = prepareChannelUserMapperValues(channelUserMapper);
        dbHelper.getWritableDatabase().update(CHANNEL_USER_X, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?  and " + MobiComDatabaseHelper.USERID + "=?", new String[]{String.valueOf(channelUserMapper.getKey()), String.valueOf(channelUserMapper.getUserKey())});
        dbHelper.close();
    }

    public boolean isChannelPresent(Integer channelKey) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM channel WHERE channelKey=?", new String[]{String.valueOf(channelKey)});
        cursor.moveToFirst();
        boolean present = cursor.getInt(0) > 0;
        if (cursor != null) {
            cursor.close();
        }
        dbHelper.close();
        return present;
    }

    public boolean isChannelUserPresent(Integer channelKey, String userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM channel_User_X WHERE " + MobiComDatabaseHelper.CHANNEL_KEY + "=? and " + MobiComDatabaseHelper.USERID + "=?",
                new String[]{String.valueOf(channelKey), String.valueOf(userId)});
        cursor.moveToFirst();
        boolean present = cursor.getInt(0) > 0;
        if (cursor != null) {
            cursor.close();
        }
        dbHelper.close();
        return present;
    }

}
