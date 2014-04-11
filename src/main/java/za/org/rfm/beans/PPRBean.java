package za.org.rfm.beans;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/18
 * Time: 2:23 PM
 */
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;


@ManagedBean(name="pprBean")
@ApplicationScoped
public class PPRBean implements Serializable {

    private String firstname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
