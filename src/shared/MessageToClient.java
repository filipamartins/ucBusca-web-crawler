package shared;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageToClient implements Serializable {
    
    private static final long serialVersionUID = 1L;
    public String sucess;
    public String user_id;
    public String admin;
    public String text;
    public ArrayList<String> list;
    public ArrayList<HashMap<String,String>> results;
    public int totalResults;

    public MessageToClient(String sucess, String user_id, String admin, String text){
        list = new ArrayList<String>();
        this.sucess = sucess;
        this.user_id = user_id;
        this.text = text;
        this.admin = admin;
    }

    public MessageToClient(String sucess, String text){
        list = new ArrayList<String>();
        this.sucess = sucess;
        this.text = text;
    }

    public MessageToClient(String sucess, ArrayList<String> list){
        this.list = new ArrayList<String>();
        for(String item: list){
            this.list.add(item);
        }
        this.sucess = sucess;
    }
    public MessageToClient(String sucess, int totalResults, ArrayList<HashMap<String,String>> list){
        this.results = list;
        this.totalResults = totalResults;
        this.sucess = sucess;
    }
   
    public ArrayList<HashMap<String,String>> getResults() {
    	return results;
    }
    
    public ArrayList<String> getList() {
    	return list;
    }
    
}
