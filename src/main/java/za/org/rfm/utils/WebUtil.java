package za.org.rfm.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:56 AM
 */
public class WebUtil {

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
        return  session.getAttribute("username").toString();
    }

    public static String getUserId()
    {
        HttpSession session = getSession();
        if ( session != null )
            return (String) session.getAttribute("userid");
        else
            return null;
    }

    public static Long getUserAssemblyId(){
        HttpSession session = getSession();
        if ( session != null )  {
            Long id = (Long)session.getAttribute("assembyid");
            return  id;
        }
        else
            return null;
    }
}
