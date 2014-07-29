package za.org.rfm.controllers;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import za.org.rfm.model.Account;
import za.org.rfm.model.Member;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/23
 * Time: 8:48 PM
 */
@RestController
@RequestMapping("/ws/member")
public class MemberController {
    private static final Logger logger = Logger.getLogger(MemberController.class);
    @Autowired
    MemberService memberService;

    public MemberService getMemberService() {
        return memberService;
    }

    public void setMemberService(MemberService memberService) {
        this.memberService = memberService;
    }

    ObjectMapper mapper = new ObjectMapper();


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    String getMember(@PathVariable String id) {
         try {
             mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
            Member member = memberService.getMemberById(Long.parseLong(id));
            AnnotationIntrospector introspector
                    = new JaxbAnnotationIntrospector();
            mapper.setAnnotationIntrospector(introspector);
            return mapper.writeValueAsString(member);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "Requested Resource Not found";
    }
    @RequestMapping(value = "all/{assemblyId}", method = RequestMethod.GET)
    public
    String getMembersByAssembly(@PathVariable String assemblyId) {
        try {
            mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
            List<Member> members = memberService.getMembersByAssembly(Long.parseLong(assemblyId));
            AnnotationIntrospector introspector
                    = new JaxbAnnotationIntrospector();
            mapper.setAnnotationIntrospector(introspector);
            return mapper.writeValueAsString(members);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "Requested Resource Not found";
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String createMember(@RequestBody JSONObject json) {
        try {
            logger.info("Now creating the member object...."+json);
            Member member = mapper.readValue(json.toString(),Member.class);
            if(member != null){
               if(!StringUtils.isEmpty(member.getFirstName())) {
                    if(!StringUtils.isEmpty(member.getLastName())){
                        if(!StringUtils.isEmpty(member.getPhone())){
                            member.setDateCreated(new Date());
                            member.setStatus(Constants.STATUS_ACTIVE);
                            Account account = new Account();
                            account.setMember(member);
                            member.setAccount(account);
                            Utils.capitaliseMember(member);
                            memberService.saveMember(member);
                            logger.info("Member added successfully into db");
                        }
                        return "message:Error - Phone number is  empty";
                    }
                   return "message:Error - Last Name is  empty";
                }
                return "message:Error - Phone number is null empty";

            }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            return "Encountered during processing ";
        }

         return "";
    }

    /*@RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public String deleteCustomer(@PathVariable String id) {
        Customer customer = customerService.getCustomerById(Long.parseLong(id));
        if(customer == null)
            return "Customer with id "+id+" cannot be found in db";
        customerService.deleteCustomer(customer);
        logger.info("Customer deleted ");
        return "Customer "+customer.getName()+" deleted succesfully from db";
    }*/
}
