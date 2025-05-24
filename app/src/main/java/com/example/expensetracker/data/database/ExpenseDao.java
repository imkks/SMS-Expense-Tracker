package com.example.expensetracker.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;


import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getAllExpensesWithCategory();
    @Transaction
    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    LiveData<ExpenseWithCategory> getExpenseById(int id);

    @Update
    void update(Expense expense);
    @Transaction
    @Query("SELECT * FROM expenses WHERE strftime('%Y-%m', date / 1000, 'unixepoch') = :yearMonth ORDER BY date DESC")
    LiveData<List<ExpenseWithCategory>> getExpensesByMonth(String yearMonth); // e.g. "2025-05"

    @Query("SELECT DISTINCT strftime('%Y-%m', date / 1000, 'unixepoch') AS monthYear FROM expenses ORDER BY monthYear DESC")
    LiveData<List<String>> getAllExpenseMonths();

}

