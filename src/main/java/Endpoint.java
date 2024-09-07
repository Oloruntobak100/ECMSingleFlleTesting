
import codes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.ldap.LdapContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet(name = "Endpoint", value = "/Endpoint")
@MultipartConfig(fileSizeThreshold=1024*1024*12, // 2MB
        maxFileSize=1024*1024*12,      // 16MB
        maxRequestSize=1024*1024*50)   // 50MB

public class Endpoint extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //private Gson gson = new Gson();
    private Boolean closeConnection = true;

    public Endpoint() {
        super();
    }

    protected void testdoPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String currentDir = System.getProperty("user.dir");

        ServletContext app = getServletContext();
        GlobalFunctions.app_file_seperator = File.separator;
        GlobalFunctions.app_path = app.getRealPath( GlobalFunctions.app_file_seperator );

        // Print the current directory path
        //System.out.println("Current directory: " + currentDir);
        String pace = GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "config.json");
        req.setAttribute("json", "<h1>Unknown Error</h1>" + currentDir + "<br>" + GlobalFunctions.app_path + "<br>" + pace );
        req.getRequestDispatcher("/ajax-request.jsp").forward(req, res);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        HttpSession session = req.getSession();
        if( ! Objects.isNull( session.getAttribute("glo_var") ) ) {
            GlobalFunctions.glo_var = new JSONObject(session.getAttribute("glo_var").toString());
        }

        GlobalFunctions.app_user = "";
        if( ! Objects.isNull( session.getAttribute("user_data") ) ) {
            GlobalFunctions.app_user_data = new JSONObject(session.getAttribute("user_data").toString());
        }else{
            GlobalFunctions.app_user_data = new JSONObject();
        }
        if( GlobalFunctions.app_user_data.has("id") ){
            GlobalFunctions.app_user = GlobalFunctions.app_user_data.getString("id");
        }

        if( ! Objects.isNull( session.getAttribute("redirect_data") ) ) {
            GlobalFunctions.app_redirect_data = new JSONObject(session.getAttribute("redirect_data").toString());
        }
        //System.out.println( GlobalFunctions.app_user_data );

        /*LdapContext test;
        try{
           // test = ActiveDirectory.getConnection("GLORIA","smooth-42-home", "MAYBEACHTECH.local", "WIN-LVK0HO7H2U7");
            test = ActiveDirectory.getConnection("JAMES E. MAFIA","smooth-42-home", "MAYBEACHTECH.local", "WIN-LVK0HO7H2U7");
            ActiveDirectory.getUser("JAMES E. MAFIA", test);
            //System.out.println(ActiveDirectory.getUsers(test) );
            //System.out.println(user);
            //"displayName", mail", "department"", mobile", "telephoneNumber"
            System.out.println("Connection Successful!!!");
            test.close();
        }catch ( Exception e ){
            System.out.println( e );
        }*/

        String action = req.getParameter("action");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String callback = req.getParameter("callback");
        String callback_start = req.getParameter("callback_start");

        ServletContext app = getServletContext();
        GlobalFunctions.app_file_seperator = File.separator;
        GlobalFunctions.app_path = app.getRealPath( GlobalFunctions.app_file_seperator );
        if( ! GlobalFunctions.app_path.endsWith( GlobalFunctions.app_file_seperator ) ){
            GlobalFunctions.app_path += GlobalFunctions.app_file_seperator;
        }
        GlobalFunctions.app_request_source = req.getHeader("Origin") + req.getRequestURI();
        GlobalFunctions.initialize();



        //retrieve all GET parameters & convert to JSONObject
        //System.out.println( paramJson( req.getQueryString() ) );
        JSONObject jsonGET = paramJson( req.getQueryString() );
        JSONObject jsonPOST = new JSONObject();

        Enumeration keys = req.getParameterNames();
        while (keys.hasMoreElements() )
        {
            String key = (String)keys.nextElement();

            //System.out.println(key);
            // If the same key has multiple values (check boxes)
            String[] valueArray = req.getParameterValues(key);
            if( valueArray.length > 1 ){
                /*for(int i = 0; i > valueArray.length; i++){
                    System.out.println("VALUE ARRAY" + valueArray[i]);
                }*/
                jsonPOST.put(key, valueArray );
            }else{
                //To retrieve a single value
                String value = req.getParameter(key);
                //System.out.println(value);

                if( ! ( jsonGET.has( key ) && jsonGET.getString( key ).equals( value ) ) ){
                    jsonPOST.put(key, value );
                }
            }
        }
        //System.out.println(jsonPOST);
        //System.out.println(jsonGET);
        jsonGET.put("id", req.getParameter("id") );


        Integer use_jsp = 1;
        String content = "";
        String container = "mis-container";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action );

        RequestDispatcher rd = req.getRequestDispatcher("Dashboard");
        String jspFile = "home.jsp";
        String msg = "";
        String msg_type = "error";
        String rstatus = "new-status";

        String [] jsf = new String[8];
        Boolean checkAuth = true;
        Boolean addDataTableJS = false;

        try {
            switch (action) {
                case "submit_form":
                case "datatable_button":
                case "display_nav_bar":
                case "dashboard":
                case "display_menu":
                case "display_sub_menu":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        JSONObject jr = new JSONObject();
                        jr.put("filename", "");

                        jsf[4] = "set_function_click_event";
                        jsf[5] = "prepare_new_record_form_new";
                        if (!Objects.isNull(callback) && !callback.isEmpty()) {
                            jsf[6] = callback;
                        }
                        if (!Objects.isNull(callback_start) && !callback_start.isEmpty()) {
                            jsf[0] = callback_start;
                        }

                        switch (action) {
                            case "display_nav_bar":
                                //jr = NavBar.GetContent( req.getParameter("current_tab") );
                                jspFile = "side-menu.jsp";
                                container = "dash-board-main-content-area";
                                String container2 = NavBar.GetMenuContainerID(jsonGET.getString("current_tab"));
                                String todo2 = "open_homepage";

                                HttpServletRequest req2 = req;
                                req2.setAttribute("nwp_gdata", jsonGET);
                                req2.setAttribute("nwp_pdata", jsonPOST);
                                req2.setAttribute("html_replacement_selector", container2);

                                if (GlobalFunctions.app_redirect_data.has("url_params")
                                        && GlobalFunctions.app_redirect_data.getJSONObject("url_params").has("nwp_jsp")
                                        && !GlobalFunctions.app_redirect_data.getJSONObject("url_params").getString("nwp_jsp").isEmpty()
                                ) {
                                    req2.setAttribute("url_params", GlobalFunctions.app_redirect_data.getJSONObject("url_params"));
                                    todo2 = GlobalFunctions.app_redirect_data.getJSONObject("url_params").getString("nwp_jsp");
                                }
                                req.setAttribute("default_content", "");

                                JSONObject jf2 = getViewName(new JSONObject()
                                                .put("action", action)
                                                .put("nwp2_action", action)
                                                .put("nwp2_todo", todo2)
                                                .put("todo", "")
                                        , action);

                                if (jf2.getBoolean("hasView")) {
                                    String jspFile2 = jf2.getString("jspFile");
                                    req2.setAttribute("filename", jspFile2);
                                    JSONObject reqJson = new JSONObject(getJSPResponse(req2, res, jspFile2));

                                    if (reqJson.has("nwp_redirect") && reqJson.getBoolean("nwp_redirect")) {
                                        jf2 = getViewName(reqJson, reqJson.has("action") ? reqJson.getString("action") : action);

                                        jspFile2 = jf2.getString("jspFile");

                                        if (jf2.getBoolean("hasView")) {
                                            req2 = req;
                                            req2.setAttribute("nwp_gdata", reqJson);
                                            req2.setAttribute("filename", jspFile2);
                                            req.setAttribute("default_content", getJSPResponse(req2, res, jspFile2));
                                        }

                                    } else {
                                        req.setAttribute("default_content", reqJson.has("html") ? reqJson.getString("html") : "");
                                    }
                                }

                                if (GlobalFunctions.app_redirect_data.has("url_params")) {
                                    GlobalFunctions.app_redirect_data.remove("url_params");
                                }

                                break;
                            case "dashboard":
                                jsf[6] = "$.fn.cCallBack.clickActiveTab";
                                break;
                            case "submit_form":
                            case "datatable_button":
                            case "display_menu":
                            case "display_sub_menu":
                                String post_id = req.getParameter("id");
                                String post_ids = req.getParameter("ids");
                                container = req.getParameter("html_replacement_selector");
                                jsonGET.put("nwp_pid", post_id);
                                jsonGET.put("nwp_pids", post_ids);

                                JSONObject jf = getViewName(jsonGET, action);
                                addDataTableJS = jf.getBoolean("addDataTableJS");
                                jspFile = jf.getString("jspFile");
                                msg = jf.getString("msg");


                                break;
                        }

                        if (!jr.getString("filename").isEmpty()) {
                            jspFile = jr.get("filename").toString();
                        }

                        String path1 = GlobalFunctions.app_path + jspFile;
                        File dViewFile = new File(path1);
                        if (!dViewFile.exists()) {
                            //System.out.println( "no file" );
                            msg = "<h4>Non-existent View File</h4>" + jspFile + "<br /><br />{Login.java::doPost}";
                            jspFile = "no-view-file.jsp";
                        }
                    /*System.out.println( "xxx" );
                    System.out.println( req.getAttribute("default_content") );
                    System.out.println( jspFile );*/

                        req.setAttribute("msg", msg);
                        req.setAttribute("msg_type", msg_type);
                        req.setAttribute("filename", jspFile);
                        req.setAttribute("nwp_gdata", jsonGET);
                        req.setAttribute("nwp_pdata", jsonPOST);

                        Boolean useJSPFile = true;
                        if( jsonGET.has("rst") && GlobalFunctions.app_data.has("use_rst") && GlobalFunctions.app_data.getBoolean("use_rst") ) {
                            switch( jsonGET.getString("rst") ){
                                case "user_form":
                                    useJSPFile = false;
                                    content = nwpAccessRoles.getUserForm( jsonGET, jsonPOST );
                                    break;
                            }
                        }
                        if( useJSPFile ) {
                            content = getJSPResponse(req, res, jspFile);
                        }

                    /*HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(res) {
                        private final StringWriter sw = new StringWriter();

                        @Override
                        public PrintWriter getWriter() throws IOException {
                            return new PrintWriter(sw);
                        }

                        @Override
                        public String toString() {
                            return sw.toString();
                        }
                    };

                    if( GlobalFunctions.nwp_development_mode ){
                        req.getRequestDispatcher(jspFile).include(req, responseWrapper);
                        content = responseWrapper.toString();
                    }else {
                        try {
                            req.getRequestDispatcher(jspFile).include(req, responseWrapper);
                            content = responseWrapper.toString();
                        } catch (Exception e) {
                            content = "<h4>" + jspFile + "</h4>" + e.getMessage();
                        }
                    }*/

                        jsonObject.put("javascript_functions", jsf);
                    }
                    break;
                case "display_table":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        use_jsp = 0;
                        nwpDataTable dtb = new nwpDataTable();
                        //System.out.print( dtb.ajaxServerData() );

                        req.setAttribute("json", dtb.ajaxServerData(new JSONArray(req.getParameter("dt"))));
                        this.closeConnection = false;
                    }
                    break;
                case "column_toggle":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        use_jsp = 0;
                        nwpDataTable dtb_c = new nwpDataTable();
                        //System.out.print( "gream" );
                        if (jsonGET.has("column_toggle_table") && jsonGET.has("column_toggle_name") && jsonGET.has("column_toggle_num")) {
                            req.setAttribute("json", dtb_c.hideShowColumn(jsonGET));
                        }
                    }
                    break;
                case "upload":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        use_jsp = 0;
                        //System.out.print( "gream" );
                        if (jsonGET.has("qqfile")) {
                            //System.out.print( jsonGET.getString("qqfile") );

                            String[] fpt = jsonGET.getString("qqfile").replace(".", ":::").split(":::");
                            //System.out.println(fpt);

                            if (fpt.length > 0) {
                                //final String UPLOAD_DIRECTORY = "C:" + File.separator + "uploads";
                                final String UPLOAD_DIRECTORY = GlobalFunctions.app_file_upload_dir;
                                // constructs path of the directory to save uploaded file
                                String uploadFilePath = UPLOAD_DIRECTORY;

                                // creates the save directory if it does not exists
                                File fileSaveDir = new File(uploadFilePath);
                                if (!fileSaveDir.exists()) {
                                    fileSaveDir.mkdirs();
                                }
                                //System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());

                                //Get all the parts from request and write it to the file on server

                                for (Part part : req.getParts()) {
                                    //String fileName = getFileName(part);
                                    //System.out.println( part.getSize() );
                                    if (part.getSize() <= Long.valueOf(String.valueOf(GlobalFunctions.max_upload_mb * 1024 * 1024))) {
                                        String fileName = GlobalFunctions.GetNewID("uf");


                                        part.write(uploadFilePath + File.separator + fileName + "." + fpt[fpt.length - 1]);
                                        Long fsize = Files.size(Paths.get(uploadFilePath + File.separator + fileName + "." + fpt[fpt.length - 1]));
                                        if (fsize > 0) {
                                            jsonGET.put("size", fsize).put("saved_name", fileName).put("ext", fpt[fpt.length - 1]).put("saved_path", uploadFilePath + File.separator);
                                        } else {
                                            jsonGET.put("error", "Unable to save uploaded file on the server");
                                        }
                                    } else {
                                        jsonGET.put("error", "Unable to save uploaded file on the server. File Size is larger than " + String.valueOf(GlobalFunctions.max_upload_mb) + "MB");
                                    }
                                    //System.out.println(fileName);
                                }
                            } else {
                                jsonGET.put("error", "File name was not received by the server");
                            }
                        } else {
                            jsonGET.put("error", "Undefined upload file name");
                        }
                        req.setAttribute("json", nwpDataTable.saveUploadedFiles(jsonGET));
                    }
                    break;
                case "get_select2":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        use_jsp = 0;
                        String term = req.getParameter("term");
                        String searchType = "";
                        JSONObject $qr2 = new JSONObject();
                        $qr2.put("items", new JSONArray());

                        //System.out.print( "gream" );
                        if (jsonGET.has("todo") && !jsonGET.getString("todo").isEmpty()) {
                            searchType = jsonGET.getString("todo");
                        }

                        switch (searchType) {
                            case "search_active_directory":
                                //System.out.println(searchType);
                                //System.out.println(term);
                                if (!term.isEmpty()) {
                                    LdapContext activeDirectoryConnection;
                                    try {
                                        $qr2.put("items", ActiveDirectory.getADUsers(term));
                                        if( ! ActiveDirectory.adError.isEmpty() ){
                                            $qr2.getJSONArray("items").put(new JSONObject().put("id", "0").put("text", ActiveDirectory.adError ));
                                        }
                                    /*activeDirectoryConnection = ActiveDirectory.getConnection("GLORIA", "smooth-42-home", GlobalFunctions.app_ad_domain, GlobalFunctions.app_ad_server);

                                    $qr2.put("items", ActiveDirectory.getUsers(activeDirectoryConnection, term));

                                    activeDirectoryConnection.close();*/
                                    } catch (Exception e) {
                                        //System.out.println(e);
                                        $qr2.getJSONArray("items").put(new JSONObject().put("id", "0").put("text", "Unable to Connect to AD" + e.getMessage()));
                                    }

                                }
                                break;
                            case "custom_select2":
                                JSONObject jf = getViewName(jsonGET, action);
                                jspFile = jf.getString("jspFile");
                                $qr2.put("view", jspFile);
                                $qr2.put("msg", jf.getString("msg"));

                                if (jf.getBoolean("hasView")) {
                                    req.setAttribute("filename", jspFile);
                                    req.setAttribute("nwp_gdata", jsonGET);
                                    req.setAttribute("nwp_pdata", jsonPOST);

                                /*HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(res) {
                                    private final StringWriter sw = new StringWriter();

                                    @Override
                                    public PrintWriter getWriter() throws IOException {
                                        return new PrintWriter(sw);
                                    }

                                    @Override
                                    public String toString() {
                                        return sw.toString();
                                    }
                                };

                                req.getRequestDispatcher(jspFile).include(req, responseWrapper);
                                $qr2.put("items", new JSONArray( responseWrapper.toString() ) );*/
                                    $qr2.put("items", new JSONArray(getJSPResponse(req, res, jspFile)));
                                }
                                break;
                            default:
                                if (jsonGET.has("nwp2_action") && !jsonGET.getString("nwp2_action").isEmpty()) {
                                    JSONObject $qs = new JSONObject();
                                    JSONObject $sf = new JSONObject();
                                    JSONObject dep = GlobalFunctions.get_json(jsonGET.getString("nwp2_action"));

                                    if (dep.has("fields")) {
                                        String $where = "";
                                        JSONObject $qf = dep.getJSONObject("fields");
                                        $qs.put("fields", $qf);

                                        $sf.put("name", "text");
                                        $qs.put("select_fields", $sf);

                                        if (!term.isEmpty()) {
                                            if ($qf.has("name")) {
                                                $where += " AND [" + $qf.getString("name") + "] LIKE '%" + term + "%' ";
                                            }
                                        }

                                        if (jsonPOST.has("filter_fields") && !jsonPOST.getString("filter_fields").isEmpty()) {
                                            String[] filter_fields = jsonPOST.getString("filter_fields").split(",");
                                            for (String filter_field : filter_fields) {
                                                if ($qf.has(filter_field.trim()) && jsonPOST.has(filter_field.trim()) && !jsonPOST.getString(filter_field.trim()).isEmpty()) {
                                                    $where += " AND [" + $qf.getString(filter_field.trim()) + "] = '" + jsonPOST.getString(filter_field.trim()) + "' ";
                                                }
                                            }
                                        }

                                        if (!$where.isEmpty()) {
                                            $qs.put("where", $where);
                                        }

                                        $qs.put("table", jsonGET.getString("nwp2_action"));
                                        $qs.put("limit", "20");
                                        JSONObject $qr = new JSONObject();

                                        $qr = GlobalFunctions.get_records($qs);
                                        if ($qr.has("row_count") && $qr.has("row") && $qr.getInt("row_count") > 0) {
                                            $qr2.put("items", $qr.getJSONArray("row"));
                                        } else {
                                            $qr2.put("items", new JSONArray());
                                        }

                                    }
                                }
                                break;
                        }

                        $qr2.put("do_not_reload_table", 1);
                        req.setAttribute("json", $qr2);
                    }
                    break;
                case "treeview":
                    if (GlobalFunctions.app_user_data.has("id")) {
                        use_jsp = 0;
                        JSONObject rj = NavBar.GetMenu(jsonGET.getString("current_tab"), true);
                        if (rj.has("tree")) {
                            //System.out.println(  rj.get("tree") );
                            req.setAttribute("json", rj.get("tree"));
                        }
                    }
                    break;
                case "sign_out":
                    //log action
                    GlobalFunctions.app_developer = false;
                    GlobalFunctions.app_user_data = new JSONObject();
                    session.setAttribute("user_data", GlobalFunctions.app_user_data);
                    break;
                case "authenticate":
                    checkAuth = false;
                    JSONObject authUser = nwpAccessRoles.AuthenticateUser(jsonPOST);
                    if (authUser.has("id") && !authUser.getString("id").isEmpty()) {
                        //redirect to dashboard
                        session.setAttribute("user_data", GlobalFunctions.app_user_data);
                        jsonObject.put("redirect_url", "main.html");
                    }
                    break;
                default:
                    container = "registered-company";
                    checkAuth = false;

                    try {
                        JSONObject appData = GlobalFunctions.getApp();
                        if (appData.has("company_name") && appData.has("version")) {
                            String moreNote = "";

                            if( GlobalFunctions.nwp_development_mode &&
                                    GlobalFunctions.app_data.has("no_active_directory_pwd") &&
                                    ! GlobalFunctions.app_data.getString("no_active_directory_pwd").isEmpty() ){
                                moreNote += "<br>SKIP AD CONN";
                            }

                            jsonObject.put("html_replacement_selector_two", "#year-of-app");
                            jsonObject.put("html_replacement_two", appData.getString("version") + moreNote);
                            content = appData.getString("company_name");
                            //jsonObject.put("user_details", "#year-of-app");
                            if (GlobalFunctions.app_user_data.has("id")) {
                                session.setAttribute("redirect_data", new JSONObject().put("url_params", jsonPOST));
                                jsonObject.put("redirect_url", "main.html");
                            }
                            jsf[2] = "$.fn.pHost.displayUserDetails";
                        } else {
                            content = "Invalid Config File...contact support team";
                        }
                    } catch (Exception e) {
                        content = "Failed to Initialize...contact support team..." + e.getMessage();
                    }
                    break;


            }

            if( GlobalFunctions.app_data.has("log_audit") && GlobalFunctions.app_data.getBoolean("log_audit") ){
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("audit",true)
                                .put("function", action)
                                .put("user", GlobalFunctions.currentUserName )
                                .put("params", jsonGET ),
                        null
                );
            }
        }catch (Exception e){
            GlobalFunctions.app_notice = "Server Error:<br>" + e.getMessage();
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice_only = true;
            jsonObject.put("manual_close", 1 );

            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "ENDPOINT ERROR 1: " + e.getMessage() )
                            .put("input", new JSONObject().put("jsonGET", jsonGET).put("jsonPOST", jsonPOST) )
                            .put("function", "Endpoint.POST" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }

        if( checkAuth ){
            if( ! GlobalFunctions.app_user_data.has("id") ){
                jsonObject.put("redirect_url", "index.jsp");
            }
        }

        if( addDataTableJS || GlobalFunctions.app_load_datatable ){
            jsf[1] = "nwResizeWindow.resizeWindow";
            jsf[2] = "$nwProcessor.recreateDataTables";
            jsf[3] = "$nwProcessor.update_column_view_state";
            jsf[4] = "$nwProcessor.set_function_click_event";
            GlobalFunctions.app_reload_datatable = false;
            jsonObject.put("javascript_functions", jsf );
        }

        if( ! ( GlobalFunctions.app_notice_type.isEmpty() && GlobalFunctions.app_notice.isEmpty() )  ){
            jsonObject.put("typ", GlobalFunctions.app_notice_type );
            jsonObject.put("msg", GlobalFunctions.app_notice );
            jsonObject.put("inf", GlobalFunctions.app_notice );

            jsonObject.put("theme", "note note-danger alert-danger" );
            switch( GlobalFunctions.app_notice_type ){
                case "success":
                    jsonObject.put("theme", "note note-success alert-success" );
                    break;
            }

            if(  GlobalFunctions.app_notice_only ){
                content = "";
                jsonObject.remove("javascript_functions" );
            }
        }


        if( GlobalFunctions.app_popup && ! content.isEmpty() ){
            JSONObject $popdata = new JSONObject();
            $popdata.put("modal_title", GlobalFunctions.app_popup_title );
            $popdata.put("modal_body", content);
            if( ! GlobalFunctions.app_popup_style.isEmpty() ){
                $popdata.put("modal_dialog_style", GlobalFunctions.app_popup_style );
            }
            action = "datatable_popup";

            content = nwpDataTable.getPopup($popdata);
        }else if( GlobalFunctions.app_reload_datatable ){
            jsf[2] = "$nwProcessor.reload_datatable";
            jsonObject.put("javascript_functions", jsf );
        }

        if( use_jsp == 1 ) {
            jsonObject.put("status", rstatus);

            switch (action) {
                case "datatable_popup":
                case "datatable_button":
                    container = "dash-board-main-content-area";
                    if( ! GlobalFunctions.app_popup_handle.isEmpty() ){
                        container = GlobalFunctions.app_popup_handle;
                    }

                    if( ! content.isEmpty() ) {
                        jsonObject.put("html_prepend", content);
                        jsonObject.put("html_prepend_selector", "#" + container);
                    }

                    break;
                default:
                    if( ! content.isEmpty() ) {
                        if( ! GlobalFunctions.app_replace_container.isEmpty() ){
                            container = GlobalFunctions.app_replace_container;
                            GlobalFunctions.app_replace_container = "";
                        }

                        jsonObject.put("html_replacement", content);
                        jsonObject.put("html_replacement_selector", "#" + container);
                    }
                    break;
            }

            if( ! Objects.isNull( GlobalFunctions.app_jsp_json ) ){
                String $jkey;
                if(  GlobalFunctions.app_jsp_json.names().length() > 0 ){
                    for(int i1 = 0; i1 < GlobalFunctions.app_jsp_json.names().length(); i1++){
                        $jkey = GlobalFunctions.app_jsp_json.names().getString(i1);
                        jsonObject.put( $jkey, GlobalFunctions.app_jsp_json.get( $jkey ) );
                    }
                }
            }

            if( jsonGET.has("id") ){
                jsonObject.put("oid", jsonGET.getString("id") );
            }
            if( GlobalFunctions.nwp_development_mode ){
                jsonObject.put("development", GlobalFunctions.nwp_development_mode );
            }
            //String errorMessage="Invalid Credentials, please login again!";
            String json = jsonObject.toString();

            //req.setAttribute("error", errorMessage);
            req.setAttribute("json", json);
            //System.out.println(json);
        }

        session.setAttribute("glo_var", GlobalFunctions.glo_var );

        req.getRequestDispatcher("/ajax-request.jsp").forward(req, res);
        closeConnection();

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        /*HttpSession session = req.getSession();
        if( ! Objects.isNull( session.getAttribute("glo_var") ) ) {
            GlobalFunctions.glo_var = new JSONObject(session.getAttribute("glo_var").toString());
        }

        if( ! Objects.isNull( session.getAttribute("user_data") ) ) {
            GlobalFunctions.app_user_data = new JSONObject(session.getAttribute("user_data").toString());
        }else{
            GlobalFunctions.app_user_data = new JSONObject();
        }*/
        //System.out.println( GlobalFunctions.app_user_data );

        String action = req.getParameter("action");

        ServletContext app = getServletContext();
        GlobalFunctions.app_file_seperator = File.separator;
        GlobalFunctions.app_path = app.getRealPath( GlobalFunctions.app_file_seperator );
        if( ! GlobalFunctions.app_path.endsWith( GlobalFunctions.app_file_seperator ) ){
            GlobalFunctions.app_path += GlobalFunctions.app_file_seperator;
        }
        GlobalFunctions.app_request_source = req.getHeader("Origin") + "/" + req.getRequestURI();
        GlobalFunctions.initialize();

        switch( action ){
            case "open":
            case "download":
                try {
                    String msg = "";

                    if ( ! req.getParameter("t").isEmpty() && ! req.getParameter("h").isEmpty() ) {

                        if (!req.getParameter("resource").isEmpty()) {
                            if ( true || GlobalFunctions.app_user_data.has("id")) {

                                JSONObject returnedData = nwpFiles.getFile(new JSONObject().put("id", req.getParameter("resource") ).put("token", req.getParameter("t") ).put("hash", req.getParameter("h") ) );
                                if (returnedData.has("error") && !returnedData.getString("error").isEmpty()) {
                                    req.setAttribute("json", "<h1>Unable to Retrieve File</h1>" + returnedData.getString("error"));
                                } else if (returnedData.has("file_url") && !returnedData.getString("file_url").isEmpty()) {

                                    String fileName = returnedData.has("file_url_name") ? returnedData.getString("file_url_name") : "Download";
                                    //String path = GlobalFunctions.app_file_download_dir + GlobalFunctions.app_file_seperator + fileName;
                                    String path = returnedData.getString("file_url");
                                    closeConnection();

                                    File file = new File(path);
                                    if (file.exists()) {
                                        ServletContext ctx = getServletContext();
                                        InputStream fis = new FileInputStream(file);
                                        String mimeType = ctx.getMimeType(file.getAbsolutePath());
                                        //System.out.println(mimeType);
                                        res.setContentType(mimeType != null ? mimeType : "application/octet-stream");
                                        res.setContentLength((int) file.length());

                                        switch( action ) {
                                            case "open":
                                                switch( mimeType.replaceAll("application/", "").replaceAll("image/", "") ){
                                                    case "pdf":
                                                    case "jpg":
                                                    case "jpeg":
                                                    case "png":
                                                    case "svg":
                                                    case "gif":
                                                        res.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
                                                        break;
                                                    default:
                                                        res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                                                        break;
                                                }

                                                break;
                                            default:
                                                res.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                                                break;
                                        }
                                        ServletOutputStream os = res.getOutputStream();
                                        byte[] bufferData = new byte[1024];
                                        int read = 0;
                                        while ((read = fis.read(bufferData)) != -1) {
                                            os.write(bufferData, 0, read);
                                        }
                                        os.flush();
                                        os.close();
                                        fis.close();
                                        msg = "Successful: File downloaded at client successfully";
                                        //System.out.println("File downloaded at client successfully");
                                    } else {
                                        msg = "<h1>File not Found</h1>This file may have been deleted";
                                    }
                                } else {
                                    msg = "<h1>Unknown error when retrieving file</h1>";
                                }

                            } else {
                                msg = "<h1>Access Denied</h1>Please login and try again...";
                            }
                        } else {
                            msg = "<h1>Invalid Resource</h1>Please login and try again...";
                        }
                    }else{
                        msg = "<h1>Access Violation</h1>Please login and try again...";
                    }
                    req.setAttribute("json", msg );

                    if( GlobalFunctions.app_data.has("log_audit_view") && GlobalFunctions.app_data.getBoolean("log_audit_view") ){
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("audit",true)
                                        .put("function", action)
                                        .put("user", GlobalFunctions.currentUserName )
                                        .put("params", msg ),
                                null
                        );
                    }
                }catch (Exception e){
                    req.setAttribute("json", "<h1>Unknown Error</h1>" + e.getMessage() );
                }
                break;
            case "test_db":
                req.setAttribute("json", "<h1>Testing DB</h1>" + DBConn.testDBConn() );
                break;
        }

        req.getRequestDispatcher("/ajax-request.jsp").forward(req, res);
        this.closeConnection = false;
        closeConnection();

    }



    public static String getJSPResponse( HttpServletRequest req, HttpServletResponse res, String jspFile ){
        String jspResponse = "";

        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(res) {
            private final StringWriter sw = new StringWriter();

            @Override
            public PrintWriter getWriter() throws IOException {
                return new PrintWriter(sw);
            }

            @Override
            public String toString() {
                return sw.toString();
            }
        };

        if( GlobalFunctions.app_data.has("strip_leading_slash_in_file_path") && GlobalFunctions.app_data.getBoolean("strip_leading_slash_in_file_path") ) {
            if( jspFile.substring(0,1).equals("/") ){
                jspFile = jspFile.substring(1, jspFile.length() );
            }
        }else {
            /*if (jspFile.equals("views/users/new_user_form.jsp") || jspFile.equals("/views/users/new_user_form.jsp")) {
                jspFile = "/views/users/new_user_form.jsp";
            }*/
        }

        if( GlobalFunctions.nwp_development_mode ){
            try {
                req.getRequestDispatcher(jspFile).include(req, responseWrapper);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            jspResponse = responseWrapper.toString();
        }else {
            try {
                req.getRequestDispatcher(jspFile).include(req, responseWrapper);
                jspResponse = responseWrapper.toString();
            } catch (Exception e) {
                jspResponse = "<h4>" + jspFile + "</h4>File path of jsp: " + e.getMessage();
            }
        }

        return jspResponse;
    }

    public void closeConnection() {
        if( this.closeConnection ) {
            //DBConn.closeConnectionToDatabase();
        }
        GlobalFunctions.reset();
    }

    public static JSONObject paramJson(String paramIn) {
        paramIn = paramIn.replaceAll("=", "\":\"");
        paramIn = paramIn.replaceAll("&", "\",\"");
        return new JSONObject("{\"" + paramIn + "\"}" );
    }

    private JSONObject getViewName( JSONObject jsonGET, String action){
        Boolean oldFiles = true;
        JSONObject j = new JSONObject();
        Boolean addDataTableJS = false;
        String jspFile = "";
        String msg = "";
        j.put("hasView", false );

        try {
            StringBuffer jspFileNameOld = new StringBuffer();
            StringBuffer jspFileName = new StringBuffer();
            jspFileNameOld.append(jsonGET.getString("nwp2_action"))
                    .append("-")
                    .append(jsonGET.getString("nwp2_todo"));

            jspFileName.append("views/");
            if( jsonGET.has("nwp_view") && ! jsonGET.getString("nwp_view").isEmpty() ){
                jspFileName.append(jsonGET.getString("nwp_view"));
            }else{
                jspFileName.append(jsonGET.getString("nwp2_action"));
            }

            jspFileName.append("/")
                    .append(jsonGET.getString("nwp2_todo"));

            switch (action) {
                case "display_menu":
                    if (jsonGET.has("nwp_todo") && jsonGET.has("nwp_action")) {
                        jspFileNameOld.append("-")
                                .append(jsonGET.getString("nwp_action"))
                                .append("-")
                                .append(jsonGET.getString("nwp_todo"));

                        jspFileName.append("-")
                                .append(jsonGET.getString("nwp_action"))
                                .append("-")
                                .append(jsonGET.getString("nwp_todo"));
                    }
                    break;
            }

            jspFileName.append(".jsp");

            jspFileNameOld.append(".jsp");
            jspFile = jspFileName.toString();
            String path2 = GlobalFunctions.app_path + jspFile;
            File dViewFile2 = new File(path2);
            if (oldFiles) {
                if (!dViewFile2.exists()) {
                    jspFile = jspFileNameOld.toString();
                }else{
                    j.put("hasView", true );
                }
            }else{
                if ( dViewFile2.exists()) {
                    jspFile = jspFileNameOld.toString();
                    j.put("hasView", true );
                }
            }

            String todo = "";
            if (jsonGET.has("nwp_todo") && !jsonGET.getString("nwp_todo").isEmpty() && !jsonGET.getString("nwp_todo").equals("-")) {
                todo = jsonGET.getString("nwp_todo");
            } else if (jsonGET.has("nwp2_todo") && !jsonGET.getString("nwp2_todo").equals("-")) {
                todo = jsonGET.getString("nwp2_todo");
            }

            if (todo.equals("display_records_frontend") || todo.equals("display_records_frontend_report_all") || todo.equals("display_records_frontend_report") || todo.equals("display_filtered_workflows2") || todo.equals("display_my_records_frontend") || todo.equals("display_all_records_frontend") || todo.equals("display_workflow_trail") || todo.equals("comments_search_list2") || todo.equals("files_search_list2") || todo.equals("display_record_meta_data") || todo.equals("display_all_records_full_view")) {
                addDataTableJS = true;
                //rstatus = "display-datatable";
            }
        } catch (Exception e) {
            msg = "<h4>Invalid Parameters</h4>Action & Todo...<br /><br />{Login.java::doPost}";
            jspFile = "no-view-file.jsp";
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                    .put("return", msg )
                    .put("input", new JSONObject().put("get", jsonGET).put("action", action) )
                    .put("function", "Endpoint.getViewName" )
                    .put("exception", true )
                    .put("fatal", true ) , e
            );
        }

        j.put("jspFile", jspFile);
        j.put("msg", msg);
        j.put("addDataTableJS", addDataTableJS);
        return j;
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        //System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
        /*RequestDispatcher dispatcher = req.getRequestDispatcher("/ajax-request.jsp");
        dispatcher.include(req, res);*/
}

