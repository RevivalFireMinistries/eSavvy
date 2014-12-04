package za.org.rfm.controllers;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import za.org.rfm.model.User;
import za.org.rfm.service.UserService;

import java.io.IOException;

/**
 * Created by Russel on 2014-11-14.
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = Logger.getLogger(MemberController.class);
    @Autowired
    UserService userService;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String doRestLogin(@RequestBody JSONObject json) {
        User dbUser = null;
        try {
            User user = mapper.readValue(json.toString(),User.class);
            if(user != null){
              boolean  success = userService.login(user.getUsername(),user.getPassword());
                if(success){
                    dbUser = userService.getUser(user.getUsername());
                    logger.info("Login status : "+dbUser.getFullname());
                }
            }

            return mapper.writeValueAsString(dbUser);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }


}
