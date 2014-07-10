package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.Assembly;
import za.org.rfm.utils.Constants;

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

    public List<Assembly> getAssemblyList(String status){
        Query query;
        if(Constants.STATUS_ALL.equalsIgnoreCase(status)){
           query=  sessionFactory.getCurrentSession().createQuery("from Assembly ");
        }else{
          query =  sessionFactory.getCurrentSession().createQuery("from Assembly where status=:status");
          query.setString("status",status);
        }

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

    public void saveMember(Assembly assembly) {
        getSessionFactory().getCurrentSession().saveOrUpdate(assembly);
    }
}