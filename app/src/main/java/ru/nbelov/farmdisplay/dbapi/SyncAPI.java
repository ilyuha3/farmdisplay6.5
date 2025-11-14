package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;

import java.sql.CallableStatement;
import java.sql.Types;

public class SyncAPI extends AbstractDBAPI {
    public static interface OnSuccessResponse {
        void execute(String htmlLeft, String htmlRight, String htmlFull, String voicePrice, int startStopRecord, int signal);
    }

    public SyncAPI(Context ctx, String deviceId, int width, int height, int dpi, OnSuccessResponse onSuccessResponse, DBController.OnErrorResponse onErrorResponse) {
        super(ctx, connection -> {
            String SQL = "{call organiz.pk_mbdisplay.sync(?,?,?,?,?,?,?,?,?,?)}";
            try (CallableStatement callableStatement = connection.prepareCall(SQL);) {
                callableStatement.setString("v_device_id", deviceId);
                callableStatement.setInt("v_width", width);
                callableStatement.setInt("v_height", height);
                callableStatement.setInt("v_dpi", dpi);
                callableStatement.registerOutParameter("v_html_left", Types.VARCHAR);
                callableStatement.registerOutParameter("v_html_right", Types.VARCHAR);
                callableStatement.registerOutParameter("v_html_full", Types.VARCHAR);
                callableStatement.registerOutParameter("v_voice_price", Types.VARCHAR);
                callableStatement.registerOutParameter("v_start_stop_record", Types.INTEGER);
                callableStatement.registerOutParameter("v_signal", Types.INTEGER);
                callableStatement.execute();
                String htmlLeft = callableStatement.getString("v_html_left");
                String htmlRight = callableStatement.getString("v_html_right");
                String htmlFull = callableStatement.getString("v_html_full");
                String voicePrice = callableStatement.getString("v_voice_price");
                int startStopRecord = callableStatement.getInt("v_start_stop_record");
                int signal = callableStatement.getInt("v_signal");
                DBController.executeInMainThread(ctx, () -> {
                    onSuccessResponse.execute(htmlLeft, htmlRight, htmlFull, voicePrice, startStopRecord, signal);
                });
            }
        }, onErrorResponse);
    }

}
