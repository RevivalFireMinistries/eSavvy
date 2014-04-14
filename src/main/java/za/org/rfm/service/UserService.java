package za.org.rfm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.UserDAO;
import za.org.rfm.model.User;


/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 12:52 PM
 */
@Service("UserService")
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public User getUser(String username){
        return getUserDAO().getUser(username);

    }
    public  boolean login(String username,String password){
        boolean success = false;
        User user = getUser(username);
        if(user != null && password.equalsIgnoreCase(user.getPassword())){
            success = true;
        }
        return success;
    }
}
