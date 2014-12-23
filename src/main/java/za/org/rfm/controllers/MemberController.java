package za.org.rfm.controllers;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import za.org.rfm.model.Account;
import za.org.rfm.model.Assembly;
import za.org.rfm.model.Event;
import za.org.rfm.model.Member;
import za.org.rfm.model.json.J_Event;
import za.org.rfm.model.json.J_Member;
import za.org.rfm.service.AssemblyService;
import za.org.rfm.service.EmailService;
import za.org.rfm.service.EventService;
import za.org.rfm.service.MemberService;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/23
 * Time: 8:48 PM
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    private static final Logger logger = Logger.getLogger(MemberController.class);
    @Autowired
    MemberService memberService;
    @Autowired
    EventService eventService;
    @Autowired
    AssemblyService assemblyService;
    @Autowired
    EmailService emailService;

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
            logger.info("REST Call to get members for assembly with id :"+assemblyId);
            mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
            List<Member> members = memberService.getMembersByAssembly(Long.parseLong(assemblyId));
            AnnotationIntrospector introspector
                    = new JaxbAnnotationIntrospector();
            mapper.setAnnotationIntrospector(introspector);
            logger.info("Returning a list of members : "+members.size());
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
            J_Member j_member = mapper.readValue(json.toString(),J_Member.class);
            if(j_member != null){
               if(!StringUtils.isEmpty(j_member.getFirstName())) {
                    if(!StringUtils.isEmpty(j_member.getLastName())){
                        if(!StringUtils.isEmpty(j_member.getPhone())){
                            Assembly assembly = assemblyService.getAssemblyById(Long.parseLong(j_member.getAssemblyId()));
                            if(assembly != null){
                                Member member = new Member(j_member);
                                member.setAssembly(assembly);
                                member.setDateCreated(new Date());
                                member.setStatus(Constants.STATUS_ACTIVE);
                                Account account = new Account();
                                account.setMember(member);
                                member.setAccount(account);
                                Utils.capitaliseMember(member);
                                memberService.saveMember(member);
                                memberService.addToEveryone(member);
                                logger.info("Member added successfully into db");
                            }

                        }
                        return "message:Error - Phone number is  empty";
                    }
                   return "message:Error - Last Name is  empty";
                }
                return "message:Error - Member object is null";

            }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            return "Encountered during processing ";
        }

         return "";
    }

    @RequestMapping(value = "event/{id}", method = RequestMethod.GET)
    public
    String getEvent(@PathVariable String id) {
        try {
            logger.info("Retrieving event with id : "+id);
            mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
            Event event = eventService.getEventById(Long.parseLong(id));
            AnnotationIntrospector introspector
                    = new JaxbAnnotationIntrospector();
            mapper.setAnnotationIntrospector(introspector);
            return mapper.writeValueAsString(event);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());

        }
        return "";
    }
    @RequestMapping(value = "event/add", method = RequestMethod.POST)
    public String createEvent(@RequestBody JSONObject json) {
        String msg = "";
        try {
            logger.info("Now creating the event object...."+json);
            J_Event j_event = mapper.readValue(json.toString(),J_Event.class);
            if(j_event != null){
                final Event event = new Event(j_event);
                if(!StringUtils.isEmpty(event.getEventType())) {
                    if(event.getEventDate() == null){
                        event.setEventDate(new Timestamp(Utils.calcLastSunday(new Date()).getTime()));
                        logger.warn("No event date set...using last sunday");
                    }
                    if(StringUtils.isEmpty(j_event.getAssemblyId())){
                        msg = "message: Event is missing assembly id";
                        logger.error(msg);
                    }else{
                        Assembly assembly = assemblyService.getAssemblyById(Long.parseLong(j_event.getAssemblyId()));
                        if(assembly != null){
                            event.setAssembly(assembly);
                            eventService.saveEvent(event);
                            logger.info("Event saved successfully");
                            //Now send an email to pastoral! - but spawn a new thread to separate execution
                            Thread emailThread = new Thread(){
                                @Override
                                public void run() {
                                    emailService.eventReport(event);
                                }
                            };
                            emailThread.start();
                        }else{
                            logger.error("Error - assembly not found : "+j_event.getAssemblyId());
                        }

                    }

                }
                return "message:success -Content-type: application/json; ";
            }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            return "Encountered during processing ";
        }

        return "";
    }
}
