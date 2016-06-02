package com.applozic.mobicomkit.contact.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.database.MobiComDatabaseHelper;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by adarsh on 9/7/15.
 */
public class ContactDatabase {

    public static final String CONTACT = "contact";
    private static final String TAG = "ContactDatabaseService";
    Context context = null;
    private MobiComUserPreference userPreferences;
    private MobiComDatabaseHelper dbHelper;

    public ContactDatabase(Context context) {
        this.context = context;
        this.userPreferences = MobiComUserPreference.getInstance(context);
        this.dbHelper = MobiComDatabaseHelper.getInstance(context);
    }


    public Contact getContact(Cursor cursor) {
        return getContact(cursor, null);
    }

    /**
     * Form a single contact from cursor
     *
     * @param cursor
     * @return
     */
    public Contact getContact(Cursor cursor, String primaryKeyAliash) {

        Contact contact = new Contact();
        contact.setFullName(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.FULL_NAME)));
        contact.setUserId(cursor.getString(cursor.getColumnIndex(primaryKeyAliash == null ? MobiComDatabaseHelper.USERID : primaryKeyAliash)));
        contact.setLocalImageUrl(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CONTACT_IMAGE_LOCAL_URI)));
        contact.setImageURL(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CONTACT_IMAGE_URL)));
        contact.setContactNumber(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CONTACT_NO)));
        contact.setApplicationId(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.APPLICATION_ID)));
        Long connected = cursor.getLong(cursor.getColumnIndex(MobiComDatabaseHelper.CONNECTED));
        contact.setConnected(connected != 0 && connected.intValue() == 1);
        contact.setLastSeenAt(cursor.getLong(cursor.getColumnIndex(MobiComDatabaseHelper.LAST_SEEN_AT_TIME)));
        contact.processContactNumbers(context);
        contact.setUnreadCount(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.UNREAD_COUNT)));
        Boolean userBlocked = (cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.BLOCKED)) == 1);
        contact.setBlocked(userBlocked);
        Boolean userBlockedBy = (cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.BLOCKED_BY)) == 1);
        contact.setBlockedBy(userBlockedBy);
        return contact;
    }

    /**
     * Form a single contact details from cursor
     *
     * @param cursor
     * @return
     */
    public List<Contact> getContactList(Cursor cursor) {

        List<Contact> smsList = new ArrayList<Contact>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                smsList.add(getContact(cursor));
            } while (cursor.moveToNext());
        }
        return smsList;
    }

    public List<Contact> getAllContactListExcludingLoggedInUser() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String structuredNameWhere = MobiComDatabaseHelper.USERID + " != ?";
        Cursor cursor = db.query(CONTACT, null, structuredNameWhere, new String[]{MobiComUserPreference.getInstance(context).getUserId()}, null, null, MobiComDatabaseHelper.FULL_NAME + " asc");
        List<Contact> contactList = getContactList(cursor);
        cursor.close();
        dbHelper.close();
        return contactList;
    }

    public List<Contact> getAllContact() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(CONTACT, null, null, null, null, null, MobiComDatabaseHelper.FULL_NAME + " asc");
        List<Contact> contactList = getContactList(cursor);
        cursor.close();
        dbHelper.close();
        return contactList;
    }

    public Contact getContactById(String id) {
        String structuredNameWhere = MobiComDatabaseHelper.USERID + " =?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(CONTACT, null, structuredNameWhere, new String[]{id}, null, null, null);
        Contact contact = null;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                contact = getContact(cursor);
            }
            cursor.close();
        }
        dbHelper.close();
        return contact;

    }

    public void updateContact(Contact contact) {
        ContentValues contentValues = prepareContactValues(contact);
        dbHelper.getWritableDatabase().update(CONTACT, contentValues, MobiComDatabaseHelper.USERID + "=?", new String[]{contact.getUserId()});
        dbHelper.close();
    }

    public void updateConnectedOrDisconnectedStatus(String userId, Date date, boolean connected) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.CONNECTED, connected ? 1 : 0);
        contentValues.put(MobiComDatabaseHelper.LAST_SEEN_AT_TIME, date.getTime());

        try {
            dbHelper.getWritableDatabase().update(CONTACT, contentValues, MobiComDatabaseHelper.USERID + "=?", new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public void updateLastSeenTimeAt(String userId, long lastSeenTime) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MobiComDatabaseHelper.LAST_SEEN_AT_TIME, lastSeenTime);
            dbHelper.getWritableDatabase().update(CONTACT, contentValues, MobiComDatabaseHelper.USERID + "=?", new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public void updateUserBlockStatus(String userId, boolean userBlocked) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MobiComDatabaseHelper.BLOCKED, userBlocked ? 1 : 0);
            int row = dbHelper.getWritableDatabase().update(CONTACT, contentValues, MobiComDatabaseHelper.USERID + "=?", new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public void updateUserBlockByStatus(String userId, boolean userBlockedBy) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MobiComDatabaseHelper.BLOCKED_BY, userBlockedBy ? 1 : 0);
            int row = dbHelper.getWritableDatabase().update(CONTACT, contentValues, MobiComDatabaseHelper.USERID + "=?", new String[]{userId});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    public void addContact(Contact contact) {
        ContentValues contentValues = prepareContactValues(contact);
        dbHelper.getWritableDatabase().insert(CONTACT, null, contentValues);
        dbHelper.close();
    }

    public ContentValues prepareContactValues(Contact contact) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MobiComDatabaseHelper.FULL_NAME, getFullNameForUpdate(contact));
        if(!TextUtils.isEmpty(contact.getContactNumber())){
            contentValues.put(MobiComDatabaseHelper.CONTACT_NO, contact.getContactNumber());
        }
        if (!TextUtils.isEmpty(contact.getImageURL())) {
            contentValues.put(MobiComDatabaseHelper.CONTACT_IMAGE_URL, contact.getImageURL());
        }
        if (!TextUtils.isEmpty(contact.getLocalImageUrl())) {
            contentValues.put(MobiComDatabaseHelper.CONTACT_IMAGE_LOCAL_URI, contact.getLocalImageUrl());
        }
        contentValues.put(MobiComDatabaseHelper.USERID, contact.getUserId());
        if (!TextUtils.isEmpty(contact.getEmailId())) {
            contentValues.put(MobiComDatabaseHelper.EMAIL, contact.getEmailId());
        }
        if (!TextUtils.isEmpty(contact.getApplicationId())) {
            contentValues.put(MobiComDatabaseHelper.APPLICATION_ID, contact.getApplicationId());
        }

        contentValues.put(MobiComDatabaseHelper.CONNECTED, contact.isConnected() ? 1 : 0);
        if (contact.getLastSeenAt() != 0) {
            contentValues.put(MobiComDatabaseHelper.LAST_SEEN_AT_TIME, contact.getLastSeenAt());
        }
        if (contact.getUnreadCount() != null && contact.getUnreadCount() != 0) {
            contentValues.put(MobiComDatabaseHelper.UNREAD_COUNT, contact.getUnreadCount());
        }

        if (contact.isBlocked()) {
            contentValues.put(MobiComDatabaseHelper.BLOCKED, contact.isBlocked());
        }
        if (contact.isBlockedBy()) {
            contentValues.put(MobiComDatabaseHelper.BLOCKED_BY, contact.isBlockedBy());
        }
        return contentValues;
    }

    /**
     * This method will return full name of contact to be updated.
     * This is require to avoid updating fullname back to userId in case fullname is not set while updating contact.
     * @param contact
     * @return
     */
    private String getFullNameForUpdate(Contact contact) {

        String fullName = contact.getDisplayName();
        if (TextUtils.isEmpty(contact.getFullName())) {
            Contact contactFromDB = getContactById(contact.getUserId());
            if (contactFromDB != null) {
                fullName = contactFromDB.getFullName();
            }
        }
        return fullName;
    }

    public boolean isContactPresent(String userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM contact WHERE userId = ?",
                new String[]{userId});
        cursor.moveToFirst();
        boolean present = cursor.getInt(0) > 0;
        if (cursor != null) {
            cursor.close();
        }
        return present;
    }

    public void addAllContact(List<Contact> contactList) {
        for (Contact contact : contactList) {
            addContact(contact);
        }
    }

    public void deleteContact(Contact contact) {
        deleteContactById(contact.getUserId());
    }

    public void deleteContactById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(CONTACT, "userId=?", new String[]{id});
        dbHelper.close();
    }

    public void deleteAllContact(List<Contact> contacts) {
        for (Contact contact : contacts) {
            deleteContact(contact);
        }
    }

    public Loader<Cursor> getSearchCursorLoader(final String searchString,final String[] userIdArray) {

        return new CursorLoader(context, null, null, null, null, MobiComDatabaseHelper.DISPLAY_NAME + " asc") {
            @Override
            public Cursor loadInBackground() {

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor;

                String query = "select userId as _id, fullName, contactNO, " +
                        "displayName,contactImageURL,contactImageLocalURI,email," +
                        "applicationId,connected,lastSeenAt,unreadCount,blocked," +
                        "blockedBy from " + CONTACT;

                if (userIdArray !=  null && userIdArray.length>0) {
                    String placeHolderString = Utils.makePlaceHolders(userIdArray.length);
                    if (!TextUtils.isEmpty(searchString)) {
                        query = query + " where fullName like '%" + searchString + "%' and  userId  IN (" + placeHolderString + ")";
                    } else {
                        query = query + " where userId IN (" + placeHolderString + ")";
                    }
                    query = query + " order by connected desc,lastSeenAt desc ";

                    cursor = db.rawQuery(query, userIdArray);
                } else {
                    if (!TextUtils.isEmpty(searchString)) {
                        query = query + " where fullName like '%" + searchString + "%'";
                    }else {
                        query = query + " where userId != '"+ userPreferences.getUserId()+"'";
                    }
                    query = query + " order by fullName,userId asc ";
                    cursor = db.rawQuery(query, null);
                }

                return cursor;

            }
        };
    }

}
