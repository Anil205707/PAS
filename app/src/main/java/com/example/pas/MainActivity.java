package com.example.pas;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView lvMessages;
    private EditText etSearch;
    private Button btnAdd, btnDeleteSelected;

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
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);

        // IMPORTANT: checkbox list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, titles);
        lvMessages.setAdapter(adapter);
        lvMessages.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        loadMessages("");

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddMessageActivity.class))
        );

        // Normal tap -> open message
        lvMessages.setOnItemClickListener((parent, view, position, id) -> {
            // If any items are selected, we are in "selection mode" (so don’t open)
            if (lvMessages.getCheckedItemCount() > 0) {
                Toast.makeText(this, lvMessages.getCheckedItemCount() + " selected", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(MainActivity.this, ViewMessageActivity.class);
            i.putExtra("message_id", ids.get(position));
            startActivity(i);
        });

        // Search live
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMessages(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Delete group
        btnDeleteSelected.setOnClickListener(v -> deleteSelected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages(etSearch.getText().toString());
    }

    private void loadMessages(String keyword) {
        titles.clear();
        ids.clear();
        lvMessages.clearChoices();

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

    private void deleteSelected() {
        SparseBooleanArray checked = lvMessages.getCheckedItemPositions();
        if (checked == null || checked.size() == 0) {
            Toast.makeText(this, "No messages selected", Toast.LENGTH_SHORT).show();
            return;
        }

        int deletedCount = 0;

        // Go from end -> start to avoid index shifting issues
        for (int i = checked.size() - 1; i >= 0; i--) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                int messageId = ids.get(position);
                if (dbHelper.deleteMessage(messageId)) {
                    deletedCount++;
                }
            }
        }

        Toast.makeText(this, "Deleted " + deletedCount + " message(s)", Toast.LENGTH_SHORT).show();
        loadMessages(etSearch.getText().toString());
    }
}
