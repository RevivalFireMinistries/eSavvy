package za.org.rfm.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Role;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/15
 * Time: 2:59 PM
 */
@Component
@FacesConverter(forClass = Role.class,value="roleConverter")
public class RoleConverter implements Converter {

    private static UserService userService;
    @Autowired
    private UserService tmpUserService;
    public List<Role> roleList ;



    @PostConstruct
    public void init() {
        userService = tmpUserService;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
        try {
            if(submittedValue.trim().equals("")){
                return null;
            }else{
                Long id = Long.parseLong(submittedValue);
                roleList = userService.getRoles();
                for(Role role: roleList){
                    if(role.getId() == id ){
                        return role;
                    }
                }
                return null;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Conversion error, invalid role", FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Role) value).getId());
        }
    }

}
