package com.example.expensetracker.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.CategoryDao;
import com.example.expensetracker.data.database.ExpenseDao;
import com.example.expensetracker.data.database.MerchantCategoryDao;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.data.model.MerchantCategory;

import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private ExpenseDao expenseDao;
    private CategoryDao categoryDao;

    private MerchantCategoryDao merchantCategoryDao;
    private LiveData<List<ExpenseWithCategory>> allExpenses;

    public ExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.expenseDao();
        merchantCategoryDao= db.merchantCategoryDao();
        categoryDao= db.categoryDao();
        allExpenses = expenseDao.getAllExpensesWithCategory();
    }

    public void insert(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() ->{
            updateMerchantPreference(expense.merchant,expense.categoryId);
        expenseDao.insert(expense);
    });}
    public void update(Expense expense) {
        Executors.newSingleThreadExecutor().execute(() ->{
            updateMerchantPreference(expense.merchant,expense.categoryId);
            expenseDao.update(expense);
        });}
    public void updateMerchantPreference(String merchant, int categoryId)
    {
        Executors.newSingleThreadExecutor().execute(() -> merchantCategoryDao.insertOrUpdate(new MerchantCategory(merchant,categoryId)));

    }

    public LiveData<List<ExpenseWithCategory>> getAllExpenses() {
        return allExpenses;
    }
    public LiveData<ExpenseWithCategory> getExpensesById(int id) {
        return expenseDao.getExpenseById(id);

    }
    public LiveData<List<ExpenseWithCategory>> getExpensesByMonth(String yearMonth) {
        return expenseDao.getExpensesByMonth(yearMonth);
    }
    public LiveData<List<String>> getAllExpenseMonths() {
        return expenseDao.getAllExpenseMonths();
    }

    public int resolveCategoryIdForMerchant(String merchant) {
        Integer categoryId = merchantCategoryDao.getCategoryIdForMerchant(merchant);

        if (categoryId != null) {
            return categoryId;
        } else {
            // Get or insert default category
            Integer defaultId = categoryDao.getIdByName("Uncategorized");
//            if (defaultId == null) {
//                defaultId = (int) categoryDao.insert(new Category("Uncategorized"));
//            }

            // Remember it for this merchant
//            merchantCategoryDao.insertOrUpdate(new MerchantCategory(merchant, defaultId));

            return defaultId;
        }
    }

}
