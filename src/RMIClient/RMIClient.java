package RMIClient;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import shared.ClientInterface;
import shared.MessageToClient;
import shared.UcBuscaInterface;

public class RMIClient extends UnicastRemoteObject implements ClientInterface {
	private static final long serialVersionUID = 1L;


	protected RMIClient() throws RemoteException {
		super();
	}

	private static UcBuscaInterface ucBusca;
    private static String RMI_ADDRESS = "localhost";
    private static int RMI_PORT = 7000;
    private static String RMIURL = "localhost:7000/ucBusca";
    private static Boolean user_logged = false;
    private static Boolean notified = false;
    private static String user_id = "null";

    
    public void sendMessage(String username, String s) throws RemoteException {
    	if(user_logged && !notified) {
    		System.out.println("> " + s);
    		notified = true;
    	}
	}

    public static void main(String args[]) {
        BufferedReader reader;
        String[] tokens = null;
        String command;
        MessageToClient message = null;
        Boolean firstConnection = true;
        Boolean reconnect = false;
        

        if(args.length!=2){
            System.out.println("You should provide exactly two arguments. RMI_ADDRESS RMI_PORT");
            System.out.println("Example: RMIClient localhost 7000");
        }
        else{
            while (true) {
                RMI_ADDRESS = args[0];
                RMI_PORT = Integer.parseInt(args[1]);
                RMIURL = RMI_ADDRESS+":"+RMI_PORT+"/ucBusca";
                
                try {
                    ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+RMIURL);
                  
                    if(firstConnection){
                        System.out.println("Connecting to server...");
                        String welcome = ucBusca.connect();
                        System.out.println(welcome);
                        firstConnection = false;
                        reconnect = false;
                    }
                    while (true) {
                        if(!reconnect){
                            System.out.println("Insert command: ");
                            System.out.print("> ");
                            reader = new BufferedReader(new InputStreamReader(System.in));
                            command = reader.readLine();
                            tokens = command.split(" ");
                        }
                        else{
                            reconnect = false;
                        }
                        switch (tokens[0]) {
                        case "login":
                            if (tokens.length == 3) {
                                String user = tokens[1];
                                String pass = tokens[2];
                                message = ucBusca.login(user, pass);
                                if (message.sucess.equals("true")) {
                                    user_id = message.user_id;
                                    user_logged = true;
                                    ucBusca.notifications(user);                                    
                                }
                                System.out.println(message.text);
                            } else {
                                invalidCommand();
                            }
                            break;
    
                        case "register":
                            if (tokens.length == 3) {
                                String user = tokens[1];
                                String pass = tokens[2];
                                message = ucBusca.register(user, pass);
                                if (message.sucess.equals("true")) {
                                	ClientInterface rmiClient = new RMIClient();
	                                ucBusca.subscribe(user, rmiClient);
                                }
                                System.out.println(message.text);
                            } else {
                                invalidCommand();
                            }
                            break;
    
                        case "index":
                            if (!user_logged) {
                                System.out.println("Log in to ucBusca to access this feature.");
                                break;
                            }
                            if (tokens.length == 2) {
                                String url = tokens[1];
                                message = ucBusca.indexUrl(url, user_id);
                                System.out.println(message.text);
                            } else {
                                invalidCommand();
                            }
                            break;
    
                        case "search":
                            ArrayList<String> words = new ArrayList<String>();
                            for (int i = 1; i < tokens.length; i++) {
                                words.add(tokens[i]);
                            }
                            message = ucBusca.search(words, user_id);
                            if (message.sucess.equals("true")) {
                                System.out.println("Number of results: " + message.totalResults); // number of results
                                for ( HashMap<String,String> url : message.results){
                                    System.out.println(url.get("title"));
                                    System.out.println(url.get("url"));
                                    System.out.println(url.get("citation"));
                                    System.out.println(url.get("connections"));
                                }
                            } else {
                                System.out.println(message.text);
                            }
                            break;
    
                        case "connections":
                            if (tokens.length == 2) {
                                if (!user_logged) {
                                    System.out.println("Log in to ucBusca to access this feature.");
                                    break;
                                }
                                String url = tokens[1];
                                message = ucBusca.connections(url, user_id);
                                if (message.sucess.equals("true")) {
                                    int size = message.list.size();
                                    System.out.println("Pages liked to " + url +":");
                                    for (int i = 0; i < size; i++) {
                                        System.out.println(message.list.get(i));
                                    }
                                } else {
                                    System.out.println(message.text);
                                }
                            }
                            else {
                                invalidCommand();
                            }
                            break;
    
                        case "list":
                            if (tokens.length == 2) {
                                if(tokens[1].equals("users")){
                                    if (!user_logged) {
                                        System.out.println("Log in to ucBusca to access this feature.");
                                        break;
                                    }
                                    message = ucBusca.listUsers(user_id);
                                    if (message.sucess.equals("true")) {
                                        int size = message.list.size();
    
                                        for (int i = 0; i < size; i++) {
                                            String user = message.list.get(i);
                                            System.out.println(user);
                                        }
                                    } else {
                                        System.out.println(message.text);
                                    }
                                }
                                else if(tokens[1].equals("searches")){
                                    if (!user_logged) {
                                        System.out.println("Log in to ucBusca to access this feature.");
                                        break;
                                    }
                                    message = ucBusca.listSearches(user_id);
                                    if (message.sucess.equals("true")) {
                                        int size = message.list.size();
                                            
                                        System.out.println("Your research history:"); 
                                        for (int i = 0; i < size; i++) {
                                            System.out.println(message.list.get(i));
                                        }
                                    } else {
                                        System.out.println(message.text);
                                    }
                                }
                                else {
                                    invalidCommand();
                                }
                            }
                            else {
                                invalidCommand();
                            }
                            break;
    
                        case "promote":
                            if (!user_logged) {
                                System.out.println("Log in to ucBusca to access this feature.");
                                break;
                            }
                            if (tokens.length == 2) {
                                message = ucBusca.promoteUser(user_id, tokens[1]); // tokens[1] - username of user to
                                                                                   // promote to admin
                                System.out.println(message.text);
                            } else {
                                invalidCommand();
                            }
                            break;
    
                        case "admin":
                            break;
    
                        case "help":
                            printHelp();
                            break;
    
                        default:
                            invalidCommand();
                            break;
                        }
                    }
                } catch (Exception e) {
                    //System.out.println("Server down reconnecting...");
                    reconnect = true;
                    System.out.println("Exception in main: " + e);
                    //e.printStackTrace();
                }
            }
        }
    }

    private static void printHelp() {
        String s = "Available commands:\n"
                + "register [username] [password]\tRegister an user with username and password.\n"
                + "login [username] [password]   \tLogins to the server using username and password.\n"
                + "search [word1] [word2]...     \tSearch with ucBusca one or more words.\n"
                + "list searches                 \tConsult previous researches.\n"
                + "connections [url]             \tSee list of pages linked to a specific page.\n"
                + "help                          \tDisplays this message.\n\n"
                + "Commands available only to admins:\n"
                + "index [url]                   \tAdd url to be index by ucBusca.\n"
                + "list users                    \tList all registered users.\n"
                + "promote [username]            \tGive admin privileges to another user.\n"
                + "admin                         \tShow admin page.\n";
                
        System.out.println(s);
    }

    private static void invalidCommand() {
        System.out.println("Invalid Command.\n");
        printHelp();
    }

}
