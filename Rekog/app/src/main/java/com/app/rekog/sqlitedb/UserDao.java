package com.app.rekog.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

/**
 * User table dao
 */
public class UserDao extends DBHelper {

    // Table Name
    private static final String TABLE_NAME = "tbl_users";

    // Column Names
    private static final String KEY_SERIAL_ID = "_id";

    private static final String KEY_NAME = "key_name";

    private static final String KEY_EMAIL = "key_email";

    private static final String KEY_TIMESTAMP = "key_timestamp";

    // Create Table Query
    static final String CREATE_USER_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_SERIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_EMAIL + " TEXT, " + KEY_NAME
            + " TEXT, " + KEY_TIMESTAMP + " TEXT)";

    private static UserDao mInstance;

    private SQLiteDatabase localSQLiteDatabase = null;

    private Context mContext = null;

    public static synchronized UserDao getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserDao(context);
        }
        return mInstance;
    }

    private UserDao(Context paramContext) {
        this.mContext = paramContext;
        localSQLiteDatabase = openDB();
    }

    /**
     * Get user info by user id
     *
     * @param email User id of the user
     * @return user model
     */
    public ArrayList<UserModel> getUserInfo(String email) {
        ArrayList<UserModel> userModelArrayList = new ArrayList<>();
        Cursor cursor = localSQLiteDatabase.query(TABLE_NAME, null, KEY_EMAIL + "=?",
                new String[]{email}, null, null, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    final UserModel dataBean = new UserModel();
                    dataBean.name = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
                    dataBean.timestamp = cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP));
                    dataBean.name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    userModelArrayList.add(dataBean);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return userModelArrayList;
    }

    /**
     * Check user exists in db or not
     *
     * @param email User id of the user
     * @return User exists or not
     */
    private boolean checkMemberExists(String email) {
        Cursor cursor = localSQLiteDatabase.query(TABLE_NAME, null, KEY_EMAIL + "=?",
                new String[]{email}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    @Override
    public synchronized void deleteAllData() {
        localSQLiteDatabase.delete(TABLE_NAME, null, null);
    }

    @Override
    public SQLiteDatabase openDB() {
        return DatabaseHelper.getDatabaseHelperInstance(mContext)
                .getReadableDatabase();
    }

    /**
     * Insert users list in database
     *
     * @param dataBeanArrayList List of users
     */
    public void insertUsers(ArrayList<UserModel> dataBeanArrayList) {
        localSQLiteDatabase.beginTransaction();
        try {

            for (int i = 0; i < dataBeanArrayList.size(); i++) {
                ContentValues localContentValues = new ContentValues();
                localContentValues.put(KEY_NAME, dataBeanArrayList.get(i).name);
                localContentValues.put(KEY_TIMESTAMP, dataBeanArrayList.get(i).timestamp);
                localContentValues.put(KEY_EMAIL, dataBeanArrayList.get(i).email);

                long key = localSQLiteDatabase.insert(UserDao.TABLE_NAME, null, localContentValues);
            }
            localSQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            localSQLiteDatabase.endTransaction();
        }
    }
}
