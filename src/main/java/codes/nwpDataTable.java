package codes;

import org.json.JSONObject;
import org.json.*;

import java.io.File;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class
import java.time.ZoneOffset;

public class nwpDataTable{
    public static JSONObject saveUploadedFiles(JSONObject jget){
        JSONObject $r = new JSONObject();

        if( jget.has("saved_path") ){
            File dViewFile = new File( jget.getString("saved_path") );

            if( dViewFile.exists() ){
                $r.put("success", "true");
                $r.put("title", jget.getString("qqfile").replace("." + jget.getString("ext"), "") );
                $r.put("filename", jget.getString("saved_name") );
                $r.put("ext", jget.getString("ext") );
                $r.put("label", jget.has("label")?jget.getString("label"):"" );
                $r.put("element", jget.has("name")?jget.getString("name"):"" );
                $r.put("oname", jget.getString("qqfile") );
               // $r.put("stored_name", jget.getString("saved_name") + "." + jget.getString("ext") );
                $r.put("dir", jget.getString("saved_path") );
                $r.put("size", jget.get("size") );
                $r.put("fullname", jget.getString("saved_path") + jget.getString("saved_name") + "." + jget.getString("ext") );
                $r.put("stored_name", jget.getString("saved_path") + jget.getString("saved_name") + "." + jget.getString("ext") );
            }else{
                $r.put("error", "Unable to read file from disk cache");
            }
        }else{
            $r.put("error", "Unable to save file to disk cache");
        }

        if( jget.has("error") ){
            $r.put("error", jget.getString("error") );
        }

        return $r;
    }
    public static String getClosePopup(){
        String $r = "<script type=\"text/javascript\">if( $('#zero-out-negative-budget') ){$('#zero-out-negative-budget').modal('hide');}if( $(\"body\").hasClass('modal-open') ){$(\"body\").removeClass(\"modal-open\");}if( $(\".modal-backdrop\").is(':visible') ){$(\".modal-backdrop\").remove();}</script>";
        return $r;
    }

    public static String getPopup( JSONObject $data ){
        String html = "";
        JSONObject d_dat = new JSONObject( "{\"table\":\"\",\"modal_dialog_style\":\"\",\"modal_title\":\"\",\"modal_id\":\"\",\"modal_body\":\"\",\"modal_finish_caption\":\"\",\"modal_callback\":\"\"}" );

        String $key1;
        if( d_dat.names().length() > 0 ){
            for( int i2 = 0; i2 < d_dat.names().length(); i2++ ){
                $key1 = d_dat.names().getString( i2 );
                if( !$data.has( $key1 ) ){
                    $data.put( $key1, d_dat.get( $key1 ) );
                }
            }
        }

        String $modal_title = ( $data.has( "modal_title" ) ) ? $data.getString( "modal_title" ) : "";
        String $modal_dialog_style = ( $data.has( "modal_dialog_style" ) ) ? $data.getString( "modal_dialog_style" ) : "";
        String $modal_callback = ( $data.has( "modal_callback" ) ) ? $data.getString( "modal_callback" ) : "";
        String $modal_id = ( $data.has( "modal_id" ) && !$data.getString( "modal_id" ).isEmpty() ) ? $data.getString( "modal_id" ) : "zero-out-negative-budget";
        //String $modal_id = "zero-out-negative-budget";

        String $modal_body = ( $data.has( "modal_body" ) ) ? $data.getString( "modal_body" ) : "Lover";

        String $modal_finish_caption = ( $data.has( "modal_finish_caption" ) && !$data.getString( "modal_finish_caption" ).isEmpty() ) ? $data.getString( "modal_finish_caption" ) : "Close Me";

        html += "<div id=\"ajax-modal-container\">";
        html += "<div id=\"" + $modal_id + "\" callback=\"" + $modal_callback + "\" class=\"modal fade\" tabindex=\"-1\" data-replace=\"true\">";

        html += "<div class=\"modal-dialog\" style=\"" + $modal_dialog_style + "\">";
        html += "<div class=\"modal-content\">";
        html += "<div class=\"modal-header\" style=\"background:#222; color:#fff;\">";

        html += "<button type=\"button\" style=\"color:#fff;\" class=\"close\" data-dismiss=\"modal\" title=\"Click here to close the modal box\" aria-hidden=\"true\"><i class=\"icon-remove\" style=\"color:#fff;\"></i></button>";
        html += "<h4 class=\"modal-title\">" + $modal_title + "</h4>";
        html += "</div>";
        html += "<div class=\"modal-body\" >";
        html += "<div id=\"modal-replacement-handle\">";
        html += $modal_body;
        html += "</div>";
        html += "</div>";
        html += "<div class=\"modal-footer\" >";
        html += "<button id=\"modal-popup-close\" type=\"button\" class=\"btn btn-danger\" title=\"Click here to close the modal box\" data-dismiss=\"modal\">" + $modal_finish_caption + "</button>";
        html += "</div>";
        html += "</div>";
        html += "</div>";
        html += "</div>";

        html += "<script>";
        html += "$(\"#zero-out-negative-budget\").modal(\"show\");";
        html += "$(\"#zero-out-negative-budget\").on(\"show.bs.modal\", function(){	if( $(\"input[type='date']\").is(\":focus\") ){		$(\"#zero-out-negative-budget\").addClass(\"date-in-use\");	} }).on(\"hide.bs.modal\", function(){ ";
        html += " if( $(\"#zero-out-negative-budget\").hasClass(\"date-in-use\") ){ $(\"#zero-out-negative-budget\").removeClass(\"date-in-use\");	}else{ ";
        html += " var cb = $(this).attr(\"callback\"); ";

        html += "$(\"#ajax-modal-container\").remove(); ";

        html += "$(\"body\").removeClass(\"modal-open\"); ";

        html += "if( cb && typeof( eval( cb ) ) === \"function\" ){ eval( cb + \"()\" ); } }";
        html += "});";
        html += "</script>";
        html += "</div>";

        GlobalFunctions.app_popup = false;
        return html;
    }

