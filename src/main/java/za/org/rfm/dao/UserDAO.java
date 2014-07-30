package za.org.rfm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Role;
import za.org.rfm.model.User;
import za.org.rfm.model.UserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 12:47 PM
 */
@Repository
public class UserDAO {
    Logger logger = Logger.getLogger(UserDAO.class);
    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User getUser(String username){
        User user = (User) sessionFactory.getCurrentSession().createCriteria(User.class).add(Restrictions.idEq(username)).uniqueResult();
        return user;

    }

    public Role getRoleById(Long id){
        Role role = null;
        Query query = sessionFactory.getCurrentSession().createQuery("from Role where id =:id");
         query.setLong("id", id);
        if(!query.list().isEmpty()){
           role = (Role)query.list().get(0);
    }
        return role;
    }

    public void saveUser(User user) {
        sessionFactory.getCurrentSession().saveOrUpdate(user);
    }

    public List<Role> getRoles(){
        return (List<Role>)sessionFactory.getCurrentSession().createQuery("from Role ").list();
    }

    public List<User> getUsersByAssembly(long assemblyId) {
       List<User> userList = new ArrayList<User>();
        Query query = sessionFactory.getCurrentSession().createQuery("from User where assembly =:assembly");
        query.setLong("assembly", assemblyId);
        if(!query.list().isEmpty()){
            userList = (List<User>)query.list();
        }
        logger.debug("Users for assembly : "+assemblyId+" total : "+userList.size());
        return userList;
    }

    public boolean checkUserNameExists(User user) {
          //return true if he didn't specify a username or if it exists - so that he keeps trying
        if(user.getUsername()== null || user.getUsername().equalsIgnoreCase(""))
            return true;
           if(getUser(user.getUsername()) != null){
               logger.warn("This username - "+user.getUsername()+" exists on the server");
                 return true;
           }
        //He has specified something and we have checked with the db and its not there! :-)
        return false;
    }
    public UserRole getUserRoleById(Long id) {
        UserRole userRole = (UserRole) sessionFactory.getCurrentSession().createCriteria(UserRole.class).add(Restrictions.idEq(id)).uniqueResult();
        return userRole;
    }

    public void deleteUserRole(UserRole userRole) {
        sessionFactory.getCurrentSession().delete(userRole);
    }

    public void saveOrUpdateUserRole(UserRole userRole1) {
        if(!userRoleExists(userRole1))  {
            sessionFactory.getCurrentSession().saveOrUpdate(userRole1);
        }

    }
    public boolean userRoleExists(UserRole userRole){
        User user = getUser(userRole.getUser().getUsername());
        for(UserRole userRole1: user.getUserRoles()){
             if(userRole.equals(userRole1)){
                 return true;
             }
       }
        return false;
    }
    public void saveRole(Role role1) {
        getSessionFactory().getCurrentSession().saveOrUpdate(role1);
    }

}
