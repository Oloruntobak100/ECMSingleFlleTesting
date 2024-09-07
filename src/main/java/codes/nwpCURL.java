package codes;

import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;

public class nwpCURL {

    public static void main(String[] args) {
        /*String tempDir = System.getProperty( "java.io.tmpdir" );
        System.out.println( "xxx" );
        System.out.println( tempDir );*/
        String jspFile = "views/users/new_user_form.jsp";
        System.out.println( jspFile.substring(0,1) );
        if( jspFile.substring(0,1).equals("/") ){
            jspFile = jspFile.substring(1, jspFile.length() );
        }
        System.out.println( jspFile );

        //test CURL call
       JSONObject curlRes = callURL(
                "http://localhost:819/feyi/engine/xml-server.php?b=staff",
                "GET",
                "",
                "",
                new JSONObject()
                        .put( "return_json", true )
        );

        /* JSONObject curlRes = callURL(
                "http://localhost:819/feyi/engine/xml-server.php",
                "POST",
                "application/json",
                new JSONObject().put("james", "john").toString(),
                new JSONObject()
                        .put("return_json", true )
                        .put("RequestProperty",
                                new JSONObject()
                                        .put("Accept", "application/xml")
                        )
        );*/

        System.out.println( curlRes );
    }

    public static JSONObject formatResponse( JSONObject opt ) {
        JSONObject r = new JSONObject();

        if( opt.has("string") ){
            //try for xml
            try {
                if( GlobalFunctions.app_data.has("xml_api_response") && GlobalFunctions.app_data.getBoolean("xml_api_response") ) {
                    JSONObject xData = XML.toJSONObject(opt.getString("string"));
                    if (xData.has("root")) {
                        if (xData.getJSONObject("root").has("Element0")) {
                            r = new JSONObject(xData.getJSONObject("root").get("Element0").toString());
                        } else {
                            r = xData.getJSONObject("root");
                        }
                    }

                    if (r.length() == 0) {
                        //try json
                        r = new JSONObject(opt.getString("string"));
                    }
                }else{
                    r = new JSONObject( opt.getString("string") );
                }
                //System.out.println( xData.get("root") );
            }catch(Exception e){
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "FormatAPI Response Error:" + e.getMessage() )
                                .put("input", opt )
                                .put("function", "nwpCURL.formatResponse" )
                                .put("exception", false )
                                .put("fatal", false ) , null
                );
            }
        }

        return r;
    }

    public static JSONObject getEndpointsSettings( JSONObject opt ){
        JSONObject endpoints = new JSONObject(GlobalFunctions.fileGetContentsExternal("settings"+ GlobalFunctions.app_file_seperator +"endpoints.json"));
        JSONObject r = new JSONObject();

        if( opt.has("key") ){
            if( endpoints.has( opt.getString("key") ) ){
                r = endpoints.getJSONObject( opt.getString("key") );
            }
        }else{
            r = endpoints;
        }
        return r;
    }

    public static JSONObject callURL( String endpoint, String requestMethod, String contentType, String payLoad, JSONObject opt ) {
        JSONObject r = new JSONObject();
        try {
            URL url = new URL( endpoint );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( requestMethod );

            if( ! contentType.isEmpty() ) {
                connection.setRequestProperty("Content-Type", contentType);
            }

            if( opt.has("RequestProperty") && opt.getJSONObject("RequestProperty").length() > 0 ){
                for( Object op : opt.getJSONObject("RequestProperty").names() ){
                    String opk = op.toString();
                    if( opt.getJSONObject("RequestProperty").has( opk )
                            && ! opt.getJSONObject("RequestProperty").getString( opk ).isEmpty() ){
                        connection.setRequestProperty(opk, opt.getJSONObject("RequestProperty").getString( opk ) );
                    }
                }
            }

            connection.setDoOutput(true);
            if( ! payLoad.isEmpty() ) {
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(payLoad);
                outputStream.flush();
                outputStream.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            r.put("string", response.toString() );

            if( opt.has("return_json") && opt.getBoolean("return_json") ) {
                r.put( "json", formatResponse(
                        new JSONObject()
                                .put("endpoint", endpoint )
                                .put("string", response.toString() )
                ) );
            }

            if( ( GlobalFunctions.app_data.has("log_api_calls") && GlobalFunctions.app_data.getBoolean("log_api_calls") ) ){
                String apiKey = "API Log:";
                if( opt.has("api_key") ) {
                    apiKey = opt.getString("api_key") + " " + apiKey;
                }

                JSONObject gInput = new JSONObject()
                        .put("endpoint", endpoint)
                        .put("requestMethod", requestMethod)
                        .put("contentType", contentType)
                        .put("payLoad", payLoad)
                        .put("response", response.toString() );

                if( r.has("json") ) {
                    gInput.put("json_response", r.getJSONObject("json") );
                }

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", apiKey )
                                .put("input", gInput )
                                .put("function", "nwpCURL.callURL")
                                .put("non_fatal", true), null
                );

            }

            //System.out.println( response.toString() );

            connection.disconnect();
        } catch (Exception e) {
            //e.printStackTrace();
            r.put("error", "Error in CURL for: " + endpoint );

            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", r.get("error") )
                            .put("input", new JSONObject()
                                    .put("endpoint", endpoint)
                                    .put("requestMethod", requestMethod)
                                    .put("contentType", contentType)
                                    .put("payLoad", payLoad) )
                            .put("function", "nwpCURL.callURL" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }

        return r;
    }
}
