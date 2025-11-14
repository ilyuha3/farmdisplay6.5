package ru.nbelov.farmdisplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class DialogSelectUser extends DialogFragment {
    final String LOG_TAG = "myLogs";

    public static interface OptionSameLoginAction {
        void execute();
    }

    public static interface OptionNewLoginAction {
        void execute();
    }

    public static interface OptionNewLoginByConnectionString {
        void execute();
    }

    private final OptionSameLoginAction optionSameLogin;
    private final OptionNewLoginAction optionNewLogin;
    private final OptionNewLoginByConnectionString optionNewLoginByConnectionString;

    public DialogSelectUser(OptionSameLoginAction optionSameLogin, OptionNewLoginAction optionNewLogin, OptionNewLoginByConnectionString optionNewLoginByConnectionString) {
        this.optionSameLogin = optionSameLogin;
        this.optionNewLogin = optionNewLogin;
        this.optionNewLoginByConnectionString = optionNewLoginByConnectionString;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_selectuser, null);
        ConstraintLayout optionSameLoginLayout = v.findViewById(R.id.optionSameLogin);
        if (optionSameLogin == null) {
            optionSameLoginLayout.setVisibility(View.GONE);
        } else {
            TextView loginName = v.findViewById(R.id.loginName);
            loginName.setText("Текущее соединение");
            optionSameLoginLayout.setVisibility(View.VISIBLE);
            optionSameLoginLayout.setOnClickListener(v1 -> {
                dismiss();
                optionSameLogin.execute();
            });
        }
        ConstraintLayout optionNewLoginLayout = v.findViewById(R.id.optionNewLogin);
        optionNewLoginLayout.setOnClickListener(v1 -> {
            dismiss();
            optionNewLogin.execute();
        });
        ConstraintLayout optionNewLoginByConnectionStringLayout = v.findViewById(R.id.optionNewLoginByConnectionString);
        optionNewLoginByConnectionStringLayout.setOnClickListener(v1 -> {
            dismiss();
            optionNewLoginByConnectionString.execute();
        });
        return v;
    }
}
