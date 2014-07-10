package za.org.rfm.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.SystemVar;
import za.org.rfm.service.SystemVarService;
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
 * Date: 2014/07/10
 * Time: 12:51 PM
 */
@Component
@FacesConverter(forClass = SystemVar.class,value="systemVarConverter")
public class SystemVarConverter implements Converter {
    private static SystemVarService systemVarService;
    @Autowired
    private SystemVarService systemVarService1;
    public List<SystemVar> systemVarList;
    @PostConstruct
    public void init() {
        systemVarService = systemVarService1;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
        try {
            if(submittedValue.trim().equals("")){
                return null;
            }else{
                Long id = Long.parseLong(submittedValue);
                systemVarList = systemVarService.getAllVars();
                for(SystemVar var: systemVarList){
                    if(var.getId() == id ){
                        return var;
                    }
                }
                return null;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Conversion error, invalid var", FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((SystemVar) value).getId());
        }
    }
}
