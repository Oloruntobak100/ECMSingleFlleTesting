package codes;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Objects;

public class DBConn {
    public static Connection db_conn = null;
    public static String ConnectionString = "";
    public static String testMsg = "";

    public static void main(String[] args){
        getConnectionToDatabase();
    }

    public static String testDBConn(){
        String r = "";
        if( GlobalFunctions.nwp_development_mode ) {
            DBConn.db_conn = null;
            DBConn.testMsg = "";
            DBConn.ConnectionString = "";
            getConnectionToDatabase();
        }else{
            DBConn.testMsg = "Access Denied: Dev. Mode Must be Enabled";
        }
        return DBConn.testMsg;
    }

    public static JSONObject executeQuery( JSONObject $qs ) {
        JSONObject $qr = new JSONObject();
        String $qtype = $qs.has("query_type")?$qs.getString("query_type"):"select";
        String table = $qs.has("table")?$qs.getString("table"):"";
        String $index_field = $qs.has("index_field")?$qs.getString("index_field"):"";
        Boolean $multiple_index = $qs.has("multiple_index")?$qs.getBoolean("multiple_index"):false;

        $qr.put("row_count", 0);
        $qr.put("col_count", 0);

        PreparedStatement preparedStatement = null;
        if( $qs.has("query") ){
            try {

                // get the connection for the database
                Connection connection = getConnectionToDatabase();

                // write the insert query
                String Query = $qs.getString("query");
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "" )
                                .put("input", new JSONObject().put("query", Query).put("qs", $qs) )
                                .put("function", "DBConn.executeQuery" )
                                .put("query", true ) , null
                );


                // set parameters with PreparedStatement
                preparedStatement = connection.prepareStatement(Query); //Making use of prepared statements here to insert bunch of data

                if( $qs.has("query_params") ){
                    JSONObject $qp = $qs.getJSONObject("query_params");
                    String $key1 = "";
                    JSONObject $val1 = new JSONObject();

                    for(int i2 = 0; i2 < $qp.names().length(); i2++) {
                        $key1 = $qp.names().getString(i2);
                        $val1 = $qp.getJSONObject($key1);

                        if( $val1.has("type") && $val1.has("value") ){
                            String vtype = $val1.getString("type");

                            switch( vtype ){
                                case "string":
                                    preparedStatement.setString( (i2 + 1 ), $val1.getString("value") );
                                    break;
                            }
                        }
                    }
                }


                int numberOfRows = 0;
                JSONObject $cols = new JSONObject();
                JSONArray $all_rows = new JSONArray();
                JSONObject $index_rows = new JSONObject();

                switch( $qtype ){
                    case "delete":
                    case "update":
                    case "insert":
                        numberOfRows = preparedStatement.executeUpdate();
                        $qr.put("row_count", numberOfRows);
                        break;
                    default:
                        ResultSet set = preparedStatement.executeQuery();
                        ResultSetMetaData metadata = set.getMetaData();
                        int numberOfCols = metadata.getColumnCount();

                        $qr.put("col_count", numberOfCols);

                        if( numberOfCols > 0 ){

                            for( int i1=1; i1 <= numberOfCols; i1 ++) {
                                $cols.put( "" + i1, metadata.getColumnName( i1 ) );
                            }

                            String db_val = "";
                            while (set.next()) {
                                //user.setUsername(set.getString("username"));
                                JSONObject $row = new JSONObject();
                                JSONArray $files = new JSONArray();

                                for (int i = 1; i <= numberOfCols; i++) {
                                    db_val = "";
                                    if (!Objects.isNull(set.getObject(i))) {
                                        db_val = GlobalFunctions.stripslashes(set.getObject(i).toString());
                                    }

                                    if( $qs.has("files") && $qs.getJSONObject("files").has( $cols.getString("" + i) ) ){
                                        $files.put( $cols.getString("" + i) );
                                    }else {
                                        $row.put($cols.getString("" + i), db_val);
                                    }

                                }

                                if( $files.length() > 0 && $row.has("id") ) {
                                    try {
                                        for (int i2 = 0; i2 < $files.length(); i2++) {
                                            if( $qs.has("files") && $qs.getJSONObject("files").has( $files.getString(i2) ) ) {
                                                JSONObject fileData = $qs.getJSONObject("files").getJSONObject( $files.getString(i2) );

                                                if( fileData.has("field") && fileData.has("ext_field") ){
                                                    String extField = fileData.getString("ext_field");
                                                    if( $row.has( extField ) && ! $row.getString( extField ).isEmpty() ) {
                                                        String ext = "";
                                                        String dType = fileData.has("ext_type") ? fileData.getString("ext_type") : "";

                                                        switch (dType) {
                                                            case "data":
                                                                if (fileData.has("ext_data_field") && ! fileData.getString("ext_data_field").isEmpty() ) {
                                                                    JSONObject $jval = new JSONObject( GlobalFunctions.urldecode( $row.getString( extField ) ) );
                                                                    extField = fileData.getString("ext_data_field");

                                                                    if( ! $jval.has(extField) && $jval.names().length() > 0) {
                                                                        if( $jval.has( $jval.names().getString(0) ) ) {
                                                                            $jval = $jval.getJSONObject($jval.names().getString(0));
                                                                        }
                                                                    }

                                                                    if ($jval.has( extField )) {
                                                                        ext = $jval.getString( extField );
                                                                    }
                                                                }
                                                                break;
                                                            default:
                                                                ext = $row.getString( extField );
                                                                break;
                                                        }

                                                        byte[] decode = Base64.getDecoder().decode( set.getObject( fileData.getString("field") ).toString() );
                                                        Blob blob = new SerialBlob( decode );
                                                        InputStream inputStream = blob.getBinaryStream();

                                                        /*Blob blob = set.getBlob(fileData.getString("field"));
                                                        InputStream inputStream = blob.getBinaryStream();*/
                                                        String fileName = $row.getString("id") + "-" + i2 + "." + ext;

                                                        String pathURI = GlobalFunctions.app_file_download_dir + GlobalFunctions.app_file_seperator;
                                                        if( ! table.isEmpty() ){
                                                            pathURI += table;
                                                            File dir1 = new File(pathURI);
                                                            if (!dir1.exists()) {
                                                                dir1.mkdir();
                                                            }
                                                            pathURI += GlobalFunctions.app_file_seperator;
                                                        }
                                                        //Path path = Paths.get("C:\\downloads\\" + fileName);
                                                        //System.out.println(GlobalFunctions.app_file_download_dir + GlobalFunctions.app_file_seperator + fileName);
                                                        Path path = Paths.get( pathURI + fileName);

                                                        //System.out.println(inputStream);
                                                        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                                                        $row.put(fileData.getString("field") + "_name", fileName );
                                                        $row.put(fileData.getString("field"), path.toString());
                                                        $row.put("nwp_file_found", 1);
                                                    }else{
                                                        $qr.put("file_error", "Invalid File Extension");
                                                    }
                                                }else{
                                                    $qr.put("file_error", "Invalid Field and Ext_Field values");
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        //System.out.println(e.getMessage());
                                        GlobalFunctions.nw_dev_handler(
                                                new JSONObject()
                                                        .put("return", e.getMessage() )
                                                        .put("input", new JSONObject().put("qs", $qs).put("files", $files) )
                                                        .put("function", "DBConn.executeQuery" )
                                                        .put("exception", true )
                                                        .put("non_fatal", true ) , e
                                        );
                                    }

                                }

                                //System.out.println( $row );
                                if ($index_field.isEmpty()) {
                                    $all_rows.put($row);
                                } else {
                                    if ($row.has($index_field)) {
                                        if( $multiple_index ) {
                                            if( ! $index_rows.has($row.getString($index_field) )  ){
                                                $index_rows.put($row.getString($index_field), new JSONArray() );
                                            }
                                            $index_rows.getJSONArray($row.getString($index_field) ).put( $row );
                                        }else{
                                            $index_rows.put($row.getString($index_field), $row);

                                        }
                                    }
                                }
                                ++numberOfRows;
                            }

                            $qr.put("col", $cols);
                            if( $index_field.isEmpty() ) {
                                $qr.put("row", $all_rows);
                            }else{
                                $qr.put("row", $index_rows);
                            }
                            $qr.put("row_count", numberOfRows);
                        }
                        break;
                }


            } catch (SQLException e) {
                //e.printStackTrace();
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", e.getMessage() )
                                .put("input", new JSONObject().put("qs", $qs) )
                                .put("function", "DBConn.executeQuery" )
                                .put("fatal", true )
                                .put("query", true ) , e
                );
            }
        }

        return $qr;
    }

