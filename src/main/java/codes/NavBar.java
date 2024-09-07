package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class NavBar {

    public static void main(String[] args) {
        //System.out.println( theSubMenu() );
    }

    public static String GetCurrentUserCard(String a){
        String $site_url2 = GlobalFunctions.get_project_data().getString("domain_name");
        String $fullname = ( GlobalFunctions.app_user_data.has("name") )?GlobalFunctions.app_user_data.getString("name"):"";
        if( $fullname.length() > 16 ){
            $fullname = $fullname.substring(0, 15) + "...";
        }
        //String $fullname = ( GlobalFunctions.app_user_data.has("username") )?GlobalFunctions.app_user_data.getString("username"):"";

        //$fullname += ( GlobalFunctions.app_user_data.has("last_name") )?(" " + GlobalFunctions.app_user_data.getString("last_name") ):"";
        String $role =  "<div style=\"margin-top: 5px;\"><small>";
        $role += ( GlobalFunctions.app_user_data.has("access_role") )?("<span title=\"Access Role\">" + GlobalFunctions.app_user_data.getString("access_role") + "</span>" ):"";
        if( GlobalFunctions.app_user_data.has("paccess_role") && ! GlobalFunctions.app_user_data.getString("paccess_role").isEmpty() ){
            $role += "<br /><small title=\"Provisional Access Role\"><i>" + GlobalFunctions.app_user_data.getString("paccess_role")  + "</i></small>";
        }
        $role += "</small></div>";

        String $dashboard_link = "<li style=\"margin:-10px; overflow:hidden;\"><div class=\"dropdown-menu-header\"><div class=\"dropdown-menu-header-inner bg-dark\"><div class=\"menu-header-content\"><div class=\"avatar-icon-wrapper mb-3 avatar-icon-xl\"><div class=\"avatar-icon\"><img src=\""+ ( GlobalFunctions.app_user_data.has("photograph") && ! GlobalFunctions.app_user_data.getString("photograph").isEmpty() ? ( $site_url2 + GlobalFunctions.app_user_data.getString("photograph") ) : ( $site_url2 + "a-assets/images/avatars/1.jpg" ) ) +"\" alt=\""+ $fullname +"\"></div></div><div><h5 class=\"menu-header-title\">"+ $fullname + $role +"</h5></div><div class=\"menu-header-btn-pane pt-1\"><a href=\"javascript:;\" style=\"display: inline-block;\" class=\"btn default btn-sm custom-single-selected-record-button\" action=\"?action=sign_out&todo=sign_out&nwp2_action=sign_out&nwp2_todo=sign_out&html_replacement_selector=\" override-selected-record=\"-\"><i class=\"icon-power-off \"> </i></a></div></div></div></div></li>";

        String $key = "my";
        String $container2 = "my-tree-" + a;
        $dashboard_link += "<li><div id=\""+$key+"-tree-con\" class=\"white-tree\" style=\"margin-top:10px;\"><a href=\"#\" id=\""+$key+"-tree\" data-action=\"get_tree_view\" data-todo=\"display_my_tree_view\" data-container=\""+ $container2 +"\" data-table=\""+ $key +"\" data-current_tab=\""+ a +"\" class=\"btn btn-sm blue pull-rightx \" style=\"display:none;\">Explore</a></div></li>";
        $dashboard_link += "<script type=\"text/javascript\">var nwMisHome = function () {return {recordItem: {id:\"\",},init: function () {nwMisHome.startTreeview('my-tree');},startTreeview: function ( id ) {var g_container = '';var $a = $('a#' + id );if( $a.attr(\"data-action\") ){$(\"#\"+ id +\"-con\").html('<div id=\"'+ id +'\" class=\"demo\"></div>');nwTreeView.selector = \"#\" + id;nwTreeView.action = $a.attr(\"data-action\");nwTreeView.todo = $a.attr(\"data-todo\");if( $a.attr(\"data-container\") ){g_container = $a.attr(\"data-container\");}nwTreeView.data = { html_replacement_selector:g_container, table: $a.attr(\"data-table\"),current_tab: $a.attr(\"data-current_tab\"), development_mode_off:1 };nwTreeView.activate_tree_view_main();}},};}();nwMisHome.init();</script>";

        return $dashboard_link;
    }

    public static String GetMenuContainerID(String a){
        String r = "mis-container-sub";
        return r;
    }

    public static JSONObject GetMenu(String a, Boolean treeview){
        String r = "";
        JSONObject rj = new JSONObject();
        JSONArray rjt = new JSONArray();

        String html_container = GetMenuContainerID("");
        JSONObject j = new JSONObject();
        //j.put("filename", "side-menu.jsp" );
        j.put("filename", a );
        JSONObject j1 = theMenu( a );

        JSONObject sub_menu = theSubMenu();
        String $first = "1";
        StringBuffer r3 = new StringBuffer();
        JSONObject r3t = new JSONObject();
        String $eparams = "";
        JSONObject r2t;
        JSONArray r2j;
        Boolean show = false;

        switch(a){
            case "home":
                JSONArray homeMenu = theHomeMenu();
                if( homeMenu.length() > 0 ){
                    for (int i2 = 0; i2 < homeMenu.length(); i2++) {
                        show = false;
                        JSONObject homeMenuItem = homeMenu.getJSONObject(i2);
                        if ( homeMenuItem.has("no_access")) {
                            show = true;
                        } else if (GlobalFunctions.app_user_data.has("accessible_functions")) {
                            if (  homeMenuItem.has("menu_key") && GlobalFunctions.app_user_data.getJSONObject("accessible_functions").has( "home_menu." + homeMenuItem.getString("menu_key") ) ) {
                                show = true;
                            }
                        }

                        if ( ! show &&  homeMenuItem.has("access_type") &&  homeMenuItem.getString("access_type").equals( GlobalFunctions.app_sys_admin_role ) ) {
                            if( GlobalFunctions.app_user_data.has("role") && GlobalFunctions.app_user_data.getString("role").equals( GlobalFunctions.app_sys_admin_role ) ){
                                show = true;
                            }
                        }

                        if( show ) {
                            rjt.put( homeMenuItem );
                        }
                    }
                }
                //System.out.println( rjt );

                if( true ){
                    //generate menu json
                    JSONObject $qrs = nwpWorkflow.getWorkflowSettings( new JSONObject().put("source", "menu").put("check_access", "1") );
                    JSONObject $opt = new JSONObject();
                    $opt.put("return_type", "2");
                    JSONObject $qrs2 = GlobalFunctions.get_list_box_options("workflow_category", $opt);

                    String $key;
                    String $stxt;
                    JSONObject $w_menu = new JSONObject();
                    JSONObject $val;
                    if( $qrs.length() > 0 && $qrs.names().length() > 0 ){

                        for (int i2 = 0; i2 < $qrs.names().length(); i2++) {
                            $key = $qrs.names().getString(i2);
                            $val = $qrs.getJSONObject( $key );

                            if ( $val.has("category") && $val.has("name") && $qrs2.has( $val.getString("category") ) ) {
                                if( ! $w_menu.has( $val.getString("category") ) ){
                                    $stxt = $qrs2.getString( $val.getString("category") );
                                    if( $stxt.length() > 21 ){
                                        $stxt = $stxt.substring(0, 18) + "...";
                                    }

                                    $w_menu.put( $val.getString("category"), new JSONObject().put("text", $stxt ).put("tooltip", $qrs2.getString( $val.getString("category") ) ).put("id", "wk_menu_" + $val.getString("category") ).put("children", new JSONArray() ).put("li_attr", new JSONObject().put("title", $qrs2.getString( $val.getString("category") ) ) ) );
                                }

                                $stxt = $val.getString("name");
                                if( $stxt.length() > 18 ){
                                    $stxt = $stxt.substring(0, 15) + "...";
                                }

                                r2t = new JSONObject();
                                r2t.put("id", "action=display_menu:::todo=execute:::nwp2_action=nwp_workflow:::nwp2_todo=display_filtered_workflows2:::nwp2_type=full:::table="+ $val.getString("table") +":::id="+ $val.getString("id") +":::get_children=1:::html_replacement_selector=" + html_container + ":::current_tab="+ a +":::is_menu=1:::menu_title=" + GlobalFunctions.rawurlencode( $qrs2.getString( $val.getString("category") ) + ": "+ $val.getString("name") ) );
                                r2t.put("tooltip", $val.getString("name") );
                                r2t.put("li_attr", new JSONObject().put("title", $val.getString("name") ) );
                                r2t.put("text",  $stxt );

                                $w_menu.getJSONObject( $val.getString("category") ).getJSONArray("children").put( r2t );
                            }
                        }

                        if( $w_menu.names().length() > 0 ){
                            ArrayList<String> arrayForSorting = new ArrayList<String>();
                            for (int i = 0; i < $w_menu.names().length(); i++) {
                                arrayForSorting.add( $w_menu.names().getString(i) );
                            }
                            Collections.sort( arrayForSorting );

                            for (int i2 = 0; i2 < arrayForSorting.size(); i2++) {
                                $key = arrayForSorting.get(i2);
                                if( $w_menu.has($key) ) {
                                    $val = $w_menu.getJSONObject($key);
                                    rjt.put($val);
                                }
                            }
                        }

                    }

                }
                break;
        }

        //System.out.println( j1.names() );
        if( j1.length() > 0 ){
            for(int i = 0; i < j1.names().length(); i++){
                //r2 = j1.get(j1.names().getString(i)).toString();

                JSONObject obj1 = new JSONObject( j1.get(j1.names().getString(i)).toString() );


                String $html1 = "";
                StringBuffer r4 = new StringBuffer();
                JSONObject j2 = new JSONObject( obj1.get( "sub_menu" ).toString() );
                r2t = new JSONObject();
                r2j = new JSONArray();

                if( j2.length() > 0 ) {
                    for (int i1 = 0; i1 < j2.names().length(); i1++) {
                        if (sub_menu.has(j2.names().getString(i1))) {
                            JSONObject j3 = new JSONObject(sub_menu.get(j2.names().getString(i1)).toString());
                            //String menu_title =  encodeValue( j3.getString("title") );
                            String menu_title = GlobalFunctions.rawurlencode(j3.has("full_title") ? j3.getString("full_title") : j3.getString("title"));
                            String menu_key = j2.names().getString(i1);

                            show = false;
                            if (j3.has("no_access")) {
                                show = true;
                            } else if (GlobalFunctions.app_user_data.has("role")) {
                                if (GlobalFunctions.app_user_data.has("accessible_functions")) {
                                    if (GlobalFunctions.app_user_data.getJSONObject("accessible_functions").has(menu_key)) {
                                        show = true;
                                    }
                                }
                            }else{
                                show = true;
                            }

                            if (show) {
                                r4.append("<li><a href=\"javascript:;\" class=\"custom-single-selected-record-button\" action=\"?action=display_menu&current_tab=")
                                        .append(a)
                                        .append("&todo=")
                                        .append(menu_key)
                                        .append("&is_menu=1&html_replacement_selector=")
                                        .append(html_container)
                                        .append("&nwp2_action=")
                                        .append(j3.getString("class"))
                                        .append("&nwp2_todo=")
                                        .append(j3.getString("action"))
                                        .append("&menu_title=")
                                        .append(menu_title)
                                        .append("\" override-selected-record=\"")
                                        .append(menu_key)
                                        .append("\" >")
                                        .append(j3.getString("title").toUpperCase())
                                        .append("</a></li>");

                                r2t = new JSONObject();
                                r2t.put("id", "action=display_menu:::todo=" + menu_key + ":::current_tab=" + a + ":::html_replacement_selector=" + html_container + ":::is_menu=1:::nwp2_action=" + j3.getString("class") + ":::nwp2_todo=" + (j3.getString("action")).replace("&", ":::") + $eparams + ":::menu_title=" + menu_title);
                                r2t.put("text", j3.getString("title").toUpperCase());
                                //r2t.put("children", true );

                                if (j3.has("tooltip")) {
                                    r2t.put("li_attr", new JSONObject().put("title", j3.getString("tooltip")));
                                    r2t.put("tooltip", j3.getString("tooltip"));
                                }

                                if (j3.has("icon")) {
                                    r2t.put("icon", j3.getString("icon"));
                                }
                                r2j.put(r2t);
                            }
                        }
                    }
                    $html1 = r4.toString();
                }

                if( ! $html1.isEmpty() ) {

                    String $hidden = "hidden";
                    String $picon = "icon-caret-left";
                    if (!$first.isEmpty()) {
                        $hidden = "";
                        $picon = "icon-caret-down";
                    }
                    $first = "";

                    r3.append("<li class=\"app-sidebar__heading ")
                            .append("\">")
                            .append(obj1.get("title"))
                            .append(" <i class=\"pull-right ")
                            .append($picon)
                            .append("\"></i></li><li><ul class=\"sub-menu ")
                            .append($hidden)
                            .append("\" >")
                            .append($html1)
                            .append("</ul></li>");

                    r3t = new JSONObject();
                    r3t.put("id", obj1.getString("class") + "-" + obj1.getString("action") + i);
                    r3t.put("text", obj1.getString("title").toUpperCase());
                    r3t.put("children", r2j);

                    if (obj1.has("tooltip")) {
                        r3t.put("tooltip", obj1.getString("tooltip"));
                        r3t.put("li_attr", new JSONObject().put("title", obj1.getString("tooltip")));
                    }

                    if (obj1.has("icon")) {
                        r3t.put("icon", obj1.getString("icon"));
                    }
                    //System.out.println( r3t.toString() );
                    rjt.put(r3t);
                }

                //JSONObject obj1 = new JSONObject( r2 );
                //System.out.println( obj1.get("title") );
            }
        }
        r = r3.toString();

        if( treeview ) {
            rj.put("tree", rjt );
        }else{
            rj.put("html", r);
        }

        return rj;
    }

    public static JSONObject theMenu( String a ){
        JSONObject j = new JSONObject();
        JSONObject jr = new JSONObject();
        //j.put("filename", a );
        JSONObject jhome = new JSONObject();
        JSONObject jsystem = new JSONObject();

        JSONObject j2 = theMainMenu();

        //for(int i1 = 0; i1 < j2.names().length(); i1++) {
        if( j2.length() > 0 ){
            for(int i1 = 0; i1 < j2.length(); i1++) {
                String mkey = j2.names().getString(i1);
                JSONObject j3 = new JSONObject( j2.get( mkey ).toString() );

                JSONObject jtab = new JSONObject( j3.get( "tab" ).toString() );
                //System.out.println( jtab );
                if( jtab.length() > 0 ) {
                    for (int i2 = 0; i2 < jtab.names().length(); i2++) {
                        switch (jtab.names().getString(i2)) {
                            case "home":
                                jhome.put(mkey, j3);
                                break;
                            case "system":
                                jsystem.put(mkey, j3);
                                break;
                        }
                    }
                }
            }
        }
        j.put("home", jhome );
        j.put("system", jsystem );

        if( ! a.isEmpty() ){
            jr = new JSONObject( j.get( a ).toString() );
        }

        return jr;
    }

    public static JSONObject theMainMenu(){
        return new JSONObject(GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "menu.json"));
    }

    public static JSONObject theSubMenu(){
        JSONObject r = new JSONObject( GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "submenu.json") );

        if( ! GlobalFunctions.nwp_development_mode ){
            for( Object mk : r.names() ){
                String k = mk.toString();
                if( r.has( k ) && r.getJSONObject( k ).has("development") && r.getJSONObject( k ).getBoolean("development") ){
                    r.remove( k );
                }
            }
        }

        return r;
    }

    public static JSONArray theHomeMenu(){
        return new JSONArray(GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "home_submenu.json"));
    }

    public static String getNvaigationTabs( String a ) {
        String r = "";

        switch( a ){
            case "home":
                JSONObject j1 = theModules();

                StringBuffer r3 = new StringBuffer();
                String active = " active ";
                Boolean show = false;
                if( j1.length() > 0 ){
                    for(int i = 0; i < j1.length(); i++){
                        show = false;
                        JSONObject obj1 = new JSONObject( j1.get(j1.names().getString(i)).toString() );
                        if( obj1.has("no_access") ){
                            show = true;
                        }else{
                            if( GlobalFunctions.app_user_data.has("role") ){
                                if( GlobalFunctions.app_user_data.has("accessible_functions") ){
                                    if( GlobalFunctions.app_user_data.getJSONObject("accessible_functions").has("frontend_tabs." + j1.names().getString(i) ) ){
                                        show = true;
                                    }
                                }
                            }else{
                                show = true;
                            }
                        }

                        if( show ){
                            r3.append("<li class=\"nav-item ")
                                    .append( active )
                                    .append("\"><a href=\"javascript:;\"")
                                    .append(" class=\"nav-link custom-single-selected-record-button empty-tab")
                                    .append( active )
                                    .append("\" id=\"")
                                    .append( j1.names().getString(i) )
                                    .append("\"  override-selected-record=\"1\" action=\"")
                                    .append( obj1.get("action") )
                                    .append("\" data-toggle=\"tab\"><span id=\"")
                                    .append( j1.names().getString(i) )
                                    .append("-span\">")
                                    .append( obj1.get("title") )
                                    .append("</span></a></li>");
                            active = "";
                        }
                    }
                }
                r = r3.toString();

                break;
        }

        return r;
    }

    public static JSONObject theModules() {
        return new JSONObject(GlobalFunctions.fileGetContentsExternal("settings" + GlobalFunctions.app_file_seperator + "modules.json"));
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
