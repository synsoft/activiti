package de.pickert.bpmn.login;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.explorer.Constants;
import org.activiti.explorer.identity.LoggedInUser;
import org.activiti.explorer.identity.LoggedInUserImpl;
import org.activiti.explorer.ui.login.LoginHandler;


import de.pickert.bpmn.identity.rqm.RqmGroupManager;
import de.pickert.bpmn.identity.rqm.RqmUserManager;

/**
 * Default login handler, using activiti's {@link IdentityService}.
 * 
 * @author Frederik Heremans
 */
public class RqmLoginHandler implements LoginHandler {

  private IdentityService identityService;
  private RqmUserManager rqmUserManager;
  private RqmGroupManager rqmGroupManager;

  public LoggedInUserImpl authenticate(String userName, String password) {
    LoggedInUserImpl loggedInUser = null;
    if (identityService.checkPassword(userName, password)) {
    	
    	
      User user = rqmUserManager.findUserById(userName);
      //System.out.println("RqmLoginHandler we are getting user from rqm database service: " + user);
      // Fetch and cache user data
      loggedInUser = new LoggedInUserImpl(user, password);
      
      //System.out.println("loggedInUser user: " + loggedInUser.getId() + " pass: " + loggedInUser.getPassword() );
      
      
      
      ////////////////////////////////
      
      
      //TODO here we can add some restrictions for some groups
      // currently no implementation
      //loggedInUser.addSecurityRoleGroup("Admin");
      loggedInUser.setAdmin(true);
      loggedInUser.setUser(true);
      
      
      
      //////////////////////////////
      
      
      /////////////////////////////
// 		TODO ADD explorer groups depending of rqm groups
//		List<Group> groups = rqmGroupManager.findGroupsByUser(userName);
//		System.out.println("groups size: "  + groups.size()); 

      
//      for (Group group : groups) {
//        if (Constants.SECURITY_ROLE.equals(group.getType())) {
//          loggedInUser.addSecurityRoleGroup(group);
//          if (Constants.SECURITY_ROLE_USER.equals(group.getId())) {
//            loggedInUser.setUser(true);
//          }
//          if (Constants.SECURITY_ROLE_ADMIN.equals(group.getId())) {
//            loggedInUser.setAdmin(true);
//          }
//        } else {
//          loggedInUser.addGroup(group);
//        }
//      }
      
      
      /////////////////////////////
    }
    
    return loggedInUser;
  }
  
  public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
    // Noting to do here
  }

  public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
    // Noting to do here
  }
  
  public LoggedInUser authenticate(HttpServletRequest request, HttpServletResponse response) {
    // No automatic authentication is used by default, always through credentials.
    return null;
  }
  
  public void logout(LoggedInUser userToLogout) {
    // Clear activiti authentication context
    Authentication.setAuthenticatedUserId(null);
  }
  
  public void setIdentityService(IdentityService identityService) {
    this.identityService = identityService;
  }

public RqmUserManager getRqmUserManager() {
	return rqmUserManager;
}

public void setRqmUserManager(RqmUserManager rqmUserManager) {
	this.rqmUserManager = rqmUserManager;
}

public RqmGroupManager getRqmGroupManager() {
	return rqmGroupManager;
}

public void setRqmGroupManager(RqmGroupManager rqmGroupManager) {
	this.rqmGroupManager = rqmGroupManager;
}

  
  
  
}
