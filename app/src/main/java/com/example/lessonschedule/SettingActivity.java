package com.example.lessonschedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

public class SettingActivity extends AppCompatActivity {
    private EditText editText;
    private String saveText;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1=new Intent();
        intent1.putExtra("data_return",saveText);
        setResult(RESULT_OK,intent1);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        String data=intent.getStringExtra("extra_data");
        setContentView(R.layout.activity_setting);
        editText=findViewById(R.id.edit_text);
        editText.setText(data);
        Editable saveText = editText.getText();
    }
}