    public static String getButtons( JSONObject $data ){
        String $html = "";
        //$data = new JSONObject("{\"params\":\"&html_replacement_selector=datatable-split-screen-ecm2\",\"html_replacement_selector\":\"datatable-split-screen-ecm2\",\"plugin\":\"nwp_ecm\",\"selected_record\":\"em227160171058\",\"table\":\"ecm2\",\"utility_buttons\":{\"comments\":1,\"attach_file\":1,\"view_details\":1,\"view_attachments\":1},\"more_actions\":{\"op\":{\"todo\":\"control_panel\",\"title\":\"Open and View Details\",\"text\":\"Open\",\"html_replacement_key\":\"phtml_replacement_selector\"},\"actions\":{\"title\":\"Actions\",\"data\":{\"cp\":{\"pl\":{\"todo\":\"priority_pop_form\",\"title\":\"Manage Priority\",\"text\":\"Manage Priority\"},\"cs\":{\"todo\":\"change_status_pop_form\",\"title\":\"Change Status\",\"text\":\"Change Status\"},\"tj\":{\"todo\":\"transfer_pop_form\",\"title\":\"Transfer Job\",\"text\":\"Transfer Job\"},\"cn\":{\"todo\":\"cancel_pop_form\",\"title\":\"Cancel Job\",\"text\":\"Cancel Job\"}}}}},\"phtml_replacement_selector\":\"inventories-asset-center-sub\",\"development\":1}");
        JSONObject $more_data = new JSONObject( "{\"html_replacement_selector\":\"datatable-split-screen-ecm2\"}" );
        String $hide = $data.has( "hide" ) ? $data.getString( "hide" ) : "";

        $html += "<span>";

        int $access = 0;
        int $super = 0;
        if( $data.has( "skip_access_control" ) && $data.getInt( "skip_access_control" ) == 1 ){
            $super = 1;
        }else{
            //       int $access = get_accessed_functions();
            $access = 1;
            $super = 0;
            if( $access == 1 ){
                $super = 1;
            }
        }
        $super = 1;

        String $plugin = $data.has( "plugin" ) ? $data.getString( "plugin" ) : "";

        String $g_search_params2 = "";
        String $html_replacement_selector = "";
        if( $data.has( "html_replacement_selector" ) && !$data.getString( "html_replacement_selector" ).isEmpty() ){
            $html_replacement_selector = $data.getString( "html_replacement_selector" );
            $g_search_params2 = "&html_replacement_selector=" + $data.getString( "html_replacement_selector" );
        }

        String $cls = " custom-single-selected-record-button-old ";
        String $cls2 = " dark ";
        String $cls3 = "More";

        String $attr = "";
        String $gbranch = $more_data.has( "branch" ) ? $more_data.getString( "branch" ) : "";
        String $additional_params = ( $data.has( "more_data" ) && $data.getJSONObject( "more_data" ).has( "additional_params" ) ) ? $data.getJSONObject( "more_data" ).getString( "additional_params" ) : "";

        if( !$gbranch.isEmpty() ){
            String $store = $gbranch;
            $g_search_params2 += "&branch=" + $gbranch;
        }


        if( $data.has( "selected_record" ) && !$data.getString( "selected_record" ).isEmpty() ){
            $cls2 = " btn-default ";

            $cls = " custom-single-selected-record-button ";
            $attr += " override-selected-record=\"" + $data.getString( "selected_record" ) + "\" ";
        }

        if( $data.has( "mod" ) && !$data.getString( "mod" ).isEmpty() ){
            $attr += " mod=\"" + $data.getString( "mod" ) + "\" ";
        }

        //echo $plugin;
        //echo "<pre>";print_r( $access ); echo "</pre>";
        //echo "<pre>";print_r( $data ); echo "</pre>";
        String $standalone = "";
        String $ma_html = "";
        if( $data.has( "more_actions" ) && $data.getJSONObject( "more_actions" ).names().length() > 0 ){

            for( int i = 0; i < $data.getJSONObject( "more_actions" ).names().length(); i++ ){
                String $act2k = $data.getJSONObject( "more_actions" ).names().getString( i );
                JSONObject $act2 = $data.getJSONObject( "more_actions" ).getJSONObject( $act2k );
                if( !$act2.has( "title" ) ){
                    continue;
                }
                if( ! $hide.isEmpty() && $act2.has( $hide ) ){
                    continue;
                }
                //System.out.println( $act2 );
                String $h = "";

                if( $act2.has( "data" ) ){
                    if( $act2.getJSONObject( "data" ).names().length() > 0 ){
                        for( int i2 = 0; i2 < $act2.getJSONObject( "data" ).names().length(); i2++ ){
                            String $act3k = $act2.getJSONObject( "data" ).names().getString( i2 );
                            JSONObject $act3 = $act2.getJSONObject( "data" ).getJSONObject( $act3k );
                            if( $act3.length() > 0 ){

                                if( !$h.isEmpty() ){
                                    $h += "<li class=\"divider\"></li>";
                                }

                                for( int i3 = 0; i3 < $act3.names().length(); i3++ ){
                                    String $bk1 = $act3.names().getString( i3 );
                                    JSONObject $act = $act3.getJSONObject( $bk1 );
                                    String $table = $act.has( "action" ) ? $act.getString( "action" ) : $data.getString( "table" );
                                    String $clt = $table;
                                    if( !$act.has( "no_plugin" ) && !$plugin.isEmpty() ){
                                        $clt = $plugin;
                                    }
                                    if( !GlobalFunctions.nwp_development_mode && $act.has( "development" ) && $act.getInt( "development" ) > 0 ){
                                        continue;
                                    }
                                    if( ! $hide.isEmpty() && $act.has( $hide ) ){
                                        continue;
                                    }

                                    String $pk1 = "";
                                    if( !$act.has( "no_plugin" ) && !$plugin.isEmpty() ){
                                        $pk1 = $plugin + ".";
                                    }

                                    String $ar_tb = $data.has( "access_role_tb" ) ? $data.getString( "access_role_tb" ) : $data.getString( "table" );

                            /*To be reviewed
                            if( ! $act.has("all_access") && $super && ! isset( $access["accessible_functions"][ $pk1 . $ar_tb . "." . $act2k . "." .$bk1 ] ) ){
                                continue;
                            }*/

                                    String $url = "#";
                                    String $d_action = "";
                                    String $d_cls = "";

                                    String $attr2 = $attr;

                                    if( $act.has( "url" ) && !$act.getString( "url" ).isEmpty() ){
                                        $url = $act.getString( "url" );
                                    }else{

                                        $d_action = "action=display_sub_menu&nwp2_action=" + $table + "&todo=&nwp2_todo=" + $act.getString( "todo" );
                                        /*if (!$act.has("no_plugin") && !$plugin.isEmpty()) {
                                            $d_action = "action=" + $plugin + "&todo=execute&nwp_action=" + $table + "&nwp_todo=" + $act.getString("todo");
                                        }*/

                                        if( $act.has( "empty_container" ) && !$act.getString( "empty_container" ).isEmpty() ){
                                            $d_action += "&empty_container=" + ( $act.has( "empty_container_id" ) ? $act.getString( "empty_container_id" ) : $html_replacement_selector );
                                        }

                                        $d_cls = $cls;
                                        if( $act.has( "multiple" ) && !$act.getString( "multiple" ).isEmpty() ){
                                            $d_cls = " custom-multi-selected-record-button ";
                                        }

                                        if( $act.has( "selected_record" ) && !$act.getString( "selected_record" ).isEmpty() ){
                                            $d_cls = " custom-single-selected-record-button ";
                                            $attr2 = " override-selected-record=\"" + $act.getString( "selected_record" ) + "\" ";
                                        }

                                        if( $act.has( "id_field" ) && !$act.getString( "id_field" ).isEmpty() && $data.getJSONObject( "id_field" ).has( $act.getString( "id_field" ) ) && !$data.getJSONObject( "id_field" ).getString( $act.getString( "id_field" ) ).isEmpty() ){
                                            $d_cls = " custom-single-selected-record-button ";
                                            $attr2 = " override-selected-record=\"" + $data.getJSONObject( "id_field" ).getString( $act.getString( "id_field" ) ) + "\" ";
                                        }

                                        if( $act.has( "mod" ) && !$act.getString( "mod" ).isEmpty() ){
                                            $attr2 += " mod=\"" + $act.getString( "mod" ) + "\" ";
                                        }

                                        if( $act.has( "html_replacement_key" ) && $data.has( $act.getString( "html_replacement_key" ) ) && !$data.getString( $act.getString( "html_replacement_key" ) ).isEmpty() ){
                                            $d_action += "&html_replacement_selector=" + $data.getString( $act.getString( "html_replacement_key" ) );
                                        }else{
                                            $d_action += $g_search_params2;
                                        }
                                        // print_r( $cls ); exit;
                                    }

                                    if( $act.has( "attributes" ) && !$act.getString( "attributes" ).isEmpty() ){
                                        $attr2 += $act.getString( "attributes" );
                                    }

                                    if( $act.has( "class" ) && !$act.getString( "class" ).isEmpty() ){
                                        $d_cls += $act.getString( "class" );
                                    }

                                    $d_action += $additional_params;

                                    if( ( $data.has( "standalone_buttons" ) && $data.getJSONObject( "standalone_buttons" ).has( $bk1 ) ) && !$data.getJSONObject( "standalone_buttons" ).getString( $bk1 ).isEmpty() ){
                                        $d_cls += ( $act.has( "standalone_class" ) ? $act.getString( "standalone_class" ) : " btn-default " );

                                        $standalone += "<a href=\"" + $url + "\" class=\"btn btn-sm " + $d_cls + "\" " + $attr2 + " action=\"?" + $d_action + "&menu_title=" + GlobalFunctions.rawurlencode( $act.getString( "title" ) ) + "\" title=\"" + $act.getString( "title" ) + "\">" + $act.getString( "text" ) + "</a>";
                                    }else{
                                        $h += "<li><a href=\"" + $url + "\" class=\"" + $d_cls + "\" " + $attr2 + " action=\"?" + $d_action + "&menu_title=" + GlobalFunctions.rawurlencode( $act.getString( "title" ) ) + "\" title=\"" + $act.getString( "title" ) + "\">" + $act.getString( "text" ) + "</a></li>";
                                    }
                                }
                            }
                        }

                        if( !$h.isEmpty() ){
                            $h = "<div class=\"btn-group\"><a type=\"button\"  class=\"btn btn-sm " + $cls2 + " dropdown-toggle\" data-toggle=\"dropdown\" data-hover=\"dropdown\" data-delay=\"1000\" data-close-others=\"true\">" + $act2.getString( "title" ) + " <!--<i class=\"icon-angle-down\"></i>--></a><div class=\"dropdown-backdrop\"></div><ul class=\"dropdown-menu\" role=\"menu\">" + $h + "</ul></div>";
                        }

                    }
                }else{
                    if( $act2.has( "todo" ) || $act2.has( "url" ) ){
                        String $pk1 = "";
                        String $table = $act2.has( "action" ) ? $act2.getString( "action" ) : $data.getString( "table" );
                        String $clt = $table;
                        if( !$act2.has( "no_plugin" ) && !$plugin.isEmpty() ){
                            $pk1 = $plugin + ".";
                            $clt = $plugin;
                        }

                        if( !GlobalFunctions.nwp_development_mode && $act2.has( "development" ) && $act2.getInt( "development" ) == 1 ){
                            continue;
                        }

                        String $ar_tb = $data.has( "access_role_tb" ) ? $data.getString( "access_role_tb" ) : $data.getString( "table" );
                    /*To be reviewed
                    if( ! $act2.has("all_access") && ! $super && ! isset( $access["accessible_functions"][ $pk1 . $ar_tb . "." . $act2k ] ) ){
                        continue;
                    }*/

                        String $attr2 = $attr;
                        String $d_action = "";
                        String $btn_color = $act2.has( "button_class" ) ? $act2.getString( "button_class" ) : $cls2;
                        String $d_cls = $cls;
                        String $url = "#";

                        if( $act2.has( "url" ) && !$act2.getString( "url" ).isEmpty() && ! ( $act2.has( "todo" ) && ! $act2.getString( "todo" ).isEmpty() ) ){
                            $d_cls = "";
                            $url = $act2.getString( "url" );
                            // $cls = "";
                        }else{

                            if( $act2.has( "multiple" ) && !$act2.getString( "multiple" ).isEmpty() ){
                                $d_cls = " custom-multi-selected-record-button ";
                            }

                            if( $act2.has( "selected_record" ) && !$act2.getString( "selected_record" ).isEmpty() ){
                                $d_cls = " custom-single-selected-record-button ";
                                $attr2 = " override-selected-record=\"" + $act2.getString( "selected_record" ) + "\" ";
                            }

                            if( $act2.has( "id_field" ) && !$act2.getString( "id_field" ).isEmpty() && $data.getJSONObject( "id_field" ).has( $act2.getString( "id_field" ) ) && !$data.getJSONObject( "id_field" ).getString( $act2.getString( "id_field" ) ).isEmpty() ){
                                $d_cls = " custom-single-selected-record-button ";
                                $attr2 = " override-selected-record=\"" + $data.getJSONObject( "id_field" ).getString( $act2.getString( "id_field" ) ) + "\" ";
                            }

                            if( $act2.has( "url" ) && !$act2.getString( "url" ).isEmpty() ){
                                $url = $act2.getString( "url" );
                                // $d_cls = "";
                            }

                            if( $act2.has( "mod" ) && !$act2.getString( "mod" ).isEmpty() ){
                                $attr += " mod=\"" + $act2.getString( "mod" ) + "\" ";
                            }

                            $d_action = "action=display_sub_menu&nwp2_action=" + $table + "&todo=&nwp2_todo=" + $act2.getString( "todo" );
                            /*if (!$act2.has("no_plugin") && !$plugin.isEmpty()) {
                                $d_action = "action=" + $plugin + "&todo=execute&nwp_action=" + $table + "&nwp_todo=" + $act2.getString("todo");
                            }*/

                            if( $act2.has( "empty_container" ) && !$act2.getString( "empty_container" ).isEmpty() ){
                                $d_action += "&empty_container=" + ( $act2.has( "empty_container_id" ) ? $act2.getString( "empty_container_id" ) : $html_replacement_selector );
                            }

                            //System.out.println( $data );
                            // System.out.println( $act2 );
                            if( $act2.has( "html_replacement_key" ) ){
                                //System.out.println( $act2.getString( "html_replacement_key" ) );
                            }
                            if( $act2.has( "html_replacement_key" ) && $data.has( $act2.getString( "html_replacement_key" ) ) && !$data.getString( $act2.getString( "html_replacement_key" ) ).isEmpty() ){
                                $d_action += "&html_replacement_selector=" + $data.getString( $act2.getString( "html_replacement_key" ) );
                            }else{
                                $d_action += $g_search_params2;
                            }
                        }

                        if( $act2.has( "attributes" ) && !$act2.getString( "attributes" ).isEmpty() ){
                            $attr2 += $act2.getString( "attributes" );
                        }

                        if( $act2.has( "class" ) && !$act2.getString( "class" ).isEmpty() ){
                            $d_cls += $act2.getString( "class" );
                        }

                        $d_action += $additional_params;

                        $h = "<a href=\"" + $url + "\" class=\"" + $d_cls + " btn btn-sm " + $btn_color + "\" action=\"?" + $d_action + "&menu_title=" + GlobalFunctions.rawurlencode( $act2.getString( "title" ) ) + "\" " + $attr2 + " title=\"" + $act2.getString( "title" ) + "\">" + $act2.getString( "text" ) + "</a>";

                        if( $act2.has( "append_html" ) && !$act2.getString( "append_html" ).isEmpty() ){
                            $h += $act2.getString( "append_html" );
                        }
                    }
                }

                $ma_html += $h;
            }
        }

        $html += $standalone + $ma_html + "</span>";

        //utility-buttons

        String $table = $data.has( "table" ) ? $data.getString( "table" ) : "";
        String $rtable = ( $data.has( "records_table" ) && !$data.getString( "records_table" ).isEmpty() ) ? $data.getString( "records_table" ) : $table;
        String $params = $data.has( "params" ) ? $data.getString( "params" ) : "";
        JSONObject $ub = $data.has( "utility_buttons" ) ? $data.getJSONObject( "utility_buttons" ) : new JSONObject();
        JSONObject $set_html_container = $data.has( "set_html_container" ) ? $data.getJSONObject( "set_html_container" ) : new JSONObject();
        //    $html_replacement_selector = isset( $data["html_replacement_selector"] )?$data["html_replacement_selector"]:"";

        if( $ub.length() > 0 && $ub.names().length() > 0 ){
            $html += "<span>";

            int $use_group = 0;
            int $group_name = 0;

            JSONObject $paramsA = new JSONObject();
            String $class = "btn dark btn-sm custom-multi-selected-record-button";
            String $class2 = "btn dark btn-sm custom-single-selected-record-button-old ";
            String $class4 = "btn btn-sm custom-single-selected-record-button-old ";
            String $class3 = "btn dark btn-sm custom-single-selected-record-button ";
            String $group_class = " dark ";

            $attr = "";
            if( $data.has( "selected_record" ) && !$data.getString( "selected_record" ).isEmpty() ){
                $group_class = " btn-default ";
                $class2 = " btn-sm btn btn-default custom-single-selected-record-button ";
                $attr = " override-selected-record=\"" + $data.getString( "selected_record" ) + "\" ";
            }

            JSONObject $tmp_d;
            for( int i = 0; i < $ub.names().length(); i++ ){
                String $ubs = $ub.names().getString( i );
                int $usv = $ub.getInt( $ubs );

                if( $set_html_container.has( $ubs ) && !$html_replacement_selector.isEmpty() ){
                    $paramsA.put( $ubs, new JSONObject().put( "params", "html_replacement_selector=" + $html_replacement_selector ) );
                }

                if( $ubs.equals( "view_details" ) ){
                    $tmp_d = new JSONObject().put( "action", $table ).put( "class", $class2 ).put( "todo", "view_details" ).put( "text", "View Details" ).put( "title", "View Details" );

                    $paramsA.put( $ubs, $tmp_d );

                }else if( $ubs.equals( "comments" ) || $ubs.equals( "attach_file" ) || $ubs.equals( "view_attachments" ) ){
                    $tmp_d = new JSONObject();


                    String $clas = "custom-multi-selected-record-button";

                    if( $data.has( "selected_record" ) && !$data.getString( "selected_record" ).isEmpty() ){
                        $clas = " custom-single-selected-record-button ";
                        $attr = " override-selected-record=\"" + $data.getString( "selected_record" ) + "\" ";
                    }

                    if( $ubs.equals( "view_attachments" ) ){
                        $tmp_d.put( "class", $clas ).put( "action", "files" ).put( "todo", "view_attachments" ).put( "text", "View Attachments" );

                        $tmp_d.put( "title", "Display Files Attached to this File" ).put( "params", "reference_table=" + $rtable ).put( "id", "share-records-button" );
                        $tmp_d.put( "icon", "icon-share" );

                    }else if( $ubs.equals( "attach_file" ) ){
                        $tmp_d.put( "class", $clas ).put( "action", "files" ).put( "todo", "attach_file_to_record" ).put( "text", "Attach File" );
                        $tmp_d.put( "title", "Add File" ).put( "params", "table=" + $rtable ).put( "id", "share-records-button" ).put( "icon", "icon-tag" );

                    }else if( $ubs.equals( "comments" ) ){
                        $tmp_d.put( "class", $clas ).put( "action", "comments" ).put( "todo", "add_comment" ).put( "text", "Comments" );
                        $tmp_d.put( "title", "Comments" ).put( "params", "table=" + $rtable ).put( "id", "share-records-button" ).put( "icon", "icon-pencil" );

                    }


                    if( $set_html_container.has( $ubs ) && !$html_replacement_selector.isEmpty() ){
                        $tmp_d.put( "params", "html_replacement_selector=" + $html_replacement_selector );
                    }

                    if( !$paramsA.has( "utilities" ) ){
                        $paramsA.put( "utilities", new JSONObject().put( "use_group", 1 ).put( "group_name", "Utilities" ) );
                    }
                    $paramsA.getJSONObject( "utilities" ).put( $ubs, $tmp_d );

                    if( $ubs.equals( "comments" ) ){
                        $tmp_d = new JSONObject();
                        $tmp_d.put( "class", $clas ).put( "action", "comments" ).put( "todo", "view_details_by_reference" ).put( "text", "View Comments" );
                        $tmp_d.put( "title", "View Comments" ).put( "params", "table=" + $rtable ).put( "id", "share-records-button" ).put( "icon", "icon-comment" );

                        $paramsA.getJSONObject( "utilities" ).put( "comments_view", $tmp_d );
                    }

                }


                if( $ub.get( $ubs ) instanceof JSONObject ){
                    JSONObject $usvA = $ub.getJSONObject( $ubs );
                    if( $usvA.has( "html_replacement_key" ) && $data.has( $usvA.getString( "html_replacement_key" ) ) && !$data.getString( $usvA.getString( "html_replacement_key" ) ).isEmpty() ){
                        $paramsA.put( $ubs, new JSONObject().put( "params", new JSONObject().put( "params", "html_replacement_selector=" + $data.getString( $usvA.getString( "html_replacement_key" ) ) ) ) );
                    }
                    if( $usvA.has( "params" ) && !$usvA.getString( "params" ).isEmpty() ){
                        if( !$paramsA.has( $ubs ) && !$paramsA.getJSONObject( $ubs ).has( "params" ) ){
                            $paramsA.put( $ubs, new JSONObject().put( "params", "" ) );
                        }
                        if( $usvA.getString( "params" ).substring( 0, 1 ).equals( "&" ) ){
                            $usvA.put( "params", "&" + $usvA.getString( "params" ) );
                        }

                        $paramsA.put( $ubs, new JSONObject().put( "params", $paramsA.getJSONObject( $ubs ).getString( "params" ) + $usvA.getString( "params" ) ) );

                    }

                    if( $usvA.has( "attributes" ) && !$usvA.getString( "attributes" ).isEmpty() ){
                        $paramsA.put( $ubs, new JSONObject().put( "attributes", $usvA.getString( "attributes" ) ) );
                    }
                    if( $usvA.has( "text" ) && !$usvA.getString( "text" ).isEmpty() ){
                        $paramsA.put( $ubs, new JSONObject().put( "text", $usvA.getString( "text" ) ) );
                    }
                    if( $usvA.has( "title" ) && !$usvA.getString( "title" ).isEmpty() ){
                        $paramsA.put( $ubs, new JSONObject().put( "title", $usvA.getString( "title" ) ) );
                    }
                    if( $usvA.has( "standalone_class" ) && ( $paramsA.has( $ubs ) && $paramsA.getJSONObject( $ubs ).has( "class" ) ) ){
                        $paramsA.put( $ubs, new JSONObject().put( "class", $usvA.getString( "standalone_class" ) ) );
                    }

                    if( $usvA.has( "no_plugin" ) ){
                        $paramsA.put( $ubs, new JSONObject().put( "no_plugin", $usvA.getString( "no_plugin" ) ) );
                    }
                    if( $usvA.has( "table" ) ){
                        $paramsA.put( $ubs, new JSONObject().put( "action", $usvA.getString( "table" ) ) );
                    }
                }

            }

            //echo "<pre>"; print_r( $params ); echo "</pre>";
            for( int i = 0; i < $paramsA.names().length(); i++ ){
                String $k1 = $paramsA.names().getString( i );
                JSONObject $val = $paramsA.getJSONObject( $k1 );

                JSONObject $val1;
                String $key1;

                //System.out.println( $val );
                if( $val.has( "use_group" ) ){
                    $html += "<div class=\"btn-group\">\n" + "<a type=\"button\"  class=\"btn btn-sm " + $group_class + " dropdown-toggle\" data-toggle=\"dropdown\" data-hover=\"dropdown\" data-delay=\"1000\" data-close-others=\"true\">" + $val.getString( "group_name" ) + "</a><div class=\"dropdown-backdrop\"></div>\n" + "<ul class=\"dropdown-menu\" role=\"menu\">";
                    $val.remove( "use_group" );
                    $val.remove( "group_name" );

                    //System.out.println( $val.names() );
                    if( $val.names().length() > 0 ){

                        for( int i2 = 0; i2 < $val.names().length(); i2++ ){
                            $key1 = $val.names().getString( i2 );
                            $val1 = $val.getJSONObject( $key1 );
                            if( ! $hide.isEmpty() && $val1.has( $hide ) ){
                                continue;
                            }
                            for( int i3 = 0; i3 < new String[]{ "params", "action" }.length; i3++ ){
                                String $pk1 = new String[]{ "params", "action" }[ i3 ];
                                if( $val1.has( $pk1 ) && !$val1.getString( $pk1 ).isEmpty() ){
                                    $val1.put( $pk1, $val1.getString( $pk1 ) );
                                }
                            }

                            String $attr2 = "";
                            if( $val1.has( "attributes" ) && !$val1.getString( "attributes" ).isEmpty() ){
                                $attr2 = $val1.getString( "attributes" );
                            }

                            $html += "<li><a href=\"#\" id=\"" + $val1.getString( "id" ) + "\" class=\"" + $val1.getString( "class" ) + "\" action=\"?action=display_sub_menu&nwp2_action=" + $val1.getString( "action" ) + "&nwp2_todo=" + $val1.getString( "todo" ) + "&todo=&app_manager_control=1&" + $val1.getString( "params" ) + $additional_params + "\" title=\"" + $val1.getString( "title" ) + "\" " + $attr + $attr2 + ">" + ( !$val1.getString( "icon" ).isEmpty() ? "<i class=\"" + $val1.getString( "icon" ) + "\"></i>" : "" ) + $val1.getString( "text" ) + "</a></li>";

                        }

                    }

                    $html += "</ul></div>";
                }else{
                    if( ! $hide.isEmpty() && $val.has( $hide ) ){
                        continue;
                    }

                    if( $val.has( "action" ) && $val.has( "todo" ) ){
                        String $attr2 = "";
                        String $params2 = "";
                        if( $val.has( "attributes" ) && !$val.getString( "attributes" ).isEmpty() ){
                            $attr2 = $val.getString( "attributes" );
                        }
                        if( $val.has( "params" ) && !$val.getString( "params" ).isEmpty() ){
                            $params2 = $val.getString( "params" );
                        }
                        $params2 += $additional_params;

                        $html += "<a href=\"#\" id=\"" + ( $val.has( "id" ) ? $val.getString( "id" ) : "" ) + "\" class=\"" + ( $val.has( "class" ) ? $val.getString( "class" ) : "" ) + "\" action=\"?action=display_sub_menu&nwp2_action=" + $val.getString( "action" ) + "&nwp2_todo=" + $val.getString( "todo" ) + "&todo=&app_manager_control=1" + $params2 + "\" title=\"" + ( $val.has( "title" ) ? $val.getString( "title" ) : "" ) + "\" " + $attr + $attr2 + ">" + ( $val.has( "icon" ) ? $val.getString( "icon" ) : "" ) + $val.getString( "text" ) + "</a>";
                    }
                }
            }

            $html += "</span>";
            //out.println($html);
        }
        return $html;
    }

