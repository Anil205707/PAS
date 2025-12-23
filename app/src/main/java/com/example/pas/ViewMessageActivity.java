package com.example.pas;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewMessageActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private int messageId = -1;

    private TextView tvTitle, tvContent;
    private ImageView imgView;
    private Button btnEdit, btnDelete, btnShare, btnUpload;

    private String loadedTitle = "";
    private String loadedContent = "";
    private String loadedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        dbHelper = new DBHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        imgView = findViewById(R.id.imgView);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        btnUpload = findViewById(R.id.btnUpload);

        messageId = getIntent().getIntExtra("message_id", -1);
        if (messageId == -1) {
            Toast.makeText(this, "Message not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMessage();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(ViewMessageActivity.this, EditMessageActivity.class);
            i.putExtra("message_id", messageId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            boolean ok = dbHelper.deleteMessage(messageId);
            Toast.makeText(this, ok ? "Deleted" : "Delete failed", Toast.LENGTH_SHORT).show();
            if (ok) finish();
        });

        // ✅ SHARE (text + image)
        btnShare.setOnClickListener(v -> shareMessage());

        // Upload will be done next milestone
        btnUpload.setOnClickListener(v -> {
            Toast.makeText(this, "Upload feature next", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessage();
    }

    private void loadMessage() {
        Cursor c = dbHelper.getMessageById(messageId);
        if (c.moveToFirst()) {
            loadedTitle = c.getString(c.getColumnIndexOrThrow("title"));
            loadedContent = c.getString(c.getColumnIndexOrThrow("content"));
            loadedImageUri = c.getString(c.getColumnIndexOrThrow("image_uri"));

            tvTitle.setText(loadedTitle);
            tvContent.setText(loadedContent);

            if (loadedImageUri != null && !loadedImageUri.trim().isEmpty()) {
                imgView.setVisibility(View.VISIBLE);
                imgView.setImageURI(Uri.parse(loadedImageUri));
            } else {
                imgView.setVisibility(View.GONE);
            }
        }
        c.close();
    }

    private void shareMessage() {
        if (loadedTitle == null) loadedTitle = "";
        if (loadedContent == null) loadedContent = "";

        String shareText = loadedTitle + "\n\n" + loadedContent;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // If image exists, share as image + text
        if (loadedImageUri != null && !loadedImageUri.trim().isEmpty()) {
            shareIntent.setType("image/*");
            Uri uri = Uri.parse(loadedImageUri);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // text only
            shareIntent.setType("text/plain");
        }

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, loadedTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Share message using"));
    }
}
