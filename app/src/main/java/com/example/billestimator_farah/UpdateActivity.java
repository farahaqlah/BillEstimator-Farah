package com.example.billestimator_farah;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnit;
    RadioGroup rgRebate;
    Button btnUpdate, btnBack;
    DataHelper dbHelper;
    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        dbHelper = new DataHelper(this);
        recordId = getIntent().getIntExtra("id", -1);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        rgRebate = findViewById(R.id.rgRebate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        // fill the month dropdown (months list comes from MainActivity)
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, MainActivity.months);
        spinnerMonth.setAdapter(adapter);

        // show the old data in the form
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill WHERE id = " + recordId, null);
        if (cursor.moveToFirst()) {
            String month = cursor.getString(1);
            for (int i = 0; i < MainActivity.months.length; i++) {
                if (MainActivity.months[i].equals(month)) {
                    spinnerMonth.setSelection(i);
                }
            }
            etUnit.setText(String.valueOf(cursor.getInt(2)));

            // rebate 0-5 matches the position of the radio button in the group
            int rebate = cursor.getInt(4);
            ((RadioButton) rgRebate.getChildAt(rebate)).setChecked(true);
        }
        cursor.close();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unitText = etUnit.getText().toString().trim();

                // error messages
                if (unitText.isEmpty()) {
                    etUnit.setError("Please enter the electricity unit (kWh)");
                    return;
                }
                int unit = Integer.parseInt(unitText);
                if (unit < 1 || unit > 1000) {
                    etUnit.setError("Unit must be between 1 and 1000 kWh");
                    return;
                }

                String month = spinnerMonth.getSelectedItem().toString();
                RadioButton rb = findViewById(rgRebate.getCheckedRadioButtonId());
                int rebate = Integer.parseInt(rb.getText().toString().replace("%", ""));

                // recalculate using the same method as the main page
                double total = MainActivity.calculateTotal(unit);
                double finalCost = total - (total * rebate / 100);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE bill SET month='" + month + "', unit=" + unit
                        + ", total=" + total + ", rebate=" + rebate
                        + ", finalcost=" + finalCost + " WHERE id=" + recordId);

                Toast.makeText(getApplicationContext(), "Record updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}