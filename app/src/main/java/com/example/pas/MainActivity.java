package com.example.pas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView lvMessages;
    private EditText etSearch;
    private Button btnAdd;

    private final ArrayList<String> titles = new ArrayList<>();
    private final ArrayList<Integer> ids = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        lvMessages = findViewById(R.id.lvMessages);
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        lvMessages.setAdapter(adapter);

        loadMessages("");

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMessageActivity.class))
        );

        lvMessages.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MainActivity.this, ViewMessageActivity.class);
            i.putExtra("message_id", ids.get(position));
            startActivity(i);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMessages(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages(etSearch.getText().toString());
    }

    private void loadMessages(String keyword) {
        titles.clear();
        ids.clear();

        Cursor c = (keyword == null || keyword.trim().isEmpty())
                ? dbHelper.getAllMessages()
                : dbHelper.searchMessages(keyword.trim());

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String title = c.getString(c.getColumnIndexOrThrow("title"));
            ids.add(id);
            titles.add("#" + id + " - " + title);
        }
        c.close();
        adapter.notifyDataSetChanged();
    }
}
