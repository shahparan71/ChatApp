package com.opus_bd.realtimemessagin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    EditText etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etText = findViewById(R.id.etText);
    }

    public void login(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("user",etText.getText().toString().trim());
        startActivity(intent);
    }
}