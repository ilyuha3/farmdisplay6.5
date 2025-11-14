package ru.nbelov.farmdisplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class NewAppVersionDialog {
    public static void show(final Context context, String setupPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Версия данного приложения устарела.\nНеобходимо перейти на сайт и установить обновление")
                .setPositiveButton("Перейти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(setupPath));
                        context.startActivity(i);
                    }
                }
            ).show();
    }
}
