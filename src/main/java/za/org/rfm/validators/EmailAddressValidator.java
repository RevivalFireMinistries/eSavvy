package za.org.rfm.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/07
 * Time: 1:12 PM
 */
@FacesValidator(value = "emailAddressValidator")
public class EmailAddressValidator implements Validator {

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        String email = String.valueOf(value);
        boolean valid = true;
        if (value == null) {
            valid = false;
        } else if (!email.contains("@")) {
            valid = false;
        } else if (!email.contains(".")) {
            valid = false;
        } else if (email.contains(" ")) {
            valid = false;
        }
        if (!valid) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Invalid email address",
                    "The email address you entered is not valid.");
            throw new ValidatorException(message);
        }
    }
}
