package com.app.rekog.sqlitedb;

import android.database.sqlite.SQLiteDatabase;

/**
 * Abstract DB Helper class
 */
abstract class DBHelper {
    /**
     * Abstract method to get an instance of DB
     *
     * @return An instance of Sqlite DB
     */
    public abstract SQLiteDatabase openDB();

    /**
     * Abstract method to delete/truncate all data in a table
     */
    public abstract void deleteAllData();
}
