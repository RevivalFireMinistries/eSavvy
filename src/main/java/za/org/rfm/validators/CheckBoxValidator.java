package za.org.rfm.validators;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/10
 * Time: 10:35 AM
 */
@FacesValidator(value = "checkBoxValidator")
public class CheckBoxValidator implements Validator{
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

    }
}