    public static void closeConnectionToDatabase() {

        if( ! Objects.isNull( DBConn.db_conn ) ){
            try {
                DBConn.db_conn.close();
                DBConn.db_conn = null;

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "Connection closed" )
                                .put("input", new JSONObject() )
                                .put("function", "DBConn.closeConnectionToDatabase" )
                                .put("non_fatal", true ) , null
                );
            } catch (Exception e) {

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "Unable to close connection: " + e.getMessage() )
                                .put("input", new JSONObject() )
                                .put("function", "DBConn.closeConnectionToDatabase" )
                                .put("fatal", true ) , e
                );
            }
        }

    }

    public static Connection getConnectionToDatabase() {

        if( ! Objects.isNull( DBConn.db_conn ) ){

            try {
                if (DBConn.db_conn.isClosed()) {
                    //System.out.println("first db connection closed...reconnecting");
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "first db connection closed...reconnecting" )
                                    .put("input", new JSONObject() )
                                    .put("function", "DBConn.getConnectionToDatabase" )
                                    .put("non_fatal", true ) , null
                    );
                }else{
                    //System.out.println("Reusing first db connection");
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "Reusing first db connection" )
                                    .put("input", new JSONObject() )
                                    .put("function", "DBConn.getConnectionToDatabase" )
                                    .put("non_fatal", true ) , null
                    );
                    return DBConn.db_conn;
                }
            }catch (Exception e){
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", e.getMessage() )
                                .put("input", new JSONObject() )
                                .put("function", "DBConn.getConnectionToDatabase" )
                                .put("fatal", true ) , e
                );
            }

        }

        try {
            // load the driver class
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //System.out.println("MS SQL JDBC Driver Registered!");

            if( ConnectionString.isEmpty() ){
                JSONObject appData = GlobalFunctions.getApp();
                if( appData.has("database_connection") && ! appData.getString("database_connection").isEmpty() ) {
                    if( appData.has("decrypt_database_connection") && appData.getBoolean("decrypt_database_connection") ) {
                        ConnectionString = GlobalFunctions.decrypt( appData.getString("database_connection") );
                    }else{
                        ConnectionString = appData.getString("database_connection");
                    }
                }
            }

            if( ! ConnectionString.isEmpty() ) {
                // get hold of the DriverManager
                DBConn.db_conn = DriverManager.getConnection(ConnectionString);
                //DBConn.db_conn = DriverManager.getConnection("jdbc:sqlserver://WIN-LVK0HO7H2U7;user=law;password=1234567;database=cbn_ecm2;sslProtocol=TLSv1.2;encrypt=true;trustServerCertificate=true");
                //mig45-30HEN
                DBConn.testMsg = "Connection String: " + ConnectionString + "<br>";
            }else{
                //System.out.println("Invalid DB Connection Information: Check Config File");
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "Invalid DB Connection Information: Check Config File" )
                                .put("input", new JSONObject().put("connectionString", ConnectionString) )
                                .put("function", "DBConn.getConnectionToDatabase" )
                                .put("fatal", true ) , null
                );
                DBConn.testMsg = "Connection String: Empty<br>";
            }

        } catch (ClassNotFoundException e) {
            //System.out.println("MS SQL JDBC Driver Not Found!");
            //e.printStackTrace();
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "MS SQL JDBC Driver Not Found! " + e.getMessage() )
                            .put("input", new JSONObject().put("connectionString", ConnectionString) )
                            .put("function", "DBConn.getConnectionToDatabase" )
                            .put("fatal", true ) , e
            );
            DBConn.testMsg = "DB Test Connection Error 1: "+ e.getMessage() +"<br>";

        }

        catch (SQLException e) {
           // System.out.println("Connection Failed! Check output console");
            //e.printStackTrace();
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "Connection Failed! Check output console" )
                            .put("input", new JSONObject().put("connectionString", ConnectionString) )
                            .put("function", "DBConn.getConnectionToDatabase" )
                            .put("fatal", true ) , e
            );
            DBConn.testMsg = "DB Test Connection Error 2: "+ e.getMessage() +"<br>";
        }

        if (DBConn.db_conn != null) {
            //System.out.println("Connection made to DB!");
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "Connection made to DB!" )
                            .put("input", new JSONObject() )
                            .put("function", "DBConn.getConnectionToDatabase" )
                            .put("non_fatal", true ) , null
            );
            DBConn.testMsg = "DB Test Connection Success:<br>";
        }
        return DBConn.db_conn;
    }
}
