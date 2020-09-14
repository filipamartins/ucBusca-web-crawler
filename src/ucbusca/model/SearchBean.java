package ucbusca.model;

import shared.MessageToClient;
import shared.UcBuscaInterface;
import ucbusca.api.YandexClient;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchBean implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private static UcBuscaInterface ucBusca;
    private static String RMI_ADDRESS = "localhost";
    private static int RMI_PORT = 7000;
    private static String RMIURL = "localhost:7000/ucBusca";
	private static String search;
	private static MessageToClient message; //contains results of search
	private static ArrayList<HashMap<String,String>> results;
	
	public SearchBean() {
		try {
			 ucBusca = (UcBuscaInterface) Naming.lookup("rmi://"+RMIURL);
		}
		catch(NotBoundException|MalformedURLException|RemoteException e) {
			e.printStackTrace(); 
		}
	}
	
	public String search(String user_id) throws RemoteException {
	    ArrayList<String> words = new ArrayList<String>();
		String[] search_words = search.split(" ");
		
		for (int i = 0; i < search_words.length; i++) {
            words.add(search_words[i]);
        }
		message = ucBusca.search(words, user_id);
		if(message.sucess.equals("true")) {
			results = message.getResults();
			/* comment this block (next 16 lines) if you are not using Yandex API */
			int size = results.size();
			for(int i = 0; i< size; i++) {
				String title = results.get(i).get("title");
				String citation = results.get(i).get("citation");
				String lang = YandexClient.getLanguage(title); //get language of url title
				System.out.println("LINGUA ORIGINAL " + lang);
				results.get(i).put("original_lang", lang);
				if(!lang.equals("pt")) {
					if(!citation.equals("")) {
						String translation_title = YandexClient.translate(title, lang, "pt");
						String translation_citation = YandexClient.translate(citation, lang, "pt");
						results.get(i).put("title", translation_title);
						results.get(i).put("citation", translation_citation);
					}
				}
			}
			/*  */
		}
		return message.sucess;
	}
	
	
	public void listSearches(String user_id) throws RemoteException {
		message = ucBusca.listSearches(user_id);	
	}
	
	public String getSearch() {
        return search;
    }
    
    public void setSearch(String search) {
    	this.search = search;
    }
    
    public ArrayList<HashMap<String,String>> getResults() {
        return results;
    }
    
    public void setResults(ArrayList<HashMap<String,String>> results) {
    	this.results = results;
    }
    
    public MessageToClient getMessage() {
        return message;
    }
    
    public void setMessage(MessageToClient message) {
    	this.message = message;
    }
    
    public String getMessageText() {
		return message.text;
	}
	
}
