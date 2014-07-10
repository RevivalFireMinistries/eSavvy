package za.org.rfm.validators;

import org.springframework.beans.factory.annotation.Autowired;
import za.org.rfm.model.User;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.WebUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/08
 * Time: 1:48 AM
 */
@FacesValidator(value = "oldPasswordValidator")
public class OldPasswordValidator implements Validator {
    @Autowired
    UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        String oldPassword = String.valueOf(o);
        System.out.println("changing password for : "+WebUtil.getUserName());
        User user = userService.getUser(WebUtil.getUserName());
        System.out.println("now fetched the user for validation...."+user.getFullname());
        boolean valid = true;
        if(user == null){
            valid = false;
            System.out.println("the user is null...");
        }
        if(user != null &&!oldPassword.equals(user.getPassword())){
            System.out.println("user pwds are not equal...");
           valid = false;
        }
        if (!valid) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Incorrect old password",
                    "The old password you entered is not valid.");
            throw new ValidatorException(message);
        }
    }
}