    public static JSONObject save_line_items( JSONObject a ){
        JSONObject $jrd = new JSONObject();

        String error = "";
        JSONObject r = new JSONObject();

        if( a.has("table") && a.has("line_items") && a.getJSONArray("line_items").length() > 0 ) {
            String $table = a.getString("table");
            JSONObject dep = GlobalFunctions.get_json( $table );
            JSONObject fields = dep.has( "fields" ) ? dep.getJSONObject( "fields" ) : new JSONObject();
            JSONObject labels = dep.has( "labels" ) ? dep.getJSONObject( "labels" ) : new JSONObject();
            JSONObject $gf = a.has( "global_fields" ) ? a.getJSONObject( "global_fields" ) : new JSONObject();
            JSONObject $my_ins = new JSONObject();
            JSONObject $qs = new JSONObject();
            JSONArray $line_items = new JSONArray();
            JSONObject $vline;
            JSONObject $tline;
            String $key2;
            $qs.put("table", $table);

            if( a.getJSONArray("line_items").length() > 0 ){
                for(int i3 = 0; i3 < a.getJSONArray("line_items").length(); i3++) {
                    $vline = a.getJSONArray("line_items").getJSONObject( i3 );
                    $tline = new JSONObject();

                    if( $vline.length() > 0 ) {
                        for (int i4 = 0; i4 < $vline.names().length(); i4++) {
                            $key2 = $vline.names().getString(i4);
                            if( fields.has( $key2 ) ){
                            //if( fields.has( $key2 ) && labels.has( fields.getString( $key2 ) )  && labels.getJSONObject( fields.getString( $key2 ) ).has("form_field") ){
                                //$tline.put( fields.getString( $key2 ), new JSONObject().put("value", $vline.get( $key2 ) ).put("form_field", labels.getJSONObject( fields.getString( $key2 ) ).getString("form_field") ) );
                                $tline.put( fields.getString( $key2 ),  $vline.get( $key2 ) );
                            }
                        }

                        if( $tline.length() > 0 ){
                            //$line_items.put( $tline );
                            if( $gf.length() > 0 ){
                                for (int i5 = 0; i5 < $gf.names().length(); i5++) {
                                    $key2 = $gf.names().getString(i5);
                                    if( fields.has( $key2 ) ){
                                        $tline.put( fields.getString( $key2 ),  $gf.get( $key2 ) );
                                    }
                                }
                            }

                            $my_ins = new JSONObject();
                            $my_ins.put( "table", $table );
                            $my_ins.put( "post_data", $tline );
                            $my_ins.put( "action", "create_new_record" );
                            $my_ins.put( "labels", labels );
                            $my_ins.put( "fields", fields );
                            $line_items.put( nwpFormGen.getFormData( $my_ins ) );
                        }

                    }
                }
            }

            System.out.println("xxx");
            System.out.println( $line_items );

            $qs.put("line_items", $line_items);
            r = GlobalFunctions.save_line_items( $qs );

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

    public static JSONObject saveDataForm( JSONObject $tableData ){
        String html = "";
        String $title = "";
        String $error_msg = "";
        JSONObject $jrd = new JSONObject();
        JSONObject $check_data = new JSONObject();
        JSONObject $c_val;
        JSONArray $rf = new JSONArray();

        Boolean returnData = $tableData.has( "return_data" )?$tableData.getBoolean( "return_data" ):false;
        Boolean showViewDetails = $tableData.has( "show_view_details" )?$tableData.getBoolean( "show_view_details" ):true;


        if( $tableData.has( "title" ) ){
            $title = $tableData.getString( "title" );
        }

        if( $tableData.has( "table" ) && $tableData.has( "post_data" ) ){
            String $table = $tableData.getString( "table" );

            JSONObject $my_ins = $tableData;
            $my_ins.put( "table", $table );
            $my_ins.put( "post_data", $tableData.getJSONObject( "post_data" ) );

            //JSONObject dep = new JSONObject( GlobalFunctions.fileGetContents( "dependencies/" + $table + ".json" ) );
            JSONObject dep = GlobalFunctions.get_json( $table );

            /*System.out.println( "xxx" );
            System.out.println( $my_ins );*/
            //System.out.println( dep );
            if( dep.has( "label" ) && $title.isEmpty() ){

                switch( $table ){
                    case "files":
                        if( dep.has( "fields" ) && dep.getJSONObject( "fields" ).has("file_url") && dep.getJSONObject( "fields" ).has("content") && $my_ins.getJSONObject( "post_data" ).has( dep.getJSONObject( "fields" ).getString("file_url") + "_json" ) ){
                            $my_ins.getJSONObject( "post_data" ).put( dep.getJSONObject( "fields" ).getString("content") , GlobalFunctions.rawurlencode( $my_ins.getJSONObject( "post_data" ).getString( dep.getJSONObject( "fields" ).getString("file_url") + "_json" ) ));
                        }
                        break;
                }

                if( $tableData.has( "todo" ) ){
                    switch( $tableData.getString( "todo" ) ){
                    case "create_new_record":
                        if( $my_ins.has("post_data") && $my_ins.getJSONObject("post_data").has( "tmp_id" ) && ! $my_ins.getJSONObject("post_data").getString( "tmp_id" ).isEmpty() ){
                            $my_ins.put( "id", $my_ins.getJSONObject("post_data").getString( "tmp_id" ) );
                        }
                        break;
                    case "delete":
                        if( $tableData.has( "id" ) && !$tableData.getString( "id" ).isEmpty() ){
                            $my_ins.put( "id", $tableData.getString( "id" ) );
                            $my_ins.put( "delete", $tableData.getString( "id" ) );
                        }else{
                            $error_msg = "Saved Delete operation stopped because record id was not specified";
                        }
                        break;
                    case "edit":
                        if( $tableData.has( "id" ) && !$tableData.getString( "id" ).isEmpty() ){
                            $my_ins.put( "id", $tableData.getString( "id" ) );
                        }else{
                            $error_msg = "Saved Edit operation stopped because record id was not specified";
                        }
                        break;
                    }
                }else{
                    $error_msg = "Todo was not specified: " + $table;
                }

            }else{
                $error_msg = "No labels found in the table json file";
            }

            if( $error_msg.isEmpty() ){
                if( $my_ins.getJSONObject("post_data").has("nw_checksum") && ! $my_ins.getJSONObject("post_data").getString("nw_checksum").isEmpty() ){
                    $check_data =  GlobalFunctions.get_form_checksum( $my_ins.getJSONObject("post_data").getString("nw_checksum"), false );

                    if( $check_data.has("saved") ){
                        if( $check_data.has("fields") && $check_data.getJSONArray("fields").length() > 0 ){
                            $check_data.put("display_fields", new JSONArray() );

                            for(int i2 = 0; i2 < $check_data.getJSONArray("fields").length(); i2++){

                                $c_val = $check_data.getJSONArray("fields").getJSONObject(i2);
                                if( $c_val.has("id") && $c_val.has("value") && $c_val.has("secure") && $c_val.getBoolean("secure") ){
                                    $my_ins.getJSONObject("post_data").put( $c_val.getString("id"), $c_val.getString("value") );
                                }

                                if( $c_val.has("id") && $c_val.has("value") && $c_val.has("show") && $c_val.getBoolean("show") ){
                                    if( ! ( $c_val.has("readonly") && $c_val.getBoolean("readonly") ) ){
                                        $check_data.getJSONArray("display_fields" ).put( $c_val );
                                    }
                                }

                            }
                        }
                    }else{
                        $error_msg = "<h4>Broken Security Layer</h4>Unable to retrieve token.<br /><br />Please repeat the operation again from the beginning";
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("return", $error_msg )
                                        .put("non_fatal", true )
                                        .put("input", new JSONObject( $my_ins.getJSONObject("post_data").toString() ).put("check_data", $check_data) )
                                        .put("function", "nwpDataTable.saveDataForm.check_sum" ),
                                null
                        );
                    }
                }/*else{
                    $error_msg = "<h4>Security Violation</h4>No token found. Please sign-out and sign-in before you re-try";
                }*/
            }

            if( $error_msg.isEmpty() ){
                if( dep.has( "labels" ) && dep.has( "fields" ) ){

                    $my_ins.put( "action", $tableData.getString( "todo" ) );
                    $my_ins.put( "labels", dep.getJSONObject( "labels" ) );
                    $my_ins.put( "fields", dep.getJSONObject( "fields" ) );
                    if( dep.has("prevent_duplicate") && dep.getJSONObject("prevent_duplicate").has("fields") ) {
                        $my_ins.put("prevent_duplicate", dep.getJSONObject("prevent_duplicate") );
                    }

                    try{

                        JSONObject rd = nwpFormGen.getFormData( $my_ins );
                        //rd.put( "error", "Halt ops" );
                        /*System.out.println( "xxx" );
                        System.out.println( rd );*/
                        if( rd.has( "error" ) ){
                            html = rd.getString( "error" );
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject().put("fatal", true ).put("return", rd ).put("input", $my_ins ).put("function", "nwpDataTable.saveDataForm" ),
                                    null
                            );
                        }else{


                            if( rd.length() > 0 ){
                                if( returnData ){
                                    $jrd.put("form_data", rd);
                                    $jrd.put("table", $table);
                                }else {
                                    String rid = "";
                                    JSONObject $sd = new JSONObject();
                                    JSONArray $lt = new JSONArray();
                                    $lt.put(rd);

                                    $sd.put("line_items", $lt);
                                    $sd.put("table", $table);


                                    if ($my_ins.has("id")) {
                                        //edit operation
                                        rid = $my_ins.getString("id");

                                    } else if (rd.has("id")) {
                                        rid = rd.getJSONObject("id").getString("value");
                                        //System.out.println(rid);
                                    }

                                    if ($my_ins.has("main_update_field")
                                            && !$my_ins.getString("main_update_field").isEmpty()
                                            && dep.getJSONObject("fields").has($my_ins.getString("main_update_field"))) {
                                        $sd.put("main_update_field", dep.getJSONObject("fields").getString($my_ins.getString("main_update_field")));
                                    }

                                    switch ($my_ins.getString("action")) {
                                        case "delete":
                                            $sd.put("delete", rid);

                                            if ($tableData.has("record_ids") && !$tableData.getString("record_ids").isEmpty()) {
                                                $sd.put("record_ids", $tableData.getString("record_ids"));
                                            } else if ($tableData.has("post_data") && $tableData.getJSONObject("post_data").has("ids") && !$tableData.getJSONObject("post_data").getString("ids").isEmpty()) {
                                                String[] aid = $tableData.getJSONObject("post_data").getString("ids").split(":::");
                                                String aid_in = "";
                                                if (aid.length > 0) {
                                                    for (int i3 = 0; i3 < aid.length; i3++) {
                                                        if (!aid_in.isEmpty()) {
                                                            aid_in += ", ";
                                                        }
                                                        aid_in += "'" + aid[i3] + "'";
                                                    }
                                                }

                                                if (!aid_in.isEmpty()) {
                                                    $sd.put("record_ids", aid_in);
                                                }
                                            }

                                            break;
                                        case "edit":
                                            $sd.put("update", rid);
                                            break;
                                    }

                                    if (!rid.isEmpty()) {
                                        $sd.put("record_id", rid);
                                    }

                                    JSONObject $r2 = GlobalFunctions.save_line_items($sd);
                                    if (!rid.isEmpty() && $r2.has(rid)) {
                                        $jrd.put("saved_record", $r2.getJSONObject(rid));

                                        switch ($my_ins.getString("action")) {
                                            case "create_new_record":
                                                break;
                                            case "delete":
                                                //callback: cascade delete
                                                if (dep.has("cascade_delete") && dep.getJSONObject("cascade_delete").length() > 0) {
                                                    for (Object ck : dep.getJSONObject("cascade_delete").names()) {
                                                        JSONObject cv = dep.getJSONObject("cascade_delete").getJSONObject(ck.toString());

                                                        if (cv.has("id_field")
                                                                && !cv.getString("id_field").isEmpty()) {

                                                            saveDataForm(new JSONObject()
                                                                    .put("id", rid)
                                                                    .put("record_ids", ($sd.has("record_ids") && !$sd.getString("record_ids").isEmpty()) ? $sd.getString("record_ids") : "")
                                                                    .put("table", ck.toString())
                                                                    .put("post_data", new JSONObject()
                                                                            .put("id", rid)
                                                                            .put("ids", "")
                                                                            .put("mod", "delete-" + ck.toString())
                                                                    )
                                                                    .put("todo", "delete")
                                                                    .put("main_update_field", cv.getString("id_field"))

                                                            );

                                                        }
                                                    }
                                                }

                                                //empty cache

                                                break;
                                            case "edit":
                                                break;
                                        }

                                        //get view details
                                        if ($jrd.getJSONObject("saved_record").has("id")) {

                                            if ( showViewDetails ) {
                                                if ( $tableData.has("display_fields") && $tableData.getJSONArray("display_fields").length() > 0 ) {
                                                    $check_data.put("display_fields", $tableData.getJSONArray("display_fields") );
                                                }

                                                if ($check_data.has("display_fields") && $check_data.getJSONArray("display_fields").length() > 0) {
                                                    for (int i2 = 0; i2 < $check_data.getJSONArray("display_fields").length(); i2++) {

                                                        $c_val = $check_data.getJSONArray("display_fields").getJSONObject(i2);
                                                        $c_val.put("value", "");

                                                        if ($c_val.has("key") && $jrd.getJSONObject("saved_record").has($c_val.getString("key"))) {
                                                            $c_val.put("value", $jrd.getJSONObject("saved_record").getString($c_val.getString("key")));
                                                        }
                                                        $rf.put($c_val);
                                                    }

                                                    $jrd.put("view_details", GlobalFunctions.view_details(new JSONObject().put("id", $jrd.getJSONObject("saved_record").getString("id")).put("record", $jrd.getJSONObject("saved_record")), new JSONObject().put("fields", $rf)));
                                                }
                                            }else{

                                                if ($check_data.has("display_fields") && $check_data.getJSONArray("display_fields").length() > 0) {
                                                    $jrd.put("display_fields", $check_data.getJSONArray("display_fields"));
                                                }

                                            }
                                        }
                                    }
                                }
                            }else{
                                html = "No data was contained in the form";
                            }
                        }
                    }catch( Exception e ){
                        html = "Unable to generate form<br /><br />" + e.getMessage();
                        GlobalFunctions.nw_dev_handler(
                                new JSONObject()
                                        .put("return", html )
                                        .put("input", new JSONObject( $tableData.toString() ) )
                                        .put("function", "nwpDataTable.saveDataForm" )
                                        .put("exception", true )
                                        .put("fatal", true ) , e
                        );
                    }
                }else{
                    html = "Missing / Corrupt Table Configuration";
                }
            }else{
                html = $error_msg;
            }
        }else{
            html = "Table Name was not specified";
        }

        if( html.isEmpty() ){
            GlobalFunctions.app_notice_type = "success";
            GlobalFunctions.app_notice = "<h4>Successfully Saved</h4>";
            GlobalFunctions.app_notice_only = false;
            GlobalFunctions.app_reload_datatable = true;

            if( $check_data.has("checksum") ){
                GlobalFunctions.get_form_checksum( $check_data.getString("checksum"), true );
            }

            if( $tableData.has( "todo" ) ){
                switch( $tableData.getString( "todo" ) ){
                case "create_new_record":
                    break;
                case "delete":
                    GlobalFunctions.app_notice = "<h4>Successfully Deleted</h4>";
                    GlobalFunctions.app_notice_only = true;
                    break;
                case "edit":
                    break;
                }
            }
        }else{
            GlobalFunctions.app_notice_type = "error";
            GlobalFunctions.app_notice = html;
            GlobalFunctions.app_notice_only = true;
            $jrd.put( "error", html );
        }

        return $jrd;
    }

