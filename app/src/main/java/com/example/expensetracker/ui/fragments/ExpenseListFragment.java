package com.example.expensetracker.ui.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.data.model.Expense;
import com.example.expensetracker.data.model.ExpenseWithCategory;
import com.example.expensetracker.ui.main.ExpenseAdapter;
import com.example.expensetracker.ui.main.ExpenseViewModel;
import com.example.expensetracker.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ExpenseListFragment extends Fragment {
    private ExpenseViewModel expenseViewModel;
    private TextView tvTotal;        TextView tvSelectedMonth;
    private ExpenseAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);
        tvTotal = view.findViewById(R.id.tvTotalExpense);
         tvSelectedMonth = view.findViewById(R.id.tvSelectedMonth);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

// Show current month by default
        tvSelectedMonth.setText(displayFormat.format(calendar.getTime()));
        expenseViewModel.setMonth(apiFormat.format(calendar.getTime()));

// Click to open DatePickerDialog
//        btnPickMonth.setOnClickListener(v -> {
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//
//            DatePickerDialog dpd = new DatePickerDialog(getContext(),
//                    (view1, selYear, selMonth, selDay) -> {
//                        calendar.set(Calendar.YEAR, selYear);
//                        calendar.set(Calendar.MONTH, selMonth);
//                        String selectedMonthStr = apiFormat.format(calendar.getTime());
//                        tvSelectedMonth.setText(displayFormat.format(calendar.getTime()));
//                        expenseViewModel.setMonth(selectedMonthStr);
//                    }, year, month, 1); // default day = 1
//
//            // 🧼 Hide the day spinner (works on most Android versions)
//            try {
//                ((ViewGroup) ((ViewGroup) dpd.getDatePicker()).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            dpd.show();
//        });
       RecyclerView recyclerView = view.findViewById(R.id.recyclerExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();
        adapter.setOnExpenseClickListener(expenseId -> {
            ExpenseDetailFragment sheet = ExpenseDetailFragment.newInstance(expenseId);
            sheet.show(getParentFragmentManager(), "ExpenseDetail");
        });
        recyclerView.setAdapter(adapter);
//        NavController navController = Navigation.findNavController(view);

//        NavController navController = NavHostFragment.findNavController(ExpenseListFragment.this);

        Button btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);


            navController.navigate(R.id.action_expenseList_to_addExpense);

        });
        Button btnCaregories = view.findViewById(R.id.btnAddCategory);
        btnCaregories.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);


            navController.navigate(R.id.action_expenseList_to_categoryManager);

        });
        expenseViewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            // Update RecyclerView or UI
            adapter.setExpenses(expenses);

            double total = 0;
            for (ExpenseWithCategory e : expenses) {
                total += e.expense.amount;
            }
            tvTotal.setText("Total Spent: ₹" + String.format("%.2f", total));
        });

addMonth(view);


        return view;
    }
    public void addMonth(View view) {
        ChipGroup chipContainer = view.findViewById(R.id.monthChipContainer);
        View btnCalendar = view.findViewById(R.id.btnPickMonth);
        View monthChipSection = view.findViewById(R.id.monthChipSection);

        btnCalendar.setOnClickListener(v -> {
            if (monthChipSection.getVisibility() == View.GONE) {
                monthChipSection.setVisibility(View.VISIBLE);
                monthChipSection.setAlpha(0f);
                monthChipSection.animate().alpha(1f).setDuration(200).start();
            } else {
                monthChipSection.animate().alpha(0f).setDuration(200).withEndAction(() ->
                        monthChipSection.setVisibility(View.GONE)).start();
            }
        });

        expenseViewModel.getAvailableMonths().observe(getViewLifecycleOwner(), monthList -> {
            chipContainer.removeAllViews();
            if (monthList == null || monthList.isEmpty()) return;

            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            SimpleDateFormat outFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());

            for (int i = 0; i < monthList.size(); i++) {
                String ym = monthList.get(i);
                String display;
                try {
                    Date date = inFormat.parse(ym);
                    display = outFormat.format(date);
                } catch (ParseException e) {
                    display = ym;
                }

                Chip chip = new Chip(getContext());
                chip.setText(display);
                chip.setTag(ym);
                chip.setCheckable(true);

                chip.setOnClickListener(v -> {
                    for (int j = 0; j < chipContainer.getChildCount(); j++) {
                        Chip child = (Chip) chipContainer.getChildAt(j);
                        child.setChecked(false);
                    }
                    chip.setChecked(true);
                    String selectedYM = (String) v.getTag();
                    tvSelectedMonth.setText(((Chip) v).getText());
                    expenseViewModel.setMonth(selectedYM);
                });

                chipContainer.addView(chip);

                if (i == 0) {
                    chip.performClick();
                }
            }
        });
    }
}
