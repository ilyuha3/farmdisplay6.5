package ru.nbelov.farmdisplay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.nbelov.farmdisplay.dbapi.DBController;
import ru.nbelov.farmdisplay.dbapi.GetVersionAPI;

public class FindAPIActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button findApiBtn;
    private TextView errorMesage;
    private ProgressBar progressBar;
    private boolean isRequest = false;

    public static int RESULT_CONNECTIONSTRINGFINDAPI = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ValidatePermissionsActivity.validate(this)) {
            Intent intent = new Intent(this, ValidatePermissionsActivity.class);
            finishActivity(0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_findapi);
        errorMesage = findViewById(R.id.errorMesage);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        findApiBtn = findViewById(R.id.findApiBtn);
        progressBar = findViewById(R.id.progressBar);
        errorMesage.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        findApiBtn.setVisibility(View.VISIBLE);
    }

    public void onUrlClick(View view) {
        String url = "http://algoritm-s.ru/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRequest) {
            if (errorMesage != null) {
                errorMesage.setText("");
                errorMesage.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                findApiBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void dropDbCredentials() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("dbLogin", "");
            editor.putString("dbPass", "");
            editor.putString("dbUrl", "");
            editor.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final Activity ctx = this;
                String contents = data.getStringExtra("SCAN_RESULT");
                Log.i("App", contents);
                String[] params = contents.split(";");
                if (params.length < 3) {
                    TextView errorMesage = findViewById(R.id.errorMesage);
                    errorMesage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    findApiBtn.setVisibility(View.VISIBLE);
                    //dropDbCredentials();
                    errorMesage.setText("Ошибка подключения к базе данных, попробуйте снова");
                    Validation.Vibrate(this);
                    return;
                }
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("dbLogin", params[1]);
                editor.putString("dbPass", params[2]);
                editor.putString("dbUrl", params[0]);
                editor.commit();
                DBController.init(1, params[0], params[1], params[2]);
                afterCredentialsGetAction(this);
            }
            if (resultCode == RESULT_CONNECTIONSTRINGFINDAPI) {
                String dbUrl = data.getStringExtra("dbUrl");
                String dbLogin = data.getStringExtra("dbLogin");
                String dbPass = data.getStringExtra("dbPass");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("dbLogin", dbLogin);
                editor.putString("dbPass", dbPass);
                editor.putString("dbUrl", dbUrl);
                editor.commit();
                DBController.init(1, dbUrl, dbLogin, dbPass);
                afterCredentialsGetAction(this);
            }
            if(resultCode == RESULT_CANCELED){
                errorMesage.setText("");
                errorMesage.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                findApiBtn.setVisibility(View.VISIBLE);
                TextView errorMesage = findViewById(R.id.errorMesage);
                errorMesage.setVisibility(View.VISIBLE);
               // dropDbCredentials();
                errorMesage.setText("Ошибка подключения к базе данных, попробуйте снова");
                Validation.Vibrate(this);
            }
        }
    }

    private void afterCredentialsGetAction(Activity ctx) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        GetVersionAPI getVersionAPI = new GetVersionAPI(this, (code, setupPath) -> {
            if (!setupPath.isEmpty()) {
                NewAppVersionDialog.show(this, setupPath);
                return;
            }
//            Intent intent = new Intent(this, EnregistrementVideoStackActivity.class);
            Intent intent = new Intent(this, MainActivity.class);
            finishActivity(0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }, errorMessage -> {
            TextView errorMesage = findViewById(R.id.errorMesage);
            errorMesage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            findApiBtn.setVisibility(View.VISIBLE);
            // Формируем новое, более информативное сообщение об ошибке
            // dropDbCredentials();
            errorMesage.setText("Ошибка подключения к базе данных, попробуйте снова   информация: " + errorMessage);
            Validation.Vibrate(this);
        });
        getVersionAPI.execute();
    }

    public void onClickFindApi(View view) {
        try {
            errorMesage.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            findApiBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            isRequest = true;
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String dbLogin = sharedPreferences.getString("dbLogin", "");
            String dbPassword = sharedPreferences.getString("dbPass", "");
            String dbUrl = sharedPreferences.getString("dbUrl", "");

            // Если нет строки подключения к БД сразу просим установить настройки
            // Если пользователь не найден то сразу просим установить настройки
            DialogSelectUser dialogSelectUser;
            if (dbUrl.isEmpty()) {
                dialogSelectUser = new DialogSelectUser(null, () -> {
                    // Если выбран режим сканирования соединения с базой
                    Intent intent = new Intent(this, APIQRCodeScanner.class);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                }, ()->{
                    // Если выбран режим ручного ввода строки сканирования
                    Intent intent = new Intent(this, ConnectionStringFindAPIActivity.class);
                    startActivityForResult(intent, 0);
                });
            } else {
                dialogSelectUser = new DialogSelectUser(() -> {
                    // Если выбран режим существующего входа
                    DBController.init(1, dbUrl, dbLogin, dbPassword);
                    afterCredentialsGetAction(this);
                }, () -> {
                    // Если выбран режим сканирования соединения с базой
                    Intent intent = new Intent(this, APIQRCodeScanner.class);
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    startActivityForResult(intent, 0);
                }, ()->{
                    // Если выбран режим ручного ввода строки сканирования
                    Intent intent = new Intent(this, ConnectionStringFindAPIActivity.class);
                    startActivityForResult(intent, 0);
                });
            }
            dialogSelectUser.setCancelable(false);
            dialogSelectUser.show(getSupportFragmentManager(), "dlg1");
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            findApiBtn.setVisibility(View.VISIBLE);
            errorMesage.setVisibility(View.VISIBLE);
           // dropDbCredentials();
            errorMesage.setText("Ошибка подключения к базе данных, попробуйте снова");
            Validation.Vibrate(this);
        }
    }

}