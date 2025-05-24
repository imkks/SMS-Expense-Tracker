package com.example.expensetracker.ui.fragments;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class ExpenseDetailFragment extends BottomSheetDialogFragment {
    private Spinner categorySpinner;
//    private int expenseId;
    private List<Category> categoryList;
    public static ExpenseDetailFragment newInstance(int expenseId) {
        ExpenseDetailFragment fragment = new ExpenseDetailFragment();
        Bundle args = new Bundle();
        args.putInt("expenseId", expenseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_details, container, false);

        TextView tvMerchant = view.findViewById(R.id.tvMerchant);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvDate = view.findViewById(R.id.tvDate);
        categorySpinner = view.findViewById(R.id.spinnerEditCategory);
        Button btnUpdate = view.findViewById(R.id.btnUpdateExpense);

        int expenseId = getArguments() != null ? getArguments().getInt("expenseId") : -1;
        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

//        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
//            categoryList = categoryViewModel.getAllCategoriesStatic();
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Category> categories = categoryViewModel.getAllCategoriesStatic();

            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                categorySpinner.setAdapter(adapter);
            });
        });
//            ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(),
//                    android.R.layout.simple_spinner_item, categoryList!=null?categoryList:new ArrayList<>());
//            categorySpinner.setAdapter(adapter);
//        });

        expenseViewModel.getExpenseById(expenseId).observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                tvMerchant.setText("Merchant: " + expense.expense.merchant);
                tvAmount.setText("Amount: ₹" + expense.expense.amount);
                tvDate.setText("Date: " + DateFormat.getDateInstance().format(new Date(expense.expense.date)));
//                adapter.get
                // Select the current category in the spinner
                for (int i = 0; i < categorySpinner.getCount(); i++) {
                    Category item = (Category) categorySpinner.getItemAtPosition(i);

                    if (item.id == expense.expense.categoryId) {
                        categorySpinner.setSelection(i);
                        break;
                    }
                }

                btnUpdate.setOnClickListener(v -> {
                    Category selected = (Category) categorySpinner.getSelectedItem();
                    expense.expense.categoryId = selected.id;
                    expenseViewModel.updateExpense(expense.expense);
                    Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
//                    NavHostFragment.findNavController(ExpenseListFragment.this)
//                            .navigate(R.id.action_addExpense_to_expenseList);
                });
            }
        });

        return view;
    }
}

