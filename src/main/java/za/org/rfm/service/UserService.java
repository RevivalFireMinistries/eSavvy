package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.UserDAO;
import za.org.rfm.model.User;

import java.util.List;


/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 12:52 PM
 */
@Service("UserService")
@Transactional(readOnly = true)
public class UserService {
     Logger logger = Logger.getLogger(UserService.class);
    @Autowired
    UserDAO userDAO;
    //TODO: need to look into using JpaReository in future
 /*   @Resource
    private UserDAOInterface userDAOInterface;

    public UserDAOInterface getUserDAOInterface() {
        return userDAOInterface;
    }

    public void setUserDAOInterface(UserDAOInterface userDAOInterface) {
        this.userDAOInterface = userDAOInterface;
    } */

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Transactional
    public User getUser(String username){
        logger.debug("Finding user : "+username+" ... ");
        return userDAO.getUser(username);

    }
    public  boolean login(String username,String password){
        boolean success = false;
        User user = getUser(username);
        if(user != null && password.equalsIgnoreCase(user.getPassword())){
            success = true;
        }
     return success;
    }
    public List<User> getUsersByAssembly(long assemblyId) {
        return getUserDAO().getUsersByAssembly(assemblyId);
    }
    @Transactional(readOnly = false)
    public void saveUser(User user) {
        getUserDAO().saveUser(user);
    }
    public boolean checkUserNameExists(User user){
        return getUserDAO().checkUserNameExists(user);
    }


}