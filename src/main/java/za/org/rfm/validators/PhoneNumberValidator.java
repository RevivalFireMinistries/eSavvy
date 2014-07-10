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
 * Time: 2:07 PM
 */
@FacesValidator(value = "phoneNumberValidator")
public class PhoneNumberValidator implements Validator {
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        String phoneNo = String.valueOf(o);
        boolean valid = false;
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}"))
            valid = true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
            valid = true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
            valid = true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
            valid = true;
        if (!valid) {
            FacesMessage message = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Invalid phone number",
                    "The phone number you entered is not valid.");
            throw new ValidatorException(message);
        }
    }
}
