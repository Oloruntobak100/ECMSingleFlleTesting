package codes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;

//Imports for changing password
import javax.naming.ldap.StartTlsResponse;
import javax.naming.ldap.StartTlsRequest;
import javax.net.ssl.*;

//******************************************************************************
//**  ActiveDirectory
//*****************************************************************************/
/**
 *   Provides static methods to authenticate users, change passwords, etc.
 *
 ******************************************************************************/

public class ActiveDirectory {
    public static String adError = "";

    private static String[] userAttributes = {
            "distinguishedName","cn","name","uid",
            "sn","givenname","memberOf","samaccountname", "displayName", "mail", "department", "mobile", "telephoneNumber",
            "userPrincipalName"
    };

    private ActiveDirectory(){}

    //**************************************************************************
    //** getConnection
    //*************************************************************************/
    /**  Used to authenticate a user given a username/password and domain name.
     */
    /*public static LdapContext getConnectionOld(String username, String password, String domainName) throws NamingException {
        return getConnectionOld(username, password, domainName, null);
    }*/

    //**************************************************************************
    //** getConnection
    //*************************************************************************/
    /** Used to authenticate a user given a username/password and domain name.
     *  Provides an option to identify a specific a Active Directory server.
     */
    //static DirContext ldapContext;
    public static JSONObject getConnection(String username, String password, String domainName, String serverName) throws NamingException {
        DirContext ldapContext = null;
        ActiveDirectory.adError = "";
        JSONObject transformedUser = new JSONObject();
        JSONObject singleUser = new JSONObject();

        try{
            //System.out.println( ActiveDirectory.getADUserInfoConnection("Ozone@MAYBEACHTECH.local") );
            JSONObject adUser = ActiveDirectory.getADUserInfoConnection(username, password );

            if( adUser.has("username") && ! adUser.getString("username").isEmpty() && adUser.has("app_cn") && ! adUser.getString("app_cn").isEmpty() ) {

                JSONObject appData = GlobalFunctions.getApp();
                if (!(appData.has("active_directory_provider_url") && !appData.getString("active_directory_provider_url").isEmpty())) {
                    ActiveDirectory.adError = "Missing: active_directory_provider_url";
                } else if (!(appData.has("active_directory_initial_context_factory") && !appData.getString("active_directory_initial_context_factory").isEmpty())) {
                    ActiveDirectory.adError = "Missing: active_directory_initial_context_factory";
                } else if (!(appData.has("active_directory_security_authentication") && !appData.getString("active_directory_security_authentication").isEmpty())) {
                    ActiveDirectory.adError = "Missing: active_directory_security_authentication";
                } else if (!(appData.has("active_directory_search_base") && !appData.getString("active_directory_search_base").isEmpty())) {
                    ActiveDirectory.adError = "Missing: active_directory_search_base";
                } else{

                    Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
                    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, appData.getString("active_directory_initial_context_factory"));
                    //ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

                    //ldapEnv.put(Context.PROVIDER_URL,  "ldap://societe.fr:389");
                    //ldapEnv.put(Context.PROVIDER_URL,  "ldap://corp.maybeachtech.com");
                    ldapEnv.put(Context.PROVIDER_URL, appData.getString("active_directory_provider_url"));

                    //ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                    ldapEnv.put(Context.SECURITY_AUTHENTICATION, appData.getString("active_directory_security_authentication"));

                    ldapEnv.put(Context.SECURITY_PRINCIPAL, username );
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");

                    ldapEnv.put(Context.SECURITY_CREDENTIALS, password );
                    //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
                    //ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");
                    ldapContext = new InitialDirContext(ldapEnv);

                    // Create the search controls
                    SearchControls searchCtls = new SearchControls();

                    //Specify the attributes to return
                    String returnedAtts[] = {"sn", "givenName", "samAccountName", "UserPrincipalName"};
                    searchCtls.setReturningAttributes(returnedAtts);

                    //Specify the search scope
                    searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                    //specify the LDAP search filter
                    //String searchFilter = "(&(objectClass=user))";
                    String searchFilter = "(& (userPrincipalName="+username+")(objectClass=user))";

                    //Specify the Base for the search
                    //String searchBase = "dc=corp,dc=maybeachtech,dc=com";
                    String searchBase = appData.getString("active_directory_search_base");
                    //initialize counter to total the results
                    int totalResults = 0;

                    SearchControls controls = new SearchControls();
                    controls.setSearchScope(SUBTREE_SCOPE);
                    controls.setReturningAttributes(userAttributes);
                    NamingEnumeration<SearchResult> answer = ldapContext.search( searchBase, searchFilter, controls);
                    if (answer.hasMore()) {

                        Attributes attr = answer.next().getAttributes();

                        if( appData.has("log_active_directory_result") && appData.getBoolean("log_active_directory_result") ) {
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", "AD RESULT 2: " + attr.toString() )
                                            .put("input", new JSONObject().put("searchFilter", searchFilter).put("searchBase", searchBase).put("ldap", ldapEnv) )
                                            .put("function", "ActiveDirectory.getConnection" )
                                            .put("log", true )
                                            .put("non_fatal", true ) , null
                            );
                        }

                        Attribute user = attr.get("userPrincipalName");
                    /*System.out.println( attr.get("displayName") );
                    System.out.println( attr.get("sn") );
                    System.out.println( attr.get("mail") );
                    System.out.println( attr.get("telephoneNumber") );
                    System.out.println( attr.get("department") );
                    System.out.println(user);*/

                            singleUser = new JSONObject();
                            singleUser.put("userPrincipalName", attr.get("userPrincipalName"));
                            singleUser.put("displayName", attr.get("displayName"));
                            singleUser.put("mail", attr.get("mail"));
                            singleUser.put("telephoneNumber", attr.get("telephoneNumber"));
                            singleUser.put("department", attr.get("department"));

                    }
                    //System.out.println(singleUser);

                    if( singleUser.length() > 0 ){
                        JSONObject singleUser2 = new JSONObject( singleUser.toString() );

                        for( int i1 = 0; i1 < singleUser2.names().length(); i1++ ){
                            String key = singleUser2.names().getString( i1 );
                            String val = singleUser2.getString( key ).replaceAll(key + ": ", "");

                            switch( key ){
                                case "telephoneNumber":
                                    key = "phone_number";
                                    break;
                                case "mail":
                                    key = "email";
                                    break;
                                case "userPrincipalName":
                                    key = "username";
                                    break;
                                case "displayName":
                                    key = "name";
                                    break;
                            }
                            transformedUser.put( key, val );
                        }

                        if( transformedUser.has("username") ) {
                            transformedUser.put("id", GlobalFunctions.md5(transformedUser.getString("username")));
                            transformedUser.put("text", transformedUser.getString("name") + " ["+ transformedUser.getString("username") +"]" );
                        }else{
                            ActiveDirectory.adError = "Invalid user credentials or expired account";
                        }
                    }else{
                        ActiveDirectory.adError = "Invalid user credentials or expired account";
                    }
                }
            }else{
                if( ActiveDirectory.adError.isEmpty() ){
                    ActiveDirectory.adError = "Unable to retrieve app_cn or principalName : " + username;
                }
            }

        }
        catch(Exception e){
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "AD CONN error: " + e.getMessage() )
                            .put("input", new JSONObject().put("userName", username).put("domainName", domainName).put("serverName", serverName) )
                            .put("function", "ActiveDirectory.getConnection" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
           /* System.out.println("AD CONN error: " + e.getMessage() );
            e.printStackTrace();*/
            //System.exit(-1);
        }


        if( ! ActiveDirectory.adError.isEmpty() ){
            //System.out.println( ActiveDirectory.adError );
            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", ActiveDirectory.adError )
                            .put("input", new JSONObject().put("userName", username).put("domainName", domainName).put("serverName", serverName) )
                            .put("function", "ActiveDirectory.getConnection" )
                            .put("fatal", true ), null
            );
        }

        return transformedUser;
    }

    public static JSONArray getADUsers(String searchParameter ){

        JSONArray usersData = new JSONArray();
        java.util.ArrayList<User> users = new java.util.ArrayList<User>();
        JSONObject singleUser = new JSONObject();
        JSONObject transformedUser = new JSONObject();

        try{

            JSONObject appData = GlobalFunctions.getApp();
            if( ! ( appData.has("active_directory_search_base") && ! appData.getString("active_directory_search_base").isEmpty() ) ) {
                //System.out.println("Missing: active_directory_search_base");
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD ERROR 3: Missing active_directory_search_base" )
                                .put("input", new JSONObject().put("searchParameter", searchParameter) )
                                .put("function", "ActiveDirectory.getADUsers" )
                                .put("fatal", true ) , null
                );
            }else {
                DirContext ldapContext = ActiveDirectory.getADInfoConn( GlobalFunctions.currentUserName, GlobalFunctions.currentUserPwd );

                // Create the search controls
                SearchControls searchCtls = new SearchControls();

                //Specify the attributes to return
                /*String returnedAtts[] = {"sn", "givenName", "samAccountName", "UserPrincipalName"};
                searchCtls.setReturningAttributes(returnedAtts);*/

                //Specify the search scope
                searchCtls.setReturningAttributes(userAttributes);
                searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);


                //specify the LDAP search filter
                //String searchFilter = "(&(objectClass=user))";
                //String searchFilter = "(& (displayName=*"+ searchParameter +"*)(objectClass=user))";
                String searchFilter = "(& (userPrincipalName=*"+ searchParameter +"*)(objectClass=user))";

                //Specify the Base for the search
                //String searchBase = "dc=corp,dc=maybeachtech,dc=com";
                String searchBase = appData.getString("active_directory_search_base");
                //initialize counter to total the results
                int totalResults = 0;

                // Search for objects using the filter
                NamingEnumeration answer = ldapContext.search( searchBase, searchFilter, searchCtls);
                try{
                    while(answer.hasMore()) {
                        singleUser = new JSONObject();
                        Attributes attr = ((SearchResult) answer.next()).getAttributes();

                        if( appData.has("log_active_directory_result") && appData.getBoolean("log_active_directory_result") ) {
                            GlobalFunctions.nw_dev_handler(
                                    new JSONObject()
                                            .put("return", "AD RESULT: " + attr.toString() )
                                            .put("input", new JSONObject().put("searchParameter", searchParameter).put("attr", attr.toString() ) )
                                            .put("function", "ActiveDirectory.getADUsers" )
                                            .put("log", true )
                                            .put("non_fatal", true ) , null
                            );
                        }

                        Attribute user = attr.get("userPrincipalName");


                        if (user!=null){
                            singleUser.put("userPrincipalName", attr.get("userPrincipalName") );
                            singleUser.put("displayName", attr.get("displayName") );
                            singleUser.put("mail", attr.get("mail") );
                            singleUser.put("telephoneNumber", attr.get("telephoneNumber") );
                            singleUser.put("department", attr.get("department") );

                            if( GlobalFunctions.app_data.has("active_directory_fields_map")
                                    && GlobalFunctions.app_data.getJSONObject("active_directory_fields_map").length() > 0 ){
                                for(Object fm : GlobalFunctions.app_data.getJSONObject("active_directory_fields_map").names() ){
                                    String fmk = fm.toString();
                                    try{
                                        singleUser.put(
                                                GlobalFunctions.app_data.getJSONObject("active_directory_fields_map").getString( fmk ),
                                                attr.get( fmk ).toString().toLowerCase().replaceAll(fmk.toLowerCase() + ": ", "") );
                                    }catch (Exception e){

                                    }
                                }
                            }

                            usersData.put( singleUser.toString() );
                            //users.add(new User(attr));
                        }
                    }


                }catch(Exception e){
                   /* GlobalFunctions.nw_dev_handler(
                            new JSONObject()
                                    .put("return", "AD CONN ERROR 2: " + e.getMessage() )
                                    .put("input", new JSONObject().put("searchParameter", searchParameter) )
                                    .put("function", "ActiveDirectory.getADUsers" )
                                    .put("exception", true )
                                    .put("fatal", true ) , e
                    );*/
                }

                //System.out.println("Total results: " + totalResults);
                ldapContext.close();
            }
        }
        catch(Exception e){
            if( ActiveDirectory.adError.isEmpty() ){
                ActiveDirectory.adError = "AD CONN ERROR 1: " + e.getMessage();
            }

            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "AD CONN ERROR 1: " + e.getMessage() )
                            .put("input", new JSONObject().put("searchParameter", searchParameter) )
                            .put("function", "ActiveDirectory.getADUsers" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
            //System.exit(-1);
        }

        //return ldapContext;
        JSONArray usersDataTransformed = new JSONArray();
        if( usersData.length() > 0 ) {
            for( int i2 = 0; i2 < usersData.length(); i2++ ){
                JSONObject singleUserTransformed = new JSONObject( usersData.getString( i2 ) );
                singleUser = new JSONObject();
                //System.out.println(singleUserTransformed);

                for( int i1 = 0; i1 < singleUserTransformed.names().length(); i1++ ){
                    String key = singleUserTransformed.names().getString( i1 );
                    String val = singleUserTransformed.getString( key ).toLowerCase();
                    val = val.replaceAll(key.toLowerCase() + ": ", "");

                    switch( key.toLowerCase() ){
                        case "telephonenumber":
                            key = "phone_number";
                            break;
                        case "mail":
                            key = "email";
                            break;
                        case "userprincipalname":
                            key = "username";
                            break;
                        case "displayname":
                            key = "name";
                            break;
                    }
                    singleUser.put( key, val );
                }

                if( singleUser.has("username") ) {
                    singleUser.put("id", GlobalFunctions.md5(singleUser.getString("username")));
                    singleUser.put("text", singleUser.getString("name") + " ["+ singleUser.getString("username") +"]" );
                    usersDataTransformed.put(singleUser);
                }
            }
            //System.out.println(usersDataTransformed);
        }

        return usersDataTransformed;
    }

    private static JSONObject getADUserInfoConnection( String principalName, String principalPwd ){
        JSONObject transformedUser = new JSONObject();
        JSONObject singleUser = new JSONObject();

        try{

            JSONObject appData = GlobalFunctions.getApp();
            if( ! ( appData.has("active_directory_search_base") && ! appData.getString("active_directory_search_base").isEmpty() ) ) {
                System.out.println("Missing: active_directory_search_base");
            }else {
                DirContext ldapContext = ActiveDirectory.getADInfoConn( principalName, principalPwd );

                // Create the search controls
                SearchControls searchCtls = new SearchControls();

                //Specify the attributes to return
                //String returnedAtts[] = {"sn", "givenName", "samAccountName", "userPrincipalName"};
                //searchCtls.setReturningAttributes(returnedAtts);

                //Specify the search scope
                searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                //specify the LDAP search filter
                //String searchFilter = "(&(objectClass=user))";
                String searchFilter = "(& (userPrincipalName=" + principalName + ")(objectClass=user))";

                //Specify the Base for the search
                //String searchBase = "dc=corp,dc=maybeachtech,dc=com";
                String searchBase = appData.getString("active_directory_search_base");
                //initialize counter to total the results
                int totalResults = 0;

                // Search for objects using the filter
                NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, searchFilter, searchCtls);

                //Loop through the search results
                while (answer.hasMoreElements()) {
                    SearchResult sr = (SearchResult) answer.next();

                    totalResults++;

                    Attributes attr = sr.getAttributes();
                    /*System.out.println(">>>" + sr.getName());
                    System.out.println(">>>>>>" + attr.get("samAccountName"));
                    System.out.println(">>>" + attr.get("UserPrincipalName"));*/

                    singleUser = new JSONObject();
                    singleUser.put("app_cn", sr.getName());
                    singleUser.put("samAccountName", attr.get("samAccountName"));
                    singleUser.put("userPrincipalName", attr.get("userPrincipalName"));


                }

                //System.out.println("Total results: " + totalResults);
                ldapContext.close();
            }
        }
        catch(Exception e){
            /*System.out.println("AD CONN error: " + e.getMessage() );
            e.printStackTrace();*/
            if( ActiveDirectory.adError.isEmpty() ){
                ActiveDirectory.adError = "AD CONN error 20: " + e.getMessage();
            }

            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "AD CONN error 20: " + e.getMessage() )
                            .put("input", new JSONObject().put("principalName", principalName) )
                            .put("function", "ActiveDirectory.getADUserInfoConnection" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
            //System.exit(-1);
        }
        //return ldapContext;

        if( singleUser.length() > 0 ){
            JSONObject singleUser2 = new JSONObject( singleUser.toString() );

            for( int i1 = 0; i1 < singleUser2.names().length(); i1++ ){
                String key = singleUser2.names().getString( i1 );
                String val = singleUser2.getString( key ).toLowerCase();
                val = val.replaceAll(key.toLowerCase() + ": ", "");

                switch( key.toLowerCase() ){
                    case "telephonenumber":
                        key = "phone_number";
                        break;
                    case "mail":
                        key = "email";
                        break;
                    case "userprincipalname":
                        key = "username";
                        break;
                    case "displayname":
                        key = "name";
                        break;
                }
                transformedUser.put( key, val );
            }

        }


        return transformedUser;
    }

    private static DirContext getADInfoConn( String principalName, String principalPwd ){
        DirContext ldapContext = null;

        try{
            JSONObject appData = GlobalFunctions.getApp();
            if( ! ( appData.has("active_directory_sadmin_principal") && ! appData.getString("active_directory_sadmin_principal").isEmpty() ) ) {

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_sadmin_principal" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else if( ! ( appData.has("active_directory_provider_url") && ! appData.getString("active_directory_provider_url").isEmpty() ) ) {
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_provider_url" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else if( ! ( appData.has("active_directory_initial_context_factory") && ! appData.getString("active_directory_initial_context_factory").isEmpty() ) ) {

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_initial_context_factory" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else if( ! ( appData.has("active_directory_security_authentication") && ! appData.getString("active_directory_security_authentication").isEmpty() ) ) {

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_security_authentication" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else if( ! ( appData.has("active_directory_security_credentials") && ! appData.getString("active_directory_security_credentials").isEmpty() ) ) {

                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_security_credentials" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else if( ! ( appData.has("active_directory_search_base") && ! appData.getString("active_directory_search_base").isEmpty() ) ) {
                GlobalFunctions.nw_dev_handler(
                        new JSONObject()
                                .put("return", "AD CONN error: " + "Missing: active_directory_search_base" )
                                .put("input", new JSONObject() )
                                .put("function", "ActiveDirectory.getADInfoConn" )
                                .put("fatal", true ) , null
                );
            }else{

                Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
                ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, appData.getString("active_directory_initial_context_factory") );
                //ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

                //ldapEnv.put(Context.PROVIDER_URL,  "ldap://societe.fr:389");
                //ldapEnv.put(Context.PROVIDER_URL,  "ldap://corp.maybeachtech.com");
                ldapEnv.put(Context.PROVIDER_URL,  appData.getString("active_directory_provider_url") );

                //ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                ldapEnv.put(Context.SECURITY_AUTHENTICATION, appData.getString("active_directory_security_authentication") );
                //ldapEnv.put(Context.SECURITY_PROTOCOL, appData.getString("active_directory_security_authentication") );

                ldapEnv.put(Context.SECURITY_PRINCIPAL, appData.getString("active_directory_sadmin_principal") );
                //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");

                ldapEnv.put(Context.SECURITY_CREDENTIALS, appData.getString("active_directory_security_credentials") );
                //ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
                //ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");

                if( GlobalFunctions.app_data.has("cbn_ad_conn3") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn3") ){
                    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    ldapEnv.put(Context.PROVIDER_URL,  "ldap://172.24.90.70");
                    ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                    ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=FIAPS SERVICE ACCOUNT,ou=services,dc=CENBANK,dc=NET");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CENBANK.NET");

                    if( GlobalFunctions.app_data.has("cbn_ad_conn_pwd") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn_pwd") ){
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, appData.getString("active_directory_security_credentials") );
                    }else {
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, "Pap3rL1te@cbn");
                    }
                }else if( GlobalFunctions.app_data.has("cbn_ad_conn2") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn2") ){
                    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    ldapEnv.put(Context.PROVIDER_URL,  "ldap://172.24.90.152");
                    ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPS SERVICE ACCOUNT" + "@" + "CENBANK.NET");
                    ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CENBANK.NET");
                    ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CBN.GOV.NG");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CENBANK.NET");
                    if( GlobalFunctions.app_data.has("cbn_ad_conn_pwd") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn_pwd") ){
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, appData.getString("active_directory_security_credentials") );
                    }else {
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, "Pap3rL1te@cbn");
                    }
                }else if( GlobalFunctions.app_data.has("cbn_ad_conn") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn") ){
                    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    ldapEnv.put(Context.PROVIDER_URL,  "ldap://ABJHP580DCT03.cenbank.net");
                    ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
                    ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=FIAPS SERVICE ACCOUNT,ou=services,dc=CENBANK,dc=NET");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CENBANK.NET");
                    if( GlobalFunctions.app_data.has("cbn_ad_conn_pwd") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn_pwd") ){
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, appData.getString("active_directory_security_credentials") );
                    }else {
                        ldapEnv.put(Context.SECURITY_CREDENTIALS, "Pap3rL1te@cbn");
                    }
                }

                if( GlobalFunctions.app_data.has("cbn_ad_conn_user") && GlobalFunctions.app_data.getBoolean("cbn_ad_conn_user") ){
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn="+ principalName  + GlobalFunctions.app_data.getString("active_directory_cn_dc") );

                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CBN.GOV.NG");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "FIAPSUSRSPSV" + "@" + "CENBANK.NET");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "OLAKAREEM25015" + "@" + "CENBANK.NET");
                    //ldapEnv.put(Context.SECURITY_PRINCIPAL, "OLAKAREEM25015" + "@" + "CBN.GOV.NG");
                    if( GlobalFunctions.app_data.has("active_directory_cn_dc") && ! GlobalFunctions.app_data.getString("active_directory_cn_dc").isEmpty() ){
                        ldapEnv.put(Context.SECURITY_PRINCIPAL, principalName + GlobalFunctions.app_data.getString("active_directory_cn_dc") );
                    }else{
                        ldapEnv.put(Context.SECURITY_PRINCIPAL, principalName );
                    }
                    ldapEnv.put(Context.SECURITY_CREDENTIALS, principalPwd );
                }

                ldapContext = new InitialDirContext(ldapEnv);
            }

        }
        catch(Exception e){
            // System.out.println("AD CONN error: " + e.getMessage() );
            //e.printStackTrace();
            //System.exit(-1);
            ActiveDirectory.adError = "AD CONN error 21: " + e.getMessage();

            GlobalFunctions.nw_dev_handler(
                    new JSONObject()
                            .put("return", "AD CONN error 21: " + e.getMessage() )
                            .put("input", new JSONObject() )
                            .put("function", "ActiveDirectory.getADInfoConn" )
                            .put("exception", true )
                            .put("fatal", true ) , e
            );
        }
        return ldapContext;
    }

    public static LdapContext getConnectionOld(String username, String password, String domainName, String serverName) throws NamingException {

        if (domainName==null){
            try{
                String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
                if (fqdn.split("\\.").length>1) domainName = fqdn.substring(fqdn.indexOf(".")+1);
            }
            catch(java.net.UnknownHostException e){}
        }

        System.out.println("Authenticating " + username + "@" + domainName + " through " + serverName);

        if (password!=null){
            password = password.trim();
            if (password.length()==0) password = null;
        }

        //bind by using the specified username/password
        Hashtable props = new Hashtable();
        String principalName = username + "@" + domainName;
        //String principalName = "cn=Administrator,ou=users,dc=corp,dc=maybeachtech, dc=com";

        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password!=null) props.put(Context.SECURITY_CREDENTIALS, password);


        String ldapURL = "ldap://" + ((serverName==null)? domainName : serverName + "." + domainName) + '/';
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        try{
            return new InitialLdapContext(props, null);
        }
        catch(javax.naming.CommunicationException e){
            throw new NamingException("Failed to connect to " + domainName + ((serverName==null)? "" : " through " + serverName));
        }
        catch(NamingException e){
            throw new NamingException("Failed to authenticate " + username + "@" + domainName + ((serverName==null)? "" : " through " + serverName));
        }
    }


    //**************************************************************************
    //** getUser
    //*************************************************************************/
    /** Used to check whether a username is valid.
     *  @param username A username to validate (e.g. "peter", "peter@acme.com",
     *  or "ACME\peter").
     */
    public static JSONObject getUser(String username, LdapContext context) {
        JSONObject transformedUser = new JSONObject();
        JSONObject singleUser = new JSONObject();
        try{

            String domainName = null;
            if (username.contains("@")){
                username = username.substring(0, username.indexOf("@"));
                domainName = username.substring(username.indexOf("@")+1);
            }
            else if(username.contains("\\")){
                username = username.substring(0, username.indexOf("\\"));
                domainName = username.substring(username.indexOf("\\")+1);
            }
            else{
                String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
                if (authenticatedUser.contains("@")){
                    domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
                }
            }

            if (domainName!=null){
                String principalName = username + "@" + domainName;
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SUBTREE_SCOPE);
                controls.setReturningAttributes(userAttributes);
                NamingEnumeration<SearchResult> answer = context.search( toDC(domainName), "(& (userPrincipalName="+principalName+")(objectClass=user))", controls);
                if (answer.hasMore()) {

                    Attributes attr = answer.next().getAttributes();
                    Attribute user = attr.get("userPrincipalName");
                    /*System.out.println( attr.get("displayName") );
                    System.out.println( attr.get("sn") );
                    System.out.println( attr.get("mail") );
                    System.out.println( attr.get("telephoneNumber") );
                    System.out.println( attr.get("department") );
                    System.out.println(user);*/

                    if (user!=null) {
                        singleUser = new JSONObject();
                        singleUser.put("userPrincipalName", attr.get("userPrincipalName"));
                        singleUser.put("displayName", attr.get("displayName"));
                        singleUser.put("mail", attr.get("mail"));
                        singleUser.put("telephoneNumber", attr.get("telephoneNumber"));
                        singleUser.put("department", attr.get("department"));
                    }
                    //return new User(attr);

                }
            }
        }
        catch(NamingException e){
            //e.printStackTrace();
        }

        if( singleUser.length() > 0 ){
            JSONObject singleUser2 = new JSONObject( singleUser.toString() );

            for( int i1 = 0; i1 < singleUser2.names().length(); i1++ ){
                String key = singleUser2.names().getString( i1 );
                String val = singleUser2.getString( key ).replaceAll(key + ": ", "");

                switch( key ){
                    case "telephoneNumber":
                        key = "phone_number";
                        break;
                    case "mail":
                        key = "email";
                        break;
                    case "userPrincipalName":
                        key = "username";
                        break;
                    case "displayName":
                        key = "name";
                        break;
                }
                transformedUser.put( key, val );
            }

            if( transformedUser.has("username") ) {
                transformedUser.put("id", GlobalFunctions.md5(transformedUser.getString("username")));
                transformedUser.put("text", transformedUser.getString("name") + " ["+ transformedUser.getString("username") +"]" );
            }
        }


        return transformedUser;
    }


    //**************************************************************************
    //** getUsers
    //*************************************************************************/
    /** Returns a list of users in the domain.
     */
    public static JSONArray getUsers(LdapContext context, String searchParameter ) throws NamingException {

        JSONArray usersData = new JSONArray();
        java.util.ArrayList<User> users = new java.util.ArrayList<User>();
        String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
        JSONObject singleUser;

        if (authenticatedUser.contains("@")){
            String domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
           //NamingEnumeration answer = context.search( toDC(domainName), "(objectClass=user)", controls);
            NamingEnumeration answer = context.search( toDC(domainName), "(& (displayName=*"+ searchParameter +"*)(objectClass=user))", controls);
            try{
                while(answer.hasMore()) {
                    singleUser = new JSONObject();
                    Attributes attr = ((SearchResult) answer.next()).getAttributes();
                    Attribute user = attr.get("userPrincipalName");


                    if (user!=null){
                        singleUser.put("userPrincipalName", attr.get("userPrincipalName") );
                        singleUser.put("displayName", attr.get("displayName") );
                        singleUser.put("mail", attr.get("mail") );
                        singleUser.put("telephoneNumber", attr.get("telephoneNumber") );
                        singleUser.put("department", attr.get("department") );
                        usersData.put( singleUser.toString() );
                        //users.add(new User(attr));
                    }
                }
            }
            catch(Exception e){}
        }

        JSONArray usersDataTransformed = new JSONArray();
        if( usersData.length() > 0 ) {
            for( int i2 = 0; i2 < usersData.length(); i2++ ){
                JSONObject singleUserTransformed = new JSONObject( usersData.getString( i2 ) );
                singleUser = new JSONObject();

                for( int i1 = 0; i1 < singleUserTransformed.names().length(); i1++ ){
                    String key = singleUserTransformed.names().getString( i1 );
                    String val = singleUserTransformed.getString( key ).replaceAll(key + ": ", "");

                    switch( key ){
                        case "telephoneNumber":
                            key = "phone_number";
                            break;
                        case "mail":
                            key = "email";
                            break;
                        case "userPrincipalName":
                            key = "username";
                            break;
                        case "displayName":
                            key = "name";
                            break;
                    }
                    singleUser.put( key, val );
                }

                if( singleUser.has("username") ) {
                    singleUser.put("id", GlobalFunctions.md5(singleUser.getString("username")));
                    singleUser.put("text", singleUser.getString("name") + " ["+ singleUser.getString("username") +"]" );
                    usersDataTransformed.put(singleUser);
                }
            }
            //System.out.println(usersDataTransformed);
        }
        return usersDataTransformed;
        //System.out.println( users.toArray(new User[users.size()]) );
        //return users.toArray(new User[users.size()]);
    }


    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if(token.length()==0)   continue;   // defensive check
            if(buf.length()>0)  buf.append(",");
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }


    //**************************************************************************
    //** User Class
    //*************************************************************************/
    /** Used to represent a User in Active Directory
     */
    public static class User {
        private String distinguishedName;
        private String userPrincipal;
        private String commonName;
        public User(Attributes attr) throws javax.naming.NamingException {
            userPrincipal = (String) attr.get("userPrincipalName").get();
            commonName = (String) attr.get("cn").get();
            distinguishedName = (String) attr.get("distinguishedName").get();

        }

        public String getUserPrincipal(){
            return userPrincipal;
        }

        public String getCommonName(){
            return commonName;
        }

        public String getDistinguishedName(){
            return distinguishedName;
        }

        public String toString(){
            return getDistinguishedName();
        }

        /** Used to change the user password. Throws an IOException if the Domain
         *  Controller is not LDAPS enabled.
         *  @param trustAllCerts If true, bypasses all certificate and host name
         *  validation. If false, ensure that the LDAPS certificate has been
         *  imported into a trust store and sourced before calling this method.
         *  Example:
        String keystore = "/usr/java/jdk1.5.0_01/jre/lib/security/cacerts";
        System.setProperty("javax.net.ssl.trustStore",keystore);
         */
        public void changePassword(String oldPass, String newPass, boolean trustAllCerts, LdapContext context)
                throws java.io.IOException, NamingException {
            String dn = getDistinguishedName();


            //Switch to SSL/TLS
            StartTlsResponse tls = null;
            try{
                tls = (StartTlsResponse) context.extendedOperation(new StartTlsRequest());
            }
            catch(Exception e){
                //"Problem creating object: javax.naming.ServiceUnavailableException: [LDAP: error code 52 - 00000000: LdapErr: DSID-0C090E09, comment: Error initializing SSL/TLS, data 0, v1db0"
                throw new java.io.IOException("Failed to establish SSL connection to the Domain Controller. Is LDAPS enabled?");
            }


            //Exchange certificates
            if (trustAllCerts){
                tls.setHostnameVerifier(DO_NOT_VERIFY);
                SSLSocketFactory sf = null;
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, TRUST_ALL_CERTS, null);
                    sf = sc.getSocketFactory();
                }
                catch(java.security.NoSuchAlgorithmException e) {}
                catch(java.security.KeyManagementException e) {}
                tls.negotiate(sf);
            }
            else{
                tls.negotiate();
            }


            //Change password
            try {
                //ModificationItem[] modificationItems = new ModificationItem[1];
                //modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)));

                ModificationItem[] modificationItems = new ModificationItem[2];
                modificationItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(oldPass)) );
                modificationItems[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)) );
                context.modifyAttributes(dn, modificationItems);
            }
            catch(javax.naming.directory.InvalidAttributeValueException e){
                String error = e.getMessage().trim();
                if (error.startsWith("[") && error.endsWith("]")){
                    error = error.substring(1, error.length()-1);
                }
                System.err.println(error);
                //e.printStackTrace();
                tls.close();
                throw new NamingException(
                        "New password does not meet Active Directory requirements. " +
                                "Please ensure that the new password meets password complexity, " +
                                "length, minimum password age, and password history requirements."
                );
            }
            catch(NamingException e) {
                tls.close();
                throw e;
            }

            //Close the TLS/SSL session
            tls.close();
        }

        private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        private static TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };


        private byte[] getPassword(String newPass){
            String quotedPassword = "\"" + newPass + "\"";
            //return quotedPassword.getBytes("UTF-16LE");
            char unicodePwd[] = quotedPassword.toCharArray();
            byte pwdArray[] = new byte[unicodePwd.length * 2];
            for (int i=0; i<unicodePwd.length; i++) {
                pwdArray[i*2 + 1] = (byte) (unicodePwd[i] >>> 8);
                pwdArray[i*2 + 0] = (byte) (unicodePwd[i] & 0xff);
            }
            return pwdArray;
        }
    }
}