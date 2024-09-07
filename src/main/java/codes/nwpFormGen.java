package codes;

import org.json.JSONObject;
import org.json.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.util.*;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import static codes.GlobalFunctions.stripslashes;

public class nwpFormGen{
    private static Integer id_increment = 0;
    private static String lbl = "";
    public static JSONObject form_content = new JSONObject();

    public static JSONObject getFormData( JSONObject pdata ){
        String $t = "";
        String $returning_html_data = "";
        JSONObject $clean_data = new JSONObject();
        JSONObject $clean_data_tmp = new JSONObject();
        String $vr = "";
        String $sr = "";
        String $update = "";

        Boolean return_keys = pdata.has( "return_keys" )?pdata.getBoolean( "return_keys" ):false;

        String $table = pdata.has( "table" ) ? pdata.getString( "table" ) : "";
        String $id = pdata.has( "id" ) ? pdata.getString( "id" ) : "";
        String $action = pdata.has( "action" ) ? pdata.getString( "action" ) : "";
        JSONObject $labels = pdata.has( "labels" ) ? pdata.getJSONObject( "labels" ) : new JSONObject();
        JSONObject $field_details = new JSONObject();
        JSONObject $fields = pdata.has( "fields" ) ? pdata.getJSONObject( "fields" ) : new JSONObject();
        JSONObject duplicateSettings = pdata.has( "prevent_duplicate" ) ? pdata.getJSONObject( "prevent_duplicate" ) : new JSONObject();
        JSONObject duplicateFields = duplicateSettings.has( "fields" ) ? duplicateSettings.getJSONObject( "fields" ) : new JSONObject();
        String duplicateWhere = "";
        JSONObject $post_data = pdata.has( "post_data" ) ? pdata.getJSONObject( "post_data" ) : new JSONObject();
        JSONObject $system_values = new JSONObject();
        String dupWhere2 = "";

        String $key1 = "";
        JSONObject $val1 = new JSONObject();
        JSONObject $validate_checker = new JSONObject();

        if( $labels.names().length() > 0 ){
            for( int i2 = 0; i2 < $labels.names().length(); i2++ ){
                $key1 = $labels.names().getString( i2 );
                $val1 = $labels.getJSONObject( $key1 );

                if( $post_data.has( $key1 ) && $val1.has( "form_field" ) && $val1.has( "field_identifier" ) ){
                    $clean_data_tmp = new JSONObject();
                    $clean_data_tmp.put( "form_field", $val1.getString( "form_field" ) );


                    switch( $val1.getString( "form_field" ) ){
                    case "calculated":
                        if( $val1.has( "calculations" ) ){
                            JSONObject cal = $val1.getJSONObject( "calculations" );
                            if( cal.has( "form_field" ) ){
                                $val1.put( "form_field", cal.getString( "form_field" ) );
                            }
                        }
                        break;
                    }

                    JSONObject val_inp = new JSONObject();
                    /*val_inp.put( "post_key", $key1 );
                    val_inp.put( "post_data", $post_data );*/
                    val_inp.put( "value", $post_data.getString( $key1 ) );
                    val_inp.put( "field_details", $val1 );

                    $validate_checker = validate( val_inp );

                    if( $validate_checker.has( "error" ) && !$validate_checker.getString( "error" ).isEmpty() ){
                        return $validate_checker;
                    }else if( $validate_checker.has( "value" ) ){
                        //$clean_data_tmp.put( "ivalue", $post_data.getString( $key1 ) );
                        $clean_data_tmp.put( "value", $validate_checker.get( "value" ) );
                        $clean_data_tmp.put( "field_identifier", $val1.getString( "field_identifier" ) );

                        if( return_keys ){
                            $clean_data_tmp.put( "field_identifier", $key1 );
                            $clean_data.put( $val1.getString( "field_identifier" ), $clean_data_tmp);
                        }else {
                            $clean_data.put($key1, $clean_data_tmp);
                        }

                        //get duplicate query
                        if( duplicateSettings.has("condition") && duplicateFields.has(  $val1.getString( "field_identifier" ) ) ){
                            JSONObject dupFields = duplicateFields.getJSONObject(  $val1.getString( "field_identifier" ) );
                            if( dupFields.has("skip_if_empty") && $validate_checker.getString( "value" ).isEmpty() ){

                            }else{
                                if( ! duplicateWhere.isEmpty() ){
                                    duplicateWhere += " " + duplicateSettings.getString("condition") + " ";
                                }
                                String dupWhere = "LOWER( ["+$key1+"] ) = '" + $validate_checker.getString( "value" ).toLowerCase().trim() + "'";

                                //test flip condition e.g a == value or b == value
                                if( dupFields.has("flip_test") && dupFields.getJSONObject( "flip_test" ).length() > 0 ){
                                    if( dupFields.getJSONObject( "flip_test" ).names().length() > 0 ) {
                                        for (int i3 = 0; i3 < dupFields.getJSONObject( "flip_test" ).names().length(); i3++) {
                                            String $key2 = dupFields.getJSONObject( "flip_test" ).names().getString(i3);
                                            if( $fields.has( $key2 ) && $labels.has( $fields.getString( $key2 ) ) ){
                                                if( ! dupWhere2.isEmpty() ){
                                                    dupWhere2 += " " + duplicateSettings.getString("condition") + " ";
                                                }
                                                dupWhere2 += " LOWER( ["+ $fields.getString( $key2 ) +"] ) = '" + $validate_checker.getString( "value" ).toLowerCase().trim() + "' ";
                                            }
                                        }


                                    }
                                }

                                duplicateWhere += dupWhere;
                            }
                        }

                    }else{
                        $validate_checker.put( "error", "Validation Error on: " + $val1.getString( "field_label" ) );
                        return $validate_checker;
                    }
                }
            }

            if( ! duplicateWhere.isEmpty() && ! dupWhere2.isEmpty() ){
                duplicateWhere = "(" + duplicateWhere + ") OR (" + dupWhere2 + ")";
            }

            String $rs = "1";
            String dfs = "id,created_by,creation_date,modified_by,modification_date,record_status";
            switch( $action ){
            case "edit":
                dfs = "modified_by,modification_date";

                if( pdata.has( "no_modification" ) && pdata.getBoolean("no_modification") ){
                    dfs = "";
                }
                break;
            case "delete":
                dfs = "modified_by,modification_date,record_status";
                $rs = "0";
                break;
            }


            Date currentDate = new Date();
            long timestamp = currentDate.getTime() / 1000;

            if( ! dfs.isEmpty() ) {
                String[] df = dfs.split( "," );
                if( df.length > 0 ) {
                    for (int i3 = 0; i3 < df.length; i3++) {
                        $clean_data_tmp = new JSONObject();
                        $clean_data_tmp.put("form_field", "system");

                        switch (df[i3]) {
                            case "id":
                                if ($id.isEmpty()) {
                                    $clean_data_tmp.put("value", GlobalFunctions.GetNewID($table));
                                } else {
                                    $clean_data_tmp.put("value", $id);
                                }
                                break;
                            case "modified_by":
                            case "created_by":
                                $clean_data_tmp.put("value", GlobalFunctions.app_user);
                                break;
                            case "modification_date":
                            case "creation_date":
                                $clean_data_tmp.put("value", timestamp);
                                $clean_data_tmp.put("form_field", "system_time");
                                break;
                            case "record_status":
                                $clean_data_tmp.put("value", $rs);
                                break;
                        }

                        $clean_data_tmp.put("field_identifier", df[i3]);
                        $clean_data.put(df[i3], $clean_data_tmp);
                    }
                }
            }
        }
        //$clean_data.put("error", "Stop 2" );
        //check for duplicates
        if( ! duplicateWhere.isEmpty() ){
            JSONObject grd = new JSONObject();
            grd.put( "fields", $fields );
            grd.put( "table", $table );
            grd.put( "where", " AND ( " + duplicateWhere + " ) AND [id] <> '"+ $id +"'" );

            JSONObject $rds = GlobalFunctions.get_records( grd );
            //System.out.println( $rds );
            if( $rds.has( "row_count" ) && $rds.getInt("row_count") > 0 ){
                JSONObject existing = $rds.getJSONArray("row").getJSONObject(0);
                String duplicateError = "";
                if( duplicateSettings.has("error_title") ){
                    duplicateError += "<h4>"+ duplicateSettings.getString("error_title") +"</h4>";
                }else{
                    duplicateError += "<h4>Existing Duplicate Record</h4>";
                }
                if( duplicateSettings.has("error_message") ){
                    duplicateError += "<p>"+ duplicateSettings.getString("error_message") +"</p>";
                }else{
                    duplicateError += "<p>Existing Duplicate Record</p>";
                }
                duplicateError += "<b>ID:</b> "+ existing.getString("id") +"<br />";

                if ( duplicateFields.names().length() > 0) {
                    String $key2 = "";
                    String $val2 = "";

                    for (int i2 = 0; i2 < duplicateFields.names().length(); i2++) {
                        $key2 = duplicateFields.names().getString(i2);
                        $val2 = existing.has($key2)?existing.getString($key2):"";

                        duplicateError += "<b>"+ $key2 +":</b> "+ $val2 +"<br />";
                    }
                }
                $clean_data.put("error", duplicateError );
            }

        }

       /*JSONObject $rd = new JSONObject();
        $rd.put("data", $clean_data );
        $rd.put("data", $clean_data );*/

        return $clean_data;
    }

