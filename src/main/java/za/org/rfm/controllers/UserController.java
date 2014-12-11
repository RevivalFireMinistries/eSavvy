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
import java.sql.Timestamp;

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
            System.out.println("REST login request username :"+user.getUsername()+" pswd : "+user.getPassword());
            if(user != null){
              boolean  success = userService.login(user.getUsername(),user.getPassword());
                System.out.println("Results : "+success);
                if(success){
                    dbUser = userService.getUser(user.getUsername());
                    dbUser.setAssemblyId(""+dbUser.getAssembly().getAssemblyid());
                    dbUser.setAssemblyName(dbUser.getAssembly().getName());
                    logger.info("Login status : "+dbUser.getFullname());
                    dbUser.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
                    userService.saveUser(dbUser);
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
