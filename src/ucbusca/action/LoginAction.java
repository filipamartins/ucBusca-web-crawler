package ucbusca.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import java.rmi.RemoteException;
import java.util.Map;
import ucbusca.model.UserBean;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private UserBean userBean;


	public void validate(){
	    if (userBean.getUsername().length() == 0) {
	        addFieldError("userBean.username", "Username is required.");
	    }
	    if (userBean.getPassword().length() == 0) {
	        addFieldError("userBean.password", "Password is required.");
	    }
	}
	
	@Override
	public String execute() throws RemoteException {
		String success = userBean.login();
		if (success.equals("false")) {
			addFieldError("userBean.password", userBean.getMessageText());
			return "fail";
		}
		else {
			setSessionUserBean(this.userBean);
			session.put("loggedin", true); // this marks the user as logged in
			return SUCCESS;
		}
	}

	public UserBean getUserBean() {
        return userBean;
    }
    
    public void setUserBean(UserBean userBean) {
    	this.userBean = userBean;
    }
	
	public UserBean getSessionUserBean() {
		if(!session.containsKey("userBean"))
			this.setUserBean(new UserBean());
		return (UserBean) session.get("userBean");
	}

	public void setSessionUserBean(UserBean userBean) {
		this.session.put("userBean", userBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	
}
