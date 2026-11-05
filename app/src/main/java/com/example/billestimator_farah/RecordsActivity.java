package com.example.billestimator_farah;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecordsActivity extends AppCompatActivity {

    ListView listView;
    TextView tvEmpty;
    Button btnBack;
    DataHelper dbHelper;
    String[] register;
    int[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        dbHelper = new DataHelper(this);
        listView = findViewById(R.id.listView1);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(RecordsActivity.this, DetailActivity.class);
                i.putExtra("id", ids[position]);
                startActivity(i);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // runs every time this page is shown again (after edit / delete)
    @Override
    protected void onResume() {
        super.onResume();
        RefreshList();
    }

    public void RefreshList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bill ORDER BY id DESC", null);
        register = new String[cursor.getCount()];
        ids = new int[cursor.getCount()];
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++) {
            cursor.moveToPosition(cc);
            ids[cc] = cursor.getInt(0);
            // month and final cost only
            register[cc] = cursor.getString(1) + "  -  RM " + String.format("%.2f", cursor.getDouble(5));
        }
        cursor.close();

        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, register));

        if (register.length == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }
}