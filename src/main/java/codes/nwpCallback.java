package codes;

import org.json.JSONObject;

public class nwpCallback {

    public JSONObject ecm_cash_advance_workflow_callback(JSONObject d){
       return nwpCashAdvance.workflowCallback( d );
    }

}