    public static String getDataForm( JSONObject $tableData ){
        String html = "";
        String $title = "";
        String $error_msg = "";
        if( $tableData.has( "title" ) ){
            $title = $tableData.getString( "title" );
        }
        String submitFormAction = "submit_form";
        String submitFormAction2 = submitFormAction;

        if( $tableData.has( "table" ) ){
            String $table = $tableData.getString( "table" );

            JSONObject r = new JSONObject();

            JSONObject $my_ins = $tableData;
            $my_ins.put( "table", $table );

            JSONObject dep;
            if( $tableData.has( "table_settings" ) ){
                dep = $tableData.getJSONObject( "table_settings" );
            }else{
                dep = new JSONObject( GlobalFunctions.fileGetContentsExternal( "dependencies/" + $table + ".json" ) );

            }
            //System.out.println( dep );
            if( dep.has( "label" ) && $title.isEmpty() ){
                $title = dep.getString( "label" );
                Boolean editOP = $tableData.has( "edit_record" )?$tableData.getBoolean( "edit_record" ):false;

                if( $tableData.has( "todo" ) ){
                    switch( $tableData.getString( "todo" ) ){
                    case "create_new_record":
                        $title = "New: " + $title;
                        break;
                    case "edit":
                        editOP = true;
                        break;
                    }
                }

                if( $tableData.has( "todo" )
                        && dep.has( "datatable_options" )
                        && dep.getJSONObject( "datatable_options" ).has("submit_form_actions")
                        && dep.getJSONObject( "datatable_options" ).getJSONObject("submit_form_actions").has( $tableData.getString( "todo" ) )
                        && ! dep.getJSONObject( "datatable_options" ).getJSONObject("submit_form_actions").getString( $tableData.getString( "todo" ) ).isEmpty()
                ){
                    submitFormAction2 = dep.getJSONObject( "datatable_options" ).getJSONObject("submit_form_actions").getString( $tableData.getString( "todo" ) );
                    submitFormAction = "display_sub_menu&todo=";
                }

                if( editOP ){
                    $title = "Edit: " + $title;
                    if( dep.has( "fields" ) && $tableData.has( "id" ) && ! $tableData.getString( "id" ).isEmpty() ){
                        JSONObject grd = new JSONObject();
                        grd.put( "id", $tableData.getString( "id" ) );
                        grd.put( "fields", dep.getJSONObject( "fields" ) );
                        grd.put( "table", $table );

                        JSONObject $rd = GlobalFunctions.get_record( grd );
                        if( $rd.has( "id" ) ){
                            $my_ins.put( "record_id", $tableData.getString( "id" ) );
                            $my_ins.put( "values", $rd );
                        }
                    }else{
                        $error_msg = "Edit operation stopped because record id was not specified";
                    }
                }

            }

            if( $error_msg.isEmpty() ){
                if( dep.has( "labels" ) && dep.has( "form_order" ) ){

                    $my_ins.put( "labels", dep.getJSONObject( "labels" ) );
                    $my_ins.put( "form_order", dep.getJSONArray( "form_order" ) );

                    JSONObject options = new JSONObject();
                    JSONObject disable_form_element = new JSONObject();
                    JSONObject form_display_not_editable_value = new JSONObject();

                    String $action = "?action="+ submitFormAction +"&nwp2_source=" + $table;
                    if( $tableData.has( "action" ) ){
                        $action = $tableData.getString( "action" );
                    }else if( $tableData.has( "todo" ) ){
                        $action += "&nwp2_action="+ submitFormAction2 +"&nwp2_todo=" + $tableData.getString( "todo" );
                    }else{
                        $action += "&nwp2_todo="+ submitFormAction2;
                    }

                    if( $tableData.has( "params" ) ){
                        $action += $tableData.getString( "params" );
                    }

                    Boolean replace_popup = false;
                    if( $tableData.has( "popup" ) && $tableData.getBoolean( "popup" ) )replace_popup = true;

                    if( $tableData.has( "replace_popup" ) && $tableData.getBoolean( "replace_popup" ) )replace_popup = true;

                    if( $tableData.has( "html_replacement_selector" ) && ! $tableData.getString( "html_replacement_selector" ).isEmpty() ){
                        $action += "&html_replacement_selector=" + $tableData.getString( "html_replacement_selector" );
                    }else if( replace_popup ){
                        $action += "&html_replacement_selector=modal-replacement-handle";
                    }

                    $my_ins.put( "options", options );
                    $my_ins.put( "disable_form_element", disable_form_element );
                    $my_ins.put( "form_display_not_editable_value", form_display_not_editable_value );

                    $my_ins.put( "html_id", $table );
                    $my_ins.put( "action_to_perform", $action );
                    $my_ins.put( "form_class", "activate-ajax" );
                    $my_ins.put( "method", "post" );

                    r.put( "data", $my_ins );
                    try{
                        html = nwpFormGen.createForm( r );
                    }catch( Exception e ){
                        html = "Unable to generate form<br /><br />" + e.getMessage();
                    }
                }else{
                    html = "Missing / Corrupt Table Configuration";
                }
            }else{
                html = $error_msg;
            }
        }else{
            html = "Table Name was not specified";
        }

        if( $tableData.has( "popup" ) && $tableData.getBoolean( "popup" ) ){
            JSONObject $popdata = new JSONObject();
            $popdata.put( "modal_title", $title );
            $popdata.put( "modal_body", html );

            html = getPopup( $popdata );
        }

        return html;
    }

