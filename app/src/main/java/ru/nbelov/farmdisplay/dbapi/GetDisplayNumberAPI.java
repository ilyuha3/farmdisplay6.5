package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;

import java.sql.CallableStatement;
import java.sql.Types;

public class GetDisplayNumberAPI extends AbstractDBAPI {
    public static interface OnSuccessResponse {
        void execute(int displayid);
    }

    public GetDisplayNumberAPI(Context ctx, String deviceId, int width, int height, int dpi, OnSuccessResponse onSuccessResponse, DBController.OnErrorResponse onErrorResponse) {
        super(ctx, connection -> {
            String SQL = "{call organiz.pk_mbdisplay.get_display_number(?,?,?,?,?)}";
            try (CallableStatement callableStatement = connection.prepareCall(SQL);) {
                callableStatement.setString("v_device_id", deviceId);
                callableStatement.setInt("v_width", width);
                callableStatement.setInt("v_height", height);
                callableStatement.setInt("v_dpi", dpi);
                callableStatement.registerOutParameter("v_display_id", Types.INTEGER);
                callableStatement.execute();
                int displayId = callableStatement.getInt("v_display_id");
                DBController.executeInMainThread(ctx, () -> {
                    onSuccessResponse.execute(displayId);
                });
            }
        }, onErrorResponse);
    }

}
