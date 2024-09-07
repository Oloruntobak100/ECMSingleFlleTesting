package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.Part;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GlobalFunctions {
    public static String app_ad_server = "WIN-LVK0HO7H2U7";
    public static String app_ad_domain = "MAYBEACHTECH.local";
    public static String app_config_dir = "C:\\dev1kit\\paperlite\\";
    public static String app_config_dir_linux = "/var/lib/dev1kit/paperlite/";

    public static String app_path = "";
    public static String app_notice = "";
    public static String app_notice_type = "";
    public static String app_user = "system";
    public static String app_title = "Dev1kit JSP";
    public static String app_user_dept = "";
    public static JSONObject app_user_data = new JSONObject();
    public static JSONObject app_redirect_data = new JSONObject();
    public static Boolean app_notice_only = false;
    public static Boolean app_reload_datatable = false;
    public static Boolean app_load_datatable = false;

    public static String app_popup_style = "";
    public static Boolean app_popup = false;
    public static String app_popup_title = "";
    public static String app_popup_handle = "";
    public static String app_replace_container = "";

    public static JSONObject app_jsp_json;
    public static Boolean nwp_development_mode = false;
    public static Boolean app_developer = true;
    public static Boolean enable_access_control = true;
    public static Integer cache_email = 0;
    public static String app_sys_admin_role = "_sys_";

    public static String app_request_source = "";
    public static String upload_file_formats = "pdf:::jpg:::jpeg:::png:::bmp:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt";
    public static Integer max_upload_mb = 10;
    public static String app_file_seperator = "/";
    public static String app_file_tmp_dir = "";
    public static String app_file_upload_dir = "";
    public static String app_file_download_dir = "";
    public static String app_cache_dir = "";
    public static String app_email_dir = "";
    public static String app_salter = "THBJL*(7^%&^UHBf343f";
    public static String app_log = "";
    public static JSONObject app_data = new JSONObject();
    public static JSONObject glo_var = new JSONObject();

    public static JSONObject noCache = new JSONObject("{\"users\":{\"system\":1}}");

    public static String currentUserName = "";
    public static String currentUserPwd = "";


    public static void initialize(){

        JSONObject appData = GlobalFunctions.getApp();
        GlobalFunctions.app_data = appData;

        if( appData.has("gpl") && appData.has("gp") && appData.getBoolean("gp") ) {
            Long ct = System.currentTimeMillis() / 1000;
            if( ct > Long.valueOf( appData.getString("gpl") ) ){
                try {
                    throw new Exception("Network connection restriction. NWP-XBR");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if( appData.has("noCache") && appData.getJSONObject("noCache").length() > 0 ) {
            noCache = appData.getJSONObject("noCache");
        }

        String logPath = System.getProperty( "java.io.tmpdir" );
        if( ! GlobalFunctions.app_file_seperator.isEmpty() ) {
            logPath += GlobalFunctions.app_file_seperator + "paperlite";
            File dir = new File(logPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
        }
        GlobalFunctions.app_log = logPath;

        if( appData.has("development_mode") && appData.getInt("development_mode") > 0 ) {

            if( GlobalFunctions.app_developer ) {
                GlobalFunctions.app_user_data.put("id", "developer").put("name", "RIA A.H").put("access_role", "Developer").put("paccess_role", "Coordinator");
                GlobalFunctions.app_user_data.put("department", "ofisd_id").put("department_text", "R&D").put("group", "grp-8").put("team", "team-a").put("grade_level", "developer").put("grade_level_text", "Developer");
                GlobalFunctions.app_user_data.put("group_text", "R&D Group 8-K").put("team_text", "Log Path: " + GlobalFunctions.app_log );
            }
            GlobalFunctions.nwp_development_mode = true;
        }

        if( appData.has("company_name") && ! appData.getString("company_name").isEmpty() ) {
            GlobalFunctions.app_title = appData.getString("company_name");
        }

        if( appData.has("cache_email") && appData.getInt("cache_email") > 0 ) {
            GlobalFunctions.cache_email = appData.getInt("cache_email");
        }

        if( appData.has("disable_access_control") && appData.getInt("disable_access_control") > 0 ) {
            GlobalFunctions.enable_access_control = false;
        }

        if( appData.has("upload_file_formats") && ! appData.getString("upload_file_formats").isEmpty() ) {
            GlobalFunctions.upload_file_formats = appData.getString("upload_file_formats");
        }

        if( appData.has("max_upload_mb") && appData.getInt("max_upload_mb") > 0 ) {
            GlobalFunctions.max_upload_mb = appData.getInt("max_upload_mb");
        }

        if( ! GlobalFunctions.app_path.isEmpty() ){
            String tmp_dir = "tmp";
            String tmp_email = "emails";
            String tmp_upload = "uploads";
            String tmp_downloads = "downloads";
            String tmp_cache = "cache";
            if( ! GlobalFunctions.app_file_seperator.isEmpty() && ! tmp_dir.isEmpty() ){
                String path2 = GlobalFunctions.app_path + tmp_dir;
                File dir = new File(path2);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                GlobalFunctions.app_file_tmp_dir = path2;

                if( ! tmp_upload.isEmpty() ){
                    String path3 = GlobalFunctions.app_file_tmp_dir + GlobalFunctions.app_file_seperator + tmp_upload;
                    File dir1 = new File(path3);
                    if (!dir1.exists()) {
                        dir1.mkdir();
                    }
                    GlobalFunctions.app_file_upload_dir = path3;
                }

                if( ! tmp_downloads.isEmpty() ){
                    String path4 = GlobalFunctions.app_file_tmp_dir + GlobalFunctions.app_file_seperator + tmp_downloads;
                    File dir2 = new File(path4);
                    if (!dir2.exists()) {
                        dir2.mkdir();
                    }
                    GlobalFunctions.app_file_download_dir = path4;
                }

                if( ! tmp_cache.isEmpty() ){
                    String path5 = GlobalFunctions.app_file_tmp_dir + GlobalFunctions.app_file_seperator + tmp_cache;
                    File dir3 = new File(path5);
                    if (!dir3.exists()) {
                        dir3.mkdir();
                    }
                    GlobalFunctions.app_cache_dir = path5;
                }

                if( ! tmp_cache.isEmpty() ){
                    String path6 = GlobalFunctions.app_file_tmp_dir + GlobalFunctions.app_file_seperator + tmp_email;
                    File dir3 = new File(path6);
                    if (!dir3.exists()) {
                        dir3.mkdir();
                    }
                    GlobalFunctions.app_email_dir = path6;
                }

            }
        }
    }

    public static JSONObject getApp(){
        return new JSONObject( GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "config.json") );
    }

    public static void reset(){
        GlobalFunctions.app_notice = "";
        GlobalFunctions.app_notice_type = "";
        GlobalFunctions.app_notice_only = false;
        GlobalFunctions.app_load_datatable = false;
        GlobalFunctions.app_reload_datatable = false;
        GlobalFunctions.app_popup_handle = "";
        GlobalFunctions.app_replace_container = "";
        GlobalFunctions.app_popup_title = "";
        GlobalFunctions.app_popup_style = "";
        GlobalFunctions.app_popup = false;
        GlobalFunctions.app_jsp_json = null;

        GlobalFunctions.glo_var = new JSONObject();
        /*if( GlobalFunctions.glo_var.has("dynamic") ) {
            GlobalFunctions.glo_var.put("dynamic", new JSONObject() );
        }*/
    }

    public static JSONObject get_json(String table){
        return new JSONObject( GlobalFunctions.fileGetContentsExternal( "dependencies" + GlobalFunctions.app_file_seperator + table +".json" ) );
    }

    public static JSONObject getProjectTables( JSONObject options ) {
        JSONObject r = new JSONObject();
        final File folder = new File(GlobalFunctions.app_path + "dependencies");
        JSONArray dependencies = GlobalFunctions.listFilesForFolder( folder, false );

        //System.out.println( dependencies );
        if( dependencies.length() > 0 ){
            for(int i3 = 0; i3 < dependencies.length(); i3++){
                if( dependencies.getString( i3 ).contains(".json") ){
                    JSONObject dep = get_json( dependencies.getString( i3 ).replace(".json", "") );
                    Boolean add = true;
                    if( dep.has("name") && dep.has("label") && dep.has("fields") && dep.has("labels") && dep.has("form_order") ){

                        if( options.has("basic_crud") ){
                            if( dep.has("exclude_crud") ){
                                add = false;
                            }else{
                                dep.remove("fields");
                                dep.remove("labels");
                                dep.remove("form_order");
                            }
                        }

                        if( add ){
                            r.put( dep.getString("name"), dep );
                        }
                    }
                }
            }
        }

        return r;
    }

    public static JSONArray listFilesForFolder(final File folder, Boolean CheckSubFolders ) {
        JSONArray files = new JSONArray();
        try {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    if (CheckSubFolders) {
                        JSONArray nfiles = listFilesForFolder(fileEntry, CheckSubFolders);
                        if (nfiles.length() > 0) {
                            for (int i3 = 0; i3 < nfiles.length(); i3++) {
                                files.put(nfiles.get(i3));
                            }
                        }
                    }
                } else {
                    //System.out.println(fileEntry.getName());
                    files.put(fileEntry.getName());
                }
            }
        }catch(Exception e){
            System.out.println( e.getMessage() );
        }

        return files;
    }

    public static JSONObject get_project_data(){
        //return new JSONObject().put("domain_name", "http://localhost:8080/");
        return new JSONObject("{\"development_mode\":true,\"company_name\":\"CBN ECM - b.2\",\"project_title\":\"CBN ECM - b.2\",\"auto_backup\":\"\",\"app_title\":\"CBN ECM - b.2\",\"slogan\":\"...ba wahala\",\"project_name\":\"pragma.com\",\"domain_name\":\"http://localhost:8080/ECM3/\",\"app_url\":\"http://localhost:8080/\",\"domain_name_only\":\"www.maybeachtech.com\",\"street_address\":\"No 42 Lobito Crescent, Wuse II\",\"full_street_address\":\"No 42 Lobito Crescent, Wuse II, Abuja 900288\",\"city\":\"Abuja\",\"state\":\"F.C.T\",\"country\":\"Nigeria\",\"phone\":\"+234 700-7467-943633\",\"support_line\":\"+234 814-9906-150\",\"admin_email\":\"maybeachtech@gmail.com\",\"sender_email\":\"notification@hyella.com\",\"reply_to_email\":\"maybeachtech@gmail.com\",\"accounts_email\":\"maybeachtech@gmail.com\",\"email\":\"info@pragma.com\",\"support_email\":\"support@pragma.com\",\"payment_reciept_email\":\"support@pragma.com\",\"delivery_email\":\"info@pragma.com\",\"admin_login_form_passkey\":\"19881011988\",\"smtp_auth_email\":\"notification@hyella.com\",\"smtp_auth_password\":\"BIG-bald86*-429-Min\"}");
    }

    public static JSONObject deleteQuery( JSONObject $qs ){
        JSONObject $r3 = new JSONObject();
        JSONObject $r2 = new JSONObject();
        String ins_query = "";
        String where = "";

        if( $qs.has("table") && $qs.has("where") && ! $qs.getString("where").isEmpty() ) {
			where = $qs.getString("where");
		}
		
        if( $qs.has("table") && $qs.has("conditions") && $qs.getJSONObject("conditions").length() > 0 ) {
			JSONObject dep = GlobalFunctions.get_json( $qs.getString("table") );
            JSONObject fields = dep.has( "fields" ) ? dep.getJSONObject( "fields" ) : new JSONObject();
			JSONObject labels = dep.has( "labels" ) ? dep.getJSONObject( "labels" ) : new JSONObject();
			
			for( int i2 = 0; i2 < $qs.getJSONObject("conditions").length(); i2++ ){
				String k2 = $qs.getJSONObject("conditions").names().getString(i2);
				if( fields.has( k2 ) ){
					if( ! where.isEmpty() ){
						where += " AND ";
					}
					JSONObject thisLabel = labels.has( fields.getString( k2 ) )?labels.getJSONObject( fields.getString( k2 ) ):( new JSONObject() );
					String thisType = thisLabel.has("form_field")?thisLabel.getString("form_field"):"";
					
					where += " ["+ fields.getString( k2 ) +"] = ";
					switch( thisType ){
						case "date-5":
						case "date-5time":
						case "system_time":
						case "number":
						case "decimal_long":
						case "decimal":
							where += $qs.getJSONObject("conditions").get(k2);
							break;
						default:
							where += "'"+ $qs.getJSONObject("conditions").get(k2) + "'";
							break;
					}
					
				}
				
			}
		}
		
        if( $qs.has("table") && ! where.isEmpty() ) {
            ins_query = "DELETE FROM " + $qs.getString("table") + " WHERE " + where + ";";
            $r3.put("query_type", "delete");
            $r3.put("query", ins_query);
            //System.out.println( DBConn.executeQuery( $r3 ) );
            $r2 = DBConn.executeQuery($r3);
        }

        return $r2;
    }

    public static JSONObject save_line_items( JSONObject $qs ){
        JSONObject $r2 = new JSONObject();
        JSONObject $r3 = new JSONObject();
        String $update = $qs.has("update")?$qs.getString("update"):"";
        String $delete = $qs.has("delete")?$qs.getString("delete"):"";
        String $main_update_field = $qs.has("main_update_field")?$qs.getString("main_update_field"):"";

        if( ! $delete.isEmpty() ){
            $update = $delete;
        }


        if( $qs.has("table") && $qs.has("line_items") ) {

            JSONArray $qr = $qs.getJSONArray("line_items");
            JSONArray $inserts = new JSONArray();
            String $ins_field = "";

            if ( $qr.length() > 0) {
                for(int i3 = 0; i3 < $qr.length(); i3++){
                    $r3 = $qr.getJSONObject( i3 );

                    String $key1 = "";
                    String $ins_val = "";
                    JSONObject $val1 = new JSONObject();

                    try {
                        if ($r3.names().length() > 0) {
                            for (int i2 = 0; i2 < $r3.names().length(); i2++) {
                                $key1 = $r3.names().getString(i2);
                                $val1 = $r3.getJSONObject($key1);

                                if (i3 == 0) {
                                    if (!$ins_field.isEmpty()) {
                                        $ins_field += ", ";
                                    }
                                    $ins_field += "[" + $key1 + "]";
                                }

                                if ($val1.has("form_field")) {
                                    if (!$ins_val.isEmpty()) {
                                        $ins_val += ", ";
                                    }

                                    if (!$update.isEmpty()) {
                                        $ins_val += "[" + $key1 + "] = ";
                                    }

                                    switch ($val1.getString("form_field")) {
                                        case "date-5":
                                        case "date-5time":
                                        case "system_time":
                                            $ins_val += $val1.getLong("value");
                                            break;
                                        case "number":
                                        case "decimal_long":
                                        case "decimal":
                                            $ins_val += $val1.getDouble("value");
                                            break;
                                        case "file":
                                            if ($val1.getString("value").isEmpty()) {
                                                $ins_val += " NULL ";
                                            } else {
                                                //$ins_val += " (SELECT * FROM OPENROWSET(BULK N'" + $val1.getString("value") + "', SINGLE_BLOB) AS CFG_FILE) ";
                                                //File inputFile = new File($val1.getString("value") );
                                                //try (FileInputStream inStream = new FileInputStream(inputFile)) {
                                                try {
                                                    byte[] fbyte = GlobalFunctions.convertFileContentToBlob($val1.getString("value"));
                                                    //pstmt.setBinaryStream(2, inStream);
                                                    //$ins_val += " (SELECT * FROM OPENROWSET(BULK N'" + $val1.getString("value") + "', SINGLE_BLOB) AS CFG_FILE) ";
                                                    //$ins_val += " ("+ inStream +") AS CFG_FILE) ";
                                                    String s = Base64.getEncoder().encodeToString(fbyte);

                                                    $ins_val += " '" + s + "' ";
                                                } catch (Exception e) {
                                                    GlobalFunctions.nw_dev_handler(
                                                            new JSONObject()
                                                                    .put("return", e.getMessage())
                                                                    .put("input", new JSONObject().put("val1", $val1).put("qs", $qs))
                                                                    .put("function", "GlobalFunctions.save_line_items")
                                                                    .put("exception", true)
                                                                    .put("fatal", true), e
                                                    );
                                                }
                                            }
                                            break;
                                        default:
                                            $ins_val += "'" + $val1.getString("value") + "'";
                                            break;
                                    }
                                }


                            }

                            if (!$ins_val.isEmpty()) {
                                if ($update.isEmpty()) {
                                    $inserts.put("(" + $ins_val + ")");
                                } else {
                                    $inserts.put($ins_val);
                                }
                            }
                        }
                    }catch (Exception e){
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("return", e.getMessage() )
                                        .put("input", new JSONObject().put("r3", $r3).put("qs", $qs) )
                                        .put("function", "GlobalFunctions.save_line_items" )
                                        .put("exception", true )
                                        .put("fatal", true ) , e
                        );
                    }

                }
               /* $inserts = new JSONArray();
                $inserts.put("4rgd");
                $inserts.put("3eff");
                $inserts.put("Calton");*/

                String ins_query = "";
                if( ! $ins_field.isEmpty() && $inserts.length() > 0 ){
                    //System.out.println( "(" + $ins_field + ")" );
                    //System.out.println( $inserts.join(",") );

                    for (int i4 = 0; i4 < $inserts.length(); i4++) {
                        if( ! ins_query.isEmpty() ){
                            ins_query += ", ";
                        }
                        ins_query += $inserts.getString(i4);
                    }

                    if( $update.isEmpty() ) {
                        ins_query = "INSERT INTO " + $qs.getString("table") + " (" + $ins_field + ") VALUES " + ins_query + ";";
                        $r3.put("query_type", "insert" );
                    }else{

                        $r3.put("query_type", "update" );
                        if( ! $delete.isEmpty() ){
                            $r3.put("query_type", "delete" );
                        }
                        ins_query = "UPDATE " + $qs.getString("table") + " SET " + ins_query + " WHERE";

                        if( ! $main_update_field.isEmpty() ){
                            ins_query += " [" + $qs.getString("table") + "].["+ $main_update_field +"]";
                        }else{
                            ins_query += " [" + $qs.getString("table") + "].[id]";
                        }

                        if( $qs.has("record_ids") && ! $qs.getString("record_ids").isEmpty() ){
                            ins_query += " IN ( "+ $qs.getString("record_ids") +") ;";
                        }else{
                            ins_query += " = '"+ $update +"';";
                        }
                    }
                    //System.out.println( ins_query );


                    $r3.put("query", ins_query );
                    //System.out.println( DBConn.executeQuery( $r3 ) );
                    $r2 = DBConn.executeQuery( $r3 );

                    if( $r2.has("row_count") && $r2.getInt("row_count") > 0 ){
                        if( $qs.has("record_id") ) {
                            String rid = $qs.getString("record_id");
                            if( ! rid.isEmpty() ){
                                JSONObject rjd = new JSONObject();
                                rjd.put("table", $qs.getString("table") );
                                rjd.put("id", rid );
                                rjd.put("setCache", true );
                                $r2.put( rid, get_record( rjd ) );

                            }
                        }
                    }
                }

            }

        }

        return $r2;
    }

    public static byte[] convertFileContentToBlob(String filePath){
        // create file object
        File file = new File(filePath);
        // initialize a byte array of size of the file
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream inputStream = null;
        try {
            // create an input stream pointing to the file
            inputStream = new FileInputStream(file);
            // read the contents of file into byte array
            inputStream.read(fileContent);
        } catch (Exception e) {
            //System.out.println( e.getMessage() );
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", e.getMessage() )
                            .put("input", filePath )
                            .put("function", "GlobalFunctions.convertFileContentToBlob" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        } finally {
            // close input stream

        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        }catch (Exception e){
            //System.out.println( e.getMessage() );
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", e.getMessage() )
                            .put("input", filePath )
                            .put("function", "GlobalFunctions.convertFileContentToBlob" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }
        return fileContent;
    }

    public static JSONObject get_record( JSONObject $qs ){
        JSONObject $r2 = new JSONObject();
        try {
            if( $qs.has("id") ) {
                String $where = " AND [id] = '" + $qs.getString("id") + "' ";
                if( $qs.has("use_where") && $qs.has("where") ) {
                    $where = $qs.getString("where");
                }
                $qs.put("where", $where);

                if( $qs.has("setCache") && $qs.getBoolean("setCache") ) {
                }else if( GlobalFunctions.app_data.has("use_get_record_cache") && GlobalFunctions.app_data.getBoolean("use_get_record_cache") ){
                    $r2 = GlobalFunctions.getCache( $qs );
                }

                if( ! $r2.has("id") ) {
                    JSONObject $qr = get_records($qs);

                    if ($qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) {
                        JSONArray $r32 = $qr.getJSONArray("row");
                        $r2 = $r32.getJSONObject(0);

                        if ($r2.has("id") && $qs.has("setCache") && $qs.getBoolean("setCache")) {
                            $qs.put("cache", $r2);
                            GlobalFunctions.setCache($qs);
                        }
                    }
                }
            }
        }catch (Exception e){
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", e.getMessage() )
                            .put("input", $qs )
                            .put("function", "GlobalFunctions.get_record" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }

        return $r2;
    }

    public static String getCalculatedValue( String value, JSONObject fieldLabel, JSONObject options ){
        String $value = value;

        if( fieldLabel.has("calculations") && fieldLabel.getJSONObject("calculations").has("type") ){
            JSONObject calc = fieldLabel.getJSONObject("calculations");
            switch( calc.getString("type") ){
                case "others":
                    break;
                default:
                    if( calc.has("reference_table") && ! calc.getString("reference_table").isEmpty() ){
                        calc.put("id", value );
                        calc.put("table", calc.getString("reference_table") );
                        $value = GlobalFunctions.get_record_name( calc );
                    }
                    break;
            }
        }

        return $value;
    }

    public static String get_record_name( JSONObject $qs ){
        String $name = "__no_ref__";
        if( $qs.has("id") && $qs.has("table") ){
            JSONObject cache = GlobalFunctions.getCache( $qs );
            String nameKey = $qs.has("name_key")?$qs.getString("name_key"):"name";
            $name = $qs.getString("id");

            if( cache.has(nameKey) ){
                $name = cache.getString(nameKey);
            }
            /*if( cache.has("username") ){
                $name = cache.getString("username");
            }*/
        }
        return $name;
    }


    public static String decrypt( String encodedBytes ){
        return new String( Base64.getDecoder().decode( encodedBytes.getBytes() ) );
    }

    public static JSONObject getCache( JSONObject $qs ){
        JSONObject cache = new JSONObject();
        if( $qs.has("id") && ! $qs.getString("id").isEmpty() && $qs.has("table") && ! $qs.getString("table").isEmpty() ){
            if( ! ( noCache.has($qs.getString("table") ) && noCache.getJSONObject($qs.getString("table") ).has( $qs.getString("id") ) ) ){

                try {
                    /*byte[] encodedBytes = Base64.getEncoder().encode("Test".getBytes());
                    System.out.println("encodedBytes " + new String(encodedBytes) );
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
                    System.out.println("decodedBytes " + new String(decodedBytes));*/

                    //cache = new JSONObject(GlobalFunctions.fileGetContentsRemote(GlobalFunctions.app_cache_dir + GlobalFunctions.app_file_seperator + $qs.getString("table") + GlobalFunctions.app_file_seperator + $qs.getString("id") ) );
                    String encodedBytes = GlobalFunctions.fileGetContentsRemote(GlobalFunctions.app_cache_dir + GlobalFunctions.app_file_seperator + $qs.getString("table") + GlobalFunctions.app_file_seperator + $qs.getString("id") );
                    String decoded = GlobalFunctions.decrypt( encodedBytes );

                    if( ! decoded.isEmpty() ) {
                        //System.out.println("xencodedBytes " + $qs.getString("id") + " -- " + decoded);
                        cache = new JSONObject( decoded );

                        //System.out.println("encodedBytes " + cache);
                        if (cache.length() <= 0) {
                            cache = new JSONObject();
                        }
                    }
                }catch (Exception e){
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", e.getMessage() )
                                    .put("input", $qs )
                                    .put("function", "GlobalFunctions.getCache" )
                                    .put("exception", true )
                                    .put("fatal", true ) , e
                    );
                }

                if( ! $qs.has("no_db") ){
                    if( ! cache.has("id") ){
                        //rebuild cache
                        //System.out.println("setting");
                        $qs.put("setCache", true );
                        cache = GlobalFunctions.get_record( $qs );
                        if (cache.length() <= 0) {
                            if( ! noCache.has( $qs.getString("table") ) ){
                                noCache.put( $qs.getString("table"), new JSONObject() );
                            }
                            noCache.getJSONObject($qs.getString("table") ).put( $qs.getString("id"), "1" );
                        }
                    }
                }
            }
        }
        return cache;
    }

    public static JSONObject setCache( JSONObject $qs ){
        JSONObject cache = new JSONObject();
        if( $qs.has("id") && $qs.has("table") && $qs.has("cache") ){
            try {
                byte[] encodedBytes = Base64.getEncoder().encode( $qs.getJSONObject("cache").toString().getBytes() );
                String cacheDir = GlobalFunctions.app_cache_dir;
                if( $qs.has("cache_type") && ! $qs.getString("cache_type").isEmpty() ){
                    switch( $qs.getString("cache_type") ){
                        case "email":
                            cacheDir = GlobalFunctions.app_email_dir;
                            break;
                    }
                }
                //System.out.println("encodedBytes " + new String(encodedBytes) );
                //System.out.println( "xxx cache" );
                //System.out.println( cacheDir + GlobalFunctions.app_file_seperator + $qs.getString("table") );

                File fileSaveDir = new File( cacheDir + GlobalFunctions.app_file_seperator + $qs.getString("table") );
                if (!fileSaveDir.exists()) {
                    fileSaveDir.mkdir();
                }

                GlobalFunctions.filePutContentsRemote( cacheDir + GlobalFunctions.app_file_seperator + $qs.getString("table") + GlobalFunctions.app_file_seperator + $qs.getString("id"), new String( encodedBytes ) );

            }catch (Exception e){
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", e.getMessage()  )
                                .put("input", $qs )
                                .put("function", "GlobalFunctions.setCache" )
                                .put("exception", true )
                                .put("fatal", true ) , e
                );
            }
        }
        return cache;
    }

    public static JSONObject delete_records( JSONObject $qs ){
        JSONObject r = new JSONObject();
        if( $qs.has("where") && ! $qs.getString("where").isEmpty() ){
            if( ! $qs.has("delete") ){
                $qs.put("delete", "DELETE ");
            }
            r = GlobalFunctions.get_records( $qs );
        }
        return r;
    }

    public static JSONObject get_records( JSONObject $qs ){
        //query db

        //$qs.put("query", "SELECT TOP 5 * FROM " + table + " WHERE record_status='1' ");
        String $query = "";
        String $query_type = "select";
        String $limit = "";
        String $where = "";
        String $group = "";
        String $join = "";
        String $order = "";
        String $limit1 = "";


        //"SELECT "+$limit1+" * FROM " + $qs.getString("table") + " WHERE record_status='1' " + $order + $limit;

        if( $qs.has("table") && ! $qs.getString("table").isEmpty() ){
            //$order = " ORDER BY ["+ $qs.getString("table") +"].[modification_date] DESC ";
            String $rWhere = "["+ $qs.getString("table") +"].[record_status]='1' ";

            if ( $qs.has("recordIDs") && $qs.getJSONArray("recordIDs").length() > 0 ) {
                String idsString = "";
                for (int i2 = 0; i2 < $qs.getJSONArray("recordIDs").length(); i2++) {
                    if( ! idsString.isEmpty() ){
                        idsString += ", ";
                    }
                    idsString += "'" + $qs.getJSONArray("recordIDs").getString( i2 ) + "'";
                }
                $rWhere += " AND [" + $qs.getString("table") + "].[id] IN ( "+ idsString +" ) ";
                $qs.put("index_field", "id");
            }

            if( $qs.has("limit") ){
                $limit1 = $qs.getString("limit");
            }
            if( ! $limit1.isEmpty() ){
                $limit1 = " TOP " + $limit1;
            }

            if( $qs.has("order") ){
                $order = $qs.getString("order");
            }

            if( $qs.has("where") ){
                $where = $qs.getString("where");
            }
            if( $qs.has("join") ){
                $join = $qs.getString("join");
            }
            if( $qs.has("group") ){
                $group = $qs.getString("group");
            }

            JSONObject $sel_f = new JSONObject();
            if( $qs.has("select_fields") ){
                $sel_f = $qs.getJSONObject("select_fields");
            }

            try {
                if ($qs.has("delete") && ! $qs.getString("delete").isEmpty()) {
                    $query = $qs.getString("delete");
                    $query_type = "delete";
                    $rWhere = "";
                    $limit1 = "";
                    $group = "";
                    $order = "";
                }else if ($qs.has("select") && !$qs.getString("select").isEmpty()) {
                    $query = $qs.getString("select");
                } else {
                    $query = "SELECT " + $limit1 + " ["+ $qs.getString("table") +"].* ";


                    if (!$qs.has("fields")) {
                        JSONObject dep = GlobalFunctions.get_json( $qs.getString("table") );
                        if (dep.has("fields")) {
                            $qs.put("fields", dep.getJSONObject("fields"));
                        }
                    }

                    if ($qs.has("fields")) {
                        JSONObject $fields = $qs.getJSONObject("fields");

                        if ($fields.names().length() > 0) {

                            String $key1 = "";
                            String $val1 = "";
                            String $v2 = " ["+ $qs.getString("table") +"].[id], ["+ $qs.getString("table") +"].[serial_num], ["+ $qs.getString("table") +"].[created_by], ["+ $qs.getString("table") +"].[creation_date], ["+ $qs.getString("table") +"].[modified_by], ["+ $qs.getString("table") +"].[modification_date] ";

                            if( $qs.has("select_prefix") ){
                                $v2 = $qs.getString("select_prefix");
                                if( ! $qs.has("order") ){
                                    $order = "";
                                }
                            }

                            for (int i2 = 0; i2 < $fields.names().length(); i2++) {
                                $key1 = $fields.names().getString(i2);
                                $val1 = $fields.get($key1).toString();

                                if ($sel_f.length() > 0) {
                                    if ($sel_f.has($key1)) {
                                        $v2 += ", ["+ $qs.getString("table") +"].[" + $val1 + "] as '" + $sel_f.getString($key1) + "' ";
                                    }/*else{
                                    $v2 += ", " + $val1 + " as '" + $sel_f.getString($key1) + "' ";
                                }*/
                                } else {
                                    $v2 += ", ["+ $qs.getString("table") +"].[" + $val1 + "] as '" + $key1 + "' ";
                                }

                            }
                            $query = "SELECT " + $limit1 + " " + $v2;
                        }
                    }
                }

                if ($qs.has("from") && !$qs.getString("from").isEmpty()) {
                    $query = $qs.getString("from");
                } else {
                    $query += " FROM " + $qs.getString("table");
                }

                $query += $join + " WHERE " + $rWhere + $where + $group + $order + $limit;
                //System.out.println( $query );
            }catch (Exception e){
                /*System.out.println( $query );
                System.out.println( e.getMessage() );*/
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", e.getMessage() )
                                .put("input", new JSONObject().put("query", $query).put("qs", $qs ) )
                                .put("function", "Endpoint.getViewName" )
                                .put("query", true )
                                .put("exception", true )
                                .put("fatal", true ) , e
                );
            }
        }else{

        }
        $qs.put("query_type", $query_type );
        $qs.put("query", $query );
        return DBConn.executeQuery( $qs );
    }

    public static void set_form_checksum( String $checksum, JSONObject form_content ){
        if( ! GlobalFunctions.glo_var.has("nwp_fm") ){
            GlobalFunctions.glo_var.put("nwp_fm", new JSONObject() );
        }
        if( GlobalFunctions.glo_var.has("nwp_fm") ){
            GlobalFunctions.glo_var.getJSONObject("nwp_fm").put( $checksum, form_content );
        }
    }

    public static JSONObject get_form_checksum( String $checksum, Boolean $clear ){
        JSONObject $r = new JSONObject();
        if( GlobalFunctions.glo_var.has("nwp_fm") && GlobalFunctions.glo_var.getJSONObject("nwp_fm").has( $checksum ) ){
            $r = GlobalFunctions.glo_var.getJSONObject("nwp_fm").getJSONObject( $checksum );
            $r.put("checksum", $checksum );

            if( $r.has("saved") && $clear ){
                GlobalFunctions.glo_var.getJSONObject("nwp_fm").remove( $checksum );
            }
        }
        return $r;
    }

    public static String get_form_headers( JSONObject $params ){

        JSONObject a = new JSONObject("{\"id\":\"\", \"uid\":\"1\", \"user_priv\":\"\", \"table\":\"\", \"table_id\":\"\", \"processing\":\"\", \"origin\":\"\", \"nw_more_data\":\"\"}");
        String $h = "";
        String $origin = "";

        String $table = $params.has("table")?$params.getString("table"):"";
        String tok = "234";

        $params.put("processing", md5( tok + $table ) );
        $params.put("origin", "" );
        String $av = "";

        if(  a.names().length() > 0 ){
            for(int i2 = 0; i2 < a.names().length(); i2++){
                $av = a.names().getString(i2);
                switch( $av ){
                    case "nw_more_data":
                        if( $params.has($av ) ){
                            $h += "<textarea style=\"display:none;\" name=\""+$av+"\">"+ $params.get($av ).toString() +"</textarea>";
                        }
                        break;
                    default:
                        $h += "<input type=\"hidden\" name=\""+$av+"\" value=\""+ ( $params.has($av)?$params.getString($av):"" ) +"\" />";
                        break;
                }
            }
        }

        if( GlobalFunctions.nwp_development_mode ){
            if( $params.has("action" ) ){
                $h += "<pre>action: "+ $params.getString("action" ) +"</pre>";
            }
            $h += "<pre>origin: "+$origin+"</pre>";
        }
        return $h;

    }

    public static String fileGetContentsRemote( String $file ){
        String content = "";
        if( ! $file.isEmpty() ) {
            File dViewFile = new File($file);
            if (dViewFile.exists()) {
                try {
                    //content = new String( Files.readAllBytes(Paths.get( path1 )) );
                    Scanner myReader = new Scanner(dViewFile);
                    while (myReader.hasNextLine()) {
                        content += myReader.nextLine();
                    }
                    myReader.close();
                    //content = "file was found";
                } catch (Exception e) {
                    //System.out.println("Unable to read: " + $file);
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "Unable to read: " + $file )
                                    .put("input", new JSONObject().put("file", $file) )
                                    .put("function", "GlobalFunctions.fileGetContentsRemote" )
                                    .put("exception", true )
                                    .put("fatal", true ) , e
                    );
                }
           }
        }
        return content;
    }

    public void readAllFilesinDir(){
        //not yet used
        String info = "";
        File folder = new File(GlobalFunctions.app_path + "settings");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                info += "<br>File " + listOfFiles[i].getName();
                info += "<hr>";
                info += GlobalFunctions.fileGetContentsRemote( listOfFiles[i].getAbsolutePath() );
                info += "<hr>";
            } else if (listOfFiles[i].isDirectory()) {
                info += "<br>Directory " + listOfFiles[i].getName() + " : " + listOfFiles[i].getAbsolutePath() + " : " + listOfFiles[i].getPath();
            }
        }
    }

    public static Boolean filePutContentsRemote( String $file, String $content ){
        Boolean success = false;
        try {
            Files.write(Paths.get($file),  $content.getBytes());
             success = true;
        }catch (Exception e){
            /*System.out.println("filePutContentsRemote");
            System.out.println( e.getMessage() );*/
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", e.getMessage() )
                            .put("input", new JSONObject().put("file", $file).put("content", $content) )
                            .put("function", "GlobalFunctions.filePutContentsRemote" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }
        return success;
    }

    public static String fileGetContents( String $file ){
        return GlobalFunctions.fileGetContentsRemote( GlobalFunctions.app_path + $file );
    }
    public static String fileGetContentsExternal( String $file ){
        //System.out.printf(GlobalFunctions.app_config_dir + $file);
        String conf = "";
        try {
            //check for env value
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("linux")) {
                GlobalFunctions.app_config_dir = GlobalFunctions.app_config_dir_linux;
                /*String myApp_config_dir = System.getenv("DEV1KIT_APP_CONFIG_DIR");
                if (!myApp_config_dir.trim().isEmpty()) {
                    GlobalFunctions.app_config_dir = myApp_config_dir;
                }*/
            }

            conf = GlobalFunctions.fileGetContentsRemote(GlobalFunctions.app_config_dir + $file);
            if( conf.trim().isEmpty() ) {
                //conf = GlobalFunctions.app_path + $file + "X" + GlobalFunctions.fileGetContentsRemote( GlobalFunctions.app_path + $file );
                conf = GlobalFunctions.fileGetContentsRemote( GlobalFunctions.app_path + $file );
            }
        }catch(Exception e) {
            conf = e.getMessage() + GlobalFunctions.app_config_dir + $file;
        }

        return conf;
    }

    public static String __perpare_buttons(JSONObject $task, String $unique_key, String pid, String $container ){
        String $h = "";

        $h = "prepare_buttons";
        return $h;
    }

    public static String get_age(Long start, Long end, Integer type, Integer option ){
        String $dob = "";

        $dob = ( time_passed_since_action_occurred( Double.valueOf( String.valueOf( end - start ) ), 5 ) );
        return $dob;
    }

    public static Integer NewIDCounter = 0;
    public static String LastIDTime = "";
    public static String GetNewID(String source ){
        String id = "";
        Date currentDate = new Date();

        if( ! GlobalFunctions.LastIDTime.equals( String.valueOf( currentDate.getTime() ) ) ){
            GlobalFunctions.NewIDCounter = 0;
        }
        ++GlobalFunctions.NewIDCounter;
        id = GlobalFunctions.NewIDCounter + source.substring(0,1);
        if( source.length() > 2 ){
            id += source.substring( source.length() - 2 );
        }
        GlobalFunctions.LastIDTime = String.valueOf( currentDate.getTime() );
        id +=  GlobalFunctions.LastIDTime;

        return id;
    }

    public static JSONObject GetAccessedFunctions(){
        JSONObject access = new JSONObject();

        access.put("super", "1");

        return access;
    }

    public static String urldecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static String rawurlencode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static String get_current_time(Integer $type ){
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if( $type == 2 ) {
            myFormatObj = DateTimeFormatter.ofPattern("YY/MM");
        }else if( $type == 3 ) {
            myFormatObj = DateTimeFormatter.ofPattern("yyyy");
        }else if( $type == 4 ) {
            myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        }else if( $type == 5 ) {
            myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        }
        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime myDateObj = LocalDateTime.now();

        if( $type == 1 ) {
            return myDateObj.format(myFormatObj) + "T" + myDateObj.format(myFormatObj2);
        }
        return myDateObj.format(myFormatObj);
    }

    public static String convert_timestamp_to_date( String $cell_value , String $s_form_field, Integer $type ){
        String pattern1 = "dd-MMM-yyyy";
        String pattern2 = "dd-MMM-yyyy HH:mm";
        if( $type == 1 ){
            pattern1 = "yyyy-MM-dd";
            pattern2 = "yyyy-MM-dd HH:mm";
        }
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern( pattern1 );

        switch ($s_form_field) {
            case "date-5time":
                myFormatObj = DateTimeFormatter.ofPattern( pattern2 );
                break;
        }
        if( $cell_value.isEmpty() ){
            $cell_value = "0";
        }
        /*LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.of("Africa/Lagos");
        ZoneOffset zoneOffSet = zone.getRules().getOffset(now);*/

        Integer ts = Integer.parseInt( $cell_value );
        LocalDateTime myDateObj = LocalDateTime.ofEpochSecond( ts,0, ZoneOffset.UTC );


        return myDateObj.format(myFormatObj);
    }

    public static Long convert_date_to_timestamp( String str , int $type, JSONObject $options ){

        long epochSeconds = 0;
        try {
            if( ! str.isEmpty() ) {
                if( str.contains("-") ) {
                    DateTimeFormatter dtf;
                    //str = "2014-07-04 04:05:10"; // UTC
                    if ($type == 1) {
                        str += ":00"; // UTC
                    } else {
                        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
                        LocalDateTime myDateObj = LocalDateTime.now(ZoneId.of("Etc/UTC"));

                        str += " " + myDateObj.format(dtf2);
                        //str += " 00:00:00"; // UTC
                    }
                    dtf = DateTimeFormatter.ofPattern("u-M-d H:m:s", Locale.ENGLISH);

                    LocalDateTime ldt = LocalDateTime.parse(str, dtf);
                    ZonedDateTime zdtUtc = ldt.atZone(ZoneId.of("Etc/UTC"));

                    Instant instant = zdtUtc.toInstant();
                    long epochMillis = instant.toEpochMilli();
                    epochSeconds = TimeUnit.SECONDS.convert(epochMillis, TimeUnit.MILLISECONDS);
                    //System.out.println(epochSeconds);   //1404446710

                    // Corresponding date-time in America/Toronto
                    //ZonedDateTime zdtToronto = instant.atZone(ZoneId.of("Africa/Lagos"));
                    //System.out.println(zdtToronto); //2014-07-04T00:05:10-04:00[America/Toronto]


                    //System.out.println( Instant.ofEpochSecond(epochSeconds) );
                }else{
                    epochSeconds = Long.valueOf( str );
                }
            }
        } catch (Exception e) {
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", e.getMessage() )
                            .put("input", new JSONObject().put("str", str).put("type", $type).put("options", $options) )
                            .put("function", "GlobalFunctions.convert_date_to_timestamp" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }

        return epochSeconds;
    }

    public static Map<String, String> frontend_tabs1() {
        /*String [][] home_tab = new String[5][5];
        home_tab[0][0] = "test";
        home_tab[0][1] = "test1";
        home_tab[1][0] = "test3";
        */


        String return1 ="";
        String return2 ="";
        String test1 = "";
        //HashMap<String, String> hm1 = new HashMap<>();
        Map<String, String> home_tab = new LinkedHashMap<>();
        Map<String, String> admin_tab = new LinkedHashMap<>();
        //List<String> list = new ArrayList<>();
        // Add Elements using put method
        home_tab.put("title", "<i class='icon-home'>&nbsp;</i>");
        admin_tab.put("title", "System");
        home_tab.put("action", "?action=all_reports&todo=display_inventory_menu&current_tab=");
        admin_tab.put("action", "?action=all_reports&todo=display_system_menu&current_tab=system");
        home_tab.put("module_name", "frontend_modules");
        admin_tab.put("module_name", "frontend_modules");
        home_tab.put("access_role", "home");
        admin_tab.put("access_role", "e039540b469a42cb2484ee61d3c25259");
        home_tab.put("no_access", "1");


        /*home_tab.get("action").add("?action=all_reports&todo=display_inventory_menu&current_tab=");
        home_tab.get("action").add("?action=all_reports&todo=display_system_menu&current_tab=system");
        home_tab.put("access_role", list);
        home_tab.get("access_role").add("home");
        home_tab.get("access_role").add("e039540b469a42cb2484ee61d3c25259");
        home_tab.put("module_name", list);
        home_tab.get("module_name").add("frontend_modules");
        home_tab.get("module_name").add("frontend_modules");
        home_tab.put("no_access", list);
        home_tab.get("no_access").add("1");*/


//        return1=admin_tab.toString();
//        return2=home_tab.toString();
        //test1=home_tab.get("title").toString();
        //hm2 = hm1.values();
        //hm2 = return1.get("title").get(1);
        //hm1.values();
        //return admin_tab;
        return admin_tab;
    }


    public static String md5(String input){
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String ucwords( String someString ) {
        String string1 = someString.substring(0,1).toUpperCase() + someString.substring(1).toLowerCase();

        return string1;
    }

    public boolean function_exists2( String className, String methodName, Boolean MethodType ){
        String staticMethodName = "methodStatic";
        String instanceMethodName = "methodInstance";
        Class<?>[] formalParameters = { int.class, String.class };
        Object[] effectiveParameters = new Object[] { 5, "hello" };
        String packageName = getClass().getPackage().getName();

        try {
            Class<?> clazz = Class.forName(packageName + "." + className);

            Method method = clazz.getMethod( methodName, formalParameters);
            method.invoke(null, effectiveParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean function_exists( String clas, String methodName ) throws ClassNotFoundException {
        boolean result = false;
        Class clazz = Class.forName( clas );

        for( Method method : clazz.getDeclaredMethods() ){
            if( method.getName().equals( methodName ) ){
                result = true;
                break;
            }
        }

        return result;
    }

    public static String view_details( JSONObject jget, JSONObject $opt ){
        String $return = "";
        //System.out.println( view_details( jget, $opt ) );

        if( ! jget.has("record") && jget.has("table") && jget.has("id") && ! jget.getString("id").isEmpty() ){
            JSONObject dep = GlobalFunctions.get_json( jget.getString("table") );
            JSONObject $fields = dep.has( "fields" ) ? dep.getJSONObject( "fields" ) : new JSONObject();
            JSONObject $labels = dep.has( "labels" ) ? dep.getJSONObject( "labels" ) : new JSONObject();
            JSONArray $formOder = dep.has( "form_order" ) ? dep.getJSONArray( "form_order" ) : new JSONArray();
            JSONArray viewOptionFields = new JSONArray();
            if( ! jget.has("nwp2_action") ){
                jget.put("nwp2_action", jget.getString("table") );
            }

            if( $formOder.length() > 0 && $fields.names().length() > 0 ) {
                JSONObject grd = new JSONObject();
                grd.put("id", jget.getString("id"));
                grd.put("fields", $fields );
                grd.put("table", jget.getString("table"));
                JSONObject $rd = GlobalFunctions.get_record(grd);
                JSONObject $fieldDetails = new JSONObject();

                if( $rd.has("id") ) {

                    for( int i1 = 0; i1 < $formOder.length(); i1++ ){
                        String $field_id = $formOder.getString( i1 );
                        if( $labels.has( $field_id ) ){
                            $fieldDetails = $labels.getJSONObject( $field_id );

                            Boolean showField = true;
                            if( $fieldDetails.has("no_view") ) {
                                showField = false;
                            }else if(  $fieldDetails.has("display_position") ) {
                                switch( $fieldDetails.getString("display_position") ){
                                    case "do-not-display-in-table":
                                        showField = false;
                                        break;
                                }
                            }

                            if( showField && ! ( $fieldDetails.has("display_field_label") && ! $fieldDetails.getString("display_field_label").isEmpty() ) && $fieldDetails.has("field_label") ) {
                                $fieldDetails.put("display_field_label", $fieldDetails.getString("field_label") );
                            }

                            if( showField && $fieldDetails.has("field_identifier") && $fieldDetails.has("display_field_label") ) {
                                if( ! $rd.getString($fieldDetails.getString("field_identifier")).isEmpty() ) {
                                    $fieldDetails.put("label", $fieldDetails.getString("display_field_label"));
                                    $fieldDetails.put("value", $rd.get($fieldDetails.getString("field_identifier")));
                                    viewOptionFields.put($fieldDetails);
                                }
                            }
                        }
                    }

                    String modifier = "";
                    if( $rd.has("modified_by") ){
                        $fieldDetails = new JSONObject();
                        $fieldDetails.put("label", "Last Modified");
                        modifier = $rd.getString("modified_by");
                        String sysValue = GlobalFunctions.get_record_name( new JSONObject().put("id", modifier ).put("table", "users" ) );
                        if( $rd.has("modification_date") ){
                            sysValue += " " + GlobalFunctions.convert_timestamp_to_date( $rd.getString("modification_date"), "date-5time", 0 );
                        }
                        $fieldDetails.put("value", sysValue );
                        viewOptionFields.put($fieldDetails);
                    }

                    if( $rd.has("created_by") && ! modifier.equals( $rd.getString("created_by") ) ){
                        $fieldDetails = new JSONObject();
                        $fieldDetails.put("label", "Created By");
                        String sysValue = GlobalFunctions.get_record_name( new JSONObject().put("id", $rd.getString("created_by") ).put("table", "users" ) );
                        if( $rd.has("creation_date") ){
                            sysValue += " " + GlobalFunctions.convert_timestamp_to_date( $rd.getString("creation_date"), "date-5time", 0 );
                        }
                        $fieldDetails.put("value", sysValue );
                        viewOptionFields.put($fieldDetails);
                    }

                    $opt.put("fields", viewOptionFields );
                    jget.put("record", $rd );

                    if( ! $opt.has("buttons") ){
                        if( dep.has("datatable_options") ){
                            JSONObject $btn_data = new JSONObject();
                            if( dep.getJSONObject("datatable_options").has("more_actions") ){
                                $btn_data.put( "more_actions", dep.getJSONObject("datatable_options").getJSONObject("more_actions") );
                            }
                            if( dep.getJSONObject("datatable_options").has("utility_buttons") ){
                                $btn_data.put( "utility_buttons", dep.getJSONObject("datatable_options").getJSONObject("utility_buttons") );
                            }
                            if( $btn_data.length() > 0 ){
                                $btn_data.put("hide", "no_split_view" );

                                $btn_data.put( "html_replacement_selector", "datatable-split-screen-" + jget.getString("nwp2_action") );
                                $btn_data.put( "params", "&html_replacement_selector=" + $btn_data.getString( "html_replacement_selector" ) );
                                $btn_data.put( "phtml_replacement_selector", ( jget.has("phtml_replacement_selector") && ! jget.getString("phtml_replacement_selector").isEmpty() )?jget.getString("phtml_replacement_selector"):( jget.has("html_replacement_selector")?jget.getString("html_replacement_selector"):"" ) );
                                $btn_data.put( "selected_record", $rd.getString("id") );
                                $btn_data.put( "table", jget.getString("nwp2_action") );

                                $opt.put( "buttons", nwpDataTable.getButtons( $btn_data ) + "<br /><br />" );
                            }
                        }
                    }

                }else{
                    $return = "Record "+ jget.getString("id") +" not found or may have been deleted";
                }
            }else{
                $return = "Unable to Interprete Fields from: " + "dependencies/" + jget.getString("nwp2_source") + ".json";
            }
        }

        if( jget.has("record") && jget.has("id") && ! jget.getString("id").isEmpty() ){

            JSONObject $item = jget.getJSONObject("record");

            String $table = "";

            if( $item.has("id") ){

                String $key = "";

                if( $opt.has("buttons") ){
                    $return += $opt.getString("buttons");
                }
                if( $opt.has("title0") ) {
                    $return += "<h4>" + $opt.getString("title0") + "</h4>";
                }
                $return += "<div class=\"table-responsive bg-white \">";

                if( $opt.has("title1") ) {
                    $return += "<h4>" + $opt.getString("title1") + "</h4>";
                }


                $return += "<table class=\"align-middle mb-0 table table-borderless table-striped table-hover\">" +
                        "<tbody>";

                if( $opt.has("title2") ){
                    $return += "<tr><td class=\""+ ( $opt.has("title2_class")?$opt.getString("title2_class"):"" ) +"\">"+ $opt.getString("title2") +"</td></tr>";
                }

                if( $opt.has("fields") && $opt.getJSONArray("fields").length() > 0 ){
                    JSONArray $opt_fields = $opt.getJSONArray("fields");

                    for( int ix = 0; ix < $opt_fields.length(); ix++ ){
                        //String $ofk = $opt_fields.names().getString(ix);
                        JSONObject $ofv = $opt_fields.getJSONObject(ix);

                        if( $ofv.length() > 0 ){
                            $return += "<tr>" +
                                    "<td>" +
                                    "        <div class=\"widget-content p-0\">" +
                                    "          <div class=\"widget-content-wrapper\">" +
                                    "            <div class=\"widget-content-left flex2\">" +
                                    "              <div class=\"widget-heading\">"+ ( $ofv.has("label") ? $ofv.getString("label") : "" ) +"</div>" +
                                    "              <div class=\"widget-subheadingx opacity-7x\">"+ ( $ofv.has("value") ? GlobalFunctions.getValue( $ofv.getString("value"), $ofv, new JSONObject() ) : "") +"</div>" +
                                    "            </div>" +
                                    "          </div>" +
                                    "        </div>" +
                                    "      </td>" +
                                    "</tr>";

                        }
                    }

                }

                $return += "</tbody>" +
                        "    </table>" +
                        "  </div>";
            }else{
                $return += "Unable to Retrieve the Record from " + $table;
            }
        }else if( $return.isEmpty() ){
            $return += "Invalid Reference ID";
        }


        return $return;
    }

    public static String getValue( String value, JSONObject fieldLabel, JSONObject options ){
        String $s_form_field = "";
        if ( fieldLabel.has("form_field") ) {
            $s_form_field = fieldLabel.getString("form_field");
        }

        switch ($s_form_field) {
            case "picture":
            case "file":
            case "text-file":
                break;
            case "calculated":
                value = GlobalFunctions.getCalculatedValue(value, fieldLabel, new JSONObject() );
                break;
            case "date-5":
            case "date-5time":
                if( ! value.isEmpty() && ! value.equals("0") ) {
                    value = GlobalFunctions.convert_timestamp_to_date(value, $s_form_field, 0);
                }
                break;
            case "multi-select":
            case "checkbox":
            case "radio":
            case "select":
                JSONObject $options = GlobalFunctions.selectBoxOptions( fieldLabel );
                if( $options.length() > 0 && $options.has( value ) ){
                    value = $options.getString( value );
                }
                break;
            case "textarea":
                value = value.replaceAll("\n", "<br>");
                break;
            case "decimal_long":
            case "currency":
            case "decimal":
                if( ! value.isEmpty() ) {
                    DecimalFormat df = new DecimalFormat("###,###,###.##");
                    value = df.format( Double.valueOf( value ) );
                }
                break;
            case "number":
                if( ! value.isEmpty() ) {
                    DecimalFormat df2 = new DecimalFormat("###,###,###");
                    value = df2.format( Double.valueOf( value ) );
                }
                break;
            default:
                break;
        }


        if (fieldLabel.has("format_function")) {
            switch (fieldLabel.getString("format_function")) {
                case "__nwp_workflow_status_format":
                    value = "<b>" + value.replaceAll("_", " ").toUpperCase() + "</b>";
                    break;
                case "__nwp_workflow_flag_format":
                   // System.out.println(options);
                    if( fieldLabel.has("format_function_key") && ! fieldLabel.getString("format_function_key").isEmpty() ) {
                        if ( options.has("row") && options.getJSONObject("row").has(fieldLabel.getString("format_function_key") ) && ! options.getJSONObject("row").getString( fieldLabel.getString("format_function_key") ).isEmpty()) {
                            String fv = options.getJSONObject("row").getString( fieldLabel.getString("format_function_key") );
                            JSONObject $os = GlobalFunctions.get_list_box_options("ecm_priority_levels", new JSONObject().put("return_type", "2"));
                            //System.out.println($os);
                            if ($os.length() > 0 && $os.has(fv)) {
                                String fvs = "";
                                switch( fv ){
                                    case "2":
                                        fvs = "<small>"+ $os.getString( fv ) +"</small>";
                                        break;
                                    case "3":
                                        break;
                                    case "4":
                                        break;
                                    case "5":
                                    case "6":
                                    case "7":
                                        break;
                                }
                                value += "<br>" + $os.getString( fv );
                            }
                        }
                    }
                    break;
            }
        }

        return value;
    }

    public static JSONObject selectBoxOptions( JSONObject $field_details ){
        JSONObject $key = new JSONObject();
        //$key.put("meat","Calton Meat");
        //$key.put("grass","Bush Meat");

        if( $field_details.has( "options_array" ) && $field_details.getJSONObject( "options_array" ).length() > 0 ){
            $key = $field_details.getJSONObject( "options_array" );
        }else if( $field_details.has( "options" ) && !$field_details.getString( "options" ).isEmpty() ){
            if( ! $field_details.has( "form_field_options_separator" ) )$field_details.put( "form_field_options_separator", ";" );
            if( ! $field_details.has( "form_field_options_separator2" ) )$field_details.put( "form_field_options_separator2", ":" );

            List<String> $val2 = Pattern.compile($field_details.getString( "form_field_options_separator" )).splitAsStream( $field_details.getString( "options" ) ).collect( Collectors.toList());;
            for( Integer i1 = 0; i1 < $val2.size(); i1++ ){
                String v = $val2.get( i1 );
                if( $field_details.has( "form_field_options_separator2" ) && !$field_details.getString( "form_field_options_separator2" ).isEmpty() ){
                    List<String> $val3 = Pattern.compile($field_details.getString( "form_field_options_separator2" )).splitAsStream( v ).collect( Collectors.toList());
                    if( $val3.contains(1) ){
                        $key.put( $val3.get(0), $val3.get(1) );
                    }else{
                        $key.put( $val3.get(0), $val3.get(0) );
                    }
                }else{
                    $key.put( v, v );
                }
            }

        }else if( $field_details.has( "form_field_options" ) && !$field_details.getString( "form_field_options" ).isEmpty() ){
            JSONObject $field_details_$data = new JSONObject();
            try{
                $field_details_$data = $field_details.has( "data" ) ? $field_details.getJSONObject( "data" ) : new JSONObject();
            }catch( Exception e ){

            }

            if( $field_details_$data.has( "form_field_options_source" ) && ( $field_details_$data.getString( "form_field_options_source" ).equals( "2" ) || $field_details_$data.getString( "form_field_options_source" ).equals( "list_box_class" ) ) ){

                //if( $field_details_$data.has( "form_field_options_value" ) && $field_details_$data.getJSONObject( "form_field_options_value" ).length() > 0 ){
                JSONObject $inp = new JSONObject();
                $inp.put( "return_type", "2" );
                JSONObject $ex = new JSONObject();
                //$ex = $field_details_$data.getJSONObject( "form_field_options_value" );

                $ex = GlobalFunctions.get_list_box_options( $field_details.getString( "form_field_options" ), $inp );
                //System.out.println($ex);
                if( $ex.length() > 0 ){
                    $key = $ex;
                }

            }else{
                $key = GlobalFunctions.selectBoxOptions( $field_details.getString( "form_field_options" ) );
            }
        }else{
        }

        return $key;
    }

    public static JSONObject get_list_box_options(String $key, JSONObject $opt){

        JSONObject r = new JSONObject();
        if( !$key.isEmpty() && $opt.has("return_type") ){

            JSONObject grd = new JSONObject();
            grd.put("id", md5( $key ) );
            grd.put("table", "list_box_options");
            //JSONObject $rd = get_record( grd );

            JSONObject dep2 = GlobalFunctions.get_json("list_box_options");
            JSONObject $qf2 = dep2.getJSONObject("fields");
            grd.put("where", " AND [list_box_options].[" + $qf2.getString("key") + "] = '" + $key + "' " );
            grd.put("use_where", true );
            JSONObject $rd = GlobalFunctions.get_record(grd);

            if( $rd.has("data") ){
                JSONObject $r = new JSONObject( ( $rd.getString("data") ) );
                //System.out.println($r);

                if( $r.has("options") && $r.getJSONObject("options").length() > 0 ){
                    $r.put("data2", new JSONObject() );

                    //System.out.println($r.getJSONObject("options").toString());

                    switch( $opt.getString("return_type") ){
                        case "2":
                            r = $r.getJSONObject("options");
                            break;
                        case "1":
                            for( int i = 0; i < $r.getJSONObject("options").length(); i++ ){
                                String $ek = $r.names().getString(i);
                                JSONObject $ev = $r.getJSONObject($ek);

                                $r.getJSONObject("data2").getJSONObject("keys").put(String.valueOf(i), $ek );
                                $r.getJSONObject("data2").getJSONObject("values").put(String.valueOf(i), $ev );
                            }

                            if( r.length() <= 0 ){
                                r = new JSONObject( $r.getJSONObject("data2").toString() );
                            }
                            break;
                    }



                }

            }
        }

        return r;
    }

    public static String stripslashes( String string ){
        return string.replace("\\\\", "!~!").replace("\\", "").replace("!~!", "\\");
    }

    public static JSONObject selectBoxOptions( String key ){
        JSONObject $sel = new JSONObject(GlobalFunctions.fileGetContentsExternal("settings"+ GlobalFunctions.app_file_seperator +"select_options.json"));
        JSONObject $selr = new JSONObject();
        if( $sel.has( key ) ){
            $selr = $sel.getJSONObject( key );
        }
        return $selr;
    }

    public static String get_file_upload_form_field( JSONObject $settings ){
        String $field_id = $settings.has("field_id") ? $settings.getString("field_id") : "";
        int $t = $settings.has("t") ? $settings.getInt("t") :0;
        String $attr = $settings.has("attributes") ? $settings.getString("attributes") :"";
        String $acceptable_files = $settings.has("acceptable_files_format") ? $settings.getString("acceptable_files_format") :"";

        if( $settings.has("hide_on_select") && ! $settings.getString("hide_on_select").isEmpty() ){
            $attr += " hide_on_select=\"1\" ";

            if( $settings.has("value") && ! $settings.getString("value").isEmpty() ){
                $attr += " data-value=\"1\" ";
            }
        }

        if( $settings.has("label") && ! $settings.getString("label").isEmpty() ){
            $attr += " label=\""+ $settings.getString("label") +"\" ";
        }
        if( $settings.has("value") && ! $settings.getString("value").isEmpty() ){
            $attr += " value=\""+ $settings.getString("value") +"\" ";
        }

        String $h_content_loop = "<input alt=\"file\" type=\"hidden\" class=\""+$field_id+"-replace\" "+$attr+" data-id=\"upload-box-"+ $field_id +"\" />";

        $h_content_loop += "<textarea class=\"form-control\" name=\""+ $field_id+ "_json\" style=\"display:none;\"></textarea>";

        $h_content_loop += "<img id=\""+$field_id +"-img\" class=\"form-gen-element-image-upload-preview\" style=\"display:none;\" /><div class=\"controls cell-element upload-box \" id=\"upload-box-"+$field_id+"\">";

        $h_content_loop += "<input type=\"file\" class=\"form-control uploaded-file\" name=\""+$field_id+"\" id=\""+$field_id+"\" acceptable-files-format=\""+ $acceptable_files+"\" "+ $attr +" />";
        $h_content_loop += "<textarea class=\"form-control\" name=\""+ $field_id +"_json\" style=\"display:none;\"></textarea>";
        $h_content_loop += "<span class=\"input-status\"></span></div>";

        String $fc = "";
        if( $settings.has("field_label") && ! $settings.getString("field_label").isEmpty() && $settings.has("value") && ! $settings.has("value") && $settings.has("pagepointer") && ! $settings.getString("pagepointer").isEmpty() ){
            //$fc = get_uploaded_files( $settings.getString("pagepointer") , $settings.getString("value"), $settings.getString("field_label"), $field_id, new JSONObject() );
        }
        $h_content_loop += "<div class=\"file-content\" id=\""+$field_id+"-file-content\">"+$fc+"</div>";

        return $h_content_loop;
    }

    public static String time_passed_since_action_occurred( Double $seconds , Integer $format ){
        String $r = "";
        String $tyears = "";
        String $t = "";
        if($seconds < 0){
            $tyears = "just now";
        }else{


            Double $one_year = 31536000.00;
            if( $seconds > $one_year ){
                Double $new_seconds1 = ( $seconds / $one_year );
                Double $new_seconds = Math.floor( $new_seconds1 );

                $seconds -= ( $one_year * $new_seconds );
                $tyears = $new_seconds + " yr(s) ";

                switch($format){
                    case 4:
                        return String.valueOf( Math.round( $new_seconds1 ) ) + " yr(s) ";
                }

            }

            JSONArray $comp = new JSONArray().put(1.0).put(2.0).put(2.0).put(2.0).put(2.0);
            JSONArray $div = new JSONArray().put(60).put(60).put(24).put(30).put(12);
            JSONArray $label_for_time_only = new JSONArray().put("secs").put("mins").put("hrs").put("days").put("mths");


            //Test if time is in seconds
            Double $curve = 1.0;
            Double $ti;
            for(int $x=0; $x< $div.length(); $x++){
                $ti = $seconds / $curve;
                if($ti > $comp.getInt( $x ) ){
                    $t = Math.round($ti)+" "+$label_for_time_only.getString( $x );
                }
                $curve *= $div.getInt( $x );
            }
        }
        return $tyears + $t;
    }

    public static String nw_pretty_number(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;


        if(size < sizeMb)
            return df.format(size / sizeKb)+ " K";
        else if(size < sizeGb)
            return df.format(size / sizeMb) + " M";
        else if(size < sizeTerra)
            return df.format(size / sizeGb) + " G";

        return "";
    }

    public static String getEmailContent( JSONObject $e ){
        String html = "";
        String $project_title = GlobalFunctions.app_title;

        if( $e.has("add_stylesheet") && $e.getBoolean("add_stylesheet") ){
            html += "<style>" + GlobalFunctions.fileGetContents("settings"+ GlobalFunctions.app_file_seperator +"email_style.css") + "</style>";
        }

        html += "<table id=\"email-table\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color:#dee0e2; padding-top:20px; padding-bottom:20px;\">";
        html += "<tr>";
        html += "<td>";
        html += "<center>";
        html += "<table id=\"email-table-1\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  style=\"max-width:500px; height:100%; background-color:#f9f9f9; border-bottom:1px solid #d6e9c6;  box-shadow:2px 2px 2px #ddd;\">";

        if( $e.has("show_title") && $e.getBoolean("show_title") ) {
            html += "<tr class=\"email-title\" style=\"background-color:#161b2d; color:#fff;\">";
            html += "<td valign=\"top\" style=\"text-align:center; padding:20px;\">";

            html += "<strong style='line-height:60px; font-size:22px;'>" + $project_title + "</strong>";

            html += "</td>";
            html += "</tr>";
        }

        if( $e.has("show_date") && $e.getBoolean("show_date") ) {
            html += "<tr class=\"email-date\">";
            html += "<td style=\"text-align:right; padding:20px;\">" + get_current_time(4) + "</td>";
            html += "</tr>";
        }

        html += "<tr class=\"email-body\">";
        html += "<td valign=\"top\" style=\"padding:20px;\">";
            if( $e.has("full_name") && ! $e.getString("full_name").isEmpty() ){
                html += "<strong>Dear " + $e.getString("full_name") + ",</strong>";
            }

        if( $e.has("title") && ! $e.getString("title").isEmpty() ) {
            html += "<h4 style=\"text-align:center;\">";
            html += "<strong>"+ $e.getString("title") +"</strong>";
                    html += "</h4>";
            html += "<br />";
            html += "<hr />";
        }

        if( $e.has("info_html") && ! $e.getString("info_html").isEmpty() ) {
            html += "<div class=\"textdark\">" + $e.getString("info_html");
            html += "</div><br />";
        }

        if( $e.has("html_data") && $e.getJSONObject("html_data").length() > 0 ) {
            html += "<div class=\"textdark\">";
            for(Object lbl : $e.getJSONObject("html_data").names() ){
                String label = lbl.toString();
                if( $e.getJSONObject("html_data").has( label ) ){
                    html += "<b style=\"display:block; margin-bottom:0px; margin-top:10px;\">" + label + ":</b>";
                    html += $e.getJSONObject("html_data").getString( label ) + "<br />";
                }
            }
            html += "</div><br />";
        }

        if( $e.has("html") && ! $e.getString("html").isEmpty() ) {
            html += "<div class=\"textdark\">" + $e.getString("html");
            html += "</div><br />";
        }
        html += "</td>";
        html += "</tr>";

        html += "<tr class=\"email-footer\" style=\"background-color:#f5f5f5;\">";
        html += "<td style=\"padding:20px; text-align:center; font-size:12px;\">";
        html += get_current_time(3) + " " + $project_title + ". ALL Rights Reserved";
        html += "</td>";
        html += "</tr>";
        html += "</table>";
        html += "</center>";
        html += "</td>";
        html += "</tr>";
        html += "</table>";

        return html;
    }

    public static boolean validateEmail(String email) {
        String EMAIL_REGEX =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void nw_dev_handler( JSONObject $input, Exception e ){
        Boolean print = false;
        String logFile = "fatal";
        String logFileMsg = "";
        String logSource = "";

        if( $input.has("fatal") && $input.getBoolean("fatal") ){
            print = true;
        }

        if( GlobalFunctions.nwp_development_mode ){
            if( $input.has("non_fatal") && $input.getBoolean("non_fatal") ) {
                print = true;
            }
            if( $input.has("query") && $input.getBoolean("query") ) {
                print = true;
            }
        }else{
            if( $input.has("log") && $input.getBoolean("log") ){
                print = true;
                logFile = "info";
            }

            if( GlobalFunctions.app_data.has("log_non_fatal") && GlobalFunctions.app_data.getBoolean("log_non_fatal") ){
                if( $input.has("non_fatal") && $input.getBoolean("non_fatal") ) {
                    print = true;
                    logFile = "non_fatal";
                }
            }
            if( GlobalFunctions.app_data.has("log_query") && GlobalFunctions.app_data.getBoolean("log_query") ){
                if( $input.has("query") && $input.getBoolean("query") ) {
                    print = true;
                    logFile = "query";
                }
            }
        }

        String addLine = "";
        if( $input.has("login") && $input.getBoolean("login") ) {
            logFile = "login";
            print = true;
            addLine = "\n";
        }else if( $input.has("audit") && $input.getBoolean("audit") ){
            logFile = "audit";
            print = true;
            addLine = "\n";
        }

        if( print ){
            if( $input.has("function") ) {
                logSource = "function: " + $input.getString("function");
            }
            logFileMsg += " -- " + $input.toString() + addLine;
        }

        if( $input.has("exception") && $input.getBoolean("exception") ) {
            //logFileMsg += " -- " +  e.getStackTrace().toString();
            StackTraceElement[] stackTrace = e.getStackTrace();

            // Convert the array to a string
            StringBuilder stackTraceAsString = new StringBuilder();
            for (StackTraceElement element : stackTrace) {
                stackTraceAsString.append(element.toString()).append("\n");
            }

            // Print or use the stack trace string as needed
            logFileMsg += "\n\nStack Trace:\n" + stackTraceAsString.toString();
            e.printStackTrace();
        }

        if( ! logFileMsg.isEmpty() ) {
            GlobalFunctions.logInfo( logFileMsg, GlobalFunctions.app_log + GlobalFunctions.app_file_seperator + logFile + ".log", logSource );
            System.out.println( logSource + logFileMsg);

            if( GlobalFunctions.app_data.has("log_retention_in_days") && GlobalFunctions.app_data.getInt("log_retention_in_days") > 0 ) {
                long ageThresholdMillis = GlobalFunctions.app_data.getInt("log_retention_in_days") * 24 * 60 * 60 * 1000; // 7 days in milliseconds
                if( GlobalFunctions.deleteOldFiles(GlobalFunctions.app_log, ageThresholdMillis) ) {
                    GlobalFunctions.logInfo("Retention Period Active: " + GlobalFunctions.app_data.getInt("log_retention_in_days"), GlobalFunctions.app_log + GlobalFunctions.app_file_seperator + "retention.log", "retention");
                }
            }
        }

    }

    public static void logInfo(String logMsg, String logFile, String logSource ){
        Logger logger = Logger.getLogger( logFile );
        FileHandler fileHandler = null;
        try {

            File file = new File( logFile );
            if( file.length() > 1000000 ){
                //rename log file and archive old log
                String newFileName = new Date() + " - " +  logSource;
                File newFile = new File( newFileName.replaceAll(":", "-") );
                file.renameTo(newFile);
                fileHandler = new FileHandler( logFile );
            }else{
                fileHandler = new FileHandler( logFile , true);
            }

            logger.addHandler(fileHandler);

            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.info(logMsg + "\n\n");
            fileHandler.close();
            fileHandler = null;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
            }
        }
    }

    public static boolean deleteOldFiles(String folderPath, long ageThresholdMillis) {
        File folder = new File(folderPath);
        Boolean r = false;

        File[] files = folder.listFiles();
        if (files != null) {
            long currentTimeMillis = System.currentTimeMillis();

            for (File file : files) {
                long lastModified = file.lastModified();
                if (currentTimeMillis - ageThresholdMillis > lastModified ) {
                    file.delete();
                    //System.out.println("Deleted file: " + file.getName() + ": ctime = " + currentTimeMillis  + ": age = " + ageThresholdMillis  + ": lastmod = " + lastModified );
                    r = true;
                }
            }
        }

        return r;
    }
    public static String getLog( String logFile ) {
        String h = "";
        try {
            h = Files.readString(Paths.get( logFile ));
        } catch (IOException e) {
            System.out.println( e.getMessage() );
        }
        return h;
    }

    public static JSONObject getGeneralSettings( String className ){
        JSONObject r = new JSONObject();
        String gsTable = "general_settings";

        JSONObject dep = GlobalFunctions.get_json( gsTable );
        if( dep.has("fields") ) {
            JSONObject $qf = dep.getJSONObject("fields");
            JSONObject $qs = new JSONObject();

            String $where = " AND [" + gsTable + "].[" + $qf.getString("classname") + "] = '" + className + "' ";
            $qs.put("where", $where);
            $qs.put("table", gsTable );
            $qs.put("index_field", "key" );
            JSONObject rd = GlobalFunctions.get_records($qs);
            if( rd.has("row") ){
                r = rd.getJSONObject("row");
            }
        }

        return r;
    }


    public static void main(String[] args) {
        System.out.println( getGeneralSettings( "cash_advance" ) );
        //System.out.println(frontend_tabs());

        /*ArrayList<Object> frontend_tabs_home_tab = new ArrayList<Object>();
        accessible_function.add("view");
        accessible_function.add("settings");
        accessible_function.add("home");
        accessible_function.add("profile");
    }*/


    }
}
