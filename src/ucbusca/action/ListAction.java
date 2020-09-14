package ucbusca.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.Map;

import ucbusca.model.SearchBean;
import ucbusca.model.UserBean;

public class ListAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private UserBean userBean = new UserBean();
	private SearchBean searchBean = new SearchBean();
	
	@Override
	public String execute() throws RemoteException {
		String user_id = getUserIdFromSession();
		searchBean.listSearches(user_id); 
		userBean.listUsers(user_id);
		return SUCCESS;
	}

	public UserBean getUserBean() {
        return userBean;
    }
    
    public void setUserBean(UserBean userBean) {
    	this.userBean = userBean;
    }
	
    public SearchBean getSearchBean() {
        return searchBean;
    }
    
    public void setSearchBean(SearchBean searchBean) {
    	this.searchBean = searchBean;
    }
    
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	private String getUserIdFromSession() {
    	UserBean userBean = (UserBean) session.get("userBean");
    	return userBean.getUserId();
    }
	
}
