package codes;

import org.json.JSONArray;
import org.json.JSONObject;

public class nwpTest {

    public static void main(String[] args) {
        GlobalFunctions.initialize();
        //testZonesFromLocations();

        //testTripQuery();

       //long ageThresholdMillis = 100 * 24 * 60 * 60 * 1000; // 7 days in milliseconds
        //System.out.println("ageThresholdMillis:" + ageThresholdMillis);
       // GlobalFunctions.deleteOldFiles( "C:\\xampp\\tomcat\\temp\\paperlite", ageThresholdMillis);
    }

    private static void testTripQuery(){
        String id = "1eip1693315187279";
        String table = nwpCashAdvance.table_name_trip;

        JSONObject $qs = new JSONObject();
        JSONObject $r = new JSONObject();
        $qs.put("table", table );

        $r = GlobalFunctions.get_records($qs);
        System.out.println( "CASH ADVANCE TRIP RECORDS" );
        System.out.println( $r );
        System.out.println( "CASH ADVANCE TRIP RECORDS" );

        System.out.println( "-------" );
        System.out.println( "-------" );
        System.out.println( "VIEW DETAILS" );

        System.out.println( GlobalFunctions.view_details(new JSONObject()
                        .put("id", id )
                        .put("table", table )
                , new JSONObject().put("buttons","") ) );
        System.out.println( "-------" );
    }

    private static void testZonesFromLocations(){
        //get zones from locations
        System.out.println( "CASH ADVANCE getCashAdvanceZoneData" );
        System.out.println( GlobalFunctions.getApp() );
        System.out.println( nwpCashAdvance.getCashAdvanceZoneData(
                new JSONObject()
                        .put("recordIDs",
                                new JSONArray()
                                        .put( "akure-ondo" )
                                        .put( "abuja-fct" )
                        ))
        );

    }

}
