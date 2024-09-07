package codes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class nwpConnectToAPI {
    public static String email_table = "fiaps_email_notification";

    public static void main(String[] args) {
        nwpConnectToAPI.get_staff_details( new JSONObject().put("employee_no", "234") );
    }

    public static JSONObject ecm_cash_advance_submit_to_erp(JSONObject input){
        //Input contract
        /*"cash_advance": JSONObject
        "workflow": JSONObject
        "source": "nwpCashAdvance.workflowCallback"
        "user_data": JSONObject*/

        JSONObject r = new JSONObject();    //returned object
        String error = "";  //return error to halt further execution
        String info = "";   //return info, does not halt further execution

        Double caAmount = 0.0;
        String caEmployeeNum = "";
        String caApplicant = "";
        String caApprovedBy = "";
        String caDate = GlobalFunctions.get_current_time( 5 );
        String caRefNum = "";
        String caDesc = "";

        //Validate Contract
        if( error.isEmpty() ) {
            JSONObject cashAdvance = new JSONObject();
            if (input.has("cash_advance") && input.getJSONObject("cash_advance").has("id")) {
                cashAdvance = input.getJSONObject("cash_advance");

                if( cashAdvance.has("amount") ){
                    caAmount = Double.valueOf( cashAdvance.getString("amount") );
                }

                if( caAmount <= 0 ){
                    error = "<h4>No Amount</h4>Connection to ERP halted because the Cash Advance has no total amount";
                }

                if( error.isEmpty() ) {
                    if( cashAdvance.has("name") && ! cashAdvance.getString("name").isEmpty() ){
                        caRefNum = cashAdvance.getString("name");
                    }else{
                        error = "<h4>No Reference Number</h4>Connection to ERP halted because the Cash Advance has no Reference Number";
                    }
                }

                if( error.isEmpty() ) {
                    if( cashAdvance.has("type") && ! cashAdvance.getString("type").isEmpty() ){
                        caDesc = cashAdvance.getString("type");
                        JSONObject caCode = GlobalFunctions.selectBoxOptions( "ecm_cash_advance_types" );
                        if( caCode.has( caDesc ) && ! caCode.getString( caDesc ).isEmpty() ){
                            caDesc = caCode.getString( caDesc );
                        }

                        if( cashAdvance.has("reason") && ! cashAdvance.getString("reason").isEmpty() ){
                            caDesc += ": " + cashAdvance.getString("reason");
                        }
                    }else{
                        error = "<h4>No Description (CA Type)</h4>Connection to ERP halted because the Cash Advance has no Description (CA Type)";
                    }
                }


                if( error.isEmpty() ) {
                    if( cashAdvance.has("staff") && ! cashAdvance.getString("staff").isEmpty() ){
                        //get staff username
                        caApplicant = cashAdvance.getString("staff");

                        caEmployeeNum = GlobalFunctions.get_record_name( new JSONObject()
                                .put("id", caApplicant )
                                .put("table", "users" )
                                .put("name_key", "employee_no" )
                        );
                        if( caEmployeeNum.isEmpty() || caEmployeeNum.equals("__no_ref__") || caEmployeeNum.equals( caApplicant ) ){
                            error = "<h4>Applicant has No Employee No.</h4>Connection to ERP halted because the Cash Advance has no applicant Employee Number";
                        }
                    }else{
                        error = "<h4>No Applicant</h4>Connection to ERP halted because the Cash Advance has no applicant specified";
                    }
                }

                if( error.isEmpty() ) {
                    if( input.has("user_data") && input.getJSONObject("user_data").has("id") && ! input.getJSONObject("user_data").getString("id").isEmpty() ){
                        caApprovedBy = input.getJSONObject("user_data").getString("id");
                    }else{
                        error = "<h4>No Approver</h4>Connection to ERP halted because you are not logged in";
                    }
                }
            } else {
                error = "<h4>No Cash Advance Record</h4>Connection to ERP halted";
            }
        }

        //API CALL TO SUBMIT DATA TO ERP
        if( error.isEmpty() ) {
            String api_key = "create_invoice";
            JSONObject endPoint = nwpCURL.getEndpointsSettings(
                    new JSONObject()
                            .put("key", api_key )
            );

            if (endPoint.has("url")
                    && endPoint.has("request_method")
                    && endPoint.has("content_type")
                    && endPoint.has("header_options")
                    && endPoint.has("pay_load")
            ) {
                try {
                    JSONObject payLoad = new JSONObject(endPoint.get("pay_load").toString());

                    payLoad.put("employee_no", caEmployeeNum );
                    payLoad.put("invoice_date", caDate );
                    payLoad.put("invoice_amount", caAmount );
                    payLoad.put("invoice_no", caRefNum );
                    payLoad.put("description", caDesc );

                    JSONObject curlRes = nwpCURL.callURL(
                            endPoint.getString("url"),
                            endPoint.getString("request_method"),
                            endPoint.getString("content_type"),
                            payLoad.toString(),
                            new JSONObject(endPoint.get("header_options").toString()).put("api_key", api_key )
                    );

                    if (curlRes.has("json") && curlRes.getJSONObject("json").length() > 0) {
                        input.put( "response", curlRes.getJSONObject("json") );
                    } else if (curlRes.has("string")) {
                        input.put("response_string", curlRes.getJSONObject("string") );
                        error = "<h4>Failed to Parse Response from ERP</h4>";
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "API ENDPOINT CALL: " + e.getMessage())
                                    .put("input", new JSONObject()
                                            .put("api_key", api_key)
                                            .put("input", input)
                                            .put("endPoint", endPoint))
                                    .put("function", "nwpConnectToAPI.send_email")
                                    .put("exception", true)
                                    .put("fatal", true), e
                    );
                }
            } else {
                error = "<h4>Failed to Connect to ERP</h4>bad endpoint settings";
            }
        }

        if( ! error.isEmpty() ){
            r.put("error", error);
        }

        if( ! info.isEmpty() ){
            r.put("info", info);
        }

        return r;
    }

    public static JSONObject send_email(JSONObject input){
        //Input contract
        /*
        subject: subject
        content: content
        sender: JSONObject()
        recipients: users.getJSONArray("row")
        reference_table: nwpWorkflow.table_name
        reference: $workflow.has("id")
        user_data: GlobalFunctions.app_user_data
        */

        JSONObject r = new JSONObject();    //returned object
        String error = "";  //return error to halt further execution
        String info = "";   //return info, does not halt further execution
        String validEmails = "";
        String sender = "paperlite@cbn.gov.ng";
        if( GlobalFunctions.app_data.has("admin_email") && ! GlobalFunctions.app_data.getString("admin_email").isEmpty() ){
            if( GlobalFunctions.validateEmail( GlobalFunctions.app_data.getString("admin_email") ) ) {
                sender = GlobalFunctions.app_data.getString("admin_email");
            }
        }

        //Validate Contract
        if( error.isEmpty() ) {

            if (input.has("user_data")
                    && input.has("reference_table")
                    && input.has("reference")
                    && input.has("content")
                    && ! input.getString("content").isEmpty()
                    && input.has("subject")  && ! input.getString("subject").isEmpty()
                    && input.has("recipients") && input.getJSONArray("recipients").length() > 0 ) {
                //validate recipients
                String invalidEmail = "";
                r.put("valid_emails", new JSONArray() );

                for(Object rec : input.getJSONArray("recipients") ){
                    JSONObject recip = new JSONObject( rec.toString() );
                    if( recip.has("email") && ! recip.getString("email").isEmpty() ){
                        if( GlobalFunctions.validateEmail( recip.getString("email") ) ){
                            if( ! validEmails.isEmpty() ){
                                validEmails += ",";
                            }
                            validEmails += recip.getString("email");
                            r.getJSONArray("valid_emails").put( recip );
                        }else{
                            invalidEmail += recip.getString("email") + ", ";
                        }
                    }
                }


                if( ! ( GlobalFunctions.app_data.has("always_use_admin_email") && GlobalFunctions.app_data.getBoolean("always_use_admin_email") ) ){
                    if( input.getJSONObject("user_data").has("email")
                            && ! input.getJSONObject("user_data").getString("email").isEmpty() ){
                        if( GlobalFunctions.validateEmail( input.getJSONObject("user_data").getString("email") ) ) {
                            sender = input.getJSONObject("user_data").getString("email");
                        }
                    }
                }

                if( ! ( r.has("valid_emails") && r.getJSONArray("valid_emails").length() > 0 ) ){
                    error = "<h4>Invalid Email Recipient</h4>No valid email was address found.<br>" + invalidEmail;
                }
            } else {
                error = "<h4>No Email Contract Found</h4>No Content, Subject or Recipients";
            }
        }

        String emailStatus = "pending";

        //API CALL TO SEND EMAIL
        if( error.isEmpty() ) {

            JSONObject endPoint = nwpCURL.getEndpointsSettings(
                    new JSONObject()
                            .put("key", "send_email")
            );

            if (endPoint.has("url")
                    && endPoint.has("request_method")
                    && endPoint.has("content_type")
                    && endPoint.has("header_options")
                    && endPoint.has("pay_load")
            ) {
                try {
                    JSONObject payLoad = new JSONObject(endPoint.get("pay_load").toString());

                    payLoad.getJSONObject("RqData").put("sender", sender);
                    payLoad.getJSONObject("RqData").put("receiver", validEmails);
                    payLoad.getJSONObject("RqData").put("subject", input.getString("subject"));
                    payLoad.getJSONObject("RqData").put("body", input.getString("content"));

                    JSONObject curlRes = nwpCURL.callURL(
                            endPoint.getString("url"),
                            endPoint.getString("request_method"),
                            endPoint.getString("content_type"),
                            payLoad.toString(),
                            new JSONObject(endPoint.get("header_options").toString()).put("api_key", "send_email")
                    );

                    if (curlRes.has("json") && curlRes.getJSONObject("json").length() > 0) {
                        emailStatus = "processed";
                        input.put("response", curlRes.getJSONObject("json"));
                    } else if (curlRes.has("string")) {
                        input.put("response_string", curlRes.getJSONObject("string"));
                    }
                } catch (Exception e) {
                    emailStatus = e.getMessage();
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "API ENDPOINT CALL: " + e.getMessage())
                                    .put("input", new JSONObject()
                                            .put("input", input)
                                            .put("endPoint", endPoint))
                                    .put("function", "nwpConnectToAPI.send_email")
                                    .put("exception", true)
                                    .put("fatal", true), e
                    );
                }
            } else {
                emailStatus = "bad endpoint settings";
            }
        }

        if( error.isEmpty() ) {
            Date currentDate = new Date();
            long timestamp = currentDate.getTime() / 1000;

            Boolean sCache = false;
            Boolean sDb = false;
            Boolean sDbPurge = true;

            switch( GlobalFunctions.cache_email ) {
                case 4: //do not purge db
                    sDb = true;
                    sDbPurge = false;
                    break;
                case 3: //save in db only
                    sDb = true;
                    break;
                case 2: //save in db & cache
                    sCache = true;
                    sDb = true;
                    break;
                case 1: //cache only
                    sCache = true;
                break;
            }

            if( sCache ){
                GlobalFunctions.setCache(new JSONObject()
                        .put("cache", input)
                        .put("cache_type", "email")
                        .put("table", "email")
                        .put("id", GlobalFunctions.GetNewID("email"))
                );
            }

            //sDb = true;
            if( sDb ) {
                //not yet complete, might change table schema
                JSONObject $emailFields = new JSONObject();
                JSONObject edep = GlobalFunctions.get_json( nwpConnectToAPI.email_table );
                if( edep.has( "fields" ) ){
                    $emailFields = edep.getJSONObject( "fields" );
                }

                if (sDbPurge) {
                    //delete old records

                    GlobalFunctions.delete_records( new JSONObject()
                            .put("table", nwpConnectToAPI.email_table)
                            .put("where", " [creation_date] <= " + String.valueOf(  timestamp - (3600*24*7) ) + " " )
                    );
                }

                JSONObject li = new JSONObject();

                li.put( $emailFields.getString( "reference" ), input.getString("reference") );
                li.put( $emailFields.getString( "reference_table" ), input.getString("reference_table") );
                li.put( $emailFields.getString( "date" ), GlobalFunctions.get_current_time(1) );
                li.put( $emailFields.getString( "recipient" ), validEmails );
                li.put( $emailFields.getString( "status" ), emailStatus );
                li.put( $emailFields.getString( "current_user" ), input.getJSONObject("user_data").getString("id") );
                li.put( $emailFields.getString( "subject" ), input.getString("subject") );
                //li.put( $emailFields.getString( "data" ), GlobalFunctions.rawurlencode( input.toString() ) );

                JSONObject iData = new JSONObject( input.toString() );
                if( iData.has("user_data") ){
                    iData.remove("user_data");
                }
                li.put( $emailFields.getString( "data" ), iData.toString() );

                JSONObject a = new JSONObject();

                a.put( "table",nwpConnectToAPI.email_table );
                a.put( "todo", "create_new_record" ); // create_new_record
                a.put( "post_data", li );

                JSONObject $rr = nwpDataTable.saveDataForm( a );

                if( $rr.has( "error" ) && !$rr.getString( "error" ).isEmpty() ){

                }
            }

            error = "<h4>Failed to Connect to Email Server</h4>";
        }

        if( ! error.isEmpty() ){
            r.put("error", error);
        }

        if( ! info.isEmpty() ){
            r.put("info", info);
        }

        return r;
    }

    public static JSONObject get_staff_details( JSONObject input ){
        //Input contract
        /*"employee_no": String
        "user_data": JSONObject*/

        JSONObject r = new JSONObject();    //returned object
        String error = "";  //return error to halt further execution
        String info = "";   //return info, does not halt further execution

        String employeeNum = "";

        //Validate Contract
        if( error.isEmpty() ) {
            if (input.has("employee_no") && ! input.getString("employee_no").isEmpty() ) {
                employeeNum = input.getString("employee_no");
            } else {
                error = "<h4>No Employee Num</h4>Connection to Staff Details API halted";
            }
        }

        //API CALL TO SUBMIT DATA TO Staff Details
        if( error.isEmpty() ) {
            String api_key = "staff_details";
            JSONObject endPoint = nwpCURL.getEndpointsSettings(
                    new JSONObject()
                            .put("key", api_key )
            );

            if (endPoint.has("url")
                    && endPoint.has("request_method")
                    && endPoint.has("content_type")
                    && endPoint.has("header_options")
                    && endPoint.has("pay_load")
            ) {
                try {


                    String url = endPoint.getString("url");
                    JSONObject curlRes;
                    if( endPoint.getString("request_method").toLowerCase().equals("get") ){
                        url += "?staff_id=" + employeeNum;
                        curlRes = nwpCURL.callURL(
                                url,
                                endPoint.getString("request_method"),
                                endPoint.getString("content_type"),
                                "",
                                new JSONObject( endPoint.get("header_options").toString() ).put("api_key", api_key )
                        );
                    }else {
                        JSONObject payLoad = new JSONObject(endPoint.get("pay_load").toString());
                        payLoad.put("employee_no", employeeNum);
                        curlRes = nwpCURL.callURL(
                                url,
                                endPoint.getString("request_method"),
                                endPoint.getString("content_type"),
                                payLoad.toString(),
                                new JSONObject(endPoint.get("header_options").toString()).put("api_key", api_key )
                        );
                    }

                    if (curlRes.has("json") && curlRes.getJSONObject("json").length() > 0) {

                        /*if( curlRes.getJSONObject("json").has("Response-element")
                                && curlRes.getJSONObject("json").getJSONObject("Response-element").has("grade")
                                && curlRes.getJSONObject("json").getJSONObject("Response-element").getJSONObject("grade").has("$")
                                && ! curlRes.getJSONObject("json").getJSONObject("Response-element").getJSONObject("grade").get("$").toString().isEmpty()
                        ) {
                            r.put("grade_level", curlRes.getJSONObject("json").getJSONObject("Response-element").getJSONObject("grade").get("$").toString() );
                        }else{
                            error = "<h4>Failed to Retrieve GRADE from Staff Details API Response</h4>";
                        }*/

                        if( curlRes.getJSONObject("json").has("grade")
                                && ! curlRes.getJSONObject("json").getString("grade").isEmpty() ) {

                            r.put("grade_level", curlRes.getJSONObject("json").getString("grade") );

                        }else{
                            error = "<h4>Failed to Retrieve GRADE from Staff Details API Response</h4>";
                        }

                    } else if (curlRes.has("string")) {
                        error = "<h4>Failed to Parse Response from Staff Details API</h4>";
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                    GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "API ENDPOINT CALL: " + e.getMessage())
                                    .put("input", new JSONObject()
                                            .put("api_key", api_key)
                                            .put("input", input)
                                            .put("endPoint", endPoint))
                                    .put("function", "nwpConnectToAPI.get_staff_details")
                                    .put("exception", true)
                                    .put("fatal", true), e
                    );
                }
            } else {
                error = "<h4>Failed to Connect to Staff Details API</h4>bad endpoint settings";
            }
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
