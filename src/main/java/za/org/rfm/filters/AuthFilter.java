package za.org.rfm.filters;

import org.apache.log4j.Logger;
import za.org.rfm.utils.Constants;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 11:03 AM
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"*"})
public class AuthFilter implements Filter {
    Logger logger = Logger.getLogger(AuthFilter.class);
    public AuthFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {

            // check whether session variable is set
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            HttpSession ses = req.getSession(false);
            //  allow user to proceed if url is login.xhtml or user logged in or user is accessing any page in //public folder
            String reqURI = req.getRequestURI();
            if ( reqURI.indexOf("/login.xhtml") >= 0 ||reqURI.indexOf("/resetPassword.xhtml") >= 0|| (ses != null && ses.getAttribute("username") != null)
                    || reqURI.indexOf("/public/") >= 0 || reqURI.contains("javax.faces.resource") || reqURI.indexOf("/ws/") >= 0 ) {
                //System.out.println("Request allowed...."+reqURI);
                chain.doFilter(request, response);

            }

            else {
                            // user didn't log in but asking for a page that is not allowed so take user to login page but preserve url
                res.sendRedirect(req.getContextPath() + "/login.xhtml?from=" + URLEncoder.encode(reqURI, "UTF-8"));  // Anonymous user. Redirect to login page
            }

        }
        catch(Throwable t) {
            logger.error(t.getMessage());
            t.printStackTrace();
        }
    } //doFilter

    @Override
    public void destroy() {

    }
}
