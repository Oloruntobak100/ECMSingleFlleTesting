package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import codes.GlobalFunctions;

import java.util.Date;

public class nwpFiles {
    public static String table_name = "files";
    public static String label = "Files";

    public static JSONObject getFile( JSONObject opt ){
        String error = "";
        JSONObject returnData = new JSONObject();

        try{
            if( opt.has("hash") && opt.has("token") && opt.has("id") ) {
                String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + opt.getString("token") + opt.getString("id") );
                //String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + opt.getString("token") + opt.getString("id") + GlobalFunctions.app_user_data.getString("id") );
                if( ! fileHash.equals( opt.getString("hash") ) ){
                    error = "<h4>Security Violation</h4>Restricted Resource";
                }
            }

            if( error.isEmpty() ) {
                if (opt.has("id")) {
                    JSONObject $r3 = new JSONObject();
                    JSONObject $files = new JSONObject();
                    $files.put("file_url", new JSONObject().put("field", "file_url").put("ext_field", "content").put("ext_type", "data").put("ext_data_field", "ext"));

                    JSONObject rjd = new JSONObject();
                    rjd.put("table", nwpFiles.table_name);
                    rjd.put("files", $files);
                    rjd.put("id", opt.getString("id"));
                    returnData = GlobalFunctions.get_record(rjd);

                    //System.out.println(returnData);
                    if (returnData.has("id")) {
                        if (returnData.has("nwp_file_found") && returnData.has("file_url") && !returnData.getString("file_url").isEmpty()) {

                        } else {
                            error = "File could not be retrieved from DB";
                        }
                    } else {
                        error = "File Record Not Found in DB";
                    }

                    System.out.println("Following flies are downloaded from database..");
                } else {
                    error = "Resource ID was not specified";
                }
            }
        } catch (Exception e) {
            error = e.getMessage();
        }

