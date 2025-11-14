package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetVersionAPI extends AbstractDBAPI {

    public static interface OnSuccessResponse {
        void execute(int code, String setupPath);
    }

    public GetVersionAPI(Context ctx, OnSuccessResponse onSuccessResponse, DBController.OnErrorResponse onErrorResponse) {
        super(ctx, connection -> {
            try (Statement stmt = connection.createStatement();) {
                StringBuffer stringBuffer = new StringBuffer();
                ResultSet rs = stmt.executeQuery("SELECT organiz.pk_mbdisplay.validateVersion() FROM dual");
                rs.next();
                String version = rs.getString(1);
                if (DBController.VERSION.equals(version)) {
                    DBController.executeInMainThread(ctx, () -> {
                        onSuccessResponse.execute(200, "");
                    });
                    return;
                }
            }
        }, onErrorResponse);
    }
}
