package za.org.rfm.beans;

import org.primefaces.event.RowEditEvent;
import za.org.rfm.model.SystemVar;
import za.org.rfm.service.SystemVarService;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/16
 * Time: 2:52 PM
 */
@ViewScoped
@ManagedBean
public class SystemVarBean extends SuperBean {
    @ManagedProperty(value="#{SystemVarService}")
    SystemVarService systemVarService;
    List<SystemVar> vars;
    SystemVar systemVar;

    public SystemVar getSystemVar() {
        return systemVar;
    }

    public void setSystemVar(SystemVar systemVar) {
        this.systemVar = systemVar;
    }

    public List<SystemVar> getVars() {
        return vars;
    }

    public void setVars(List<SystemVar> vars) {
        this.vars = vars;
    }

    public SystemVarService getSystemVarService() {
        return systemVarService;
    }

    public void setSystemVarService(SystemVarService systemVarService) {
        this.systemVarService = systemVarService;
    }
    public void save(){
        systemVarService.saveSystemVar(systemVar);
        FacesMessage msg = new FacesMessage("Variable Created Successfully :", systemVar.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        systemVar = new SystemVar(); //reset model
        vars = systemVarService.getAllVars();
    }
    @PostConstruct
    public void init() {
          vars = systemVarService.getAllVars();
          logger.debug("System vars loaded...."+vars.size());
        systemVar = new SystemVar();
    }
    public void onRowEdit(RowEditEvent event) {
        SystemVar var = (SystemVar) event.getObject();
        systemVarService.saveSystemVar(var);
        FacesMessage msg = new FacesMessage("Variable Edited :", var.getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled : ", ((SystemVar) event.getObject()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


}
