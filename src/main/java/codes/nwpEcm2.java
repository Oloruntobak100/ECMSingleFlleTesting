package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class nwpEcm2{
    public static String table_name = "ecm2";

    public static JSONObject save_new_process( JSONObject a ){
        JSONObject r = new JSONObject();
        String error = "";
        String $key1;
        String $key2;
        Boolean $pass;
        JSONObject $val1;
        JSONObject $vline;
        JSONObject $aline;
        JSONArray $afiles = new JSONArray();

        if( a.has( "post_data" ) ){
            a.put( "table", nwpEcm2.table_name );
            a.put( "todo", "create_new_record" );

            if( a.getJSONObject( "post_data" ).has("extra_fields") ){
                JSONObject $ef = new JSONObject( a.getJSONObject( "post_data" ).getString("extra_fields") );

                if( $ef.has("wfs_fields") && $ef.getJSONObject("wfs_fields").has("file") && $ef.getJSONObject("wfs_fields").getJSONObject("file").length() > 0 ){
                    for(int i2 = 0; i2 < $ef.getJSONObject("wfs_fields").getJSONObject("file").names().length(); i2++){
                        $key1 = $ef.getJSONObject("wfs_fields").getJSONObject("file").names().getString(i2);
                        $val1 = $ef.getJSONObject("wfs_fields").getJSONObject("file").getJSONObject( $key1 );

                        $pass = false;
                        if( a.getJSONObject( "post_data" ).has($key1 + "_json") && ! a.getJSONObject("post_data").getString($key1 + "_json").isEmpty() ) {
                            $vline = new JSONObject( a.getJSONObject("post_data").getString($key1 + "_json") );
                        if( $vline.length() > 0 ) {

                            for(int i3 = 0; i3 < $vline.names().length(); i3++){
                                $key2 = $vline.names().getString(i3);
                                $aline = $vline.getJSONObject( $key2 );

                                    if( $aline.has("fullname") && ! $aline.getString("fullname").isEmpty() ) {

                                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
                                        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern( "HH:mm" );
                                        LocalDateTime myDateObj = LocalDateTime.now();

                                        $aline.put("date", myDateObj.format( myFormatObj ) + "T" + myDateObj.format( myFormatObj2 ) );

                                        //$aline.put("description", "create_file" );
                                        $aline.put("type", "create_file" );
                                        $aline.put("content", GlobalFunctions.rawurlencode( $aline.toString() ) );
                                        $aline.put("file_url", $aline.getString("fullname") );
                                        $aline.put("name", GlobalFunctions.urldecode( $aline.getString("label") ) );

                                        $afiles.put($aline);
                                        $pass = true;
                                    }
                                }
                            }
                        }

                        if( $val1.has( "required_field" ) && $val1.getString( "required_field" ).equals("yes") ){
                            if( ! $pass ){
                                error = "<h4>Invalid Attachment</h4><p>Please upload a valid file for <b>"+ ( $val1.has( "field_label" )?$val1.getString("field_label"):$key1 ) +"</b></p>";
                            }
                        }
                    }
                }
            }


            if( error.isEmpty() ) {
                r = nwpDataTable.saveDataForm(a);
                // System.out.println("msnab");
                // System.out.println( r );
                // System.out.println( a );
                if (r.has("saved_record")) {

                    JSONObject $rd = r.getJSONObject("saved_record");
                    String ecm_id = "";
                    if ($rd.has("type") && !$rd.getString("type").isEmpty()) {
                        String workflow_id = GlobalFunctions.GetNewID(nwpWorkflow.table_name);
                        ecm_id = r.getJSONObject("saved_record").getString("id");

                        $rd.put("workflow_settings", $rd.getString("type"));
                        $rd.put("reference", $rd.getString("id"));
                        $rd.put("reference_table", a.getString("table"));
                        $rd.put("comment", $rd.getString("description"));
                        $rd.put("description", $rd.getString("recommendation"));
                        $rd.put("name", $rd.getString("name"));
                        $rd.put("id", workflow_id);
                        //$rd.put("team", $rd.getString("team") );
                        //$rd.put("group", $rd.getString("group") );

                        //$rd.put("date", Long.valueOf( $rd.getString("start_date") ) );
                        $rd.put("date", GlobalFunctions.convert_timestamp_to_date($rd.getString("start_date"), "date-5", 1));
                        //$rd.put("date",  $rd.getString("start_date") );

                        JSONObject r2 = nwpWorkflow.add_to_workflow($rd);

                        // Save Meta data in workflow items
                        // System.out.println( r2 );
                        if (!r2.has("error")) {
                            /*System.out.println( $afiles );*/
                            if( $afiles.length() > 0 ) {
                                JSONObject rsf = nwpFiles.save_files(new JSONObject().put("line_items", $afiles).put("community", workflow_id).put("description", $rd.getString("comment")).put("reference_table", nwpEcm2.table_name).put("reference", ecm_id ));
                                if( rsf.has("error") && ! rsf.getString("error").isEmpty() ){
                                    error = rsf.getString("error");
                                }
                            }

                            if( error.isEmpty() ) {
                                JSONObject meta_data = new JSONObject();
                                JSONObject $qs = new JSONObject();
                                $qs.put("id", $rd.getString("type"));
                                $qs.put("table", nwpWorkflow.workflow_settings_table_name );

                                JSONObject $item = GlobalFunctions.get_record($qs);
                                JSONObject wsd = $item.has("data") ? new JSONObject(GlobalFunctions.urldecode($item.getString("data"))) : new JSONObject();

                                if (wsd.has("file") && wsd.getJSONObject("file").has("start") && wsd.getJSONObject("file").getJSONObject("start").has("data") && wsd.getJSONObject("file").getJSONObject("start").getJSONObject("data").length() > 0) {
                                    JSONObject start_items = new JSONObject();
                                    JSONObject start = new JSONObject();
                                    JSONObject fd = wsd.getJSONObject("file").getJSONObject("start").getJSONObject("data");

                                    for (int i2 = 0; i2 < fd.names().length(); i2++) {
                                        $key1 = fd.names().getString(i2);
                                        start_items = fd.getJSONObject( $key1 );

                                        if (a.getJSONObject("post_data").has($key1) && !a.getJSONObject("post_data").getString($key1).isEmpty()) {
                                            if( start_items.has("form_field") && ! start_items.getString("form_field").equals("file") ){
                                                start_items.put("value", a.getJSONObject("post_data").getString($key1) );
                                                start.put($key1, start_items );
                                            }
                                        }
                                    }

                                    if (start.length() > 0) {

                                        /*Date currentDate = new Date();
                                        Long timestamp = currentDate.getTime() / 1000;

                                        String ts = GlobalFunctions.convert_timestamp_to_date(String.valueOf(timestamp), "date-5time", 1);*/
                                        String current_time = GlobalFunctions.get_current_time(1);

                                       /* System.out.println("yox");
                                        System.out.println(start);
                                        System.out.println($rd);*/
                                        meta_data.put("meta", start );

                                        String wit = nwpWorkflow.workflow_items_table_name;
                                        JSONObject wi_fields = new JSONObject();
                                        JSONObject $workflow_data = GlobalFunctions.get_json(wit);
                                        if ($workflow_data.has("fields")) {
                                            wi_fields = $workflow_data.getJSONObject("fields");
                                        }

                                        JSONObject $rdx = new JSONObject();

                                        $rdx.put(wi_fields.getString("date"), current_time );
                                        $rdx.put(wi_fields.getString("name"), ($rd.getString("name")));
                                        $rdx.put(wi_fields.getString("workflow"), workflow_id);
                                        $rdx.put(wi_fields.getString("status"), (r2.has("saved_record") && r2.getJSONObject("saved_record").has(r2.getJSONObject("workflow_fields").getString("status")) ? r2.getJSONObject("saved_record").getString(r2.getJSONObject("workflow_fields").getString("status")) : ""));
                                        $rdx.put(wi_fields.getString("type"), "meta_data");
                                        $rdx.put(wi_fields.getString("reference"), ecm_id);
                                        $rdx.put(wi_fields.getString("reference_table"), a.getString("table"));
                                        $rdx.put(wi_fields.getString("reference_plugin"), "");
                                        $rdx.put(wi_fields.getString("data"), GlobalFunctions.rawurlencode( meta_data.toString() ) );

                                        //System.out.println(jpet);
                                        a = new JSONObject();
                                        a.put("post_data", $rdx);

                                        a.put("todo", "create_new_record");
                                        a.put("table", wit);
                                        JSONObject p = nwpDataTable.saveDataForm(a);

                                    }
                                }
                            }
                        }
                    } else {
                        error = "No workflow settings was specified";
                    }
                }
            }
        }else{
            error = "No data was submitted by the form";
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

    public static String new_workflow_item_form( JSONObject a ){
        //JSONObject r = new JSONObject();
        String r = "";

        if( a.has( "id" ) ){
            String source_table = a.has("source")?a.getString("source"):nwpEcm2.table_name;
            JSONObject hide_fields = a.has("hide_fields")?a.getJSONObject("hide_fields"):new JSONObject();

            Boolean setDefault = false;
            if( source_table.equals( nwpEcm2.table_name ) ){
                setDefault = true;
            }

            a.put( "table", nwpWorkflow.workflow_settings_table_name );
            JSONObject rd = GlobalFunctions.get_record( a );
            JSONObject $ed = new JSONObject().put("wfs_fields", new JSONObject() );

            if( rd.has( "id" ) && ! rd.getString( "id" ).isEmpty() ){

                String rep = a.has( "html_replacement_selector" ) ? a.getString( "html_replacement_selector" ) : "";

                JSONObject wsd = rd.has( "data" ) ? new JSONObject( GlobalFunctions.urldecode( rd.getString( "data" ) ) ) : new JSONObject();
                JSONObject jdata = new JSONObject();
                JSONObject $fs = new JSONObject();

                JSONObject $line = new JSONObject();
                String $key1 = "";

                JSONObject dep = GlobalFunctions.get_json( source_table );
                if( dep.has( "labels" ) && dep.has( "form_order" ) ){
                    JSONObject labels = dep.getJSONObject( "labels" );
                    JSONObject labels2 = new JSONObject();
                    if( labels.names().length() > 0 ){

                        for( int i2 = 0; i2 < labels.names().length(); i2++ ){
                            $key1 = labels.names().getString( i2 );
                            $line = labels.getJSONObject( $key1 );
                            String $fd = $line.has( "field_identifier" )?$line.getString( "field_identifier" ):"";
                            if( ! hide_fields.has( $fd ) ) {
                                switch ($fd) {
                                    case "recommendation":
                                    case "priority":
                                    case "description":
                                    case "name":
                                        labels2.put($key1, $line);
                                        break;
                                    case "group":
                                        $line.put("value", "group_1");
                                        $line.put("hidden_records_css", true);
                                        labels2.put($key1, $line);
                                        break;
                                    case "team":
                                        $line.put("value", "team_b");
                                        $line.put("hidden_records_css", true);
                                        labels2.put($key1, $line);
                                        break;
                                    case "department":
                                        if (setDefault) {
                                            if (GlobalFunctions.app_user_data.has("department")) {
                                                $line.put("value", GlobalFunctions.app_user_data.getString("department"));
                                                $line.put("hidden_records_css", true);
                                                labels2.put($key1, $line);
                                            }
                                        }
                                        break;
                                    case "type":
                                        if (setDefault) {
                                            $line.put("value", rd.getString("id"));
                                            $line.put("value_text", rd.getString("name"));
                                            $line.put("hidden_records_css", true);
                                            labels2.put($key1, $line);
                                        }
                                        break;
                                    case "category":
                                        if (setDefault) {
                                            $line.put("value", rd.getString("category"));
                                            $line.put("hidden_records_css", true);
                                            labels2.put($key1, $line);
                                        }
                                        break;
                                    case "start_date":
                                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("HH:mm");

                                        LocalDateTime myDateObj = LocalDateTime.now();

                                        $line.put("value", myDateObj.format(myFormatObj) + "T" + myDateObj.format(myFormatObj2));
                                        $line.put("hidden_records_css", true);
                                        labels2.put($key1, $line);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }


                    if( wsd.has( "file" ) && wsd.getJSONObject( "file" ).has( "start" ) && wsd.getJSONObject( "file" ).getJSONObject( "start" ).has( "data" ) && wsd.getJSONObject( "file" ).getJSONObject( "start" ).getJSONObject( "data" ).length() > 0 ){
                        if( wsd.has( "file" ) && wsd.getJSONObject( "file" ).has( "start" ) && wsd.getJSONObject( "file" ).getJSONObject( "start" ).has( "before_html" ) && !wsd.getJSONObject( "file" ).getJSONObject( "start" ).getString( "before_html" ).isEmpty() ){

                            JSONObject before_html = new JSONObject();
                            before_html.put( "form_field", "header" );
                            before_html.put( "field_label", wsd.getJSONObject( "file" ).getJSONObject( "start" ).getString( "before_html" ) );
                            before_html.put( "display_position", "1" );

                            labels2.put( "before_html", before_html );
                            dep.getJSONObject( "fields" ).put( "before_html", "before_html" );
                            dep.getJSONArray( "form_order" ).put( "before_html" );
                        }
                        Integer skip;

                        for( int i2 = 0; i2 < wsd.getJSONObject( "file" ).getJSONObject( "start" ).getJSONObject( "data" ).names().length(); i2++ ){
                            $key1 = wsd.getJSONObject( "file" ).getJSONObject( "start" ).getJSONObject( "data" ).names().getString( i2 );
                            $line = wsd.getJSONObject( "file" ).getJSONObject( "start" ).getJSONObject( "data" ).getJSONObject( $key1 );
                            skip = 0;

                            $line.put( "display_position", "display-in-table-row" );
                            if( !$line.has( "form_field" ) )
                                $line.put( "form_field", "file" );

                            switch( $line.getString( "form_field" ) ){
                            case "file":
                                if( !( $line.has( "acceptable_files_format" ) && !$line.getString( "acceptable_files_format" ).isEmpty() ) ){
                                    $line.put( "acceptable_files_format", "pdf:::jpg:::jpeg:::png:::bmp:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt" );
                                }
                                break;
                            case "select":

                                if( $line.has( "options" ) && ! $line.getString( "options" ).isEmpty() ){
                                    $line.put( "form_field_options_separator", "," );
                                    $line.put( "form_field_options_separator2", ":" );
                                }else{
                                    skip = 1;
                                }
                                break;
                            }

                            if( skip == 0 ){
                                if( ! $ed.getJSONObject("wfs_fields").has( $line.getString( "form_field" ) ) ){
                                    $ed.getJSONObject("wfs_fields").put( $line.getString( "form_field" ), new JSONObject() );
                                }

                                $ed.getJSONObject("wfs_fields").getJSONObject( $line.getString( "form_field" ) ).put( $key1, $line );

                                labels2.put( $key1, $line );
                                dep.getJSONObject( "fields" ).put( $key1, $key1 );
                                dep.getJSONArray( "form_order" ).put( $key1 );
                            }
                        }
                    }

                    dep.put( "labels", labels2 );

                    //System.out.println( "dep" );
                    //System.out.println( labels2 );
                }

                jdata.put( "form_extra_field_data", $ed.toString() );
                jdata.put( "table_settings", dep );
                jdata.put( "table", source_table );
                jdata.put( "action", "?action=display_sub_menu&nwp2_action="+ source_table +"&nwp2_todo=save_new_process&wf="+ rd.getString( "id" ) +"&html_replacement_selector=" + rep );

                r = nwpDataTable.getDataForm( jdata );
            }else{
                r = "<h4>Invalid Process</h4>The selected process might have been removed";
            }
        }else{
            r = "<h4>Please select the Process Type</h4>";
        }

        return r;
    }

    public static JSONObject GetControlPanel( String $id, String $table, String $workflow ){

        JSONObject $data = new JSONObject();
        String $error = "";
        JSONObject $qs = new JSONObject();
        JSONObject $workf;
        $qs.put( "id", $workflow );
        $qs.put( "table", nwpWorkflow.table_name );
        $workf = GlobalFunctions.get_record( $qs );

        if( $workf.has("reference_table") && ! $workf.getString("reference_table").isEmpty() ) {

            try{
                $data = new JSONObject(GlobalFunctions.fileGetContentsExternal("settings"+ GlobalFunctions.app_file_seperator + "workflow" + GlobalFunctions.app_file_seperator + $workf.getString("reference_table") +".json"));
            }catch(Exception e){
                $data = new JSONObject(GlobalFunctions.fileGetContentsExternal("settings"+ GlobalFunctions.app_file_seperator +"control_panel.json"));
            }

            $qs = new JSONObject();
            $id = $id.trim();
            $qs.put("id", $id);
            $qs.put("table", $workf.getString("reference_table") );

            JSONObject $rec = GlobalFunctions.get_record($qs);

            if ($rec.has("id")) {
                $data.put("table", $workf.getString("reference_table") );
                $data.put("workflow", $workflow);
                $data.put("id", $id);
                $data.put("item", $rec);
                $data.put("title_text", $rec.getString("name") + " - #" + $rec.getString("serial_num"));
                $data.put("show_refresh", 1);

                $data.put("details_column", 4);
                $data.put("action_column", 4);
                $data.put("comments_column", 4);

                JSONObject jget = new JSONObject();
                jget.put("id", $id);
                jget.put("title", "Process Info");
                jget.put("hide_buttons", true);
                if( $workf.getString("reference_table").equals( nwpEcm2.table_name ) ){
                    $data.put("details", nwpEcm2.getDetailsView(jget));
                }else if( $workf.getString("reference_table").equals( nwpCashAdvance.table_name ) ){
                    $data.put("details", nwpCashAdvance.getSingleDetailsView( new JSONObject( jget.toString() ).put("hide_status", true).put("ecm_record",$rec ) ) );
                }else{
                    jget.put("table", $workf.getString("reference_table") );
                    $data.put("details", GlobalFunctions.view_details( jget, new JSONObject().put("title1",jget.getString("title")).put("buttons","") ) );
                }

                JSONObject $flow_d = new JSONObject();
                JSONObject $flows = new JSONObject();
                if ($error.isEmpty()) {
                    $qs = new JSONObject();
                    $qs.put("id", $workf.getString("workflow_settings"));
                    $qs.put("table", nwpWorkflow.workflow_settings_table_name);
                    $flows = GlobalFunctions.get_record($qs);

                    if ($flows.has("data")) {
                        $flow_d = new JSONObject(GlobalFunctions.urldecode($flows.getString("data")));
                        if ( ! $flow_d.has("status")) {
                            $error = "Unable to decode workflow_settings data: " + $rec.getString("type");
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", $error )
                                            .put("input", new JSONObject().put("flows", $flows).put("rec", $rec) )
                                            .put("function", "nwpEcm2.GetControlPanel" )
                                            .put("fatal", true ) , null
                            );
                        }
                    }else{
                        $error = "Unable to retrieve workflow_settings data: " + $rec.getString("type");
                    }
                }

                if ($error.isEmpty()) {
                    jget = new JSONObject();
                    jget.put("id", $workflow);
                    jget.put("title", "Approval Info");
                    jget.put("hide_buttons", true);
                    $data.put("static_comments", nwpWorkflow.getDetailsView(jget));

                    if ($data.has("general_actions") && $data.getJSONObject("general_actions").has("v_workf")) {
                        $data.getJSONObject("general_actions").getJSONObject("v_workf").put("custom_id", $workf.getString("workflow_settings"));
                    }

                    Boolean show = false;
                    if (GlobalFunctions.app_user_data.has("role")) {
                        if (GlobalFunctions.app_user_data.has("accessible_functions")
                                && GlobalFunctions.app_user_data.getJSONObject("accessible_functions").has(nwpEcm2.table_name + ".move_job.move_job")) {
                            show = true;
                        }
                    } else {
                        show = true;
                    }
                    if( show && $workf.has("status") && $workf.getString("status").equals("complete") ){
                        show = false;
                        if ( $flow_d.has("status")
                                && $flow_d.getJSONObject("status").has("complete")
                                && $flow_d.getJSONObject("status").getJSONObject("complete").has("general_actions")
                                && $flow_d.getJSONObject("status").getJSONObject("complete").getJSONObject("general_actions").has("move_job")
                                && $flow_d.getJSONObject("status").getJSONObject("complete").getJSONObject("general_actions").getJSONObject("move_job").has("show") ) {
                            show = true;
                        }
                    }

                    if (!show && $data.has("general_actions") && $data.getJSONObject("general_actions").has("move_job")) {
                        $data.getJSONObject("general_actions").remove("move_job");
                    }

                    show = false;
                    if (GlobalFunctions.app_user_data.has("role")) {
                        if (GlobalFunctions.app_user_data.has("accessible_functions") && GlobalFunctions.app_user_data.getJSONObject("accessible_functions").has(nwpEcm2.table_name + ".flag_job.flag_job")) {
                            show = true;
                        }
                    } else {
                        show = true;
                    }
                    if (!show && $data.has("general_actions") && $data.getJSONObject("general_actions").has("flag_job")) {
                        $data.getJSONObject("general_actions").remove("flag_job");
                    }


                    if ($data.has("tabs") && $data.getJSONObject("tabs").has("trail")) {
                        $data.getJSONObject("tabs").getJSONObject("trail").put("custom_id", $workflow);
                    }
                    if ($data.has("tabs") && $data.getJSONObject("tabs").has("history")) {
                        $data.getJSONObject("tabs").getJSONObject("history").put("custom_id", $workflow);
                    }
                }
                //get workflow status and check if complete or cancelled

                String $workf_status = "";
                if ($error.isEmpty()) {

                    if ($workf.has("reference") && $workf.getString("reference").equals($id)) {
                        $workf_status = $workf.getString("status").toLowerCase();
                    } else {
                        $error = "Unable to retrieve workflow data for process: " + $id;
                    }
                }

                //get workflow data and prepare buttons
                String $previous_state = "";
                String $next_state = "";
                String $attr = "";

                if ($error.isEmpty()) {
                    JSONObject $ns = new JSONObject();

                    //set workflow notes
                    if ($flow_d.has("status") && $flow_d.getJSONObject("status").has($workf_status) && $flow_d.getJSONObject("status").getJSONObject($workf_status).has("note")) {
                        JSONArray $na = new JSONArray();
                        $na.put($flow_d.getJSONObject("status").getJSONObject($workf_status).getJSONObject("note"));
                        $data.put("note", $na);
                    }

                    //set footer buttons
                    if ($workf_status != "complete") {
                        if ($flow_d.has("forward") && $flow_d.getJSONObject("forward").has($workf_status)) {
                            String $nfs = $flow_d.getJSONObject("forward").getString($workf_status);

                            if ($nfs.equals("complete")) {
                                $next_state = "Approved";

                                if ($flow_d.has("status") && $flow_d.getJSONObject("status").has($nfs)) {
                                    $ns = $flow_d.getJSONObject("status").getJSONObject($nfs);
                                    if ($ns.has("submit_title") && !$ns.getString("submit_title").isEmpty()) {
                                        $next_state = $ns.getString("submit_title").toUpperCase();
                                    }
                                }
                            } else {

                                if ($flow_d.has("status") && $flow_d.getJSONObject("status").has($nfs)) {

                                    $ns = $flow_d.getJSONObject("status").getJSONObject($nfs);

                                    if ($ns.has("submit_title") && !$ns.getString("submit_title").isEmpty()) {
                                        $next_state = $ns.getString("submit_title").toUpperCase();
                                    } else {
                                        $next_state = "Submit to " + ($ns.has("name") ? $ns.getString("name") : $nfs).toUpperCase();
                                    }
                                }
                            }
                        }

                        if ($flow_d.has("backward") && $flow_d.getJSONObject("backward").has($workf_status)) {
                            String $nbs = $flow_d.getJSONObject("backward").getString($workf_status);
                            if (!$nbs.equals("start")) {

                                if ($flow_d.has("status") && $flow_d.getJSONObject("status").has($nbs)) {
                                    $ns = $flow_d.getJSONObject("status").getJSONObject($nbs);

                                    if ($ns.has("return_title") && !$ns.getString("return_title").isEmpty()) {
                                        $previous_state = $ns.getString("return_title").toUpperCase();
                                    } else {
                                        $previous_state = "Return to " + ($ns.has("name") ? $ns.getString("name") : $nbs).toUpperCase();
                                    }

                                }
                            }
                        }
                    }

                    if ($workf_status.equals("start") || ($flow_d.has("forward") && $flow_d.getJSONObject("forward").has("start") && $flow_d.getJSONObject("forward").getString("start").equals($workf_status))) {
                        $previous_state = "";
                    }


                    JSONObject $footer_buttons = new JSONObject();
                    if (!$next_state.isEmpty()) {
                        //$footer_buttons.put( "forward20", new JSONObject().put( "custom_id", $workflow ).put( "action", "display_sub_menu" ).put( "todo", "&nwp2_action=workflow&nwp2_todo=submit_comment&nwp2_type=submit_comment&nwp_wf_cur_state=" + GlobalFunctions.md5( $workf_status + nwpWorkflow.salter ) ).put( "confirm_prompt", "Submit this job" ).put( "text", $next_state + " &rarr;" ).put( "attr", $attr ).put( "class", " blue " ) );
                        $footer_buttons.put("forward20", new JSONObject().put("custom_id", $workflow).put("action", "display_sub_menu").put("todo", "&nwp2_action=nwp_workflow&nwp2_todo=submit_comment&nwp2_type=submit_comment&nwp_wf_cur_state=" + GlobalFunctions.md5($workf_status + nwpWorkflow.salter)).put("text", $next_state + " &rarr;").put("attr", $attr).put("class", " blue "));
                    }
                    if (!($flow_d.has("status") && $flow_d.getJSONObject("status").has($workf_status) && $flow_d.getJSONObject("status").getJSONObject($workf_status).has("hide_return") && ! $flow_d.getJSONObject("status").getJSONObject($workf_status).get("hide_return").toString().isEmpty()) && !$previous_state.isEmpty()) {
                        //$footer_buttons.put( "backward", new JSONObject().put( "custom_id", $workflow ).put( "action", "display_sub_menu" ).put( "todo", "&nwp2_action=workflow&nwp2_todo=submit_comment&nwp2_type=return_comment&nwp_wf_cur_state=" + GlobalFunctions.md5( $workf_status + nwpWorkflow.salter ) ).put( "confirm_prompt", "Return this job" ).put( "text", "&larr; " + $previous_state ).put( "attr", $attr ) );
                        $footer_buttons.put("backward", new JSONObject().put("custom_id", $workflow).put("action", "display_sub_menu").put("todo", "&nwp2_action=nwp_workflow&nwp2_todo=submit_comment&nwp2_type=return_comment&nwp_wf_cur_state=" + GlobalFunctions.md5($workf_status + nwpWorkflow.salter)).put("text", "&larr; " + $previous_state).put("attr", $attr));
                    }

                    $data.put("footer_buttons", $footer_buttons);
                    //System.out.println( $footer_buttons );

                    //test for action group
                    if ($flow_d.has("custom_actions") && $flow_d.getJSONObject("custom_actions").has("title") && $flow_d.getJSONObject("custom_actions").has("actions") ) {
                        JSONObject $cas = new JSONObject();
                        for( Object $name : $flow_d.getJSONObject("custom_actions").getJSONObject("actions").names() ){
                            if( $flow_d.getJSONObject("custom_actions").getJSONObject("actions").has( $name.toString() ) ){
                                $cas = $flow_d.getJSONObject("custom_actions").getJSONObject("actions").getJSONObject( $name.toString() );

                                if( $cas.has("status") && $cas.getJSONObject("status").length() > 0 ){
                                    //show in specific status
                                    if( ! $cas.getJSONObject("status").has( $workf_status ) ){
                                        $flow_d.getJSONObject("custom_actions").getJSONObject("actions").remove( $name.toString() );
                                    }
                                }else{
                                    //show in all status
                                }
                            }

                            if ( $flow_d.getJSONObject("custom_actions").getJSONObject("actions").length() > 0 ) {
                                $cas = new JSONObject();
                                if( $data.has("action_group") ){
                                    $cas = $data.getJSONObject("action_group");
                                }
                                $cas.put("custom_actions", $flow_d.getJSONObject("custom_actions") );
                                $data.put("action_group", $cas);
                            }
                            //System.out.println( $cas.getJSONObject( $name.toString() ) );
                        }
                    }

                }

            }
        }
        return $data;
    }

    public static String getDetailsView( JSONObject jget ){
        String $r = "";
        JSONObject $vd = new JSONObject();
        String $table = nwpEcm2.table_name;

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
                JSONObject $fieldDetails;
                Date currentDate = new Date();
                Boolean $has_ended = false;
                Long $end_date = currentDate.getTime() / 1000;
                if( $item.has( "status" ) && ( $item.getString( "status" ).equals( "completed" ) || $item.getString( "status" ).equals( "cancelled" ) ) ){
                    $end_date = Long.valueOf( $item.getString( "end_date" ) );
                    $has_ended = true;
                }

                String $value;
                if( !$has_ended ){
                    $key = "end_date";
                    $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "0";
                    if( !$value.isEmpty() ){
                        $rf.put( new JSONObject().put( "label", "Completion Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                    }
                }

                $key = "start_date";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Start Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                }

                $key = "name";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Title" ).put( "value", $value ) );
                }

                $key = "priority";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $fields.has($key ) && $labels.has( $fields.getString($key ) ) ){
                    String v1 = "<code>" + GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) + "</code>";

                    if( $item.has("flag_by") ){
                        v1 += "<small> by " + GlobalFunctions.get_record_name( new JSONObject().put("id", $item.getString("flag_by") ).put("table", "users" ) ) + "</small>";
                    }

                    if( $item.has("flag_comment") && ! $item.getString("flag_comment").isEmpty()){
                        v1 += "<br><i>" + $item.getString("flag_comment").replaceAll("\n", "<br>") + "</i>";
                    }

                    $rf.put( new JSONObject().put( "label", "Priority" ).put( "value", v1 ) );
                }

                if( $item.has("modified_by") ){
                    $fieldDetails = new JSONObject();
                    $fieldDetails.put("label", "Last Modified");
                    String sysValue = GlobalFunctions.get_record_name( new JSONObject().put("id", $item.getString("modified_by") ).put("table", "users" ) );
                    if( $item.has("modification_date") ){
                        sysValue += " " + GlobalFunctions.convert_timestamp_to_date( $item.getString("modification_date"), "date-5time", 0 );
                    }
                    $fieldDetails.put("value", sysValue );
                    $rf.put($fieldDetails);
                }

                if( $item.has("created_by") ){
                    $fieldDetails = new JSONObject();
                    $fieldDetails.put("label", "Created By");
                    String sysValue = GlobalFunctions.get_record_name( new JSONObject().put("id", $item.getString("created_by") ).put("table", "users" ) );
                    if( $item.has("creation_date") ){
                        sysValue += " " + GlobalFunctions.convert_timestamp_to_date( $item.getString("creation_date"), "date-5time", 0 );
                    }
                    $fieldDetails.put("value", sysValue );
                    $rf.put($fieldDetails);
                }

                $vd.put( "fields", $rf );

                if( jget.has( "title" ) ){
                    $vd.put( "title1", jget.getString( "title" ) );
                }else{
                    $vd.put( "title2", "#" + $item.getString( "serial_num" ) + " - " + $item.getString( "name" ) );
                    $vd.put( "title2_class", "card-shadow-success border border-success bg-success text-white" );
                }

                if( jget.has("set_popup_title") ){
                    GlobalFunctions.app_popup_title = "#" + $item.getString( "serial_num" ) + " - " + $item.getString( "name" );
                }

                if( !jget.has( "hide_buttons" ) ){
                    //JSONObject $btn_data = new JSONObject( "{\"utility_buttons\":{\"comments\":1,\"attach_file\":1,\"view_details\":1},\"more_actions\":{\"op\":{\"todo\":\"control_panel\",\"title\":\"Open and View Details\",\"text\":\"Open\",\"html_replacement_key\":\"phtml_replacement_selector\"}}}" );
                    //JSONObject more_data = new JSONObject();
                    //more_data.put("additional_params", "&workflow="+ $item.getString("id"));
                    JSONObject $btn_data = new JSONObject();
                    if( $data.getJSONObject("datatable_options").has("more_actions") ){
                        $btn_data.put( "more_actions", $data.getJSONObject("datatable_options").getJSONObject("more_actions") );
                    }
                    if( $data.getJSONObject("datatable_options").has("utility_buttons") ){
                        $btn_data.put( "utility_buttons", $data.getJSONObject("datatable_options").getJSONObject("utility_buttons") );
                    }
                    if( $btn_data.length() > 0 ) {
                        $btn_data.put("hide", "no_split_view");

                        $btn_data.put("html_replacement_selector", "datatable-split-screen-" + $table);
                        $btn_data.put("params", "&html_replacement_selector=" + $btn_data.getString("html_replacement_selector"));
                        $btn_data.put("phtml_replacement_selector", $container2);
                        $btn_data.put("selected_record", $item.getString("id"));
                        $btn_data.put("table", $table);
                        //$btn_data.put("more_data", more_data );

                        $vd.put("buttons", nwpDataTable.getButtons($btn_data) + "<br /><br />");
                    }
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

    public static JSONObject Ecm2GetStartNewProcess( String a ){
        JSONObject r = new JSONObject();
        JSONObject $lib = new JSONObject();
        JSONObject $qr = nwpWorkflow.getWorkflowSettings( new JSONObject().put("source", "new_job").put("no_index", "1").put("check_access", "1") );

        if( $qr.has( "row_count" ) && $qr.has( "row" ) && $qr.getInt( "row_count" ) > 0 ){
            JSONArray $r32 = $qr.getJSONArray( "row" );
            if( $r32.length() > 0 ){

                JSONObject $opt = new JSONObject();
                $opt.put( "return_type", "2" );
                JSONObject $qrs2 = GlobalFunctions.get_list_box_options( "workflow_category", $opt );

                //JSONObject $line = new JSONObject();
                //JSONObject $line2 = new JSONObject();
                String $cat = "";
                for( int i2 = 0; i2 < $r32.length(); i2++ ){
                    $cat = $r32.getJSONObject( i2 ).getString( "category" );
                    if( $qrs2.has( $cat ) ){
                        $cat = $qrs2.getString( $cat );
                    }

                    //$line.put( $r32.getJSONObject( i2 ).getString("id"), $r32.getJSONObject( i2 ) );
                    if( !$lib.has( $cat ) ){
                        $lib.put( $cat, new JSONObject() );
                    }
                    $lib.getJSONObject( $cat ).put( $r32.getJSONObject( i2 ).getString( "id" ), $r32.getJSONObject( i2 ) );
                }


            }
        }

        System.out.println( $qr );

        r.put( "libraries", $lib );

        return r;
    }

    public static JSONObject directUpdateWorkflow( JSONObject a ){
        JSONObject r = new JSONObject();
        String error = "";
        String h1 = "";
        String success_click_handle = a.has("success_click_handle")?a.getString("success_click_handle"):"";
        JSONObject update_fields = a.has("update_fields")?a.getJSONObject("update_fields"):new JSONObject();
        JSONObject uf_fields = a.has("uf_fields")?a.getJSONObject("uf_fields"):new JSONObject();
        Boolean updateOp = a.has("update_op")?a.getBoolean("update_op"):true;

        if( a.has("update_ref") && a.has("update_ref_table") && ! a.getString("update_ref").isEmpty() && ! a.getString("update_ref_table").isEmpty() ) {
            JSONObject dep = GlobalFunctions.get_json(nwpWorkflow.table_name);

            if (dep.has("fields")) {
                JSONObject labels = dep.getJSONObject("labels");

                JSONObject $qs = new JSONObject();

                $qs.put("where", " AND [" + dep.getJSONObject("fields").getString("reference") + "] = '" + a.getString("update_ref") + "' AND [" + dep.getJSONObject("fields").getString("reference_table") + "] = '" + a.getString("update_ref_table") + "' ");
                $qs.put("select_fields", new JSONObject().put("reference", "reference").put("status", "status"));

                $qs.put("table", nwpWorkflow.table_name );
                JSONObject $qr = GlobalFunctions.get_records($qs);
                // System.out.print( $qr );

                if ($qr.has("row_count") && $qr.getInt("row_count") > 0) {
                    if( $qr.has("row") && $qr.getJSONArray("row").getJSONObject(0).has("id") ) {

                        if( updateOp ) {
                            a.put("id", $qr.getJSONArray("row").getJSONObject(0).getString("id"));
                            a.put("action", $qr.getJSONArray("row").getJSONObject(0).getString("status"));
                            a.put("status", $qr.getJSONArray("row").getJSONObject(0).getString("status"));
                            String wName = "Direct Job Movement";

                            if (uf_fields.length() > 0) {

                                for (int i = 0; i < uf_fields.length(); i++) {
                                    String key = uf_fields.names().getString(i);
                                    String val = uf_fields.getString(key);

                                    if (dep.getJSONObject("fields").has(key)) {
                                        switch (key) {
                                            case "priority":
                                                wName = "Job Flagged";
                                                JSONObject $os = GlobalFunctions.get_list_box_options("ecm_priority_levels", new JSONObject().put("return_type", "2"));
                                                if ($os.length() > 0 && $os.has(val)) {
                                                    wName += ": " + $os.getString(val);
                                                }
                                                break;
                                            case "status":
                                                a.put(key, val);
                                                break;
                                        }

                                        update_fields.put(dep.getJSONObject("fields").getString(key), val);
                                    }
                                }
                            }

                            a.put("name", wName);
                        }else{
                            //return workflow data
                            r.put("record", $qr.getJSONArray("row").getJSONObject(0) );
                        }
                    }else{
                        error = "<h4>Unable to Retrieve Workflow with Ref: "+ a.getString("update_ref") +"</h4>";
                    }
                }
            }
        }

        if( updateOp ) {
            if( ! ( update_fields.length() > 0 ) ) {
                error = "<h4>No Data Found</h4>Workflow was not updated";
            }else if( a.has("id") && ! a.getString("id").isEmpty() ) {

                JSONObject a1 = new JSONObject();
                a1.put("id", a.getString("id") );
                a1.put("table", nwpWorkflow.table_name);
                a1.put("todo", "edit"); // create_new_record
                a1.put("post_data", update_fields);

                r = nwpDataTable.saveDataForm(a1);

                if ( r.has("saved_record") && r.getJSONObject("saved_record").has("id")) {

                    //create trail for comment
                    JSONObject li = new JSONObject();
                    JSONObject $wtrail_fields = new JSONObject();
                    JSONObject $wtrail_data = GlobalFunctions.get_json(nwpWorkflow.workflow_trail_table_name);
                    if ($wtrail_data.has("fields")) {
                        $wtrail_fields = $wtrail_data.getJSONObject("fields");
                    }

                    //create trail
                    //li.put( $wtrail_fields.getString( "date" ), timestamp.toString() ); //date("U")
                    li.put($wtrail_fields.getString("date"), GlobalFunctions.get_current_time(1)); //date("U")
                    li.put($wtrail_fields.getString("workflow"),  r.getJSONObject("saved_record").getString("id"));
                    li.put($wtrail_fields.getString("type"), a.getString("type") );
                    li.put($wtrail_fields.getString("action"), a.getString("action") );
                    li.put($wtrail_fields.getString("status"), a.getString("status") );

                    li.put($wtrail_fields.getString("staff_responsible"), GlobalFunctions.app_user);
                    if( GlobalFunctions.app_user_data.has("id") && GlobalFunctions.app_user_data.getString("id").equals("developer") ){
                        li.put($wtrail_fields.getString("staff_responsible"), GlobalFunctions.app_user_data.getString("id") );
                    }

                    li.put($wtrail_fields.getString("name"), a.getString("name") );
                    if ( a.has("comment")) {
                        li.put($wtrail_fields.getString("comment"), a.getString("comment") );
                    }
                    a = new JSONObject();

                    a.put("table", nwpWorkflow.workflow_trail_table_name);
                    a.put("todo", "create_new_record"); // create_new_record
                    a.put("post_data", li);

                    nwpDataTable.saveDataForm(a);

                    h1 += "<div class=\"note note-success\"><h4>Updated Successfully</h4></div>";
                    if ( r.has("view_details")) {
                        h1 +=  r.getString("view_details");
                    }

                    if (!success_click_handle.isEmpty()) {
                        h1 += "<script>setTimeout(function(){ if( $('#" + success_click_handle + "').is(':visible') )$('#" + success_click_handle + "').click(); }, 300);</script>";
                    }
                } else {
                    if ( r.has("msg")) {
                        error =  r.getString("msg");
                    } else {
                        error = "Unable to save workflow record...Unable to update workflow";
                    }
                }
            }else{
                error = "Invalid Workflow ID...Unable to update workflow";
            }
        }

        r.put("html", h1 );
        if( ! error.isEmpty() ){
            r.put("error", error);
        }

        return r;
    }
}
