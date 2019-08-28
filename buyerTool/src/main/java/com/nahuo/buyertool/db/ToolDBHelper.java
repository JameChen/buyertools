package com.nahuo.buyertool.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nahuo.buyer.tool.R;
import com.nahuo.buyertool.common.BaiduStats;

import java.io.InputStream;

public class ToolDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ToolDBHelper.class.getSimpleName();
    private SQLiteDatabase mDefaultWritableDatabase = null;
    private static final String DATABASE_NAME = "buyer_tool.db";
    private static final int DATABASE_VERSION = 5;
    private Context mContext;

    public ToolDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public ToolDBHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        final SQLiteDatabase db;
        if (mDefaultWritableDatabase != null) {
            db = mDefaultWritableDatabase;
        } else {
            db = super.getWritableDatabase();
        }
        return db;
    }

    // 数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.mDefaultWritableDatabase = db;
        String strSQL = "";

        InputStream in = null;

        // 读取文件
        try {
            in = mContext.getResources().openRawResource(R.raw.sql1);
            // 将文件读入字节数组
            byte[] reader = new byte[in.available()];
            while (in.read(reader) != -1) {
            }
            strSQL = new String(reader, "utf-8");

            String[] strTables = strSQL.split(";");
            // db.beginTransaction();
            for (String strTalbe : strTables) {
                db.execSQL(strTalbe.replace("\r\n", "") + ";");
            }
            // db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            BaiduStats.log(mContext, BaiduStats.EventId.CREATE_DB_FAILED,
                    e.getMessage());
        }
    }


    private void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            db.beginTransaction();
            db.execSQL("delete from upload_tasklist;");
            db.execSQL("delete from all_item_list;");
            db.execSQL("delete from upload_tasklist;");
            db.execSQL("delete from upload_tasklist;");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    // 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, oldVersion + "--->>>" + newVersion);
        try {
            this.mDefaultWritableDatabase = db;
            switch (oldVersion) {
                case 1:
                    version2(db);
                    break;
                case 2:
                    version3(db);
                    break;
                case 3:
                    version4(db);
                    break;
                case 4:
                    version5(db);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            BaiduStats.log(mContext, BaiduStats.EventId.UPDATE_DB_FAILED,
                    oldVersion + "->" + newVersion + ":" + e.getMessage());
        }

    }

    public void version2(SQLiteDatabase db) {
        this.mDefaultWritableDatabase = db;
        try {
            db.beginTransaction();
            db.execSQL("alter table upload_list add color_pics_list text;");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void version3(SQLiteDatabase db) {
        this.mDefaultWritableDatabase = db;
        try {
            db.beginTransaction();
            db.execSQL("alter table upload_list add material_list text ;");
            db.execSQL("alter table upload_list add age_list text;");
            db.execSQL("alter table upload_list add style_list text;");
            db.execSQL("alter table upload_list add season_list text;");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void version4(SQLiteDatabase db) {
        this.mDefaultWritableDatabase = db;
        try {
            db.beginTransaction();
            db.execSQL("alter table upload_list add extend_property_type_list text;");
            db.execSQL("alter table upload_list add remark text;");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void version5(SQLiteDatabase db) {
        this.mDefaultWritableDatabase = db;
        try {
            db.beginTransaction();
            db.execSQL("alter table upload_list add discount DEC(10,2);");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.mDefaultWritableDatabase = db;
    }

    private void execSql(SQLiteDatabase db, String... sqls) {
        try {
            db.beginTransaction();
            for (String sql : sqls) {
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public static String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public static int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public static double getDouble(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    public static boolean getBoolean(Cursor cursor, String column) {
        return getInt(cursor, column) == 0 ? false : true;
    }

}
