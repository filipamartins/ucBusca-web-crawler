package ucbusca.action;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;

import ucbusca.model.SearchBean;
import ucbusca.model.UrlBean;
import ucbusca.model.UserBean;

public class UrlAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private UrlBean urlBean = new UrlBean();
	private SearchBean searchBean = new SearchBean();
	private UserBean userBean = new UserBean();
	private String urlToIndex = null;
	private String urlConnections = null;
	
	
	public String executeIndexUrl() throws RemoteException {
		String user_id = getUserIdFromSession();
		searchBean.listSearches(user_id); //get user searches before page refresh 
		userBean.listUsers(user_id); //get list of users before page refresh 
		if (urlToIndex == null || urlToIndex.equals("")) {
			return "fail";
		}
		urlBean.setUrl(urlToIndex);
		String success = urlBean.indexUrl(user_id);
		if (success.equals("false") || success.equals("already_indexed")) {
			addFieldError("urlToIndex", urlBean.getMessageText());
			return "fail";
		}
		addActionMessage(urlBean.getMessageText());
		return SUCCESS;
	}

	
	public String executeConnections() throws RemoteException {
		String user_id = getUserIdFromSession();
		searchBean.listSearches(user_id); 
		userBean.listUsers(user_id);
		if (urlConnections == null || urlConnections.equals("")) {
			return "fail";
		}
		urlBean.setUrl(urlConnections);
		String success = urlBean.connections(user_id);
		if (success.equals("false")) {
			addActionMessage(urlBean.getMessageText());
			return "fail";
		}
		return SUCCESS;
	}
	
	public UrlBean getUrlBean() {
        return urlBean;
    }
    
    public void setUrlBean(UrlBean urlBean) {
    	this.urlBean = urlBean;
    }
    public SearchBean getSearchBean() {
        return searchBean;
    }
    
    public void setSearchBean(SearchBean searchBean) {
    	this.searchBean = searchBean;
    }
    
    public UserBean getUserBean() {
        return userBean;
    }
    
    public void setUserBean(UserBean userBean) {
    	this.userBean = userBean;
    }
    
    public String getUrlToIndex() {
        return urlToIndex;
    }
    
    public void setUrlToIndex(String urlToIndex) {
    	this.urlToIndex = urlToIndex;
    }
    
    public String getUrlConnections() {
        return urlConnections;
    }
    
    public void setUrlConnections(String urlConnections) {
    	this.urlConnections = urlConnections;
    }
    
    private String getUserIdFromSession() {
    	UserBean userBean = (UserBean) session.get("userBean");
    	return userBean.getUserId();
    }
    
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	
}
