package ucbusca.model;

import shared.MessageToClient;
import shared.UcBuscaInterface;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class UrlBean implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static UcBuscaInterface ucBusca;
    private static String RMI_ADDRESS = "localhost";
    private static int RMI_PORT = 7000;
    private static String RMIURL = "localhost:7000/ucBusca";
	private String url; // url supplied by the user
	private MessageToClient message;

	
	public UrlBean() {
		try {
			 ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+RMIURL);
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); 
		}
	}
	
	public String indexUrl(String user_id) throws RemoteException {
		message = ucBusca.indexUrl(this.url, user_id);
		return message.sucess;
	}
	
	public String connections(String user_id) throws RemoteException {
		message = ucBusca.connections(this.url, user_id);
		return message.sucess;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getMessageText() {
		return message.text;
	}
	
	public MessageToClient getMessage() {
        return message;
    }
    
    public void setMessage(MessageToClient message) {
    	this.message = message;
    }
}
