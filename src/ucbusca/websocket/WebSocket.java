package ucbusca.websocket;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import shared.ClientInterface;
import shared.UcBuscaInterface;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint("/ws/{username}")
public class WebSocket  extends UnicastRemoteObject implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
    private Session session;
    private static String RMIURL = "localhost:7000/ucBusca";
    private static UcBuscaInterface ucBusca;
    private static Map<String, Session> sessions = new ConcurrentHashMap<>(); 
      
    public WebSocket() throws RemoteException {
        try {
			 ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+RMIURL);
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); 
		}
    }

    @OnOpen
    public void start(Session session, @PathParam("username") String username) throws RemoteException {
    	sessions.put(username, session);
        this.session = session;

        ClientInterface client = new WebSocket();
        ucBusca.subscribe(username, client);
        
        String message = "*" + username + "* connected.";
        sendMessage(username, message);
        ucBusca.notifications(username);
    }

    @OnClose
    public void close(@PathParam("userid") String userId)  throws RemoteException {
    	sessions.remove(userId);
    	// clean up once the WebSocket connection is closed
    }

    @OnMessage
    public void receiveMessage(String message)  throws RemoteException{
		// one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
    	// String upperCaseMessage = message.toUpperCase();
    	// sendMessage("[" + user + "] " + upperCaseMessage);
    }
    
    @OnError
    public void handleError(Throwable t) {
    	t.printStackTrace();
    }


    public void sendMessage(String username, String text) throws RemoteException {
    	System.out.println("NUM CONNEC: "+sessions.size());
    	System.out.println(sessions.keySet());
    	System.out.println("------");
        Session s = sessions.get(username);
    	try {
    		s.getBasicRemote().sendText(text);
    		System.out.println("SEND:"+text);
    	}
    	catch(Exception e) {
    		System.out.println("Unable to send message");
    		e.printStackTrace();
    		try {
    			this.session.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    	}
    }
    
    public void broadcast(String text) {
    	System.out.println("NUM CONNEC: "+sessions.size());
    	System.out.println(sessions.keySet());
    	System.out.println("------");
        sessions.values().forEach(s -> {
        	try {
        		s.getBasicRemote().sendText(text);
        		System.out.println("SEND:"+text);
        	}
        	catch(Exception e) {
        		System.out.println("Unable to send message");
        		e.printStackTrace();
        	}
        });
    }

}
