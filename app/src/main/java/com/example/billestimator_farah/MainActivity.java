package com.example.billestimator_farah;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnit;
    RadioGroup rgRebate;
    Button btnCalculate, btnView, btnAbout;
    TextView tvTotal, tvFinal;
    DataHelper dbHelper;

    public static String[] months = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October",
            "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnit = findViewById(R.id.etUnit);
        rgRebate = findViewById(R.id.rgRebate);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnView = findViewById(R.id.btnView);
        btnAbout = findViewById(R.id.btnAbout);
        tvTotal = findViewById(R.id.tvTotal);
        tvFinal = findViewById(R.id.tvFinal);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndSave();
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    public void calculateAndSave() {
        String unitText = etUnit.getText().toString().trim();


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

        double total = calculateTotal(unit);
        double finalCost = total - (total * rebate / 100);


        tvTotal.setText("Total charges: RM " + String.format("%.2f", total));
        tvFinal.setText("Final cost: RM " + String.format("%.2f", finalCost));


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("INSERT INTO bill(month, unit, total, rebate, finalcost) VALUES('"
                + month + "'," + unit + "," + total + "," + rebate + "," + finalCost + ")");

        Toast.makeText(getApplicationContext(), "Bill saved successfully", Toast.LENGTH_SHORT).show();
    }


    public static double calculateTotal(int unit) {
        double total;
        if (unit <= 200) {
            total = unit * 0.218;
        } else if (unit <= 300) {
            total = (200 * 0.218) + ((unit - 200) * 0.334);
        } else if (unit <= 600) {
            total = (200 * 0.218) + (100 * 0.334) + ((unit - 300) * 0.516);
        } else {
            total = (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unit - 600) * 0.546);
        }
        return total;
    }
}