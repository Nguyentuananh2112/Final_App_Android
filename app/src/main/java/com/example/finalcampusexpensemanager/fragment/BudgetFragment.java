package com.example.finalcampusexpensemanager.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalcampusexpensemanager.R;
import com.example.finalcampusexpensemanager.adapter.TransactionDetailAdapter;
import com.example.finalcampusexpensemanager.db.DatabaseHelper;
import com.example.finalcampusexpensemanager.model.ExpenseModel;
import com.google.android.material.button.MaterialButton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BudgetFragment extends Fragment {
    private MaterialButton exportDateInput1, exportDateInput2, btnExport, btnExportPdf;
    private TextView tvSummary, tvTransactions, tvSeeAll;
    private DatabaseHelper dbHelper;
    private int userId;
    private String startDate, endDate;
    private List<ExpenseModel> currentExpenses; // Store the current list of expenses for PDF export and dialog

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Initialize views
        exportDateInput1 = view.findViewById(R.id.export_date_input_1);
        exportDateInput2 = view.findViewById(R.id.export_date_input_2);
        btnExport = view.findViewById(R.id.btn_export);
        btnExportPdf = view.findViewById(R.id.btn_export_pdf);
        tvSummary = view.findViewById(R.id.tv_summary);
        tvTransactions = view.findViewById(R.id.tv_transactions);
        tvSeeAll = view.findViewById(R.id.tv_see_all);

        // Initialize database
        dbHelper = new DatabaseHelper(getContext());

        // Get userId from arguments
        if (getArguments() != null) {
            userId = getArguments().getInt("USER_ID", 0);
        }

        // Date picker events
        exportDateInput1.setOnClickListener(v -> showDatePicker(true));
        exportDateInput2.setOnClickListener(v -> showDatePicker(false));

        // Export button event
        btnExport.setOnClickListener(v -> exportTransactionReport());

        // "See All" button event
        tvSeeAll.setOnClickListener(v -> showTransactionDetailsDialog());

        // "Export to PDF" button event
        btnExportPdf.setOnClickListener(v -> exportToPdf());

        return view;
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
            if (isStartDate) {
                startDate = selectedDate;
                exportDateInput1.setText(selectedDate);
            } else {
                endDate = selectedDate;
                exportDateInput2.setText(selectedDate);
            }
        }, year, month, day).show();
    }

    private void exportTransactionReport() {
        if (startDate == null || endDate == null) {
            Toast.makeText(getContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use Locale.US to ensure MM/dd/yyyy format
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            if (sdf.parse(startDate).after(sdf.parse(endDate))) {
                Toast.makeText(getContext(), "Start date must be before end date", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Date format error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Get expenses within the date range
        currentExpenses = dbHelper.getExpensesByUserAndDateRange(userId, startDate, endDate);
        if (currentExpenses.isEmpty()) {
            tvSummary.setText("Total Income: $0\nTotal Expense: $0\nBalance: $0");
            tvTransactions.setText("No transactions available");
            return;
        }

        // Calculate total income, total expense, and balance
        int totalIncome = 0;
        int totalExpense = 0;
        StringBuilder transactionDetails = new StringBuilder();

        for (ExpenseModel expense : currentExpenses) {
            String categoryName = dbHelper.getCategoryName(expense.getCategoryId());
            String description = expense.getDescription() != null && !expense.getDescription().isEmpty() ? expense.getDescription() : "No note";
            transactionDetails.append(String.format(Locale.getDefault(), "%s: $%d [%s] [%s] [%s]\n",
                    expense.getType(), expense.getAmount(), expense.getDate(), categoryName, description));

            if ("Income".equals(expense.getType())) {
                totalIncome += expense.getAmount();
            } else if ("Expense".equals(expense.getType())) {
                totalExpense += expense.getAmount();
            }
        }

        // Update summary
        tvSummary.setText(String.format(Locale.getDefault(),
                "Total Income: $%d\nTotal Expense: $%d\nBalance: $%d",
                totalIncome, totalExpense, totalIncome - totalExpense));

        // Update transaction list
        tvTransactions.setText(transactionDetails.toString());
    }

    private void showTransactionDetailsDialog() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            Toast.makeText(getContext(), "No transactions to display", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transaction_details, null);
        builder.setView(dialogView);

        // Initialize RecyclerView in dialog
        RecyclerView rvTransactionDetails = dialogView.findViewById(R.id.rv_transaction_details);
        TransactionDetailAdapter adapter = new TransactionDetailAdapter(currentExpenses, dbHelper);
        rvTransactionDetails.setAdapter(adapter);

        // Close button in dialog
        dialogView.findViewById(R.id.btn_close).setOnClickListener(v -> {
            AlertDialog dialog = (AlertDialog) v.getTag();
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btn_close).setTag(dialog);
        dialog.show();
    }

    private void exportToPdf() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            Toast.makeText(getContext(), "No transactions to export", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Use MediaStore to save PDF file to Downloads directory
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, "TransactionReport.pdf");
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            ContentResolver resolver = getContext().getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                    // Create PDF
                    Document document = new Document();
                    PdfWriter.getInstance(document, outputStream);
                    document.open();

                    // Define fonts with different sizes
                    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                    Font dateFont = new Font(Font.FontFamily.HELVETICA, 14);
                    Font sectionFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
                    Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);

                    // Add title
                    Paragraph title = new Paragraph("Transaction Report", titleFont);
                    title.setAlignment(Paragraph.ALIGN_CENTER);
                    document.add(title);

                    // Add date range
                    Paragraph dateRange = new Paragraph(String.format("From %s to %s", startDate, endDate), dateFont);
                    document.add(dateRange);

                    // Add summary
                    Paragraph summaryHeader = new Paragraph("\nSummary:", sectionFont);
                    document.add(summaryHeader);
                    Paragraph summaryContent = new Paragraph(tvSummary.getText().toString(), contentFont);
                    document.add(summaryContent);

                    // Add transaction details
                    Paragraph transactionsHeader = new Paragraph("\nTransaction Details:", sectionFont);
                    document.add(transactionsHeader);
                    for (ExpenseModel expense : currentExpenses) {
                        String categoryName = dbHelper.getCategoryName(expense.getCategoryId());
                        String description = expense.getDescription() != null && !expense.getDescription().isEmpty() ? expense.getDescription() : "No note";
                        String transactionLine = String.format("%s: $%d [%s] [%s] [%s]",
                                expense.getType(), expense.getAmount(), expense.getDate(), categoryName, description);
                        Paragraph transactionParagraph = new Paragraph(transactionLine, contentFont);
                        document.add(transactionParagraph);
                    }

                    // Close document
                    document.close();

                    // Update file status
                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(uri, values, null, null);

                    Toast.makeText(getContext(), "PDF exported to Downloads/TransactionReport.pdf", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to create PDF file", Toast.LENGTH_LONG).show();
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error exporting PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}