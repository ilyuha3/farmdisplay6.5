package ru.nbelov.farmdisplay.dbapi;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetAssetSyncAPI {
    public static byte[] getAsset(String name) {
        try {
            try (Connection connection = DBController.getDataSource().getConnection();) {
                try (Statement stmt = connection.createStatement();) {
                    StringBuffer stringBuffer = new StringBuffer();
                    Log.i("TT","select blob_storage_data from organiz.t_blob_storage t where t.ext_file_name = '" + name + "'");
                    ResultSet rs = stmt.executeQuery("select blob_storage_data from organiz.t_blob_storage t where t.ext_file_name = '" + name + "'");
                    if (!rs.next()) {
                        return "".getBytes();
                    }
                    return rs.getBytes(1);
                }
            }
        } catch (SQLException ex) {
            Log.e("GetAssetSyncAPI", ex.getLocalizedMessage());
            ex.printStackTrace();
            return "".getBytes();
        }
    }
}
