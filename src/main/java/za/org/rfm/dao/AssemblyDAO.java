package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Assembly;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/20
 * Time: 10:56 PM
 */
@Repository
public class AssemblyDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Assembly> getAssemblyList(){
        Query query = sessionFactory.getCurrentSession().createQuery("from Assembly");
        return (List<Assembly>)query.list();
    }


    public Assembly getAssemblyById(long l) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Assembly where id=:id");
        query.setLong("id",l);
        Assembly assembly = (Assembly)query.list().get(0);
        System.out.println("----got an assembly ---"+assembly.name);
        assembly.setMembersRegistered(assembly.getTotalRegistered());
        return assembly;
    }
}