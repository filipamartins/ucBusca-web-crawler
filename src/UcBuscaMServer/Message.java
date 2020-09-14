package UcBuscaMServer;
import java.util.ArrayList;
import java.util.HashMap;

import shared.Url;

public class Message {
    public HashMap<String,String> values = new HashMap<String, String>();

    public Message(){}

    public Message(String type, String servername, String requestId){
        values.put("type", type);
        values.put("servername", servername);
        values.put("requestid", requestId);
    }
    
    public void setValue(String key, String value){
        values.put(key,value);
    }

    public ArrayList<String> getList(){
        int size = Integer.parseInt(values.get("item_count"));
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i< size; i++){ 
            String item = values.get("item_"+i);
            list.add(item);
        }
        return list;
    }

    public ArrayList<String> getList(String topic){
        int size = Integer.parseInt(values.get("item_count"));
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i< size; i++){ 
            String item = values.get("item_"+i+"_"+topic);
            list.add(item);
        }
        return list;
    }


    public ArrayList<Url> getUrlList(){
        int size = Integer.parseInt(values.get("item_count"));
        ArrayList<Url> list = new ArrayList<Url>();
        for(int i = 0; i< size; i++){ 
            String ws = values.get("item_"+i);
            int depth = Integer.parseInt(values.get("item_"+i+"_depth"));
            Url url = new Url(ws, depth);
            list.add(url);
        }
        return list;
    }

    @Override
    public String toString(){
        String msg = "";
        for (String k: values.keySet()){
            msg +=  k + " | " + values.get(k) + " ; ";
        }
        msg = msg.substring(0, msg.length() - 2);
        return msg;
    }

    public void fromString(String msg){
        String[] tokens = msg.split(";");
        for(String s: tokens){
            String[] keyvalue = s.split("\\|");
            try{
                values.put(keyvalue[0].trim(), keyvalue[1].trim());
            }catch (Exception e){
                System.out.println("NOT a pair:"+ keyvalue);
            }
        } 
    }
}


class RegisterLogin extends Message {

    public RegisterLogin(String type, String servername, String requestId, String username, String password){
        super(type, servername, requestId);
        values.put("username", username);
        values.put("password", password);
    }
}

class Search extends Message{
    public Search(String type, String servername, String requestId, String user_id, ArrayList<String> list){
        super(type, servername, requestId);
        values.put("user_id", user_id);
        values.put("item_count", Integer.toString(list.size()));
        for(int i = 0; i< list.size(); i++){
            values.put("item_"+i, list.get(i));
        }
    }
}

class Index extends Message{
    public Index(String type, String servername, String requestId, String user_id, String url){
        super(type, servername, requestId);
        values.put("user_id", user_id);
        values.put("url", url);
    }
}

class IndexRequest extends Message{
    public IndexRequest(String type, String servername, String requestId, String target, String url){
        super(type, servername, requestId);
        values.put("target", target);
        values.put("url", url);
    }
}

class Promote extends Message{
    public Promote(String type, String servername, String requestId, String user_id, String user_to_promote){
        super(type, servername, requestId);
        values.put("user_id", user_id);
        values.put("user_to_promote", user_to_promote);
    }
}

class Admin extends Message{
    //constructor of type consult, list users and page admin
    public Admin(String type, String servername, String requestId, String user_id){
        super(type, servername, requestId);
        values.put("user_id", user_id);
    }
}

class Notification extends Message{
    public Notification(String type, String servername, String requestId, String user_id){
        super(type, servername, requestId);
        values.put("user_id", user_id);
    }
}

class ShareUrls extends Message{
    public ShareUrls(String type, String servername, String requestId, ArrayList<Url> list){
        super(type, servername, requestId);
        values.put("item_count", Integer.toString(list.size()));
        for(int i = 0; i< list.size(); i++){
            values.put("item_"+i, list.get(i).url);
            values.put("item_"+i+"_depth", Integer.toString(list.get(i).depth));
        }
    }
}

class ReplySearch extends Message{
    public ReplySearch(String type, String servername, String requestId, String sucess, ArrayList<HashMap<String, String>> urls){
        super(type, servername, requestId);
        values.put("sucess", sucess);
        values.put("item_count", Integer.toString(urls.size()));
        for(int i = 0; i< urls.size(); i++){
            HashMap<String, String> url = urls.get(i);
            values.put("item_"+i, url.get("url"));
            values.put("item_"+i+"_connections", url.get("connections"));
            values.put("item_"+i+"_title", url.get("title"));
            values.put("item_"+i+"_citation", url.get("citation"));
        }
    }
}

class ReplyList extends Message{
    public ReplyList(String type, String servername, String requestId, String sucess, ArrayList<String> list){
        super(type, servername, requestId);
        values.put("sucess", sucess);
        values.put("item_count", Integer.toString(list.size()));
        for(int i = 0; i< list.size(); i++){
            values.put("item_"+i, list.get(i));
        }
    }
}
