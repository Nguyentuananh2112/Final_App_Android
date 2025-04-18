package com.example.finalcampusexpensemanager.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.finalcampusexpensemanager.model.BudgetModel;
import com.example.finalcampusexpensemanager.model.CategoryModel;
import com.example.finalcampusexpensemanager.model.ExpenseModel;
import com.example.finalcampusexpensemanager.model.UserModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "campus_expenses";
    public static final int DB_VERSION = 5;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String USERS_ID = "id";
    public static final String USERS_USERNAME = "username";
    public static final String USERS_PASSWORD = "password";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_PHONE = "phone_number";
    public static final String USERS_ROLE = "role_id";
    public static final String USERS_CREATED_AT = "created_at";
    public static final String USERS_UPDATED_AT = "updated_at";
    public static final String USERS_DELETED_AT = "deleted_at";

    // Bảng categories
    public static final String TABLE_CATEGORIES = "categories";
    public static final String CATEGORIES_ID = "id";
    public static final String CATEGORIES_NAME = "name";
    public static final String CATEGORIES_DESCRIPTION = "description";
    public static final String CATEGORIES_CREATED_AT = "created_at";
    public static final String CATEGORIES_UPDATED_AT = "updated_at";

    // Bảng expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String EXPENSES_ID = "id";
    public static final String EXPENSES_USER_ID = "user_id";
    public static final String EXPENSES_CATEGORY_ID = "category_id";
    public static final String EXPENSES_DESCRIPTION = "description";
    public static final String EXPENSES_DATE = "date";
    public static final String EXPENSES_AMOUNT = "amount";
    public static final String EXPENSES_IS_RECURRING = "is_recurring";
    public static final String EXPENSES_RECURRENCE_INTERVAL = "recurrence_interval";
    public static final String EXPENSES_START_DATE = "start_date";
    public static final String EXPENSES_END_DATE = "end_date";
    public static final String EXPENSES_TYPE = "type"; // Thêm cột type
    public static final String EXPENSES_CREATED_AT = "created_at";
    public static final String EXPENSES_UPDATED_AT = "updated_at";

    // Bảng budgets
    public static final String TABLE_BUDGETS = "budgets";
    public static final String BUDGETS_ID = "id";
    public static final String BUDGETS_USER_ID = "user_id";
    public static final String BUDGETS_CATEGORY_ID = "category_id";
    public static final String BUDGETS_MONTH = "month";
    public static final String BUDGETS_AMOUNT = "budget_amount";
    public static final String BUDGETS_CREATED_AT = "created_at";
    public static final String BUDGETS_UPDATED_AT = "updated_at";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERS_USERNAME + " VARCHAR(60) NOT NULL, "
                + USERS_PASSWORD + " VARCHAR(200) NOT NULL, "
                + USERS_EMAIL + " VARCHAR(60) NOT NULL, "
                + USERS_PHONE + " VARCHAR(30) NOT NULL, "
                + USERS_ROLE + " INTEGER, "
                + USERS_CREATED_AT + " TEXT, "
                + USERS_UPDATED_AT + " TEXT, "
                + USERS_DELETED_AT + " TEXT )";
        db.execSQL(createUsersTable);

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + " ("
                + CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CATEGORIES_NAME + " VARCHAR(60) NOT NULL, "
                + CATEGORIES_DESCRIPTION + " TEXT, "
                + CATEGORIES_CREATED_AT + " TEXT, "
                + CATEGORIES_UPDATED_AT + " TEXT )";
        db.execSQL(createCategoriesTable);

        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + " ("
                + EXPENSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXPENSES_USER_ID + " INTEGER NOT NULL, "
                + EXPENSES_CATEGORY_ID + " INTEGER NOT NULL, "
                + EXPENSES_DESCRIPTION + " TEXT, "
                + EXPENSES_DATE + " TEXT NOT NULL, "
                + EXPENSES_AMOUNT + " INTEGER NOT NULL, "
                + EXPENSES_IS_RECURRING + " INTEGER DEFAULT 0, "
                + EXPENSES_RECURRENCE_INTERVAL + " TEXT, "
                + EXPENSES_START_DATE + " TEXT, "
                + EXPENSES_END_DATE + " TEXT, "
                + EXPENSES_TYPE + " TEXT NOT NULL, " // Thêm cột type
                + EXPENSES_CREATED_AT + " TEXT, "
                + EXPENSES_UPDATED_AT + " TEXT, "
                + "FOREIGN KEY (" + EXPENSES_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + "), "
                + "FOREIGN KEY (" + EXPENSES_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") )";
        db.execSQL(createExpensesTable);

        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + " ("
                + BUDGETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BUDGETS_USER_ID + " INTEGER NOT NULL, "
                + BUDGETS_CATEGORY_ID + " INTEGER NOT NULL, "
                + BUDGETS_MONTH + " TEXT NOT NULL, "
                + BUDGETS_AMOUNT + " INTEGER NOT NULL, "
                + BUDGETS_CREATED_AT + " TEXT, "
                + BUDGETS_UPDATED_AT + " TEXT, "
                + "FOREIGN KEY (" + BUDGETS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + "), "
                + "FOREIGN KEY (" + BUDGETS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + ") )";
        db.execSQL(createBudgetsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            // Thêm cột type vào bảng expenses nếu version cũ hơn 5
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + EXPENSES_TYPE + " TEXT NOT NULL DEFAULT 'Expense'");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        onCreate(db);


    }

    // Insert Expense với type
    public long insertExpense(int userId, int categoryId, String description, String date, int amount, boolean isRecurring, String recurrenceInterval, String startDate, String endDate, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateNow = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSES_USER_ID, userId);
        values.put(EXPENSES_CATEGORY_ID, categoryId);
        values.put(EXPENSES_DESCRIPTION, description);
        values.put(EXPENSES_DATE, date);
        values.put(EXPENSES_AMOUNT, amount);
        values.put(EXPENSES_IS_RECURRING, isRecurring ? 1 : 0);
        values.put(EXPENSES_RECURRENCE_INTERVAL, recurrenceInterval);
        values.put(EXPENSES_START_DATE, startDate);
        values.put(EXPENSES_END_DATE, endDate);
        values.put(EXPENSES_TYPE, type); // Thêm type
        values.put(EXPENSES_CREATED_AT, dateNow);
        long insert = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return insert;
    }


    // Phương thức từ UserDb
    public long insertUserToDatabase(String username, String password, String email, String phone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateNow = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_USERNAME, username);
        values.put(USERS_PASSWORD, password);
        values.put(USERS_EMAIL, email);
        values.put(USERS_PHONE, phone);
        values.put(USERS_ROLE, 0);
        values.put(USERS_CREATED_AT, dateNow);
        long insert = db.insert(TABLE_USERS, null, values);
        db.close();
        return insert;
    }

    public boolean checkUsernameExists(String username) {
        boolean checking = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = {USERS_ID, USERS_USERNAME, USERS_EMAIL, USERS_PHONE, USERS_ROLE};
            String condition = USERS_USERNAME + " =? ";
            String[] params = {username};
            Cursor cursor = db.query(TABLE_USERS, cols, condition, params, null, null, null);
            if (cursor.getCount() > 0) {
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
    public UserModel getInfoUser(String username, String data, int type) {
        UserModel user = new UserModel();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = {USERS_ID, USERS_USERNAME, USERS_EMAIL, USERS_PHONE, USERS_ROLE};
            String[] params = {username, data};
            String condition = (type == 0) ?
                    USERS_USERNAME + " =? AND " + USERS_PASSWORD + " =? " :
                    (USERS_USERNAME + " =? AND " + USERS_EMAIL + " =? ");
            Cursor cursor = db.query(TABLE_USERS, cols, condition, params, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                user.setId(cursor.getInt(cursor.getColumnIndex(USERS_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERS_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(USERS_EMAIL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(USERS_PHONE)));
                user.setRoleId(cursor.getInt(cursor.getColumnIndex(USERS_ROLE)));
            }
            cursor.close();
            db.close();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @SuppressLint("Range")
    public int updateAccountPassword(int idAccount, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERS_PASSWORD, newPassword);
        String condition = USERS_ID + " =? ";
        String[] params = {String.valueOf(idAccount)};
        int update = db.update(TABLE_USERS, values, condition, params);
        db.close();
        return update;
    }

    // Phương thức cho categories
    public long insertCategory(String name, String description) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateNow = sdf.format(new Date());

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if (!db.isOpen()) {
                throw new IllegalStateException("Database is not open");
            }

            ContentValues values = new ContentValues();
            values.put(CATEGORIES_NAME, name);
            values.put(CATEGORIES_DESCRIPTION, description);
            values.put(CATEGORIES_CREATED_AT, dateNow);

            long insert = db.insert(TABLE_CATEGORIES, null, values);
            if (insert == -1) {
                throw new SQLException("Failed to insert category into " + TABLE_CATEGORIES);
            }
            return insert;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting category: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }


    @SuppressLint("Range")
    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryModel category = new CategoryModel();
                category.setId(cursor.getInt(cursor.getColumnIndex(CATEGORIES_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME)));
                category.setDescription(cursor.getString(cursor.getColumnIndex(CATEGORIES_DESCRIPTION)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    // Cập nhật danh mục
    public int updateCategory(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORIES_NAME, name);
        values.put(CATEGORIES_DESCRIPTION, description);
        values.put(CATEGORIES_UPDATED_AT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        int result = db.update(TABLE_CATEGORIES, values, CATEGORIES_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }
    public boolean isCategoryExists(String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + TABLE_CATEGORIES +
                    " WHERE " + CATEGORIES_NAME + " = ? COLLATE NOCASE";
            cursor = db.rawQuery(query, new String[]{categoryName});
            if (cursor.moveToFirst()) {
                return cursor.getInt(0) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    // Xóa danh mục
    public int deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CATEGORIES, CATEGORIES_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // Thêm phương thức xóa giao dịch
    public int deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EXPENSES, EXPENSES_ID + " = ?", new String[]{String.valueOf(expenseId)});
        db.close();
        return result;
    }
    public void printAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);

        if (cursor.getCount() == 0) {
            Log.d("Database", "No categories found in the database");
        } else {
            Log.d("Database", "Total categories: " + cursor.getCount());
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(CATEGORIES_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(CATEGORIES_DESCRIPTION));
                @SuppressLint("Range") String createdAt = cursor.getString(cursor.getColumnIndex(CATEGORIES_CREATED_AT));
                @SuppressLint("Range") String updatedAt = cursor.getString(cursor.getColumnIndex(CATEGORIES_UPDATED_AT));

                Log.d("Database", "ID: " + id + ", Name: " + name + ", Description: " + description +
                        ", Created At: " + createdAt + ", Updated At: " + updatedAt);
            }
        }

        cursor.close();
        db.close();
    }

    // Phương thức cho expenses
    public long insertExpense(int userId, int categoryId, String description, String date, int amount, boolean isRecurring, String recurrenceInterval, String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateNow = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSES_USER_ID, userId);
        values.put(EXPENSES_CATEGORY_ID, categoryId);
        values.put(EXPENSES_DESCRIPTION, description);
        values.put(EXPENSES_DATE, date);
        values.put(EXPENSES_AMOUNT, amount);
        values.put(EXPENSES_IS_RECURRING, isRecurring ? 1 : 0);
        values.put(EXPENSES_RECURRENCE_INTERVAL, recurrenceInterval);
        values.put(EXPENSES_START_DATE, startDate);
        values.put(EXPENSES_END_DATE, endDate);
        values.put(EXPENSES_CREATED_AT, dateNow);
        long insert = db.insert(TABLE_EXPENSES, null, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public List<ExpenseModel> getExpensesByUser(int userId) {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + EXPENSES_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel();
                expense.setId(cursor.getInt(cursor.getColumnIndex(EXPENSES_ID)));
                expense.setUserId(cursor.getInt(cursor.getColumnIndex(EXPENSES_USER_ID)));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndex(EXPENSES_CATEGORY_ID)));
                expense.setDescription(cursor.getString(cursor.getColumnIndex(EXPENSES_DESCRIPTION)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(EXPENSES_DATE)));
                expense.setAmount(cursor.getInt(cursor.getColumnIndex(EXPENSES_AMOUNT)));
                expense.setRecurring(cursor.getInt(cursor.getColumnIndex(EXPENSES_IS_RECURRING)) == 1);
                expense.setRecurrenceInterval(cursor.getString(cursor.getColumnIndex(EXPENSES_RECURRENCE_INTERVAL)));
                expense.setStartDate(cursor.getString(cursor.getColumnIndex(EXPENSES_START_DATE)));
                expense.setEndDate(cursor.getString(cursor.getColumnIndex(EXPENSES_END_DATE)));
                expense.setType(cursor.getString(cursor.getColumnIndex(EXPENSES_TYPE))); // Lấy type
                expense.setCreatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_CREATED_AT)));
                expense.setUpdatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_UPDATED_AT)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    // Phương thức cho budgets
    public long insertBudget(int userId, int categoryId, String month, int budgetAmount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateNow = sdf.format(new Date());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BUDGETS_USER_ID, userId);
        values.put(BUDGETS_CATEGORY_ID, categoryId);
        values.put(BUDGETS_MONTH, month);
        values.put(BUDGETS_AMOUNT, budgetAmount);
        values.put(BUDGETS_CREATED_AT, dateNow);
        long insert = db.insert(TABLE_BUDGETS, null, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public List<BudgetModel> getBudgetsByUser(int userId) {
        List<BudgetModel> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGETS + " WHERE " + BUDGETS_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                BudgetModel budget = new BudgetModel();
                budget.setId(cursor.getInt(cursor.getColumnIndex(BUDGETS_ID)));
                budget.setUserId(cursor.getInt(cursor.getColumnIndex(BUDGETS_USER_ID)));
                budget.setCategoryId(cursor.getInt(cursor.getColumnIndex(BUDGETS_CATEGORY_ID)));
                budget.setMonth(cursor.getString(cursor.getColumnIndex(BUDGETS_MONTH)));
                budget.setBudgetAmount(cursor.getInt(cursor.getColumnIndex(BUDGETS_AMOUNT)));
                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgets;
    }
    //report
    @SuppressLint("Range")
    public List<ExpenseModel> getExpensesByUserAndDateRange(int userId, String startDate, String endDate) {
        List<ExpenseModel> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + EXPENSES_USER_ID + " = ? AND " +
                EXPENSES_DATE + " BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), startDate, endDate});
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expense = new ExpenseModel();
                expense.setId(cursor.getInt(cursor.getColumnIndex(EXPENSES_ID)));
                expense.setUserId(cursor.getInt(cursor.getColumnIndex(EXPENSES_USER_ID)));
                expense.setCategoryId(cursor.getInt(cursor.getColumnIndex(EXPENSES_CATEGORY_ID)));
                expense.setDescription(cursor.getString(cursor.getColumnIndex(EXPENSES_DESCRIPTION)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(EXPENSES_DATE)));
                expense.setAmount(cursor.getInt(cursor.getColumnIndex(EXPENSES_AMOUNT)));
                expense.setRecurring(cursor.getInt(cursor.getColumnIndex(EXPENSES_IS_RECURRING)) == 1);
                expense.setRecurrenceInterval(cursor.getString(cursor.getColumnIndex(EXPENSES_RECURRENCE_INTERVAL)));
                expense.setStartDate(cursor.getString(cursor.getColumnIndex(EXPENSES_START_DATE)));
                expense.setEndDate(cursor.getString(cursor.getColumnIndex(EXPENSES_END_DATE)));
                expense.setType(cursor.getString(cursor.getColumnIndex(EXPENSES_TYPE)));
                expense.setCreatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_CREATED_AT)));
                expense.setUpdatedAt(cursor.getString(cursor.getColumnIndex(EXPENSES_UPDATED_AT)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenses;
    }

    @SuppressLint("Range")
    public String getCategoryName(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + CATEGORIES_NAME + " FROM " + TABLE_CATEGORIES + " WHERE " + CATEGORIES_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        String name = "Unknown";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME));
        }
        cursor.close();
        db.close();
        return name;
    }

    public double getTotalIncome(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + EXPENSES_AMOUNT + ") FROM " + TABLE_EXPENSES + 
                      " WHERE " + EXPENSES_USER_ID + " = ? AND " + EXPENSES_TYPE + " = 'Income'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double getTotalExpense(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + EXPENSES_AMOUNT + ") FROM " + TABLE_EXPENSES + 
                      " WHERE " + EXPENSES_USER_ID + " = ? AND " + EXPENSES_TYPE + " = 'Expense'";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }
}