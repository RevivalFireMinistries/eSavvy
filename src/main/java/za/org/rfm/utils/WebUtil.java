package za.org.rfm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.User;
import za.org.rfm.model.UserRole;
import za.org.rfm.service.UserService;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:56 AM
 */
@Component
public class WebUtil {
    private static UserService userService;
    @Autowired
    private UserService tmpUserService;
    @PostConstruct
    public void init() {
        userService  = tmpUserService;
    }

    public static HttpSession getSession(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (HttpSession) facesContext.getExternalContext().getSession(false);
    }
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }

    public static String getUserName()
    {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        Object obj = session.getAttribute("username");
        if(obj != null)
            return obj.toString();
        return null;
    }
      //FIXME:This doesn't make sense!!!
  /*  public static String getUserId()
    {
        HttpSession session = getSession();
        if ( session != null )
            return (String) session.getAttribute("userid");
        else
            return null;
    }*/


    public static Long getUserAssemblyId(){
        HttpSession session = getSession();
        if ( session != null )  {
            Assembly assembly = (Assembly)session.getAttribute("assembly");
            return  assembly.getAssemblyid();
        }
        else
            return null;
    }

    public static User getCurrentUser(){
        return userService.getUser(getUserName());
    }

    public static List<Role> getCurrentUserRoles(){
        List<Role> roles = new ArrayList<Role>();
        User user = getCurrentUser();
        for(UserRole userRole : user.getUserRoles()){
           roles.add(userRole.getRoleAsEnum());
        }
        return roles;
    }
}
