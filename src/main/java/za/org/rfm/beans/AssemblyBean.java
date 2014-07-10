package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/11
 * Time: 11:35 AM
 */
@ManagedBean(name = "assemblyBean")
@ViewScoped
public class AssemblyBean {
    private static Logger logger = Logger.getLogger(AssemblyBean.class.getName());
    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    private Assembly assembly;
    private boolean skip;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    List<Member> memberList;
    Map<String,String> countries;

    public Map<String, String> getCountries() {
        return countries;
    }

    public void setCountries(Map<String, String> countries) {
        this.countries = countries;
    }

    String country;

    public String getCountry() {
        return Utils.getCountryFriendlyName(this.country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @PostConstruct
    public void init() {
        memberList = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
        assembly = new Assembly();
       // countryList = Utils.getAllCountries();
        countries = Utils.getCountriesMap();
    }





    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

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

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }




    public String onFlowProcess(FlowEvent event) {
        logger.info("Current wizard step:" + event.getOldStep());
        logger.info("Next step:" + event.getNewStep());

        if(skip) {
            skip = false;	//reset in case user goes back
            return "confirm";
        }
        else {
            return event.getNewStep();
        }
    }
    public void save(ActionEvent actionEvent){
       assembly = getAssembly();
       assembly.setStatus(Constants.STATUS_ACTIVE);
        assembly.setLocale(country);
        assemblyService.saveAssembly(assembly);
        Utils.addFacesMessage("Assembly :"+assembly.getName()+" has been saved", FacesMessage.SEVERITY_INFO);
        try {
            String url = "viewAssembly.faces?assemblyid="+assembly.getAssemblyid();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening assembly view :"+e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

}
