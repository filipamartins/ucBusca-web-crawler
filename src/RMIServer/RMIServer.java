package RMIServer;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import shared.UcBuscaInterface;
import shared.ClientInterface;
import shared.MessageToClient;
import shared.Multicast;


public class RMIServer extends UnicastRemoteObject implements UcBuscaInterface {

    private static final long serialVersionUID = 1L;
    private static String MULTICAST_ADDRESS = "224.1.2.3";
    private static String RMI_ADDRESS = "localhost";
    private static int PORT = 5000;
    private static int RMI_PORT = 7000;
    private static String RMIURL = "localhost:7000/ucBusca";
    private static Multicast multicast = null;
    private static String serverName;
    private static CopyOnWriteArrayList<Message> queue;
    private static CopyOnWriteArrayList<Integer> requestIdList;
    public static HashMap<String, ClientInterface> subscribedUsers = new HashMap<String, ClientInterface>();

    public RMIServer() throws RemoteException {
        super();
    }

    public int getNewRequestId(){
        int randomNum;
        do{
            randomNum = ThreadLocalRandom.current().nextInt(1, 1000 + 1);
        } while(requestIdList.contains(randomNum));
        requestIdList.add(randomNum);
        return randomNum;
    }
    
   
    public void removeRequestId(Integer id){
        requestIdList.remove(id);
    }

    public MessageToClient login(String user, String pass) throws RemoteException{
        MessageToClient msg_to_client = registerLogin("login", user, pass);
        return msg_to_client;
    }


    public MessageToClient register(String user, String pass) throws RemoteException{
        MessageToClient msg_to_client = registerLogin("register", user, pass);
        return msg_to_client;
    }
    
    //returns the messages from queue associated with a given request id passed by parameter
    private ArrayList<Message> getMessagesByRequestId(String requestId){
        ArrayList<Message> myMessages = new ArrayList<Message>();
        for(Message msg : queue){
            if (msg.values.get("requestid").equals(requestId)){
                myMessages.add(msg);
            }
            queue.removeAll(myMessages);
        }
        return myMessages;
    }
    
    //filters messages received from multicast servers by status (success of request true false or other)
    private ArrayList<Message> filterByStatus(ArrayList<Message> messages, String status){
        ArrayList<Message> myMessages = new ArrayList<Message>();
        for(Message msg : messages ){
            if(msg.values.get("sucess").equals(status)){
                myMessages.add(msg);
            }
        }
        return myMessages;
    }

    //returns all the messages received by getMessagesByRequestId() if total number of messages is equal to the 
    //number of multicast servers, the elapsed time is 1000 and there is at least one message received 
    //or the elapsed time is > 30seconds
    private ArrayList<Message> getMyMessages(String requestId, String msg){

        ArrayList<Message> myMessages = new ArrayList<Message>();
        long elapsedTime = 0;
        int i = 1;
        long startTime = System.currentTimeMillis();
        do{
            try{
                Thread.sleep(100);
                elapsedTime = System.currentTimeMillis() - startTime;
                if(elapsedTime >= 5000*i){                            
                    //resend message from client at 5 seconds interval, ensuring that client receives an answer if server is down 
                    multicast.sendMessage(msg);
                    i++;
                }
            } catch(Exception e){
                System.out.print(e);
            }
            myMessages.addAll(getMessagesByRequestId(requestId));
        } while (myMessages.size() < 2 && (elapsedTime < 1000 || myMessages.size() <= 0) && elapsedTime < 30000);
        return myMessages;
    }

    //return servername with lowest load (number of urls to visit)
    private String servernameWithLowestLoad(ArrayList<Message> messages){
        int lowest = Integer.parseInt(messages.get(0).values.get("load"));
        String servername = messages.get(0).values.get("servername");
        for(int i = 1; i < messages.size(); i++){
            int load = Integer.parseInt(messages.get(i).values.get("load"));
            if(load < lowest){
                lowest = load;
                servername = messages.get(i).values.get("servername");
            }
        }
        return servername;
    }

    //joins the results of all successful messages received by all the multicast servers 
    //ensuring that there is no duplicates
    private ArrayList<String> joinResults(ArrayList<Message> messages){
        ArrayList<String> joinedResults = messages.get(0).getList();
        int size = messages.size();
        for(int i = 1; i< size; i++){
            ArrayList<String> results = messages.get(i).getList();
            for(String result: results){
                if(!joinedResults.contains(result)){
                    joinedResults.add(result);
                }
            }
        }
        return joinedResults;
    }

    private ArrayList<HashMap<String, String>> getListUrl(Message message){
        ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        ArrayList<String> urls = message.getList();
        ArrayList<String> connections = message.getList("connections");
        ArrayList<String> titles = message.getList("title"); 
        ArrayList<String> citations = message.getList("citation");
        for(int i=0; i < urls.size(); i++){
            HashMap<String, String> url = new  HashMap<String, String>();
            url.put("url", urls.get(i));
            url.put("connections", connections.get(i));
            url.put("title", titles.get(i));
            url.put("citation", citations.get(i));
            results.add(url);
        }
        return results;
    }

