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
import java.util.Calendar;
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
    public static final String EXPENSES_NOTE = "note";
    public static final String EXPENSES_RECURRING_TYPE = "recurring_type";
    public static final String EXPENSES_RECURRING_DAY = "recurring_day";
    public static final String EXPENSES_RECURRING_END_DATE = "recurring_end_date";

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
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + USERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USERS_USERNAME + " TEXT,"
                + USERS_PASSWORD + " TEXT,"
                + USERS_EMAIL + " TEXT,"
                + USERS_PHONE + " TEXT,"
                + USERS_ROLE + " INTEGER,"
                + USERS_CREATED_AT + " TEXT,"
                + USERS_UPDATED_AT + " TEXT,"
                + USERS_DELETED_AT + " TEXT)";
        db.execSQL(createUsersTable);

        String createCategoriesTable = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + CATEGORIES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CATEGORIES_NAME + " TEXT,"
                + CATEGORIES_DESCRIPTION + " TEXT,"
                + CATEGORIES_CREATED_AT + " TEXT,"
                + CATEGORIES_UPDATED_AT + " TEXT)";
        db.execSQL(createCategoriesTable);

        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + "("
                + EXPENSES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EXPENSES_USER_ID + " INTEGER,"
                + EXPENSES_CATEGORY_ID + " INTEGER,"
                + EXPENSES_DESCRIPTION + " TEXT,"
                + EXPENSES_DATE + " TEXT,"
                + EXPENSES_AMOUNT + " INTEGER,"
                + EXPENSES_IS_RECURRING + " INTEGER,"
                + EXPENSES_RECURRENCE_INTERVAL + " TEXT,"
                + EXPENSES_START_DATE + " TEXT,"
                + EXPENSES_END_DATE + " TEXT,"
                + EXPENSES_TYPE + " TEXT,"
                + EXPENSES_CREATED_AT + " TEXT,"
                + EXPENSES_UPDATED_AT + " TEXT,"
                + "FOREIGN KEY(" + EXPENSES_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + "),"
                + "FOREIGN KEY(" + EXPENSES_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + "))";
        db.execSQL(createExpensesTable);

        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + "("
                + BUDGETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BUDGETS_USER_ID + " INTEGER,"
                + BUDGETS_CATEGORY_ID + " INTEGER,"
                + BUDGETS_MONTH + " TEXT,"
                + BUDGETS_AMOUNT + " INTEGER,"
                + BUDGETS_CREATED_AT + " TEXT,"
                + BUDGETS_UPDATED_AT + " TEXT,"
                + "FOREIGN KEY(" + BUDGETS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USERS_ID + "),"
                + "FOREIGN KEY(" + BUDGETS_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_ID + "))";
        db.execSQL(createBudgetsTable);

        // Tạo bảng budget_warnings
        String createBudgetWarningsTable = "CREATE TABLE budget_warnings("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER,"
                + "category TEXT,"
                + "amount REAL,"
                + "total_income REAL,"
                + "created_at INTEGER,"
                + "FOREIGN KEY(user_id) REFERENCES users(id))";
        db.execSQL(createBudgetWarningsTable);
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
    public long insertExpense(int userId, int categoryId, String note, String date, int amount, boolean isRecurring, String recurringType, Integer recurringDay, String recurringEndDate, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXPENSES_USER_ID, userId);
        values.put(EXPENSES_CATEGORY_ID, categoryId);
        values.put(EXPENSES_DESCRIPTION, note);
        values.put(EXPENSES_DATE, date);
        values.put(EXPENSES_AMOUNT, amount);
        values.put(EXPENSES_IS_RECURRING, isRecurring ? 1 : 0);
        values.put(EXPENSES_RECURRENCE_INTERVAL, recurringType);
        values.put(EXPENSES_START_DATE, recurringDay != null ? String.valueOf(recurringDay) : null);
        values.put(EXPENSES_END_DATE, recurringEndDate);
        values.put(EXPENSES_TYPE, type);
        values.put(EXPENSES_CREATED_AT, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        return db.insert(TABLE_EXPENSES, null, values);
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

    public List<String> checkBudgetExceeded(int userId) {
        List<String> exceededCategories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Lấy tháng và năm hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        int currentYear = calendar.get(Calendar.YEAR);
        
        // Truy vấn để lấy tổng thu nhập và chi tiêu của tháng hiện tại
        String incomeQuery = "SELECT COALESCE(SUM(amount), 0) as total_income FROM expenses " +
                           "WHERE user_id = ? AND type = 'Income' " +
                           "AND strftime('%m', date) = ? AND strftime('%Y', date) = ?";
        Cursor incomeCursor = db.rawQuery(incomeQuery, new String[]{
            String.valueOf(userId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear)
        });
        
        double totalIncome = 0;
        if (incomeCursor.moveToFirst()) {
            totalIncome = incomeCursor.getDouble(0);
        }
        incomeCursor.close();
        
        String expenseQuery = "SELECT COALESCE(SUM(amount), 0) as total_expense FROM expenses " +
                            "WHERE user_id = ? AND type = 'Expense' " +
                            "AND strftime('%m', date) = ? AND strftime('%Y', date) = ?";
        Cursor expenseCursor = db.rawQuery(expenseQuery, new String[]{
            String.valueOf(userId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear)
        });
        
        double totalExpense = 0;
        if (expenseCursor.moveToFirst()) {
            totalExpense = expenseCursor.getDouble(0);
        }
        expenseCursor.close();
        
        // Kiểm tra nếu tổng chi tiêu vượt quá tổng thu nhập
        if (totalExpense > totalIncome) {
            exceededCategories.add(String.format("Tổng chi tiêu (%.2f) vượt quá tổng thu nhập (%.2f)", 
                totalExpense, totalIncome));
        }
        
        // Kiểm tra từng danh mục ngân sách
        String budgetQuery = "SELECT c.name, b.budget_amount, COALESCE(SUM(e.amount), 0) as total_expense " +
                           "FROM categories c " +
                           "LEFT JOIN budgets b ON c.id = b.category_id AND b.user_id = ? " +
                           "AND b.month = ? AND strftime('%Y', b.created_at) = ? " +
                           "LEFT JOIN expenses e ON c.id = e.category_id AND e.user_id = ? " +
                           "AND strftime('%m', e.date) = ? AND strftime('%Y', e.date) = ? " +
                           "WHERE b.budget_amount > 0 " +
                           "GROUP BY c.id, c.name, b.budget_amount";
        
        Cursor cursor = db.rawQuery(budgetQuery, new String[]{
            String.valueOf(userId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear),
            String.valueOf(userId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear)
        });
        
        while (cursor.moveToNext()) {
            String categoryName = cursor.getString(0);
            double budgetAmount = cursor.getDouble(1);
            double totalCategoryExpense = cursor.getDouble(2);
            
            if (totalCategoryExpense > budgetAmount) {
                exceededCategories.add(String.format("%s: Đã chi %.2f / Ngân sách %.2f", 
                    categoryName, totalCategoryExpense, budgetAmount));
            }
        }
        
        cursor.close();
        return exceededCategories;
    }

    // Thêm phương thức lấy tổng chi tiêu theo danh mục
    public double getTotalExpenseByCategory(int userId, int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalExpense = 0;

        // Lấy tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        int currentYear = calendar.get(Calendar.YEAR);

        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                      "WHERE user_id = ? AND category_id = ? " +
                      "AND type = 'Expense' " +
                      "AND strftime('%m', date) = ? AND strftime('%Y', date) = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(userId),
            String.valueOf(categoryId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear)
        });

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }

        cursor.close();
        return totalExpense;
    }

    public double getTotalIncome(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalIncome = 0;

        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                      "WHERE user_id = ? AND type = 'Income'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        
        // Debug log
        Log.d("DatabaseHelper", "Total Income for user " + userId + ": " + totalIncome);
        
        return totalIncome;
    }

    public double getTotalExpense(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalExpense = 0;

        String query = "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
                      "WHERE user_id = ? AND type = 'Expense'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            totalExpense = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        
        // Debug log
        Log.d("DatabaseHelper", "Total Expense for user " + userId + ": " + totalExpense);
        
        return totalExpense;
    }

    public double getBudgetAmount(int userId, int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double budgetAmount = 0;

        // Lấy tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Tháng bắt đầu từ 0
        int currentYear = calendar.get(Calendar.YEAR);

        String query = "SELECT budget_amount FROM budgets " +
                      "WHERE user_id = ? AND category_id = ? " +
                      "AND month = ? AND strftime('%Y', created_at) = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(userId),
            String.valueOf(categoryId),
            String.format("%02d", currentMonth),
            String.valueOf(currentYear)
        });

        if (cursor.moveToFirst()) {
            budgetAmount = cursor.getDouble(0);
        }

        cursor.close();
        return budgetAmount;
    }

    public void addBudgetWarning(int userId, String category, double amount, double totalIncome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("category", category);
        values.put("amount", amount);
        values.put("total_income", totalIncome);
        values.put("created_at", System.currentTimeMillis());
        db.insert("budget_warnings", null, values);
        db.close();
    }

    public List<String> getBudgetWarnings(int userId) {
        List<String> warnings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("budget_warnings",
                new String[]{"category", "amount", "total_income", "created_at"},
                "user_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, "created_at DESC");

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                double amount = cursor.getDouble(1);
                double totalIncome = cursor.getDouble(2);
                long timestamp = cursor.getLong(3);
                
                String warning = String.format("Danh mục: %s\nSố tiền: %,.0f VND\nThu nhập: %,.0f VND\nThời gian: %s",
                        category, amount, totalIncome, new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(timestamp)));
                warnings.add(warning);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return warnings;
    }
}