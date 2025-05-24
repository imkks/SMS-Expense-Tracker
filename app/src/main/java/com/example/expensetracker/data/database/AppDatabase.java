package com.example.expensetracker.data.database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.MerchantCategory;
import com.example.expensetracker.data.model.SmsHash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;

@Database(entities = {Expense.class, Category.class, MerchantCategory.class, SmsHash.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ExpenseDao expenseDao();
    public abstract CategoryDao categoryDao();
    public abstract SmsHashDao smsHashDao();

    public abstract MerchantCategoryDao merchantCategoryDao();



    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
//            Log.d("AppDatabase", "Database created. Attempting to insert default category...");

            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "expense_database")
                    .fallbackToDestructiveMigration(true)
                    .addCallback(roomCallback)
//                    .addCallback(new Callback() {
//                        @Override
//                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                            super.onCreate(db);
//                            Log.d("AppDatabase", "Database created. Attempting to insert default category...");
//
//                            Executors.newSingleThreadExecutor().execute(() -> {
//
//                                CategoryDao categoryDao = instance.categoryDao();
//                                Log.d("AppDatabase", "De 'Uncategorized' inserted successfully"+categoryDao.toString());
//
//                                categoryDao.insert(new Category("Uncategorized"));
//                            });
//                        }
//                    })
                    .build();
        }
        return instance;
    }
    private static final Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            Executors.newSingleThreadExecutor().execute(() -> {

                CategoryDao categoryDao = getInstance(null).categoryDao();
                categoryDao.insertAll(Arrays.asList(new Category("Uncategorized"),
                        new Category("Grocery"),
                        new Category("Shopping"),
                        new Category("Travel"),
                        new Category("Bills"),
                        new Category("Entertainment")
                ));
            });
        }
    };
}
