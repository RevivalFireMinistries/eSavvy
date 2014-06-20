package za.org.rfm.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.org.rfm.model.Member;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Utils;
import za.org.rfm.utils.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 3:56 PM
 */
@Component
@FacesConverter(forClass = Member.class,value="memberConverter")
public class MemberConverter implements Converter {

    private static MemberService memberService;

    @Autowired
    private MemberService tmpMemberService;
    public List<Member> memberList ;



    @PostConstruct
    public void init() {
        memberService = tmpMemberService;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
        try {
            if(submittedValue.trim().equals("")){
                return null;
            }else{
                Long id = Long.parseLong(submittedValue);
                memberList = memberService.getMembersByAssembly(WebUtil.getUserAssemblyId());
                for(Member member: memberList){
                    if(member.getId() == id ){
                        return member;
                    }
                }
                return null;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Utils.addFacesMessage("Conversion error, invalid member", FacesMessage.SEVERITY_ERROR);
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Member) value).getId());
        }
    }


}
