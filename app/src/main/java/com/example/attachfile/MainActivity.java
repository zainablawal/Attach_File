package com.example.attachfile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button button;
    double totalSizeOfSelectedFilesInMB = 40.0;
    private static final int PICKFILE_RESULT_CODE = 100;
    ArrayList<String> selectedFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        // chipGroup = findViewById(R.id.chipGroup);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICKFILE_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        Toast.makeText(this, "Select a file", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (null != data.getData()) {
                        Uri uri = data.getData();
                        String path = uri.getPath();
                        Log.d("Files", "Path: " + path);
                        File selectedFile = null;
                        selectedFile = new File(path);
                        double selectedFileSizeInMB = (double) (selectedFile.length()) / (1024 * 1024);
                        Log.d("Selected FILE SIZE", "selected file size: "+selectedFile.length());
                        totalSizeOfSelectedFilesInMB += selectedFileSizeInMB;
                        if (totalSizeOfSelectedFilesInMB <= 40.0 && selectedFiles.size() <= 10) {
                            selectedFiles.add(path);
                            Toast.makeText(this, "" + selectedFiles.size(), Toast.LENGTH_SHORT).show();
                            getFileName(uri);
                            textView.append(getFileName(uri) + "\n");
                        } else {
                            if (selectedFiles.size() == 10 && totalSizeOfSelectedFilesInMB >= 40.0)
                                Toast.makeText(this, "Can't attach more than 10 files", Toast.LENGTH_SHORT).show();

                            else
                                Toast.makeText(this, "All files attached exceed 40.0MB limit", Toast.LENGTH_SHORT).show();

                        }
                    }

                }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("File")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}


