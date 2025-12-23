package com.example.pas;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditMessageActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int messageId = -1;

    private EditText etTitle, etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        dbHelper = new DBHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        messageId = getIntent().getIntExtra("message_id", -1);
        if (messageId == -1) {
            Toast.makeText(this, "Message not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMessageToEdit();

        btnUpdate.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Title and content required", Toast.LENGTH_SHORT).show();
                return;
            }

            // imageUri is null for now (we will add image support later)
            boolean ok = dbHelper.updateMessage(messageId, title, content, null);
            Toast.makeText(this, ok ? "Updated!" : "Update failed", Toast.LENGTH_SHORT).show();
            if (ok) finish();
        });
    }

    private void loadMessageToEdit() {
        Cursor c = dbHelper.getMessageById(messageId);
        if (c.moveToFirst()) {
            etTitle.setText(c.getString(c.getColumnIndexOrThrow("title")));
            etContent.setText(c.getString(c.getColumnIndexOrThrow("content")));
        }
        c.close();
    }
}
