package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class nwpCashAdvance {
    public static String table_name = "ecm_cash_advance";
    public static String table_name_trip = "ecm_cash_advance_trip";
    public static String table_name_rate = "ecm_cash_advance_rate";
    public static String table_name_km_chart = "ecm_cash_advance_km_chart";
    public static String table_name_zone = "ecm_cash_advance_zone";
    private static Boolean linkRetirement = true;


    public static JSONObject save_new_process( JSONObject a ){
        JSONObject r = new JSONObject();
        String error = "";
        String $key1;
        String $key2;
        //String container = a.has("html_replacement_selector")?a.getString("html_replacement_selector"):"";
        String container = a.has("html_parent")?a.getString("html_parent"):"";
        Boolean $pass;
        JSONObject $val1;
        JSONObject $vline;
        JSONObject $aline;
        String caType = "retire";
        JSONArray $afiles = new JSONArray();

        if( a.has( "post_data" ) && a.has("workflow_settings") ){
            a.put( "table", nwpCashAdvance.table_name );
            a.put( "todo", "create_new_record" );

            //same code in ecm2.save_new_process: optimize
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

            String refNum = "";
            if( error.isEmpty() ) {
                JSONObject dep = GlobalFunctions.get_json( nwpCashAdvance.table_name );

                if( dep.has( "fields" ) && dep.getJSONObject( "fields" ).length() > 0 ){

                    if( a.getJSONObject( "post_data" ).has( dep.getJSONObject( "fields" ).getString("type") )
                            && ! a.getJSONObject( "post_data" ).getString( dep.getJSONObject( "fields" ).getString("type") ).isEmpty() ) {
                        refNum = getCARefNum(new JSONObject()
                                .put("type", a.getJSONObject("post_data").getString(dep.getJSONObject("fields").getString("type")))
                                .put("serial_num", "XXX")
                        );
                    }

                    for(int i2 = 0; i2 < dep.getJSONObject( "fields" ).length(); i2++){
                        $key1 = dep.getJSONObject( "fields" ).names().getString(i2);
                        $key2 = dep.getJSONObject( "fields" ).getString( $key1 );
                        if( a.getJSONObject( "post_data" ).has("wf_" + $key1 ) && ! a.getJSONObject("post_data").getString("wf_" + $key1 ).isEmpty() ) {
                            a.getJSONObject( "post_data" ).put( $key2,  a.getJSONObject("post_data").getString("wf_" + $key1 ) );
                        }

                        switch( $key1 ){
                            case "date":
                                a.getJSONObject( "post_data" ).put( $key2,  GlobalFunctions.get_current_time(0) );
                                break;
                            case "staff":
                                a.getJSONObject( "post_data" ).put( $key2,  GlobalFunctions.app_user );
                                if( GlobalFunctions.app_user_data.has("id") && GlobalFunctions.app_user_data.getString("id").equals("developer") ){
                                    a.getJSONObject( "post_data" ).put( $key2, GlobalFunctions.app_user_data.getString("id") );
                                }
                                break;
                            case "status":
                                a.getJSONObject( "post_data" ).put( $key2,  "draft" );
                                break;
                            case "name":
                                a.getJSONObject("post_data").put($key2, refNum );
                                break;
                            case "department":
                                if( GlobalFunctions.app_user_data.has("department") ){
                                    a.getJSONObject( "post_data" ).put( $key2,  GlobalFunctions.app_user_data.getString("department") );
                                }
                                break;
                            case "grade_level":
                                if( GlobalFunctions.app_user_data.has("grade_level") ){
                                    a.getJSONObject( "post_data" ).put( $key2,  GlobalFunctions.app_user_data.getString("grade_level") );
                                }
                                break;
                        }
                    }
                }

                if( a.getJSONObject( "post_data" ).has( dep.getJSONObject( "fields" ).getString("type") ) && ! a.getJSONObject( "post_data" ).getString( dep.getJSONObject( "fields" ).getString("type") ).isEmpty() ){
                    JSONObject caCode = GlobalFunctions.selectBoxOptions( "ecm_cash_advance_types" );
                    if( ! caCode.has( a.getJSONObject( "post_data" ).getString( dep.getJSONObject( "fields" ).getString("type") ) ) ){
                        error = "<h4>Unregistered Cash Advance Type</h4>Add the specified option to select_options.json:ecm_cash_advance_types";
                    }
                }else{
                    error = "<h4>Invalid Cash Advance Type</h4><p>Reconfigure workflow settings</p>";
                }
                //System.out.println( a );


                if( error.isEmpty() ) {

                    if( a.getJSONObject("post_data").has("wf_previous_ref") && ! a.getJSONObject("post_data").getString("wf_previous_ref").isEmpty() ){
                        a.getJSONObject("post_data").put("previous_ref", a.getJSONObject("post_data").getString("wf_previous_ref") );
                    }
                    Boolean hasPrevious = false;


                    if (a.getJSONObject("post_data").has("previous_ref") && !a.getJSONObject("post_data").getString("previous_ref").isEmpty()) {
                        hasPrevious = true;
                        a.getJSONObject("post_data").put(dep.getJSONObject("fields").getString("previous_ref"), a.getJSONObject("post_data").getString("previous_ref") );
                        if (a.getJSONObject("post_data").has("wf_type") && ! a.getJSONObject("post_data").getString("wf_type").isEmpty()) {
                            caType = a.getJSONObject("post_data").getString("wf_type");
                        }
                    }

                    if (a.getJSONObject("post_data").has(dep.getJSONObject("fields").getString("previous_ref")) && !a.getJSONObject("post_data").getString(dep.getJSONObject("fields").getString("previous_ref")).isEmpty()) {
                        hasPrevious = true;
                    }

                    if ( hasPrevious ) {
                        JSONObject $qs2 = new JSONObject();
                        $qs2.put("id",  a.getJSONObject("post_data").getString(dep.getJSONObject("fields").getString("previous_ref")) );
                        $qs2.put("table", nwpCashAdvance.table_name );
                        JSONObject pr = GlobalFunctions.get_record( $qs2 );

                        if( pr.has("id") && pr.has("name") ) {
                            a.getJSONObject("post_data").put(dep.getJSONObject("fields").getString("previous_reference_number"), pr.getString("name") );
                            //change to amount approved: nwpCashAd..135
                            a.getJSONObject("post_data").put(dep.getJSONObject("fields").getString("amount"), pr.getString("amount") );
                        }else{
                            error = "<h4>Unable to Retrieve Previously Approved Cash Advance</h4><p>Reference #"+ $qs2.getString("id") +"</p>";
                        }

                    }
                }
            }

            String workflow_id = "";
            if( error.isEmpty() ) {
                r = nwpDataTable.saveDataForm(a);
                // System.out.println("msnab");
                // System.out.println( r );
                // System.out.println( a );
                if (r.has("saved_record")) {

                    //same code in ecm2.save_new_process: optimize
                    JSONObject $rd = r.getJSONObject("saved_record");
                    JSONObject $rd3 = new JSONObject( r.getJSONObject("saved_record").toString() );

                   /* GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "SAVED PROCESS: " )
                                    .put("input", new JSONObject().put("r", r).put("$rd", $rd) )
                                    .put("function", "Saving.POST" )
                                    .put("exception", false )
                                    .put("fatal", true ) , null
                    );*/

                    String ecm_id = "";

                    if ( $rd.has("id") && !$rd.getString("id").isEmpty()) {
                        //add trip details
                        if( nwpCashAdvance.linkRetirement && $rd.has("previous_ref") && ! $rd.getString("previous_ref").isEmpty() ){
                            JSONObject $qs3 = new JSONObject();
                            JSONObject depT = GlobalFunctions.get_json( nwpCashAdvance.table_name_trip );
                            if( depT.has("fields") ) {
                                JSONObject $qf2 = depT.getJSONObject("fields");

                                $qs3.put("select_all", true);
                                $qs3.put("fields", $qf2);
                                $qs3.put("id",  $rd.getString("previous_ref") );
                                JSONObject $qr3 = getTripDetails( $qs3 );

                                if ($qr3.has("row_count") && $qr3.has("row") && $qr3.getInt("row_count") > 0) {

                                    $qs3 = new JSONObject();
                                    $qs3.put("global_fields", new JSONObject()
                                            .put("cash_advance", $rd.getString("id") )
                                            .put("cash_advance_type", caType )
                                            .put("date", GlobalFunctions.get_current_time(0) ) );
                                    $qs3.put("line_items",  $qr3.getJSONArray("row") );
                                    $qs3.put("table", nwpCashAdvance.table_name_trip );
                                    $qr3 = nwpDataTable.save_line_items( $qs3 );
                                    if( $qr3.has("error") ){
                                        error = $qr3.getString("error");
                                    }
                                }
                            }
                        }

                        refNum = getCARefNum( $rd );

                        workflow_id = GlobalFunctions.GetNewID(nwpWorkflow.table_name);
                        ecm_id = r.getJSONObject("saved_record").getString("id");

                        $rd.put("workflow_settings", a.getString("workflow_settings") );
                        $rd.put("reference", ecm_id );
                        $rd.put("reference_table", a.getString("table"));
                        $rd.put("comment", $rd.getString("reason"));
                        $rd.put("description", $rd.getString("reason"));

                        //$rd.put("name", "XX");
                        //$rd.put("name", $rd.getString("name") );
                        $rd.put("name", refNum );
                        $rd.put("id", workflow_id);
                        $rd.put("team", "" );
                        $rd.put("group", "" );

                        //$rd.put("date", Long.valueOf( $rd.getString("start_date") ) );
                        $rd.put("date", GlobalFunctions.convert_timestamp_to_date($rd.getString("date"), "date-5", 1));
                        //$rd.put("date",  $rd.getString("start_date") );

                        JSONObject r2 = nwpWorkflow.add_to_workflow($rd);

                        // Save Meta data in workflow items
                        // System.out.println( r2 );
                        if (!r2.has("error")) {
                            /*System.out.println( $afiles );*/
                            if( $afiles.length() > 0 ) {
                                JSONObject rsf = nwpFiles.save_files(new JSONObject().put("line_items", $afiles).put("community", workflow_id).put("description", $rd.getString("comment")).put("reference_table", a.getString("table") ).put("reference", ecm_id ));
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

                    //open cash advance
                    if( error.isEmpty() ) {
                        r.put("view_details", nwpCashAdvance.getDetailsView(
                                new JSONObject()
                                .put("show_workflow_button", true )
                                .put("workflow", workflow_id )
                                .put("saved_record", $rd3 )
                                .put("saved", true)
                                .put("html_replacement_selector", container)
                                )
                        );
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

    public static String getDetailsView( JSONObject jget ){
        String $r = "";
        JSONObject $vd = new JSONObject();
        String $table = nwpCashAdvance.table_name;

        JSONObject $item = new JSONObject();
        Boolean justSaved = jget.has("saved")?jget.getBoolean("saved"):false;
        String action = jget.has("action")?jget.getString("action"):"";

        Boolean isValidateTripDetails = jget.has("isValidateTripDetails")?jget.getBoolean("isValidateTripDetails"):false;
        if( jget.has("isValidating") && ! jget.getString("isValidating").isEmpty() ){
            isValidateTripDetails = true;
        }

        //System.out.println( "viewCAD" );
        //System.out.println( jget );
        if( jget.has( "saved_record" ) && jget.getJSONObject( "saved_record" ).has("id") ){
            $item = jget.getJSONObject( "saved_record" );
        }else if( jget.has( "id" ) && !jget.getString( "id" ).isEmpty() ){
            JSONObject $qs = new JSONObject();
            $qs.put( "id", jget.getString( "id" ) );
            $qs.put( "table", $table );

            $item = GlobalFunctions.get_record( $qs );
        }

        if( $item.has( "id" ) && ! $item.getString( "id" ).isEmpty() ){

            String $container = jget.has( "html_replacement_selector" ) ? jget.getString( "html_replacement_selector" ) : "";
            String $container2 = jget.has( "phtml_replacement_selector" ) ? jget.getString( "phtml_replacement_selector" ) : "";
            //echo '<pre>';print_r( json_encode($data) );echo '</pre>';
            if( $item.has( "type" ) ){
                jget.put( "id", $item.getString( "id" ) );
                jget.put( "table", $table );
                jget.put( "record", $item );

                JSONArray $rf = new JSONArray();
                String $key = "";
                String $value;

                $key = "staff";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Title" ).put( "value", GlobalFunctions.get_record_name( new JSONObject().put("id", $value ).put("table", "users" ) ) ) );
                }


                /*if( $item.has("modified_by") ){
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
                }*/

                /*$vd.put( "fields", $rf );
                if( jget.has( "name" ) ){
                    $vd.put( "title1", jget.getString( "name" ) );
                }*/
                Boolean validTrip = false;
                String h1 = "";
                String h2 = "";
                String h3 = "";
                String col1 = "8";
                String col2 = "4";
                JSONObject jdata = new JSONObject();
                jdata.put( "cash_advance", $item.getString( "id" ) );
                Boolean allowEdit = true;
                String $workflow = "";


                switch (  action ){
                    case "submit_for_approval":
                        String sh2 = "";
                        h2 = "<h4>3. Submit for Approval</h4>";
                        h2 += "<a href='javascript:;' class='btn blue custom-single-selected-record-button' title='Go Back' action='?action=display_sub_menu&nwp2_action=ecm_cash_advance&todo=&nwp2_todo=control_panel";
                        if( jget.has( "workflow" ) && ! jget.getString( "workflow" ).isEmpty() && jget.has( "show_workflow_button" ) && jget.getBoolean( "show_workflow_button" ) ){
                            h2 += "&workflow="+ jget.getString( "workflow" ) +"&show_workflow_button=true";
                            sh2 = " <a href='javascript:;' class='btn blue custom-single-selected-record-button' action='?action=display_sub_menu&nwp2_action=nwp_ecm&todo=&nwp2_todo=control_panel&html_replacement_selector="+ $container +"&workflow="+ jget.getString( "workflow" ) +"&menu_title=Open+and+View+Details' override-selected-record='"+ $item.getString( "id" ) +"' >Open Details &uarr;</a> ";
                        }
                        if( isValidateTripDetails ){
                            h2 += "&isValidating=1";
                        }
                        h2 += "&html_replacement_selector="+ $container +"&menu_title=Open+and+View+Details' override-selected-record='"+ $item.getString( "id" ) +"' > &larr; </a> ";
                        h2 += sh2;

                        if( jget.has( "workflow" ) && ! jget.getString( "workflow" ).isEmpty() ){
                            JSONObject $qs = new JSONObject();
                            $qs.put( "id", jget.getString( "workflow" ) );
                            $qs.put( "table", nwpWorkflow.table_name );

                            JSONObject wrkfl = GlobalFunctions.get_record( $qs );
                            if( wrkfl.has("status") && ! wrkfl.getString("status").isEmpty() ) {
                                h2 += "<a href='javascript:;' class='btn blue custom-single-selected-record-button' action='?action=display_sub_menu&nwp2_action=nwp_workflow&todo=&nwp2_todo=submit_comment&nwp_wf_cur_state=" + GlobalFunctions.md5(wrkfl.getString("status") + nwpWorkflow.salter) + "&nwp2_type=submit_comment&html_replacement_selector=" + $container + "&menu_title=Submit+for+Approval' override-selected-record='" + jget.getString("workflow") + "' >Submit for Approval &rarr;</a><hr />";
                            }
                        }
                            //h2 += "<a href='javascript:;' class='btn blue custom-single-selected-record-button' action='?action=display_sub_menu&nwp2_action=ecm_cash_advance&todo=&nwp2_todo=submit_for_approval&html_replacement_selector="+ $container +"&menu_title=Submit+for+Approval' override-selected-record='"+ $item.getString( "id" ) +"' >Submit for Approval &rarr;</a><hr />";
                        col1 = "3";
                        col2 = "6";
                        break;
                    default:
                        if( ! justSaved ){
                            allowEdit = false;
                            JSONObject a = new JSONObject();
                            a.put("update_ref", $item.getString("id") );
                            a.put("update_ref_table", nwpCashAdvance.table_name );
                            a.put("update_op", false ); //return only workflow data, do not update

                            JSONObject wf = nwpEcm2.directUpdateWorkflow( a );
                            if( wf.has("record") && wf.getJSONObject("record").has("status") ){
                                $workflow = wf.getJSONObject("record").getString("id");
                                switch( wf.getJSONObject("record").getString("status") ){
                                    case "start":
                                    case "draft":
                                        allowEdit = true;
                                        break;
                                }
                            }
                        }


                        if( isValidateTripDetails ){
                            allowEdit = true;
                        }

                        if( allowEdit ){
                            h1 = "<h4>1. Enter & Save Details of Trip</h4>";
                            if( isValidateTripDetails ){
                                h1 = "<h4>1. Validate & Update Details of Trip</h4>";
                                jdata.put( "isValidateTripDetails", isValidateTripDetails );
                            }
                        }else{
                            h1 = "<h4>View Details of Trip</h4>";
                            JSONObject j1 = new JSONObject();
                            j1.put("id", $workflow);
                            j1.put("hide_buttons", true);
                            h3 = "<h4>Approval Info</h4>" + nwpWorkflow.getDetailsView(j1);

                        }

                        String tcon = "single_trip-form-con";
                        jdata.put( "html_replacement_selector", tcon );

                        switch (  $item.getString( "type" ) ){
                            case "single_trip":
                                //get single trip form
                                validTrip = true;
                                if( ! justSaved ){
                                    jdata.put( "check_existing", true );
                                }

                                col1 = "4 col-md-offset-1";
                                col2 = "4 col-md-offset-1";

                                if( allowEdit ){
                                    h1 += "<div id='"+ tcon +"' class='card mb-3' style='padding:1rem;'>"+ nwpCashAdvance.getTripDetailsForm( jdata ) +"</div>";
                                }else{
                                    validTrip = false;
                                    jdata.put( "id", nwpCashAdvance.getTripDetailsForm(jdata.put("update_op", false)) );
                                    jdata.put( "table", nwpCashAdvance.table_name_trip );

                                    h1 += "<div id='"+ tcon +"' class='card mb-3' styleX='padding:1rem;'>"+ GlobalFunctions.view_details( jdata, new JSONObject().put("buttons","") ) +"</div>";

                                    h2 = "<h4>Summary</h4>";
                                    h3 = "</div><div class='col-md-4'>" + h3;

                                    col1 = "4";
                                    col2 = "4";
                                }
                                break;
                            case "admin_cheque":
                            case "reimbursement":
                            case "refund":
                            case "retire":
                            case "multiple_trip":
                                //get trips table
                                validTrip = true;

                                if( allowEdit ){

                                }else{
                                    validTrip = false;
                                    h2 = h3 + "<hr /><h4>Summary</h4>";
                                    h3 = "";
                                    //h3 = "<hr />" + h3;
                                }

                                Boolean showAddDelete = true;
                                Boolean adminCheque = false;
                                switch (  $item.getString( "type" ) ) {
                                    case "reimbursement":
                                    case "refund":
                                    case "retire":
                                        if( nwpCashAdvance.linkRetirement && $item.has( "previous_ref" ) && ! $item.getString( "previous_ref" ).isEmpty() ){
                                            showAddDelete = false;
                                        }
                                        break;
                                    case "admin_cheque":
                                        adminCheque = true;
                                        break;
                                }

                                JSONObject $tableData = new JSONObject();
                                JSONObject $query = new JSONObject();
                                JSONObject $dbs = new JSONObject();

                                $tableData.put("table", nwpCashAdvance.table_name_trip );
                                JSONObject depT = GlobalFunctions.get_json( $tableData.getString("table") );
                                if (depT.has("fields")) {
                                    $query.put("where", " AND [" + $tableData.getString("table") + "].[" + depT.getJSONObject("fields").getString("cash_advance") + "] = '" + $item.getString( "id" ) + "' ");
                                    $tableData.put("query", $query);

                                    if( depT.has("datatable_options") ){
                                        $dbs = new JSONObject( depT.getJSONObject("datatable_options").toString() );
                                        if( ! $dbs.has( "view_details_options" ) ) {
                                            $dbs.put("view_details_options", new JSONObject().put("no_params", true) );
                                        }
                                    }

                                    $dbs.put("show_delete_button", 0);
                                    $dbs.put("show_add_new", 0);
                                    $dbs.put("show_edit_button", 0);

                                    if( allowEdit ){
                                        String bparams = "";

                                        if( showAddDelete ) {
                                            $dbs.put("show_delete_button", 1);
                                            $dbs.put("show_add_new", 1);
                                            //bparams += "&ca_ad=ac";
                                        }else{
                                            bparams += "&ca_ad=1";
                                        }
                                        if( adminCheque ){
                                            bparams = "&ca_ad=ac";
                                        }

                                        $dbs.put("show_edit_button", 1);
                                        $dbs.put("skip_access_control", "1");

                                        if( jdata.has("html_replacement_selector") ) {
                                            $dbs.put("html_replacement_selector", jdata.getString("html_replacement_selector"));

                                            //bcos of custom buttons
                                            bparams += "&html_replacement_selector=" + jdata.getString("html_replacement_selector");
                                        }
                                        bparams += "&ca_type=" + $item.getString( "type" );
                                        $dbs.put("button_params", bparams + "&nwp_view=" + nwpCashAdvance.table_name + "&cash_advance=" + $item.getString( "id" ) );

                                        JSONObject newButtonOptions = new JSONObject();
                                        newButtonOptions.put("nwp2_action", "display_sub_menu" );
                                        newButtonOptions.put("nwp2_todo", "trip_details_form");
                                        newButtonOptions.put("params", "&nwp2_action=" + $tableData.getString("table") + "&nwp2_todo=trip_details_form" );
                                        $dbs.put("show_add_new_options", newButtonOptions );

                                        newButtonOptions = new JSONObject();
                                        newButtonOptions.put("nwp2_action", "display_sub_menu" );
                                        newButtonOptions.put("nwp2_todo", "trip_details_form");
                                        newButtonOptions.put("params", "&nwp2_action=" + $tableData.getString("table") + "&nwp2_todo=trip_details_form" );
                                        $dbs.put("show_edit_button_options", newButtonOptions );
                                    }

                                    $dbs.put("hide_title", "1");
                                    $tableData.put("datatable_settings", $dbs);
                                    //$tableData.put("table_settings", depT);

                                    h1 += "<div id='"+ tcon +"'>" + nwpDataTable.getDataTable($tableData) + "</div>";
                                    GlobalFunctions.app_load_datatable = true;
                                }

                                break;
                            default:
                                h1 = "<b>Unrecognized Trip Type: " + $item.getString( "type" ) + "</b>";
                                break;
                        }

                        if( validTrip ){
                            h2 = "<h4>2. Review Computation</h4><a href='javascript:;' class='btn blue custom-single-selected-record-button' action='?action=display_sub_menu&nwp2_action=ecm_cash_advance&todo=&nwp2_todo=review_computation";
                            if( jget.has( "workflow" ) && ! jget.getString( "workflow" ).isEmpty() ){
                                h2 += "&workflow="+ jget.getString( "workflow" );
                            }
                            if( isValidateTripDetails ){
                                h2 += "&isValidating=1";
                            }
                            if( jget.has( "show_workflow_button" ) && jget.getBoolean( "show_workflow_button" ) ){
                                h2 += "&show_workflow_button=true";
                            }
                            h2 += "&html_replacement_selector="+ $container +"&menu_title=Review+Computation' override-selected-record='"+ $item.getString( "id" ) +"'>Review Computation &rarr;</a><hr />";
                        }
                        break;
                }

                jget.remove("record");  //might remove if $vd is set
                $vd.put("buttons","");

                $r = "";
                if( justSaved ){
                    $r += "<div class=\"note note-info alert alert-dismissable \"><button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\"></button>You can resume or track your cash advance job via <b>MY SPACE</b> menu</div>";
                }

                //$r += "<div class='row'><div class='col-md-"+col1+"'>"+ h1 +"</div><div class='col-md-"+col2+"'>" + h2 + GlobalFunctions.view_details( jget, $vd ) + h3 +"</div></div></div>";
                //System.out.println( "patron" );
                //System.out.println( jget );
                jget.put("hide_buttons", 1);
                $r += "<div class='row'><div class='col-md-"+col1+"'>"+ h1 +"</div><div class='col-md-"+col2+"'>" + h2 + nwpCashAdvance.getSingleDetailsView( jget ) + h3 +"</div></div></div>";


            }else{
                $r = $table + "->getDetailsView: Unable to Retrieve Record";
            }
        }else{
            $r = $table + "->getDetailsView: Invalid Reference ID";
        }


        return $r;
    }

    public static String getTripDetailsForm( JSONObject jdata ){
        String $return = "";

        jdata.put( "table", nwpCashAdvance.table_name_trip );
        Boolean updateOp = jdata.has("update_op")?jdata.getBoolean("update_op"):true;
        Boolean isValidateTripDetails = jdata.has("isValidateTripDetails")?jdata.getBoolean("isValidateTripDetails"):false;
        if( isValidateTripDetails ){
            updateOp = true;
        }

        JSONObject dep = GlobalFunctions.get_json( jdata.getString("table") );
        JSONObject labels2 = new JSONObject();
        String saveLabel = "Trip";
        String actionLabel = "New ";
        String eparams = "";
        if( jdata.has( "cash_advance" ) && ! jdata.getString( "cash_advance" ).isEmpty() ) {

            Boolean retire = false;
            Boolean adminCheque = false;
            Boolean showAddDelete = true;
            if( jdata.has( "ca_type" ) ) {
                switch (jdata.getString("ca_type")) {
                    case "reimbursement":
                    case "refund":
                    case "retire":
                        retire = true;
                        saveLabel = "Expense";
                        eparams += "&ca_type=" + jdata.getString("ca_type");
                        if( jdata.has( "ca_ad" ) && ! jdata.getString( "ca_ad" ).isEmpty() ) {
                            eparams += "&ca_ad=" + jdata.getString("ca_ad");
                            showAddDelete = false;
                        }
                        break;
                    case "admin_cheque":
                        adminCheque = true;
                        saveLabel = "Administrator Cheque";
                        eparams += "&ca_type=" + jdata.getString("ca_type");
                        if( jdata.has( "ca_ad" ) && ! jdata.getString( "ca_ad" ).isEmpty() ) {
                            eparams += "&ca_ad=" + jdata.getString("ca_ad");
                        }
                        break;
                }
            }

            if (dep.has("labels") && dep.has("form_order")) {
                JSONObject labels = dep.getJSONObject("labels");
                String $tmpVal = "";
                String $key1 = "";
                JSONObject $line;

                if (labels.names().length() > 0) {

                    for (int i2 = 0; i2 < labels.names().length(); i2++) {
                        $key1 = labels.names().getString(i2);
                        $line = labels.getJSONObject($key1);

                        switch ($line.getString("field_identifier")) {
                            case "to_zone":
                            case "from_zone":
                                if (GlobalFunctions.app_data.has("hidden_zones_in_cash_advance")
                                        && GlobalFunctions.app_data.getBoolean("hidden_zones_in_cash_advance")) {
                                    $line.put("hidden_records", true);
                                }
                                break;
                        }

                        switch ($line.getString("field_identifier")) {
                            case "cash_advance":
                                if (jdata.has("cash_advance")) {
                                    $line.put("value", jdata.getString("cash_advance"));
                                    $line.put("hidden_records_css", true);
                                    labels2.put($key1, $line);
                                }
                                break;
                            case "cash_advance_type":
                                if (jdata.has("ca_type")) {
                                    $line.put("value", jdata.getString("ca_type"));
                                    $line.put("hidden_records_css", true);
                                    labels2.put($key1, $line);
                                }
                                break;
                            case "end_date":
                            case "start_date":
                                if( jdata.has( "ca_type" ) ) {
                                    switch (jdata.getString("ca_type")) {
                                        case "retire":
                                        case "reimbursement":
                                            if( $line.has("min_date") ) {
                                                $line.put("max_date", $line.get("min_date") );
                                                $line.remove("min_date");
                                            }
                                            break;
                                    }
                                }
                            case "to_zone":
                            case "from_zone":
                            case "to_location":
                            case "from_location":
                                if( ! showAddDelete ){
                                    $line.put("readonly", true);
                                }
                                labels2.put($key1, $line);
                                break;
                            case "transport":
                                if( showAddDelete ){
                                    labels2.put($key1, $line);
                                }
                                break;
                            case "comment":
                                labels2.put($key1, $line);
                                break;
                            /*case "amount_spent":
                                if( retire ) {
                                    $line.put("required_field", "yes");
                                    labels2.put($key1, $line);
                                }
                                break;*/
                            case "date":
                                $line.put("value", GlobalFunctions.get_current_time(0));
                                $line.put("hidden_records_css", true);
                                labels2.put($key1, $line);
                                break;
                            case "staff":
                                if( adminCheque ) {
                                    $line.put("required_field", "yes");
                                    labels2.put($key1, $line);
                                }else if( isValidateTripDetails ){
                                    $line.put("required_field", "yes");
                                    $line.put("hidden_records_css", true);
                                    labels2.put($key1, $line);
                                }
                                break;
                            default:
                                if( retire ) {
                                    switch ($line.getString("field_identifier")) {
                                        case "amount_spent":
                                        //case "transport_amount":
                                        //case "extra_days":
                                        //case "lunch_amount":
                                        //case "incidence_amount":
                                            break;
                                        default:
                                            $line.put("hidden_records", true);
                                            break;
                                    }
                                }else{
                                    $line.put("hidden_records", true);
                                }
                                labels2.put($key1, $line);
                                break;
                        }

                    }
                }

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "test XX2: " )
                                .put("input", new JSONObject().put("retire", retire).put("jdata", jdata) )
                                .put("function", "text234" )
                                .put("exception", false )
                                .put("fatal", true ) , null
                );


                JSONObject $qf = dep.getJSONObject("fields");
                if (jdata.has("check_existing") && jdata.getBoolean("check_existing")) {
                    JSONObject $qs = new JSONObject();
                    //$qs.put("select", "SELECT [id] as 'id' ");
                    String $where = " AND [" + nwpCashAdvance.table_name_trip + "].[" + $qf.getString("cash_advance") + "] = '" + jdata.getString( "cash_advance" ) + "' ";
                    $qs.put("where", $where);
                    $qs.put("table", nwpCashAdvance.table_name_trip);

                    JSONObject $qr = GlobalFunctions.get_records($qs);
                    JSONObject $qt;

                    if ($qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) {
                        $qt = $qr.getJSONArray("row").getJSONObject(0);
                        if ( $qt.has("id") && ! $qt.getString("id").isEmpty()) {
                            if( updateOp ){
                                jdata.put( "record_id", $qt.getString( "id" ) );
                                jdata.put( "values", $qt );
                                labels2.remove( $qf.getString("date") );
                                labels2.remove( $qf.getString("cash_advance") );
                                actionLabel = "Edit ";
                            }else{
                                $return =  $qt.getString( "id" );
                            }
                        }
                    }
                }

                if( jdata.has( "edit_record" ) && jdata.getBoolean( "edit_record" ) ){
                    labels2.remove( $qf.getString("date") );
                    labels2.remove( $qf.getString("cash_advance") );
                    actionLabel = "Edit ";
                }
                dep.put("labels", labels2);
            }


            if( updateOp ) {
                jdata.put("table_settings", dep);
                jdata.put("todo", "save_trip_details&nwp_view=" + nwpCashAdvance.table_name + eparams);
                jdata.put("submit_label", "Save "+ saveLabel +" Details &rarr;");
                $return = nwpDataTable.getDataForm(jdata);
            }

            if( GlobalFunctions.app_popup ){
                GlobalFunctions.app_popup_title = actionLabel + saveLabel +" Details";
            }
        }else{
            if( updateOp ) {
                $return = "Invalid Cash Advance ID/Ref";
            }
        }
        return $return;
    }

    public static JSONObject saveTripDetailsForm( JSONObject jdata ){
        JSONObject r = new JSONObject();
        JSONObject jpet = jdata.has("post_data")?jdata.getJSONObject("post_data"):new JSONObject();
        String error = "";
        JSONObject $qs = new JSONObject();

        JSONObject dep = GlobalFunctions.get_json( nwpCashAdvance.table_name_trip );
        if ( dep.has("fields") ) {
            JSONObject $qf = dep.getJSONObject("fields");
            Boolean calcValues = true;
            if ( jdata.has("ca_ad") && ! jdata.getString("ca_ad").isEmpty() ) {
                calcValues = false;
                if( jdata.getString("ca_ad").equals("ac") ){
                    calcValues = true;
                }
            }

            /*JSONObject cashAd = new JSONObject();
            if( ! calcValues ){
                if( jpet.has($qf.getString("cash_advance") ) && ! jpet.getString( $qf.getString("cash_advance") ).isEmpty() ) {
                    $qs = new JSONObject();
                    $qs.put("id", jpet.getString($qf.getString("cash_advance") ) );
                    $qs.put("table", nwpCashAdvance.table_name );
                    cashAd = GlobalFunctions.get_record( $qs );

                    if( cashAd.has("status") && cashAd.getString("status").equals("draft") ){
                        calcValues = true;
                    }
                }else{
                    error = "<h4>Invalid Cash Advance</h4>Please restart the process";
                }
            }*/

            String $trip_type = "";
            if( error.isEmpty() ) {
                if ( calcValues ) {
                    if (jpet.has($qf.getString("transport")) && !jpet.getString($qf.getString("transport")).isEmpty()) {
                        $trip_type = jpet.getString($qf.getString("transport"));
                    } else {
                        error = "<h4>Invalid Trip Type</h4>Specify a valid mode of transportation at Flight or Land etc";
                    }
                }
            }


            Double tripDuration = 0.00;
            if( error.isEmpty() ){
                if ( calcValues ) {
                    if ($qf.has("start_date") && jpet.has($qf.getString("start_date")) && !jpet.getString($qf.getString("start_date")).isEmpty()) {
                        if ($qf.has("end_date") && jpet.has($qf.getString("end_date")) && !jpet.getString($qf.getString("end_date")).isEmpty()) {

                            tripDuration = Double.valueOf(String.valueOf(GlobalFunctions.convert_date_to_timestamp(jpet.getString($qf.getString("end_date")), 0, new JSONObject())))
                                    - Double.valueOf(String.valueOf(GlobalFunctions.convert_date_to_timestamp(jpet.getString($qf.getString("start_date")), 0, new JSONObject())));
                            tripDuration = Math.floor(tripDuration / (3600 * 24));

                            if (tripDuration < 0) {
                                error = "<h4>Invalid Trip Duration</h4>Return Date must be greater than the Start Date<br>tripDuration = " + tripDuration;
                            } else if (tripDuration == 0) {
                                tripDuration = 1.00;    //same day trip, start date == end date
                            }
                            jpet.put($qf.getString("duration"), String.valueOf(new Double(tripDuration).intValue()));
                        } else {
                            error = "<h4>Invalid End Date of Trip</h4>";
                        }
                    } else {
                        error = "<h4>Invalid Start Date of Trip</h4>";
                    }
                }
            }

            Boolean loggedInStaff = false;
            if( error.isEmpty() ){
                if ( calcValues ) {
                    if (!jpet.has($qf.getString("staff"))) {
                        jpet.put($qf.getString("staff"), GlobalFunctions.app_user);
                        loggedInStaff = true;
                    }
                }
            }

            if( error.isEmpty() && ! jpet.has($qf.getString("grade_level") ) ) {
                if ( calcValues ) {
                    //test for grade level
                    error = checkForUpdatedGradeLevel( loggedInStaff, jpet, $qf );


                    if( error.isEmpty() ){
                        if (loggedInStaff) {
                            if (GlobalFunctions.app_user_data.has("grade_level") && !GlobalFunctions.app_user_data.getString("grade_level").isEmpty()) {
                                jpet.put($qf.getString("grade_level"), GlobalFunctions.app_user_data.getString("grade_level"));

                                if (!jpet.has($qf.getString("department"))) {
                                    if (GlobalFunctions.app_user_data.has("department") && !GlobalFunctions.app_user_data.getString("department").isEmpty()) {
                                        jpet.put($qf.getString("department"), GlobalFunctions.app_user_data.getString("department"));
                                    }
                                }
                            } else {
                                error = "Unable to Save Trip: No Grade Level found for user " + GlobalFunctions.app_user;
                            }
                        } else {
                            //selected staff
                            if (jpet.has($qf.getString("staff")) && !jpet.getString($qf.getString("staff")).isEmpty()) {
                                JSONObject staffRecord = GlobalFunctions.get_record(new JSONObject().put("table", nwpAccessRoles.users_table).put("id", jpet.getString($qf.getString("staff"))));
                                if (staffRecord.has("id")) {
                                    if (staffRecord.has("grade_level") && !staffRecord.getString("grade_level").isEmpty()) {
                                        jpet.put($qf.getString("grade_level"), staffRecord.getString("grade_level"));

                                        if (!jpet.has($qf.getString("department"))) {
                                            if (staffRecord.has("department") && !staffRecord.getString("department").isEmpty()) {
                                                jpet.put($qf.getString("department"), staffRecord.getString("department"));
                                            }
                                        }
                                    } else {
                                        error = "Unable to Save Trip: The selected staff has no Grade Level " + staffRecord.getString("id");
                                    }
                                } else {
                                    error = "Unable to Save Trip: The selected staff is not registered " + jpet.getString($qf.getString("staff"));
                                }
                            } else {
                                error = "Unable to Save Trip: The staff was not specified";
                            }
                        }
                    }
                }
            }

            if( error.isEmpty() ) {
                if ( calcValues ) {

                    String toZone = "to_zone";
                    String fromZone = "from_zone";

                    if( GlobalFunctions.app_data.has("hidden_zones_in_cash_advance")
                            && GlobalFunctions.app_data.getBoolean("hidden_zones_in_cash_advance") ) {
                        if (
                                $qf.has( toZone ) &&
                                        $qf.has( fromZone ) &&
                                        $qf.has( "from_location" ) && jpet.has( $qf.getString( "from_location" ) ) && ! jpet.getString( $qf.getString( "from_location" ) ).isEmpty()
                                        && $qf.has("to_location") && jpet.has( $qf.getString("to_location") ) && ! jpet.getString( $qf.getString("to_location") ).isEmpty()
                        ) {

                            String toLocation = jpet.getString($qf.getString("to_location"));
                            String fromLocation = jpet.getString($qf.getString("from_location"));
                            //get zones from locations
                            JSONObject zonesFromLocations = nwpCashAdvance.getCashAdvanceZoneData(
                                    new JSONObject()
                                            .put("recordIDs",
                                                    new JSONArray()
                                                            .put( toLocation )
                                                            .put( fromLocation )
                                            ));

                            if( zonesFromLocations.has("row") ){
                                if( zonesFromLocations.getJSONObject("row").has( toLocation )
                                        && zonesFromLocations.getJSONObject("row").getJSONObject( toLocation ).has("zone") ){
                                    jpet.put( $qf.getString( toZone ),  zonesFromLocations.getJSONObject("row").getJSONObject( toLocation ).getString("zone") );
                                }

                                if( zonesFromLocations.getJSONObject("row").has( fromLocation )
                                        && zonesFromLocations.getJSONObject("row").getJSONObject( fromLocation ).has("zone") ){
                                    jpet.put( $qf.getString( fromZone ),  zonesFromLocations.getJSONObject("row").getJSONObject( fromLocation ).getString("zone") );
                                }
                            }

                        }
                    }

                    if (
                            $qf.has( toZone ) && jpet.has( $qf.getString( toZone ) ) && ! jpet.getString( $qf.getString( toZone ) ).isEmpty()
                                    && $qf.has("to_location") && jpet.has( $qf.getString("to_location") ) && ! jpet.getString( $qf.getString("to_location") ).isEmpty()
                    ) {
                        if (
                                $qf.has( fromZone ) && jpet.has( $qf.getString( fromZone ) ) && ! jpet.getString( $qf.getString( fromZone ) ).isEmpty()
                                        && $qf.has("from_location") && jpet.has( $qf.getString("from_location") ) && ! jpet.getString( $qf.getString("from_location") ).isEmpty()
                        ) {
                            /*if ( jpet.getString("from_location").equals( jpet.getString("from_location") ) ) {
                                error = "<h4>Invalid Origin & Destination</h4>Destination (To) cannot be equal to Origin (From)";
                            }*/
                        }else{
                            error = "<h4>Invalid Origin (From)</h4>";
                        }
                    }else{
                        error = "<h4>Invalid Destination (To)</h4>";
                    }
                }
            }

            if( calcValues && error.isEmpty() ) {
                //get amount...
                Double amount = 0.00;

                Double noOfDaysForPerDiem = 0.00;
                Double noOfDaysIncrement = 0.00;
                Double perDiemSettings = 0.00;
                Double localRunsMultiplier = 0.05;
                Double incidenceAmount = 0.00;
                Double transportAmount = 0.00;
                Double lunchAmount = 0.00;
                Double accommodationAmount = 0.00;
                Double lunchInTransitAmount = 0.00;
                Double othersAmount = 0.00;

                JSONObject caSettings = GlobalFunctions.getGeneralSettings( "cash_advance" );
                if( caSettings.has("local_runs_multiplier")
                        && caSettings.getJSONObject("local_runs_multiplier").has("value")
                        && ! caSettings.getJSONObject("local_runs_multiplier").getString("value").isEmpty() ){
                    localRunsMultiplier = Double.valueOf( caSettings.getJSONObject("local_runs_multiplier").getString("value") );
                }
                if( caSettings.has("duration_increment")
                        && caSettings.getJSONObject("duration_increment").has("value")
                        && ! caSettings.getJSONObject("duration_increment").getString("value").isEmpty() ){
                    noOfDaysIncrement = Double.valueOf( caSettings.getJSONObject("duration_increment").getString("value") );

                    if( GlobalFunctions.app_data.has("add_increment_in_saved_duration")
                            && GlobalFunctions.app_data.getBoolean("add_increment_in_saved_duration") ){
                        jpet.put($qf.getString("duration"), String.valueOf(new Double(tripDuration + noOfDaysIncrement).intValue()));
                    }
                }


                JSONObject dep2 = GlobalFunctions.get_json( nwpCashAdvance.table_name_rate );
                if ( dep2.has("fields")) {
                    JSONObject $qf2 = dep2.getJSONObject("fields");

                    //get cash advance rates based on grade level
                    $qs = new JSONObject();
                    $qs.put("fields", $qf2);
                    String $where = " AND ";
                    $where += " [" + nwpCashAdvance.table_name_rate + "].[" + $qf2.getString("grade_level") + "] = '" + jpet.getString($qf.getString("grade_level")) + "' ";

                    $where += " AND [" + nwpCashAdvance.table_name_rate + "].[" + $qf2.getString("zone") + "] = '" + jpet.getString($qf.getString("to_zone")) + "' ";

                    if( $qf.has("department") && jpet.has($qf.getString("department")) && ! jpet.getString($qf.getString("department")).trim().isEmpty() ){
                        $where += " AND ( [" + nwpCashAdvance.table_name_rate + "].[" + $qf2.getString("department") + "] = '" + jpet.getString($qf.getString("department")) + "' OR [" + nwpCashAdvance.table_name_rate + "].[" + $qf2.getString("department") + "] = '' OR [" + nwpCashAdvance.table_name_rate + "].[" + $qf2.getString("department") + "] IS NULL ) ";
                    }


                    $qs.put("where", $where);
                    $qs.put("table", nwpCashAdvance.table_name_rate);

                    JSONObject cashAdvanceRates = GlobalFunctions.get_records($qs);
                    JSONObject cashAdRateRecord = new JSONObject();
                    String $key1;

                    if (cashAdvanceRates.has("row_count") && cashAdvanceRates.has("row") && cashAdvanceRates.getInt("row_count") > 0) {
                        for (int i2 = 0; i2 < cashAdvanceRates.getInt("row_count"); i2++) {
                            cashAdRateRecord = cashAdvanceRates.getJSONArray("row").getJSONObject(i2);
                            if( cashAdRateRecord.has("department") && ! cashAdRateRecord.getString("department").isEmpty() ){
                                break;
                            }
                        }
                    }

                    //if no rate is found, then use default settings
                    if( GlobalFunctions.app_data.has("use_default_cash_advance_settings")
                            && GlobalFunctions.app_data.getBoolean("use_default_cash_advance_settings") ){
                        if( ! cashAdRateRecord.has("grade_level") ){
                            if( caSettings.length() > 0 ){
                                for( Object ck : $qf2.names() ){
                                    String cks = ck.toString();
                                    if( caSettings.has( cks )
                                            && caSettings.getJSONObject( cks ).has("value")
                                            && ! caSettings.getJSONObject( cks ).getString("value").isEmpty() ){
                                        cashAdRateRecord.put( cks , caSettings.getJSONObject( cks ).getString("value") );
                                    }
                                }
                                cashAdRateRecord.put("grade_level", jpet.getString($qf.getString("grade_level")) );
                                cashAdRateRecord.put("department", jpet.getString($qf.getString("department")) );
                            }
                        }
                    }


                    if( cashAdRateRecord.has("grade_level") ){

                        JSONObject cashAdvanceKM = new JSONObject();
                        if(  $trip_type.equals("land") ){
                            //get cash advance zone info and kilometer chart
                            JSONObject dep3 = GlobalFunctions.get_json( nwpCashAdvance.table_name_km_chart );
                            if ( dep3.has("fields")) {
                                JSONObject $qf2k = dep3.getJSONObject("fields");
                                $qs = new JSONObject();
                                $qs.put("fields", $qf2k);
                                $where = " AND ";
                                $where += " ( ";
                                $where += " ( [" + nwpCashAdvance.table_name_km_chart + "].[" + $qf2k.getString("from") + "] = '" + jpet.getString($qf.getString("from_location")) + "' AND [" + nwpCashAdvance.table_name_km_chart + "].[" + $qf2k.getString("to") + "] = '" + jpet.getString($qf.getString("to_location")) + "' ) ";
                                $where += " OR ( [" + nwpCashAdvance.table_name_km_chart + "].[" + $qf2k.getString("to") + "] = '" + jpet.getString($qf.getString("from_location")) + "' AND [" + nwpCashAdvance.table_name_km_chart + "].[" + $qf2k.getString("from") + "] = '" + jpet.getString($qf.getString("to_location")) + "' ) ";
                                $where += " ) ";
                                $qs.put("where", $where);
                                $qs.put("table", nwpCashAdvance.table_name_km_chart);
                                $qs = GlobalFunctions.get_records($qs);
                                if ($qs.has("row_count") && $qs.has("row") && $qs.getInt("row_count") > 0) {
                                    cashAdvanceKM = $qs.getJSONArray("row").getJSONObject(0);
                                }
                            }
                        }else{
                            cashAdvanceKM.put("id", $trip_type);
                        }

                        if (cashAdvanceKM.has("id") ) {
                            Double multiplier = 0.00;
                            Double markup = 0.00;
                            Double fare = 0.00;

                            for (int i3 = 0; i3 < cashAdRateRecord.length(); i3++) {
                                fare = 0.00;
                                markup = 0.00;
                                multiplier = 0.00;

                                $key1 = cashAdRateRecord.names().getString(i3);

                                switch ($key1) {
                                    case "land_fare":
                                    //case "flight_fare": //before 27th Aug 2023, flight fare was calculated based on kM

                                        if ( cashAdRateRecord.has($key1) && $key1.equals( $trip_type + "_fare" ) ) {

                                            if( cashAdvanceKM.has($trip_type ) && ! cashAdvanceKM.getString($trip_type ).isEmpty() ){
                                                multiplier = Double.valueOf( cashAdvanceKM.getString($trip_type ));
                                            }
                                            fare = ( Double.valueOf(cashAdRateRecord.getString($key1)) * multiplier );

                                            if( cashAdvanceKM.has($trip_type + "_percent") && ! cashAdvanceKM.getString($trip_type + "_percent").isEmpty() ){
                                                markup = Double.valueOf(cashAdvanceKM.getString($trip_type + "_percent"));
                                                if( markup > 0 || markup < 0 ) {
                                                    fare += (fare * markup / 100);
                                                }
                                            }

                                            transportAmount = fare;
                                        }
                                        break;
                                    case "flight_taxi":     //after 27th Aug 2023, flight fare is now fixed amount
                                    case "flight_fare":     //after 27th Aug 2023, flight fare is now fixed amount
                                        if ( $key1.equals( $trip_type + "_taxi" ) || $key1.equals( $trip_type + "_fare" ) ) {
                                            if (cashAdRateRecord.has($key1) && !cashAdRateRecord.getString($key1).isEmpty()) {
                                                transportAmount += Double.valueOf(cashAdRateRecord.getString($key1));
                                            }
                                        }
                                        break;
                                    case "lunch":
                                        if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                            lunchAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                        }
                                        break;
                                    case "accommodation":
                                        if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {

                                            if( GlobalFunctions.app_data.has("add_increment_in_accommodation")
                                                    && GlobalFunctions.app_data.getBoolean("add_increment_in_accommodation") ){
                                                accommodationAmount = ( tripDuration + noOfDaysIncrement ) * Double.valueOf(cashAdRateRecord.getString($key1));
                                            }else if( GlobalFunctions.app_data.has("add_no_of_days_in_accommodation")
                                                    && GlobalFunctions.app_data.getBoolean("add_no_of_days_in_accommodation") ){
                                                accommodationAmount = ( tripDuration ) * Double.valueOf(cashAdRateRecord.getString($key1));
                                            }else{
                                                accommodationAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                            }
                                        }
                                        break;
                                    case "per_diem":
                                        //PD = ( no of days + increment ) * 90k[per diem value for grade level & zone based on destination zone]
                                        if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                            perDiemSettings = Double.valueOf(cashAdRateRecord.getString($key1));
                                        }
                                        break;
                                    case "local_runs":
                                        //LR = 0.05 * no of days * PD
                                        break;
                                    case "incidence":
                                        if( ! $trip_type.equals("flight") ){
                                            if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                                incidenceAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                            }
                                        }
                                        break;
                                    case "incidence_flight":
                                        if( $trip_type.equals("flight") ){
                                            if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                                incidenceAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                            }
                                        }
                                        break;
                                    case "lunch_intransit":
                                        if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                            lunchInTransitAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                        }
                                        break;
                                    case "others":
                                        if (cashAdRateRecord.has($key1) && ! cashAdRateRecord.getString($key1).isEmpty() ) {
                                            othersAmount = Double.valueOf(cashAdRateRecord.getString($key1));
                                        }
                                        break;
                                }

                            }

                            if( ! ( GlobalFunctions.app_data.has("exclude_accommodation_from_calculation")
                                    && GlobalFunctions.app_data.getBoolean("exclude_accommodation_from_calculation") ) ) {
                                amount += accommodationAmount;
                                jpet.put($qf.getString("accommodation_amount"), String.valueOf(accommodationAmount));
                            }

                            amount += lunchInTransitAmount;
                            jpet.put($qf.getString("lunch_intransit_amount"), String.valueOf(lunchInTransitAmount));

                            amount += othersAmount;
                            jpet.put($qf.getString("others_amount"), String.valueOf(othersAmount));

                            amount += transportAmount;
                            jpet.put($qf.getString("transport_amount"), String.valueOf(transportAmount));

                            if( ! ( GlobalFunctions.app_data.has("exclude_lunch_amount_from_calculation")
                                    && GlobalFunctions.app_data.getBoolean("exclude_lunch_amount_from_calculation") ) ) {
                                amount += lunchAmount;
                                jpet.put($qf.getString("lunch_amount"), String.valueOf(lunchAmount));
                            }

                            amount += incidenceAmount;
                            jpet.put($qf.getString("incidence_amount"), String.valueOf(incidenceAmount));

                            jpet.put($qf.getString("duration_increment"), String.valueOf(noOfDaysIncrement));

                            noOfDaysForPerDiem = noOfDaysIncrement;
                            if( GlobalFunctions.app_data.has("use_number_of_nights_in_per_diem")
                                    && GlobalFunctions.app_data.getBoolean("use_number_of_nights_in_per_diem") ){
                                noOfDaysForPerDiem = tripDuration;
                                if( GlobalFunctions.app_data.has("number_of_nights_per_diem_modifier") ){
                                    noOfDaysForPerDiem += GlobalFunctions.app_data.getDouble("number_of_nights_per_diem_modifier");
                                }
                            }
                            Double perDiem = ( noOfDaysForPerDiem ) * perDiemSettings;
                            amount += perDiem;
                            jpet.put($qf.getString("per_diem_amount"), String.valueOf(perDiem));
                            jpet.put($qf.getString("per_diem_settings"), String.valueOf(perDiemSettings));

                            Double localRuns = localRunsMultiplier * tripDuration * perDiemSettings;
                            if( GlobalFunctions.app_data.has("add_increment_in_localruns")
                                    && GlobalFunctions.app_data.getBoolean("add_increment_in_localruns") ){
                                localRuns = localRunsMultiplier * ( tripDuration + noOfDaysIncrement ) * perDiemSettings;
                            }
                            amount += localRuns;
                            jpet.put($qf.getString("local_runs"), String.valueOf(localRuns));
                            jpet.put($qf.getString("local_runs_multiplier"), String.valueOf(localRunsMultiplier));

                            if ((amount > 0)) {
                                jpet.put($qf.getString("amount"), String.valueOf(amount));
                                jpet.put($qf.getString("amount_approved"), String.valueOf(amount));
                            } else {
                                error = "<h4>No Rate Found</h4>The Selected Origin (From) & Destination (To) has no rate";
                            }
                        }else{
                            error = "<h4>No Distance in Kilometer Chart</h4>The Selected Origin (From) & Destination (To) has no distance defined";
                        }
                    }else{
                        error = "<h4>No Rate Found</h4>The Selected Staff Grade Level has no rate";
                    }
                }else{
                    error = "Unable to Read JSON Config " + nwpCashAdvance.table_name_rate;
                }
            }

        }else{
            error = "Unable to Read JSON Config " + nwpCashAdvance.table_name_trip;
        }

        if( error.isEmpty() ) {
            if (jpet.has("id") && !jpet.getString("id").isEmpty()) {
                jdata.put("id", jpet.getString("id"));
                jdata.put("todo", "edit");
            }
            jdata.put("post_data", jpet );
            jdata.put("show_view_details", false );

            //System.out.println("JADE");
            JSONObject rd = nwpDataTable.saveDataForm( jdata );
            //System.out.println(rd);
            if( rd.has( "saved_record" ) && rd.getJSONObject( "saved_record" ).has( "id" ) ){
                r = rd;
                r.put( "view_details",
                        GlobalFunctions.view_details(
                             new JSONObject()
                                .put("id", rd.getJSONObject( "saved_record" ).getString( "id" ) )
                                .put("table", nwpCashAdvance.table_name_trip )
                            , new JSONObject().put("buttons","")
                        )
                );
            }else if( rd.has("error") ){
                error = rd.getString("error");
            }
        }

        if( ! error.isEmpty() ){
            r.put("error", error );
        }
        return r;
    }

    public static JSONObject getCashAdvanceZoneData( JSONObject inputData ){
        JSONObject cashAdvanceZones = new JSONObject();

        if ( inputData.length() > 0 ) {
            JSONObject dep2 = GlobalFunctions.get_json(nwpCashAdvance.table_name_zone);

            if (dep2.has("fields")) {
                JSONObject $qf2 = dep2.getJSONObject("fields");
                JSONObject $qs = inputData;
                $qs.put("fields", $qf2);

                Boolean runQuery = false;
                String $where = "";
                if ( inputData.has("recordIDs") && inputData.getJSONArray("recordIDs").length() > 0 ) {
                    runQuery = true;
                }
                if( ! $where.isEmpty() ) {
                    runQuery = true;
                }

                if( runQuery ) {
                    $qs.put("where", $where);
                    $qs.put("table", nwpCashAdvance.table_name_zone);

                    cashAdvanceZones = GlobalFunctions.get_records($qs);
                }
            }
        }

        return cashAdvanceZones;
    }

    public static String checkForUpdatedGradeLevel( Boolean loggedInStaff, JSONObject jpet, JSONObject fields){
        String error = "";
        Boolean callAPI = true;

        if( GlobalFunctions.app_data.has("skip_staff_details_api")
                && GlobalFunctions.app_data.getJSONObject("skip_staff_details_api").has( "cash_advance" )
                && GlobalFunctions.app_data.getJSONObject("skip_staff_details_api").getBoolean( "cash_advance" ) ){
            callAPI = false;
        }

        String staffID = "";
        JSONObject staffRecord = new JSONObject();
        String grade_level = "";
        String modification_date = "";
        if( callAPI ) {
            Long cutOffTime = Long.valueOf("0");


            if (loggedInStaff) {
                staffRecord = GlobalFunctions.app_user_data;
                staffID = GlobalFunctions.app_user;
            } else {
                //selected staff
                if (jpet.has(fields.getString("staff")) && !jpet.getString(fields.getString("staff")).isEmpty()) {
                    staffID = jpet.getString(fields.getString("staff"));

                    staffRecord = GlobalFunctions.get_record( new JSONObject()
                            .put("table", nwpAccessRoles.users_table)
                            .put("id", staffID )
                    );
                } else {
                    error = "Unable to Verify Grade Level: The staff was not specified";
                }
            }

            if (staffRecord.has("id")) {
                if ( staffRecord.has("grade_level") && ! staffRecord.getString("grade_level").isEmpty() && staffRecord.has("modification_date") ) {
                    grade_level = staffRecord.getString("grade_level");
                    modification_date = staffRecord.getString("modification_date");
                }
            } else {
                error = "Unable to Verify Grade Level: The selected staff is not registered " + staffID;
            }

            if ( ! grade_level.isEmpty() && ! modification_date.isEmpty()) {
                if (GlobalFunctions.app_data.has("staff_details_api_cache_time_secs")
                        && GlobalFunctions.app_data.getInt("staff_details_api_cache_time_secs") > 0) {
                    Date currentDate = new Date();
                    long timestamp = currentDate.getTime() / 1000;
                    cutOffTime = timestamp - Long.valueOf(GlobalFunctions.app_data.get("staff_details_api_cache_time_secs").toString());
                }

                if (Long.valueOf(modification_date) >= cutOffTime) {
                    callAPI = false;
                }
            }
        }

        if( callAPI  && staffRecord.has("id") ){
            JSONObject r = nwpAccessRoles.updateEmployeeDetailsFromAPI(
                    new JSONObject( staffRecord.toString() )
                            .put("source", "cash_advance" )
            );

            if( r.has("error") ){
                error = r.getString("error");
            }
        }

        return error;
    }

    public static JSONObject getTripDetails( JSONObject jpet ){
        JSONObject $r = new JSONObject();
        JSONObject $qs = new JSONObject();
        String $where = "";
        if( jpet.has("id") && ! jpet.getString("id").isEmpty() ) {
            JSONObject dep2 = new JSONObject();
            JSONObject $qf2 = new JSONObject();

            if( jpet.has("fields") ) {
                $qf2 = jpet.getJSONObject("fields");
            }else{
                dep2 = GlobalFunctions.get_json(nwpCashAdvance.table_name_trip);
                $qf2 = dep2.getJSONObject("fields");
            }

            if( ! ( jpet.has("select_all") && jpet.getBoolean("select_all") ) ){
                $qs.put("select", "SELECT SUM( [" + nwpCashAdvance.table_name_trip + "].[" + $qf2.getString("amount") + "] ) as 'amount', " +
                        "SUM( [" + nwpCashAdvance.table_name_trip + "].[" + $qf2.getString("amount_spent") + "] ) as 'amount_spent' ");
            }
            $where = " AND [" + nwpCashAdvance.table_name_trip + "].[" + $qf2.getString("cash_advance") + "] = '" + jpet.getString("id") + "' ";
            $qs.put("where", $where);
            $qs.put("table", nwpCashAdvance.table_name_trip);

            $r = GlobalFunctions.get_records($qs);
        }

        return $r;
    }

    public static JSONObject reviewComputation( JSONObject jpet ){
        String h1 = "";
        String error = "";
        JSONObject r = new JSONObject();

        Boolean isValidateTripDetails = jpet.has("isValidateTripDetails")?jpet.getBoolean("isValidateTripDetails"):false;
        if( jpet.has("isValidating") && ! jpet.getString("isValidating").isEmpty() ){
            isValidateTripDetails = true;
        }

        //test for trip details...
        JSONObject dep = GlobalFunctions.get_json( nwpCashAdvance.table_name );
        JSONObject dep2 = GlobalFunctions.get_json( nwpCashAdvance.table_name_trip );
        Double amount = 0.00;
        Double amountSpent = 0.00;
        if ( ! jpet.has("id")) {
            error = "Unable to reviewComputation. Invalid CA Reference";
        }else if ( dep.has("fields") && dep2.has("fields")) {
            JSONObject $qf2 = dep2.getJSONObject("fields");

            JSONObject $qs = new JSONObject();
            String $where = "";

            $qs.put("id", jpet.getString("id") );
            $qs.put("table", nwpCashAdvance.table_name );
            JSONObject cashAd = GlobalFunctions.get_record( $qs );

            if( cashAd.has("id") && ! cashAd.getString("id").isEmpty() ) {

                $qs = new JSONObject();
                $qs.put("id", cashAd.getString("id") );
                $qs.put("fields", $qf2);
                JSONObject $qr = getTripDetails( $qs );
                JSONObject $qt;

                if ($qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) {
                    $qt = $qr.getJSONArray("row").getJSONObject(0);
                    if ($qt.has("amount") && ! $qt.getString("amount").isEmpty() ) {
                        amount = Double.valueOf($qt.getString("amount"));
                    }
                    if( $qt.has("amount_spent") && ! $qt.getString("amount_spent").isEmpty() ) {
                        amountSpent = Double.valueOf($qt.getString("amount_spent"));
                    }
                }

                JSONObject jdata = new JSONObject();
                JSONObject postData = new JSONObject();
                if (amount > 0) {
                    //update amount, reference number
                    String refNum = "";

                    //generate reference num & update
                    //STAFFID/CA/ST/SBU/YY/MM/SN
                    postData.put(dep.getJSONObject("fields").getString("amount"), String.valueOf(amount));
                    postData.put(dep.getJSONObject("fields").getString("amount_spent"), String.valueOf(amountSpent));
                    postData.put(dep.getJSONObject("fields").getString("amount_approved"), String.valueOf(amount));
                    if( amountSpent > 0 ){
                        postData.put(dep.getJSONObject("fields").getString("amount_approved"), String.valueOf(amountSpent));
                        postData.put(dep.getJSONObject("fields").getString("amount_reimbursed"), "0");
                        postData.put(dep.getJSONObject("fields").getString("amount_refund"), "0");
                        if( amountSpent > amount ){
                            postData.put(dep.getJSONObject("fields").getString("amount_reimbursed"), String.valueOf(amountSpent - amount));
                            postData.put(dep.getJSONObject("fields").getString("type"), "reimbursement");
                        }else{
                            postData.put(dep.getJSONObject("fields").getString("type"), "refund");
                            postData.put(dep.getJSONObject("fields").getString("amount_refund"), String.valueOf(amount - amountSpent ));
                        }
                        cashAd.put("type", postData.getString( dep.getJSONObject("fields").getString("type") ) );
                    }

                    refNum = getCARefNum( cashAd );

                    postData.put(dep.getJSONObject("fields").getString("name"), refNum );

                    //although this should happen / be controlled by the workflow
                    postData.put(dep.getJSONObject("fields").getString("status"), "submitted" );
                    postData.put(dep.getJSONObject("fields").getString("date"), GlobalFunctions.get_current_time(0) );

                    jdata.put("table", nwpCashAdvance.table_name );
                    jdata.put("id", jpet.getString("id") );
                    jdata.put("todo", "edit");
                    jdata.put("post_data", postData);

                    JSONObject rd = nwpDataTable.saveDataForm( jdata );
                    if( rd.has( "saved_record" ) && rd.getJSONObject( "saved_record" ).has( "id" ) ){
                        rd.put("action", "submit_for_approval");
                        rd.put("html_replacement_selector", ( jpet.has( "html_replacement_selector" ) ? jpet.getString( "html_replacement_selector" ) : "" ) );
                        if( jpet.has("show_workflow_button") ){
                            rd.put("show_workflow_button", true);
                        }
                        if( jpet.has("workflow") ){
                            rd.put("workflow", jpet.getString("workflow"));
                        }
                        if( isValidateTripDetails ){
                            rd.put("isValidateTripDetails", isValidateTripDetails );
                        }

                        h1 = nwpCashAdvance.getDetailsView( rd );
                    }else if( rd.has("error") ){
                        error = rd.getString("error");
                    }else{
                        error = "<h4>Unknown Error</h4>Unable to reviewComputation.";
                    }

                } else {
                    error = "<h4>Invalid Amount Due</h4>Unable to reviewComputation.";
                }

                if( ! error.isEmpty() ){
                    //update amount due to zero on error
                    jdata.put("table", nwpCashAdvance.table_name );
                    jdata.put("id", jpet.getString("id") );
                    jdata.put("todo", "edit");

                    postData = new JSONObject();
                    postData.put(dep.getJSONObject("fields").getString("amount"), "0");
                    postData.put(dep.getJSONObject("fields").getString("amount_spent"), "0");
                    postData.put(dep.getJSONObject("fields").getString("amount_approved"), "0");
                    postData.put(dep.getJSONObject("fields").getString("amount_approved"), "0");
                    postData.put(dep.getJSONObject("fields").getString("amount_reimbursed"), "0");
                    postData.put(dep.getJSONObject("fields").getString("amount_refund"), "0");

                    jdata.put("post_data", postData);

                    JSONObject rd = nwpDataTable.saveDataForm( jdata );
                }
            }else{
                error = "<h4>Unable to Retrieve Cash Advance Record</h4>Unable to reviewComputation.";
            }
        }else{
            error = "Unable to reviewComputation. Invalid CONFIG " + nwpCashAdvance.table_name_trip + ", " + nwpCashAdvance.table_name;
        }

        if( ! error.isEmpty() ){
            r.put("error", error);
        }
        r.put("html", h1);

        return r;
    }
    public static String getCARefNum( JSONObject cashAd ){
        String refNum = "";

        String userName = ( GlobalFunctions.app_user_data.has("username") )?GlobalFunctions.app_user_data.getString("username"):"";
        String[] un = userName.split("@");
        if( un.length > 0 ){
            userName = un[0];
        }
        refNum += userName.toUpperCase() + "/CA/";
        JSONObject caCode = GlobalFunctions.selectBoxOptions( "ecm_cash_advance_types_code" );
        if( cashAd.has("type") && caCode.has( cashAd.getString("type") ) ){
            refNum += caCode.getString( cashAd.getString("type") );
        }
        if( GlobalFunctions.app_user_data.has("department_sbu") ){
            refNum += "/" + GlobalFunctions.app_user_data.getString("department_sbu");
        }
        refNum += "/" + GlobalFunctions.get_current_time(2);
        if( cashAd.has("serial_num") ){
            refNum += "/" + cashAd.getString("serial_num");
        }
        return refNum;
    }

    public static JSONObject submitForApproval( JSONObject jpet ){
        //9-jun-2023 No longer in use, i passed it through the workflow submission button
        String h1 = "";
        String error = "";
        JSONObject r = new JSONObject();

        //test for trip details...
        Double amount = 0.00;
        if ( jpet.has("id")) {

            JSONObject $qs = new JSONObject();

            $qs.put("id", jpet.getString("id") );
            $qs.put("table", nwpCashAdvance.table_name);
            JSONObject cashAd = GlobalFunctions.get_record( $qs );

            if( cashAd.has("id") && ! cashAd.getString("id").isEmpty() ) {

                if (cashAd.has("amount") && ! cashAd.getString("amount").isEmpty() ) {
                   amount = Double.valueOf(cashAd.getString("amount"));
               }

                if (amount > 0) {

                    //move workflow
                    JSONObject a = new JSONObject();
                    a.put("update_ref", cashAd.getString("id") );
                    a.put("update_ref_table", nwpCashAdvance.table_name );

                    JSONObject update_fields = new JSONObject();
                    update_fields.put( "status", "dept_admin" );
                    //update_fields.put( "name", cashAd.getString("name") + "<br>" + GlobalFunctions.getValue( cashAd.getString("amount"), new JSONObject().put("form_field", "decimal"), new JSONObject() ) );
                    update_fields.put( "name", cashAd.getString("name") );

                    a.put("uf_fields", update_fields );
                    a.put("type", "submitted");
                    JSONObject $r = nwpEcm2.directUpdateWorkflow( a );

                    if( $r.has("saved_record") && $r.getJSONObject("saved_record").has("id") ){
                        h1 = "<div class='note note-success'><h4>Successfully Submitted for Approval</h4></div>";

                       /* if( $r.has("html") ){
                            h1 += $r.getString("html");
                        }*/
                    }else{
                        if ($r.has("error")) {
                            error = $r.getString("error");
                        }else{
                            error = "Unable to save workflow record";
                        }
                    }

                } else {
                    error = "<h4>Invalid Amount Due</h4>Unable to submitForApproval.";
                }
            }else{
                error = "<h4>Unable to Retrieve Cash Advance Record</h4>Unable to submitForApproval.";
            }
        }else{
            error = "Unable to submitForApproval. Invalid CA Reference";
        }


        //workflow dept access condition
        if( ! error.isEmpty() ){
            r.put("error", error);
        }
        r.put("html", h1);

        return r;
    }

    public static String getSingleDetailsView( JSONObject jget ){
        String $r = "";
        JSONObject $vd = new JSONObject();
        String $table = nwpCashAdvance.table_name;

        if( jget.has( "id" ) && ! jget.getString( "id" ).isEmpty() ){

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
                String $value;


                $key = "type";
                String ca_type = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                String v1 = "";
                if( ! ca_type.isEmpty() && $fields.has($key ) && $labels.has( $fields.getString($key ) ) ){
                    v1 = GlobalFunctions.getValue( ca_type, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ;
                }

                if( ! ( jget.has("hide_status") && jget.getBoolean("hide_status") ) ) {
                    $key = "status";
                    $value = ($item.has($key)) ? $item.getString($key) : "";
                    if (!$value.isEmpty()) {
                        String flag = "warning";
                        switch ($value) {
                            case "approved":
                                flag = "success";
                                break;
                            case "declined":
                                flag = "danger";
                                break;
                            case "cancelled":
                                flag = "default";
                                break;
                            case "submitted":
                                flag = "info";
                                break;
                        }
                        v1 += "<span class='pull-right label label-" + flag + "'>" + GlobalFunctions.getValue($value, $labels.getJSONObject($fields.getString($key)), new JSONObject()) + "</span>";
                    }
                }
                $rf.put( new JSONObject().put( "label", "Type" ).put( "value", v1 ) );


                $key = "previous_reference_number";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                    $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                }

                $key = "amount";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                    $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                }
                switch( ca_type ){
                    case "single_trip":
                    case "multiple_trip":
                    case "admin_cheque":
                        break;
                    case "retire":
                    case "refund":
                    case "reimbursement":
                        $key = "amount_spent";
                        $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                        if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                            $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                        }

                        $key = "amount_refund";
                        $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                        if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                            if( Double.valueOf( $value ) > 0 ) {
                                $rf.put(new JSONObject().put("label", $labels.getJSONObject($fields.getString($key)).getString("field_label")).put("value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ));
                            }
                        }

                        $key = "amount_reimbursed";
                        $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                        if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                            if( Double.valueOf( $value ) > 0 ) {
                                $rf.put(new JSONObject().put("label", $labels.getJSONObject($fields.getString($key)).getString("field_label")).put("value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ));
                            }
                        }

                        break;
                }

                if( $item.has( "status" ) ){
                    switch(  $item.getString( "status" ) ) {
                        case "approved":
                            $key = "amount_approved";
                            $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                            if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                                $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                            }
                        case "declined":
                        case "cancelled":
                            $end_date = Long.valueOf($item.getString("date_approved"));
                            $has_ended = true;
                            break;
                    }
                }

                $key = "reason";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                    $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                }

                if( $has_ended ){
                    $rf.put( new JSONObject().put( "label", "Completion Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( String.valueOf($end_date), "date-5time", 0 ) ) );
                }

                $key = "date";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Start Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                }

                $key = "comment";
                $value = ( $item.has( $key ) ) ? $item.getString( $key ) : "";
                if( !$value.isEmpty() && $labels.has( $fields.getString($key ) ) ){
                    $rf.put( new JSONObject().put( "label", $labels.getJSONObject( $fields.getString($key ) ).getString("field_label") ).put( "value", GlobalFunctions.getValue( $value, $labels.getJSONObject( $fields.getString($key ) ), new JSONObject() ) ) );
                }

                String modifier = "";
                if( $item.has("modified_by") ){
                    $fieldDetails = new JSONObject();
                    $fieldDetails.put("label", "Last Modified");
                    modifier = $item.getString("modified_by");
                    String sysValue = GlobalFunctions.get_record_name( new JSONObject().put("id", $item.getString("modified_by") ).put("table", "users" ) );
                    if( $item.has("modification_date") ){
                        sysValue += " " + GlobalFunctions.convert_timestamp_to_date( $item.getString("modification_date"), "date-5time", 0 );
                    }
                    $fieldDetails.put("value", sysValue );
                    $rf.put($fieldDetails);
                }

                if( $item.has("created_by") && ! modifier.equals( $item.getString("created_by") ) ){
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
                    $vd.put( "title0",  $item.getString( "serial_num" ) + " - " + $item.getString( "name" ) );
                    /*$vd.put( "title2", "#" + $item.getString( "serial_num" ) + " - " + $item.getString( "name" ) );
                    $vd.put( "title2_class", "card-shadow-success border border-success bg-success text-white" );*/
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
                $r = $table + "->getSingleDetailsView: Unable to Retrieve Record";
            }
        }else{
            $r = $table + "->getSingleDetailsView: Invalid Reference ID";
        }


        return $r;
    }

    public static JSONObject workflowCallback(JSONObject d){
        JSONObject r = new JSONObject();
        String status = d.has("status")?d.getString("status"):"";
        String action_to_perform = d.has("action_to_perform")?d.getString("action_to_perform"):"";
        String error = "";
        String info = "";
        String next_status = status;    //can be changed based on logic of code

        JSONObject allowed = new JSONObject()
                .put("dept_admin_submit", true)
                .put("dept_admin_submit_comment", true)
                //.put("dept_admin_return", true)
                .put("complete_submit", true)
                .put("declined_submit", true);

        if( allowed.has( status + "_" + action_to_perform ) ) {
            try {
                Boolean isSubmitting = true;
                switch ( action_to_perform ){
                    case "submit_comment":
                    case "return_comment":
                        isSubmitting = false;
                        break;
                }
                //System.out.println(action_to_perform);

                JSONObject ws = d.has("workflow_settings_data") ? d.getJSONObject("workflow_settings_data") : new JSONObject();
                JSONObject wcb = ws.has("callback") ? ws.getJSONObject("callback") : new JSONObject();

                JSONObject wd = d.has("workflow_data") ? d.getJSONObject("workflow_data") : new JSONObject();
                String id = wd.has("reference") ? wd.getString("reference") : "";

                if (!id.isEmpty()) {
                /*System.out.println("xxx");
                System.out.println(d);
                System.out.println("xxx");*/

                    JSONObject $qs2 = new JSONObject();
                    $qs2.put("id", id);
                    $qs2.put("table", nwpCashAdvance.table_name);
                    JSONObject e = GlobalFunctions.get_record($qs2);

                    if (e.has("id")) {

                        switch (status) {
                            case "dept_admin":
                                if (wcb.has("approval_limit") && wcb.getJSONObject("approval_limit").has("limits")) {
                                    if (wcb.getJSONObject("approval_limit").has("field")) {
                                        if (e.has(wcb.getJSONObject("approval_limit").getString("field"))) {
                                        /*System.out.println("xxx");
                                        System.out.println(e);
                                        System.out.println(wcb.getJSONObject("approval_limit"));
                                        System.out.println("xxx");*/
                                            Double amt = 0.00;

                                            if (!e.getString(wcb.getJSONObject("approval_limit").getString("field")).isEmpty()) {
                                                amt = Double.valueOf(e.getString(wcb.getJSONObject("approval_limit").getString("field")));
                                            }
                                            //System.out.println(amt);

                                            if (wcb.getJSONObject("approval_limit").getJSONArray("limits").length() > 0) {
                                                JSONArray jj = wcb.getJSONObject("approval_limit").getJSONArray("limits");
                                                //System.out.println( jj );
                                                for (Object j : jj) {
                                                    JSONObject jv = new JSONObject(j.toString());
                                                    if (jv.has("status") && jv.has("min") && jv.has("max")) {
                                                        Double min = Double.valueOf(jv.get("min").toString());
                                                        Double max = Double.valueOf(jv.get("max").toString());

                                                        if (amt >= min && amt <= max) {
                                                            next_status = jv.getString("status");
                                                            break;
                                                        } else if (amt >= min) {
                                                            next_status = jv.getString("status");
                                                        }
                                                    }
                                                    //System.out.println( jv );
                                                }
                                            }
                                        } else {
                                            info = "No Comparision Value for the Approval Limit";
                                        }
                                    } else {
                                        info = "No Approval Field is Defined in the Workflow Settings Callback";
                                    }
                                } else {
                                    info = "No Approval Limit is Defined in the Workflow Settings Callback";
                                }

                                //prevent status from being switched when not submitting the workflow
                                if( ! isSubmitting ){
                                    if( info.isEmpty() ) {
                                        JSONObject all_status = d.has("all_status") ? d.getJSONObject("all_status") : new JSONObject();
                                        info = "This Submission will go to the <b>";
                                        if (all_status.has(next_status)) {
                                            info += all_status.getString(next_status);
                                        }else{
                                            info += nwpWorkflow.__nwp_workflow_status_format( next_status );
                                        }
                                        info += "</b> based on the approval limit settings";
                                    }
                                    next_status = "";

                                    JSONObject $qs = new JSONObject();
                                    $qs.put("id", e.getString("id") );
                                    JSONObject $qr = getTripDetails( $qs );

                                    if ( ( $qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) ) {
                                        JSONObject $qt = $qr.getJSONArray("row").getJSONObject(0);
                                        if ($qt.has("amount") && ! $qt.getString("amount").isEmpty() ) {

                                        }else{
                                            error = "<h4>No Trip Details Found</h4><p>Trip details is not specified for the Cash Advance</p>";
                                        }
                                    }else{
                                        error = "<h4>No Trip Details Found</h4><p>Trip details is not specified for the Cash Advance</p>";
                                    }

                                   /* if ( ! ( e.has( "amount" ) && ! e.getString( "amount" ).isEmpty() && Double.valueOf( e.getString( "amount" ) ) > 0 ) ){
                                        error = "<h4>No Trip Details Found</h4><p>Trip details is not specified for the Cash Advance</p>";
                                    }*/
                                }
                                break;
                            case "complete":
                                //submit to erp
                                JSONObject rd = nwpConnectToAPI.ecm_cash_advance_submit_to_erp( new JSONObject()
                                        .put("cash_advance", e )
                                        .put("workflow", wd )
                                        .put("source", "nwpCashAdvance.workflowCallback" )
                                        .put("user_data", GlobalFunctions.app_user_data )
                                );
                                if( rd.has("error") ){
                                    error = rd.getString("error");
                                }
                                if( rd.has("info") ){
                                    info = rd.getString("info");
                                }
                                //trigger email notification: move to general workflow
                                break;
                            case "reject":
                                //trigger email notification: move to general workflow
                                break;
                        }

                    } else {
                        error = "<h4>Unable to Retrieve Approved Cash Advance</h4><p>Reference #" + $qs2.getString("id") + "</p>";
                    }

                } else {
                    error = "Undefined Cash Advance Entry";
                }
            } catch (Exception e) {
                error = e.getMessage();

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", error )
                                .put("input", d )
                                .put("function", "nwpCashAdvance.workflowCallback" )
                                .put("exception", true ), e
                );
            }
        }
        //System.out.println( "xxx" );
        //System.out.println( next_status );

        if( ! next_status.isEmpty() ){
            r.put("next_status", next_status);
        }

        if( ! error.isEmpty() ){
            r.put("error", error);
        }

        if( ! info.isEmpty() ){
            r.put("info", info);
        }

        return r;
    }

}