    public static String getDataTable( JSONObject $tableData ){
        String html = "";
        if( $tableData.has( "table" ) ){
            String $table = $tableData.getString( "table" );

            JSONObject r = new JSONObject();
            JSONObject dbs = new JSONObject();

            JSONObject $my_ins = $tableData;
            $my_ins.put( "table", $table );

            if( $my_ins.has( "datatable_settings" ) ){
                dbs = $my_ins.getJSONObject( "datatable_settings" );
            }

            JSONObject dep;
            if( $tableData.has( "table_settings" ) && $tableData.getJSONObject( "table_settings" ).has( "labels" ) ){
                dep = $tableData.getJSONObject( "table_settings" );
            }else{
                dep = GlobalFunctions.get_json( $table );
            }

            if( $tableData.has("filter") && ! $tableData.getString("filter").isEmpty()
                    && dep.has( "filter" ) && dep.getJSONObject("filter").has( $tableData.getString("filter") )
                    && dep.getJSONObject("filter").getJSONObject( $tableData.getString("filter") ).has("fields")
                    && dep.getJSONObject("filter").getJSONObject( $tableData.getString("filter") ).getJSONArray("fields").length() > 0
            ){
                //$query.put("where", "" );
                JSONObject queryObj = ( $tableData.has("query")  )?$tableData.getJSONObject("query"):new JSONObject();
                String $where = ( queryObj.has("where") )?queryObj.getString("where"):"";

                JSONArray fFields = dep.getJSONObject("filter").getJSONObject( $tableData.getString("filter") ).getJSONArray("fields");
                if( dep.has( "fields" ) ) {
                    for (int i1 = 0; i1 < fFields.length(); i1++) {
                        if (fFields.getJSONObject(i1).has("name")
                                && dep.getJSONObject( "fields" ).has( fFields.getJSONObject(i1).getString("name") )
                                && fFields.getJSONObject(i1).has("value")
                                && fFields.getJSONObject(i1).has("condition")
                        ) {
                            $where += " AND ["+ $table +"].["+ dep.getJSONObject( "fields" ).getString( fFields.getJSONObject(i1).getString("name") ) +"] "
                                    + fFields.getJSONObject(i1).getString("condition") + " "
                                    + fFields.getJSONObject(i1).getString("value");
                        }
                    }

                    queryObj.put("where", $where );
                    $my_ins.put( "query", queryObj );

                }
            }


            //System.out.println( dep );
            //System.out.println( dbs );

            if( dep.has( "labels" ) && dep.has( "form_order" ) ){
                $my_ins.put( "labels", dep.getJSONObject( "labels" ) );
                $my_ins.put( "form_order", dep.getJSONArray( "form_order" ) );

                if( dep.has( "label" ) && !dbs.has( "title" ) ){
                    dbs.put( "title", dep.getString( "label" ) );
                }
                if( ! dbs.has( "datatable_options" ) && dep.has( "datatable_options" ) ){
                    dbs.put( "datatable_options", dep.getJSONObject( "datatable_options" ) );
                }
                $my_ins.put( "datatable_settings", dbs );

                r.put( "data", $my_ins );
                html = nwpDataTable.myNwpDataTable( r );
            }else{
                html = "Missing / Corrupt Table Configuration";
            }
        }else{
            html = "Table Name was not specified";
        }

        return html;
    }

    public static JSONArray getSampleOrder(){
        JSONArray $form_label = new JSONArray();
        return $form_label;
    }

    public static JSONObject getSampleLabel(){
        JSONObject $form_label = new JSONObject();
        return $form_label;
    }

    public static JSONObject ajaxServerData( JSONArray pdata ){
        JSONObject $form_label = new JSONObject();
        JSONArray $form_order = new JSONArray();

        //$form_label = getSampleLabel();
        //$form_order = getSampleOrder();

        JSONObject r = new JSONObject();
        JSONObject pd2 = new JSONObject();
        JSONObject $query = new JSONObject();
        //System.out.println( pdata );
        if( pdata.length() > 0 ){
            for( int i1 = 0; i1 < pdata.length(); i1++ ){
                JSONObject pt = pdata.getJSONObject( i1 );
                if( pt.has( "name" ) && pt.has( "value" ) ){
                    pd2.put( pt.getString( "name" ), pt.get( "value" ) );
                }
            }
        }

        Integer $itotal = 1;
        Integer $itotal_dis = 1;
        Integer numrows = 0;
        Integer offset = 0;
        Integer sEcho = 1;
        String sSearch = "";
        JSONArray array = new JSONArray();
        if( pd2.has( "sEcho" ) ){
            sEcho = pd2.getInt( "sEcho" );
        }
        if( pd2.has( "sSearch" ) ){
            sSearch = pd2.getString( "sSearch" ).toLowerCase();
        }
        if( pd2.has( "iDisplayLength" ) ){
            numrows = pd2.getInt( "iDisplayLength" );
        }
        if( pd2.has( "iDisplayStart" ) ){
            offset = pd2.getInt( "iDisplayStart" );
        }
        //System.out.println( pd2 );
        if( pd2.has( "more_data" ) ){
            JSONObject md = pd2.getJSONObject( "more_data" );
            JSONObject dep = new JSONObject();
            if( md.has( "table" ) ){
                dep = GlobalFunctions.get_json( md.getString( "table" ) );
            }

            if( md.has( "table" ) && dep.has( "form_order" ) && dep.has( "labels" ) ){
                $form_label = dep.getJSONObject( "labels" );
                $form_order = dep.getJSONArray( "form_order" );

                if( md.has( "query" ) ){
                    $query = md.getJSONObject( "query" );
                }

                //System.out.println( fields.names() );
                //Integer fields_len = fields.length();
                Integer fields_len = $form_order.length();

                //System.out.println(fields_len);
                //System.out.println();
                if( fields_len > 0 ){
                    String table = md.getString( "table" );
                    //System.out.println(md.getString("table"));

                    //query db
                    JSONObject $qs = new JSONObject();
                    //$qs.put("query", "SELECT TOP 5 * FROM " + table + " WHERE record_status='1' ");
                    String $where = "";
                    String $limit = "";
                    String $limit1 = "";
                    if( numrows > 0 ){
                        //$limit = " OFFSET "+ offset + " ROWS FETCH NEXT " + numrows + " ROWS ONLY ";
                        $limit1 = " TOP " + numrows;
                    }
                    if( $query.has( "limit" ) ){
                        $limit1 = $query.getString( "limit" );

                    }

                    String $order = "";
                    //System.out.println( pd2 );
                    if( pd2.has( "iSortCol_0" ) && pd2.has( "sSortDir_0" ) && pd2.has( "iSortingCols" ) ){

                        try {
                            for (int $i = 0; $i < pd2.getInt("iSortingCols"); $i++) {
                                if (pd2.has("iSortCol_" + $i) && pd2.has("bSortable_" + pd2.getInt("iSortCol_" + $i)) && pd2.getBoolean("bSortable_" + pd2.getInt("iSortCol_" + $i))) {
                                    Integer $scol = pd2.getInt("iSortCol_" + $i);
                                    String descAsc = pd2.getString("sSortDir_" + $i);
                                    //System.out.println($scol);
                                    //System.out.println($form_order.getString($scol));

                                    if (!$order.isEmpty()) {
                                        $order += ", ";
                                    }
                                    $order += "[" + table + "].[" + $form_order.getString($scol) + "] " + descAsc;
                                }

                            }
                        }catch (Exception e){
                            //System.out.println( "Datatable Sorting Error:" + e.getMessage() );
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", "Datatable Sorting Error:" + e.getMessage() )
                                            .put("input", new JSONObject().put("pdata", pdata) )
                                            .put("function", "nwpDataTable.ajaxServerData" )
                                            .put("exception", true )
                                            .put("fatal", true ) , e
                            );
                        }

                    }

                    if ( $order.isEmpty() ) {
                        $query.put( "order", " ORDER BY [" + table + "].[modification_date] DESC" );
                    }else{
                        $query.put( "order", " ORDER BY " + $order );
                    }

                    /*
                    $qs.put("query", "SELECT "+$limit1+" * FROM " + table + " WHERE record_status='1' " + $order + $limit);
                    JSONObject $qr = DBConn.executeQuery( $qs );*/

                    if( $query.has( "where" ) ){
                        $where = $query.getString( "where" );
                    }
                    if( ! sSearch.isEmpty() ){
                        String $k;
                        String $where1 = "";
                        for( Integer i1 = 0; i1 < $form_label.names().length(); i1++ ) {
                            $k = $form_label.names().getString(i1);
                            if ( $form_label.has( $k ) && $form_label.getJSONObject( $k ).has("form_field") ) {
                                switch( $form_label.getJSONObject( $k ).getString("form_field") ){
                                    case "text":
                                    case "textarea":
                                        if ( $form_label.getJSONObject( $k ).has("display_position") && $form_label.getJSONObject( $k ).getString("display_position").equals("display-in-table-row") ) {
                                            if( ! $where1.isEmpty() ){
                                                $where1 += " OR ";
                                            }
                                            $where1 += " LOWER( ["+ table +"].["+ $k +"] ) LIKE '%"+ sSearch +"%' ";
                                        }
                                        break;
                                }
                            }
                        }

                        if( ! $where1.isEmpty() ){
                            $where += " AND ( "+ $where1 +" ) ";
                        }
                    }

                    if( $query.has( "order" ) ){
                        $qs.put( "order", $query.getString( "order" ) );
                    }
                    if( $query.has( "join" ) ){
                        $qs.put( "join", $query.getString( "join" ) );
                    }
                    if( $query.has( "group" ) ){
                        $qs.put( "group", $query.getString( "group" ) );
                    }
                    if( ! $where.isEmpty() ){
                        $qs.put( "where", $where );
                    }
                    $qs.put( "select", "SELECT " + $limit1 + " ["+table+"].* " );
                    $qs.put( "table", table );
                    JSONObject $qr = GlobalFunctions.get_records( $qs );
                    //System.out.println( $qs.get("query") );

                    //System.out.println( $qr );

                    if( $qr.has( "row_count" ) && $qr.has( "row" ) && $qr.getInt( "row_count" ) > 0 ){

                        String $key1 = "";
                        String $col_key = "";
                        JSONObject $val1 = new JSONObject();
                        JSONObject $s_field = new JSONObject();
                        JSONArray $all_row = $qr.getJSONArray( "row" );
                        JSONObject $a_row = new JSONObject();
                        String $s_form_field = "";
                        String $cell_data = "";
                        String $cell_value = "";
                        String $cell_id = "";
                        Integer $sn = 0;
                        Boolean $show_field = false;

                        for( int ir = 0; ir < $qr.getInt( "row_count" ); ir++ ){
                            //String[] jsf = new String[ fields_len ];
                            JSONArray jsf = new JSONArray();
                            $a_row = $all_row.getJSONObject( ir );
                            $cell_id = "";
                            if( $a_row.has( "id" ) ){
                                $cell_id = $a_row.get( "id" ).toString();
                                ++$sn;
                            }

                            //for (int i2 = 0; i2 < fields_len; i2++) {
                            for( int i2 = 0; i2 < $form_order.length(); i2++ ){
                                //$key1 = fields.names().getString(i2);
                                //$val1 = new JSONObject(fields.get($key1).toString());
                                $col_key = $form_order.getString( i2 );

                                //if ($val1.has("Field")) {
                                //$col_key = $val1.getString("Field");
                                if( $form_label.has( $col_key ) ){
                                    $s_field = $form_label.getJSONObject( $col_key );
                                }else{
                                    $s_field = new JSONObject();
                                    $s_field.put( "form_field", "text" );
                                    $s_field.put( "display_position", "" );
                                    $s_field.put( "field_label", $col_key );
                                }

                                if( $s_field.has("display_position") && ! $s_field.getString("display_position").equals("do-not-display-in-table") ) {
                                    $cell_value = "";
                                    if ($a_row.has($col_key)) {
                                        $cell_value = $a_row.get($col_key).toString();
                                    }

                                    switch ($col_key) {
                                        case "id":
                                            //jsf[i2] = "<b id=\"" + $cell_id + "\" class=\"datatables-record-id\" style=\"font-size:0+8em; \">" + $sn + "</b>";
                                            jsf.put("<b id=\"" + $cell_id + "\" class=\"datatables-record-id\" style=\"font-size:0+8em; \">" + $sn + "</b>");
                                            break;
                                        case "modified_source":
                                        case "created_source":
                                        case "creator_role":
                                        case "serial_num":
                                        case "device_id":
                                        case "record_status":
                                        case "ip_address":
                                            break;
                                        case "created_by":
                                        case "modified_by":
                                            jsf.put( GlobalFunctions.get_record_name( new JSONObject().put("id", $cell_value ).put("table", nwpAccessRoles.users_table ) ) );
                                            break;
                                        case "creation_date":
                                        case "modification_date":
                                            jsf.put( GlobalFunctions.convert_timestamp_to_date( $cell_value, "date-5time", 0 ) );
                                            break;
                                        default:
                                            $s_form_field = "";
                                            if ($s_field.has("form_field")) {
                                                $s_form_field = $s_field.getString("form_field");
                                            }

                                            //$cell_data = $col_key + $s_form_field;
                                            //jsf[i2] = $cell_value;
                                            jsf.put( GlobalFunctions.getValue( $cell_value, $s_field, new JSONObject().put("row", $a_row ) ) );
                                            break;
                                    }
                                }
                            }

                            array.put( jsf );
                        }
                    }else{
                        //no result found

                    }
                }
            }
        }

