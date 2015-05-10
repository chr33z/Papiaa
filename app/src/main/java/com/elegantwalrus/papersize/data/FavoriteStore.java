package com.elegantwalrus.papersize.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.elegantwalrus.papersize.paper.Paper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 09.05.15.
 */
public class FavoriteStore {

    // Database fields
    private SQLiteDatabase database;
    private FavoriteStoreHelper dbHelper;
    private String[] allColumns = { FavoriteStoreHelper.COLUMN_ID,
            FavoriteStoreHelper.COLUMN_FAVORITE };

    public FavoriteStore(Context context) {
        dbHelper = new FavoriteStoreHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Add a favorite to the database
     * @param paper
     * @return True if the favorite could be added, false otherwise
     */
    public boolean addFavorite(Paper paper) {
        ContentValues values = new ContentValues();
        values.put(FavoriteStoreHelper.COLUMN_FAVORITE, paper.getId());
        long insertId = database.insert(FavoriteStoreHelper.TABLE_FAVORITE, null, values);

        return insertId != -1;
    }

    /**
     * Delete a favorite by its id
     * @param paper
     * @return True there was a favorite to delete, false otherwise
     */
    public boolean deleteFavorite(Paper paper) {
        int rows = database.delete(FavoriteStoreHelper.TABLE_FAVORITE,
                FavoriteStoreHelper.COLUMN_FAVORITE + "=?", new String[]{ paper.getId() });
        return rows > 0;
    }

    /**
     * @return Get all favorite ids
     */
    public List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();

        Cursor cursor = database.query(FavoriteStoreHelper.TABLE_FAVORITE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            favorites.add(cursor.getString(1));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return favorites;
    }
}
