//package com.example.finalcampusexpensemanager.db;
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import com.example.finalcampusexpensemanager.model.ExpenseModel;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class ExpenseDb {
//    private static final String DB_NAME = "campus_expenses";
//    private static final int DB_VERSION = 1;
//    private static final String TABLE_EXPENSES = "expenses";
//
//    // Các cột trong bảng expenses
//    private static final String EXPENSES_ID = "id";
//    private static final String EXPENSES_USER_ID = "user_id";
//    private static final String EXPENSES_CATEGORY_ID = "category_id";
//    private static final String EXPENSES_DESCRIPTION = "description";
//    private static final String EXPENSES_DATE = "date";
//    private static final String EXPENSES_AMOUNT = "amount";
//    private static final String EXPENSES_TYPE = "type"; // Thêm cột type để phân biệt Income/Expense
//    private static final String EXPENSES_CREATED_AT = "created_at";
//    private static final String EXPENSES_UPDATED_AT = "updated_at";
//
//    private DatabaseHelper dbHelper;
//
//    public ExpenseDb(Context context) {
//        dbHelper = new DatabaseHelper(context);
//    }
//
//    // Thêm một chi tiêu/thu nhập mới
//    public long addExpense(int userId, int categoryId, String description, String date, int amount, String type) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        String dateNow = sdf.format(new Date());
//
//        ContentValues values = new ContentValues();
//        values.put(EXPENSES_USER_ID, userId);
//        values.put(EXPENSES_CATEGORY_ID, categoryId);
//        values.put(EXPENSES_DESCRIPTION, description);
//        values.put(EXPENSES_DATE, date);
//        values.put(EXPENSES_AMOUNT, amount);
//        values.put(EXPENSES_TYPE, type); // "Income" hoặc "Expense"
//        values.put(EXPENSES_CREATED_AT, dateNow);
//        values.put(EXPENSES_UPDATED_AT, dateNow);
//
//        long result = db.insert(TABLE_EXPENSES, null, values);
//        db.close();
//        return result; // Trả về ID của bản ghi mới, hoặc -1 nếu thất bại
//    }
//
//    // Lấy danh sách chi tiêu/thu nhập theo userId
//    @SuppressLint("Range")
//    public List<ExpenseModel> getExpensesByUser(int userId) {
//        List<ExpenseModel> expenses = new ArrayList<>();
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + EXPENSES_USER_ID + " = ?";
//        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
//
//        if (cursor.moveToFirst()) {
//            do {
//                ExpenseModel expense = new ExpenseModel();
//                expense.setId(cursor.getInt(cursor.getColumnIndex(EXPENSES_ID)));
//                expense.setUserId(cursor.getInt(cursor.getColumnIndex(EXPENSES_USER_ID)));
//                expense.setCategoryId(cursor.getInt(cursor.getColumnIndex(EXPENSES_CATEGORY_ID)));
//                expense.setDescription(cursor.getString(cursor.getColumnIndex(EXPENSES_DESCRIPTION)));
//                expense.setDate(cursor.getString(cursor.getColumnIndex(EXPENSES_DATE)));
//                expense.setAmount(cursor.getInt(cursor.getColumnIndex(EXPENSES_AMOUNT)));
//                expense.setType(cursor.getString(cursor.getColumnIndex(EXPENSES_TYPE)));
//                expense.setCreatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_CREATED_AT)));
//                expense.setUpdatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_UPDATED_AT)));
//                expenses.add(expense);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return expenses;
//    }
//
//    // Xóa một bản ghi chi tiêu/thu nhập theo ID
//    public boolean deleteExpense(int expenseId) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        int rowsDeleted = db.delete(TABLE_EXPENSES, EXPENSES_ID + " = ?", new String[]{String.valueOf(expenseId)});
//        db.close();
//        return rowsDeleted > 0;
//    }
//
//    // Cập nhật một bản ghi chi tiêu/thu nhập
//    public boolean updateExpense(int expenseId, int categoryId, String description, String date, int amount, String type) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        String dateNow = sdf.format(new Date());
//
//        ContentValues values = new ContentValues();
//        values.put(EXPENSES_CATEGORY_ID, categoryId);
//        values.put(EXPENSES_DESCRIPTION, description);
//        values.put(EXPENSES_DATE, date);
//        values.put(EXPENSES_AMOUNT, amount);
//        values.put(EXPENSES_TYPE, type);
//        values.put(EXPENSES_UPDATED_AT, dateNow);
//
//        int rowsUpdated = db.update(TABLE_EXPENSES, values, EXPENSES_ID + " = ?", new String[]{String.valueOf(expenseId)});
//        db.close();
//        return rowsUpdated > 0;
//    }
//}