package za.org.rfm.beans;

import za.org.rfm.model.Assembly;
import za.org.rfm.service.AssemblyService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/19
 * Time: 11:18 AM
 */
@ManagedBean(name = "viewMemberBean")
@ViewScoped
public class ViewAssembly {
    Assembly assembly;
    @ManagedProperty(value = "#{viewAssembly}")
    AssemblyService assemblyService;

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public Assembly getAssembly() {
        return assembly;
    }

    public void setAssembly(Assembly assembly) {
        this.assembly = assembly;
    }

    @PostConstruct
    public void init() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String assemblyId = facesContext.getExternalContext().getRequestParameterMap().get("assemblyid");
            if(assemblyId == null){
                facesContext.getExternalContext().responseSendError(401,"Invalid assembly id specified");
            }
            setAssembly(assemblyService.getAssemblyById(Long.parseLong(assemblyId)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
