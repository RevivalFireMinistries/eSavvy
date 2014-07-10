package za.org.rfm.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.User;

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
        User user = null;
        Query query = sessionFactory.getCurrentSession().createQuery("from User where username =:username");
         query.setString("username", username);
        if(!query.list().isEmpty()){
           user = (User)query.list().get(0);
    }
        return user;
    }

    public void saveUser(User user) {
        sessionFactory.getCurrentSession().saveOrUpdate(user);
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
               logger.warn("This username - "+user.getUsername()+" already exists on the server");
                 return true;
           }
        //He has specified something and we have checked with the db and its not there! :-)
        return false;
    }

}