    public static JSONObject validate( JSONObject pdata ){
        JSONObject r = new JSONObject();
        /*JSONObject post_data = pdata.has( "post_data" ) ? pdata.getJSONObject( "post_data" ) : new JSONObject();
        String post_key = pdata.has( "post_key" ) ? pdata.getString( "post_key" ).trim() : "";*/
        JSONObject $field_details = pdata.has( "field_details" ) ? pdata.getJSONObject( "field_details" ) : new JSONObject();
        String $data = pdata.has( "value" ) ? pdata.getString( "value" ).trim() : "";
        String form_field = $field_details.has( "form_field" ) ? $field_details.getString( "form_field" ) : "";
        Boolean failed = true;

        JSONObject $field_details_callback = $field_details.has( "callback" ) ? $field_details.getJSONObject( "callback" ) : new JSONObject();
        //System.out.println( $field_details_callback );
        if( $field_details_callback.has( "save" ) ){
            switch( $field_details_callback.getString( "save" ) ){
            case "urldecode":
                $data = GlobalFunctions.urldecode( $data );
            case "urlencode":
                $data = GlobalFunctions.rawurlencode( $data );
            default:
                break;
            }
        }

        switch( form_field ){
        case "text-file":    //"text"
        case "calculated":
        case "text":    //"text"
        case "select":    //"select",
            //$data = $data.substring( 0, 200 );

            if( !$data.isEmpty() ){
                failed = false;
            }
            break;
        case "number":
        case "decimal_long":
        case "decimal":
            if( !$data.isEmpty() ){
                failed = false;
            }
            break;
        case "date-5time":
        case "datetime":
        case "date-5":
        case "date":

            //System.out.println( GlobalFunctions.convert_date_to_timestamp( $data, 0, new JSONObject() ) );
            long timestamp;
            switch( form_field ){
            case "date-5time":
                $data = $data.replace( "T", " " );
                timestamp = GlobalFunctions.convert_date_to_timestamp( $data, 1, new JSONObject() );
                break;
            case "datetime":
            default:
                timestamp = GlobalFunctions.convert_date_to_timestamp( $data, 0, new JSONObject() );
                break;

            }
            //System.out.println( $data );
            //System.out.println( timestamp );
            if( !Objects.isNull( timestamp ) ){
                r.put( "value", timestamp );
                return r;
            }

            break;
        case "checkbox":
            break;
        /*case "file":
            if( post_data.has(post_key + "_json") )
            break;*/
        default:
            if( !$data.isEmpty() ){
                failed = false;
            }
            break;
        }

        if( $field_details.has( "required_field" ) && $field_details.getString( "required_field" ).equals( "yes" ) && failed ){
            //Return Error Message
            r.put( "error", "<h4>Missing Value in <b>" + $field_details.getString( "field_label" ) + "</b> Field</h4>Please ensure that all required fields are properly filled" );
        }

        switch( form_field ) {
            case "number":
            case "decimal_long":
            case "decimal":
                if( $data.isEmpty() ){
                    r.put("value", 0 );
                }else {
                    r.put("value", Double.valueOf($data));
                }
                break;
            default:
                r.put( "value", $data );
                break;
        }


        return r;
    }

