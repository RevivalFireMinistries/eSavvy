package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import za.org.rfm.model.Member;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.TxnService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 3:54 PM
 */
@ManagedBean(name = "enterTitheBean")
@SessionScoped
public class EnterTitheBean {
     Logger logger = Logger.getLogger(getClass());
    private Member selectedMember;
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{TxnService}")
    TxnService txnService;
    @ManagedProperty(value="#{mailService}")
    EmailService emailService;
    String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public TxnService getTxnService() {
        return txnService;
    }

    public void setTxnService(TxnService txnService) {
        this.txnService = txnService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    private List<Member> memberList;
    private double amount;
    private Date date;
    private Date maxDate = new Date(System.currentTimeMillis());
    private List<Tithe> titheList;

    public List<Tithe> getTitheList() {
        return titheList;
    }

    public void setTitheList(List<Tithe> titheList) {
        this.titheList = titheList;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    public List<Member> getMemberList() {

        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }
    @PostConstruct
    public void init() {
        memberList = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
        titheList =  new ArrayList<Tithe>();
    }

    public List<Member> completeMember(String query){
        List<Member> suggestions = new ArrayList<Member>();
        for(Member member: memberList){
            if(member.getFirstName().toLowerCase().startsWith(query.toLowerCase())){
                suggestions.add(member);
            }
        }
        return suggestions;
    }

    public void onNameChange(SelectEvent event) {
        Member m = (Member)event.getObject();
        Member tmpmember = memberService.getMemberById(m.getId());
        setSelectedMember(tmpmember);
        setPhone(tmpmember.getPhone());
    }

    public void addTithe(){
        //first check if there isn't a similar txn already

        Tithe tithe = new Tithe(getSelectedMember(),getAmount(),getDate());
        if(!Utils.txnExists(titheList,tithe))  {
            //similar txn doesn't exist so go ahead!

            getTitheList().add(tithe);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tithe transaction queued for processing"));
            //now reset the tithe form
            clear();
        } else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate Tithe transaction!!",null));
        }

    }
    public void clear(){
        setSelectedMember(null);
        setPhone("");
        setAmount(0.0);
        setDate(null);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Tithe form cleared"));
    }

    public void onEdit(RowEditEvent rowEditEvent){
        Utils.addFacesMessage("Row edited!",FacesMessage.SEVERITY_INFO);
    }
    public void onCancel(RowEditEvent rowEditEvent){
        Utils.addFacesMessage("Editing cancelled",FacesMessage.SEVERITY_INFO);
    }
    public String deleteAction(Tithe tithe) {
        int size = titheList.size();
        for(int i=size-1;i>-1;i--){
            if(titheList.get(i).getId() == tithe.getId()){
                titheList.remove(i);
            }
        }
        Utils.addFacesMessage("Tithe entry deleted successfully",FacesMessage.SEVERITY_INFO);
        return null;
    }
    public void submitAll(){
        int size = titheList.size();
        for(final Tithe tithe: titheList){
            //FIXME: spawn a thread for each txn to avoid making the user to wait
            Thread titheThread = new Thread(){
                @Override
                public void run() {
                    //create txn object and save to db
                    txnService.processTithe(tithe);
                }
            };
            titheThread.start();

        }
        emailService.titheReport(titheList, WebUtil.getCurrentUser(), new Date());
        titheList.clear();
        Utils.addFacesMessage(size+" Tithe transactions have been processed successfully",FacesMessage.SEVERITY_INFO);
    }

    public void deleteAll(){
        int size = titheList.size();
        titheList.clear();
        clear();
        Utils.addFacesMessage(size+" Tithe transactions have been removed",FacesMessage.SEVERITY_INFO);
    }
    public boolean getFlag(){
        return !titheList.isEmpty();
    }
}