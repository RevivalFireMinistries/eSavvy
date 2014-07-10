package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/20
 * Time: 1:37 PM
 */
@ManagedBean(name = "viewAssembliesBean")
@ViewScoped
public class ViewAssembliesBean {
    private static Logger logger = Logger.getLogger(ViewAssembliesBean.class.getName());
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    List<Assembly> assemblyList;
    private List<Assembly> filteredAssemblies;


    private Assembly selectedAssembly;

    private Assembly[] selectedAssemblies;

    public List<Assembly> getFilteredAssemblies() {
        return filteredAssemblies;
    }

    public void setFilteredAssemblies(List<Assembly> filteredAssemblies) {
        this.filteredAssemblies = filteredAssemblies;
    }

    public Assembly getSelectedAssembly() {
        return selectedAssembly;
    }

    public void setSelectedAssembly(Assembly selectedAssembly) {
        this.selectedAssembly = selectedAssembly;
    }

    public Assembly[] getSelectedAssemblies() {
        return selectedAssemblies;
    }

    public void setSelectedAssemblies(Assembly[] selectedAssemblies) {
        this.selectedAssemblies = selectedAssemblies;
    }

    public List<Assembly> getAssemblyList() {
        return assemblyList;
    }

    public void setAssemblyList(List<Assembly> assemblyList) {
        this.assemblyList = assemblyList;
    }

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    @PostConstruct
    public void init() {

        populateAssemblies();

    }
    private void populateAssemblies() {
        List<Assembly> assemblies = assemblyService.getAssemblyList(Constants.STATUS_ALL);
        setAssemblyList(assemblies);
    }

    public void onRowSelect(SelectEvent event){
        try {
            String url = "viewAssembly.faces?assemblyid="+this.getSelectedAssembly().getAssemblyid();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening assembly view :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

}