        r.put( "sEcho", sEcho );
        r.put( "iTotalRecords", $itotal );
        r.put( "iTotalDisplayRecords", $itotal_dis );
        r.put( "aaData", array );

        return r;
    }

    public static JSONObject hideShowColumn( JSONObject columnData ){
        JSONObject r = new JSONObject();

        String column_name = columnData.has("column_toggle_name")?columnData.getString("column_toggle_name"):"";
        Integer column_num = columnData.has("column_toggle_num")? ( Integer.parseInt( columnData.getString("column_toggle_num") ) ):0;
        /*if( column_num > 0 ){
            --column_num;
        }*/
        String column_state = "checked";

        r.put( "column_name", column_name );
        r.put( "column_num", column_num );
        r.put( "column_state", column_state );
        r.put( "status", "column-toggle" );

        return r;
    }

    public static JSONObject myNwpDataTableDefaultSettings(){
        JSONObject r = new JSONObject();

        //JSONObject $my_ins = new JSONObject( "{\"fields\":{},\"table\":\"\",\"admin_user\":\"\",\"datatable_settings\":{\"show_toolbar\":0,\"show_navigation_pane\":0,\"select_audit_trail\":0,\"show_add_new\":0,\"show_import_excel_table\":0,\"show_add_new_memo_report_letter\":0,\"show_add_new_scanned_file\":0,\"show_add_new_label\":0,\"show_advance_search\":0,\"show_column_selector\":0,\"show_units_converter\":0,\"show_units_converter_volume\":1,\"show_units_converter_currency\":1,\"show_units_converter_currency_per_unit_kvalue\":1,\"show_units_converter_kvalue\":1,\"show_units_converter_time\":1,\"show_units_converter_pressure\":1,\"show_units_converter_volume_per_day\":1,\"show_units_converter_heating_value\":1,\"show_records_view_options_selector\":0,\"array_of_view_options\":{},\"show_get_images_button\":0,\"show_synchronization_button\":0,\"show_attach_files_to_gas_sales_agreements\":0,\"show_edit_password_button\":0,\"show_edit_passphrase_button\":0,\"show_edit_button\":0,\"user_can_edit\":0,\"show_delete_button\":0,\"show_status_update\":0,\"show_record_assign\":0,\"show_delete_forever\":0,\"show_restore_button\":0,\"show_generate_report\":0,\"show_timeline\":0,\"timestamp_action\":\"\",\"show_details\":0,\"show_serial_number\":0,\"show_verification_status\":0,\"show_creator\":0,\"show_modifier\":0,\"show_action_buttons\":1,\"table_class\":\"display-no-scroll\",\"current_module_id\":\"\",\"multiple_table_header\":0,\"multiple_table_header_cells\":\"\",\"skip_fields\":{}}}" );
        JSONObject $my_ins = new JSONObject();

        JSONObject $fields = new JSONObject(  );
        JSONObject $form_label = getSampleLabel();
        JSONArray $form_order = getSampleOrder();

        $my_ins.put( "fields", $fields );
        $my_ins.put( "labels", $form_label );
        $my_ins.put( "form_order", $form_order );
        r.put( "data", $my_ins );

        return r;
    }

    public static String myNwpDataTable( JSONObject $data ){
        //JSONObject $data = myNwpDataTableDefaultSettings();
        String $returning_html_data = "";

        JSONObject $my_ins = $data.getJSONObject( "data" );
        JSONObject $my_insd = new JSONObject( "{\"fields\":{},\"table\":\"workflow\",\"admin_user\":\"\",\"datatable_settings\":{\"show_toolbar\":1,\"show_navigation_pane\":0,\"select_audit_trail\":0,\"show_add_new\":1,\"show_import_excel_table\":0,\"show_add_new_memo_report_letter\":0,\"show_add_new_scanned_file\":0,\"show_add_new_label\":0,\"show_advance_search\":0,\"show_column_selector\":1,\"show_units_converter\":0,\"show_units_converter_volume\":1,\"show_units_converter_currency\":1,\"show_units_converter_currency_per_unit_kvalue\":1,\"show_units_converter_kvalue\":1,\"show_units_converter_time\":1,\"show_units_converter_pressure\":1,\"show_units_converter_volume_per_day\":1,\"show_units_converter_heating_value\":1,\"show_records_view_options_selector\":0,\"array_of_view_options\":{},\"show_get_images_button\":0,\"show_synchronization_button\":0,\"show_attach_files_to_gas_sales_agreements\":0,\"show_edit_password_button\":0,\"show_edit_passphrase_button\":0,\"show_edit_button\":1,\"user_can_edit\":0,\"show_delete_button\":1,\"show_status_update\":0,\"show_record_assign\":0,\"show_delete_forever\":0,\"show_restore_button\":0,\"show_generate_report\":0,\"show_timeline\":0,\"timestamp_action\":\"\",\"show_details\":0,\"show_serial_number\":1,\"show_verification_status\":0,\"show_creator\":0,\"show_modifier\":0,\"show_action_buttons\":1,\"table_class\":\"display-no-scroll\",\"current_module_id\":\"\",\"multiple_table_header\":0,\"multiple_table_header_cells\":\"\",\"skip_fields\":{}}}" );

        String $key1 = "";
        if( $my_insd.names().length() > 0 ){
            for( int i2 = 0; i2 < $my_insd.names().length(); i2++ ){
                $key1 = $my_insd.names().getString( i2 );
                if( !$my_ins.has( $key1 ) ){
                    $my_ins.put( $key1, $my_insd.get( $key1 ) );
                }
            }


            String akey = "datatable_settings";
            JSONObject $my_insd2 = $my_insd.getJSONObject( akey );
            JSONObject $replace = new JSONObject();
            if( $my_ins.has( akey ) ){
                $replace = $my_ins.getJSONObject( akey );
            }
            if( $my_insd2.names().length() > 0 ){
                for( int i3 = 0; i3 < $my_insd2.names().length(); i3++ ){
                    $key1 = $my_insd2.names().getString( i3 );
                    if( !$replace.has( $key1 ) ){
                        $replace.put( $key1, $my_insd2.get( $key1 ) );
                    }
                }
                $my_ins.put( akey, $replace );
            }

        }

        //System.out.println( $my_ins );
        Integer $t = 0;

        String $hide_show_col = "";

        String $hsbc = "";

        String $header_left = "";

        String $header_right = "";

        String $header_bottom = "";

        JSONArray $form_order = $my_ins.getJSONArray( "form_order" );
        JSONObject $form_label = $my_ins.getJSONObject( "labels" );
        //JSONObject $fields = $my_ins.getJSONObject("fields");
        JSONObject $data_tb_set = $my_ins.getJSONObject( "datatable_settings" );

        String $classname = $my_ins.getString( "table" );
        //System.out.println($my_ins);

        String $row_span = "";

        String $table_name = $classname + "-datatable";

        String $table_name_container = "dynamic";

        JSONObject $attr = new JSONObject();
        //$attr.put("fields", $fields);
        //$attr.put("form_label", $form_label);
        //$attr.put("form_order", $form_order);
        if( $my_ins.has( "query" ) ){
            $attr.put( "query", $my_ins.getJSONObject( "query" ) );
        }

        String $plugin = "";

        String $database_table_field_intepretation_function_name = "";

        JSONObject $custom_parameters = new JSONObject();

        String $sel = "";

        String $sel_attr = "";

        Integer $cols = 0;

        String $selection = "";

        if( $data_tb_set.has( "data_table_name" ) && $data_tb_set.getString( "data_table_name" ) != "" ){
            $table_name_container = $table_name + "-container";
        }

        if( $data_tb_set.has( "table_data" ) && $data_tb_set.getJSONObject( "table_data" ).length() > 0 ){
            $attr = $data_tb_set.getJSONObject( "table_data" );
        }

        if( $data_tb_set.has( "plugin" ) && $data_tb_set.getString( "plugin" ) != "" ){
            $plugin = $data_tb_set.getString( "plugin" );
            $attr.put( "plugin", $plugin );
        }

        if( $data_tb_set.has( "show_selection" ) && $data_tb_set.getJSONObject( "show_selection" ).length() > 0 ){
            $attr.put( "show_selection", $data_tb_set.getJSONObject( "show_selection" ) );
        }


        //GET ARRAY OF VALUES FOR FORM LABELS
        $database_table_field_intepretation_function_name = $classname;
        if( $data_tb_set.has( "real_table" ) && $data_tb_set.getString( "real_table" ) != "" ){
            $database_table_field_intepretation_function_name = $data_tb_set.getString( "real_table" );
            $attr.put( "real_table", $database_table_field_intepretation_function_name );
        }

        if( $data_tb_set.has( "db_table" ) && $data_tb_set.getString( "db_table" ) != "" ){
            $attr.put( "db_table", $data_tb_set.getString( "db_table" ) );
        }

        String $returning_html_data2 = "";
        if( $data_tb_set.has( "hide_title" ) && !$data_tb_set.getString( "hide_title" ).isEmpty() ){
            $returning_html_data2 += "<div>";
        }else{
            $returning_html_data2 += "<div class=\"page-table-wrapper\" style=\" background:#fff; border: 1px solid #ddd; \">";
            $returning_html_data2 += "<div class=\"page-title-wrapperX \" style=\"background: #efefef;  padding: 1px 15px; box-shadow: 0px 2px 1px 1px #d8d8d8;\">";
            $returning_html_data2 += "<div class=\"page-title-headingX \">";
            $returning_html_data2 += "<h4 id=\"datatable-title\" style=\"font-size:17px;\"><strong classX=\"text-success\">";
            if( $data_tb_set.has( "title" ) )
                $returning_html_data2 += $data_tb_set.getString( "title" );
            $returning_html_data2 += "</strong></h4>";
            $returning_html_data2 += "</div>";
            $returning_html_data2 += "<div class=\"page-title-actions\" style=\"float: right; margin-top: -36px;\">";

            $returning_html_data2 += "</div>";
            $returning_html_data2 += "</div>";
        }

        $returning_html_data2 += "<div style=\"padding:15px;\" id=\"data-table-section\" class=\"shopping-cart-tableX\">";

        $returning_html_data += "<textarea class=\"hyella-data\" style=\"height:1px;\" id=\"" + $table_name + "-attributes\">" + $attr.toString() + "</textarea>";
        $returning_html_data += "<table xyrcp cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"table table-striped table-bordered table-hover dataTable display " + ( $data_tb_set.has( "table_class" ) ? $data_tb_set.getString( "table_class" ) : "display-no-scroll" ) + " " + $classname + "\" id=\"" + $table_name + "\" class-name=\"" + $classname + "\" container=\"" + $table_name_container + "\">";
        $returning_html_data += "<thead>";

        $sel = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        $sel_attr = " style=\" max-width:65px;\" ";
        JSONObject $ssel = new JSONObject();
        if( $data_tb_set.has( "show_selection" ) ){
            $ssel = $data_tb_set.getJSONObject( "show_selection" );
        }

        if( $ssel.has( "action" ) && $ssel.getString( "action" ) != "" ){
            $sel = "<input type=\"checkbox\" id=\"datatable-select-all-checkbox\" />";
        }

        if( $ssel.has( "title" ) && $ssel.getString( "title" ) != "" ){
            $sel_attr = "";
            $sel = $ssel.getString( "title" );
        }

        if( $data_tb_set.has( "show_details" ) && $data_tb_set.getInt( "show_details" ) > 0 ){
            $header_left += "<th class=\"table-header nwp-datatable-details-show remove-before-export\" " + $sel_attr + " " + $row_span + " >";
            $header_left += $sel;
            $header_left += "</th>";
        }

        //CHECK WHETHER OR NOT TO SHOW SERIAL NUMBER
        if( $data_tb_set.has( "show_serial_number" ) && $data_tb_set.getInt( "show_serial_number" ) > 0 ){
            //Show Serial Number
            $header_left += "<th class=\"table-header nwp-datatable-sn-show \" style=\"min-width:40px; max-width:65px;\" " + $row_span + ">";
            $header_left += "S/N";
            $header_left += "</th>";
        }

        //System.out.println( $form_order.length() );
        if( $form_order.length() > 0 ){
            try{
                String $field_id = "";
                JSONObject $swatch = new JSONObject( "{\"created_by\":1,\"creation_date\":1,\"modified_by\":1,\"modification_date\":1}" );
                //System.out.println($form_order);
                for( int i1 = 0; i1 < $form_order.length(); i1++ ){
                    //for(int i1 = 0; i1 < $fields.names().length(); i1++){
                    //String fk = $fields.names().getString(i1);

                    JSONObject $field_details = new JSONObject();
                    //JSONObject a1 = new JSONObject( $fields.get( fk ).toString() );

                    //$field_id = "";
                    $field_id = $form_order.getString( i1 );


                    /*if( a1.has( "0" ) ){
                        $field_id = a1.getString( "0" );
                    }*/
                    if( $form_label.has( $field_id ) ){
                        $field_details = $form_label.getJSONObject( $field_id );
                    }

                    Boolean $show_field = false;

                    if( $swatch.has( $field_id ) ){
                        $show_field = true;
                    }

                    if( $field_details.length() > 0 || $show_field == true ){

                        if( ( $field_details.has( "display_position" ) && ( $field_details.getString( "display_position" ).equals( "display-in-table-row" ) || ( $field_details.getString( "display_position" ).equals( "display-in-admin-table" ) && $my_ins.has( "admin_user" ) ) ) ) || $show_field ){
                            $header_bottom += "<th class=\"" + $field_id + "\">";

                            if( $field_details.has( "field_label" ) && $field_details.getString( "field_label" ) != "" ){

                                $header_bottom += $field_details.has( "text" ) ? $field_details.getString( "text" ) : $field_details.getString( "field_label" );

                                $hide_show_col += get_column_toggler_checkboxes( $field_id, $classname, "", $field_details );
                            }else{

                                $header_bottom += $field_id.replaceAll( "_", " " ).toUpperCase();

                                $hide_show_col += get_column_toggler_checkboxes( $field_id, $classname, "", $field_details );
                            }

                            $header_bottom += "</th>";


                            ++$cols;
                            if( $field_details.has( "form_field" ) ){
                                //$display[$t++] = $field_details.getString("form_field" );
                            }else{
                                //$display[$t++] = $field_id;
                            }
                        }
                    }


                }
            }catch( Exception e ){
                $header_bottom += "No Table Fields";
            }
        }

        //DETERMINES WHETHER OR NOT TO SHOW RECORD CREATOR
        if( $data_tb_set.has( "show_creator" ) && $data_tb_set.getInt( "show_creator" ) > 0 ){
            $header_right += "<th class=\"header remove-before-export\" " + $row_span + ">";
            $header_right += "Created By";
            $header_right += "</th>";
            $header_right += "<th class=\"header remove-before-export\" " + $row_span + ">";
            $header_right += "Created On";
            $header_right += "</th>";
        }

        //DETERMINES WHETHER OR NOT TO SHOW RECORD MODIFIER
        if( $data_tb_set.has( "show_modifier" ) && $data_tb_set.getInt( "show_modifier" ) > 0 ){
            $header_right += "<th class=\"header remove-before-export\" " + $row_span + ">";
            $header_right += "Modified By";
            $header_right += "</th>";
            $header_right += "<th class=\"header remove-before-export\" " + $row_span + ">";
            $header_right += "Modified On";
            $header_right += "</th>";
        }


        $returning_html_data += "<tr>";
        $returning_html_data += $header_left + $header_bottom + $header_right;
        $returning_html_data += "</tr>";

        $returning_html_data += "</thead>";

        $returning_html_data += "<tbody>";
        $returning_html_data += "<tr>";
        $returning_html_data += "<td colspan=\"" + $cols + "\" class=\"dataTables_empty\">Loading data from server</td>";
        $returning_html_data += "</tr>";
        $returning_html_data += "</tbody>";
        $returning_html_data += "</table>";

        //$selection = $this->_get_selection_form();
        $selection = "";

        JSONObject $spcol = new JSONObject();
        if( $data_tb_set.has( "datatable_split_screen" ) ){
            $spcol = $data_tb_set.getJSONObject( "datatable_split_screen" );
        }

        Integer $split_screen = 0;
        if( $spcol.has( "col" ) ){
            $split_screen = $spcol.getInt( "col" );

            String $split_screen_action = $spcol.has( "action" ) ? $spcol.getString( "action" ) : "";

            String $split_screen_content = $spcol.has( "content" ) ? $spcol.getString( "content" ) : get_quick_view_default_message_settings();

            if( !$split_screen_action.isEmpty() ){
                $split_screen_action += "&html_replacement_selector=datatable-split-screen-" + $classname;
            }

            if( $split_screen > 0 && $split_screen < 12 ){
                $returning_html_data = "<div class=\"row\"><div class=\"col-md-" + ( 12 - $split_screen ) + "\">" + $returning_html_data + "</div><div class=\"col-md-" + ( $split_screen ) + "\">" + $selection + "<div class=\"datatable-split-screen resizable-height\" style=\"overflow-y:auto; overflow-x:hidden;\" id=\"datatable-split-screen-" + $classname + "\" action=\"" + $split_screen_action + "\" data-subtract=\"156\">" + $split_screen_content + "</div></div></div>";
            }
        }

        $returning_html_data = $returning_html_data2 + "<div id=\"" + $table_name_container + "\" class=\"dynamic\" data-table=\"" + $classname + "\">" + GetToolbar( $hide_show_col, $data_tb_set, $classname ) + $returning_html_data;
        //$returning_html_data = $returning_html_data2 + "<div id=\""+$table_name_container+"\" class=\"dynamic\" data-table=\""+$classname+"\">" + $returning_html_data;

        $returning_html_data += "</div>";
        $returning_html_data += "</div>";
        if( $split_screen <= 0 ) {
            //closure AAA
            $returning_html_data += "</div>";
        }

        return $returning_html_data;
        //out.println( $returning_html_data );
    }

    public static String get_quick_view_default_message_settings(){
        return "<div style=\"text-align:center;\"><br /><br /><br /><h2>Quick View Window</h2><hr />Select a record by clicking on it</div>";
    }

    public static String get_column_toggler_checkboxes( String $column_name, String $table, String $module, JSONObject $field_details ){
        String $display_label = "";

        String $sq = "";

        String $column_state = "1";

        String $cls = "";
        if( $field_details.has( "field_label" ) ){
            $display_label = $field_details.getString( "field_label" );
        }else{
            $display_label = $column_name.replaceAll( "_", " " ).toUpperCase();
        }

        if( $field_details.has( "default_field_label" ) && $field_details.getString( "default_field_label" ) != "" ){
            $display_label = $field_details.getString( "default_field_label" );
        }

        //$sq = md5("column_toggle".$_SESSION["key"]);

        //Hide Columns by default
        if( ( $field_details.has( "default_appearance_in_table_fields" ) ) && $field_details.getString( "default_appearance_in_table_fields" ).equals( "show" ) ){
            $column_state = "checked=\"checked\"";
            //$_SESSION[$sq][$table][$column_name] = 1;
        }

        return "<li><label class=\"checkbox\"><input type=\"checkbox\" class=\"" + $cls + "\" name=\"" + $column_name + "\" function-id=\"column_toggle\" function-class=\"column_toggle\" column-toggle-table=\"" + $table + "\" function-name=\"column_toggle\" module-name=\"" + $module + "\" module-id=\"" + $module + "\" " + $column_state + "><small>" + $display_label + "</small></label></li>";
    }

    public static String GetToolbar( String $hide_show_col, JSONObject $data_tb_set, String $classname ){
        String $returning_html_data = "";

        String $super = "";

        String $temp_table_name = "";

        JSONObject $access = new JSONObject();
        JSONObject $gsettings = new JSONObject();

        String $access_class = "";

        String $allow_new = "";

        String $allow_import_excel_table = "";

        String $allow_editing_records = "";

        String $allow_deleting_records = "";

        String $allow_restore_button = "";

        String $allow_export = "";

        String $pplugin = "";

        JSONObject $hf = new JSONObject();

        String $hide_csv = "";

        String $button_params = "";

        String $function_name = "";

        String $function_text = "";

        String $function_title = "";

        String $function_class = "";

        String $href = "";

        String $b_class = "";

        String $caption = "";

        String $title_text = "";

        String $ea = "";

        String $show_cancel = "";

        String $adx = "";

        String $xstyle = "";

        String $html_replacement_selector = $data_tb_set.has( "html_replacement_selector" )?$data_tb_set.getString( "html_replacement_selector" ):"";

        //GET DETAILS OF CURRENTLY LOGGED IN USER
        if( $data_tb_set.has( "skip_access_control" ) && ! $data_tb_set.getString( "skip_access_control" ).isEmpty() ){
            $super = "1";
        }else{
            if( GlobalFunctions.app_user_data.has("role") ) {
                if( GlobalFunctions.app_user_data.getString("role").equals( GlobalFunctions.app_sys_admin_role ) ) {
                    $super = "1";
                }else if( GlobalFunctions.app_user_data.has("accessible_functions") ){
                    $access = GlobalFunctions.app_user_data.getJSONObject("accessible_functions");
                }
            }else{
                $super = "1";
            }
        }

        //CHECK FOR BUTTONS ACTION PROCESSING CLASS
        $temp_table_name = $classname;
        String $data_table_id = $classname + "-datatable";
        if( $data_tb_set.has( "buttons_action_processing_class" ) && ! $data_tb_set.getString( "buttons_action_processing_class" ).isEmpty() ){
            $classname = $data_tb_set.getString( "buttons_action_processing_class" );
        }


        //Check for New record privilege
        $gsettings = new JSONObject();
        $gsettings.put( "force", 1 );

        $classname = $classname;
        $access_class = $classname;

        if( $data_tb_set.has( "access_control_class" ) && ! $data_tb_set.getString( "access_control_class" ).isEmpty() ){
            $access_class = $data_tb_set.getString( "access_control_class" );
        }
        if( $data_tb_set.has( "plugin" ) && ! $data_tb_set.getString( "plugin" ).isEmpty() ){
            $access_class = $data_tb_set.getString( "plugin" ) + "+" + $access_class;
        }

        $allow_new = "1";
        if( $super.isEmpty() && ! $access.has( $access_class + ".create_new_record" ) ){
            $allow_new = "";
        }

        $allow_import_excel_table = "1";

        $allow_editing_records = "";
        if( $data_tb_set.has( "show_edit_button" ) && $data_tb_set.getInt( "show_edit_button" ) > 0 ){
            $allow_editing_records = "1";
            if( $super.isEmpty() && !$access.has( $access_class + ".edit" ) ){
                $allow_editing_records = "";
            }

            $data_tb_set.put( "user_can_edit", $allow_editing_records );
        }

        $allow_deleting_records = "1";
        if( $super.isEmpty() && !$access.has( $access_class + ".delete" ) ){
            $allow_deleting_records = "";
        }

        $allow_restore_button = "1";
        if( $super.isEmpty() && !$access.has( $access_class + ".restore" ) ){
            $allow_restore_button = "";
        }

        $allow_export = "1";
        if( $super.isEmpty() && !$access.has( $access_class + ".export" ) ){
            $allow_export = "";
        }

        $pplugin = "";
        if( $data_tb_set.has( "plugin" ) && $data_tb_set.getString( "plugin" ) != "" ){
            $pplugin = "&plugin=" + $data_tb_set.getString( "plugin" );
        }


        //CHECK WHETHER OR NOT TO SHOW TOOLBAR
        if( $data_tb_set.has( "show_toolbar" ) && $data_tb_set.getInt( "show_toolbar" ) > 0 ){
            JSONObject hf1 = new JSONObject();
            hf1.put( "name", "table" );
            hf1.put( "value", $classname );

            $hf.put( "1", hf1 );

            if( $allow_export != "" ){
                $hide_csv = ( $data_tb_set.has( "hide_csv" ) && !$data_tb_set.getString( "hide_csv" ).equals( "" ) ) ? "" : "1";

                //$returning_html_data += get_export_and_print_popup( "#"+$data_table_id , "#"+$data_table_container, "true", 0, array( "hidden_fields" => $hf, "csv" => $hide_csv ) );
                //$returning_html_data += "</div>&nbsp;";
            }

            //Toolbar
            $returning_html_data += "<div class=\"btn-group\">";

            //CHECK WHETHER OR NOT TO SHOW ADD NEW RECORD BUTTON
            $button_params = "";
            if( $data_tb_set.has( "button_params" ) && ! $data_tb_set.getString( "button_params" ).isEmpty() ){
                $button_params = $data_tb_set.getString( "button_params" );
            }

            if( $data_tb_set.has( "show_add_new" ) && $data_tb_set.getInt( "show_add_new" ) > 0 ){
                if( ! $allow_new.isEmpty() ){

                    $function_name = "create_new_record";
                    $function_text = "New";
                    $function_title = "Add new record to the dataTable";
                    $function_class = $classname;
                    String $button_params_new = $button_params;

                    if( $data_tb_set.has( "show_add_new_options" ) ){
                        if( $data_tb_set.getJSONObject( "show_add_new_options" ).has("nwp2_action") ){
                            $function_class = $data_tb_set.getJSONObject( "show_add_new_options" ).getString("nwp2_action");
                        }
                        if( $data_tb_set.getJSONObject( "show_add_new_options" ).has("nwp2_todo") ){
                            $function_name = $data_tb_set.getJSONObject( "show_add_new_options" ).getString("nwp2_todo");
                        }
                        if( $data_tb_set.getJSONObject( "show_add_new_options" ).has("params") ){
                            $button_params_new += $data_tb_set.getJSONObject( "show_add_new_options" ).getString("params");
                        }
                        $button_params_new += "&nwp_ub=new";
                    }

                    $href = "#";
                    $b_class = "";
                    if( ! $button_params.isEmpty() ){
                        $href = "?action=" + $function_class + "&todo=" + $function_name + $button_params_new;
                        $b_class = "custom-action-button-url";
                    }

                    $returning_html_data += "<a href=\"" + $href + "\" id=\"add-new-record\" class=\"" + $b_class + " btn btn-mini btn-sm dark\"  function-id=\"-\" search-table=\"\" function-class=\"" + $function_class + "\" function-name=\"" + $function_name + "\" module-id=\"\" module-name=\"\" title=\"" + $function_title + "\">" + $function_text + "</a>";

                }
            }

            //CHECK WHETHER OR NOT TO SHOW EDIT BUTTON
            if( $data_tb_set.has( "show_edit_button" ) && $data_tb_set.getInt( "show_edit_button" ) > 0 ){
                $caption = "Edit";
                $title_text = "Edit Record the Selected Record";

                $function_name = "edit";
                $function_class = $classname;
                String defaultAction = "datatable_button";
                Boolean hasbp = true;
                String $button_params_edit = $button_params;

                if( $data_tb_set.has( "show_edit_button_options" ) ){
                    if( $data_tb_set.getJSONObject( "show_edit_button_options" ).has("nwp2_action") ){
                        defaultAction = $data_tb_set.getJSONObject( "show_edit_button_options" ).getString("nwp2_action");
                    }
                    if( $data_tb_set.getJSONObject( "show_edit_button_options" ).has("nwp2_todo") ){
                        $function_name = $data_tb_set.getJSONObject( "show_edit_button_options" ).getString("nwp2_todo");
                    }
                    if( $data_tb_set.getJSONObject( "show_edit_button_options" ).has("params") ){
                        hasbp = false;
                        $button_params_edit += $data_tb_set.getJSONObject( "show_edit_button_options" ).getString("params");
                    }
                    $button_params_edit += "&nwp_ub=edit";
                }

                if( ! $allow_editing_records.isEmpty() ){

                    $ea = "&action="+ defaultAction + "&todo=" + $function_name;
                    if( hasbp ){
                        $ea += "&nwp2_action="+ defaultAction +"&nwp2_source=" + $function_class + "&nwp2_todo=" + $function_name;
                    }

                    $returning_html_data += "<a href=\"#\" id=\"edit-selected-record\" class=\"btn btn-mini btn-sm dark\" function-id=\"" + $allow_editing_records + "\" search-table=\"\" function-class=\"" + $function_class + "\" function-name=\"" + $function_name + "\" module-id=\"\" module-name=\"\" action=\"?module=" + $ea + $button_params_edit + "\" mod=\"edit-" + ( $function_class ) + "\" todo=\"edit\" title=\"" + $title_text + "\">" + $caption + "</a>";
                }
            }

            if( $data_tb_set.has( "custom_edit_button" ) && ! $data_tb_set.getString( "custom_edit_button" ).isEmpty() ){
                $returning_html_data += $data_tb_set.getString( "custom_edit_button" );
            }

            //CHECK WHETHER OR NOT TO SHOW DELETE BUTTON
            if( $data_tb_set.has( "show_restore_button" ) && $data_tb_set.getInt( "show_restore_button" ) > 0 ){

                if( $allow_restore_button != "" ){
                    $function_name = "restore";

                    $function_text = "Restore";
                    $function_title = "Restore selected record(s)";
                    $function_class = $classname;

                    $ea = "&action=" + $function_class + "&todo=" + $function_name;

                    $returning_html_data += "<a href=\"#\" id=\"restore-selected-record\" class=\"btn btn-mini btn-sm dark pop-up-button\" function-id=\"" + $allow_deleting_records + "\" search-table=\"\" function-class=\"" + $function_class + "\" function-name=\"" + $function_name + "\" module-id=\"\" module-name=\"\" action=\"?module=" + $ea + "\" mod=\"delete-" + ( $classname ) + "\" todo=\"" + $function_name + "\" data-toggle=\"popover\" data-trigger=\"manual\" data-placement=\"bottom\" title=\"" + $function_title + "\">" + $function_text;
                    $returning_html_data += "</a>";

                    $returning_html_data += "<div class=\"pop-up-content\" style=\"display:none;\">";
                    $returning_html_data += "Are you sure you want to restore the selected record(s)<br /><br /><input type=\"button\" class=\"btn btn-mini btn-sm btn-primary\" value=\"Yes\" id=\"restore-button-yes\" />&nbsp;<input type=\"button\" value=\"No\" class=\"btn btn-mini\" id=\"restore-button-no\" />";
                    $returning_html_data += "</div>";
                }
            }else if( $data_tb_set.has( "show_delete_button" ) && $data_tb_set.getInt( "show_delete_button" ) > 0 ){
                if( ! $allow_deleting_records.isEmpty() ){
                    $function_name = "delete";
                    $function_text = "Delete";
                    $function_title = "Delete selected record(s)";
                    $function_class = $classname;

                    //$ea = "&action="+$function_class+"&todo="+ $function_name;
                    $ea = "&action=datatable_button&todo=" + $function_name;
                    $ea += "&nwp2_action=datatable_button&nwp2_source=" + $function_class + "&nwp2_todo=" + $function_name;

                    $returning_html_data += "<a href=\"#\" id=\"delete-selected-record\" class=\"btn btn-mini btn-sm dark pop-up-button\" function-id=\"" + $allow_deleting_records + "\" search-table=\"\" function-class=\"" + $function_class + "\" function-name=\"" + $function_name + "\" module-id=\"\" module-name=\"\" action=\"?module=" + $ea + "\" mod=\"delete-" + ( $classname ) + "\" todo=\"" + $function_name + "\" data-toggle=\"popover\" data-trigger=\"manual\" data-placement=\"bottom\" title=\"" + $function_title + "\">" + $function_text;
                    $returning_html_data += "</a>";

                    $returning_html_data += "<div class=\"pop-up-content\" style=\"display:none;\">";
                    $returning_html_data += "Are you sure you want to delete the selected record(s)<br /><br /><input type=\"button\" class=\"btn btn-mini btn-sm btn-primary\" value=\"Yes\" id=\"delete-button-yes\" />&nbsp;<input type=\"button\" value=\"No\" class=\"btn btn-mini\" id=\"delete-button-no\" />";
                    $returning_html_data += "</div>";
                }
            }


            $returning_html_data += "</div>&nbsp;";

            $returning_html_data += "<div data-role=\"controlgroup\" class=\"btn-group\" data-type=\"horizontal\" data-mini=\"true\">";

            $show_cancel = "";

            //CHECK WHETHER OR NOT TO SHOW ADVANCE SEARCH BUTTON
            if( $data_tb_set.has( "popup_search" ) && $data_tb_set.getInt( "popup_search" ) > 0 ){

                //@nw5
                $adx = "";
                if( $data_tb_set.has( "real_table" ) && $data_tb_set.getString( "real_table" ) != "" ){
                    $adx += "&real_table=" + $data_tb_set.getString( "real_table" );
                }

                if( $data_tb_set.has( "db_table" ) && $data_tb_set.getString( "db_table" ) != "" ){
                    $adx += "&db_table=" + $data_tb_set.getString( "db_table" );
                }

                //Advance Search Button
                $returning_html_data += "<a href=\"#\" class=\"btn btn-sm dark custom-single-selected-record-button\" override-selected-record=\"1\" action=\"?action=search&todo=search_window&table=" + $classname + $pplugin + $adx + "\" module-id=\"\" module-name=\"\" title=\"Perform advance search query\">Advance Search</a>";

                $show_cancel = "1";
            }

            if( $show_cancel != "" ){
                //check for search query
                $xstyle = "display:none;";
                $returning_html_data += "<a href=\"#\" id=\"clear-advance-search-button\" style=\"" + $xstyle + "\" class=\"btn btn-sm red 	custom-single-selected-record-button\" override-selected-record=\"1\" action=\"?action=search&todo=clear_search_window&table=" + $classname + $pplugin + "\" title=\"Clear advance search query\"><i class=\"icon-remove\"></i></a>";
            }


            Boolean showDetails = true;
            if( $data_tb_set.has( "hide_view_details" ) ){
                showDetails = false;
            }else if ($data_tb_set.has("datatable_options") && $data_tb_set.getJSONObject("datatable_options").has("hide_view_details")) {
                showDetails = false;
            }else if( $data_tb_set.has( "datatable_split_screen" ) && $data_tb_set.getJSONObject( "datatable_split_screen" ).has( "col" ) ){
                showDetails = false;
            }

            String $button_params2 = new String( $button_params );
            if( showDetails ){
                $function_name = "view_details";
                $function_class = $classname;
                String defaultAction = "datatable_button";
                $caption = "View";
                $title_text = "View Details";

                if( $data_tb_set.has( "view_details_options" ) ){
                    if( $data_tb_set.getJSONObject( "view_details_options" ).has("nwp2_action") ){
                        defaultAction = $data_tb_set.getJSONObject( "view_details_options" ).getString("nwp2_action");
                    }
                    if( $data_tb_set.getJSONObject( "view_details_options" ).has("nwp2_todo") ){
                        $function_name = $data_tb_set.getJSONObject( "view_details_options" ).getString("nwp2_todo");
                    }
                    if( $data_tb_set.getJSONObject( "view_details_options" ).has("no_params") ){
                        $button_params2 = "";
                    }
                }

                $ea = "&action="+ defaultAction + "&todo=" + $function_name;
                $ea += "&nwp2_action="+ defaultAction +"&nwp2_source=" + $function_class + "&nwp2_todo=" + $function_name;

                $returning_html_data += "<a href=\"javascript:;\" class=\"btn btn-mini custom-single-selected-record-button-old btn-sm dark\" action=\"?module=" + $ea + $button_params2 + "\" todo=\"view_details\" title=\"" + $title_text + "\">" + $caption + "</a>";
            }


            if( $data_tb_set.has( "datatable_options" ) ){
                try {
                    JSONObject $btn_data = new JSONObject();
                    if ($data_tb_set.getJSONObject("datatable_options").has("more_actions")) {
                        $btn_data.put("more_actions", $data_tb_set.getJSONObject("datatable_options").getJSONObject("more_actions"));
                    }
                    if ($data_tb_set.getJSONObject("datatable_options").has("utility_buttons")) {
                        if ( ! $data_tb_set.getJSONObject("datatable_options").getJSONObject("utility_buttons").has("no_dt_table") ) {
                            $btn_data.put("utility_buttons", $data_tb_set.getJSONObject("datatable_options").getJSONObject("utility_buttons"));
                        }
                    }
                    if ($btn_data.length() > 0) {
                        $btn_data.put("hide", "no_dt_table");
                        $btn_data.put( "html_replacement_selector", $html_replacement_selector );
                        $btn_data.put( "params", "&html_replacement_selector=" + $btn_data.getString( "html_replacement_selector" ) );
                        $btn_data.put("table", $classname);
                        $returning_html_data += nwpDataTable.getButtons($btn_data);
                    }
                }catch (Exception e){
                    $returning_html_data += e.getMessage();
                }
            }

            if( $data_tb_set.has( "custom_view_button" ) && ! $data_tb_set.getString( "custom_view_button" ).isEmpty() ){
                $returning_html_data += $data_tb_set.getString( "custom_view_button" );
            }

            $returning_html_data += "</div>&nbsp;";

            $returning_html_data += "<div data-role=\"controlgroup\" class=\"btn-group\" data-type=\"horizontal\" data-mini=\"true\">";

            //CHECK WHETHER OR NOT TO SHOW COLUMN SELECTOR BUTTON
            if( $data_tb_set.has( "show_column_selector" ) && $data_tb_set.getInt( "show_column_selector" ) > 0 ){
                //Toggle Column Selector Button
                $returning_html_data += "<a href=\"#\" class=\"btn btn-mini btn-sm default pop-up-buttonX dropdown-toggle\" id=\"hide-show-columns\" title=\"Hide / Show Columns\" data-toggleX=\"popover\" data-toggle=\"dropdown\" data-placement=\"bottom\">Show Columns ";
                $returning_html_data += "</a>";

                $returning_html_data += "<div class=\"pop-up-contentX dropdown-menu hold-on-click\" style=\"displayX:none; margin:0; box-shadow: 1px 3px 5px #ddd; padding-left:10px; list-style:none;  background-color: #F5F5F5;  border: 1px solid #CCCCCC\"><ul class=\"show-hide-column-con\" style=\"padding:0; margin:0; list-style:none; max-height:380px; overflow-y:auto;\" data-table=\"" + $data_table_id + "\">";
                $returning_html_data += $hide_show_col;
                $returning_html_data += "</ul></div>";
            }

            $returning_html_data += "</div>";

        }//END - CHECK WHETHER OR NOT TO SHOW TOOLBAR

        return $returning_html_data;
    }
}