    public static String createForm( JSONObject pdata ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException{

        form_content = new JSONObject().put("fields", new JSONArray() );

        //System.out.println( r );
        Integer $enabled = 1;

        String $search_combo_option = "";

        String $search_combo_option_text = "";


        String $returning_html_data = "<div id=\"form-panel-wrapper\">";
        String $h_content = "";
        String $checksum = "nwp_fm_" + GlobalFunctions.GetNewID("form");

        if( pdata.has( "data" ) ){
            JSONObject r = pdata.getJSONObject( "data" );

            String $html_id = r.getString( "html_id" );
            String $table = r.getString( "table" );

            String $mobile = "";
            String todo = r.has( "todo" )?r.getString( "todo" ):"";
            String $method = r.getString( "method" );
            String $action_to_perform = r.getString( "action_to_perform" );
            String $form_class = r.getString( "form_class" );

            String $database_table_field_intepretation_function_name = r.getString( "table" );

            JSONObject $form_label = r.getJSONObject( "labels" );
            JSONArray $form_order = r.getJSONArray( "form_order" );

            String $mobile_class1 = "";

            String $mobile_class2 = "";

            String $mobile_class3 = "";

            String $mobile_class4 = "";

            String $mobile_class5 = "";

            Integer $t = 0;

            String $stop = "";

            String $css_hide = "";

            Boolean $populate_form_with_values = false;

            String $field_id = "";

            JSONObject $nw_more_data = new JSONObject();

            JSONObject $values = new JSONObject();
            if( r.has( "values" ) ){
                $values = r.getJSONObject( "values" );
            }

            if( $enabled > 0 ){
                $returning_html_data += "<form name=\"" + $table + "\" id=\"" + $html_id + "-form\" method=\"" + $method + "\" action=\"" + $action_to_perform + "\" enctype=\"multipart/form-data\" data-ajax=\"false\" class=\" inputs-list login-form " + $form_class + "\">";
            }

            if( $values.length() > 0 ){
                $populate_form_with_values = true;
            }else{
                $populate_form_with_values = false;
            }
            //System.out.println( "$form_label" );
            //System.out.println( $form_label );
            Boolean getForm = true;

            if( $form_order.length() > 0 ){
                for( int i2 = 0; i2 < $form_order.length(); i2++ ){
                    $field_id = $form_order.getString( i2 );
                    getForm = true;

                    switch( todo ){
                        case "edit":
                            if( $form_label.has( $field_id ) && $form_label.getJSONObject( $field_id ).has("no_edit") ){
                                getForm = false;
                            }
                            break;
                        case "create_new_record":
                            if( $form_label.has( $field_id ) && $form_label.getJSONObject( $field_id ).has("no_create") ){
                                getForm = false;
                            }
                            break;
                    }

                    if( getForm ) {
                        JSONObject $ips = new JSONObject();

                        $ips.put("field_id", $field_id);
                        $ips.put("form_label", $form_label);

                        $ips.put("populate_form_with_values", $populate_form_with_values);

                        $ips.put("aa", $values);
                        $ips.put("t", $t);

                        // System.out.println( "form" );
                        // System.out.println( $ips );
                        try {
                            $h_content += nw_generate_form_field($ips);
                        } catch (Exception e) {
                            $h_content += "Problem with:" + $field_id + "<br /><br />" + e.getMessage();
                        }

                        ++$t;
                    }
                }
            }

            if( $enabled > 0 ){

                String $record_id = ( r.has( "record_id" ) ? r.getString( "record_id" ) : "" );
                String $user_id = ( r.has( "user_id" ) ? r.getString( "user_id" ) : "" );
                String $user_priv_id = ( r.has( "user_priv_id" ) ? r.getString( "user_priv_id" ) : "" );

                JSONObject $hdata = new JSONObject();
                $hdata.put( "table", $table );
                $hdata.put( "id", $record_id );
                $hdata.put( "uid", $user_id );
                $hdata.put( "user_priv", $user_priv_id );
                $hdata.put( "action", $action_to_perform );

                $returning_html_data += GlobalFunctions.get_form_headers( $hdata );

                $returning_html_data += "<input type=\"hidden\" name=\"nw_checksum\" value=\"" + $checksum + "\" />";
                $returning_html_data += "<input type=\"hidden\" name=\"skip_validation\" value=\"1\" />";

                if( r.has( "tmp_id" ) ){
                    $returning_html_data += "<input type=\"hidden\" name=\"tmp_id\" value=\"" + r.getString( "tmp_id" ) + "\" />";
                }

                if( r.has( "form_extra_field_data" ) ){
                    $returning_html_data += "<textarea class=\"hyella-dataX\" style=\"display:none;\" id=\"extra_fields\" name=\"extra_fields\">" + r.getString( "form_extra_field_data" ) + "</textarea>";
                }

            }

            $returning_html_data += "<div class=\"form-body\" >";
            $returning_html_data += $h_content;

            if( r.has( "form_extra_field_elements" ) ){
                $returning_html_data += r.getString( "form_extra_field_elements" );
            }

            if( $enabled > 0 ){
                $returning_html_data += "<div id=\"bottom-row-container\">";
                $returning_html_data += "<div class=\"control-group input-row bottom-row\" style=\"margin-bottom:20px; clear:both;\">";
                $returning_html_data += "<div class=\"controls cell\">";

                String $submit_label = ( r.has( "submit_label" ) ? r.getString( "submit_label" ) : "Submit &rarr;" );
                $returning_html_data += "<input tabindex=\"" + ( $t + 2 ) + "\" id=\"form-gen-submit\" data-loading-text=\"processing+++\" class=\"btn blue form-gen-button \" value=\"" + $submit_label + "\" type=\"submit\"/> ";

                $returning_html_data += "</div>";
                $returning_html_data += "</div>";
                $returning_html_data += "</div>";
            }

            $returning_html_data += "</div>";
            if( $enabled > 0 ){
                $returning_html_data += "</form>";
            }
        }
        $returning_html_data += "</div>";

        if( form_content.getJSONArray("fields").length() > 0 ){
            form_content.put("saved", true );
            GlobalFunctions.set_form_checksum( $checksum, form_content );
        }

        return $returning_html_data;
        //return $returning_html_data;
    }

    public static String textarea( String $field_name, String $ctrl, Integer $serial_number, JSONObject $field_details ){
        //CREATES A TEXTAREA ELEMENT
        //$t = DATATYPE - integer; FUNCTION - defines the tab index as well as the element name
        //$ctrl = DATATYPE - string; FUNCTION - sets the default value of the element
        JSONObject $special_element_class = $field_details.has( "special_element_class" ) ? $field_details.getJSONObject( "special_element_class" ) : new JSONObject();
        JSONObject $disable_form_element = $field_details.has( "disable_form_element" ) ? $field_details.getJSONObject( "disable_form_element" ) : new JSONObject();

        //Set unique id for elements
        String $input_id = "";

        if( nwpFormGen.id_increment == 0 )
            $input_id = "kd";
        else
            $input_id = "kd" + nwpFormGen.id_increment;

        nwpFormGen.id_increment++;

        String $required = ( ( $field_details.has( "required_field" ) && $field_details.getString( "required_field" ).equals("yes") ) ? "form-element-required-field" : "" );
        String $attributes = ( $field_details.has( "attributes" ) ? $field_details.getString( "attributes" ) : "" );

        if( !$required.isEmpty() ){
            if( !$attributes.isEmpty() )
                $attributes += " required=\"required\" ";
            else
                $attributes = " required=\"required\" ";
        }

        String $returning_html_data = "<textarea tabindex=\"" + ( $serial_number + 1 ) + "\" class=\"form-control form-gen-element " + ( $special_element_class.has( $field_name ) ? $special_element_class.getString( $field_name ) : "" ) + " " + nwpFormGen.lbl + " " + $required + ( ( $field_details.has( "class" ) ) ? $field_details.getString( "class" ) : "" ) + "\" type=\"text\" id=\"" + $field_name + "\" name=\"" + $field_name + "\" rows=\"2\" cols=\"26\" tip=\"" + ( $field_details.has( "tooltip" ) ? $field_details.get( "tooltip" ) : "" ) + "\" placeholder=\"" + ( $field_details.has( "placeholder" ) ? $field_details.getString( "placeholder" ) : "" ) + "\" " + ( $disable_form_element.has( $field_name ) ? $disable_form_element.getString( $field_name ) : "" ) + " " + ( $attributes ) + " alt1=\"" + $field_details.getString( "form_field" ) + "\" alt=\"textarea\" data-type=\"textarea\" >" + stripslashes( $ctrl ) + "</textarea>";

        return $returning_html_data;
    }

    public static String date( Integer $t, String $value, JSONObject $field_details, String $field_id ){
        Integer $timestamp = Integer.parseInt( $value );

        //DISPLAY DATE SELECTOR
        /*if($value && is_numeric($value)){
            $date = date("j-M-Y",$value);
            $value = explode('-',$date);
        }else{
            $date = date("j-M-Y");
            $value = explode('-',$date);
        }*/

        //If Search Mode is active ensure first select element is null
        /*if($this->searching){
            $value[0] = "";
            $value[1] = "";
            $value[2] = "";
        }*/

        //$returning_html_data = '<div class="date">';

        ArrayList<Integer> $key = new ArrayList<Integer>();
        String $returning_html_data = "DATEX<fieldset data-role=\"controlgroup\" data-type=\"horizontal\" class=\"date\" " + ( $field_details.has( "attributes" ) ? $field_details.getString( "attributes" ) : "" ) + ">";

            /*for($x=1;$x<32;$x++)$key.add( $x );
            $returning_html_data += select( 9 , $key , $key , $field_id , "cus88day" , $value , "" , "" , "" , $t );
            //$returning_html_data += '<div class="date-lbl">Month</div>';
            String stx = "jan<>feb<>mar<>apr<>may<>jun<>jul<>aug<>sep<>oct<>nov<>dec";
            $key = stx.split("<>");
            $returning_html_data += select( 9 , $key , $key , $field_id , "cus88month" , $value.toLowerCase(), "" , "" , "" , $t );
            //$returning_html_data += "<div class="date-lbl">Year</div>";
            for($x=((date("Y")+30) - $nwpFormGen.date_min_year);$x>1900;$x--)$key.add( $x );
            $returning_html_data += select( 9 , $key , $key , $field_id , "cus88year" , $value[2] , "" , "" , "" , $t );
            $returning_html_data += "<input type=\"hidden\" name=\""+$field_id+"cus88timestamp\" value=\""+$timestamp+"\" />";
            $returning_html_data += "<input type=\"hidden\" name=\""+$field_id+"\" value=\"date\" />";*/
        //$returning_html_data += "</div>";
        $returning_html_data += "</fieldset>";
        return $returning_html_data;
    }

    public static String form_value( String $val, Boolean $populate_form_with_values, String $field_id, JSONObject $form_details ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException{
        JSONObject $post = $form_details.has( "post" ) ? $form_details.getJSONObject( "post" ) : new JSONObject();

        //FUNCTION THAT DETERMINES WHETHER TO LOAD VALUES INTO A FORM OR NOT
        if( $populate_form_with_values && !$val.isEmpty() ){


            DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
            //LocalDateTime now = LocalDateTime.now();
            LocalDateTime myDateObj;
            Integer ts = 0;

            switch( $form_details.getString( "form_field" ) ){
            case "date":
            case "datetime":
            case "date-5":
                ts = Integer.parseInt( $val );
                myDateObj = LocalDateTime.ofEpochSecond( ts, 0, ZoneOffset.UTC );
                return myDateObj.format( dtf );
            // return dtf.format( Integer.parseInt( $val ) );

            case "datetime-local":
            case "date-5time":
                dtf = DateTimeFormatter.ofPattern( "yyyy-MM-dd \\THH:mm" );

                ts = Integer.parseInt( $val );
                myDateObj = LocalDateTime.ofEpochSecond( ts, 0, ZoneOffset.UTC );
                return myDateObj.format( dtf );
            // return date( "Y-m-d\\TH:i" , Integer.parseInt( $val ) );
            case "time":
                return $val;
            //                return format_time( $val );
            case "old-password":
            case "password":
                return "";
            case "number":

                if( $form_details.has( "display_text" ) && $form_details.getInt( "display_text" ) > 0 ){
                    //                    return NumberFormat.getInstance().format( $val2 );
                }

                return $val;
            case "decimal":
            case "currency":

                if( $form_details.has( "display_text" ) && $form_details.getInt( "display_text" ) > 0 ){
                    // return NumberFormat.getInstance().format( $val2 );
                    // return number_format( $val, 2 );
                }

                return $val;
            }

            JSONObject $field_details_callback = $form_details.has( "callback" ) ? $form_details.getJSONObject( "callback" ) : new JSONObject();
            //System.out.println( $field_details_callback );
            if( $field_details_callback.has( "load" ) ){

                switch( $field_details_callback.getString( "load" ) ){
                case "urldecode":
                    return GlobalFunctions.urldecode( $val );
                case "urlencode":
                    return GlobalFunctions.rawurlencode( $val );
                default:
                    if( GlobalFunctions.function_exists( "GlobalFunctions", $field_details_callback.getString( "load" ) ) ){
                        String $fnx = $field_details_callback.getString( "load" );
                        System.out.println( $form_details.getString( "form_field" ) + "--" + $fnx );
                        Method method = GlobalFunctions.class.getDeclaredMethod( $fnx );
                        //return (String) method.invoke( stripslashes( $val ) );
                        return $fnx;
                    }
                    break;
                }

            }

            return GlobalFunctions.stripslashes( $val );
        }

        if( ( !$populate_form_with_values ) && $post.has( $field_id ) ){
            return $post.getString( $field_id );
        }

        return $val;
    }

    public static String group_boxes( JSONObject $option, String $field_id, JSONObject $field_details, JSONObject $t, String $value, Boolean $form ){

        String $html = "";

        JSONObject $this_form_display_not_editable_value = new JSONObject();

        JSONObject $f = new JSONObject();

        String $not_editable = "";

        JSONObject $ex = new JSONObject();

        String $ev = "";

        String $ek = "";

        String $sel = "";

        ArrayList<String> $test_values = new ArrayList<String>();

        String $attr = "";

        String $cls = "";

        //FUNCTION USED TO DISPLAY GROUP OF CHECK BOXES / OPTION BUTTON
        //$select_rows = DATATYPE - array; FUNCTION - defines number of groups of boxes

        JSONObject $field_details_data = $field_details.has( "data" ) ? $field_details.getJSONObject( "data" ) : new JSONObject();
        $f = $option;
        /*if( $field_details_data.has( "form_field_options_source" ) && $field_details_data.getInt( "form_field_options_source" ) == 2 ){
            $f = get_list_box_options( $field_details.getString( "form_field_options" ), new JSONObject("{\"return_type\":2}") );
        }else if( is_array( $option ) ){
            $f = $option;
        }else if( function_exists( $option ) ){
            $f = $option();
        }*/

        /*if( $field_details.has( "options"] ) && $field_details[ "options" ] ){
            $ex = explode(";", $field_details[ "options" ] );

            if( is_array( $ex ) && ! empty( $ex ) ){
                foreach( $ex as $ek => $ev ){
                    if( $ev ){
                        $es2 = explode(":", $ev );
                        if( isset( $es2[0] ) && $es2[0] ){
                            $f[ $es2[0] ] = ( isset( $es2[1] ) && $es2[1] )?$es2[1]:$es2[0];
                        }
                    }
                }
            }
        }*/

        if( $f.length() > 0 ){
            $sel = "";
            if( !$value.isEmpty() && $f.has( $value ) ){
            }else{
                //$sel = " checked="checked" ";
            }

            $html += "<div>";
            $not_editable += "<pre>";

            String $name = $field_id;

            switch( $field_details.getString( "form_field" ) ){
            case "checkbox":
                $name = $field_id + "[]";
                // if( ! $value.isEmpty() )$test_values = explode( ":::", $value );
                $sel = "";
                break;
            default:
                if( !$value.isEmpty() )
                    $test_values.add( $value );
                break;
            }

            if( $test_values.size() > 0 ){
                $not_editable += "&nbsp;";
            }

            $attr = "";
            if( $field_details.has( "attributes" ) ){
                $attr = $field_details.getString( "attributes" );
            }

            $cls = "";
            if( $field_details.has( "field_identifier" ) ){
                $cls = "fi-" + $field_details.get( "field_identifier" );
            }

            for( Integer i1 = 0; i1 < $f.names().length(); i1++ ){
                String $k = $f.names().getString( i1 );
                String $v = $f.getString( $k );

                if( $test_values.contains( $k ) ){
                    $sel = " checked=\"checked\" ";
                }

                $html += "<div class=\"form-check form-check-inline form-check-blockX\">";
                $html += "<label class=\"form-check-label\"><input " + $attr + " class=\"form-check-input " + $cls + "\" " + $sel + " type=\"" + $field_details.getString( "form_field" ) + "\" name=\"" + $name + "\" id=\"" + $field_id + "-" + $k + "\" value=\"" + $k + "\" data-text=\"" + $v + "\"> " + $v + "</label>";
                $html += "</div>";

                if( !$sel.isEmpty() ){
                    $not_editable += $v + ", ";
                }

                $sel = "";
            }

            $not_editable += "</pre>";
            $html += "</div>";

        }

        if( $this_form_display_not_editable_value.has( $field_id ) ){
            $html = $not_editable;
        }

        return $html;
    }

    public static String nw_generate_form_field( JSONObject $iparams ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException{
        String $html = "";

        JSONObject $form_content = new JSONObject();
        JSONObject $special_element_class = $iparams.has( "special_element_class" ) ? $iparams.getJSONObject( "special_element_class" ) : new JSONObject();

        JSONObject $aa = $iparams.has( "aa" ) ? $iparams.getJSONObject( "aa" ) : new JSONObject();
        //System.out.println($aa);

        JSONObject $form_label = $iparams.has( "form_label" ) ? $iparams.getJSONObject( "form_label" ) : new JSONObject();
        String $field_id = $iparams.has( "field_id" ) ? $iparams.getString( "field_id" ) : "";

        String $h_content_loop = "";

        JSONObject $field_details = $form_label.has( $field_id ) ? $form_label.getJSONObject( $field_id ) : new JSONObject();
        String $field_iden = $field_details.has( "field_identifier" ) ? $field_details.getString( "field_identifier" ) : "";

        String $more_attr = "";
        String $option_text = "";

        Boolean $this_hide_record_css = $iparams.has( "hide_record_css" ) ? $iparams.getBoolean( "hide_record_css" ) : false;

        if( $field_details.has( "hidden_records_css" ) ){
            $this_hide_record_css = $field_details.getBoolean( "hidden_records_css" );
        }

        Boolean $this_hide_record = $iparams.has( "hide_record" ) ? $iparams.getBoolean( "hide_record" ) : false;

        if( $field_details.has( "hidden_records" ) ){
            $this_hide_record = $field_details.getBoolean( "hidden_records" );
        }

        if( $field_details.has( "value" ) ){
            $aa.put( $field_iden, $field_details.getString( "value" ) );

            if( $field_details.has( "value_text" ) ){
                //$more_attr += " label=\""+ $field_details.getString( "value_text" ) +"\" ";
                $option_text = $field_details.getString( "value_text" );
            }
        }

        JSONObject $this_form_display_not_editable_value = $iparams.has( "form_display_not_editable_value" ) ? $iparams.getJSONObject( "form_display_not_editable_value" ) : new JSONObject();

        String $manage_action = "new_popup_form";

        JSONObject $this_form_extra_options = $iparams.has( "this_form_extra_options" ) ? $iparams.getJSONObject( "this_form_extra_options" ) : new JSONObject();

        String $this_lbl = "";

        Integer $this_searching = 0;

        JSONObject $this_attributes = $iparams.has( "this_attributes" ) ? $iparams.getJSONObject( "this_attributes" ) : new JSONObject();

        Boolean $populate_form_with_values = $iparams.has( "populate_form_with_values" ) ? $iparams.getBoolean( "populate_form_with_values" ) : false;


        JSONObject $form_extra_options = $iparams.has( "form_extra_options" ) ? $iparams.getJSONObject( "form_extra_options" ) : new JSONObject();

        String $search_combo_option_text = "";

        String $this_field_label = "";

        String $css_hide = "";

        String $cl2 = "";

        String $con_csl = "";

        Integer $show_label = 0;

        String $this_hide_form_labels = "";

        String $mt = "";

        String $mkt = "";

        String $this_inline_edit_form = "";

        String $element_class = "";

        String $input_value_step = "";

        Integer $max_length = 0;

        String $min_value = "";

        JSONObject $_data = $iparams.has( "_data" ) ? $iparams.getJSONObject( "_data" ) : new JSONObject();

        JSONObject $option_array = $iparams.has( "option_array" ) ? $iparams.getJSONObject( "option_array" ) : new JSONObject();

        JSONObject $this_disable_form_element = $iparams.has( "disable_form_element" ) ? $iparams.getJSONObject( "disable_form_element" ) : new JSONObject();

        Integer $t = $iparams.has( "t" ) ? $iparams.getInt( "t" ) : 0;

        Integer $option_use_text = 0;


        String $this_search_combo_option = "";

        String $note = "";

        String $duplicate = "";
        String $form_field = "";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "dd-MM-yyyy" );
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        //if( $iparams.length() == 0 )return "<p style=\"color:#d42111;\">Invalid Form Settings</p>";
        $field_details = new JSONObject();
        if( $form_label.has( $field_id ) && $form_label.getJSONObject( $field_id ).length() > 0 ){
            $field_details = $form_label.getJSONObject( $field_id );
        }

        if( $form_extra_options.has( "modal" ) && !$form_extra_options.getString( "modal" ).isEmpty() ){
            $manage_action = "new_popup_form_in_popup";
        }

        if( $field_details.has( "form_field" ) ){
            $form_field = $field_details.getString( "form_field" );

            $this_lbl = $field_id;
            nwpFormGen.lbl = $field_id;
            //JSONObject $field_details_data = $field_details.has( "data" ) ? $field_details.getJSONObject( "data" ) : new JSONObject();

            $form_content.put("id", $this_lbl );
            $form_content.put("key", $field_iden );
            $form_content.put("form_field", $form_field );
            $form_content.put("show", true );
            //Check if field is not hidden
            if( ! $this_hide_record ){

                //Check if field is hidden with css
                $css_hide = "\"";
                //System.out.println($field_details);
                if( $field_details.has( "readonly") ){
                    $this_form_display_not_editable_value.put( $field_id, true );
                }

                if( $this_hide_record_css ){
                    $css_hide = " default-hidden-row\" style=\"display:none;\" ";
                    $form_content.put("secure", true );
                    $form_content.put("show", false );
                }else if( $this_form_display_not_editable_value.has( $field_id ) ){
                    $form_content.put("secure", true );
                    $form_content.put("readonly", true );
                }

                //Check in search mode -- hide all fields with css
                if( $this_searching > 0 ){
                    $css_hide = " default-hidden-row\" style=\"display:none;\" ";
                }

                JSONObject $this_attributes_disable_class = $this_attributes.has( "disable_class" ) ? $this_attributes.getJSONObject( "disable_class" ) : new JSONObject();
                if( $this_searching > 0 || $this_attributes_disable_class.has( $field_id ) || ( $this_attributes.has( "disable_all_class" ) && $this_attributes.getInt( "disable_all_class" ) > 0 ) ){
                    if( $field_details.has( "class" ) )
                        $field_details.remove( "class" );
                }

                JSONObject $this_attributes_disable_required_field = $this_attributes.has( "disable_required_field" ) ? $this_attributes.getJSONObject( "disable_required_field" ) : new JSONObject();
                if( $this_searching > 0 || $this_attributes_disable_required_field.has( $field_id ) ){
                    if( $field_details.has( "required_field" ) )
                        $field_details.remove( "required_field" );
                }

                if( $form_extra_options.has( "required_field" ) && !$form_extra_options.getString( "required_field" ).isEmpty() ){
                    $field_details.put( "required_field", $form_extra_options.getString( "required_field" ) );
                }

                if( $field_details.has( "display_position" ) ){

                    $cl2 = ( $field_details.has( "class" ) ? $field_details.getString( "class" ) : "" );

                    if( $form_extra_options.has( "class" ) && !$form_extra_options.getString( "class" ).isEmpty() ){
                        $cl2 = $form_extra_options.getString( "class" );
                    }

                    if( $field_details.has( "skip_container_class" ) ){
                        $cl2 = "";
                    }else{
                        $con_csl = $cl2 + "-row ";
                    }

                    $css_hide = " " + $form_field + "-item-con " + $css_hide;
                    if( !$cl2.isEmpty() ){
                        $css_hide = " clear " + $css_hide;
                    }

                    $h_content_loop += "<div class=\" form-group control-group input-row " + $cl2 + "-row " + $this_lbl + "-row" + $css_hide + "\">";

                    $show_label = 1;
                    switch( $form_field ){
                    case "hidden":
                    case "html":
                        $show_label = 0;
                        break;
                    }

                    if( $show_label > 0 ){

                        //DISPLAY LABEL
                        $h_content_loop += "<label class=\" control-label cell " + ( $this_lbl ) + "-label form-element-required-label-" + ( $field_details.has( "required_field" ) ? $field_details.getString( "required_field" ) : "" ) + "\">";

                        $this_field_label = $field_details.getString( "field_label" ).trim();

                        if( $form_extra_options.has( "field_label" ) && !$form_extra_options.getString( "field_label" ).isEmpty() && !$form_extra_options.getString( "field_label" ).isEmpty() ){
                            $this_field_label = $form_extra_options.getString( "field_label" );
                        }

                        $form_content.put("label", $this_field_label );
                        $h_content_loop += $this_field_label;

                        //Set Text to be displayed in search combo
                        $search_combo_option_text = $this_field_label;

                        $h_content_loop += "</label>";
                    }

                    JSONObject $field_details_calc = $field_details.has( "calculations" ) ? $field_details.getJSONObject( "calculations" ) : new JSONObject();
                    if( $form_field.equals( "text-file" ) || $form_field.equals( "calculated" ) ){
                        $form_content.put("calculations", $field_details_calc );

                        $form_field = "text";
                        if( $field_details_calc.has( "form_field" ) && !$field_details_calc.getString( "form_field" ).isEmpty() ){
                            $form_field = $field_details_calc.getString( "form_field" );

                            if( $form_extra_options.has( "attributes" ) && !$form_extra_options.getString( "attributes" ).isEmpty() ){
                                $field_details.put( "attributes", $form_extra_options.getString( "attributes" ) );
                            }

                            if( $field_details_calc.has( "action" ) && $field_details_calc.has( "todo" ) ){
                                if( !$field_details.has( "attributes" ) ){
                                    $field_details.put( "attributes", "" );
                                }

                                String fattr = $field_details.getString( "attributes" );

                                $mt = "";
                                $mkt = $field_details_calc.getString( "action" ) + $field_details_calc.getString( "todo" );
                                if( $field_details_calc.has( "todo2" ) ){
                                    $mt = $field_details_calc.getString( "todo2" );
                                    $mkt += $field_details_calc.getString( "todo2" );
                                }

                                fattr += " action=\"?action=" + $field_details_calc.getString( "action" ) + "&todo=" + $field_details_calc.getString( "todo" ) + $mt + "\" ";

                                fattr += " data-action=\"" + $field_details_calc.getString( "action" ) + "\" ";

                                if( $field_details_calc.has( "minlength" ) ){
                                    fattr += " minlength=\"" + $field_details_calc.getString( "minlength" ) + "\" ";
                                }

                            /*if( $field_details_calc.has( "tags" ) && $field_details.has( " " ) && $field_details.getString( " " ).has( "tags" ) ){
                                fattr += " tags=\""+ $field_details.getString( " " ).getString( "tags" ) +"\" ";
                            }*/

                                if( $field_details_calc.has( "data-key-field" ) ){
                                    fattr += " data-key-field=\"" + $field_details_calc.getString( "data-key-field" ) + "\" ";
                                }

                                if( $field_details_calc.has( "data-params" ) ){
                                    fattr += " data-params=\"" + $field_details_calc.getString( "data-params" ) + "\" ";
                                }

                                if( $field_details_calc.has( "key" ) && !$field_details_calc.getString( "key" ).isEmpty() ){
                                    fattr += " data-key=\"" + $field_details_calc.getString( "key" ) + "\" ";
                                }else{
                                    fattr += " data-key=\"" + GlobalFunctions.md5( $mkt ) + "\" ";
                                }

                                $field_details.put( "attributes", fattr );
                            }
                        }
                    }

                    if( $form_extra_options.has( "form_field" ) && !$form_extra_options.getString( "form_field" ).isEmpty() ){
                        $form_field = $form_extra_options.getString( "form_field" );
                    }

                    if( $this_attributes.has( $field_id ) && !$this_attributes.getString( $field_id ).isEmpty() ){
                        $this_attributes.put( "attributes", $this_attributes.getString( $field_id ) );
                    }


                    $element_class = ( ( $field_details.has( "class" ) ) ? $field_details.getString( "class" ) : " " );
                    $element_class = $element_class.replace( "col-md", "" );
                    $element_class = $element_class.replace( "col-lg", "" );
                    $field_details.put( "class", $element_class );

                    //text,password,hidden,single radio, single check
                    String $vx = "";
                    switch( $form_field ){
                    case "header":
                        if( $field_details.has( "value" ) && ! $field_details.getString( "value" ).isEmpty() ){
                            $h_content_loop += "<h4>"+ $field_details.getString( "value" ) +"</h4>";
                        }
                        break;
                    case "text":
                    case "time":
                    case "tag":
                    case "calculated":
                    case "passphrase":
                    case "password":
                    case "old-password":
                    case "hidden":
                    case "email":
                    case "tel":
                    case "number":
                    case "currency":
                    case "decimal":
                    case "date-5time":
                    case "date-5":
                    case "datetime":
                    case "date_time":
                    case "text-file":
                        //case "select":
                        //case "multi-select":

                        $input_value_step = "step=\"any\"";
                        $max_length = 200;

                        if( $form_field.equals( "number" ) ){
                            $input_value_step = "step=\"1\"";
                        }

                        switch( $form_field ){
                        case "currency":
                        case "decimal":
                        case "decimal_long":
                            $form_field = "number";
                            $input_value_step = "step=\"any\"";

                            break;
                        case "tel":
                            $input_value_step = " pattern=\"\\+[0-9]{4,14}\" ";
                            $field_details.put( "title", "Phone Numbers must be of this format. eg. +2348052529580" );
                            $field_details.put( "placeholder", "+2348052529580" );
                            break;
                        case "select":
                        case "multi-select":
                            $form_field = "text";
                            $element_class = " select2 ";
                            if( $form_extra_options.has( "form_field_options" ) && !$form_extra_options.getString( "form_field_options" ).isEmpty() ){
                                $field_details.put( "form_field_options", $form_extra_options.getString( "form_field_options" ) );

                            }

                            if( $field_details.has( "form_field_options" ) && !$field_details.getString( "form_field_options" ).isEmpty() ){

                            }
                            break;
                        }

                        if( $form_field.equals( "old-password" ) ){
                            $form_field = "password";
                        }

                        if( $form_field.equals( "passphrase" ) ){
                            $form_field = "password";
                        }

                        if( $form_field.equals( "tag" ) ){
                            $form_field = "text";
                        }

                        if( $form_field.equals( "date-5" ) || $form_field.equals( "date-5time" ) ){

                            if( $form_field.equals( "date-5time" ) ){

                                $form_field = "datetime-local";
                                $field_details.put( "placeholder", "YYYY-MM-DD:H:" );
                                $input_value_step = " current-date=\"" + dtf.format( now ) + "\"";

                            }else{

                                $form_field = "date";
                                $field_details.put( "placeholder", "YYYY-MM-DD" );
                                $input_value_step = " current-date=\"" + dtf.format( now ) + "\"";

                            }

                            if( $field_details.has( "min_date" ) && ! $field_details.getString( "min_date" ).isEmpty() ){
                                $field_details.put( "minimum", "no" );
                                if( $field_details.getString( "min_date" ).equals( "today" ) ){
                                    $more_attr += " min=\"" + dtf2.format( now ) + "\" ";
                                }else{
                                    $more_attr += " min=\"" + $field_details.getString( "min_date" ) + "\" ";
                                }
                            }
                            if( $field_details.has( "max_date" ) && ! $field_details.getString( "max_date" ).isEmpty() ){
                                if( $field_details.getString( "max_date" ).equals( "today" ) ){
                                    $more_attr += " max=\"" + dtf2.format( now ) + "\" ";
                                }else{
                                    $more_attr += " max=\"" + $field_details.getString( "max_date" ) + "\" ";
                                }
                            }

                            /*JSONObject $field_details_custom_data = $field_details.has( "custom_data" ) ? $field_details.getJSONObject( "custom_data" ) : new JSONObject();
                            if( $field_details_custom_data.has( "min-age-limit" ) && !$field_details_custom_data.getString( "min-age-limit" ).isEmpty() ){
                                $input_value_step += " min-year=\"" + $field_details_custom_data.getString( "min-age-limit" ) + "\" ";
                            }

                            if( $field_details_custom_data.has( "max-age-limit" ) && !$field_details_custom_data.getString( "max-age-limit" ).isEmpty() ){
                                $input_value_step += " max-year=\"" + $field_details_custom_data.getString( "max-age-limit" ) + "\" ";
                            }*/
                        }

                        $min_value = " min=\"0\" ";
                        if( $field_details.has( "minimum" ) && $field_details.getString( "minimum" ).equals( "no" ) ){
                            $min_value = "";
                        }

                        if( $field_details.has( "field_key" ) && !$field_details.getString( "field_key" ).isEmpty() ){
                            $more_attr += " field_key=\"" + $field_details.getString( "field_key" ) + "\" ";
                        }

                        if( $aa.has( $field_iden ) && !$aa.getString( $field_iden ).isEmpty() && $field_details_calc.length() > 0 ){
                            String $fsource = "form";
                            if( $this_form_display_not_editable_value.has( $field_id ) ){
                                $fsource = "<br />";
                            }

                            JSONObject xp = new JSONObject();
                            xp.put( "source", $fsource );
                            xp.put( "row_data", new JSONObject( "{\"" + $field_id + "\":" + $aa.getString( $field_iden ) + "}" ) );
                            xp.put( "form_field_data", $field_details );

                            $_data = new JSONObject();
                            String cal_value_text = GlobalFunctions.getCalculatedValue( $aa.getString( $field_iden ) , $field_details, new JSONObject() );
                            $option_text = cal_value_text;

                            // $_data = evaluate_calculated_value( xp );

                           /* if( $_data.has( "value" ) && !$_data.getString( "value" ).isEmpty() ){
                                if( $_data.has( "value" ) ){
                                    $option_array = $_data.getJSONObject( "value" );
                                }else{

                                }
                            }*/

                            if( $field_details_calc.has( "multiple" ) && !$field_details_calc.getString( "multiple" ).isEmpty() ){
                                $option_use_text = 1;
                            }

                        }

                        $h_content_loop += "<div class=\" controls cell-element " + ( ( $field_details.has( "icon" ) && !$field_details.getString( "icon" ).isEmpty() ) ? "input-icon" : "" ) + "\">"; // . $option_text;

                        //Not Editable Value
                        //$vx = $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "";
                        $vx = form_value( $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "", $populate_form_with_values, $field_id, $field_details );

                        $form_content.put("ivalue", $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "" );
                        $form_content.put("value", $vx );

                        if( $this_form_display_not_editable_value.has( $field_id ) ){
                            /* if( ! $option_use_text ){
                                $vx = "";
                            } */
                            if( !$option_text.isEmpty() ){
                                $aa.put( $field_iden, $option_text );
                                $vx = $option_text;
                            }
                            $field_details.put( "display_text", 1 );

                            if( $vx.isEmpty() ){
                                $vx = "&nbsp;";
                            }

                            $h_content_loop += "<pre class=\"not-editable-form-element-value\">" + $vx + "</pre>";
                        }else{
                            if( $field_details.has( "icon" ) )
                                $h_content_loop += $field_details.getString( "icon" );

                            $h_content_loop += "<input value=\"" + $vx + "\" tabindex=\"" + ( $t + 1 ) + "\" class=\" form-control form-gen-element " + $field_id + " demo-input-local " + ( $special_element_class.has( $field_id ) ? $special_element_class.getString( $field_id ) : " " ) + " " + $element_class + ( ( $field_details.has( "required_field" ) && $field_details.getString( "required_field" ).equals( "yes" ) ) ? " form-element-required-field\" required=\"required" : " " ) + "\" type=\"" + $form_field + "\" id=\"" + $field_id + "\" name=\"" + $field_id + "\" tip=\"" + ( $field_details.has( "tooltip" ) ? $field_details.getString( "tooltip" ) : "" ) + "\" placeholder=\"" + ( $field_details.has( "placeholder" ) ? $field_details.getString( "placeholder" ) : "" ) + "\" " + ( $this_disable_form_element.has( $field_id ) ? $this_disable_form_element.getString( $field_id ) : "" ) + " title=\"" + ( $field_details.has( "title" ) ? $field_details.getString( "title" ) : "" ) + "\" alt=\"" + $form_field + "\" data-type=\"" + $form_field + "\" data-validate=\"" + $form_field + "\" maxlength=\"" + $max_length + "\" " + $input_value_step + " " + ( $field_details.has( "attributes" ) ? $field_details.getString( "attributes" ) : "" ) + " " + $min_value + " label=\"" + $option_text + "\" " + $more_attr + " /><span class=\"input-status\"></span>";

                            if( $option_array.length() > 0 ){
                                $h_content_loop += "<textarea style=\"display:none;\" id=\"" + $field_id + "-option-array\">" + $option_array.toString() + "</textarea>";
                            }

                        }
                        $h_content_loop += "</div>";
                        break;
                    case "single_json_data":
                    case "multiple_json_data":
                        if( $aa.has( "id" ) && !$aa.getString( "id" ).isEmpty() && $field_details.has( "form_field_options" ) && $field_details.getString( "form_field_options" ).isEmpty() ){
                            $h_content_loop += "<div class=\"controls cell-element\">";
                            // $h_content_loop += "<a href=\"#\" class=\"btn btn-sm btn-default btn-block custom-single-selected-record-button\" action=\"?module=&action=json_options&todo="+ $manage_action +"&reference_id="+$aa[\"id\"]+"&reference_table="+$this_table+"&option_table="+$field_details[\"form_field_options\"]+"\" override-selected-record=\"-\" title=\""+$this_field_label+"\">Manage "+$this_field_label+"</a><br />";
                            $h_content_loop += "</div>";
                        }else{
                            $h_content_loop += "<div class=\"controls cell-element\">Record must be saved first</div>";
                        }
                        break;
                    case "checkbox":
                    case "radio":

                        JSONObject $option = new JSONObject();
                        if( $field_details.has( "form_field_options" ) && $field_details.getJSONObject( "form_field_options" ).length() > 0 ){
                            $option = $field_details.getJSONObject( "form_field_options" );
                            $form_content.put("form_field_options", $option );
                        }

                        if( $option.length() > 0 ){
                            $h_content_loop += "<div class=\"controls cell-element\">";
                            $h_content_loop += "GROUPBOXES";
                            //                                $h_content_loop += group_boxes( $option, $field_id, $field_details, $t, form_value( ( $aa.has( $field_id )? $aa.getString( $field_id ) : "" ), $populate_form_with_values , $field_id , $field_details ) );
                            $h_content_loop += "<span class=\"input-status \"></span></div>";
                        }

                        break;
                    case "date":
                        //                    case "date_time":
                    case "date-time":
                        $h_content_loop += "<div class=\" controls cell-element\">";

                        $h_content_loop += "DATExxx";
                        // $h_content_loop += date( $t, form_value( ( $aa.has( $field_iden ) ? $aa.get( $field_iden ) : "" ) , $populate_form_with_values , $field_id , $field_details ) , $field_details , $field_id );

                        $h_content_loop += "</div><span class=\"input-status\"></span>";
                        break;
                    case "select":
                    case "multi-select":
                        $h_content_loop += "<div class=\" controls cell-element\">";
                        //Initialize key value pair
                        JSONObject $key = new JSONObject();
                        JSONObject $value = new JSONObject();

                        if( $form_extra_options.has( "form_field_options" ) && !$form_extra_options.getString( "form_field_options" ).isEmpty() ){
                            $field_details.put( "form_field_options", $form_extra_options.getString( "form_field_options" ) );
                        }
                        if( $field_details.has( "form_field_options" ) ) {
                            $form_content.put("form_field_options", $field_details.getString("form_field_options"));
                        }
                        if( $this_form_display_not_editable_value.has( $field_id ) ) {
                            $field_details.put( "readonly", true );
                        }

                        $vx = form_value( ( $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "" ), $populate_form_with_values, $field_id, $field_details );
                        $form_content.put("ivalue", $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "" );
                        $form_content.put("value", $vx );

                        $h_content_loop += select( $field_details, $key, $value, $field_id, "", $vx, "", "", new JSONObject(), $t, new JSONObject() );

                        $h_content_loop += "<span class=\"input-status\"></span></div>";
                        break;
                    case "picture":
                    case "file":
                        if( !$this_form_display_not_editable_value.has( $field_id ) ){
                            //$h_content_loop += "<input alt=""+$form_field+"" type="hidden" class=""+$field_id+"-replace" /><img id=""+$field_id+"-img" class="form-gen-element-image-upload-preview" style="display:none;" /><div class="controls cell-element upload-box " id="upload-box-"+$t+"">";
                            //"+ ( ( $field_details.has( "class"]) ) ? $field_details[ "class" ] : "" ) +"

                            JSONObject opt = new JSONObject();
                            opt.put( "label", $this_field_label );
                            opt.put( "field_id", $field_id );
                            opt.put( "t", $t + 1 );
                            opt.put( "attributes", ( $field_details.has( "attributes" ) ? $field_details.getString( "attributes" ) : "" ) );
                            opt.put( "acceptable_files_format", ( $field_details.has( "acceptable_files_format" ) ? $field_details.getString( "acceptable_files_format" ) : "" ) );

                            $h_content_loop += GlobalFunctions.get_file_upload_form_field( opt );
                            // $h_content_loop += $this_upload( $field_details , $field_id , $this_form_value( ( $aa.has( $field_id )? $aa.get( $field_id ) : "" ) , $populate_form_with_values , $field_id , $field_details ), $t );
                            //$h_content_loop += "<span class=\"input-status\"></span></div>";

                            if( $field_details.has( "tooltip" ) ){
                                $h_content_loop += "<i>" + $field_details.getString( "tooltip" ) + "</i>";
                            }

                        }

                        // $h_content_loop += "<div class=\"file-content\">"+get_uploaded_files( $this_class_settings["calling_page"] , form_value( ( $aa.has( $field_id )? $aa.get( $field_id ) : "" ) , $populate_form_with_values , $field_id , $field_details ), $field_details.getString( "field_label" ) , $field_id )+"</div>";
                        break;
                    case "textarea":
                    case "textarea-unlimited":
                    case "textarea-unlimited-med":
                    case "textarea-norestriction":
                        String $vx2 = form_value( ( $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "" ), $populate_form_with_values, $field_id, $field_details );

                        $form_content.put("ivalue", $aa.has( $field_iden ) ? $aa.getString( $field_iden ) : "" );
                        $form_content.put("value", $vx2 );

                        $h_content_loop += "<div class=\" controls cell-element\">";
                        if( $this_form_display_not_editable_value.has( $field_id ) ){
                            if( $vx2.isEmpty() ){
                                $vx2 = "&nbsp;";
                            }

                            $h_content_loop += "<pre class=\"not-editable-form-element-value\">" + $vx2 + "</pre>";
                        }else{
                            $h_content_loop += textarea( $field_id, $vx2, $t, $field_details );
                        }

                        $h_content_loop += "<span class=\"input-status\"></span></div>";
                        break;
                    case "category":
                        $h_content_loop += "<div class=\"controls cell-element\">";
                        // $h_content_loop += $this_category($form_field,$t,$this_form_value( isset($aa[$t])?$aa[$t]:"" ,$populate_form_with_values , $field_id , $field_details ));
                        $h_content_loop += "CATEGORYX";
                        $h_content_loop += "</div>";
                        break;
                    case "group-file":
                        $h_content_loop += "<div class=\"controls cell-element\">";
                        $h_content_loop += "GROUPX";
                        // $h_content_loop += $this_group_upload("","","",$t);
                        $h_content_loop += "<span class=\"input-status\"></span></div>";
                        break;
                    case "html":
                    case "field_group":
                        $h_content_loop += "FIELD_GROUP";
                        // $h_content_loop += $this_get_field_group( $field_id, $field_details, ( $aa.has( $field_id )? $aa.get( $field_id ) : "" ) );
                        break;
                    }

                    //Search Combo Options -- if field is not hidden
                /*if( $form_field.equals( "file" ) && ! $form_field.equals( "group-file" ) && $this_searching){
                    $this_search_combo_option += "<option value=\""+ ( $this_lbl ) +"-row\">"+$search_combo_option_text+"</option>";
                }*/

                    if( !$this_form_display_not_editable_value.has( $field_id ) ){
                        $note = "";
                        if( $form_extra_options.has( "note" ) && !$form_extra_options.getString( "note" ).isEmpty() ){
                            $note = $form_extra_options.getString( "note" );
                        }else{
                            if( $field_details.has( "note" ) ){
                                $note = $field_details.getString( "note" );
                            }
                            if( $field_details.has( "note" ) ){
                                $note = $field_details.getString( "note" );
                            }
                        }

                        if( !$note.isEmpty() ){
                            $h_content_loop += "<i>" + $note + "</i>";
                        }

                    }

                    //CLOSE THE ROW
                    $h_content_loop += "</div>";
                }

            }

            JSONObject $this_attributes_drange = $this_attributes.has( "show_date_range" ) ? $this_attributes.getJSONObject( "show_date_range" ) : new JSONObject();
            if( $this_attributes_drange.has( $field_id ) && !$this_attributes_drange.getString( $field_id ).isEmpty() ){
                //duplicate field
                $duplicate = $h_content_loop.replace( $field_id, $field_id + "_range" );
                $h_content_loop = $h_content_loop.replace( $this_field_label, "Start " + $this_field_label );
                $duplicate = $duplicate.replace( $this_field_label, "End " + $this_field_label );
                $h_content_loop += $duplicate;
            }
        }

        if( $form_content.has("id") ) {
            if( ! form_content.has("fields" ) ){
                form_content.put("fields", new JSONArray() );
            }
            form_content.getJSONArray("fields" ).put( $form_content );
        }

        return $h_content_loop;
    }

    private static String select( JSONObject $field_details, JSONObject $key, JSONObject $value, String $field_id, String $cus_name, String $val, String $display, String $elementid, JSONObject $end, Integer $serial_number, JSONObject $others ) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException{
        String $returning_html_data = "";

        String $array = "";

        String $multi = "";

        String $data_role = "";

        String $autocomplete_select = "";

        String $select_option_tooltip = "";

        JSONObject $special_element_class = $others.has( "special_element_class" ) ? $others.getJSONObject( "special_element_class" ) : new JSONObject();

        JSONObject $form_display_not_editable_value = $others.has( "form_display_not_editable_value" ) ? $others.getJSONObject( "form_display_not_editable_value" ) : new JSONObject();

        JSONObject $disable_form_element = $others.has( "disable_form_element" ) ? $others.getJSONObject( "disable_form_element" ) : new JSONObject();

        JSONObject $array_of_accessible_functions_tooltips = new JSONObject();

        JSONObject $sel1 = new JSONObject();

        String $data_native_menu = "";

        String $h_not_editable = "";

        String $sel = "";

        Integer $n = 0;

        String $ctrl_form = $field_details.has( "form_field" ) ? $field_details.getString( "form_field" ) : "";

        if( $field_details.has( "readonly" ) ){
            $form_display_not_editable_value.put( $field_id, true );
        }
        if( $field_details.has( "options" ) && !$field_details.getString( "options" ).isEmpty() ){
            /*$ex = explode(";", $field_details[ "options" ] );

            if( is_array( $ex ) && ! empty( $ex ) ){
                foreach( $ex as $ek => $ev ){
                    if( $ev ){
                        $es2 = explode(":", $ev );
                        if( isset( $es2[0] ) && $es2[0] ){
                            $key[ $ek ] = $es2[0];
                            $value[ $ek ] = ( isset( $es2[1] ) && $es2[1] )?$es2[1]:$es2[0];
                        }
                    }
                }
            }*/
        }

        //$key.put("meat","Calton Meat");
        //$key.put("grass","Bush Meat");
        $key = GlobalFunctions.selectBoxOptions( $field_details );
        if( $key.length() == 0 ) {
            return $returning_html_data;
        }

        if( $ctrl_form.equals( "multi-select" ) ){
            /* if( isset( $this->form_display_not_editable_value[ $field_id ] ) ){
                return '<pre>' . __get_value( $val, $field_id, array( 'globals' => array( $field_id => $field_details ), "text-date" => 1 ) ) . '</pre>";

            } */
            //Multi-select Menu
            $data_role = "data-role=\"none\"";

            $multi = "multiple=\"multiple\" size=\"11\"";
            $array = "[]";

            if( !$val.isEmpty() ){
                String[] $val2 = $val.split( ":::" );

                if( $val2.length > 0 ){

                    for( Integer i1 = 0; i1 < $val2.length; i1++ ){
                        String v = $val2[ i1 ];

                        $sel1.put( v, "selected=\"selected\"" );
                    }
                }
            }

        }


        /*------------------------------------------*/
        //Remove after resolving pop-up issue in forms
        $data_native_menu = "true";
        /*------------------------------------------*/

        $h_not_editable = "";

        $returning_html_data += "<select " + $multi + " data-mini=\"true\" data-native-menu=\"" + $data_native_menu
                + "\" tabindex=\"" + ( $serial_number + 1 ) + "\" class=\"form-gen-element form-control " + $autocomplete_select
                + " " + $display + " " + ( $others.has( "lbl" ) ? $others.getString( "lbl" ) : "" ) + " "
                + ( $special_element_class.has( $field_id ) ? $special_element_class.getString( $field_id ) : "" )
                + " " + ( $field_details.has( "class" ) ? $field_details.getString( "class" ) : "" )
                + ( ( $field_details.has( "required_field" ) && $field_details.getString( "required_field" ).equals("yes") ) ? "form-element-required-field" : "" )
                + "\" name=\"" + $field_id + $cus_name + $array + "\" id=\""
                + $field_id + $cus_name + $array + "\" " + $data_role + " " + ( $disable_form_element.has( $field_id ) ? $disable_form_element.getString( $field_id ) : "" )
                + " " + ( $form_display_not_editable_value.has( $field_id ) && $form_display_not_editable_value.getInt( $field_id ) > 0 ? "style=\"display:none;\"" : "" )
                + " " + ( $field_details.has( "attributes" ) ? $field_details.getString( "attributes" ) : "" )
                + ( ( $field_details.has( "required_field" ) && $field_details.getString( "required_field" ).equals("yes") ) ? " required " : "" )
                + " alt=\"" + $field_details.getString( "form_field" ) + "\" data-type=\"" + $field_details.getString( "form_field" ) + "\" >";
        ////if($multi)$returning_html_data += "<option>"+ucwords(str_replace('-',' ',$this->lbl))+"</option>";

        //If Search Mode is active ensure first select element is null
        if( $others.has( "searching" ) || $others.has( "add_empty_select_option" ) || ( $field_details.has( "add_empty" ) && $field_details.getInt( "add_empty" ) > 0 ) )
            $returning_html_data += "<option value=\"\"></option>";

        if( $field_details.has( "form_field_options_group" ) ){

            String $fnx = $field_details.getString( "form_field_options_group" );
            Method method = GlobalFunctions.class.getDeclaredMethod( $fnx );
            Object[] effectiveParameters = new Object[]{ 5, "hello" };
            JSONObject $options = ( JSONObject ) method.invoke( null, effectiveParameters );

            for( Integer i1 = 0; i1 < $options.names().length(); i1++ ){
                String $opt = $options.names().getString( i1 );
                JSONObject $opts = $options.getJSONObject( $opt );

                $returning_html_data += "<optgroup label=\"" + $opt + "\">";
                for( Integer i2 = 0; i2 < $opts.names().length(); i2++ ){
                    String $k = $opts.names().getString( i2 );
                    String $v = $opts.getString( $k );

                    $sel = "";
                    if( ( $val == $k && $val != null ) || ( $sel1.has( $k ) && !$sel1.getString( $k ).isEmpty() ) )
                        $sel = "selected=\"selected\"";

                    $returning_html_data += "<option " + $sel + " value=\"" + $k + "\">" + $v + "</option>";
                }
                $returning_html_data += "</optgroup>";
            }
            $returning_html_data += "</select>";

        }else{

            if( $key.names().length() > 0 ){
                for( int i2 = 0; i2 < $key.names().length(); i2++ ){
                    String $x = $key.names().getString( i2 );
                    String $k = $key.getString( $x );

                    if( !$val.isEmpty() && $x.equals( $val ) ){
                        $sel = "selected=\"selected\"";

                        if( $form_display_not_editable_value.has( $field_id ) ){
                            //if($data_native_menu != "true')
                            $h_not_editable = "<pre class=\"not-editable-form-element-value\">" + GlobalFunctions.ucwords( $k ).replace( "_", " " ) + "</pre>";
                        }

                    }else{
                        $sel = "";
                    }

                    $returning_html_data += "<option " + $sel + " alt=\"" + ( $end.has( String.valueOf( $n ) ) ? $end.getString( String.valueOf( $n ) ) : "" ) + "\" title=\"" + GlobalFunctions.ucwords( $k ).replace( "_", " " ) + "\" " + $select_option_tooltip + " value=\"" + $x + "\">" + ( $k ).replace( "_", " " ) + "</option>";

                    ++$n;
                }
            }
            $returning_html_data += "</select>";
        }

        return $h_not_editable + $returning_html_data;

    }

}