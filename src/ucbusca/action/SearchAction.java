package ucbusca.action;

import java.rmi.RemoteException;
import java.util.Map;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;

import ucbusca.model.SearchBean;
import ucbusca.model.UserBean;

public class SearchAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private SearchBean searchBean;
	

	public String execute() throws RemoteException {
		String user_id = "null";
		if (searchBean.getSearch().length() != 0) {
			if(session.containsKey("userBean")) {
				user_id = getUserIdFromSession();
			}
	        String success = searchBean.search(user_id);
	        if(success.equals("false")) {
	        	addActionMessage(searchBean.getMessageText());
	        	return "fail";
	        }
	        else {
	        	return SUCCESS;
	        }
		}
		return "fail";
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
