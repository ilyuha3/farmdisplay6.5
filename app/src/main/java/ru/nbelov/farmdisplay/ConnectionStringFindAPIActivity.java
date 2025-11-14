package ru.nbelov.farmdisplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import static ru.nbelov.farmdisplay.FindAPIActivity.RESULT_CONNECTIONSTRINGFINDAPI;

public class ConnectionStringFindAPIActivity extends AppCompatActivity {



    private EditText dbUrlEditText;
    private EditText dbLoginEditText;
    private EditText dbPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionstringfindapi);
        dbUrlEditText = findViewById(R.id.dbUrl);
        dbLoginEditText = findViewById(R.id.dbLogin);
        dbPasswordEditText = findViewById(R.id.dbPassword);
    }

    public void onClickConnectBtn(View view) {
        if (dbUrlEditText.getText().toString().isEmpty() || dbLoginEditText.getText().toString().isEmpty() || dbPasswordEditText.getText().toString().isEmpty()) {
            Validation.Vibrate(this);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("dbUrl", "jdbc:oracle:thin:@" + dbUrlEditText.getText().toString());
        intent.putExtra("dbLogin", dbLoginEditText.getText().toString());
        intent.putExtra("dbPass", dbPasswordEditText.getText().toString());
        setResult(RESULT_CONNECTIONSTRINGFINDAPI, intent);
        finish();
    }

    public void onClickCancelBtn(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
