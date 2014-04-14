package za.org.rfm.converter;

import org.springframework.beans.factory.annotation.Autowired;
import za.org.rfm.model.Assembly;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
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
@ManagedBean(name = "assemblyCon")
@FacesConverter(forClass = Assembly.class,value="assemblyConverter")
public class AssemblyConverter implements Converter {
    @Autowired
    AssemblyService assemblyService;
   public  List<Assembly> assemblyList = assemblyService.getAssemblyList();



    @PostConstruct
    public void init() {
        assemblyList = assemblyService.getAssemblyList();
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
        try {
            if(submittedValue.trim().equals("")){
                return null;
            }else{
                Long id = Long.parseLong(submittedValue);
                for(Assembly assembly: assemblyService.getAssemblyList()){
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
