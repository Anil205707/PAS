package com.example.pas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddMessageActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private EditText etTitle, etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        dbHelper = new DBHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = dbHelper.insertMessage(title, content, null);
            Toast.makeText(this, ok ? "Saved!" : "Failed!", Toast.LENGTH_SHORT).show();

            if (ok) finish();
        });
    }
}
