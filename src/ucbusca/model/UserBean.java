package ucbusca.model;

import shared.MessageToClient;
import shared.UcBuscaInterface;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


public class UserBean implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static UcBuscaInterface ucBusca;
    private static String RMI_ADDRESS = "localhost";
    private static int RMI_PORT = 7000;
    private static String RMIURL = "localhost:7000/ucBusca";
	private String username; // username and password supplied by the user
	private String password;
	private String user_id;
	private Boolean admin;
	private MessageToClient message;

	
	public UserBean() {
		try {
			 ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+RMIURL);
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); 
		}
	}
	
	public String register() throws RemoteException {
		message = ucBusca.register(this.username, this.password);
		return message.sucess;
	}
	
	public String login() throws RemoteException {
		message = ucBusca.login(this.username, this.password);
		if(message.sucess.equals("true")) {
			setUserId(message.user_id);
			if(message.admin.equals("true")) {
				setAdmin(true);
			}
			else {
				setAdmin(false);
			}
		}
		return message.sucess;
	}
	
	public void checkNotifications(String user_id) throws RemoteException {
		ucBusca.notifications(user_id);
	}
	
	public void listUsers(String user_id) throws RemoteException {
		message = ucBusca.listUsers(user_id);
		System.out.println("MESSAGE SUCESS" + message.sucess);
	}
	
	public String promoteUser(String user_id, String username) throws RemoteException {
		message = ucBusca.promoteUser(user_id, username);
		return message.sucess;
	}
	
	public String getUsername(){
	     return username;
	}
	
	public String getPassword(){
	     return password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUserId() {
		return user_id;
	}
	
	public void setUserId(String user_id) {
		this.user_id = user_id;
	}
	 
	public boolean getAdmin(){
	     return admin;
	}
	
	public void setAdmin(boolean admin){
	     this.admin = admin;
	}
	
	public void setMessage(MessageToClient message) {
    	this.message = message;
    }
    
	public MessageToClient getMessage() {
        return message;
    }
	
	public String getMessageText() {
		return message.text;
	}
	
	public String toString() {
	    return "Username: " + getUsername() + " Password:  " + getPassword();
	}
}
