package papersize.chreez.com.papersize.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chris on 09.05.15.
 */
public class FavoriteStoreHelper extends SQLiteOpenHelper {

    public static final String TABLE_FAVORITE = "favorites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FAVORITE = "favorite_format";

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_FAVORITE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_FAVORITE
            + " text not null);";

    public FavoriteStoreHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FavoriteStoreHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        onCreate(db);
    }
}
