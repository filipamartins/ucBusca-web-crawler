package shared;

import java.rmi.*;

public interface ClientInterface extends Remote{
	public void sendMessage(String username, String s) throws java.rmi.RemoteException;
}