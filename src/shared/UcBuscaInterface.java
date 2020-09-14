package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface UcBuscaInterface extends Remote {
    public MessageToClient login(String user, String pass) throws RemoteException;
    public MessageToClient register(String user, String pass) throws RemoteException;
    public MessageToClient indexUrl(String url, String user_id) throws RemoteException;
    public MessageToClient search(ArrayList<String> words, String user_id) throws RemoteException;
    public MessageToClient listSearches(String user_id) throws RemoteException;
    public MessageToClient connections(String url, String user_id) throws RemoteException;
    public MessageToClient listUsers(String user_id) throws RemoteException;
    public MessageToClient promoteUser(String user_id, String username) throws RemoteException;
   
    public void checkRMIServer() throws RemoteException;
    public String connect() throws RemoteException;
    public void subscribe(String username, ClientInterface c) throws RemoteException;
    
    public void notifications(String username) throws RemoteException;
}
