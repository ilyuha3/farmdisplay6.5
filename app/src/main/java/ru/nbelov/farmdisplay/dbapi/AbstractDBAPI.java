package ru.nbelov.farmdisplay.dbapi;

import android.content.Context;
import android.provider.Settings;

import java.sql.ResultSet;
import java.sql.Statement;

public class AbstractDBAPI {

    private final Context ctx;
    private final DBController.OnErrorResponse onErrorResponse;
    private final DBController.DBInstruction instruction;

    public AbstractDBAPI(Context ctx, DBController.DBInstruction instruction, DBController.OnErrorResponse onErrorResponse) {
        this.ctx = ctx;
        this.onErrorResponse = onErrorResponse;
        this.instruction = instruction;
    }

    private static String DEVICEID = "";
    protected static int accountId = 0;
    public static boolean busy = false;

    protected static void setAccountId(int accountId) {
        AbstractDBAPI.accountId = accountId;
    }

    public static void logout() {
        // Выход из аккаунта. Пока решили убрать
        // accountId = 0;
    }

    public static String getDeviceId(Context ctx) {
        if (DEVICEID.isEmpty()) {
            DEVICEID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return DEVICEID;
    }

    public final void execute() {
        DBController.executeInDb(ctx, connection -> {
            busy = true;
            instruction.execute(connection);
            busy = false;
        }, ex -> {
            busy = false;
            ex.printStackTrace();
            onErrorResponse.execute(ex.getMessage());
        });
    }

}
