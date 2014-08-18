package za.org.rfm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.org.rfm.dao.UserDAO;
import za.org.rfm.model.Role;
import za.org.rfm.model.User;
import za.org.rfm.model.UserRole;

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
    //TODO: need to look into using JpaRepository in future
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
    public List<Role> getRoles(){
        return userDAO.getRoles();
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
    public Role getRoleById(Long id){
        return userDAO.getRoleById(id);
    }
    public UserRole getUserRoleById(Long id){
        return userDAO.getUserRoleById(id);
    }
    @Transactional(readOnly = false)
    public void deleteUserRole(UserRole userRole){
        userDAO.deleteUserRole(userRole);
    }
    @Transactional(readOnly = false)
    public void saveOrUpdateUserRole(UserRole userRole1) {
        userDAO.saveOrUpdateUserRole(userRole1);
    }
    @Transactional(readOnly = false)
    public void saveRole(Role role1) {
        userDAO.saveRole(role1);
    }
    public List<User> getUsersByRole(za.org.rfm.utils.Role role){
        return userDAO.getUsersByRole(role);
    }

    public User getApostle(){
        List<User> users = getUsersByRole(za.org.rfm.utils.Role.Apostle);
        if(!users.isEmpty()){
            return users.get(0);   //FIXME: make sure only one apostle exists in the system
        }
        return null;
    }
}