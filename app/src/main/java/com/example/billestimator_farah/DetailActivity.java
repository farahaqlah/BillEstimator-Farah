package com.example.billestimator_farah;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActivity extends AppCompatActivity {

    TextView tvMonth, tvUnit, tvTotal, tvRebate, tvFinal;
    Button btnEdit, btnDelete, btnBack;
    DataHelper dbHelper;
    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new DataHelper(this);
        recordId = getIntent().getIntExtra("id", -1);

        tvMonth = findViewById(R.id.tvMonth);
        tvUnit = findViewById(R.id.tvUnit);
        tvTotal = findViewById(R.id.tvTotal);
        tvRebate = findViewById(R.id.tvRebate);
        tvFinal = findViewById(R.id.tvFinal);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, UpdateActivity.class);
                i.putExtra("id", recordId);
                startActivity(i);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ask first (good design practice)
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("Delete record");
                builder.setMessage("Are you sure you want to delete this record?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM bill WHERE id = " + recordId);
                        Toast.makeText(getApplicationContext(), "Record deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // reload every time the page is shown (also after coming back from Edit)
    @Override
    protected void onResume() {
        super.onResume();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill WHERE id = " + recordId, null);
        if (cursor.moveToFirst()) {
            tvMonth.setText("Month: " + cursor.getString(1));
            tvUnit.setText("Unit used: " + cursor.getInt(2) + " kWh");
            tvTotal.setText("Total charges: RM " + String.format("%.2f", cursor.getDouble(3)));
            tvRebate.setText("Rebate: " + cursor.getInt(4) + "%");
            tvFinal.setText("Final cost: RM " + String.format("%.2f", cursor.getDouble(5)));
        }
        cursor.close();
    }
}