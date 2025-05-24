package com.example.expensetracker.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.model.Category;
import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.ui.viewmodel.CategoryViewModel;

import java.util.stream.Collectors;

public class AddExpenseFragment extends Fragment {
    private ExpenseViewModel expenseViewModel;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);
        EditText amountInput = view.findViewById(R.id.editAmount);
        EditText payeeInput = view.findViewById(R.id.editMerchant);
        Spinner categorySpinner = view.findViewById(R.id.spinnerCategory);
        Button saveBtn = view.findViewById(R.id.btnSaveExpense);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        CategoryViewModel categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
//            categoryList.clear();
//            categoryList.addAll(categories);

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    categories
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
//                    categories.stream().map(Category::getName).collect(Collectors.toList()));
//            categorySpinner.setAdapter(adapter);
        });

        saveBtn.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString().trim();
            String payee = payeeInput.getText().toString().trim();
            Category selectedCategory = (Category) categorySpinner.getSelectedItem();

            if (!amountStr.isEmpty() && !payee.isEmpty() && selectedCategory !=null) {
                double amount = Double.parseDouble(amountStr);
//                System.out.println(payee);
//                Expense expense = new Expense(amount, payee, System.currentTimeMillis(), "");
                expenseViewModel.insert(amount, payee,selectedCategory.id);
            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AddExpenseFragment.this)
                        .navigate(R.id.action_addExpense_to_expenseList);

            }});

        return view;
    }

}

