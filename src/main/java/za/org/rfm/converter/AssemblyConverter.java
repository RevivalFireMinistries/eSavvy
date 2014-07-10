package za.org.rfm.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Assembly;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.utils.Constants;
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
 * Date: 2014/03/20
 * Time: 11:42 PM
 */
@Component
@FacesConverter(forClass = Assembly.class,value="assemblyConverter")
public class AssemblyConverter implements Converter {

   private static AssemblyService assemblyService;
   @Autowired
   private AssemblyService tmpAssemblyService;
   public  List<Assembly> assemblyList ;
    @PostConstruct
    public void init() {
       assemblyService  = tmpAssemblyService;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
        try {
            if(submittedValue.trim().equals("")){
                return null;
            }else{
                Long id = Long.parseLong(submittedValue);
                assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
                for(Assembly assembly: assemblyList){
                    if(assembly.getAssemblyid() == id ){
                        return assembly;
                    }
                }
                return null;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Conversion error, invalid assembly", FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Assembly) value).getAssemblyid());
        }
    }


}