    //joins the results of all successful messages received by the multicast servers in reply 
    //to a search, ensuring there is no duplicates
    //if the duplicates have a different relevance level (number of connections) the higher 
    //relevance number will be kept  
    private ArrayList<HashMap<String, String>> joinSearchResults(ArrayList<Message> messages) {
        class MapComparator implements Comparator<HashMap<String, String>>
        {
            private final String key;

            public MapComparator(String key)
            {
                this.key = key;
            }

			@Override
			public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                int firstValue = Integer.parseInt(o1.get(key));
                int secondValue = Integer.parseInt(o2.get(key));
                return secondValue - firstValue;
			}
        }
        
        HashMap<String,HashMap<String, String>> results = new HashMap<String,HashMap<String, String>>();
        int size = messages.size();
        for(int i = 0; i< size; i++){
            Message message = messages.get(i);
            ArrayList<HashMap<String, String>> urls = getListUrl(message);
            for( HashMap<String, String> url : urls){
                String urlname = url.get("url");
                String connections = url.get("connections");
                if( results.containsKey(urlname)) {
                    String rConnections = results.get(urlname).get("connections");
                    if(Integer.parseInt(connections) > Integer.parseInt(rConnections)){
                        results.put(urlname,url);
                    }
                }
                else{
                    results.put(urlname,url);
                }
            }
        }
        Collection<HashMap<String, String>> values = results.values();
        ArrayList<HashMap<String, String>> rlist = new ArrayList<HashMap<String, String>>(values);
        Collections.sort(rlist, new MapComparator("connections")); 
        return rlist;
    }
    
    
    private MessageToClient registerLogin(String type, String user, String pass){
        String requestId = Integer.toString(getNewRequestId());
        String login_id = Integer.toString(0 + (int)(Math.random() * ((2000 - 0) + 1)));
        Message message = new RegisterLogin(type, serverName, requestId, user, pass);
        message.setValue("user_id", login_id);
        Message reply = new Message();
        MessageToClient msg_to_client;

        String msg = message.toString();
        multicast.sendMessage(msg);
        
        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if(!sucessMessages.isEmpty()){
            reply = sucessMessages.get(0);
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
        }
        else{
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }

        if(message.values.get("type").equals("login")){
            msg_to_client = new MessageToClient(reply.values.get("sucess"), login_id, reply.values.get("admin"), reply.values.get("msg"));
        }
        else{ //register
            msg_to_client = new MessageToClient(reply.values.get("sucess"), reply.values.get("msg"));
        }
        return msg_to_client;
    }

    
    public MessageToClient indexUrl(String url, String user_id) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Index("index", serverName, requestId, user_id, url);
        Message reply = new Message();
        MessageToClient msg_to_client;
        
        String msg = message.toString();
        multicast.sendMessage(msg);
        ArrayList<Message> myMessages = getMyMessages(requestId, msg);

        ArrayList<Message> already_indexed = filterByStatus(myMessages,"already_indexed");
        if(!already_indexed.isEmpty()){
            reply = already_indexed.get(0);
        }   

        else{
            ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
            ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
            if(!sucessMessages.isEmpty()){
                String target = servernameWithLowestLoad(myMessages); //even servers that don't have the user in the database can be chosen to index url

                requestId = Integer.toString(getNewRequestId());
                message = new IndexRequest("index_request", serverName, requestId, target, url);
                msg = message.toString();
                multicast.sendMessage(msg);
                myMessages = getMyMessages(requestId, msg);
                if(!myMessages.isEmpty()){
                    reply = myMessages.get(0);
                }
                else {
                    System.out.println("No messages returned.");
                    return new MessageToClient("false", "Time out ocurred. Server down.");
                }           
            }
            else if(!insucessMessages.isEmpty()){
                reply = insucessMessages.get(0);
            }
            else{
                System.out.println("No messages returned.");
                return new MessageToClient("false", "Time out ocurred. Server down.");
            }
        }
        msg_to_client = new MessageToClient(reply.values.get("sucess"), reply.values.get("msg"));
        return msg_to_client;
    }

    public MessageToClient search(ArrayList<String> words, String user_id) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Search("search", serverName, requestId, user_id, words);
        Message reply = new Message();
        MessageToClient msg_to_client;

        String msg = message.toString();
        multicast.sendMessage(msg);

        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if((!sucessMessages.isEmpty())){
            ArrayList<HashMap<String, String>> results = joinSearchResults(sucessMessages);
            int totalResults = results.size();
            System.out.println("FINAL RESULT size: " + totalResults);
            ArrayList<HashMap<String, String>> subResults = 
                        new ArrayList<HashMap<String, String>>(results.subList(0,Math.min(9, totalResults)));
            System.out.println(subResults);
            msg_to_client = new MessageToClient("true", totalResults, subResults);
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
            msg_to_client = new MessageToClient("false", reply.values.get("msg"));
        }
        else {
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }
        return msg_to_client;
    }

    public MessageToClient listSearches(String user_id) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Admin("listsearches", serverName, requestId, user_id);
        Message reply = new Message();
        MessageToClient msg_to_client;
        
        String msg = message.toString();
        multicast.sendMessage(msg);

        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if((!sucessMessages.isEmpty())){
            ArrayList<String> results = joinResults(sucessMessages);
            msg_to_client = new MessageToClient("true", results);
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
            msg_to_client = new MessageToClient("false", reply.values.get("msg"));
        }
        else {
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }
        return msg_to_client;
    }

    public MessageToClient connections(String url, String user_id) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Index("connections", serverName, requestId, user_id, url);
        Message reply = new Message();
        MessageToClient msg_to_client;
        
        String msg = message.toString();
        multicast.sendMessage(msg);

        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if((!sucessMessages.isEmpty())){
            ArrayList<String> results = joinResults(sucessMessages);
            msg_to_client = new MessageToClient("true", results);
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
            msg_to_client = new MessageToClient("false", reply.values.get("msg"));
        }
        else {
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }
        return msg_to_client;
    }

    public MessageToClient listUsers(String user_id) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Admin("listusers", serverName, requestId, user_id);
        Message reply = new Message();
        MessageToClient msg_to_client;
        
        String msg = message.toString();
        multicast.sendMessage(msg);

        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if((!sucessMessages.isEmpty())){
            ArrayList<String> results = joinResults(sucessMessages);
            msg_to_client = new MessageToClient("true", results);
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
            msg_to_client = new MessageToClient("false", reply.values.get("msg"));
        }
        else {
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }
        return msg_to_client;
    }

    public MessageToClient promoteUser(String user_id, String user_to_promote) throws RemoteException {
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Promote("promote", serverName, requestId, user_id, user_to_promote);
        Message reply = new Message();
        MessageToClient msg_to_client;
        
        String msg = message.toString();
        multicast.sendMessage(msg);

        ArrayList<Message> myMessages = getMyMessages(requestId, msg);
        ArrayList<Message> sucessMessages = filterByStatus(myMessages,"true");
        ArrayList<Message> insucessMessages = filterByStatus(myMessages,"false");
        if((!sucessMessages.isEmpty())){
            reply = sucessMessages.get(0);
            msg_to_client = new MessageToClient("true", reply.values.get("msg"));
        }
        else if(!insucessMessages.isEmpty()){
            reply = insucessMessages.get(0);
            msg_to_client = new MessageToClient("false", reply.values.get("msg"));
        }
        else {
            System.out.println("No messages returned.");
            return new MessageToClient("false", "Time out ocurred. Server down.");
        }
        return msg_to_client;
    }

    public void notifications(String username) throws RemoteException{
        String requestId = Integer.toString(getNewRequestId());
        Message message = new Admin("notification", serverName, requestId, username);
        String msg = message.toString();
        multicast.sendMessage(msg);
    }
    

    public String connect() throws RemoteException {
        System.out.println("Client connected.");
        return "Welcome to RMIServer!";
    }

    public void subscribe(String username, ClientInterface c) throws RemoteException {
    	subscribedUsers.put(username, c);
    }

    public void checkRMIServer() throws RemoteException {
        System.out.println("This server is online.");
        try{
            Thread.sleep(1000);
        } catch(Exception e) {

        }
    }

    // =========================================================
    public static void main(String args[]) {
        
        int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
        serverName = Integer.toString(randomNum);
        if(args.length!=4){
            System.out.println("You should provide exactly four arguments. MULTICAST_ADDRESS PORT RMI_ADDRESS RMI_PORT");
            System.out.println("Example: RMIServer 224.1.2.3 5000 localhost 7000");
        }
        else{
            MULTICAST_ADDRESS = args[0];
            PORT = Integer.parseInt(args[1]);
            RMI_ADDRESS = args[2];
            RMI_PORT =  Integer.parseInt(args[3]);
            RMIURL = RMI_ADDRESS+":"+RMI_PORT+"/ucBusca"; // localhost:7000/ucBusca
            
            multicast = new Multicast(MULTICAST_ADDRESS, PORT);
            System.out.println("Server: " + serverName + " running...");
         
            int calls = 3;
            while (true) {
                while (calls > 0) {
                    try {
                        System.out.println("Checking if another server is online (Calls " + calls +").");
                        UcBuscaInterface ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+ RMIURL);
                        ucBusca.checkRMIServer();
                        System.out.println("Primary server is up. Running as Backup Server.");
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        calls--;
                    }
                }
                System.out.println("Connecting as Primary Server.");
                try {
                    RMIServer rmiServer = new RMIServer();
                    queue = new CopyOnWriteArrayList<Message>();
                    requestIdList = new CopyOnWriteArrayList<Integer>();
                    new RMIReceiver(MULTICAST_ADDRESS, PORT, serverName, queue);
                    
            		LocateRegistry.createRegistry(RMI_PORT).rebind("ucBusca", rmiServer);
      
                    System.out.println("RMI Server ready.");
                    break;
                } catch (RemoteException re) {
                    System.out.println("Primary Server is down.");
    
                }
            }
        }
    }

}

