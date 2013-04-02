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

import de.pickert.bpmn.identity.mock.MockUserManager;

/**
 * Mockup login handler, using fake user det  {@link MockUserManager}.
 * 
 * @author Aleksandar
 */
public class MockLoginHandler implements LoginHandler {

  private IdentityService identityService;

  public LoggedInUserImpl authenticate(String userName, String password) {
    LoggedInUserImpl loggedInUser = null;
    if (identityService.checkPassword(userName, password)) {
    	
      User user = new MockUserManager().createNewUser(userName);
      System.out.println("we are getting user from rqm mock service " + user.getId());
      // Fetch and cache user data
      loggedInUser = new LoggedInUserImpl(user, password);
      System.out.println("loggedInUser user: " + loggedInUser.getId() + "pass: " + loggedInUser.getPassword() );
      List<Group> groups = identityService.createGroupQuery().groupMember(user.getId()).list();
      System.out.println("groups size: "  + groups.size()); 
      
      for (Group group : groups) {
        if (Constants.SECURITY_ROLE.equals(group.getType())) {
          loggedInUser.addSecurityRoleGroup(group);
          if (Constants.SECURITY_ROLE_USER.equals(group.getId())) {
            loggedInUser.setUser(true);
          }
          if (Constants.SECURITY_ROLE_ADMIN.equals(group.getId())) {
            loggedInUser.setAdmin(true);
          }
        } else {
          loggedInUser.addGroup(group);
        }
      }
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

}
