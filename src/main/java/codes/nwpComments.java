package codes;

import org.json.JSONArray;
import org.json.JSONObject;

public class nwpComments {
    public static String table_name = "comments";
    public static String label = "Comments";

    public static JSONObject get_comments( JSONObject a ){
        JSONObject r = new JSONObject();
        JSONArray $parent = new JSONArray();
        JSONArray $children = new JSONArray();

        JSONObject $qs = new JSONObject();
        $qs.put( "table", nwpComments.table_name );
        JSONObject dep = GlobalFunctions.get_json( nwpComments.table_name );
        String $where = "";
        JSONObject $qr;
        Integer $limit = 10;

        if( a.has("id") && ! a.getString("id").isEmpty() ) {

            if( a.has("table") && ! a.getString("table").isEmpty() && ! a.getString("table").equals( nwpComments.table_name ) ){
                if( dep.has("fields") && dep.getJSONObject("fields").has("reference") ) {
                    $qs = new JSONObject().put( "table", nwpComments.table_name );
                    $where = " AND ["+nwpComments.table_name+"].["+ dep.getJSONObject("fields").getString("reference_table") +"] = '"+ a.getString( "table" ) +"' ";
                    $where += " AND ["+nwpComments.table_name+"].["+ dep.getJSONObject("fields").getString("reference") +"] = '"+ a.getString( "id" ) +"' AND ["+nwpComments.table_name+"].["+ dep.getJSONObject("fields").getString("type") +"] = 'parent' ";

                    $qs.put( "limit", String.valueOf($limit) );
                    $qs.put( "where", $where );
                    $qr = GlobalFunctions.get_records( $qs );
                    if( $qr.has("row") ){
                        $parent = $qr.getJSONArray("row");
                    }
                }

            }else{
                $qs.put( "id", a.getString( "id" ) );
                $parent.put( GlobalFunctions.get_record( $qs ) );
            }

        }else if( a.has("reference") && a.has("reference_table") && ! a.getString("reference").isEmpty() ){

            if( dep.has("fields") && dep.getJSONObject("fields").has("reference") ) {
                $where = " AND ["+nwpComments.table_name+"].["+ dep.getJSONObject("fields").getString("reference") +"] = '"+ a.getString("reference") +"' AND ["+nwpComments.table_name+"].["+ dep.getJSONObject("fields").getString("reference_table") +"] = '"+ a.getString("reference_table") +"' ";

                $qs.put( "where", $where );
                $qr = GlobalFunctions.get_records( $qs );
                if( $qr.has("row") ){
                    $parent = $qr.getJSONArray("row");
                }
            }
        }


        //get children comments
        if( $parent.length() > 0 ) {
            try {
                if (dep.has("fields") && dep.getJSONObject("fields").has("reference")) {
                    String parentIDs = "";
                    for (int i = 0; i < $parent.length(); i++) {
                        if ($parent.getJSONObject(i).has("id") ) {
                            if( ! parentIDs.isEmpty() ){
                                parentIDs += ", ";
                            }
                            parentIDs += "'"+ $parent.getJSONObject(i).getString("id") +"'";
                        }
                    }

                    $qs = new JSONObject().put("table", nwpComments.table_name);
                    $where = " AND [" + nwpComments.table_name + "].[" + dep.getJSONObject("fields").getString("reference") + "] IN ( " + parentIDs + " ) AND [" + nwpComments.table_name + "].[" + dep.getJSONObject("fields").getString("type") + "] = 'child' AND [" + nwpComments.table_name + "].[" + dep.getJSONObject("fields").getString("reference_table") + "] = '" + nwpComments.table_name + "' ";

                    $qs.put("limit", String.valueOf($limit * 20));
                    $qs.put("where", $where);
                    $qr = GlobalFunctions.get_records($qs);
                    if ($qr.has("row")) {
                        $children = $qr.getJSONArray("row");
                    }
                }
            }catch (Exception e){
                System.out.println( e.getMessage() );
            }
        }

        //temp hack to: prevent view more from showing:
        //$limit += 10;

        r.put("limit", $limit );
        r.put("parent", $parent );
        r.put("children", $children );

        return r;
    }

    public static String getTableName(){
        return nwpComments.table_name;
    }
}
