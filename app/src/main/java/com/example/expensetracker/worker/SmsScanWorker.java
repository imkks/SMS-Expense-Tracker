package com.example.expensetracker.worker;

import android.app.Application;
import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.expensetracker.data.database.AppDatabase;
import com.example.expensetracker.data.database.SmsHashDao;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.SmsHash;
import com.example.expensetracker.data.repository.ExpenseRepository;
import com.example.expensetracker.utils.SmsUtils;

import java.util.HashSet;
import java.util.Set;



public class SmsScanWorker extends Worker {

    private final Context context;

    public SmsScanWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // 1. Load all existing hashes from DB
        SmsHashDao hashDao = AppDatabase.getInstance(context).smsHashDao();
        ExpenseRepository expenseRepository = new ExpenseRepository((Application) context.getApplicationContext());

        Set<String> existingHashes = new HashSet<>(hashDao.getAllHashes());
        long ninetyDaysAgo = System.currentTimeMillis() - (4*90L * 24 * 60 * 60 * 1000);
        String selection = "date >= ?";
        String[] selectionArgs = { String.valueOf(ninetyDaysAgo) };

        // 2. Scan inbox
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uriSms, null, selection, selectionArgs, "date DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                long timestampRounded = (timestamp / (60 * 1000)) * (60 * 1000);

                String bank= SmsUtils.getBank(address);
                if(bank!=null)
                {
                    String raw = address + "|" + body + "|" + timestampRounded;
                    String hash = SmsUtils.generateSmsHash(raw);
//                    Log.d("Worker",String.format("hash %s address %s body %s timestamp %s",hash,address,body,timestamp));

                    if (!existingHashes.contains(hash)) {
                        // 3. Store hash
                        Expense expense = SmsUtils.parseExpense(body, bank);
                        if(expense!=null)
                        {
                            hashDao.insert(new SmsHash(hash));
                            expense.date= timestamp;
                            expense.categoryId=expenseRepository.resolveCategoryIdForMerchant(expense.merchant);
                            expenseRepository.insert(expense);

                        }
                    }

                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        return Result.success();
    }


}

