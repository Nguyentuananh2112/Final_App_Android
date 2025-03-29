package com.example.finalcampusexpensemanager.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.example.finalcampusexpensemanager.model.UserModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserDb extends SQLiteOpenHelper {
    public static final String DB_NAME = "campus_expenses";
    public static final String DB_TABLE = "users";
    public static final int DB_VERSION = 4;

    // dinh nghia cac cot trong table
    public static final String ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String PASSWORD_COL = "password";
    public static final String EMAIL_COL = "email";
    public static final String PHONE_COL = "phone_number";
    public static final String ROLE_COL = "role_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String DELETED_AT = "deleted_at";




    public UserDb(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create db va table
        String query = "CREATE TABLE " + DB_TABLE + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " VARCHAR(60) NOT NULL, "
                + PASSWORD_COL + " VARCHAR(200) NOT NULL, "
                + EMAIL_COL + " VARCHAR(60) NOT NULL, "
                + PHONE_COL + " VARCHAR(30) NOT NULL, "
                + ROLE_COL + " INTEGER, "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME, "
                + DELETED_AT + " DATETIME ) ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // xoa du lieu khi error
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }
    // insert data vao db
    public long insertUserToDatabase(String username, String password, String email, String phone){

        // insert created_at
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        @SuppressLint({"NewApi", "LocalSuppress"}) ZonedDateTime now = ZonedDateTime.now();
        @SuppressLint({"NewApi", "LocalSuppress"}) String dateNow = dft.format(now);

        // dc phep ghi du lieu
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);
        values.put(ROLE_COL, 0);
        values.put(CREATED_AT, dateNow);
        long insert = db.insert(DB_TABLE, null, values);
        db.close();
        return insert;
    }



    public boolean checkUsernameExists (String username){
        boolean checking = false;
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = {ID_COL, USERNAME_COL, EMAIL_COL, PHONE_COL,ROLE_COL};
            String condition = USERNAME_COL + " =? ";
            String[] params = { username};
            Cursor cursor = db.query(DB_TABLE, cols, condition, params, null, null, null);
            if (cursor.getCount()>0){
                checking = true;
            }
            cursor.close();
            db.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return checking;
    }

    @SuppressLint("Range")
    public UserModel getInfoUser(String username, String data, int type){
        UserModel user = new UserModel();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = {ID_COL, USERNAME_COL, EMAIL_COL, PHONE_COL, ROLE_COL};
            // SELECT id, username, email, phone, from users WHERE username = ? AND password = ? LIMIT 1;
            String[] params = {username, data};

            String condition = (type == 0) ?
                    USERNAME_COL + " =? AND " + PASSWORD_COL + " =? " :
                    (USERNAME_COL + " =? AND " + EMAIL_COL + " =? ");

            Cursor cursor = db.query(DB_TABLE, cols, condition, params, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                user.setId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME_COL)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_COL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE_COL)));
                user.setRoleId(cursor.getInt(cursor.getColumnIndex(ROLE_COL)));

            }
            cursor.close();
            db.close();

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return user;
    }


    // Update password
    @SuppressLint("Range")
    public int updateAccountPassword(int idAccount, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_COL, newPassword);
        String condition = ID_COL + " =? ";
        String id = String.valueOf(idAccount);
        String[] params = { id };
        int update = db.update(DB_TABLE, values, condition, params);
        db.close();
        return update;
    }
}