package za.org.rfm.beans;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import za.org.rfm.model.Member;
import za.org.rfm.model.MemberGroup;
import za.org.rfm.model.SMSLog;
import za.org.rfm.model.Transaction;
import za.org.rfm.service.MemberService;
import za.org.rfm.service.SMSService;
import za.org.rfm.service.TxnService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.DateRange;
import za.org.rfm.utils.Group;
import za.org.rfm.utils.Utils;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/28
 * Time: 11:10 AM
 */
@ManagedBean(name = "viewMemberBean")
@ViewScoped
public class ViewMemberBean {
    Logger logger = Logger.getLogger(ViewMemberBean.class);
    @ManagedProperty(value="#{MemberService}")
    MemberService memberService;
    @ManagedProperty(value="#{SMSService}")
    SMSService smsService;
    @ManagedProperty(value = "#{TxnService}")
    TxnService txnService;
    private DateRange dateRange;
    public boolean tithesEmpty;
    List<Group> target;
    List<MemberGroup> memberGroupList;

    public List<MemberGroup> getMemberGroupList() {
        return memberGroupList;
    }

    public void setMemberGroupList(List<MemberGroup> memberGroupList) {
        this.memberGroupList = memberGroupList;
    }

    public List<Group> getSource() {
        return source;
    }

    public void setSource(List<Group> source) {
        this.source = source;
    }

    public List<Group> getTarget() {
        return target;
    }

    public DualListModel<Group> getGroups() {
        return groups;
    }

    public void setGroups(DualListModel<Group> groups) {
        this.groups = groups;
    }

    public void setTarget(List<Group> target) {

        this.target = target;
    }

    List<Group> source;
    private DualListModel<Group> groups;

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    private Date maxDate = new Date(System.currentTimeMillis());

    public Tithe getTithe() {
        return tithe;
    }

    public void setTithe(Tithe tithe) {
        this.tithe = tithe;
    }

    public boolean isTithesEmpty() {
        return tithesEmpty;
    }

    public void setTithesEmpty(boolean tithesEmpty) {
        this.tithesEmpty = tithesEmpty;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }
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
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String memberid = facesContext.getExternalContext().getRequestParameterMap().get("memberid");
            if(memberid == null){
                facesContext.getExternalContext().responseSendError(401,"Invalid member id specified");
            }
            long id = Long.parseLong(memberid);
            setMember(memberService.getMemberById(id));
            initializeDateRange();
            memberGroupList = memberService.getMemberGroups(id);
            initializeGroups();
            tithe = new Tithe();
            setTithesEmpty(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGroups() {
        source = Utils.getGroupsAsList();
        target = new ArrayList<Group>();
        for(MemberGroup memberGroup : memberGroupList){
            target.add(Group.valueOf(memberGroup.getGroupName()));
            source.remove(Group.valueOf(memberGroup.getGroupName()));
        }
        logger.debug("Groups initialised - source :"+source.size()+" target : "+target.size());
        groups = new DualListModel<Group>(source, target);
    }
    public void search() {

        List<Transaction> transactions = txnService.getTithesByMemberAndDateRange(this.member, this.getDateRange());
        titheList.clear();
        for(Transaction txn: transactions){
            titheList.add(new Tithe(this.member,txn.getAmount(),txn.getTxndate()));
        }
        Utils.addFacesMessage(transactions.size()+" results found ",FacesMessage.SEVERITY_INFO);
        setTithesEmpty(titheList.isEmpty());
    }
    public void sendSMS(){
        SMSLog smsLog = getMember().sendSMS(getSms(), false);
        smsService.saveSMSLog(smsLog);
        Utils.addFacesMessage("SMS Status "+smsLog.getStatus(), FacesMessage.SEVERITY_INFO);
    }
    public void onTransfer(TransferEvent event) {
       logger.debug("Found "+getGroups().getTarget().size()+" groups to be saved");
        setMemberGroupList(updateGroupStatusAndSave());
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Changes to groups saved successfully");
        FacesContext.getCurrentInstance().addMessage(null, msg);
         initializeGroups();
    }

    private List<MemberGroup> updateGroupStatusAndSave() {
        //first get this member's groups from db
        List<MemberGroup> oldGroups = memberService.getMemberGroups(this.member.id);
        List<MemberGroup> finalList = new ArrayList<MemberGroup>();
        MemberGroup memberGroup;
        if(oldGroups.isEmpty()){
            //just create new groups from the selected ones then save and exit
            for(Group group : getGroups().getTarget()){
                memberGroup = new MemberGroup();
                memberGroup.setStatus(Constants.STATUS_ACTIVE);
                memberGroup.setDateCreated(new Date());
                memberGroup.setGroupName(group.name());
                memberGroup.setMember(this.member);
                memberService.saveMemberGroup(memberGroup);
                finalList.add(memberGroup);
            }
            return finalList;
        }else{
            //there are groups existing - first check what happened to them after selection
            for(MemberGroup memberGroup1 : oldGroups){
                if(!getGroups().getTarget().contains(Group.valueOf(memberGroup1.getGroupName()))){
                   memberGroup1.setStatus(Constants.STATUS_DELETED);//mark as deleted and save
                    memberService.saveMemberGroup(memberGroup1);
                } else{
                    //they were not changed - so put into final
                    finalList.add(memberGroup1);
                }
            }
        }  //Now we need to deal with the new ones
        boolean found = false;
        for(Group group : getGroups().getTarget()){
            for(MemberGroup mg: oldGroups){
                if(group.name().equalsIgnoreCase(mg.getGroupName())){
                   found = true;      //its not new anyway so skip!
                }
            }
            if(!found){
               //this is a new selection  - so create memberGrp and save to db
                memberGroup = new MemberGroup();
                memberGroup.setStatus(Constants.STATUS_ACTIVE);
                memberGroup.setDateCreated(new Date());
                memberGroup.setGroupName(group.name());
                memberGroup.setMember(this.member);
                memberService.saveMemberGroup(memberGroup);
                finalList.add(memberGroup);
            }
            found = false;
        }
        return finalList;
    }

    public void enterTithe(){
        logger.debug("---now processing tithe---");
        getTithe().setMember(getMember());
        logger.debug("Tithe "+tithe.getMember().getFullName()+" amt "+tithe.getAmount()+" date "+tithe.getTxnDate());
        txnService.processTithe(getTithe());
        Utils.addFacesMessage("Tithe entered succesfully",FacesMessage.SEVERITY_INFO);
    }
    private void initializeDateRange() {

        DateTime firstDayOfMonth = new DateTime().dayOfMonth().withMinimumValue();
        Date today = new Date(System.currentTimeMillis());
        this.dateRange = new DateRange(firstDayOfMonth.toDate(),today);

    }
}
