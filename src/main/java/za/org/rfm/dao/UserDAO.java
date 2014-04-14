package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.User;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 12:47 PM
 */
@Repository
public class UserDAO {

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
}
