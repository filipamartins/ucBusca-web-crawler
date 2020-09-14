package ucbusca.action;

import com.opensymphony.xwork2.ActionSupport;
import java.rmi.RemoteException;
import ucbusca.model.UserBean;

public class RegisterAction extends ActionSupport {
	private static final long serialVersionUID = 4L;
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
		String success = userBean.register();
		if (success.equals("false")) {
			addFieldError("userBean.password", userBean.getMessageText());
			return "fail";
		}
        return SUCCESS;
    }
    
    public UserBean getUserBean() {
        return userBean;
    }
    
    public void setUserBean(UserBean userBean) {
    	this.userBean = userBean;
    }
		
}
