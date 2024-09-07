package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class nwpWorkflow{

    public static String workflow_settings_table_name = "workflow_settings";
    public static String workflow_trail_table_name = "workflow_trail";
    public static String workflow_items_table_name = "workflow_items";
    public static String table_name = "workflow";
    public static String label = "Workflow";
    public static String salter = "cm$fdk-3203c$#cpdimpssy";

    public static void main(String[] args) {
        System.out.println ( getUsersWithAccessToWorklowStatus( new JSONObject()
                .put("workflow", "1wow1663928149526" )
                .put("workflowSettings", "ws27047092327" )
                .put("status", "head_lit" )
        ) );
    }

    public static JSONObject add_to_workflow( JSONObject a ){
        String error = "";
        JSONObject r = new JSONObject();
        String $table = getTableName();
        JSONObject dep = GlobalFunctions.get_json( $table );
        JSONObject jpet = new JSONObject();
        JSONObject fields = dep.has( "fields" ) ? dep.getJSONObject( "fields" ) : new JSONObject();

        if( a.has( "workflow_settings" ) && a.has( "reference" ) && a.has( "reference_table" ) ){
            if( fields.has( "workflow_settings" ) ){
                JSONObject $qs = new JSONObject();
                $qs.put( "id", a.getString( "workflow_settings" ) );
                $qs.put( "table", nwpWorkflow.workflow_settings_table_name );

                JSONObject $work_settings_data = GlobalFunctions.get_record( $qs );
                JSONObject $ws_data = $work_settings_data.has( "data" ) ? new JSONObject( GlobalFunctions.urldecode( $work_settings_data.getString( "data" ) ) ) : new JSONObject();
                String $start_type = $ws_data.has( "start_type" ) && !$ws_data.getString( "start_type" ).isEmpty() ? $ws_data.getString( "start_type" ) : "record";

                String $start_status = $ws_data.has( "start_status" ) && !$ws_data.getString( "start_status" ).isEmpty() ? $ws_data.getString( "start_status" ) : "start";

                $qs = new JSONObject().put( "type", $start_type ).put( "return_status_data", 1 ).put( "current_state", $start_status ).put( "table", $table );
                JSONObject $ws_status_data = nwpWorkflow.get_workflow_status2( $work_settings_data, $qs );

                String $ws_status = $start_status;
                if( $ws_status_data.has( "key" ) && !$ws_status_data.getString( "key" ).isEmpty() ){
                    $ws_status = $ws_status_data.getString( "key" );
                }

                if( error.isEmpty() && !( $work_settings_data.has( "id" ) && !$work_settings_data.getString( "id" ).isEmpty() ) ){
                    error = "<h4><strong>Invalid Workflow Settings</strong></h4><p>Please try again</p>";
                }

                if( error.isEmpty() && $ws_status.isEmpty() ){
                    error = "<h4><strong>Invalid Workflow Settings</strong></h4><p>The selected settings does not tally with this " + ( $table ) + "</p>";
                }

                /*if( isset( $ws_status_data["expire_after"] ) && doubleval( $ws_status_data["expire_after"] ) ){
                    $line_items["expiry_date"] = date("U") + doubleval( $ws_status_data["expire_after"] );
                }else{
                    $line_items["expiry_date"] = 0;
                }

                if( isset( $ws_status_data["remind_after"] ) && doubleval( $ws_status_data["remind_after"] ) ){
                    $line_items["next_reminder_date"] = date("U") + doubleval( $ws_status_data["remind_after"] );
                }else{
                    $line_items["next_reminder_date"] = 0;
                }*/

                if( error.isEmpty() ){
                    //jpet.put( fields.getString("date") , a.getLong("date") );
                    jpet.put( fields.getString( "date" ), a.getString( "date" ) );
                    jpet.put( fields.getString( "workflow_settings" ), a.getString( "workflow_settings" ) );
                    jpet.put( fields.getString( "reference" ), a.getString( "reference" ) );
                    jpet.put( fields.getString( "reference_table" ), a.getString( "reference_table" ) );
                    jpet.put( fields.getString( "type" ), $start_type );
                    jpet.put( fields.getString( "table" ), a.getString( "reference_table" ) );
                    jpet.put( fields.getString( "description" ), a.getString( "description" ) );
                    jpet.put( fields.getString( "comment" ), a.getString( "comment" ) );
                    jpet.put( fields.getString( "name" ), a.getString( "name" ) );
                    jpet.put( fields.getString( "expiry_date" ), "" );
                    jpet.put( fields.getString( "end_date" ), "" );
                    jpet.put( fields.getString( "current_user" ), GlobalFunctions.app_user );
                    jpet.put( fields.getString( "current_department" ), GlobalFunctions.app_user_dept );
                    jpet.put( fields.getString( "last_reminder" ), "" );
                    jpet.put( fields.getString( "status" ), $ws_status );

                    if( a.has( "group" ) ) {
                        jpet.put(fields.getString("state"), a.getString("group")); //group
                    }

                    if( a.has( "team" ) ) {
                        jpet.put(fields.getString("lga"), a.getString("team")); //team
                    }

                    if( a.has( "priority" ) ) {
                        jpet.put(fields.getString("priority"), a.getString("priority")); //priority_level
                    }

                    //System.out.println(jpet);
                    a.put( "post_data", jpet );

                    a.put( "todo", "create_new_record" );
                    a.put( "table", getTableName() );
                    r = nwpDataTable.saveDataForm( a );
                    if( r.has( "error" ) ){
                        error = r.getString( "error" );
                    }else{
                        r.put( "saved_record", jpet );
                        r.put( "workflow_fields", fields );
                    }
                }
            }else{
                error = "Incorrect Table JSON Fields";
            }
        }else{
            error = "Invalid Workflow Settings or Reference Params";
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
        return nwpWorkflow.table_name;
    }

    public static JSONObject WorkflowGetDataApprovalDashboard( String a ){
        JSONObject r = new JSONObject();
        JSONObject $items = new JSONObject();

        //get workflows count group by settings
        JSONObject $qs = new JSONObject();
        JSONObject $qr2 = new JSONObject();
        JSONObject $sf = new JSONObject();
        JSONObject dep1 = GlobalFunctions.get_json( nwpWorkflow.table_name );

        if( dep1.has( "fields" ) ){
            String $where = "";

            JSONObject $qf = dep1.getJSONObject( "fields" );
            $qs.put( "fields", $qf );
            $qs.put( "select", "SELECT COUNT(*) as 'count', ["+ nwpWorkflow.table_name +"].[" + $qf.getString( "workflow_settings" ) + "] as 'workflow_settings', ["+ nwpWorkflow.table_name +"].[" + $qf.getString( "table" ) + "] as 'table' " );
            $qs.put( "table", nwpWorkflow.table_name );
            $qs.put( "group", "GROUP BY ["+ nwpWorkflow.table_name +"].[" + $qf.getString( "workflow_settings" ) + "], ["+ nwpWorkflow.table_name +"].[" + $qf.getString( "table" ) + "] " );
            $qs.put( "order", "" );

            if( GlobalFunctions.app_user_data.has("role") ){
                JSONObject dep = GlobalFunctions.get_json( nwpAccessRoles.access_role_status );
                if ( dep.has("fields") && dep.getJSONObject("fields").has("reference") && dep.getJSONObject("fields").has("reference_table") && dep.getJSONObject("fields").has("access_role_id") ) {
                    $qs.put("join", " JOIN " + nwpAccessRoles.access_role_status + " ON [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("status")+"] = ["+nwpWorkflow.table_name+"].[" + $qf.getString( "status" ) + "] AND [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("reference")+"] = ["+nwpWorkflow.table_name+"].[" + $qf.getString( "workflow_settings" ) + "] AND [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("reference_table")+"] = '"+nwpWorkflow.workflow_settings_table_name+"' AND [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("access_role_id")+"] = '"+GlobalFunctions.app_user_data.getString("role")+"'  AND [" + nwpAccessRoles.access_role_status + "].[record_status] = '1' ");
                }

                if( GlobalFunctions.app_user_data.has("id") ){
                    if( GlobalFunctions.app_data.has("view_my_jobs")
                            && GlobalFunctions.app_data.getJSONObject("view_my_jobs").length() > 0
                    ) {
                        for( Object ws : GlobalFunctions.app_data.getJSONObject("view_my_jobs").names() ){
                            String wsk = ws.toString();
                            if( GlobalFunctions.app_data.getJSONObject("view_my_jobs").has( wsk )
                                    && GlobalFunctions.app_data.getJSONObject("view_my_jobs").getJSONObject( wsk ).has("in")
                                    && ! GlobalFunctions.app_data.getJSONObject("view_my_jobs").getJSONObject( wsk ).getString("in").isEmpty()
                            ){
                                $where += " AND ( ["+ nwpWorkflow.table_name +"].["+ $qf.getString("workflow_settings") +"] != '"+ wsk +"' ";
                                $where += " OR ( ["+ nwpWorkflow.table_name +"].["+ $qf.getString("workflow_settings") +"] = '"+ wsk +"' ";
                                $where += " AND ( ["+ nwpWorkflow.table_name +"].["+ $qf.getString("status") +"] NOT IN ( "+ GlobalFunctions.app_data.getJSONObject("view_my_jobs").getJSONObject( wsk ).getString("in") +" ) ";
                                $where += " OR ( ["+ nwpWorkflow.table_name +"].["+ $qf.getString("status") +"] IN ( "+ GlobalFunctions.app_data.getJSONObject("view_my_jobs").getJSONObject( wsk ).getString("in") +" )  ";
                                $where += " AND ["+ nwpWorkflow.table_name +"].[created_by] = '"+ GlobalFunctions.app_user_data.getString("id") +"' ) ) ";
                                $where += " ) ) ";
                            }
                        }

                        $qs.put( "where", $where );
                    }
                }
            }else{
                $qs.put( "where", " AND ["+ nwpWorkflow.table_name +"].[" + $qf.getString( "status" ) + "] NOT IN ( 'complete', 'cancelled' ) " );
            }

            JSONObject $qr;
            $qr = GlobalFunctions.get_records( $qs );

            if( $qr.has( "row_count" ) && $qr.has( "row" ) && $qr.getInt( "row_count" ) > 0 ){
                JSONObject $qt = new JSONObject();
                //System.out.println( $qr.getJSONArray("row") );

                JSONObject $qrs = nwpWorkflow.getWorkflowSettings( new JSONObject().put("source", "approval_dashboard").put("check_accessX", "1") );
                JSONObject $opt = new JSONObject();
                $opt.put( "return_type", "2" );

                JSONObject $qrs2 = GlobalFunctions.get_list_box_options( "workflow_category", $opt );
                String $key2 = "";
                //System.out.println( $qrs );

                for( int i2 = 0; i2 < $qr.getInt( "row_count" ); i2++ ){

                    $qt = $qr.getJSONArray( "row" ).getJSONObject( i2 );
                    if( $qrs.has( $qt.getString( "workflow_settings" ) ) ){
                        $key2 = $qrs.getJSONObject( $qt.getString( "workflow_settings" ) ).getString( "category" );

                        $qt.put( "title", $qrs.getJSONObject( $qt.getString( "workflow_settings" ) ).getString( "name" ) );
                        $qt.put( "sub_title", $qrs2.has( $key2 ) ? $qrs2.getString( $key2 ) : $key2 );
                        $qt.put( "table", $qt.getString( "table" ) );
                        $qt.put( "no_tree", 1 );
                        $items.put( $qt.getString( "workflow_settings" ), $qt );
                    }
                }


            }else{
                $qr2.put( "items", new JSONArray() );
            }
            $qr2.put( "do_not_reload_table", 1 );
        }

        r.put( "current_tab", "1" );
        r.put( "items", $items );
        r.put( "plugin", "1" );
        r.put( "table", "1" );

        return r;
    }

    public static JSONObject getWorkflowSettings( JSONObject opt ){
        JSONObject ret = new JSONObject();
        JSONObject $qs = new JSONObject();
        $qs.put("table", nwpWorkflow.workflow_settings_table_name);

        String source = opt.has("source") ? opt.getString("source") : "";
        Boolean check_access = ( opt.has("check_access") && ! opt.getString("check_access").isEmpty() ) ? true : false;
        Boolean no_index = ( opt.has("no_index") && ! opt.getString("no_index").isEmpty() ) ? true : false;
        JSONObject $sf = new JSONObject();

        $sf.put("name", "name");
        $sf.put("category", "category");


        switch (source) {
            case "new_job":
            case "basic_crud":
                $sf.put("data", "data");
                break;
        }

        $sf.put("table", "table");
        $qs.put("select_fields", $sf);

        if( ! no_index ) {
            $qs.put("index_field", "id");
        }

        if( check_access && GlobalFunctions.app_user_data.has("role") ){
            JSONObject dep = GlobalFunctions.get_json( nwpAccessRoles.access_role_status );
            if ( dep.has("fields") && dep.getJSONObject("fields").has("reference") && dep.getJSONObject("fields").has("reference_table") && dep.getJSONObject("fields").has("access_role_id") ) {

                switch (source) {
                    case "new_job":
                        $qs.put("select_prefix", " ["+nwpWorkflow.workflow_settings_table_name+"].[id]");
                        break;
                    default:
                        $qs.put("select_prefix", "DISTINCT ["+nwpWorkflow.workflow_settings_table_name+"].[id]");
                        break;
                }

                $qs.put("join", " JOIN " + nwpAccessRoles.access_role_status + " ON [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("reference")+"] = ["+nwpWorkflow.workflow_settings_table_name+"].[id] AND [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("reference_table")+"] = '"+nwpWorkflow.workflow_settings_table_name+"' AND [" + nwpAccessRoles.access_role_status + "].["+dep.getJSONObject("fields").getString("access_role_id")+"] = '"+GlobalFunctions.app_user_data.getString("role")+"'  AND [" + nwpAccessRoles.access_role_status + "].[record_status] = '1' ");
            }
        }
        JSONObject $qr2 = GlobalFunctions.get_records($qs);

        switch (source) {
            case "basic_crud":
                if ($qr2.has("row") && $qr2.getJSONObject("row").length() > 0) {

                    for (int i3 = 0; i3 < $qr2.getJSONObject("row").length(); i3++) {
                        String k1 = $qr2.getJSONObject("row").names().getString(i3);
                        JSONObject v1 = $qr2.getJSONObject("row").getJSONObject(k1);

                        if (v1.has("name") && v1.has("id") && v1.has("data")) {
                            JSONObject wfs = new JSONObject();
                            try {
                                JSONObject $data = new JSONObject(GlobalFunctions.urldecode(v1.getString("data")));

                                switch (source) {
                                    case "new_job":
                                        if ($data.has("new_job_action") && ! $data.getString("new_job_action").isEmpty() ) {
                                            v1.put("new_job_action", $data.getString("new_job_action") );
                                            $qr2.getJSONObject("row").put(k1, v1);
                                        }
                                        break;
                                    case "basic_crud":
                                        if ($data.has("status") && $data.getJSONObject("status").length() > 0) {
                                            for (int i2 = 0; i2 < $data.getJSONObject("status").length(); i2++) {
                                                String k2 = $data.getJSONObject("status").names().getString(i2);
                                                JSONObject v2 = $data.getJSONObject("status").getJSONObject(k2);

                                                if (v2.has("name") && !wfs.has(k2)) {
                                                    wfs.put(k2, v2.getString("name"));
                                                }
                                            }
                                            ret.put(v1.getString("name"), new JSONObject().put("access_role_status:::workflow_settings:::" + v1.getString("id"), wfs));
                                        }
                                        break;
                                }
                            }catch (Exception e){
                                System.out.println(v1.getString("name"));
                                System.out.println(v1.getString("id"));
                                System.out.println( e.getMessage() );
                            }

                        }
                    }
                   // System.out.println(ret);
                }

                break;
            case "new_job":
                JSONArray $qr3 = new JSONArray();
                if ($qr2.has("row") && $qr2.getJSONArray("row").length() > 0) {

                    for (int i3 = 0; i3 < $qr2.getJSONArray("row").length(); i3++) {
                        JSONObject v1 = $qr2.getJSONArray("row").getJSONObject(i3);

                        if (v1.has("name") && v1.has("id") && v1.has("data")) {
                            JSONObject wfs = new JSONObject();
                            try {
                                JSONObject $data = new JSONObject(GlobalFunctions.urldecode(v1.getString("data")));

                                switch (source) {
                                    case "new_job":
                                        if ($data.has("new_job_action") && ! $data.getString("new_job_action").isEmpty() ) {
                                            v1.put("new_job_action", $data.getString("new_job_action") );
                                        }
                                        break;
                                }

                                $qr3.put(v1);
                            }catch (Exception e){
                                System.out.println(v1.getString("name"));
                                System.out.println(v1.getString("id"));
                                System.out.println( e.getMessage() );
                            }

                        }
                    }
                    $qr2.put( "row", $qr3 );
                    // System.out.println(ret);
                }
            case "approval_dashboard":
            case "menu":
            default:
                if( no_index ) {
                    ret = $qr2;
                }else {
                    ret = $qr2.has("row") ? $qr2.getJSONObject("row") : new JSONObject();
                }
                break;
        }
        return ret;
    }

    public static JSONObject getUsersWithAccessToWorklowStatus( JSONObject opt ){
        JSONObject r = new JSONObject();
        JSONObject $items = new JSONObject();

        String action = opt.has("action")?opt.getString("action"):"";   //action_to_perform
        String wid = opt.has("workflow")?opt.getString("workflow"):"";   //workflow_id
        String wstatus = opt.has("status")?opt.getString("status"):"";   //workflow_status
        String wset = opt.has("workflowSettings")?opt.getString("workflowSettings"):""; //workflow settings

        //get workflows count group by settings
        JSONObject $qs = new JSONObject();
        JSONObject $qr2 = new JSONObject();
        JSONObject $sf = new JSONObject();
        JSONObject dep1 = GlobalFunctions.get_json( nwpAccessRoles.users_table );
        JSONObject dep = GlobalFunctions.get_json(nwpAccessRoles.access_role_status);

        if( dep1.has( "fields" ) && dep.has("fields") && dep.getJSONObject("fields").has("reference") && dep.getJSONObject("fields").has("reference_table") ) {
            String $where = "";
            Date currentDate = new Date();
            long today = currentDate.getTime() / 1000;

            JSONObject $qf = dep1.getJSONObject("fields");
            $qs.put("fields", $qf);
            $qs.put("select", "SELECT [" + nwpAccessRoles.users_table + "].[id] as 'id', " +
                    "[" + nwpAccessRoles.users_table + "].[" + $qf.getString("email") + "] as 'email', " +
                    //"[" + nwpAccessRoles.users_table + "].[" + $qf.getString("department") + "] as 'department', " +
                    //"[" + nwpAccessRoles.users_table + "].[" + $qf.getString("grade_level") + "] as 'grade_level', " +
                    "[" + nwpAccessRoles.users_table + "].[" + $qf.getString("name") + "] as 'name' ");
            $qs.put("table", nwpAccessRoles.users_table );

            $qs.put("group", " GROUP BY [" + nwpAccessRoles.users_table + "].[id], " +
                    "[" + nwpAccessRoles.users_table + "].[" + $qf.getString("email") + "], " +
                    "[" + nwpAccessRoles.users_table + "].[" + $qf.getString("name") + "]");
            $qs.put("order", "");

            $qs.put("join", " JOIN " + nwpAccessRoles.access_role_status +
                    " ON ( " +
                    "[" + nwpAccessRoles.access_role_status + "].[" + dep.getJSONObject("fields").getString("access_role_id") + "] " +
                    " = [" + nwpAccessRoles.users_table + "].[" + dep1.getJSONObject("fields").getString("role") + "] " +
                    " OR" +
                    " ( [" + nwpAccessRoles.access_role_status + "].[" + dep.getJSONObject("fields").getString("access_role_id") + "] " +
                    " = [" + nwpAccessRoles.users_table + "].[" + dep1.getJSONObject("fields").getString("prole") + "] AND [" + nwpAccessRoles.users_table + "].[" + dep1.getJSONObject("fields").getString("prole_expiry_date") + "] >= "+ String.valueOf( today ) +"  ) " +
                    " ) " +
                    " AND [" + nwpAccessRoles.access_role_status + "].[" + dep.getJSONObject("fields").getString("status") + "] " +
                    " IN ( '"+ wstatus +"', 'cc_"+ wstatus +"' ) " +
                    "AND [" + nwpAccessRoles.access_role_status + "].[" + dep.getJSONObject("fields").getString("reference") + "] " +
                    "= '"+ wset +"' " +
                    "AND [" + nwpAccessRoles.access_role_status + "].[" + dep.getJSONObject("fields").getString("reference_table") + "] " +
                    "= '" + nwpWorkflow.workflow_settings_table_name + "' " +
                    "AND [" + nwpAccessRoles.access_role_status + "].[record_status] = '1' ");

            r = GlobalFunctions.get_records( $qs );
        }

        return r;
    }
    public static String getDetailsView( JSONObject jget ){
        String $r = "";
        JSONObject $vd = new JSONObject();
        String $table = getTableName();
        if( jget.has( "db_table" ) && ! jget.getString( "db_table" ).isEmpty() ){
            $table = jget.getString( "db_table" );
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

                if( $table.equals( nwpWorkflow.workflow_items_table_name ) ){
                    $rf.put( new JSONObject().put("label", "Status").put("value",  ($item.getString("status").replace("_", " ").toUpperCase()) ));
                    jget.put("input", $rf );
                    $rf = getWorkflowItemsViewFields( jget );
                    jget.put( "hide_buttons", 1 );
                }else {
                    Date currentDate = new Date();
                    Boolean $has_ended = false;
                    String $value;
                    Long $end_date = currentDate.getTime() / 1000;
                    if ($item.has("status") && ($item.getString("status").equals("complete") || $item.getString("status").equals("cancelled"))) {
                        $end_date = Long.valueOf($item.getString("date"));
                        $has_ended = true;
                    }

                    String flag = "";
                    $key = "priority";
                    $value = ($item.has($key)) ? $item.getString($key) : "";
                    if (!$value.isEmpty()) {
                        JSONObject $os = GlobalFunctions.get_list_box_options("ecm_priority_levels", new JSONObject().put("return_type", "2"));
                        if ($os.length() > 0 && $os.has($value)) {
                            switch( $value ){
                                case "2":
                                    flag = "<span class='pull-right label label-default'><small>Priority: "+ $os.getString( $value ) +"</small></span>";
                                    break;
                                case "3":
                                    flag = "<span class='pull-right label label-info'><small>Priority: "+ $os.getString( $value ) +"</small></span>";
                                    break;
                                case "4":
                                    flag = "<span class='pull-right label label-warning'><small>Priority: "+ $os.getString( $value ) +"</small></span>";
                                    break;
                                case "5":
                                case "6":
                                case "7":
                                    flag = "<span class='pull-right label label-danger'><small>Priority: "+ $os.getString( $value ) +"</small></span>";
                                    break;
                            }
                        }
                    }

                    $rf.put( new JSONObject().put("label", "Current State").put("value", "<code>" + ($item.getString("status").replace("_", " ").toUpperCase()) + "</code>" + flag ) );

                    String recText = "";
                    //$key = "previous_user";
                    $key = "current_user";
                    $value = ($item.has($key)) ? $item.getString($key) : "";
                    if (!$value.isEmpty()) {
                        recText = "<small><i>by " + (GlobalFunctions.get_record_name(new JSONObject("{\"id\":\"" + $value + "\", \"table\": \"users\"}"))) + "<br /></i></small>";
                    }

                    $rf.put( new JSONObject().put("label", "Recommendation").put("value", recText + ($item.getString("description").replaceAll("\n", "<br>") ) ) );


                    if (!$has_ended) {
                        $key = "date";
                        $value = ($item.has($key)) ? $item.getString($key) : "0";
                        if (!$value.isEmpty()) {
                            $rf.put( new JSONObject().put("label", "Time in Current State").put("value", GlobalFunctions.get_age(Long.valueOf($value), currentDate.getTime() / 1000, 1, 0)));
                        }
                    }

                    $key = "creation_date";
                    $value = ($item.has($key)) ? $item.getString($key) : "";
                    if (!$value.isEmpty()) {
                        $rf.put( new JSONObject().put("label", "Lifespan").put("value", GlobalFunctions.get_age(Long.valueOf($value), $end_date, 1, 0)));
                    }

                    $key = "created_by";
                    $value = ($item.has($key)) ? $item.getString($key) : "";
                    if (!$value.isEmpty()) {
                        $rf.put( new JSONObject().put("label", "Created By").put("value", GlobalFunctions.get_record_name( new JSONObject().put("id", $value ).put("table", "users" ) ) ));
                    }



                }

                $vd.put( "fields", $rf );

                if( jget.has( "title" ) ){
                    $vd.put( "title1", jget.getString( "title" ) );
                }else{
                    $vd.put( "title2", "#" + $item.getString( "serial_num" ) + " - " + $item.getString( "name" ) );
                    $vd.put( "title2_class", "card-shadow-success border border-success bg-success text-white" );
                }

                if( ! jget.has( "hide_buttons" ) && $data.has("datatable_options") ){
                    JSONObject $btn_data = $data.getJSONObject("datatable_options");
                    if( $btn_data.length() > 0 ){
                        JSONObject more_data = new JSONObject();
                        more_data.put( "additional_params", "&workflow=" + $item.getString( "id" ) );

                        $btn_data.put("hide", "no_split_view");
                        $btn_data.put( "html_replacement_selector", "datatable-split-screen-" + $table );
                        $btn_data.put( "params", "&html_replacement_selector=" + $btn_data.getString( "html_replacement_selector" ) );
                        $btn_data.put( "phtml_replacement_selector", $container2 );
                        $btn_data.put( "selected_record", $item.getString( "reference" ) );
                        $btn_data.put( "table", $item.getString( "reference_table" ) );
                        $btn_data.put( "more_data", more_data );

                        $vd.put( "buttons", nwpDataTable.getButtons( $btn_data ) + "<br /><br />" );
                    }
                }


                $r = GlobalFunctions.view_details( jget, $vd );

                if( jget.has("show_history") ){
                    JSONObject a = new JSONObject();
                    a.put( "get", jget );
                    a.put( "hide_title", true );
                    a.put( "action_to_perform", "workflow_history2" );
                    a.put( "id", $item.getString( "id" ) );

                    JSONObject $dx = _view_details2( a );
                    if( $dx.has( "html" ) && ! $dx.getString( "html" ).isEmpty() ){
                        $r += "<br /><br /><h4>Approval History</h4><br />" + $dx.getString( "html" );
                    }
                }
            }else{
                $r = $table + "->getDetailsView: Unable to Retrieve Record";
            }
        }else{
            $r = $table + "->getDetailsView: Invalid Reference ID";
        }


        return $r;
    }

    public static JSONArray getWorkflowItemsViewFields( JSONObject $d ){
        JSONArray $rf = new JSONArray();
        if( $d.has("input") ){
            $rf = $d.getJSONArray("input");
        }
        if( $d.has("record") && $d.getJSONObject("record").has("data") ){
            try{
                JSONObject $data = new JSONObject( GlobalFunctions.urldecode( $d.getJSONObject("record").getString("data") ) );
                //$rf.put( new JSONObject().put("label", "Data").put("value", $data.toString() ) );

                String $key = "date";
                String $value = ( $d.getJSONObject("record").has( $key ) ) ? $d.getJSONObject("record").getString( $key ) : "";
                if( ! $value.isEmpty() ){
                    $rf.put( new JSONObject().put( "label", "Date" ).put( "value", GlobalFunctions.convert_timestamp_to_date( $value, "date-5time", 0 ) ) );
                }

                JSONObject $val1;
                String $key1;

                if( $data.has("meta") && $data.getJSONObject("meta").length() > 0 ){
                    for(int i2 = 0; i2 < $data.getJSONObject("meta").names().length(); i2++){
                        $key1 = $data.getJSONObject("meta").names().getString(i2);
                        $val1 = $data.getJSONObject("meta").getJSONObject( $key1 );

                        if( $val1.has("value") && $val1.has("field_label") ){
                            $rf.put( new JSONObject().put("label", $val1.getString("field_label") ).put("value", $val1.getString("value").replace("\n", "<br />") ) );
                        }

                    }
                }
            }catch( Exception e ){
                $rf.put( new JSONObject().put("label", "Invalid Data").put("value", $d.getJSONObject("record").getString("data") ) );
            }
        }
        return $rf;
    }

    public static String __nwp_workflow_status_format( String $val ){
        return "<b>" + $val.replace( "_", " " ).toUpperCase() + "</b>";
    }

    public static JSONObject get_workflow_status( JSONObject $data ){
        JSONObject $ws = get_workflow_status2( $data, new JSONObject().put( "all", 1 ) );

        $ws.put( "in-progress", "In-Progress" );
        if( ! $ws.has("complete") ) {
            $ws.put("complete", "Complete");
        }
        $ws.put( "cancel", "Cancelled" );

        return $ws;
    }

    public static JSONObject get_workflow_status2( JSONObject $data, JSONObject $opt ){

        $data = $opt.has( "workflow_data" ) ? $opt.getJSONObject( "workflow_data" ) : $data;
        $opt = $opt.has( "workflow_opt" ) ? $opt.getJSONObject( "workflow_opt" ) : $opt;
        JSONObject $return = new JSONObject();

        String $current_state = "";
        String $key = "";

        JSONObject $status = new JSONObject();
        JSONObject $states = new JSONObject();

        if( !( $data.has( "data" ) && !$data.getString( "data" ).isEmpty() ) )
            return $return;

        $status = new JSONObject( GlobalFunctions.urldecode( $data.getString( "data" ) ) );
        //System.out.println( $status );
        // print_r( $status );exit;

        if( $opt.has( "type" ) ){
            $key = $opt.getString( "type" );
        }

        if( $opt.has( "table" ) ){
            $key += "." + $opt.getString( "table" );
        }
        JSONObject $status_$key = $status.has( $key ) ? $status.getJSONObject( $key ) : new JSONObject();

        if( $opt.has( "get_tasks" ) && !$opt.getString( "get_tasks" ).isEmpty() ){
            JSONObject $status_$get_tasks = $status.has( $opt.getString( "get_tasks" ) ) ? $status.getJSONObject( $opt.getString( "get_tasks" ) ) : new JSONObject();
            if( $opt.has( "all_children" ) && !$opt.getString( "all_children" ).isEmpty() && !$key.isEmpty() && $status_$key.has( $opt.getString( "get_tasks" ) ) ){
                return $status.getJSONObject( $opt.getString( "get_tasks" ) );
            }else if( $opt.has( "current_state" ) && !$opt.getString( "current_state" ).isEmpty() ){
                //$current_state = str_replace( $key . ".", ", $opt["current_state"] );
                $current_state = $opt.getString( "current_state" );

                if( $opt.getString( "get_tasks" ).equals( "1" ) ){
                    $opt.put( "get_tasks", "tasks" );
                }

                // print_r( $status[ $key ][ $opt["get_tasks"] ] );exit;
                if( !$key.isEmpty() && $status_$get_tasks.has( $current_state ) && !$status_$get_tasks.getString( $current_state ).isEmpty() ){
                    $return.put( "value", $status_$get_tasks.getString( $current_state ) );
                    return $return;
                }

            }
            return $states;
        }

        if( $opt.has( "get_actions" ) && !$opt.getString( "get_actions" ).isEmpty() ){
            if( !$key.isEmpty() && $status.has( "actions" ) && $status.getJSONObject( "actions" ).length() > 0 ){
                return $status.getJSONObject( "actions" );
            }
            return $states;
        }

        if( $opt.has( "get_general_actions" ) && !$opt.getString( "get_general_actions" ).isEmpty() ){
            if( !$key.isEmpty() && $status.has( "general_actions" ) && $status.getJSONObject( "general_actions" ).length() > 0 ){
                return $status.getJSONObject( "general_actions" );
            }
            return $return;
        }

        if( $opt.has( "all" ) && $opt.getInt( "all" ) > 0 ){

            if( $status.has( "status" ) && $status.getJSONObject( "status" ).length() > 0 ){

                for( Integer i1 = 0; i1 < $status.getJSONObject( "status" ).names().length(); i1++ ){
                    String $vk = $status.getJSONObject( "status" ).names().getString( i1 );
                    JSONObject $vv = $status.getJSONObject( "status" ).getJSONObject( $vk );

                    $states.put( $vk, ( $vv.has( "name" ) ? $vv.getString( "name" ) : $vk ) );
                }
            }

            return $states;
        }

        if( !$key.isEmpty() && $status.has( "status" ) && $status.getJSONObject( "status" ).length() > 0 ){

            if( $opt.has( "current_state" ) && !$opt.getString( "current_state" ).isEmpty() ){
                String $fkey = "forward";
                if( $opt.has( "backward" ) && $opt.getInt( "backward" ) > 0 ){
                    $fkey = "backward";
                }

                //$opt["current_state"] = str_replace( $key . ".", ", $opt["current_state"] );
                if( $opt.has( "current" ) && $opt.getInt( "current" ) > 0 ){
                    $current_state = $opt.getString( "current_state" );
                }else{
                    JSONObject $fstate = $status.has( $fkey ) ? $status.getJSONObject( $fkey ) : new JSONObject();

                    if( $opt.has( "use_expire" ) && !$opt.getString( "use_expire" ).isEmpty() && $status.getJSONObject( "status" ).getJSONObject( $opt.getString( "current_state" ) ).has( "expire_status" ) && $status.getJSONObject( "status" ).has( $status.getJSONObject( "status" ).getJSONObject( $opt.getString( "current_state" ) ).getString( "expire_status" ) ) ){
                        $current_state = $status.getJSONObject( "status" ).getJSONObject( $opt.getString( "current_state" ) ).getString( "expire_status" );
                    }else{

                        $current_state = "start";
                        if( $fstate.has( $opt.getString( "current_state" ) ) ){
                            $current_state = $fstate.getString( $opt.getString( "current_state" ) );
                        }

                    }
                }

                // print_r( $current_state );exit;

                if( !$current_state.isEmpty() && $status.getJSONObject( "status" ).has( $current_state ) ){
                    //$r = $key . "." . $current_state;
                    JSONObject $r = new JSONObject();
                    $r.put( "value", $current_state );
                    if( $opt.has( "return_status_data" ) && $opt.getInt( "return_status_data" ) > 0 ){
                        // unset( $r );
                        $r = $status.getJSONObject( "status" ).getJSONObject( $current_state );
                        $r.put( "key", $current_state );

                        if( $opt.has( "current_state" ) && !$opt.getString( "current_state" ).isEmpty() && $status.getJSONObject( "status" ).has( $opt.getString( "current_state" ) ) ){
                            JSONObject $st2 = $status.getJSONObject( "status" ).getJSONObject( $opt.getString( "current_state" ) );

                            if( $st2.has( $fkey + "_file" ) && $status.has( "file" ) && $status.getJSONObject( "file" ).has( $st2.getString( $fkey + "_file" ) ) ){
                                $r.put( "file", $status.getJSONObject( "file" ).getJSONObject( $st2.getString( $fkey + "_file" ) ) );
                            }
                            if( $st2.has( $fkey + "_form" ) && !$st2.getString( $fkey + "_form" ).isEmpty() ){
                                $r.put( "form", $st2.getString( $fkey + "_form" ) );
                            }
                            if( $st2.has( $fkey + "_option" ) && $st2.getJSONObject( $fkey + "_option" ).has("status") && $st2.getJSONObject( $fkey + "_option" ).getJSONObject("status").length() > 0 ){
                                $r.put( "option_meta", new JSONObject( $st2.getJSONObject( $fkey + "_option" ).toString() ) );
                                $r.getJSONObject("option_meta").remove("status");

                                $r.put( "option", new JSONObject() );
                                $r.getJSONObject( "option" ).put( $r.getString( "key" ), ( $r.has( "name" ) ? $r.getString( "name" ) : $r.getString( "key" ) ) );

                                for( Integer i1 = 0; i1 < $st2.getJSONObject( $fkey + "_option" ).getJSONObject("status").names().length(); i1++ ){
                                    String $ok = $st2.getJSONObject( $fkey + "_option" ).getJSONObject("status").names().getString( i1 );
                                    // String $ov = $st2.getJSONObject( $fkey + "_option" ).getString( $ok );

                                    if( $status.getJSONObject( "status" ).has( $ok ) && $status.getJSONObject( "status" ).getJSONObject( $ok ).length() > 0 ){
                                        $r.getJSONObject( "option" ).put( $ok, ( $status.getJSONObject( "status" ).getJSONObject( $ok ).has( "name" ) ? $status.getJSONObject( "status" ).getJSONObject( $ok ).getString( "name" ) : $ok ) );
                                    }
                                }
                            }
                        }

                    }else if( $opt.has( "title" ) && $opt.getInt( "title" ) > 0 ){
                        if(  $status.getJSONObject( "status" ).getJSONObject( $current_state ).has("name") ){
                            $r.put( "value", $status.getJSONObject( "status" ).getJSONObject( $current_state ).getString( "name" ) );
                        }else if( $status.getJSONObject( "status" ).getJSONObject( $current_state ).has("title") ){
                            $r.put( "value", $status.getJSONObject( "status" ).getJSONObject( $current_state ).getString( "title" ) );
                        }
                    }

                    if( $status.has( "callback" ) && $status.getJSONObject( "callback" ).has( $current_state ) ){
                        $r.put( "callback", $status.getJSONObject( "callback" ).getJSONObject( $current_state ) );
                    }


                    return $r;
                }else{
                    return new JSONObject().put( "value", $current_state );
                }
            }

            for( Integer i1 = 0; i1 < $status.getJSONObject( "status" ).names().length(); i1++ ){
                String $vk = $status.getJSONObject( "status" ).names().getString( i1 );
                JSONObject $vv = $status.getJSONObject( "status" ).getJSONObject( $vk );

                $states.put( $key + "." + $vk, $vv.getString( "name" ) );
            }

            return $states;
        }
        return $return;
    }

    public static Boolean validate_workflow_status( String $workflow, String $status ){
        Boolean $valid = false;

        if( ! $workflow.isEmpty() && ! $status.isEmpty() ) {
            JSONObject $qs = new JSONObject();
            $qs.put("id", $workflow);
            $qs.put("table", nwpWorkflow.getTableName());
            JSONObject $e = GlobalFunctions.get_record($qs);
            if( $e.has("status") && $status.equals( GlobalFunctions.md5( $e.getString("status") + nwpWorkflow.salter ) ) ){
                $valid = true;
            }

        }

        return $valid;
    }
    public static JSONObject _view_details2( JSONObject pdata ){

        JSONObject $return = new JSONObject();

        JSONObject get = pdata.has( "get" ) ? pdata.getJSONObject( "get" ) : new JSONObject();
        JSONObject post = pdata.has( "post" ) ? pdata.getJSONObject( "post" ) : new JSONObject();

        String user_id = GlobalFunctions.app_user;

        String action_to_perform = pdata.has( "action_to_perform" ) ? pdata.getString( "action_to_perform" ) : "";
        String $workflow = pdata.has( "id" ) ? pdata.getString( "id" ) : "";
        String $container = pdata.has( "container" ) ? pdata.getString( "container" ) : "";

        String $plugin = pdata.has( "plugin" ) ? pdata.getString( "plugin" ) : "";

        Integer $direct_jump = pdata.has( "direct_jump" ) ? pdata.getInt( "direct_jump" ) : 0;

        String modal_container = "";
        String modal_title = "";
        String html_replacement_selector = "";
        ArrayList<String> $js = new ArrayList<String>();

        Integer override_defaults = 0;

        String $error_msg = "";
        String form_submit_button = "Save " + nwpWorkflow.label + " &rarr;";

        // Workflow Data
        JSONObject $qs = new JSONObject();
        $qs.put( "id", $workflow );
        $qs.put( "table", nwpWorkflow.getTableName() );
        JSONObject $e = GlobalFunctions.get_record( $qs );

        JSONObject table_fields = new JSONObject();
        JSONObject ecm_data = GlobalFunctions.get_json( nwpEcm2.table_name );
        if( ecm_data.has( "fields" ) ){
            table_fields = ecm_data.getJSONObject( "fields" );
        }

        JSONObject $wtrail_fields = new JSONObject();
        JSONObject $wtrail_data = GlobalFunctions.get_json( nwpWorkflow.workflow_trail_table_name );
        if( $wtrail_data.has( "fields" ) ){
            $wtrail_fields = $wtrail_data.getJSONObject( "fields" );
        }

        JSONObject $workflow_fields = new JSONObject();
        JSONObject $workflow_data = GlobalFunctions.get_json( getTableName() );
        if( $workflow_data.has( "fields" ) ){
            $workflow_fields = $workflow_data.getJSONObject( "fields" );
        }

        JSONObject wfs_data = new JSONObject();

        JSONObject dx = new JSONObject();
        JSONObject $es = new JSONObject();
        JSONObject $wsd = new JSONObject();
        String $wd = "";

        String $empty_container = "";
        String $datatable = "";
        String $handle2 = "";

        String $next_state = "";

        // System.out.print( data );

        if( !( $e.has( "id" ) && !$e.getString( "id" ).isEmpty() ) ){
            $error_msg = "<h4><strong>Invalid Workflow Data</strong></h4>";
        }
        String workflow_settings_id = "";

        if( $workflow.isEmpty() ){
            $error_msg = "<h4><strong>Invalid Workflow ID</strong></h4>";
        }else{
            // Get Workflow Settings Data
            workflow_settings_id = $e.getString( "workflow_settings" );

            // Get Workflow Settings Data
            $qs = new JSONObject();
            $qs.put( "id", workflow_settings_id );
            $qs.put( "table", nwpWorkflow.workflow_settings_table_name );
            $es = GlobalFunctions.get_record( $qs );

            $wsd = $es.has( "data" ) ? new JSONObject( GlobalFunctions.urldecode( $es.getString( "data" ) ) ) : new JSONObject();
        }
        // System.out.print( $wsd );

        if( $e.length() == 0 ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Data</strong></h4>";
        }

        if( $es.length() == 0 ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Settings Data</strong></h4>";
        }

        if( !( $wtrail_data.has( "labels" ) && $wtrail_data.getJSONObject( "labels" ).length() > 0 ) ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Trail Labels</strong></h4>";
        }

        if( $wsd.length() == 0 ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Settings Data Items</strong></h4>";
        }

        if( !$error_msg.isEmpty() ){
            return new JSONObject().put( "error", $error_msg );
        }

        switch( action_to_perform ){
        case "workflow_history2":
        case "workflow_history":
            JSONObject $data = new JSONObject();
            JSONObject $item = new JSONObject();

            if( pdata.has( "params" ) && pdata.getJSONObject( "params" ).has( "id" ) ){
                dx.put( "workflow", pdata.getJSONObject( "params" ).getString( "id" ) );
                dx.put( "action", pdata.getJSONObject( "params" ).getString( "action" ) );
                dx.put( "todo", pdata.getJSONObject( "params" ).getString( "todo" ) );
                dx.put( "html_replacement_selector", pdata.getJSONObject( "params" ).getString( "html_replacement_selector" ) );
                dx.put( "empty_container", pdata.getJSONObject( "params" ).getString( "empty_container" ) );
                dx.put( "datatable", pdata.getJSONObject( "params" ).getString( "datatable" ) );

                dx.put( "params2", pdata.getJSONObject( "params" ) );

            }

            //print_r( $this->class_settings[ 'data' ][ 'params2' ] ); exit;
            modal_title = "Add Reason";

            if( pdata.has( "params" ) && pdata.getJSONObject( "params" ).has( "'title'" ) ){
                modal_title = pdata.getJSONObject( "params" ).getString( "'title'" );
            }

            if( pdata.has( "params" ) && pdata.getJSONObject( "params" ).has( "status_report_data" ) ){
                $data.put( "items", pdata.getJSONObject( "params" ).getJSONObject( "status_report_data" ) );
            }else{

                JSONObject $x = $e;
                $qs = new JSONObject();
                // $qs.put( "fields", $workflow_fields );

                $qs.put( "where", " AND [" + $wtrail_fields.getString( "workflow" ) + "] = '" + $e.getString( "id" ) + "' AND [" + $wtrail_fields.getString( "type" ) + "] = 'movement' " );
                // $qs.put( "select", "SELECT * " );

                $qs.put( "table", nwpWorkflow.workflow_trail_table_name );
                JSONObject vc = GlobalFunctions.get_records( $qs );

                if( vc.has( "row_count" ) && vc.has( "row" ) && vc.getInt( "row_count" ) > 0 ){
                    $x.put( "data", vc.getJSONArray( "row" ) );
                }else{
                    $x.put( "data", new JSONArray() );
                }

                // System.out.println( "vc" );
                // System.out.println( vc );

                $item = $x;
            }

            if( pdata.has( "params" ) && pdata.getJSONObject( "params" ).has( "text" ) && !pdata.getJSONObject( "params" ).getString( "text" ).isEmpty() ){
                $data.put( "'success_text'", pdata.getJSONObject( "params" ).getString( "text" ) );
            }

            $data.put( "table", getTableName() );
            $data.put( "fields", $workflow_fields );

            if( $workflow_data.has( "labels" ) ){
                $data.put( "labels", $workflow_data.getJSONObject( "labels" ) );
            }

            // Generating the veiw
            String $html = "";
            Integer $full_width = 0;
            JSONArray $report_data = new JSONArray();

            //System.out.println( $item );

            switch( action_to_perform ){
            case "workflow_history2":
                $full_width = 1;
            case "workflow_history":
                $report_data = $item.getJSONArray( "data" );

                String $col_width = "col-md-offset-3 col-md-6";
                if( $full_width > 0 ){
                    $col_width = "col-md-12";
                }

                $html += "<div class=\"row\">";
                $html += "<div class=\"" + $col_width + "\">";

                break;
            case "show_workflow_report":
                $report_data.put( $item.getJSONObject( "data" ) );
                break;
            }

            $html += "<div class=\"report-table-preview-20\">";
            String $key = "name";

            String $value = $item.has( $key ) ? $item.getString( $key ) : "";
            // String $value = isset( $item.has( $key ) ? __get_value( $item[ $key ], $key ) : "";

            if( ! pdata.has("hide_title") ) {
                $html += "<h4><strong>" + $value + "</strong></h4>";
                $html += "<br />";
            }
            $html += "<table class=\"table table-bordered table-hover\" cellspacing=\"0\">";
            $html += "<tbody>";

            if( $report_data.length() > 0 ){
                JSONObject $rdv_parent = new JSONObject();
                Integer $first = 0;
                Integer $end_date = 0;

                for( int i = 0; i < $report_data.length(); i++ ){
                    JSONObject obj = $report_data.getJSONObject( i );

                    if( $report_data.get( i ).getClass().getSimpleName().equals( "JSONObject" ) ){
                        JSONObject $rdv = new JSONObject();
                        switch( action_to_perform ){
                        case "workflow_history2":
                        case "workflow_history":
                            $rdv_parent = obj;
                            //System.out.println( "peace" );
                            //System.out.println( "peace" );

                            //System.out.println( new JSONObject( obj.getString( "data" ) ) );
                            //$rdv = obj.has( "data" ) ? new JSONObject( obj.getString( "data" ) ) : new JSONObject();

                            $rdv = obj.has( "data" ) ? new JSONObject( obj.getString( "data" ) ) : new JSONObject();
                            //echo "<pre>";print_r($rdv); echo "</pre>";
                            break;
                        default:
                            $rdv = $report_data.getJSONObject( i );
                            break;
                        }

                        String $style = "";
                        if( $rdv.has( "action_to_perform" ) && $rdv.getString( "action_to_perform" ).equals( "return" ) ){
                            $style = " color:red; ";
                        }else{
                            $style = " color:#2ab32a; ";
                        }

                       //System.out.println( "joy" );
                       //System.out.println( "joy" );

                        //System.out.println( $rdv );

                        JSONObject opt = new JSONObject().put( "type", ( $rdv.has( "type" ) ? $rdv.getString( "type" ) : "" ) );
                        opt.put( "table", $rdv.getString( "table" ) );
                        opt.put( "current_state", $rdv.getString( "status" ) );
                        opt.put( "title", 1 );
                        opt.put( "current", 1 );

                        JSONObject rx = get_workflow_status2( $es, opt );

                        $key = "status";
                        $value = ( $rdv.has( $key ) && $es.has( "data" ) && !$es.getString( "data" ).isEmpty() ) ? ( rx.has( "value" ) ? rx.getString( "value" ) : "" ) : "";
                        $html += "<tr>";
                        $html += "<td class=\"col-md-4\" colspan=\"2\" style=\"text-align: center;" + $style + "\"><strong>" + ( $rdv.has( "action_name" ) ? $rdv.getString( "action_name" ): "" ) + "</strong></td>";
                        //$html += "<td class=\"col-md-4\" colspan=\"2\" style=\"text-align: center;" + $style + "\"><strong>" + $value + ( $rdv.has( "action_name" ) ? " [\"" + $rdv.getString( "action_name" ) + "\"]" : "" ) + "</strong></td>";
                        $html += "</tr>";

                        $key = "current_user";
                        $value = $rdv.has( $key ) ? $rdv.getString( $key ) : "";
                        // $value = $rdv.has( $key ) ? get_name_of_referenced_record( array( "id" => $rdv[ $key ], "table" => "users", "link" => 1 ) ) : "";
                        if( !$value.isEmpty() ){

                            $html += "<tr>";
                            //$html += "<td class=\"col-md-4\"><strong>" + ( $first == ( $report_data.length() - 1 ) ? "Initiated By" : "By" ) + "</strong></td>";
                            $html += "<td class=\"col-md-4\"><strong>By</strong></td>";
                            $html += "<td>" + GlobalFunctions.get_record_name( new JSONObject().put("id", $value ).put("table", nwpAccessRoles.users_table ) ) + " <br /><small>("+ GlobalFunctions.convert_timestamp_to_date( $rdv_parent.getString( "date" ), "date-5time", 0 ) +")</small>" + "</td>";
                            $html += "</tr>";
                        }


                        opt = new JSONObject().put( "type", ( $rdv.has( "type" ) ? $rdv.get( "type" ) : "" ) );
                        opt.put( "table", $rdv.get( "table" ) );
                        opt.put( "current_state", $rdv.get( "status" ) );
                        opt.put( "current", 1 );
                        opt.put( "title", 1 );

                        rx = get_workflow_status2( $es, opt );

                        $key = "status";
                        //$value = $rdv.has( $key ) ? $rdv.getString( $key ) : "";
                        $value = $rdv.has( $key ) && ! $es.getString( "data" ).isEmpty() ? ( rx.has( "value" ) ? rx.getString( "value" ) : "" ) : "";
                        if( !$value.isEmpty() ){
                            $html += "<tr>";
                            $html += "<td class=\"col-md-4\"><strong>Previous Status</strong></td>";
                            $html += "<td>" + $value + "</td>";
                            $html += "</tr>";
                        }
                        //System.out.println($rdv);

                        String $lbl = "";
                        /*

                        $key = "next_state";
                        $value = $rdv.has( $key ) && !$es.getString( "data" ).isEmpty() ? ( rx.has( "value" ) ? rx.getString( "value" ) : "" ) : "";
                        $lbl = "Next State";
                        if( !$value.isEmpty() ){
                            $html += "<tr>";
                            $html += "<td class=\"col-md-4\"><strong>" + $lbl + "</strong></td>";
                            $html += "<td>" + $value + "</td>";
                            $html += "</tr>";
                        }
                        */

                        $key = "date";
                        $value = "";
                        if( $rdv_parent.has( $key ) ){
                            $value = GlobalFunctions.get_age( Long.valueOf( $rdv_parent.getString( $key ) ), Long.valueOf( $end_date ), 1, 0 );
                            $end_date = Integer.parseInt( $rdv_parent.getString( $key ) );
                        }

                        if( ! $value.isEmpty() && ! $value.equals("just now") ){
                            $html += "<tr >";
                            $html += "<td class=\"col-md-4\"><strong>Duration in this State</strong></td> <td> " + $value + " </td >";
                            $html += "</tr>";
                        }

                        // Get TASKS
                            /*$key = "tasks";
                            $dd = $rdv.has( $key )?$rdv[ $key ]:array();
                            $h = "";

                            if( is_array( $dd ) && ! empty( $dd ) ){

                                foreach( $dd as $ddv ){
                                    if( isset( $ddv[ "data" ] ) && $ddv[ "data" ] ){
                                        if( ! is_array( $ddv[ "data" ] ) )
                                            $json = json_decode( $ddv[ "data" ], true );
                                        else
                                            $json = $ddv[ "data" ];

                                        $k = "";
                                        switch( $ddv[ "action" ] ){
                                        case "workflow.check_for_duplicate_records":
                                            $k = "duplicates";
                                            break;
                                        case "workflow.check_for_omissions":
                                            $k = "omissions";
                                            break;
                                        }

                                        if( ! empty( $json ) ){
                                            foreach( $json as $jkey => $jval ){

                                                $jval[ $k ] = isset( $jval[ $k ] ) ? $jval[ $k ] : 0;
                                                $tb = ( isset( $tables[ $jkey ] ) ? $tables[ $jkey ] : "" );
                                                $h .= "<tr><td><strong>". ucwords( $k ) . "" ["" . $tb .""]</strong></td><td>". number_format( doubleval( $jval[ $k ] ), 0 ) ." [". $jval[ "action_text"" ] .""]</td></tr>"";
                                            }
                                        }

                                        // echo "<pre>";print_r( $json );echo "</pre>";
                                    }
                                }
                                echo $h;
                            }*/

                        // Get STATS
                            /*$key = "stats";
                            JSONObject  = $rdv.has( $key ) ? $rdv.getJSONObject( $key ) : new JSONObject();
                            $h = "";

                            if( is_array( $json ) && ! empty( $json ) ){

                                foreach( $json as $jval ){

                                    if( isset( $jval["count"] ) && isset( $jval["label"] ) ){
                                        $h .= "<tr><td><strong>". $jval["label"] ."</strong></td><td>". number_format( doubleval( $jval["count"] ), 0 ) ."</td></tr>";
                                    }
                                }
                            }
                            echo $h;*/

                        $key = "comment";
                        $value = $rdv.has( $key ) ? $rdv.getString( $key ) : "";
                        //$lbl = $workflow_data.has( "labels" ) ? $workflow_data.getJSONObject( "labels" ).getJSONObject( $workflow_fields.getString( $key ) ).getString( "display_field_label" ) : "Unknown Label";
                        $html += "<tr>";
                        //$html += "<td class=\"col-md-4\"><strong>" + $lbl + "</strong></td>";
                        $html += "<td class=\"col-md-4\"><strong>Recommendation</strong></td>";
                        $html += "<td>" + $value.replace( "rn", "<br>" ).replace( "\n", "<br>" ) + "</td>";
                        $html += "</tr>";
                        $first++;
                    }
                }

                if( $html.isEmpty() ){
                    $html += "<div class=\"note note-info\"><h4><strong>No History Recorded</strong></h4>Please submit this process to create a trail<div>";
                }
                $html += "</tbody>";

                $html += "</table>";
                $html += "</div>";

                switch( action_to_perform ){
                case "workflow_history2":
                case "workflow_history":
                    $html += "</div>";
                    $html += "</div>";
                    break;
                }
            }
            // System.out.println( $html );

            $return = new JSONObject().put( "html", $html );
            break;
        case "submit":
        case "return":

            String $success_msg = "<h4><strong>Successfully Returned</strong></h4>";
            $html = "";
            String $comment = "";
            String $name = "";

            String $stz = $e.getString( "type" ) + "." + $e.getString( "table" );
            String $next_state_jump = post.has( "nwp_wf_next_state" ) ? post.getString( "nwp_wf_next_state" ) : "";
            JSONObject $all_states = get_workflow_status( $es );
            JSONObject $ws_status_data = new JSONObject();

            if( post.has( "current_status" ) && ! post.getString( "current_status" ).equals( $e.has( "status" ) ) ){
                $error_msg = "<h4>Access Denied</h4><p>Workflow Job is no longer in the <b>" + post.getString( "current_status" ).toUpperCase() + "</b> state</p>";
            }

            switch( action_to_perform ){
            case "submit":
                $success_msg = "<h4><strong>Successfully Submitted</strong></h4>";


                if( $error_msg.isEmpty() ){
                    JSONObject opt = new JSONObject().put( "type", $e.getString( "type" ) );
                    opt.put( "table", $e.getString( "table" ) );
                    opt.put( "current_state", $e.getString( "status" ) );
                    if( ! $next_state_jump.isEmpty() && $all_states.has( $next_state_jump ) ){
                        opt.put( "current", 1 );
                        opt.put( "current_state", $next_state_jump );
                    }
                    opt.put( "return_status_data", 1 );
                    $ws_status_data = get_workflow_status2( $es, opt );

                    /*System.out.println( "jjj" );
                    System.out.println( $ws_status_data );*/
                    //System.out.println( "jjj" );

                    $next_state = $e.getString( "status" );
                    if( $ws_status_data.has( "key" ) && !$ws_status_data.getString( "key" ).isEmpty() ){
                        $next_state = $ws_status_data.getString( "key" );
                    }

                    if( !$next_state_jump.isEmpty() && $all_states.has( $next_state_jump ) && $ws_status_data.has( "option" ) && $ws_status_data.getJSONObject( "option" ).has( $next_state_jump ) ){
                        $next_state = $next_state_jump;
                    }
                    if( $direct_jump > 0 && !$next_state_jump.isEmpty() && $all_states.has( $next_state_jump ) ){
                        $next_state = $next_state_jump;
                    }

                    JSONObject r2 = callBackHandler( new JSONObject()
                            .put("action_to_perform", action_to_perform )
                            .put("workflow_settings_data", $ws_status_data )
                            .put("status",$next_state )
                            .put("workflow_data", $e ) );
                    if( r2.has( "error" ) && ! r2.getString( "error" ).isEmpty() ){
                        $error_msg = r2.getString( "error" );
                    }else{
                        //switch next stage based on callback
                        if( r2.has( "next_status" ) && ! r2.getString( "next_status" ).isEmpty() ) {
                            $next_state = r2.getString("next_status");
                        }
                    }

                    String $dc = "";
                    if( $direct_jump > 0 ){
                        $dc = "Status Changed ";
                        $success_msg = "<h4><strong>Successful Status Change</strong></h4>";
                        $name = "Status Changed to: " + ( $all_states.has( $next_state ) ? $all_states.getString( $next_state ) : $next_state );
                    }else{
                        $success_msg = "<h4><strong>Successfully Approved</strong></h4>";
                        $name = "Approved and sent to: " + ( $all_states.has( $next_state ) ? $all_states.getString( $next_state ) : $next_state );
                    }

                    $comment = $dc + "From " + ( $all_states.has( $e.getString( "status" ) ) ? $all_states.getString( $e.getString( "status" ) ) : $e.getString( "status" ) ) + " to " + ( $all_states.has( $next_state ) ? $all_states.getString( $next_state ) : $next_state );

                    if( $next_state.equals( "complete" ) ){
                        $next_state = "complete";
                        $success_msg = "<h4><strong>Successfully Completed</strong></h4>This workflow has now been completed";
                    }

                }
                break;
            case "return":
                if( $error_msg.isEmpty() ) {
                    JSONObject opt = new JSONObject().put("type", $e.getString("type"));
                    opt.put("table", $e.getString("table"));
                    opt.put("current_state", $e.getString("status"));
                    opt.put("backward", 1);
                    if( ! $next_state_jump.isEmpty() && $all_states.has( $next_state_jump ) ){
                        opt.put( "current", 1 );
                        opt.put( "current_state", $next_state_jump );
                    }
                    opt.put("return_status_data", 1);

                    $ws_status_data = get_workflow_status2($es, opt);
                    $next_state = $e.getString("status");

                    if ($ws_status_data.has("key") && !$ws_status_data.getString("key").isEmpty()) {
                        $next_state = $ws_status_data.getString("key");
                    } else if ($ws_status_data.length() > 0 && $ws_status_data.has("value") && !$ws_status_data.getString("value").isEmpty()) {
                        $next_state = $ws_status_data.getString("value");
                    }

                    if (!$next_state_jump.isEmpty() && $all_states.has($next_state_jump) && $ws_status_data.has("option") && $ws_status_data.getJSONObject("option").has($next_state_jump)) {
                        $next_state = $next_state_jump;
                    }

                    JSONObject r2 = callBackHandler( new JSONObject()
                            .put("action_to_perform", action_to_perform )
                            .put("workflow_settings_data", $ws_status_data )
                            .put("status",$next_state )
                            .put("workflow_data", $e ) );
                    if( r2.has( "error" ) && ! r2.getString( "error" ).isEmpty() ){
                        $error_msg = r2.getString( "error" );
                    }else{
                        //switch next stage based on callback
                        if( r2.has( "next_status" ) && ! r2.getString( "next_status" ).isEmpty() ) {
                            $next_state = r2.getString("next_status");
                        }
                    }

                    if ($next_state.equals("start")) {
                        $success_msg = "<h4><strong>Returned to Origin</strong></h4>This workflow has now been cancelled";

                        if ( ! $plugin.isEmpty() ) {
                            $e.put("plugin", $plugin);
                        }
                        $e.put("return_description", (post.has("nwp_wf_comment") ? post.getString("nwp_wf_comment") : ""));
                        $js.add("$(\"#modal-popup-close\").click");

                        $next_state = "cancelled";
                        $name = "Workflow Cancelled";

                        $comment = "Workflow was returned to the origin";
                    } else {
                        $name = "Returned to: " + ($all_states.has($next_state) ? $all_states.getString($next_state) : $next_state);

                        $comment = "Returned from " + ($all_states.has($e.getString("status")) ? $all_states.getString($e.getString("status")) : $e.getString("status")) + " to " + ($all_states.has($next_state) ? $all_states.getString($next_state) : $next_state);
                    }
                }
                break;
            }

            JSONObject $meta_data = new JSONObject().put("file", new JSONArray() ).put("meta", new JSONObject() );
            String current_time = GlobalFunctions.get_current_time(1);
            String current_time0 = GlobalFunctions.get_current_time(0 );

            //System.out.println( action_to_perform );
            if( $error_msg.isEmpty() ){
                switch( action_to_perform ) {
                    case "submit":
                    case "return":
                        //validate custom fields
                        JSONObject $fs = new JSONObject();
                        JSONObject $post_data = new JSONObject();
                        if( post.has("data") && ! post.getString("data").isEmpty() ) {
                            $post_data = new JSONObject( post.getString("data") );
                        }
                        //System.out.println( $post_data );
                        if ($post_data.has("status") && $post_data.getJSONObject("status").has("file") ) {
                            $fs = $post_data.getJSONObject("status").getJSONObject("file");


                            JSONObject $line = new JSONObject();
                            JSONObject $aline;
                            Boolean $has_value = false;
                            String $key1 = "";



                            if ($fs.has("data") && $fs.getJSONObject("data").length() > 0) {

                                for (int i2 = 0; i2 < $fs.getJSONObject("data").names().length(); i2++) {
                                    $key1 = $fs.getJSONObject("data").names().getString(i2);
                                    $line = $fs.getJSONObject("data").getJSONObject($key1);
                                    $has_value = false;

                                    if ( ! $line.has("form_field") ){
                                        $line.put("form_field", "file");
                                    }

                                    switch ($line.getString("form_field")) {
                                        case "file":

                                            if( post.has( $key1 ) && post.has( $key1 + "_json" ) && ! post.getString( $key1 + "_json" ).isEmpty() ) {

                                                JSONObject $vline = new JSONObject( post.getString( $key1 + "_json" ) );
                                                if( $vline.length() > 0 ) {

                                                    for(int i3 = 0; i3 < $vline.names().length(); i3++){
                                                        $aline = $vline.getJSONObject( $vline.names().getString(i3) );

                                                        if( $aline.has("fullname") && ! $aline.getString("fullname").isEmpty() ) {

                                                            $aline.put("date", current_time );

                                                            //$aline.put("description", "create_file" );
                                                            $aline.put("type", "create_file");
                                                            $aline.put("content", GlobalFunctions.rawurlencode( $aline.toString()) );
                                                            $aline.put("file_url", $aline.getString("fullname"));
                                                            $aline.put("name", ( $line.has("field_label")?$line.getString("field_label"):$key1 ) );

                                                            $meta_data.getJSONArray("file").put( $aline );
                                                            $has_value = true;
                                                        }
                                                    }
                                                }

                                            }
                                            break;
                                        default:
                                            if( post.has( $key1 ) && ! post.getString( $key1 ).isEmpty() ) {
                                                $line.put("value", post.getString( $key1 ) );
                                                $meta_data.getJSONObject("meta").put($key1, $line);
                                                $has_value = true;
                                            }
                                            break;
                                    }


                                    if ( ! $has_value && $line.has("required_field") && $line.getString("required_field").equals("yes") ){
                                        $error_msg = "<h4>Missing "+ ( $line.has("field_label")?$line.getString("field_label"):$key1 ) +"</h4>Please upload a file / enter a value in the form below";
                                    }

                                }
                            }
                        }
                        break;
                }
            }
            //$error_msg = "<h4>Halt 2 below";
            //System.out.println( $meta_data );

            if( $error_msg.isEmpty() ){
                //update workflow
                JSONObject $status_report_data = new JSONObject();

                if( !$next_state.isEmpty() ){
                    String replaceStatus = $next_state;
                    //check if status has a replacement value
                    if ( $ws_status_data.has("status_overwrite") && ! $ws_status_data.getString("status_overwrite").isEmpty() && $all_states.has( $ws_status_data.getString("status_overwrite") ) ) {
                        replaceStatus = $ws_status_data.getString("status_overwrite");
                    }

                    String ts = current_time0;

                    String $e_comment = post.has( "nwp_wf_comment" ) ? post.getString( "nwp_wf_comment" ) : ( post.has( "comment" ) ? post.getString( "comment" ) : "" );

                    JSONObject update_fields = new JSONObject();
                    update_fields.put( $workflow_fields.getString( "date" ), current_time0 ); //date("Y-m-d")
                    update_fields.put( $workflow_fields.getString( "status" ), replaceStatus );
                    update_fields.put( $workflow_fields.getString( "previous_user" ), $e.getString( "current_user" ) );
                    update_fields.put( $workflow_fields.getString( "current_user" ), user_id );
                    // "data" => json_encode( $dd ),
                    update_fields.put( $workflow_fields.getString( "description" ), $e_comment );


                    /*if ($ws_status_data.has("expire_after") && $ws_status_data.getInt("expire_after") > 0) {
                        update_fields.put("expiry_date", "1650481140"); //date("U") + doubleval( $ws_status_data["expire_after"] )
                    } else {
                        update_fields.put("expiry_date", "0");
                    }

                    if ($ws_status_data.has("remind_after") && $ws_status_data.getInt("remind_after") > 0) {
                        update_fields.put("next_reminder_date", "1650481140"); //date("U") + doubleval( $ws_status_data["remind_after"] )
                    } else {
                        update_fields.put("next_reminder_date", "0");
                    }*/

                    if( replaceStatus.equals( "complete" ) || replaceStatus.equals( "cancelled" ) ){
                        update_fields.put( $workflow_fields.getString( "end_date" ), current_time0 ); //date("Y-m-d");
                    }

                    JSONObject a = new JSONObject();

                    a.put( "table", getTableName() );
                    a.put( "todo", "edit" ); // create_new_record
                    a.put( "id", $e.getString( "id" ) );
                    a.put( "post_data", update_fields );

                    JSONObject $r = nwpDataTable.saveDataForm( a );
                    // System.out.println( $r );

                    if( $r.has( "saved_record" ) && $r.getJSONObject( "saved_record" ).has( "id" ) && !$r.getJSONObject( "saved_record" ).getString( "id" ).isEmpty() ){
                        // System.out.print( $e );
                        JSONObject savedWorkflow = $r.getJSONObject( "saved_record" );

                         //create trail for comment
                        $e.put( "data", $e.getString( "data" ).isEmpty() ? "" : new JSONObject( $e.getString( "data" ) ) );
                        $e.remove( "data" );
                        JSONObject $dd = new JSONObject( $e.toString() );
                        $dd.put( "previous_user", $e.getString( "current_user" ) );
                        $dd.put( "current_user", user_id );
                        $dd.put( "action_name", $name );
                        $dd.put( "next_state", ( $all_states.has( $next_state ) ? $all_states.getString( $next_state ) : $next_state ) );
                        $dd.put( "action_to_perform", action_to_perform );
                        $dd.put( "comment", $e_comment );

                        JSONObject $stats = new JSONObject();

                        if( $stats.length() > 0 ){
                            $dd.put( "stats", $stats );
                        }


                        $status_report_data.put( "data", new JSONArray().put( $dd ) );
                        // $status_report_data.put( "data", $dd );
                        $status_report_data.put( "id", $e.getString( "id" ) );

                        //create trail for comment
                        JSONObject li = new JSONObject();

                        //create trail
                        //li.put( $wtrail_fields.getString( "date" ), timestamp.toString() ); //date("U")
                        li.put( $wtrail_fields.getString( "date" ), current_time ); //date("U")
                        li.put( $wtrail_fields.getString( "workflow" ), $e.getString( "id" ) );
                        li.put( $wtrail_fields.getString( "type" ), "movement" );
                        li.put( $wtrail_fields.getString( "action" ), $e.getString( "status" ) );
                        li.put( $wtrail_fields.getString( "status" ), $next_state );
                        li.put( $wtrail_fields.getString( "staff_responsible" ), user_id );
                        li.put( $wtrail_fields.getString( "name" ), $comment );
                        li.put( $wtrail_fields.getString( "comment" ), $e_comment );

                        $dd.put( "meta", GlobalFunctions.rawurlencode( $meta_data.toString() ) );
                        li.put( $wtrail_fields.getString( "data" ), $dd.toString() );

                        a = new JSONObject();

                        a.put( "table", nwpWorkflow.workflow_trail_table_name );
                        a.put( "todo", "create_new_record" ); // create_new_record
                        a.put( "post_data", li );

                        JSONObject $rr = nwpDataTable.saveDataForm( a );

                        if( $rr.has( "error" ) && !$rr.getString( "error" ).isEmpty() ){
                            $error_msg = $rr.getString( "error" );

                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", "saving_into_"+ nwpWorkflow.workflow_trail_table_name +": "+ $error_msg )
                                            .put("input", new JSONObject().put("a", a).put("rr", $rr) )
                                            .put("function", "nwpWorkflow.getViewName" )
                                            .put("fatal", true ) , null
                            );

                            //consider reversing operation in the future, might not be necessary if the logs are consumed and acted upon
                        }

                        //check for custom status update
                        if ( $ws_status_data.has("status_update") && $ws_status_data.getJSONObject("status_update").length() > 0 ) {
                            nwpWorkflow.updateRecordsStatus( $ws_status_data, new JSONObject().put("workflow", $e ) );
                        }else {
                            switch ($next_state) {
                                case "complete":
                                case "cancelled":
                                    String $stat_key = "returned_status_update";
                                    switch ($next_state) {
                                        case "complete":
                                            $stat_key = "completion_status_update";
                                            break;
                                    }

                                    if ($wsd.has($stat_key) && !$wsd.getString($stat_key).isEmpty()) {

                                        update_fields = new JSONObject();
                                        update_fields.put($workflow_fields.getString("status"), $wsd.getString($stat_key));
                                        update_fields.put("modified_by", user_id);
                                        update_fields.put("modification_date", current_time); //date("Y-m-d")

                                        a = new JSONObject();

                                        a.put("workflow", $e.getString("id"));
                                        a.put("source", nwpWorkflow.table_name);
                                        a.put("table", $e.getString("table"));
                                        a.put("id", $e.getString("reference"));
                                        a.put("todo", "edit"); // create_new_record
                                        a.put("post_data", update_fields);

                                        JSONObject $r2 = nwpDataTable.saveDataForm(a);
                                        if( $r2.has("error") ){
                                            GlobalFunctions.nw_dev_handler(
                                                    new JSONObject()
                                                            .put("return", "updating_workflow_reference_status: "+ $r2.getString("error") )
                                                            .put("input", new JSONObject().put("a", a).put("r", $r2) )
                                                            .put("function", "nwpWorkflow.getViewName" )
                                                            .put("fatal", true ) , null
                                            );
                                        }
                                    }

                                    break;
                            }
                        }

                        // Save Meta Data
                        $qs = new JSONObject();
                        $qs.put( "id", workflow_settings_id );
                        $qs.put( "table", "workflow_settings" );

                        String $meta_rec = "";
                        if( $meta_data.has( "meta" ) && $meta_data.getJSONObject( "meta" ).length() > 0 ){

                            String wit = nwpWorkflow.workflow_items_table_name;
                            JSONObject wi_fields = new JSONObject();
                            JSONObject $wid = GlobalFunctions.get_json( wit );
                            if( $wid.has( "fields" ) ){
                                wi_fields = $wid.getJSONObject( "fields" );
                            }

                            JSONObject $rdx = new JSONObject();

                            $rdx.put( wi_fields.getString( "date" ), current_time );
                            $rdx.put( wi_fields.getString( "name" ), ( $e.getString( "name" ) ) );
                            $rdx.put( wi_fields.getString( "workflow" ), $e.getString( "id" ) );
                            $rdx.put( wi_fields.getString( "status" ), $e.getString( "status" ) );
                            $rdx.put( wi_fields.getString( "type" ), "meta_data" );
                            $rdx.put( wi_fields.getString( "reference" ), $e.getString( "reference" ) );
                            $rdx.put( wi_fields.getString( "reference_table" ), $e.getString( "table" ) );
                            $rdx.put( wi_fields.getString( "reference_plugin" ), "" );
                            $rdx.put( wi_fields.getString( "data" ), GlobalFunctions.rawurlencode( $meta_data.toString() ) );

                            //System.out.println(jpet);
                            a = new JSONObject();
                            a.put( "post_data", $rdx );

                            a.put( "todo", "create_new_record" );
                            a.put( "table", wit );
                            JSONObject mr = nwpDataTable.saveDataForm( a );
                            if( mr.has("saved_record") && mr.getJSONObject("saved_record").has("id") && ! mr.getJSONObject("saved_record").getString("id").isEmpty() ){
                                $meta_rec = mr.getJSONObject("saved_record").getString("id");
                            }else{
                                GlobalFunctions.nw_dev_handler(
                                        new JSONObject()
                                                .put("return", "saving_into_"+ nwpWorkflow.workflow_items_table_name +": " )
                                                .put("input", new JSONObject().put("a", a).put("mr", mr) )
                                                .put("function", "nwpWorkflow.getViewName" )
                                                .put("fatal", true ) , null
                                );
                            }
                        }

                        //Save Attached Files
                        if( $meta_data.has( "file" ) && $meta_data.getJSONArray( "file" ).length() > 0 ){
                            JSONObject rsfInput = new JSONObject()
                                    .put("line_items", $meta_data.getJSONArray( "file" ) )
                                    .put("meta_data_key", $meta_rec )
                                    .put("workflow", $e.getString( "id" ) )
                                    .put("description", $e_comment )
                                    .put("reference_table", $e.getString("reference_table") )
                                    .put( "reference", $e.getString("reference") );

                            JSONObject rsf = nwpFiles.save_files( rsfInput );
                            if( rsf.has("error") ){
                                GlobalFunctions.nw_dev_handler(
                                        new JSONObject()
                                                .put("return", "saving_into_"+ nwpFiles.table_name +": " + rsf.getString("error") )
                                                .put("input", rsfInput.put("output", rsf) )
                                                .put("function", "nwpWorkflow.getViewName" )
                                                .put("fatal", true ) , null
                                );
                            }
                        }

                        //Send Email Notification
                        if ( ! $ws_status_data.has("no_email_on_exit") ) {
                            sendEmailNotification( savedWorkflow,
                                    new JSONObject()
                                    .put("action", action_to_perform)
                                    .put("allStatus", $all_states )
                                    .put("workflowSettings", $es)
                                    .put("previous_status_data", $ws_status_data)
                                    .put("prev_state", $e.getString("status") )
                                    .put("next_state", $next_state)
                            );
                        }
                    }else{
                        return $r;
                    }
                }

                if( !$html.isEmpty() ){

                }else{
                    if( !$datatable.isEmpty() ){
                        $handle2 = $datatable;
                    }
                    JSONObject $r2 = new JSONObject().put( "type", "success" );
                    $r2.put( "message", $success_msg );
                    $r2.put( "do_not_display", 1 );
                    $r2.put( "html_replacement_selector", "#" + $handle2 );

                    if( !$datatable.isEmpty() ){
                        $return = $r2;
                    }else{
                        JSONObject prms = new JSONObject().put( "status_report_data", $status_report_data );
                        prms.put( "text", $r2.getString( "html_replacement_selector" ) );
                        prms.put( "action_to_perform", "show_workflow_report" );
                        prms.put( "id", $e.getString( "id" ) );

                        $return = $r2;
                        // $return = _view_details2( prms );
                    }

                    // $return[ "javascript_functions" ][] = "$nwProcessor.reload_datatable";

                    JSONObject a = new JSONObject();
                    a.put( "action_to_perform", "workflow_history" );
                    a.put( "id", $workflow );

                    JSONObject $dx = _view_details2( a );

                    if( $dx.has( "html" ) && !$dx.getString( "html" ).isEmpty() ){
                        $return.put( "history", $dx.getString( "html" ) );
                    }

                    return $return;
                }
            }
            break;
        case "return_comment":
        case "submit_comment":

            String $t1 = "";
            Integer $backward = 0;
            switch( action_to_perform ){
            case "submit_comment":
                $t1 = "submit";
                break;
            default:
                $backward = 1;
                $t1 = "return";
                break;
            }

            String $t2 = "";

            JSONObject opt = new JSONObject();
            opt.put( "type", $e.getString( "type" ) );
            opt.put( "table", $e.getString( "type" ) );
            opt.put( "current_state", $e.getString( "status" ) );
            opt.put( "return_status_data", 1 );
            opt.put( "backward", $backward );

            $ws_status_data = get_workflow_status2( $es, opt );

            $next_state = $e.getString( "status" );
            if( $ws_status_data.has( "key" ) && !$ws_status_data.getString( "key" ).isEmpty() ){
                $next_state = $ws_status_data.getString( "key" );
            }

            if( $ws_status_data.has( $t1 + "_title" ) && !$ws_status_data.getString( $t1 + "_title" ).isEmpty() ){
                $t2 = $ws_status_data.getString( $t1 + "_title" ).toUpperCase();
            }else{
                $t2 = $t1 + " to " + ( $ws_status_data.has( "name" ) ? $ws_status_data.getString( "name" ) : __nwp_workflow_status_format( $next_state ) );
            }

            JSONObject r2 = callBackHandler( new JSONObject()
                    .put("action_to_perform", action_to_perform )
                    .put("workflow_settings_data", $ws_status_data )
                    .put("all_status", get_workflow_status( $es ) )
                    .put("status",$next_state )
                    .put("workflow_data", $e ) );

                if( r2.has( "error" ) && ! r2.getString( "error" ).isEmpty() ){
                    $error_msg = r2.getString( "error" );
                }

                if( $error_msg.isEmpty() ) {
                    JSONObject params = new JSONObject();
                    params.put("action", nwpWorkflow.table_name);
                    params.put("todo", $t1);
                    params.put("id", $e.getString("id"));
                    params.put("empty_container", $empty_container);
                    params.put("datatable", $datatable);
                    params.put("html_replacement_selector", $handle2);
                    params.put("next_state", $next_state);
                    params.put("title", $t2);

                    if ($ws_status_data.length() > 0) {
                        params.put("status", $ws_status_data);
                    }

                    JSONObject op = new JSONObject();

                    if( r2.has( "info" ) && ! r2.getString( "info" ).isEmpty() ){
                        op.put("info", r2.getString( "info" ) );
                    }
                    op.put("id", $e.getString("id"));
                    op.put("e", $e);
                    op.put("ws", $es);
                    op.put("params", params);
                    op.put("action_to_perform", "show_comment");

                    $return = _view_details2(op);
                }
            break;
            case "show_comment":
                $return = getShowComment( $workflow, $wsd, pdata );
            break;
        }

        if( !$error_msg.isEmpty() ){
            return new JSONObject().put("error", $error_msg);
        }

        return $return;
    }

    public static JSONObject sendEmailNotification( JSONObject $workflow, JSONObject opt ){
        JSONObject r = new JSONObject();

        String action = opt.has("action")?opt.getString("action"):"";   //action_to_perform
        String prev_state = opt.has("prev_state")?opt.getString("prev_state"):"";
        JSONObject allStatus = opt.has("allStatus")?opt.getJSONObject("allStatus"):new JSONObject();
        JSONObject $workflowSettings = opt.has("workflowSettings")?opt.getJSONObject("workflowSettings"):new JSONObject();
        JSONObject $statusData = new JSONObject();
        JSONObject users = new JSONObject();

        if( $workflow.has("status") && $workflow.has("id") ){
            //get settings data of current status after status change is complete
            JSONObject wdt  = new JSONObject().put("type", $workflow.getString("type"));
            wdt.put("table", $workflow.getString("table") );
            wdt.put("current_state", $workflow.getString("status") );
            wdt.put("return_status_data", 1);

            $statusData = get_workflow_status2($workflowSettings, wdt );

            //Get Email Recipients
            if ( ! $statusData.has("no_email_on_entry") ) {
                try {
                    //check users with capability to this status
                    users = getUsersWithAccessToWorklowStatus( new JSONObject()
                            .put("workflow", $workflow.getString("id") )
                            .put("workflowSettings", $workflow.getString("workflow_settings") )
                            .put("status", $workflow.getString("status") )
                    );

                    Integer ccCount = 0;
                    //Add CC emails from workflow settings
                    if ( ! $statusData.has("no_cc_email") ) {
                        if ($workflowSettings.has("cc_emails") && $workflowSettings.getJSONArray("cc_emails").length() > 0) {
                            for (Object cc_email_obj : $workflowSettings.getJSONArray("cc_emails")) {
                                String cc_email = cc_email_obj.toString();
                                if (!cc_email.isEmpty()) {
                                    ++ccCount;
                                    users.getJSONArray("row").put(new JSONObject()
                                            .put("name", "WCC " + String.valueOf(ccCount))
                                            .put("email", cc_email)
                                            .put("id", "wcc" + String.valueOf(ccCount)));
                                }
                            }
                        }
                    }

                    //Add CC emails from workflow status settings
                    if ( ! $statusData.has("no_cc_email_on_status") ) {
                        if ($statusData.has("cc_emails") && $statusData.getJSONArray("cc_emails").length() > 0) {
                            for (Object cc_email_obj : $statusData.getJSONArray("cc_emails")) {
                                String cc_email = cc_email_obj.toString();
                                if (!cc_email.isEmpty()) {
                                    ++ccCount;
                                    users.getJSONArray("row").put(new JSONObject()
                                            .put("name", "WSCC " + String.valueOf(ccCount))
                                            .put("email", cc_email)
                                            .put("id", "wscc" + String.valueOf(ccCount)));
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", e.getMessage() )
                                    .put("input", new JSONObject().put("workflow", $workflow).put("opt", opt) )
                                    .put("function", "nwpWorkflow.sendEmailNotification" )
                                    .put("exception", true )
                                    .put("fatal", true ) , e
                    );
                }
            }

            //verify that i have email recipients
            if( users.has("row") && users.getJSONArray("row").length() > 0 ){
                //get email subject
                String subject = "SUBMITTED TO: ";
                if( $workflow.getString("status").equals("complete") ){
                    subject = "";
                }
                String subject2 = "";
                switch( action ){
                    case "return":
                        subject = "RETURNED TO: ";
                        break;
                }
                subject += ( allStatus.has( $workflow.getString("status") )?allStatus.getString( $workflow.getString("status") ):$workflow.getString("status") ).toUpperCase() + " - ";

                if( $workflow.has("name") && ! $workflow.getString("name").isEmpty() ){
                    subject += $workflow.getString("name") + " - ";
                }
                subject2 = subject;
                if( $workflowSettings.has("name") && ! $workflowSettings.getString("name").isEmpty() ){
                    subject += $workflowSettings.getString("name");
                }

                //get email content
                JSONObject content = new JSONObject();
                content.put("By", GlobalFunctions.get_record_name( new JSONObject()
                        .put("id",  $workflow.getString("current_user") )
                        .put("table", nwpAccessRoles.users_table ) ) + " @ "+
                        GlobalFunctions.convert_timestamp_to_date( $workflow.getString("modification_date"), "date-5time", 0 )  );


                content.put("Previous Status", allStatus.has( prev_state )?allStatus.getString( prev_state ):prev_state );
                content.put("Recommendation", $workflow.getString("comment").replace( "rn", "<br>" ).replace( "\n", "<br>" ) );
                content.put("Open and View Details", "<a href=\""+ GlobalFunctions.app_request_source +"/../?nwp_jsp=open_workflow&w="+ $workflow.getString("id") +"&id=" + $workflow.getString("reference") + "\" target=\"_blank\">Click Here to Open</a>" );

                //call email api
                r = nwpConnectToAPI.send_email( new JSONObject()
                        .put("subject", subject )
                        .put("content", GlobalFunctions.getEmailContent( new JSONObject()
                                .put("show_title", true)
                                .put("show_date", true)
                                .put("title", subject2 )
                                .put("html_data", content )
                                //.put("html", content )
                                .put("add_stylesheet", true) )
                        )
                        .put("recipients", users.getJSONArray("row") )
                        .put("reference_table", nwpWorkflow.table_name )
                        .put("reference", $workflow.getString("id") )
                        .put("user_data", GlobalFunctions.app_user_data )
                        /*.put("sender", GlobalFunctions.get_record_name( new JSONObject()
                                .put("id", $workflow.getString("current_user") )
                                .put("table", nwpAccessRoles.users_table ) )
                        )*/
                );
            }
        }

        return r;
    }


    public static JSONObject getShowComment( String $workflow, JSONObject $wsd, JSONObject pdata ){
        JSONObject $return = new JSONObject();
        String $error_msg = "";

        JSONObject dx = new JSONObject();
        JSONObject jdata = new JSONObject();
        JSONObject $fs = new JSONObject();
        String $file_key = "";
        String $wdirection = "";

        if( pdata.has( "params" ) && pdata.getJSONObject( "params" ).has( "id" ) ){
            $wdirection = pdata.getJSONObject( "params" ).getString( "todo" );

            dx.put( "workflow", pdata.getJSONObject( "params" ).getString( "id" ) );
            dx.put( "action", pdata.getJSONObject( "params" ).getString( "action" ) );
            dx.put( "todo", $wdirection );
            dx.put( "html_replacement_selector", pdata.getJSONObject( "params" ).getString( "html_replacement_selector" ) );
            dx.put( "empty_container", pdata.getJSONObject( "params" ).getString( "empty_container" ) );
            dx.put( "datatable", pdata.getJSONObject( "params" ).getString( "datatable" ) );
            dx.put( "info", pdata.has( "info" )?pdata.getString( "info" ):"" );

            dx.put( "params2", pdata.getJSONObject( "params" ) );

            //System.out.println( pdata.getJSONObject( "params" ) );
            if( pdata.getJSONObject( "params" ).has("status") && pdata.getJSONObject( "params" ).getJSONObject("status").has("file") ) {
                //System.out.println( "hi" );
                $fs = pdata.getJSONObject( "params" ).getJSONObject("status").getJSONObject("file");
            }
            //System.out.println( $fs );
        }else{
            $file_key = "start";
            if( ! $file_key.isEmpty() && $wsd.has( "file" ) && $wsd.getJSONObject( "file" ).has( $file_key ) ) {
                $fs = $wsd.getJSONObject("file").getJSONObject($file_key);
            }
        }

               /*if( pdata.has( "e" ) && pdata.getJSONObject( "e" ).has( "status" ) ){
                    String $cstatus = pdata.getJSONObject( "e" ).getString( "status" );
                    if( ! $cstatus.isEmpty() && ! $wdirection.isEmpty() ){

                    }
                }*/
        JSONObject $line = new JSONObject();
        String $key1 = "";
        String $html = "";
        Integer $t = 0;
        if( $fs.length() > 0 ){
            if( $fs.has( "before_html" ) && ! $fs.getString( "before_html" ).isEmpty() ){
                $html += $fs.getString( "before_html" );
            }
            Integer skip;

            if( $fs.has( "data" ) && $fs.getJSONObject( "data" ).length() > 0 ){
                String filesWhere = "";
                for( int i2 = 0; i2 < $fs.getJSONObject( "data" ).names().length(); i2++ ){
                    $key1 = $fs.getJSONObject( "data" ).names().getString( i2 );
                    $line = $fs.getJSONObject( "data" ).getJSONObject( $key1 );

                    if( !$line.has( "form_field" ) ) {
                        $line.put("form_field", "file");
                    }

                    switch( $line.getString( "form_field" ) ){
                        case "file":
                            if( $line.has( "field_label" ) ) {
                                if (!filesWhere.isEmpty()) {
                                    filesWhere += ", ";
                                }
                                filesWhere += "'" + $line.getString( "field_label" ) +"'";
                            }
                            break;
                    }
                }
                //get existing files
                JSONObject uploadedFiles = new JSONObject();
                if( ! filesWhere.isEmpty() ) {
                    try {
                        JSONObject depFiles = GlobalFunctions.get_json(nwpFiles.table_name);
                        if (depFiles.has("fields")) {
                            JSONObject $qs2 = new JSONObject();
                            $qs2.put("where", " AND [" + nwpFiles.table_name + "].[" + depFiles.getJSONObject("fields").getString("workflow") + "] = '" + $workflow + "' AND [" + nwpFiles.table_name + "].[" + depFiles.getJSONObject("fields").getString("name") + "] IN (" + filesWhere + ") ");
                            $qs2.put("select", "SELECT [id], [serial_num], [created_by], [creation_date], [" + depFiles.getJSONObject("fields").getString("name") + "] as 'name', [" + depFiles.getJSONObject("fields").getString("content") + "] as 'content' ");
                            $qs2.put("index_field", "name");
                            $qs2.put("multiple_index", true);
                            $qs2.put("table", nwpFiles.table_name);

                            uploadedFiles = GlobalFunctions.get_records($qs2);
                            //System.out.println( uploadedFiles );
                        }
                    }catch (Exception e){
                        //System.out.println( e.getMessage() );
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("return", e.getMessage() )
                                        .put("input", new JSONObject().put("get", $fs).put("pdata", pdata) )
                                        .put("function", "nwpWorkflow.getShowComment" )
                                        .put("exception", true )
                                        .put("fatal", true ) , e
                        );
                    }
                }

                String extraHtml = "";
                for( int i2 = 0; i2 < $fs.getJSONObject( "data" ).names().length(); i2++ ){
                    $key1 = $fs.getJSONObject( "data" ).names().getString( i2 );
                    $line = $fs.getJSONObject( "data" ).getJSONObject( $key1 );
                    skip = 0;
                    extraHtml = "";

                    $line.put( "display_position", "display-in-table-row" );
                    if( !$line.has( "form_field" ) )
                        $line.put( "form_field", "file" );

                    switch( $line.getString( "form_field" ) ){
                        case "file":
                            if( !( $line.has( "acceptable_files_format" ) && !$line.getString( "acceptable_files_format" ).isEmpty() ) ){
                                $line.put( "acceptable_files_format", GlobalFunctions.upload_file_formats );
                            }
                            try {
                                if ($line.has("field_label") && uploadedFiles.has("row_count") && uploadedFiles.has("row") && uploadedFiles.getInt("row_count") > 0 && uploadedFiles.getJSONObject("row").has($line.getString("field_label"))) {
                                    JSONArray uploaded = uploadedFiles.getJSONObject("row").getJSONArray($line.getString("field_label"));
                                    if (uploaded.length() > 0) {
                                        $line.put("required_field", "no");
                                        extraHtml += "<ol>";
                                        for (int i2a = 0; i2a < uploaded.length(); i2a++) {
                                            JSONObject urlParams = nwpFiles.getFileURL( uploaded.getJSONObject(i2a).put("checkCanOpen", true ) );
                                            String fileTitle = uploaded.getJSONObject(i2a).getString("name");
                                            if( urlParams.has("ext") ){
                                                fileTitle += "." + urlParams.getString("ext");
                                            }
                                            if( urlParams.has("size") ){
                                                fileTitle += " [" + urlParams.getString("size") + "]";
                                            }
                                            if( urlParams.has("url") ){
                                                fileTitle = "<a target='_blank' title='" + fileTitle + "' href='"+ urlParams.getString("url") +"'>" + fileTitle + "</a>";
                                            }

                                            fileTitle += "<small><br />" + GlobalFunctions.convert_timestamp_to_date( uploaded.getJSONObject(i2a).getString("creation_date"), "date-5time", 0 ) + "</small>";

                                            extraHtml += "<li>" + fileTitle + "<br /><br /></li>";
                                        }
                                        extraHtml += "</ol>";
                                    }
                                }
                            }catch (Exception e){
                                extraHtml += e.getMessage();
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

                        JSONObject $ips = new JSONObject();

                        $ips.put( "field_id", $key1 );
                        $ips.put( "form_label", new JSONObject().put( $key1, $line ) );

                        $ips.put( "populate_form_with_values", true );

                        $ips.put( "aa", new JSONObject() );
                        $ips.put( "t", $t );

                        //System.out.println( "wrk" );
                        //System.out.println( $ips );
                        try{
                            $html += nwpFormGen.nw_generate_form_field( $ips ) + extraHtml;
                            //System.out.println( $ips );
                        }catch( Exception e ){
                            $html += "Problem with:" + $key1 + "<br /><br />" + e.getMessage();
                            //System.out.println( $ips );
                            //e.printStackTrace();
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", "Problem with:" + $key1 + "<br /><br />" + e.getMessage() )
                                            .put("input", new JSONObject().put("ips", $ips) )
                                            .put("function", "nwpWorkflow.getShowComment" )
                                            .put("exception", true )
                                            .put("fatal", true ) , e
                            );
                        }

                        ++$t;
                    }
                }
            }
        }

        $return.put( "html", $html );
        $return.put( "data", dx );

        if( ! $error_msg.isEmpty() ){
            $return = new JSONObject().put("error", $error_msg);
        }

        return $return;
    }

    public static JSONObject updateRecordsStatus( JSONObject $ws_status_data, JSONObject opt ){
        JSONObject r = new JSONObject();
        JSONObject $e = opt.has("workflow")?opt.getJSONObject("workflow"):new JSONObject();
        String status_update_type = $ws_status_data.has("status_update_type")?$ws_status_data.getString("status_update_type"):"";
        JSONObject status_updates = $ws_status_data.getJSONObject("status_update");

        //foreach loop
        for( Object status_update_table : status_updates.names() ){
            JSONObject jv = status_updates.getJSONObject( status_update_table.toString() );
            if( jv.has("fields") && jv.getJSONObject("fields").length() > 0 ){

                String updateTable = status_update_table.toString();
                JSONObject updateTableSettings = GlobalFunctions.get_json( updateTable );
                JSONObject sUpdateData = new JSONObject();
                JSONObject sSelf = new JSONObject();

                if( updateTableSettings.has("fields") ) {

                    for (Object field_key : jv.getJSONObject("fields").names()) {
                        JSONObject jv1 = jv.getJSONObject("fields").getJSONObject(field_key.toString());
                        if (jv1.has("value")) {
                            String uSource = jv1.has("source") ? jv1.getString("source") : "";
                            String uValue = jv1.getString("value");
                            Boolean uHasValue = false;

                            switch (uSource) {
                                case "system":
                                    uHasValue = true;
                                    switch( uValue ){
                                        case "current_user":
                                            uValue = GlobalFunctions.app_user;
                                            break;
                                        case "datetime":
                                            Date currentDate = new Date();
                                            uValue = String.valueOf( currentDate.getTime() / 1000 );
                                            break;
                                    }
                                    break;
                                case "workflow":
                                    if( $e.has( uValue ) ){
                                        uHasValue = true;
                                        uValue = $e.getString( uValue );
                                    }
                                    break;
                                case "no_check":
                                    uValue = jv1.get("value").toString();
                                    uHasValue = true;
                                    break;
                                default:
                                    if( $e.has( "reference" ) && ! $e.getString( "reference" ).isEmpty() && ! sSelf.has("id")  ){
                                        sSelf = GlobalFunctions.get_record( new JSONObject().put("table", updateTable ).put( "id", $e.getString( "reference" ) ) );
                                    }
                                    if( sSelf.has("id") ){
                                        uHasValue = true;
                                        switch (uSource) {
                                            case "self":
                                                if (sSelf.has(uValue)) {
                                                    uValue = sSelf.getString(uValue);
                                                }
                                                break;
                                            default:
                                                if( sSelf.has(field_key.toString()) && ! sSelf.equals( jv1.get("value").toString() ) ) {
                                                    uValue = jv1.get("value").toString();
                                                }
                                                break;
                                        }
                                    }
                                    break;
                            }

                            if( uHasValue && updateTableSettings.getJSONObject("fields").has(field_key.toString() ) ){
                                sUpdateData.put( updateTableSettings.getJSONObject("fields").getString(field_key.toString() ), uValue );
                            }


                        }
                    }

                }

                if( sUpdateData.length() > 0 ){
                    JSONObject fUpdateData = new JSONObject();
                    fUpdateData.put("table", updateTable );

                    if( jv.has("no_modification") ){
                        fUpdateData.put("no_modification", jv.get("no_modification") );
                    }

                    switch( status_update_type ){
                        default:
                            //update reference record
                            fUpdateData.put("id", $e.getString( "reference" ) );
                            fUpdateData.put("todo", "edit");
                            break;
                    }

                    fUpdateData.put("post_data", sUpdateData);
                    JSONObject rd = nwpDataTable.saveDataForm( fUpdateData );
                    if( rd.has("error") ){
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("return", rd.getString("error") )
                                        .put("input", new JSONObject().put( "ws", $ws_status_data).put("opt", opt) )
                                        .put("function", "nwpWorkflow.updateRecordsStatus" )
                                        .put("fatal", true ) , null
                        );
                    }

                }

            }
        }

        return r;
    }

    public static JSONObject ChangeStatus( JSONObject e ) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException{

        JSONObject post = e.has( "post" ) ? e.getJSONObject( "post" ) : new JSONObject();
        String action_to_perform = e.has( "action_to_perform" ) ? e.getString( "action_to_perform" ) : "";
        String $workflow = e.has( "id" ) ? e.getString( "id" ) : "";
        String $container = e.has( "container" ) ? e.getString( "container" ) : "";
        String form_action = "";
        Integer $default_setup = 1;
        Integer $skip_link = 1;

        String modal_container = "";
        String modal_title = "";
        String html_replacement_selector = "";

        Integer override_defaults = 0;

        String $error_msg = "";
        String form_submit_button = "Save " + nwpWorkflow.label + " &rarr;";

        // Workflow Settings Data
        JSONObject data = new JSONObject( "{\"date\":\"1627502230\",\"data\":{\"status\":{\"executive_assistant\":{\"name\":\"Executive Assistant\",\"note\":{\"message\":\"<b>Task: Input proposal for out of court settlement. Email is sent to DLSD, HCMD & HADR</b>\",\"type\":\"warning\"}},\"head_adr\":{\"name\":\"Head ADR\",\"note\":{\"message\":\"<b>Task: Review the proposal. Email is sent to DLSD, HCMD & BU</b>\",\"type\":\"warning\"}},\"stakeholder_dept\":{\"name\":\"Stakeholder Department\",\"forward_file\":\"a2\",\"note\":{\"message\":\"<b>Task: Provide Feedback</b>\",\"type\":\"warning\"}},\"final_decision\":{\"name\":\"Head ADR for Final Decision\",\"hide_return\":1,\"forward_file\":\"a1\",\"submit_title\":\"Submit to Head ADR for Final Decision\",\"note\":{\"message\":\"<b>Task: Input the final decision reached on the system</b>\",\"type\":\"warning\"}},\"complete\":{\"name\":\"Finished\",\"title\":\"Finished\"}},\"forward\":{\"start\":\"executive_assistant\",\"executive_assistant\":\"head_adr\",\"head_adr\":\"stakeholder_dept\",\"stakeholder_dept\":\"final_decision\",\"final_decision\":\"complete\"},\"backward\":{\"executive_assistant\":\"start\",\"head_adr\":\"executive_assistant\",\"stakeholder_dept\":\"head_adr\",\"final_decision\":\"stakeholder_dept\"},\"callback\":{},\"form\":{},\"field\":{},\"file\":{\"start\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_proposal\":{\"field_label\":\"Out of Court Settlement Proposal\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}},\"a2\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_feedback_sth\":{\"field_label\":\"Out of Court Settlement - Feedback from Stakeholder Dept.\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}},\"a1\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_final_decision\":{\"field_label\":\"Out of Court Settlement Final Decision\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}}},\"completion_status_update\":\"completed\",\"returned_status_update\":\"returned\",\"title\":\"OUT OF COURT SETTLEMENT - POST JUDGEMENT\",\"sub_title\":\"Litigation Support\",\"skip_workflow_items\":1,\"open_datatable_default_view\":\"\",\"open_datatable_custom_view\":\"\",\"open_datatable\":\"\",\"open_datatable_todo\":\"display_all_records_frontend\",\"hide_buttons\":1,\"split_screen_action\":\"?action=nwp_ecm&todo=execute&nwp_action=ecm2&nwp_todo=view_wf\",\"utility_buttons\":{\"comments\":1,\"attach_file\":1,\"view_attachments\":1,\"view_details\":1},\"more_buttons\":{},\"plugin\":\"nwp_ecm\",\"table\":\"ecm2\"},\"description\":\"LITIGATION SUPPORT (Out of court settlement - Post Judgement)\",\"data_default\":{\"status\":{\"executive_assistant\":{\"name\":\"Executive Assistant\",\"note\":{\"message\":\"<b>Task: Input proposal for out of court settlement. Email is sent to DLSD, HCMD & HADR</b>\",\"type\":\"warning\"}},\"head_adr\":{\"name\":\"Head ADR\",\"note\":{\"message\":\"<b>Task: Review the proposal. Email is sent to DLSD, HCMD & BU</b>\",\"type\":\"warning\"}},\"stakeholder_dept\":{\"name\":\"Stakeholder Department\",\"forward_file\":\"a2\",\"note\":{\"message\":\"<b>Task: Provide Feedback</b>\",\"type\":\"warning\"}},\"final_decision\":{\"name\":\"Head ADR for Final Decision\",\"hide_return\":1,\"forward_file\":\"a1\",\"submit_title\":\"Submit to Head ADR for Final Decision\",\"note\":{\"message\":\"<b>Task: Input the final decision reached on the system</b>\",\"type\":\"warning\"}},\"complete\":{\"name\":\"Finished\",\"title\":\"Finished\"}},\"forward\":{\"start\":\"executive_assistant\",\"executive_assistant\":\"head_adr\",\"head_adr\":\"stakeholder_dept\",\"stakeholder_dept\":\"final_decision\",\"final_decision\":\"complete\"},\"backward\":{\"executive_assistant\":\"start\",\"head_adr\":\"executive_assistant\",\"stakeholder_dept\":\"head_adr\",\"final_decision\":\"stakeholder_dept\"},\"callback\":{},\"form\":{},\"field\":{},\"file\":{\"start\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_proposal\":{\"field_label\":\"Out of Court Settlement Proposal\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}},\"a2\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_feedback_sth\":{\"field_label\":\"Out of Court Settlement - Feedback from Stakeholder Dept.\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}},\"a1\":{\"before_html\":\"\",\"data\":{\"pj_out_of_court_final_decision\":{\"field_label\":\"Out of Court Settlement Final Decision\",\"required_field\":\"yes\",\"acceptable_files_format\":\"pdf:::doc:::docx:::xls:::xlsx:::csv:::pptx:::ppt\"}}}},\"completion_status_update\":\"completed\",\"returned_status_update\":\"returned\",\"title\":\"OUT OF COURT SETTLEMENT - POST JUDGEMENT\",\"sub_title\":\"Litigation Support\",\"skip_workflow_items\":1,\"open_datatable_default_view\":\"\",\"open_datatable_custom_view\":\"\",\"open_datatable\":\"\",\"open_datatable_todo\":\"display_all_records_frontend\",\"hide_buttons\":1,\"split_screen_action\":\"?action=nwp_ecm&todo=execute&nwp_action=ecm2&nwp_todo=view_wf\",\"utility_buttons\":{\"comments\":1,\"attach_file\":1,\"view_attachments\":1,\"view_details\":1},\"more_buttons\":{},\"plugin\":\"nwp_ecm\",\"table\":\"ecm2\"},\"creation_date\":\"1627481429\",\"serial_num\":\"47\",\"created_by\":\"35991362173a\",\"reference\":\"\",\"plugin\":\"nwp_ecm\",\"reference_table\":\"\",\"modification_date\":\"1628020630\",\"modified_by\":\"35991362173a\",\"name\":\"OUT OF COURT SETTLEMENT - POST JUDGEMENT\",\"id\":\"ws27048312856\",\"category\":\"litigation_support\",\"table\":\"ecm2\"}" );
        JSONObject table_fields = new JSONObject();
        JSONObject ecm_labels = new JSONObject();
        JSONObject ecm_data = GlobalFunctions.get_json( nwpEcm2.table_name );
        if( ecm_data.has( "fields" ) ){
            table_fields = ecm_data.getJSONObject( "fields" );
            ecm_labels = ecm_data.getJSONObject( "labels" );
        }

        JSONObject wfs_data = new JSONObject();

        JSONObject form_values_important = new JSONObject();
        JSONObject form_extra_options = new JSONObject();
        JSONObject custom_form_fields = new JSONObject();
        JSONObject hidden_records_css = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject hidden_records = new JSONObject();

        JSONObject $e = new JSONObject();
        JSONObject $ed = new JSONObject();
        String $wd = "";

        // System.out.print( data );

        if( !( data.has( "id" ) && !data.getString( "id" ).isEmpty() ) ){
            $error_msg = "<h4><strong>Invalid Workflow Data</strong></h4>";
        }

        if( $workflow.isEmpty() ){
            $error_msg = "<h4><strong>Invalid Workflow ID</strong></h4>";
        }

        if( !$error_msg.isEmpty() ){
            return new JSONObject( "{\"error\":" + $error_msg + "}" );
        }

        switch( action_to_perform ){
        case "cancel_pop_form":
        case "transfer_pop_form":
        case "change_status_pop_form":
        case "priority_pop_form":
            form_action = "?action=" + nwpWorkflow.table_name + "&todo=save_" + action_to_perform + "&workflow=" + $workflow;

            if( !$container.isEmpty() ){
                html_replacement_selector = $container;
                modal_container = "#" + $container;
            }
        case "edit_popup_form_in_popup":
        case "edit_popup_form":
            //            $_POST["mod"] = "edit-".md5(nwpWorkflow.table_name);
            modal_title = "Edit " + nwpWorkflow.label;
            override_defaults = 1;

            $default_setup = 0;

            switch( action_to_perform ){
            case "transfer_pop_form":
                modal_title = "Transfer Job";

                for( Integer i1 = 0; i1 < table_fields.names().length(); i1++ ){
                    String $k = table_fields.names().getString( i1 );
                    String $v = table_fields.getString( $k );
                    JSONObject itmv = new JSONObject();

                    switch( $k ){
                    case "team":
                    case "group":
                        //case "unit":
                    case "department":
                        //case "division":
                    case "comment":
                        form_values_important.put( $v, "" );
                        itmv.put( "required_field", "yes" );
                        break;
                    default:
                        hidden_records.put( $v, 1 );
                        break;
                    }

                    form_extra_options.put( $v, itmv );
                }

                form_submit_button = "Save Transfer &rarr;";
                break;
            case "change_status_pop_form":
                modal_title = "Change Status of Job";

                for( Integer i1 = 0; i1 < table_fields.names().length(); i1++ ){
                    String $k = table_fields.names().getString( i1 );
                    String $v = table_fields.getString( $k );
                    JSONObject itmv = new JSONObject();
                    switch( $k ){
                    //case "comment":
                    //break;
                    case "recommendation":
                        itmv.put( "required_field", "yes" );
                        break;
                    default:
                        hidden_records.put( $v, 1 );
                        break;
                    }
                    form_extra_options.put( $v, itmv );
                }


                if( post.has( "id" ) && !post.getString( "id" ).isEmpty() ){
                    //                    $this->class_settings["current_record_id"] = $_POST["id"];
                    //                    $e = $this->_get_record();
                }
                String $mt = "";
                if( $e.has( "type" ) && !$e.getString( "type" ).isEmpty() ){
                    /*$nw = new cNwp_workflow();
                    $nw->class_settings = $this->class_settings;
                    $tb = "workflow_settings";
                    $w = $nw->load_class( array( "class" => array( $tb ), "initialize" => 1 ) )[ $tb ];
                    $w->class_settings["current_record_id"] = $e["type"];
                    $p = $w->_get_record();*/
                    JSONObject $p = new JSONObject();

                    if( $p.has( "id" ) && !$p.getString( "id" ).isEmpty() ){
                        $wd = GlobalFunctions.urldecode( $p.getString( "data" ) );
                        JSONObject $wdd = new JSONObject( $wd );
                        JSONObject $wd_$forward = $wdd.has( "forward" ) ? $wdd.getJSONObject( "forward" ) : new JSONObject();
                        JSONObject $wd_$backward = $wdd.has( "backward" ) ? $wdd.getJSONObject( "backward" ) : new JSONObject();

                        if( $wdd.has( "status" ) && $wdd.getJSONObject( "status" ).length() > 0 ){

                            for( Integer i1 = 0; i1 < $wdd.getJSONObject( "status" ).names().length(); i1++ ){
                                String $sk = $wdd.getJSONObject( "status" ).names().getString( i1 );
                                Object aObj = $wdd.getJSONObject( "status" ).names().get( i1 );
                                String[] valueArray = { "start", "complete", "cancelled" };

                                if( !Arrays.asList( valueArray ).contains( $sk ) && $wd_$forward.has( $sk ) || $wd_$backward.has( $sk ) ){
                                    String snme = $sk;
                                    if( aObj instanceof String ){
                                    }else{
                                        JSONObject $sv = $wdd.getJSONObject( "status" ).getJSONObject( $sk );
                                        if( $sv.has( "name" ) ){
                                            snme = $sv.getString( "name" );
                                        }
                                    }
                                    $mt += $sk + ":" + snme + ";";

                                }
                            }
                        }
                    }else{
                        $error_msg = "<h4>Invalid Process</h4>The selected process might have been removed";
                    }
                }

                if( $mt.isEmpty() ){
                    $error_msg = "<h4>Invalid Workflow Settings</h4>No Status was found";
                }

                ArrayList<String> field_ids = new ArrayList<String>();
                JSONObject form_label = new JSONObject();

                field_ids.add( "nwp_wf_next_state" );
                JSONObject nwp_wf_next_state = new JSONObject();
                nwp_wf_next_state.put( "field_label", "New Status" );
                nwp_wf_next_state.put( "form_field", "select" );
                nwp_wf_next_state.put( "options", $mt );
                nwp_wf_next_state.put( "required_field", "yes" );

                nwp_wf_next_state.put( "display_position", "display-in-table-row" );
                nwp_wf_next_state.put( "serial_number", 12.4 );

                form_label.put( "nwp_wf_next_state", nwp_wf_next_state );

                custom_form_fields.put( "field_ids", field_ids );
                custom_form_fields.put( "form_label", form_label );

                attributes.put( "custom_form_fields", custom_form_fields );

                form_submit_button = "Save Status Change &rarr;";
                break;
            case "cancel_pop_form":
                modal_title = "Cancel Job";

                for( Integer i1 = 0; i1 < table_fields.names().length(); i1++ ){
                    String $k = table_fields.names().getString( i1 );
                    String $v = table_fields.getString( $k );
                    JSONObject itmv = new JSONObject();

                    switch( $k ){
                    case "comment":
                        itmv.put( "required_field", "yes" );
                        break;
                    default:
                        hidden_records.put( $v, 1 );
                        break;
                    }
                    form_extra_options.put( $v, itmv );
                }

                form_submit_button = "Confirm Cancellation &rarr;";
                break;
            case "priority_pop_form":
                modal_title = "Manage Priority";

                for( Integer i1 = 0; i1 < table_fields.names().length(); i1++ ){
                    String $k = table_fields.names().getString( i1 );
                    String $v = table_fields.getString( $k );
                    JSONObject itmv = new JSONObject();

                    switch( $k ){
                    case "flag_comment":
                        form_values_important.put( $v, "" );
                        break;
                    //case "flag_by":
                    case "flag":
                    case "priority":
                        break;
                    default:
                        hidden_records.put( $v, 1 );
                        break;
                    }
                }

                form_submit_button = "Save Priority &rarr;";
                break;
            }
            break;
        }

        hidden_records.put( table_fields.getString( "data" ), 1 );
        hidden_records.put( table_fields.getString( "meta_data" ), 1 );

        if( $default_setup > 0 ){

            for( Integer i1 = 0; i1 < table_fields.names().length(); i1++ ){
                String $k = table_fields.names().getString( i1 );
                String $v = table_fields.getString( $k );
                JSONObject itmv = new JSONObject();

                switch( $k ){
                case "recommendation":
                case "priority":
                case "description":
                case "name":
                    break;
                case "group":
                    form_values_important.put( $v, "group_1" );
                    hidden_records_css.put( $v, 1 );
                    break;
                case "team":
                    form_values_important.put( $v, "team_b" );
                    hidden_records_css.put( $v, 1 );
                    break;
                case "department":
                    form_values_important.put( $v, "dts27104971748" );
                    hidden_records_css.put( $v, 1 );
                    break;
                case "type":
                case "category":
                    hidden_records_css.put( $v, 1 );
                    break;
                default:
                    hidden_records.put( $v, 1 );
                    break;
                }
            }

            if( wfs_data.length() > 1 ){
                Double $sn = 12.101;
                //                $fff = get_form_fields();
                /*foreach( $this->class_settings["wfs_data"] as $wfs_type => $wfs_value ){

                 *//* switch( $wfs_type ){
						case "file":break;
						default:
							$ed["wfs_fields_data"] = $this->class_settings["wfs_fields_data"];
							$ed["wfs_fields_data_key"] = $this->class_settings["wfs_fields_data_key"];
						break;
						} *//*

                    if( isset( $wfs_value["data"] ) && is_array( $wfs_value["data"] ) && ! empty( $wfs_value["data"] ) ){

                        foreach( $wfs_value["data"] as $xk => $xv ){
                            //$xk = $xk1 . $wfs_type;
                            $f1[] = "wfs_" . $xk;

                            $ed["wfs_fields"][ $wfs_type ][ "wfs_" . $xk ] = $xv;

                            switch( $wfs_type ){
                                case "file":
                                    $xv["form_field"] = "file";

                                    if( isset( $xv["acceptable_files_format"] ) && $xv["acceptable_files_format"] ){
                                        if( isset( $xv["note"] ) )$xv["note"] .= "<br />";
                                        else $xv["note"] = "";

                                        $xv["note"] .= "Acceptable formats: ". str_replace( ":::", ", ", $xv["acceptable_files_format"] );
                                    }
                                    break;
                                default:
                                    if( ! isset( $xv["form_field"] ) || ( isset( $xv["form_field"] ) && ! $fff[ $xv["form_field"] ] ) ){
                                        $xv["form_field"] = "text";
                                    }
                                    break;
                            }

                            $f2[ "wfs_" . $xk ] = $xv;
                            $f2[ "wfs_" . $xk ]["display_position"] = "display-in-table-row";

                            if( ! ( isset( $xv["serial_number"] ) && $xv["serial_number"] ) ){
                                $sn = $sn + 0.01;
                                $f2[ "wfs_" . $xk ]["serial_number"] = $sn;
                            }

                        }
                    }
                }*/
            }

            /*if( ! empty( $f1 ) ){
                $this->class_settings["attributes"]["custom_form_fields"] = array(
                        "field_ids" => $f1,
                        "form_label" => $f2,
					);
            }*/
            //print_r( $this->class_settings["attributes"]["custom_form_fields"] ); exit;
        }

        /*if( isset( $this->class_settings["meta_data"] ) && isset( $this->class_settings["meta_data_key"] ) && $this->class_settings["meta_data"] ){
            $ed["meta_data"] = $this->class_settings["meta_data"];
            $ed["meta_data_key"] = $this->class_settings["meta_data_key"];

            $df = new cDatabase_forms();
            $df->class_settings = $this->class_settings;

            //$df->class_settings[ "return_html" ] = 1;
            //$df->class_settings[ "action_to_perform" ] = "display_form2";
            $df->class_settings[ "action_to_perform" ] = "display_form_fields_and_labels";
            $df->class_settings[ "current_record_id" ] = $this->class_settings["meta_data"];

            if( isset( $mv ) && $mv ){
                $ed["meta_data_fields"] = array_keys( $mv );
                $df->class_settings[ "saved_data" ] = $mv;
            }
            //$df->class_settings[ "params" ][ "more_data" ] = $e;

            $df->class_settings[ "html_replacement_selector" ] = "modal-replacement-handle";
            $df->class_settings[ "params" ][ "exclude_form_tag" ] = 1;

            $dff = $df->database_forms();
            //print_r( $dff ); exit;
            $ed["meta_data_fields"] = isset( $dff[ "labels" ] )?$dff[ "labels" ]:array();
            //$ed["meta_data_fields"] = isset( $dff[ "fields" ] )?$dff[ "fields" ]:array();
            $this->class_settings["form_extra_field_elements"] = isset( $dff[ "html_form" ] )?$dff[ "html_form" ]:"";
        }*/

        //$this->class_settings["skip_link"] = 1;
        // $this->class_settings["modal_callback"] = "nwCustomers.loadForm";
        //$this->class_settings[ "form_extra_field_data" ] = json_encode( $ed );

        JSONObject opt = new JSONObject();
        opt.put( "form_values_important", form_values_important );
        opt.put( "form_extra_options", form_extra_options );
        opt.put( "custom_form_fields", custom_form_fields );
        opt.put( "hidden_records_css", hidden_records_css );
        opt.put( "attributes", attributes );
        opt.put( "hidden_records", hidden_records );
        opt.put( "skip_link", $skip_link );

        opt.put( "method", "meth" );
        opt.put( "action_to_perform", "action_to_perform" );
        opt.put( "form_class", "form_class" );
        opt.put( "html_id", "html_id" );
        opt.put( "table", nwpEcm2.table_name );
        opt.put( "labels", ecm_labels );
        opt.put( "form_order", new JSONArray() );

        JSONObject Pdata = new JSONObject();
        Pdata.put( "data", opt );

        if( !$error_msg.isEmpty() ){
            return new JSONObject( "{\"error\":" + $error_msg + "}" );
        }

        String F = nwpFormGen.createForm( Pdata );
        JSONObject $return = new JSONObject();

        $return.put( "html_replacement", F );

        return $return;

    }

    public static JSONObject callBackHandler( JSONObject d ){
        JSONObject r = new JSONObject();
        JSONObject ws = d.has("workflow_settings_data")?d.getJSONObject("workflow_settings_data"):new JSONObject();
        JSONObject wcb = ws.has("callback")?ws.getJSONObject("callback"):new JSONObject();

        if( wcb.has("nwp2_action") && wcb.has("nwp2_todo") ){
            String methodName = wcb.getString("nwp2_action") + "_" + wcb.getString("nwp2_todo");

            try {
                // get the class and the method using reflection
                nwpCallback myClass = new nwpCallback();
                Class<?>[] formalParameters = { JSONObject.class };

                Method myMethod = myClass.getClass().getDeclaredMethod(methodName, formalParameters);

                // invoke the method dynamically
                Object result = myMethod.invoke(myClass, d);
                r = new JSONObject( result.toString() );

            } catch (Exception e) {
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "unrecognized claass method: " + methodName )
                                .put("input", d )
                                .put("function", "nwpWorkflow.callBackHandler" )
                                .put("exception", true )
                                .put("fatal", true ) , e
                );
            }

        }

        return r;
    }
    public static JSONObject VisualiseWorkflow( JSONObject pdata ){

        JSONObject $return = new JSONObject();

        JSONObject get = pdata.has( "get" ) ? pdata.getJSONObject( "get" ) : new JSONObject();
        JSONObject post = pdata.has( "post" ) ? pdata.getJSONObject( "post" ) : new JSONObject();

        String user_id = GlobalFunctions.app_user;

        String action_to_perform = pdata.has( "action_to_perform" ) ? pdata.getString( "action_to_perform" ) : "";
        String workflow_id = pdata.has( "id" ) ? pdata.getString( "id" ) : "";
        String $container = pdata.has( "container" ) ? pdata.getString( "container" ) : "";

        String $plugin = pdata.has( "plugin" ) ? pdata.getString( "plugin" ) : "";

        Integer $direct_jump = pdata.has( "direct_jump" ) ? pdata.getInt( "direct_jump" ) : 0;

        String modal_container = "";
        String modal_title = "";
        String html_replacement_selector = "";
        ArrayList<String> $js = new ArrayList<String>();

        Integer override_defaults = 0;

        String $error_msg = "";

        JSONObject data = new JSONObject();
        JSONObject $wsd = new JSONObject();
        JSONObject $e = new JSONObject();

        // Get Workflow Settings Data
        String workflow_settings_id = pdata.getString( "id" );

        // Get Workflow Settings Data
        JSONObject $qs = new JSONObject();
        $qs.put( "id", workflow_settings_id );
        $qs.put( "table", nwpWorkflow.workflow_settings_table_name );
        data = GlobalFunctions.get_record( $qs );

        $wsd = data.has( "data" ) ? new JSONObject( GlobalFunctions.urldecode( data.getString( "data" ) ) ) : new JSONObject();
        // System.out.print( $wsd );

        if( data.length() == 0 ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Settings Data</strong></h4>";
        }

        if( $wsd.length() == 0 ){
            $error_msg = "<h4><strong>Unable to Retrieve Workflow Settings Data Items</strong></h4>";
        }

        if( !$error_msg.isEmpty() ){
            return new JSONObject().put( "error", $error_msg );
        }

        String dd = GlobalFunctions.stripslashes( GlobalFunctions.urldecode( data.getString( "data" ) ) );
        // dd = dd.replaceAll("\t", "").replaceAll("\n", "");;
        //System.out.print( dd );

        JSONObject $b = new JSONObject();
        JSONArray $ff = new JSONArray();
        JSONObject $wd = data.has( "data" ) ? new JSONObject( dd.toString() ) : new JSONObject();
        Integer $flow_count = 0;
        Integer $sn = 0;

        if( !$error_msg.isEmpty() ){
            return new JSONObject( "{\"error\":" + $error_msg + "}" );
        }

        if( $wd.has( "forward" ) && $wd.getJSONObject( "forward" ).length() > 0 ){
            //System.out.println($wd.getJSONObject( "forward" ).names());
            Integer $len = $wd.getJSONObject( "forward" ).names().length();
            String $k;
            if( $wd.has( "forward_order" ) && $wd.getJSONArray( "forward_order" ).length() > 0 ){
                $len = $wd.getJSONArray( "forward_order" ).length();
            }

            for( Integer i1 = 0; i1 < $len; i1++ ){
                if( $wd.has( "forward_order" ) && $wd.getJSONArray( "forward_order" ).length() > 0 ) {
                    $k = $wd.getJSONArray( "forward_order" ).getString( i1 );
                }else{
                    $k = $wd.getJSONObject("forward").names().getString(i1);
                }
                if( $k.toLowerCase().equals("complete") || $k.toLowerCase().equals("start") ){
                    continue;
                }
                String $v = $wd.getJSONObject( "forward" ).getString( $k );

                JSONObject $f2 = new JSONObject();
                JSONObject $wd_status = $wd.has( "status" ) ? $wd.getJSONObject( "status" ) : new JSONObject();
                JSONObject $wd_forward = $wd.has( "forward" ) ? $wd.getJSONObject( "forward" ) : new JSONObject();

                if( $wd_status.has( $v ) ){
                    //$ff[  ]
                    $f2 = new JSONObject();
                    JSONObject tdd = $wd_status.has( $v ) ? $wd_status.getJSONObject( $v ) : new JSONObject();
                    String tvalue = tdd.has( "name" ) ? tdd.getString( "name" ) : $v;
                    String[] tv = { tvalue };
                    String $next = "";

                    $f2.put( "label", $v );
                    $f2.put( "type", "decision" );
                    $f2.put( "text", tv );
                    $f2.put( "orient", new JSONObject( "{\"yes\" : \"b\", \"next\" : \"r\"}" ) );

                    if( $wd_forward.has( $v ) && $wd_status.has( $wd_forward.getString( $v ) ) ){
                        $f2.put( "yes", $wd_forward.getString( $v ) );
                    }

                    ArrayList<String> $has_next = new ArrayList<String>();
                    String $has_next_type = "process";
                    if( tdd.has( "forward_option" ) && tdd.getJSONObject( "forward_option" ).has("status") && tdd.getJSONObject( "forward_option" ).getJSONObject("status").length() > 0 ){

                        for( Integer i2 = 0; i2 < tdd.getJSONObject( "forward_option" ).getJSONObject("status").names().length(); i2++ ){
                            String $fk = tdd.getJSONObject( "forward_option" ).getJSONObject("status").names().getString( i2 );
                            // String $fv = tdd.getJSONObject( "forward_option" ).getString( $fk );
                            JSONObject $wd_status_$fk = $wd_status.has( $fk ) ? $wd_status.getJSONObject( $fk ) : new JSONObject();

                            if( $wd_status.has( $fk ) ){
                                $has_next_type = "finish";
                                $has_next.add( $wd_status_$fk.has( "name" ) ? $wd_status_$fk.getString( "name" ) : $wd_status.getString( $fk ) );
                            }
                        }

                        if( $has_next_type.equals( "finish" ) ){
                            $has_next.add( "---" );
                        }
                    }
                    if( tdd.has( "forward_file" ) && !tdd.getString( "forward_file" ).isEmpty() ){
                        $has_next.add( "Has Attachments" );
                    }
                    if( tdd.has( "forward_form" ) && !tdd.getString( "forward_form" ).isEmpty() ){
                        $has_next.add( "Has Meta Form" );
                    }

                    if( $has_next.size() > 0 ){
                        $next = "nwp_nt_" + $v;
                        $f2.put( "next", $next );
                    }

                    $ff.put( $f2 );
                    ++$flow_count;
                    if( $has_next.size() > 0 ){
                        $f2 = new JSONObject();
                        $f2.put( "label", $next );
                        $f2.put( "type", $has_next_type );
                        $f2.put( "text", $has_next );
                        $f2.put( "orient", new JSONObject( "{\"yes\" : \"b\", \"next\" : \"r\"}" ) );

                        $ff.put( $f2 );
                    }
                }

            }

            $b.put( "flow_count", $flow_count );
            $b.put( "flow", $ff );
        }

        //System.out.println($b);
        $return.put( "data_items", new JSONObject().put( data.getString( "id" ), data ) );
        $b.put( "id", data.getString( "id" ) );
        $return.put( "other_params", $b );

        return $return;
    }

    public static String getDataTable(){
        String html = "";
        JSONObject $table = new JSONObject();
        $table.put( "table", getTableName() );
        //html = nwpDataTable.getDataTable( $table );
        return html;
    }
}
