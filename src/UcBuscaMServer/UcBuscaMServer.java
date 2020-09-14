package UcBuscaMServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import shared.Multicast;
import shared.Url;


public class UcBuscaMServer extends Thread {
    private static String MULTICAST_ADDRESS = "224.1.2.3";
    private static int PORT = 5000;
    private static HashMap<String, User> users = new HashMap<String, User>();
    private static HashMap<Integer, String> logged_users = new HashMap<Integer, String>();
    private static Crawler crawler = null;
    private static String serverName;
    
    public static void main(String[] args) {
        
        if(args.length!=2){
            System.out.println("You should provide exactly two arguments. MULTICAST_ADDRESS, PORT");
            System.out.println("Example: UcBuscaMServer 224.1.2.3 5000");
        }
        else{
            MULTICAST_ADDRESS = args[0];
            PORT = Integer.parseInt(args[1]);
            UcBuscaMServer server = new UcBuscaMServer();
            server.start();
        }
    }

    public UcBuscaMServer() {
        super("Server " + (long) (Math.random() * 1000));
        System.out.println(this.getClass().getCanonicalName());
    }

    public String prepend(String msg, String serverName, String requestId){
        return "servername | " + serverName + " ; requestid | " + requestId + " ; " + msg;
    }
    public void run() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
        serverName = Integer.toString(randomNum);
        crawler = new Crawler(MULTICAST_ADDRESS, PORT, serverName);
        crawler.start();
        Multicast multicast = new Multicast(MULTICAST_ADDRESS, PORT);
        Message message = new Message();
        String username, pass, reply;
        int user_id;
        String requestId;
        System.out.println("Server: " + serverName + " running...");

