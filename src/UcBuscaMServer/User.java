package UcBuscaMServer;
import java.util.ArrayList;

public class User {
    private String password;
    private Boolean admin;
    private Boolean notification;
    private ArrayList<String> searches;
    private Boolean logged;

    public User(String password, Boolean admin){
        searches = new ArrayList<String>();
        this.password = password;
        this.admin = admin;
        notification = false;
        logged = false;
    }

    public void setSearch(ArrayList<String> words){
        String search = "";
        int size = words.size();
        for(int i = 0; i < size; i++){
            search += words.get(i) + " ";
        }
        search = search.substring(0, search.length() -1);
        searches.add(search);
    }


    public String getPassword(){
        return this.password;
    }
    
    public void setAdmin(Boolean b){
        this.admin = b;
    }

    public Boolean isAdmin(){
        return this.admin;
    }

    public Boolean getNotification(){
        return this.notification;
    }

    public void setNotification(Boolean b){
        this.notification = b;
    }
    
    public Boolean getLogged(){
        return this.logged;
    }

    public void setLogged(Boolean b){
        this.logged = b;
    }
    public ArrayList<String> getSearches(){
        return searches;
    }
}


