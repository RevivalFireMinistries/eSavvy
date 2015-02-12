package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import za.org.rfm.dto.AssemblyMonthlyAttendanceTotals;
import za.org.rfm.dto.MemberMonthlyTitheTotals;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Transaction;
import za.org.rfm.model.User;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EventService;
import za.org.rfm.service.TxnService;
import za.org.rfm.service.UserService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/19
 * Time: 11:18 AM
 */
@ManagedBean(name = "viewAssemblyBean")
@ViewScoped
public class ViewAssembly {
    Logger logger = Logger.getLogger(ViewAssembly.class);
    Assembly assembly;
    List<User> userList;
    User selectedUser;
    List<User> filteredUsers;
    List<MemberMonthlyTitheTotals> memberMonthlyTitheTotalsList;
    MemberMonthlyTitheTotals selectedMemberMonthlyTitheTotals;
    List<AssemblyMonthlyAttendanceTotals> assemblyMonthlyAttendanceTotalsList;
    @ManagedProperty(value = "#{TxnService}")
    TxnService txnService;
    public double[] totalTithe = new double[12];
    public double[] totalOffering = new double[12];
    public double[] totalIncome = new double[12];
    public double[] totalApostolic= new double[12];

    public double[] getTotalTithe() {
        return totalTithe;
    }

    public void setTotalTithe(double[] totalTithe) {
        this.totalTithe = totalTithe;
    }

    public double[] getTotalOffering() {
        return totalOffering;
    }

    public void setTotalOffering(double[] totalOffering) {
        this.totalOffering = totalOffering;
    }

    public double[] getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double[] totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double[] getTotalApostolic() {
        return totalApostolic;
    }

    public void setTotalApostolic(double[] totalApostolic) {
        this.totalApostolic = totalApostolic;
    }

    public TxnService getTxnService() {
        return txnService;
    }

    public List<AssemblyMonthlyAttendanceTotals> getAssemblyMonthlyAttendanceTotalsList() {
        return assemblyMonthlyAttendanceTotalsList;
    }

    public void setAssemblyMonthlyAttendanceTotalsList(List<AssemblyMonthlyAttendanceTotals> assemblyMonthlyAttendanceTotalsList) {
        this.assemblyMonthlyAttendanceTotalsList = assemblyMonthlyAttendanceTotalsList;
    }

    public void onMemberTitheViewRowSelect(SelectEvent event){
        List<Transaction> transactionList = txnService.getTithesByMemberAndDateRange(selectedMemberMonthlyTitheTotals.getMember(),null);
        selectedMemberMonthlyTitheTotals.getMember().setTransactionList(transactionList);
    }

    public void setTxnService(TxnService txnService) {
        this.txnService = txnService;
    }

    public MemberMonthlyTitheTotals getSelectedMemberMonthlyTitheTotals() {
        return selectedMemberMonthlyTitheTotals;
    }

    public void setSelectedMemberMonthlyTitheTotals(MemberMonthlyTitheTotals selectedMemberMonthlyTitheTotals) {
        this.selectedMemberMonthlyTitheTotals = selectedMemberMonthlyTitheTotals;
    }

    public List<MemberMonthlyTitheTotals> getMemberMonthlyTitheTotalsList() {
        return memberMonthlyTitheTotalsList;
    }

    public void setMemberMonthlyTitheTotalsList(List<MemberMonthlyTitheTotals> memberMonthlyTitheTotalsList) {
        this.memberMonthlyTitheTotalsList = memberMonthlyTitheTotalsList;
    }

    public List<User> getFilteredUsers() {
        return filteredUsers;
    }

    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @ManagedProperty(value="#{AssemblyService}")
    AssemblyService assemblyService;
    @ManagedProperty(value="#{UserService}")
    UserService userService;
    @ManagedProperty(value="#{EventService}")
    EventService eventService;

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
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

    @PostConstruct
    public void init() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String id = facesContext.getExternalContext().getRequestParameterMap().get("assemblyid");
            if(id == null){
                facesContext.getExternalContext().responseSendError(401,"Invalid assembly id specified");
            }
            long assemblyId = Long.parseLong(id);
            //load this assembly's users as well
            userList = userService.getUsersByAssembly(assemblyId);
            setAssembly(assemblyService.getAssemblyById(assemblyId));
            memberMonthlyTitheTotalsList= assemblyService.getMemberMonthlyTitheTotals(assemblyId);
            assemblyMonthlyAttendanceTotalsList= eventService.getAssemblyMonthlyAttendance(assemblyId);
            computeTotals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveAssembly(){
        assemblyService.saveAssembly(this.assembly);
        Utils.addFacesMessage("Changes have been saved", FacesMessage.SEVERITY_INFO);
    }
    private List<String> assemblyStates = Utils.getStates();

    public List<String> getAssemblyStates() {
        return assemblyStates;
    }
    public void newUser(){
        try {
            String url = "users/newUser.faces";
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening new user form :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }
    public void onRowSelect(SelectEvent event){
        try {
            User selectedUser = (User)event.getObject();
            String url = "/users/viewUser.faces?username="+selectedUser.getUsername();
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Error opening user view :" + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void onCellClicked(ActionEvent event){
        logger.info("cell clicked!!!");
    }//TODO:Need to add a popup for viewing more on tithe txns

    private void computeTotals(){

        for(int i=0;i<12;i++){
            for(MemberMonthlyTitheTotals mmt : getMemberMonthlyTitheTotalsList()){
                 totalTithe[i] +=mmt.totals[i];
            }

            totalOffering[i] = assemblyService.getMonthlyOffering(assembly.getAssemblyid(),i);
            totalIncome[i] = totalTithe[i] + totalOffering[i];
            totalApostolic[i] = totalIncome[i]* Constants.APOSTOLIC_CONTRIBUTION_PERCENTAGE;
        }

    }

}