        while (true) {
            String msg = multicast.receiveMessage();
            message.fromString(msg);

            String type = message.values.get("type");
            switch (type) {
                case "login":
                    username = message.values.get("username");
                    pass = message.values.get("password");
                    if (users.containsKey(username)) {
                        User user = users.get(username);
                        if (user.getPassword().equals(pass)) {
                            int login_id = Integer.parseInt(message.values.get("user_id"));
                            logged_users.put(login_id, username);
                            users.get(username).setLogged(true);
                            reply = "type | reply_login ; sucess | true ;  admin | " + user.isAdmin() +" ; msg | Welcome to ucBusca! You are logged as user " + username;
                        } 
                        else {
                            reply = "type | reply_login ; sucess | false ; msg | Your password is incorrect. Access denied.";
                        }
                    } 
                    else {
                        reply = "type | reply_login ; sucess | false ; msg | Your username is incorrect or doesn't exist. Access denied.";
                    }
                    reply = prepend(reply, serverName, message.values.get("requestid"));
                    multicast.sendMessage(reply);
                    break;

                case "register":
                    username = message.values.get("username");
                    pass = message.values.get("password");
                    Boolean admin = false;
                    if(users.isEmpty()){
                        admin = true;
                    }
                    if (!users.containsKey(username)) {
                        User user = new User(pass, admin);
                        users.put(username, user);
                        if(users.get(username).isAdmin()){
                            users.get(username).setNotification(true);
                        }
                        reply = "type | reply_register ; sucess | true ; msg | You are registered as user " + username;
                    } 
                    else {
                        reply = "type | reply_register ; sucess | false ; msg | You are already registered.";
                    }
                    reply = prepend(reply, serverName, message.values.get("requestid"));
                    multicast.sendMessage(reply);
                    break;

                case "index":
                    user_id = Integer.parseInt(message.values.get("user_id"));
                    String website = message.values.get("url"); 
                    int load = crawler.numberUrlsToVisit();
                    if(logged_users.containsKey(user_id)){
                        username = logged_users.get(user_id);
                        if(users.get(username).isAdmin()){ 				//if user has administrator privileges
                            if(crawler.urlAlreadyVisited(website)){
                                reply = "type | reply_index ; sucess | already_indexed ; msg | Url " + website + " is already indexed.";
                            }
                            else{
                                reply = "type | reply_index ; sucess | true";
                            }
                        }
                        else{
                            reply = "type | reply_index ; sucess | false ; msg | You don't have administrator permissions to index an url.";
                        }
                    }
                    else{
                        reply = "type | reply_index ; sucess | false ; msg | You need to be logged and have administrator permissions to index an url.";
                    }
                    reply = prepend(reply, serverName, message.values.get("requestid"));
                    reply = reply + "; load | " + load;
                    multicast.sendMessage(reply);
                    break;
                  

                case "index_request":
                    if(message.values.get("target").equals(serverName)){
                        website = message.values.get("url"); 
                        int index_code = crawler.addUrlToVisit(website, 1);
                        if(index_code == 0){
                            reply = "type | reply_indexrequest ; sucess | true ; msg | Url " + website + " indexed.";
                            reply = prepend(reply, serverName, message.values.get("requestid"));
                        }
                        else{
                            reply = "type | reply_indexrequest ; sucess | false ; msg | An error ocurred, url not indexed.";
                            reply = prepend(reply, serverName, message.values.get("requestid"));
                        }
                        multicast.sendMessage(reply);
                    }
                    break;

                case "search":
                    requestId = message.values.get("requestid");
                    ArrayList<String> words = message.getList(); 		//create list of words received from client
                    if(!message.values.get("user_id").equals("null")){
                        String id = message.values.get("user_id");
                        user_id = Integer.parseInt(id);
                        username = logged_users.get(user_id);
                        if(username != null){
                            users.get(username).setSearch(words);
                        }
                    }
                    HashSet<String> urls = crawler.getUrls(words); 		//get urls of searched word from index
                    ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
                    if(!urls.isEmpty()){
                        System.out.println("URLS : " + urls.size());
                        for(String url: urls){
                            System.out.println(url);
                            HashMap<String, String> result = new HashMap<String, String>();
                            String title = crawler.getUrlTitle(url);
                            int connections = crawler.getNumberConnectionsToUrl(url); 
                            String citation = crawler.getUrlCitation(url);
                            result.put("url",url);
                            result.put("title",title);
                            result.put("citation", citation);
                            result.put("connections", Integer.toString(connections));
                            results.add(result);
                        }
                        Message reply_list = new ReplySearch("reply_search", serverName, requestId, "true", results);
                        reply = reply_list.toString();
                    }
                    else{
                        reply = "type | reply_search ; sucess | false ; msg | Your search return no results."; 
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                    }
                    multicast.sendMessage(reply);
                    break;


                case "listsearches":
                    requestId = message.values.get("requestid");
                    if(!message.values.get("user_id").equals("null")){
                        user_id = Integer.parseInt(message.values.get("user_id"));
                        if(logged_users.containsKey(user_id)){
                            username = logged_users.get(user_id);
                            ArrayList<String> searches = users.get(username).getSearches();
                            if(searches.isEmpty()){
                                reply = "type | reply_listsearches ; sucess | false ; msg | Your consult return no results.";
                                reply = prepend(reply, serverName, message.values.get("requestid"));
                            }
                            else{
                                Message reply_consult = new ReplyList("reply_listsearches", serverName, requestId, "true", searches);
                                reply = reply_consult.toString();
                            }
                        }
                        else{
                            reply = "type | reply_listsearches ; sucess | false ; msg | Your consult return no results.";
                            reply = prepend(reply, serverName, message.values.get("requestid"));
                        }
                    }
                    else{
                        reply = "type | reply_listsearches ; sucess | false ; msg | Your consult return no results.";
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                    }
                    multicast.sendMessage(reply);
                    break;
                
                case "connections":
                    requestId = message.values.get("requestid");
                    website = message.values.get("url"); 
                    user_id = Integer.parseInt(message.values.get("user_id"));
                    if(logged_users.containsKey(user_id)){
                        username = logged_users.get(user_id);
                        ArrayList<String> rConnections = new ArrayList<String>();
                        HashSet<String> connections = crawler.getConnections(website);
                        if(connections != null){
                            for(String url: connections){
                                rConnections.add(url);
                            }
                            Message reply_list = new ReplyList("reply_connections", serverName, requestId, "true", rConnections);
                            reply = reply_list.toString();
                        }
                        else{
                            reply = "type | reply_connections ; sucess | false ; msg | There is no pages linked to "+website+"."; 
                            reply = prepend(reply, serverName, message.values.get("requestid"));
                        }
                    }
                    else{
                        reply = "type | reply_connections ; sucess | false ; msg | Your search return no results."; 
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                    }
                    multicast.sendMessage(reply);
                    break;

                case "listusers":
                    requestId = message.values.get("requestid");
                    user_id = Integer.parseInt(message.values.get("user_id"));
                    
                    if(logged_users.containsKey(user_id)){
                        username = logged_users.get(user_id);
                        if(users.get(username).isAdmin()){ 					//if user has administrator privileges
                            ArrayList<String> names = new ArrayList<String>();
                            for(Entry<String, User> entry: users.entrySet()){
                                String name = entry.getKey();
                                User user = entry.getValue();
                                if (user.isAdmin()){
                                    names.add(name + " " + "(admin)");
                                } else{
                                    names.add(name + " " + "(not admin)");
                                }
                            }
                            Message reply_list = new ReplyList("reply_list", serverName, requestId, "true", names);
                            reply = reply_list.toString();
                        }
                        else{
                            reply = "type | reply_list ; sucess | false ; msg | You don't have admin permissions to see the list of users.";
                            reply = prepend(reply, serverName, message.values.get("requestid"));
                        }
                    }
                    else{
                        reply = "type | reply_list ; sucess | false ; msg | You need to be logged and have admin permissions to see the list of users.";
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                    }
                    multicast.sendMessage(reply);
                    break;

                case "promote":
                    user_id = Integer.parseInt(message.values.get("user_id"));
                    String user_to_promote = message.values.get("user_to_promote");
                    if(users.containsKey(user_to_promote)){
                        if(logged_users.containsKey(user_id)){
                            username = logged_users.get(user_id);
                            if(users.get(username).isAdmin()){ 				//if user has administrator privileges
                                if(!users.get(user_to_promote).isAdmin()){ 	//if user is not yet admin
                                    users.get(user_to_promote).setAdmin(true);
                                    users.get(user_to_promote).setNotification(true);
                                    
                                    String reply_notification;
                                    if(users.get(user_to_promote).getLogged()){
                                        users.get(user_to_promote).setNotification(false);
                                        reply_notification = "type | reply_notification ; sucess | true ; username | "+ user_to_promote+" ; msg | You have been promoted to admin.";
                                        reply_notification = prepend(reply_notification, serverName, "0");
                                        multicast.sendMessage(reply_notification);
                                    }
   
                                    reply = "type | reply_promote ; sucess | true ; msg | The user "+ user_to_promote + " has been promoted to admin."; 
                                }
                                else {
                                    reply = "type | reply_promote ; sucess | false ; msg | This user is already admin.";
                                }
                            }
                            else{
                                reply = "type | reply_promote ; sucess | false ; msg | You don't have administrator permissions to promote user to admin.";
                            }
                        }
                        else{
                            reply = "type | reply_promote ; sucess | false ; msg | You need to be logged and have administrator permissions to promote user to admin.";
                        }
                    }
                    else{
                        reply = "type | reply_promote ; sucess | false ; msg | This user doesn't exist.";
                    }
                    reply = prepend(reply, serverName, message.values.get("requestid"));
                    multicast.sendMessage(reply);
                    break;

                case "notification":
                	username = message.values.get("user_id");
                    try{
                        if(users.get(username).getNotification()){
                            users.get(username).setNotification(false);
                            reply = "type | reply_notification ; sucess | true ; username | "+username+" ; msg | You have been promoted to admin.";
                        }
                        else{
                            reply = "type | reply_notification ; sucess | false ; msg | No notifications to display.";
                        }
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                        multicast.sendMessage(reply);
                    } catch(Exception e){
                        System.out.println("user not in database");
                    }
                    break;

                case "shareurls":
                    int size = Integer.parseInt(message.values.get("item_count"));
                    if (crawler.numberUrlsToVisit() + size < size*2){ 		//only accepts if total load is < load of sender
                        reply = "type | reply_shareurl ; sucess | true";
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                        ArrayList<Url> urlsToShare = message.getUrlList();
                        for(Url url: urlsToShare){
                            crawler.addUrlToVisit(url.url, url.depth);
                        }
                    }
                    else{
                        reply = "type | reply_shareurl ; sucess | false";
                        reply = prepend(reply, serverName, message.values.get("requestid"));
                    }
                    multicast.sendMessage(reply);
                    break;
                case "reply_shareurl":
                    if(!message.values.get("servername").equals(serverName)){
                        if(message.values.get("sucess").equals("true")){
                            crawler.removeUrlsToVisit();
                            System.out.println("Share request sucessfull.");
                        }
                    }
                    break;
            }
        }
        //multicast.closeSocket();
    }
}
