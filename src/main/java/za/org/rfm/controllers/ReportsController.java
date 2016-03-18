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
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import za.org.rfm.model.Event;
import za.org.rfm.service.EventService;

import java.io.IOException;

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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> createEvent(@RequestBody JSONObject json) {
        try {
            logger.info("Now creating the event object...."+json);
            Event event = mapper.readValue(json.toString(),Event.class);
            if(event != null){
                if(!StringUtils.isEmpty(event.getEventType())) {
                  eventService.saveEvent(event);
                  logger.info("Event saved successfully");
                  return new ResponseEntity<String>("{\"success\":true}",HttpStatus.OK);
                }
                return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
            }

        } catch (IOException e) {
            logger.error("Encountered an error : "+e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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
}
