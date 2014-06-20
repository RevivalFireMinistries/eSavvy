package za.org.rfm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import za.org.rfm.model.Event;

import java.sql.Timestamp;

/**
 * User: Russel.Mupfumira
 * Date: 2014/05/15
 * Time: 4:36 PM
 */
@Controller
@RequestMapping("/report")
public class ReportsController {
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
}
