package ru.nbelov.farmdisplay;

import android.content.Context;
import android.content.DialogInterface;

public class AlertDialog {
    public static interface  AlertDialogAction {
        void execute();
    }
    public static void show(final Context context, String message, AlertDialogAction action, AlertDialogAction negativeAction) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        String positiveCaption = "OK";
        if (negativeAction != null) {
            positiveCaption = "Подтвердить";
        }
        builder.setMessage(message)
                .setOnDismissListener(dialog -> {
                    if (negativeAction != null) {
                        negativeAction.execute();
                    }
                })
                .setPositiveButton(positiveCaption, (dialog, which) -> {
                    if (action != null) {
                        action.execute();
                    }
                    dialog.dismiss();
                });
        if (negativeAction != null) {
            builder.setNegativeButton("Отмена", (dialog, which) -> {
                negativeAction.execute();
            });
        }
        builder.show();
    }
}