        if( ! error.isEmpty() ){
            returnData.put("error", error );
        }
        return returnData;
    }

    public static String getDetailsView( JSONObject jget ){
        String $r = "";
        JSONObject $vd = new JSONObject();
        String $table = nwpFiles.table_name;

        if( jget.has( "popup" ) && !jget.getString( "popup" ).isEmpty() ){
            GlobalFunctions.app_popup = true;
            GlobalFunctions.app_popup_title = "";
        }

        if( jget.has( "id" ) && !jget.getString( "id" ).isEmpty() ){

            JSONObject $qs = new JSONObject();
            $qs.put( "id", jget.getString( "id" ) );
            $qs.put( "table", $table );

            JSONObject $item = GlobalFunctions.get_record( $qs );

            String $container = jget.has( "html_replacement_selector" ) ? jget.getString( "html_replacement_selector" ) : "";
            String $container2 = jget.has( "phtml_replacement_selector" ) ? jget.getString( "phtml_replacement_selector" ) : "";
            //echo '<pre>';print_r( json_encode($data) );echo '</pre>';
            if( $item.has( "id" ) ){

                jget.put( "record", $item );
                JSONArray $rf = new JSONArray();
                JSONObject $data = GlobalFunctions.get_json( $table );
                String $key = "";

                JSONObject $fields = $data.has( "fields" ) ? $data.getJSONObject( "fields" ) : new JSONObject();
                JSONObject $labels = $data.has( "labels" ) ? $data.getJSONObject( "labels" ) : new JSONObject();

                //JSONObject $workflow_settings = $data.has("workflow_settings") ? $data.getJSONObject("workflow_settings") : new JSONObject();

                JSONObject $jval;
                String $value;


                String $title = "";
                $key = "name";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $title = $value;
                    //$rf.put( new JSONObject().put( "label", "Title" ).put( "value", $value ) );
                }

                $key = "created_by";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "By" ).put( "value", GlobalFunctions.get_record_name( new JSONObject().put("id", $value ).put("table", "users" ) ) ) );
                }

                $key = "date";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                }
                /*$key = "creation_date";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( ! $value.isEmpty() ){
                    $rf.put( "lifsb", new JSONObject().put( "label", "Creation Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                }*/

                $key = "content";
                String ext = "";
                JSONObject fileAttr = nwpFiles.getFileAttr( $item );
                if( fileAttr.has("ext") ){
                    $title += "." + fileAttr.getString("ext");
                    ext = fileAttr.getString("ext");
                }
                if (fileAttr.has("size")) {
                    $rf.put( new JSONObject().put("label", "Size").put("value", fileAttr.getString("size") ));
                }
                if (fileAttr.has("error") && ! fileAttr.getString("error").isEmpty() ) {
                    $rf.put( new JSONObject().put("label", "File Attr Error").put("value", fileAttr.getString("error") ));
                }

                $vd.put( "fields", $rf );
                $vd.put( "title1", $title );
                if( GlobalFunctions.app_popup ){
                    GlobalFunctions.app_popup_title = $title;
                }

                if( !jget.has( "hide_buttons" ) ){
                    JSONObject $btn_data = new JSONObject();

                    if( $data.getJSONObject("datatable_options").has("more_actions") ){
                        $btn_data.put( "more_actions", $data.getJSONObject("datatable_options").getJSONObject("more_actions") );
                    }
                    if( $data.getJSONObject("datatable_options").has("utility_buttons") ){
                        $btn_data.put( "utility_buttons", $data.getJSONObject("datatable_options").getJSONObject("utility_buttons") );
                    }

                    JSONObject moreActions = new JSONObject();
                    if( $btn_data.has("more_actions") ){
                        moreActions = $btn_data.getJSONObject("more_actions");
                    }

                    //all other file formats
                    /*String fileToken = GlobalFunctions.GetNewID("ft");
                    String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + fileToken + $item.getString("id") );
                    //String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + fileToken + $item.getString("id") + GlobalFunctions.app_user_data.getString("id") );

                    JSONObject download = new JSONObject("{\"no_dt_table\":1,\"action\":\"files\",\"todo\":\"download_file\",\"title\":\"Download\",\"button_class\":\"btn-dark\",\"no_access\":1,\"text\":\"Download <i class='icon-download-alt'></i>\",\"html_replacement_key\":\"phtml_replacement_selector\"}");
                    download.remove("todo" );
                    download.put("url", GlobalFunctions.app_request_source + "?action=download&source=" + nwpFiles.table_name + "&resource=" + $item.getString("id") + "&t=" +  fileToken + "&h=" + fileHash );
                    download.put("attributes", " target='_blank' ");*/
                    JSONObject inputItem = $item;
                    inputItem.put("type", "download");
                    moreActions.put("download", nwpFiles.getFileURL( inputItem ) );


                    //if pdf or images
                    if( nwpFiles.getFilesThatOpen( new JSONObject().put("ext", ext ) ) ){
                        inputItem.put("type", "open");
                        moreActions.put("open", nwpFiles.getFileURL( inputItem ) );
                    }

                    JSONObject del_button = nwpFiles.deleteFile( $item );
                    if( del_button.has("button") ){
                        moreActions.put("delete_file", del_button.getJSONObject("button") );
                    }


                    $btn_data.put( "more_actions", moreActions );

                    //JSONObject more_data = new JSONObject();
                    //more_data.put("additional_params", "&workflow="+ $item.getString("id"));

                    $btn_data.put("hide", "no_split_view" );
                    $btn_data.put( "html_replacement_selector", "datatable-split-screen-" + $table );
                    $btn_data.put( "params", "&html_replacement_selector=" + $btn_data.getString( "html_replacement_selector" ) );
                    $btn_data.put( "phtml_replacement_selector", $container2 );
                    $btn_data.put( "selected_record", $item.getString( "id" ) );
                    $btn_data.put( "table", $table );
                    //$btn_data.put("more_data", more_data );


                    $vd.put( "buttons", nwpDataTable.getButtons( $btn_data ) + "<br /><br />" );
                }


                $r = GlobalFunctions.view_details( jget, $vd );
            }else{
                $r = $table + "->getDetailsView: Unable to Retrieve Record";
            }
        }else{
            $r = $table + "->getDetailsView: Invalid Reference ID";
        }


        return $r;
    }

    public static JSONObject deleteFile( JSONObject $item ){
        JSONObject returnData = new JSONObject();
        String action = $item.has("action")?$item.getString("action"):"";
        Boolean checkCreator = true;
        Boolean getButton = false;
        Boolean deleteFile = false;
        Boolean getRecord = false;
        String $key = "";
        String $value = "";
        String error = "";

        switch( action.toLowerCase() ){
            case "delete":
                getRecord = true;
                deleteFile = true;
                break;
            default:
                getButton = true;
                break;
        }

        if( getRecord ){
            if( $item.has("id") && ! $item.getString("id").isEmpty() ){
                JSONObject $qs = new JSONObject();
                $qs.put( "id", $item.getString("id") );
                $qs.put( "table", nwpFiles.table_name );
                $item = GlobalFunctions.get_record( $qs );
                if( $item.has("id") && ! $item.getString("id").isEmpty() ){

                }else{
                    error = "<h4>Unable to Locate File in DB</h4>";
                }
            }else{
                error = "<h4>Invalid File Reference</h4>";
            }
        }

        if( error.isEmpty() && checkCreator ){
            JSONObject appData = GlobalFunctions.getApp();
            Boolean pass = false;
            if( appData.has("delete_my_uploads") && appData.getInt("delete_my_uploads") > 0 ) {
                $key = "created_by";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $value.equals( GlobalFunctions.app_user ) ){
                    pass = true;
                    if( appData.has("delete_my_uploads_in_secs") && appData.getInt("delete_my_uploads_in_secs") > 0 ) {
                        pass = false;
                        $key = "date";
                        $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                        if( !$value.isEmpty() ){
                            Date currentDate = new Date();
                            long ts = currentDate.getTime() / 1000;
                            long cp = Long.parseLong( String.valueOf( appData.getInt("delete_my_uploads_in_secs") + Integer.parseInt( $value ) ) );
                            if( cp > ts ){
                                pass = true;
                            }else{
                                if( deleteFile ) {
                                    error = "<h4>Access Denied</h4>File has exceeded the timeframe for delete";
                                }
                            }
                        }else{
                            if( deleteFile ) {
                                error = "<h4>Access Denied</h4>Unrecognized File Date";
                            }
                        }
                    }
                }else{
                    if( deleteFile ) {
                        error = "<h4>Access Denied</h4>Invalid File Owner";
                    }
                }
            }else{
                pass = true;
            }

            if( pass ){
                if( deleteFile ){
                    returnData.put("delete", true );
                }

                if( getButton ){
                    JSONObject del_button = new JSONObject("{\"no_dt_table\":1,\"action\":\"files\",\"todo\":\"delete_file\",\"title\":\"Delete\",\"text\":\"<i class='icon-trash'></i> Delete\",\"html_replacement_keyX\":\"phtml_replacement_selector\"}");
                    del_button.put("attributes", " confirm-prompt='Delete this file' ");
                    returnData.put("button", del_button);
                }
            }
        }

        if( deleteFile && returnData.has("delete") && returnData.getBoolean("delete") ){
            //System.out.println(returnData );
            JSONObject jdata = new JSONObject();
            jdata.put("table", nwpFiles.table_name );
            jdata.put("todo", "delete" );
            jdata.put("id", $item.getString("id") );
            jdata.put("post_data", new JSONObject().put("id", $item.getString("id")) );

            JSONObject $r2 = nwpDataTable.saveDataForm( jdata );
            /*JSONObject $sd = new JSONObject();
            $sd.put( "table", nwpFiles.table_name );
            $sd.put( "delete", $item.getString("id") );
            $sd.put( "record_id", $item.getString("id") );
            JSONObject $r2 = GlobalFunctions.save_line_items( $sd );*/

            if( $r2.has( "error") ){
                error = $r2.getString("error");
            }else{
                GlobalFunctions.app_notice_only = false;
            }
        }

        if( ! getButton && ! error.isEmpty() ){
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice = error;
            GlobalFunctions.app_notice_only = true;
            //GlobalFunctions.app_reload_datatable = true;
        }
        returnData.put("error", error );
        //System.out.println( returnData );

        return returnData;
    }

    public static JSONObject getFileAttr( JSONObject $item ){
        JSONObject returnData = new JSONObject();
        String $value = ( $item.has( "content" ) ) ? $item.getString( "content" ) : "";
        if( $value.isEmpty() ){
            returnData.put("error", "no content found in dataset...unable to get file attr" );
        }else{
            try {
                JSONObject $jval = new JSONObject(GlobalFunctions.urldecode($value));
                JSONObject $jp = $jval;
                if( ! $jp.has("ext") && $jval.names().length() > 0) {
                    //System.out.println($jval);
                    $jp = $jval.getJSONObject( $jval.names().getString(0) );
                }


                if ($jp.has("ext")) {
                    returnData.put("ext", $jp.getString("ext") );
                }

                if ($jp.has("size")) {
                    returnData.put("sizeLong", $jp.getLong("size") );
                    returnData.put("size", GlobalFunctions.nw_pretty_number($jp.getLong("size")) + "b");
                }
            }catch ( Exception e ){
                returnData.put("error", e.getMessage() );
            }
        }

        return returnData;
    }

    public static Boolean getFilesThatOpen( JSONObject $item ){
        String ext = $item.has("ext")?$item.getString("ext"):"";
        Boolean canOpen = false;

        switch( ext.toLowerCase() ){
            case "pdf":
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "svg":
                canOpen = true;
                break;
        }

        return canOpen;
    }

    public static JSONObject getFileURL( JSONObject $item ){
        String fileToken = GlobalFunctions.GetNewID("ft");
        String type = $item.has("type")?$item.getString("type"):"";
        Boolean checkCanOpen = $item.has("checkCanOpen")?$item.getBoolean("checkCanOpen"):false;

        String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + fileToken + $item.getString("id") );
        //String fileHash = GlobalFunctions.md5( GlobalFunctions.app_salter + fileToken + $item.getString("id") + GlobalFunctions.app_user_data.getString("id") );

        JSONObject fileAttr = new JSONObject();
        if( checkCanOpen ){
            fileAttr = nwpFiles.getFileAttr( $item );
            if( nwpFiles.getFilesThatOpen( fileAttr ) ){
                type = "open";
            }
        }

        JSONObject download = new JSONObject("{\"no_dt_table\":1,\"action\":\"files\",\"todo\":\"download_file\",\"title\":\"Download\",\"button_class\":\"btn-dark\",\"no_access\":1,\"text\":\"Download <i class='icon-download-alt'></i>\",\"html_replacement_key\":\"phtml_replacement_selector\"}");
        download.put("url", GlobalFunctions.app_request_source + "?action=download&source=" + nwpFiles.table_name + "&resource=" + $item.getString("id") + "&t=" +  fileToken + "&h=" + fileHash );

        switch( type ){
            case "open":
                download = new JSONObject("{\"no_dt_table\":1,\"action\":\"files\",\"todo\":\"open_file\",\"title\":\"Open and View Details\",\"text\":\"Open\",\"html_replacement_key\":\"phtml_replacement_selector\"}");
                download.put("url", GlobalFunctions.app_request_source + "?action=open&source=" + nwpFiles.table_name + "&resource=" + $item.getString("id") + "&t=" +  fileToken + "&h=" + fileHash  );
                break;
        }

        if( fileAttr.has("ext") ){
            download.put("ext", fileAttr.getString("ext") );
        }

        if( fileAttr.has("size") ){
            download.put("size", fileAttr.getString("size") );
            download.put("sizeLong", fileAttr.get("sizeLong") );
        }

        download.remove("todo" );
        download.put("attributes", " target='_blank' ");
        return download;
    }

    public static JSONObject save_files( JSONObject a ){
        String error = "";
        JSONObject r = new JSONObject();

        if( a.has("reference") && a.has("reference_table") && a.has("line_items") && a.getJSONArray("line_items").length() > 0 ) {
            JSONObject $gf = new JSONObject();
            $gf.put("reference", a.getString("reference") );
            $gf.put("reference_table", a.getString("reference_table") );
            if( a.has("community") ){
                $gf.put("community", a.getString("community") );
            }
            if( a.has("ward") ){
                $gf.put("ward", a.getString("ward") );
            }

            if( a.has("meta_data_key") ){
                $gf.put("meta_data_key", a.getString("meta_data_key") );
            }
            if( a.has("workflow") ){
                $gf.put("workflow", a.getString("workflow") );
            }
            if( a.has("state") ){
                $gf.put("state", a.getString("state") );
            }
            if( a.has("lga") ){
                $gf.put("lga", a.getString("lga") );
            }
            if( a.has("description") ){
                $gf.put("description", a.getString("description") );
            }

            a.put("global_fields", $gf );
            a.put("table", nwpFiles.table_name );
            r = nwpDataTable.save_line_items( a );
        }else{
            error = "Line Items, reference or reference table was not specified";
        }

        if( error.isEmpty() ){

        }else{
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice = error;
            GlobalFunctions.app_notice_only = true;
            r.put( "error", error );
        }

        return r;
    }

    public static String getTableName(){
        return nwpFiles.table_name;
    }


}
