package za.org.rfm.beans;

import za.org.rfm.model.Member;
import za.org.rfm.model.SMSLog;
import za.org.rfm.model.Transaction;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SMSService;
import za.org.rfm.service.TxnService;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/28
 * Time: 11:10 AM
 */
@ManagedBean(name = "viewMemberBean")
@ViewScoped
public class ViewMemberBean {
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{SMSService}")
    SMSService smsService;
    @ManagedProperty(value = "#{TxnService}")
    TxnService txnService;

    public TxnService getTxnService() {
        return txnService;
    }

    public void setTxnService(TxnService txnService) {
        this.txnService = txnService;
    }

    String fullName;
    Tithe tithe;
    List<Tithe> titheList = new ArrayList<Tithe>();

    public List<Tithe> getTitheList() {

        return titheList;
    }

    public void setTitheList(List<Tithe> titheList) {
        this.titheList = titheList;
    }

    public String getFullName() {
        return getMember().getFullName();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public SMSService getSmsService() {
        return smsService;
    }

    public void setSmsService(SMSService smsService) {
        this.smsService = smsService;
    }

    String sms;

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    private Member member;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    private String[] memberTypes = Utils.getMemberTypes();

    public String[] getMemberTypes() {
        return memberTypes;
    }
    private List<String> memberStates = Utils.getStates();

    public List<String> getMemberStates() {
        return memberStates;
    }

    public void saveMember(){
       memberService.saveMember(this.member);
        Utils.addFacesMessage("Changes have been saved",FacesMessage.SEVERITY_INFO);
    }

    @PostConstruct
    public void init() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String memberid = facesContext.getExternalContext().getRequestParameterMap().get("memberid");
        setMember(memberService.getMemberById(Long.parseLong(memberid)));
        System.out.println("---the loaded member ----"+memberid);
        List<Transaction> transactions = txnService.getTithesByMemberAndDateRange(this.member,null,null);
        for(Transaction txn: transactions){
            titheList.add(new Tithe(this.member,txn.getAmount(),txn.getTxndate()));
        }
    }

    public void sendSMS(){
        SMSLog smsLog = getMember().sendSMS(getSms(), false);
        smsService.saveSMSLog(smsLog);
        System.out.println("---sms has been sent");
        Utils.addFacesMessage("SMS Status "+smsLog.getStatus(), FacesMessage.SEVERITY_INFO);
    }
}
