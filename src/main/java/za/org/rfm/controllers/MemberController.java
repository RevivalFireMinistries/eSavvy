package za.org.rfm.controllers;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import za.org.rfm.model.*;
import za.org.rfm.model.json.J_Event;
import za.org.rfm.model.json.J_Member;
import za.org.rfm.service.*;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/23
 * Time: 8:48 PM
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    private static final Logger logger = Logger.getLogger(MemberController.class);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.messages_en_ZA", Locale.getDefault());
    @Autowired
    MemberService memberService;
    @Autowired
    EventService eventService;
    @Autowired
    AssemblyService assemblyService;
    @Autowired
    EmailService emailService;
    @Autowired
    SystemVarService systemVarService;

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
            AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
            mapper.setAnnotationIntrospector(introspector);
            logger.info("Returning a list of members : "+members.size());
            logger.info(mapper.writeValueAsString(members));
            return mapper.writeValueAsString(members);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "Requested Resource Not found";
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> createMember(@RequestBody JSONObject json) {
        ResponseEntity<String> responseEntity= null;
        try {

            logger.info("Now creating the member object...."+json);
            J_Member j_member = mapper.readValue(json.toString(),J_Member.class);
            Assembly assembly = assemblyService.getAssemblyById(Long.parseLong(j_member.getAssemblyId()));
                if( j_member != null){
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
                    logger.info("message:Success - Member saved successfully");
                    responseEntity =  new ResponseEntity<String>(HttpStatus.CREATED);
                }else{
                   responseEntity = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
                    logger.error("Error : member is null");
               }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            responseEntity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        finally {
            return responseEntity;
        }

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
    @RequestMapping(value = "report/apostolic/monthly", method = RequestMethod.GET)
    public String generateApostolicMonthly() {

        emailService.apostolicSundayMonthlyReport();

        return "Done!";
    }
    @RequestMapping(value = "report/apostolic/weekly", method = RequestMethod.GET)
    public String generateApostolicWeekly() {
        String apostolicEmail = (systemVarService.getSystemVarByNameUnique(Constants.APOSTOLIC_EMAIL)).getValue();
        if(org.apache.commons.lang.StringUtils.isEmpty(apostolicEmail)){
            logger.error("Apostolic Email not found please configure immediately");
        }
        List<Event> finalEvents = new ArrayList<Event>();
        List<Assembly> assemblyList = assemblyService.getAssemblyList(Constants.STATUS_ACTIVE);
        List<Assembly> missingReports = new ArrayList<Assembly>();
        for(Assembly a : assemblyList){
            List<Event> events = eventService.getEventsByAssemblyAndTypeAndDate(a.getAssemblyid(), Constants.SERVICE_TYPE_SUNDAY, new Timestamp(Utils.calcLastSunday(new Date()).getTime()));
            if(events != null && !events.isEmpty()){
                Event event = events.get(0);
                finalEvents.add(event);
                a.setLatestAttendance(event.getAttendance());
                a.setLatestOffering(event.getOfferings());
                a.setLatestTithe(event.getTithes());
            }
            else{      //no event report so set all to zero
                a.setLatestTithe(0);
                a.setLatestAttendance(0);
                a.setLatestOffering(0);
                //now send the missing report alert!
                a.setUsers(assemblyService.getAssemblyUsers(a.getAssemblyid()));
                for(User user : a.getUsers()){
                    emailService.sendNotification(user,resourceBundle.getString("email.subject.missing.report"),resourceBundle.getString("email.body.missing.report"));
                }
                missingReports.add(a);
                logger.info("Missing report for assembly : "+a.getName()+"  - alerts have been sent!");
            }
            assemblyService.saveAssembly(a);
        }
        if(!missingReports.isEmpty()){
            StringBuilder msgList = new StringBuilder();
            String[] list = new String[missingReports.size()];
            int count = 0;
            for(Assembly a : missingReports){
               list[count] = count+1+". "+a;
                count++;
            }

            emailService.sendNotification(apostolicEmail,"ASSEMBLIES WITH MISSING REPORTS!!!",list);
        }

        emailService.apostolicSundayWeeklyReport(finalEvents);
        return "Done!";
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
