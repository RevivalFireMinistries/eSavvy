package za.org.rfm.controllers;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import za.org.rfm.model.Event;
import za.org.rfm.service.EventService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * User: Russel.Mupfumira
 * Date: 2014/05/15
 * Time: 4:36 PM
 */
@Controller
@RequestMapping("/report")
public class ReportsController {
    Logger logger = Logger.getLogger(ReportsController.class);

    @Autowired
    EventService eventService;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public @ResponseBody String getMovie(@PathVariable String name) {
             String result = "Hello "+name;
        System.out.println("i got to the web service hey!");
        return result;

    }
    @RequestMapping(value = "/report/add", method = RequestMethod.POST)
    public @ResponseBody
    Event createEvent(@RequestBody Event event) {
        System.out.println("Start createEvent.");
        event.setEventDate(new Timestamp(System.currentTimeMillis()));
        System.out.println(" Event type:"+event.getEventType()+" tithes " +
                ":"+event.getTithes()+" offering :"+event.getOfferings()+" attendance "+event.getAttendance());
        return event;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String createEvent(@RequestBody JSONObject json) {
        try {
            logger.info("Now creating the member object...."+json);
            Event event = mapper.readValue(json.toString(),Event.class);
            if(event != null){
                if(!StringUtils.isEmpty(event.getEventType())) {
                   /* if(!StringUtils.isEmpty(member.getLastName())){
                        if(!StringUtils.isEmpty(member.getPhone())){
                          *//*  member.setDateCreated(new Date());
                            member.setStatus(Constants.STATUS_ACTIVE);
                            Account account = new Account();
                            account.setMember(member);
                            member.setAccount(account);
                            Utils.capitaliseMember(member);
                            memberService.saveMember(member);*//*
                            logger.info("Member added successfully into db");
                        }
                        return "message:Error - Phone number is  empty";
                    }*/
                    return "message:Error - Last Name is  empty";
                }
                return "message:Error - event type is null";

            }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            return "Encountered during processing ";
        }

        return "";
    }
}
