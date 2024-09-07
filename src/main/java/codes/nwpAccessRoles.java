package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import java.io.File;

public class nwpAccessRoles {
    public static String access_role_status = "access_role_status";
    public static String users_table = "users";
    public static String grade_level_table = "ecm_grade_level";
    public static String table_name = "access_roles";

    public static JSONObject AuthenticateUser(JSONObject jsonPOST){
        JSONObject r = new JSONObject();
        String error = "";
        JSONObject appData = GlobalFunctions.getApp();

        try {
            //check for user on local db
            if( jsonPOST.has("username") && ! jsonPOST.getString("username").isEmpty() && jsonPOST.has("password")  && ! jsonPOST.getString("password").isEmpty() ){

                if( appData.has("active_directory_domain_name") && appData.has("active_directory_server_name") && ! appData.getString("active_directory_domain_name").isEmpty() && ! appData.getString("active_directory_server_name").isEmpty() ) {

                    if( GlobalFunctions.nwp_development_mode &&
                            GlobalFunctions.app_data.has("no_active_directory_pwd") &&
                            ! GlobalFunctions.app_data.getString("no_active_directory_pwd").isEmpty() ){

                            if( jsonPOST.getString("password").equals( GlobalFunctions.app_data.getString("no_active_directory_pwd") ) ){
                                r.put("id", jsonPOST.getString("username") );
                                r.put("username", jsonPOST.getString("username") );
                            }
                    }else{
                        r = ActiveDirectory.getConnection(jsonPOST.getString("username"),jsonPOST.getString("password"), appData.getString("active_directory_domain_name"), appData.getString("active_directory_server_name"));
                    }

                    if( r.has("id") ){
                        GlobalFunctions.currentUserName = jsonPOST.getString("username");
                        GlobalFunctions.currentUserPwd = jsonPOST.getString("password");

                        if( GlobalFunctions.app_data.has("log_success_login") && GlobalFunctions.app_data.getBoolean("log_success_login") ){
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("login",true)
                                            .put("function","Successful Login")
                                            .put("user", jsonPOST.getString("username") ),
                                    null
                            );
                        }

                    }else{
                        if( ActiveDirectory.adError.isEmpty() ){
                            error = "<h4>Authentication Failed</h4>Invalid user credentials.<br><br>Ensure your account is active and that you have provided the correct password";
                        }else{
                            error = "<h4>Authentication Failed</h4>Provide valid AD Credentials<br><br>NOTE: Admin can enable Dev. Mode to view more details";
                            if( GlobalFunctions.nwp_development_mode ){
                                error += "<br>AD Auth Error<br>" + ActiveDirectory.adError;
                            }
                        }

                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("login",true)
                                        .put("function","Failed Login")
                                        .put("user", jsonPOST.getString("username") )
                                        .put("error", error ),
                                null
                        );
                    }

                    /*LdapContext test;
                    test = ActiveDirectory.getConnection(jsonPOST.getString("username"),jsonPOST.getString("password"), appData.getString("active_directory_domain_name"), appData.getString("active_directory_server_name"));
                    r = ActiveDirectory.getUser(jsonPOST.getString("username"), test);
                    test.close();*/

                }else{
                    error = "Invalid Config File...No AD Server & Domain Name...contact support team";
                }
            }else{
                error = "Specify your Active Directory Username & Password";
            }
        }catch(Exception e){
            error = "<h4>Auth Failed</h4>" + e.getMessage();
        }

        if( r.has("id") && ! r.getString("id").isEmpty() ){

            Boolean foundUser = false;
            JSONObject dep = GlobalFunctions.get_json( nwpAccessRoles.users_table );
            if (dep.has("fields")) {
                String $where = "";
                JSONObject $qf = dep.getJSONObject("fields");
                JSONObject $qs = new JSONObject();
                $qs.put("fields", $qf);

                if( $qf.has("username") ){
                    $where += " AND LOWER( [" + $qf.getString("username") + "] ) = '" + r.getString("username").trim().toLowerCase() + "' ";
                    $qs.put("where", $where);
                    $qs.put("table", nwpAccessRoles.users_table );

                    JSONObject $qr = GlobalFunctions.get_records($qs);
                    if ($qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) {
                        r = $qr.getJSONArray("row").getJSONObject(0);

                        JSONObject $item = new JSONObject();
                        JSONObject $qs1 = new JSONObject();

                        if (r.has("role") && ! r.getString("role").isEmpty()) {
                            $qs1 = new JSONObject();
                            $qs1.put("id", r.getString("role"));
                            $qs1.put("table", nwpAccessRoles.table_name);

                            $item = GlobalFunctions.get_record($qs1);
                            if ($item.has("name")) {
                                r.put("access_role", $item.getString("name"));
                                if ($item.has("data")) {
                                    JSONObject data = new JSONObject( $item.getString("data") );
                                    if( data.has("accessible_functions") ){
                                        r.put("accessible_functions", data.getJSONObject("accessible_functions") );
                                    }
                                }
                            }

                            if (r.has("department") && !r.getString("department").isEmpty()) {
                                $qs1 = new JSONObject();
                                $qs1.put("id", r.getString("department"));
                                $qs1.put("table", "departments");

                                $item = GlobalFunctions.get_record($qs1);
                                if ($item.has("name")) {
                                    r.put("department_text", $item.getString("name"));
                                    r.put("department_sbu", "" );
                                    if( $item.has("sbu") ){
                                        r.put("department_sbu", $item.getString("sbu") );
                                    }
                                }
                            }

                            if (r.has("grade_level") && !r.getString("grade_level").isEmpty()) {
                                $qs1 = new JSONObject();
                                $qs1.put("id", r.getString("grade_level"));
                                $qs1.put("table", "ecm_grade_level");

                                $item = GlobalFunctions.get_record($qs1);
                                if ($item.has("name")) {
                                    r.put("grade_level_text", $item.getString("name"));
                                }
                            }

                            foundUser = true;
                        }

                    }
                }
            }

            if( ! foundUser ){
                if( appData.has("admin_users") && appData.getString("admin_users").contains( r.getString("username").toLowerCase() ) ){
                    foundUser = true;
                    r.put("role", GlobalFunctions.app_sys_admin_role ).put("access_role", "Sys Admin");
                    //r.put("department", "OFISD").put("group", "OFISD Group 8 -K").put("team", "Team A Group 8");
                }
            }

            if( foundUser ) {
                GlobalFunctions.app_user_data = r;
                if (r.has("id") && ! r.getString("id").isEmpty()) {
                    GlobalFunctions.app_user = r.getString("id");
                }

                if( GlobalFunctions.nwp_development_mode ){
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject().put("return", GlobalFunctions.app_user_data ).put("input", r ).put("function", "nwpAccessRoles.AuthenticateUser" ),
                            null
                    );
                }
            }else{
                if( error.isEmpty() ) {
                    error = "<h4>Unassigned User</h4>" + r.getString("username") + " has not been assigned an access role on the ECM";
                }
                r = new JSONObject();
            }

            //log event...

        }else if( error.isEmpty() ){
            error = "Authentication Failed";
        }


        if( ! error.isEmpty() ){
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice = error;
            GlobalFunctions.app_notice_only = true;
        }

        return r;
    }

    public static Boolean canUpdateEmployeeDetailsFromAPI( JSONObject input ){
        Boolean callAPI = true;

        JSONObject $access = new JSONObject();
        if( input.has("source") ) {
            switch( input.getString("source") ){
                case "edit_users":
                case "create_users":
                    if ( GlobalFunctions.app_user_data.has("accessible_functions") ) {
                        $access = GlobalFunctions.app_user_data.getJSONObject("accessible_functions");
                        if ($access.has(nwpAccessRoles.users_table + ".skip_refresh.skip_refresh")) {
                            callAPI = false;
                        }
                    }

                    if( callAPI ){
                        if( GlobalFunctions.app_data.has("skip_staff_details_api")
                                && GlobalFunctions.app_data.getJSONObject("skip_staff_details_api").has( input.getString("source") )
                                && GlobalFunctions.app_data.getJSONObject("skip_staff_details_api").getBoolean( input.getString("source") ) ){
                            callAPI = false;
                        }
                    }
                    break;
            }
        }

        return callAPI;
    }

    public static JSONObject updateEmployeeDetailsFromAPI( JSONObject input ){
        JSONObject r = new JSONObject();
        Boolean callAPI = canUpdateEmployeeDetailsFromAPI( input );

        if( callAPI && input.has("id") ) {
            r = nwpConnectToAPI.get_staff_details(input);
            if (r.has("grade_level")
                    && !r.getString("grade_level").isEmpty()) {
                //create grade level, update grade level
                String gradeID = GlobalFunctions.md5(r.getString("grade_level"));
                String gradeLevel = r.getString("grade_level");

                //check for existing grade level
                JSONObject $qs = new JSONObject();
                $qs.put("id", gradeID);
                $qs.put("table", nwpAccessRoles.grade_level_table);
                JSONObject gradeData = GlobalFunctions.get_record($qs);

                JSONObject li;
                JSONObject edep;

                if (gradeData.has("id") && !gradeData.getString("id").isEmpty()) {
                    r.put("saved_grade_level", gradeData.getString("id"));
                } else {
                    //none found, create grade level
                    JSONObject gradeLevelFields = new JSONObject();
                    edep = GlobalFunctions.get_json(nwpAccessRoles.grade_level_table);
                    if (edep.has("fields")) {

                        GlobalFunctions.delete_records( new JSONObject()
                                .put("table", nwpAccessRoles.grade_level_table )
                                .put("where", " [id] = '" + gradeID + "' " )
                        );

                        gradeLevelFields = edep.getJSONObject("fields");

                        li = new JSONObject();

                        li.put( "tmp_id", gradeID);
                        li.put(gradeLevelFields.getString("name"), gradeLevel);
                        li.put(gradeLevelFields.getString("source"), "api");

                        JSONObject a = new JSONObject();

                        a.put("table", nwpAccessRoles.grade_level_table);
                        a.put("todo", "create_new_record"); // create_new_record
                        a.put("show_view_details", false );
                        a.put("post_data", li);

                        JSONObject $rr = nwpDataTable.saveDataForm(a);

                        if ($rr.has("error") && !$rr.getString("error").isEmpty()) {
                            r.put("error", $rr.has("error"));
                        } else {
                            r.put("saved_grade_level", gradeID);
                        }
                    } else {
                        r.put("error", "Table fields was not found: " + nwpAccessRoles.grade_level_table);
                    }
                }

                //update grade level
                if (r.has("saved_grade_level")) {
                    if (input.has("grade_level") && input.equals( r.getString("saved_grade_level") )) {
                        //no need for update, nothing has changed
                    } else {

                        JSONObject usersFields = new JSONObject();
                        edep = GlobalFunctions.get_json(nwpAccessRoles.users_table);
                        if (edep.has("fields")) {
                            usersFields = edep.getJSONObject("fields");

                            li = new JSONObject();

                            li.put(usersFields.getString("grade_level"), r.getString("saved_grade_level") );

                            JSONObject a = new JSONObject();

                            if( input.has("table_settings") ){
                                a = input.getJSONObject("table_settings");
                            }
                            a.put("table", nwpAccessRoles.users_table );
                            a.put("id", input.getString("id") );
                            a.put("todo", "edit");
                            a.put("post_data", li);

                            JSONObject $rr = nwpDataTable.saveDataForm(a);

                            if ($rr.has("error") && !$rr.getString("error").isEmpty()) {
                                r.put("error", $rr.has("error"));
                            } else {
                                r = $rr;
                            }
                        } else {
                            r.put("error", "Table fields was not found: " + nwpAccessRoles.users_table);
                        }

                    }
                }


            }
        }

        return r;
    }

    public static JSONObject GetManageCapabilitiesData(){
        JSONObject r = new JSONObject();

        r.put("frontend_tabs", NavBar.theModules() );
        r.put("frontend_menus", NavBar.theMainMenu() );
        r.put("frontend_menus_function", NavBar.theSubMenu() );
        r.put("status_table", nwpWorkflow.getWorkflowSettings( new JSONObject().put("source", "basic_crud") ) );
        r.put("home_menu", NavBar.theHomeMenu() );
        r.put("modules", new JSONObject() );

        r.put("basic_crud", GlobalFunctions.getProjectTables( new JSONObject().put("basic_crud", 1 ) ) );

        return r;
    }

    public static JSONObject saveAccessRole(JSONObject jget, JSONObject jpost ){
        JSONObject r = new JSONObject();
        JSONObject a = new JSONObject();
        String error = "";

        if( jget.has("nwp2_action") && jpost.has("id") && jpost.has("name") && jpost.has("data") ){
            if( ! jpost.getString("name").isEmpty() && ! jpost.getString("data").isEmpty() ) {
                JSONObject transformedPostData = new JSONObject();

                if( jpost.getString("id").isEmpty() ){
                    a.put( "todo", "create_new_record" );
                }else{
                    a.put( "todo", "edit" );
                    a.put( "id", jpost.getString("id") );
                }
                JSONObject dep = GlobalFunctions.get_json( jget.getString("nwp2_action") );
                JSONObject fields = dep.has( "fields" ) ? dep.getJSONObject( "fields" ) : new JSONObject();
                if( fields.length() > 0 ) {
                    for( int i = 0; i < fields.length(); i++ ){
                        String k = fields.names().getString(i);

                        if( fields.has(k) && jpost.has( k ) ){
                            transformedPostData.put( fields.getString(k) , jpost.getString( k ) );
                        }
                    }


                    if( transformedPostData.length() > 0 ){
                        if( fields.has("role_type") && ! transformedPostData.has( fields.getString("role_type") ) ) {
                            transformedPostData.put(fields.getString("role_type"), "users");
                        }
                    }else{
                        error = "<h4>No Input Detected</h4>Unable to save access role";
                    }
                }else{
                    error = "Server Error: Unable to Read Access Role JSON Config File";
                }

                if( error.isEmpty() ) {
                    a.put("table", jget.getString("nwp2_action"));
                    a.put("post_data", transformedPostData);

                    r = nwpDataTable.saveDataForm(a);
                    //System.out.println(r);
                    if (r.has("saved_record")) {
                        //GlobalFunctions.app_notice_type = "success";
                        //GlobalFunctions.app_notice = "<h4>Changes Successfully Saved</h4>";
                        GlobalFunctions.app_reload_datatable = false;

                        //save access role status
                        if (r.getJSONObject("saved_record").has("data") && r.getJSONObject("saved_record").has("id") ) {
                            JSONObject data = new JSONObject( r.getJSONObject("saved_record").getString("data") );
                            JSONArray lineItems = new JSONArray();
                            //Integer serial = 0;

                            if( data.has("status") && data.getJSONObject("status").length() > 0 ){
                                for( int i = 0; i < data.getJSONObject("status").length(); i++ ){
                                    String k1 = data.getJSONObject("status").names().getString(i);
                                    JSONObject v1 = data.getJSONObject("status").getJSONObject(k1);
                                    String[] k1a = k1.split(":::");

                                    if( k1a.length > 2 && v1.length() > 0 ){
                                        if( k1a[0].equals( nwpAccessRoles.access_role_status ) ){
                                            JSONObject defData = new JSONObject();
                                            defData.put("access_role_id", r.getJSONObject("saved_record").getString("id") );
                                            defData.put("reference_table", k1a[1] );
                                            defData.put("reference", k1a[2] );

                                            for( int i2 = 0; i2 < v1.length(); i2++ ){
                                                String k2 = v1.names().getString(i2);
                                                if( ! k2.equals("cancel") ) {
                                                    JSONObject defDataLine = new JSONObject(defData.toString());
                                                    defDataLine.put("status", k2);
                                                    //++serial;
                                                    // defDataLine.put("id", r.getJSONObject("saved_record").getString("id") +"b"+ serial );
                                                    lineItems.put(defDataLine);
                                                }
                                            }

                                        }
                                    }
                                }
                            }
							
							JSONObject aData = new JSONObject();
							aData.put("table", nwpAccessRoles.access_role_status );
							aData.put("conditions", new JSONObject().put("access_role_id", r.getJSONObject("saved_record").getString("id") ) );
							GlobalFunctions.deleteQuery( aData );

                            if( lineItems.length() > 0 ){
                                aData = new JSONObject();
                                aData.put("line_items", lineItems);
                                aData.put("table", nwpAccessRoles.access_role_status );
                                JSONObject r2 = nwpDataTable.save_line_items( aData );
                            }
                        }
                    }
                }
            }else{
                error = "<h4>Invalid Access Role Parameters</h4>Provide valid name & capabilities for the access role";
            }
        }else{
            error = "Server Error: Undefined Access Role Parameters (id, name, data)";
        }

        if( ! error.isEmpty() ) {
            GlobalFunctions.app_notice_only = true;
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice = error;
        }
        return r;
    }

    public static JSONObject neutralJ(String func_name, JSONObject object ){
        JSONObject r = new JSONObject();
        for( int i = 0; i < object.length(); i++ ){
            String k = object.names().getString(i);
            r.put( k, func_name + object.get(k).toString() );
        }
        return r;
    }

    public static String  formatAccessRoleCapabilities(JSONObject $val, JSONArray $selected, JSONObject $dsel ){
        int $serial = 0;
        String r = "";
        JSONObject $xval = new JSONObject();

        try {

            for (int i = 0; i < $val.length(); i++) {
                String $mname = $val.names().getString(i);
                JSONObject $sval = $val.getJSONObject($mname);

                if ($sval.has("function_name")) {
                    if (($serial % 4) == 0) {
                        r += "</div><br /><div class=\"row\">";
                    }
                    ++$serial;

                    String $sel = "";
//                if( $selected.has($sval.getString("id") ) && ! $selected.isNull($sval.getString("id")) ){
                    if ($selected.toString().contains($sval.getString("id"))) {
                        $sel = " checked=\"checked\" ";
                    }

                    String $saved_key2 = "";
                    String $saved_key = "accessible_functions";

                    String $input_name = "accessible_functions[]";
                    if ($sval.has("input_name") && !$sval.getString("input_name").isEmpty()) {
                        $input_name = $sval.getString("input_name");
                    }

                    String $input_attr = "";
                    if ($sval.has("input_attr") && !$sval.getString("input_attr").isEmpty()) {
                        $input_attr = $sval.getString("input_attr");
                    }

                    if ($sval.has("input_key") && !$sval.getString("input_key").isEmpty()) {
                        $saved_key = $sval.getString("input_key");
                    }

                    if ($sval.has("input_key2") && !$sval.getString("input_key2").isEmpty()) {
                        $saved_key2 = $sval.getString("input_key2");
                    }

                    if (!$saved_key2.isEmpty()) {
                        if ($dsel.has($saved_key) && $dsel.getJSONObject($saved_key).has($saved_key2) && $dsel.getJSONObject($saved_key).getJSONObject($saved_key2).has($sval.getString("id"))) {
                            $sel = " checked=\"checked\" ";
                        }
                    } else {
                        if ($dsel.has($saved_key) && $dsel.getJSONObject($saved_key).has($sval.getString("id"))) {
                            $sel = " checked=\"checked\" ";
                        }
                    }

                    r += "<div class=\"col-md-3\">\n" +
                            "    <label style=\"font-weight:normal;\"><input type=\"checkbox\" name=\"" + $input_name + "\" value=\"" + $sval.getString("id") + "\" " + $sel + "class=\"" + ($sval.has("class") ? $sval.getString("class") : "") + "\" class=\"" + ($sval.has("class") ? $sval.getString("class") : "") + "\"" + $input_attr + " /> " + $sval.getString("function_name").toUpperCase() + "</label>\n" +
                            "</div>";
                    //$val.remove($mname);
                }else{
                    $xval.put( $mname, $sval );
                }

            }
        }catch (Exception e){
            System.out.println( e.getMessage() );
            r += e.getMessage();
        }
        // System.out.println( $xval );

        if( $xval.length() > 0 ){
            for( int i = 0;  i < $xval.length(); i++ ){
                String $mname = $xval.names().getString(i);
                JSONObject $sval = $xval.getJSONObject($mname);

                r += "</div><br /><div class=\"row\">\n" +
                        "                    <div class=\"col-md-10 col-md-offset-1\">\n" +
                        "                        <i><strong>"+ $mname +"</strong></i><br />\n" +
                        "\n" +
                        "                         <div class=\"row\">\n";

                try {
                    r += formatAccessRoleCapabilities2( $sval, $selected, $dsel );
                }catch (Exception e){
                    System.out.println( $mname + ": " + e.getMessage() );
                    r += $mname;
                }

                r += "</div>\n" + "</div><br />";

            }
        }


        return r;
    }

    public static String  formatAccessRoleCapabilities2( JSONObject $val, JSONArray $selected, JSONObject $dsel ){
        int $serial = 0;
        String r = "";

        for( int i = 0; i < $val.length(); i++ ){
            String $mname = $val.names().getString(i);
            JSONObject $sval = $val.getJSONObject($mname);

            if( $sval.has("function_name") ){
                if( ( $serial % 4 ) == 0 ){
                    r += "</div><br /><div class=\"row\">";
                }
                ++$serial;

                String $sel = "";
//                if( $selected.has($sval.getString("id") ) && ! $selected.isNull($sval.getString("id")) ){
                if( $selected.toString().contains($sval.getString("id")) ){
                    $sel = " checked=\"checked\" ";
                }

                String $saved_key2 = "";
                String $saved_key = "accessible_functions";

                String $input_name = "accessible_functions[]";
                //String $input_name = "";
                if( $sval.has("input_name") && ! $sval.getString("input_name").isEmpty() ){
                    $input_name = $sval.getString("input_name");
                }

                String $input_attr = "";
                if( $sval.has("input_attr") && ! $sval.getString("input_attr").isEmpty() ){
                    $input_attr = $sval.getString("input_attr");
                }

                if( $sval.has("input_key") && ! $sval.getString("input_key").isEmpty() ){
                    $saved_key = $sval.getString("input_key");
                }

                if( $sval.has("input_key2") && ! $sval.getString("input_key2").isEmpty() ){
                    $saved_key2 = $sval.getString("input_key2");
                }

                if( ! $saved_key2.isEmpty() ){
                    if( $dsel.has($saved_key) && $dsel.getJSONObject($saved_key).has($saved_key2) && $dsel.getJSONObject($saved_key).getJSONObject($saved_key2).has($sval.getString("id") ) ){
                        $sel = " checked=\"checked\" ";
                    }
                }else{
                    if( $dsel.has($saved_key) && $dsel.getJSONObject($saved_key).has($sval.getString("id") ) ){
                        $sel = " checked=\"checked\" ";
                    }
                }

                r += "<div class=\"col-md-3\">\n" +
                        "    <label style=\"font-weight:normal;\"><input type=\"checkbox\" name=\""+ $input_name +"\" value=\""+ $sval.getString("id") +"\" "+ $sel +"class=\""+ ( $sval.has("class") ? $sval.getString("class") : "" ) +"\" class=\""+ ( $sval.has("class") ? $sval.getString("class") : "" ) +"\""+  $input_attr +" /> "+   $sval.getString("function_name").toUpperCase() +"</label>\n" +
                        "</div>";
            }

        }

        return r;
    }

    public static String getUserForm(JSONObject jget, JSONObject jpet){
        String h = "";

        if( jget.has("nwp2_source") ) {
            JSONObject jdata = new JSONObject();
            JSONObject formData = new JSONObject();
            String htmlReplacementSelector = "";
            jdata.put("table", jget.getString("nwp2_source"));
            jdata.put("todo", "create_new_record");
            //jdata.put("popup", true);

            if (jget.has("html_replacement_selector")) {
                htmlReplacementSelector = jget.getString("html_replacement_selector");
            }

            if (jpet.has("data")) {
                formData = new JSONObject(jpet.getString("data"));
            }

            if (formData.has("id")) {
                //enable to use md5 hash of ad username as id in users table
                //jdata.put("tmp_id", formData.getString("id"));

                JSONObject dep = GlobalFunctions.get_json(jdata.getString("table"));
                if (dep.has("labels") && dep.has("form_order")) {
                    JSONObject labels = dep.getJSONObject("labels");
                    JSONObject labels2 = new JSONObject();
                    String $key1 = "";
                    JSONObject $line;

                    if (labels.names().length() > 0) {

                        for (int i2 = 0; i2 < labels.names().length(); i2++) {
                            $key1 = labels.names().getString(i2);
                            $line = labels.getJSONObject($key1);
                            if (formData.has($line.getString("field_identifier"))) {
                                $line.put("value", formData.getString($line.getString("field_identifier")));
                            }
                            if( $line.has("no_ad_value") && $line.has("value") ){
                                $line.remove("value");
                            }

                            if ($line.getString("field_identifier").equals("email")) {

                            } else if ($line.getString("field_identifier").equals("grade_level")) {
                                if( $line.has("no_create") ){
                                    $line.put("hidden_records", true);
                                }
                            } else if ($line.getString("field_identifier").equals("employee_no")) {
                                if( $line.has("no_create") ){
                                    $line.put("hidden_records", true);
                                }
                            } else if ($line.getString("field_identifier").equals("phone_number")) {
                            } else if ($line.getString("field_identifier").equals("username")) {
                                $line.put("hidden_records_css", true);
                            } else if ($line.getString("field_identifier").equals("name")) {
                                $line.put("hidden_records_css", true);
                            } else if ($line.getString("field_identifier").equals("role")) {
                            } else if ($line.getString("field_identifier").equals("prole")) {
                            } else if ($line.getString("field_identifier").equals("prole_expiry_date")) {
                            } else if ($line.getString("field_identifier").equals("department")) {

                            } else if ($line.getString("field_identifier").equals("group")) {
                                if( $line.has("no_create") ){
                                    $line.put("hidden_records", true);
                                }
                            } else if ($line.getString("field_identifier").equals("team")) {
                                if( $line.has("no_create") ){
                                    $line.put("hidden_records", true);
                                }
                            } else if ($line.getString("field_identifier").equals("status")) {
                                $line.put("value", "active");
                                $line.put("hidden_records_css", true);
                            } else if ($line.getString("field_identifier").equals("locked")) {
                                $line.put("value", "no");
                                $line.put("hidden_records_css", true);
                            } else {
                                $line.put("hidden_records", true);
                            }
                        }


                    }
                }
                jdata.put("html_replacement_selector", htmlReplacementSelector);
                jdata.put("table_settings", dep);


                //jdata.put( "title", "Add New User from Active Directory" );
                h = "<h4>New User Assignment [rst]</h4>";

                h += nwpDataTable.getDataForm(jdata);
            }else{
               h = "Unable to Retrieve User ID" ;
            }
        }else{
            h = "No table specified";
        }
        return h;
    }
}
