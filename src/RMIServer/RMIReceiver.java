package RMIServer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CopyOnWriteArrayList;

import shared.ClientInterface;
import shared.Multicast;

public class RMIReceiver extends UnicastRemoteObject implements Runnable {
    private static final long serialVersionUID = 1L;
    private CopyOnWriteArrayList<Message> queue;
    private String multicastAddress;
    private  int port;
    private  String serverName;

    public RMIReceiver(String multicastAddress, int port, String serverName, CopyOnWriteArrayList<Message> queue) throws RemoteException {
        super();
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.serverName = serverName;
        this.queue = queue;
        new Thread(this, "MsgReceiver").start();
    }
    
    public void run(){
        Multicast multicast = new Multicast(multicastAddress, port);
        
        System.out.println("MsgReceiver running");
        while(true){
            Message message = new Message();
            String msg = multicast.receiveMessage();
            message.fromString(msg);
            try{
                String servername = message.values.get("servername");
                String requestid = message.values.get("requestid");
               
                System.out.println(" MSG FROM SERVER:" + servername + " REQUESTID: "+ requestid);
                if (!servername.equals(serverName)){
                	if(message.values.get("type").equals("reply_notification") && message.values.get("sucess").equals("true") ) {
                		String username = message.values.get("username");
                    	ClientInterface c = RMIServer.subscribedUsers.get(username); //RMI CALL BACK
                    	try{
                    		c.sendMessage(username,"You have been promoted to admin.");
                    	 }catch(Exception e){
                    		 System.out.println("Callback not successful.");
                    		 e.printStackTrace();
                    	 }
                    }
                    queue.add(message);
                    System.out.println("ADDED TO QUEUE!");
                }
            }catch(Exception e){
                System.out.println("Message received without servername or request id.");
            }
        }
    }
}