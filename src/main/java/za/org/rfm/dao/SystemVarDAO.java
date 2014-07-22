package za.org.rfm.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import za.org.rfm.model.SystemVar;

import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/10
 * Time: 12:23 PM
 */
@Repository
public class SystemVarDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveSystemVar(SystemVar vars){
        sessionFactory.getCurrentSession().saveOrUpdate(vars);
    }
    public SystemVar getSystemVarById(Long id) {
        Query query = sessionFactory.getCurrentSession().createQuery("from SystemVar where id =:id");
        query.setLong("id",id);
        return ((List<SystemVar>)query.list()).get(0);

    }
    public List<SystemVar> getSystemVarByName(String name) {
        Query query = sessionFactory.getCurrentSession().createQuery("from SystemVar where name like :name");
        query.setString("name",name+"%");
        return (List<SystemVar>)query.list();

    }

    public List<SystemVar> getAllVars() {
        Query query = sessionFactory.getCurrentSession().createQuery("from SystemVar ");
        return (List<SystemVar>)query.list();

    }

    public SystemVar getSystemVarByNameUnique(String name) {
        SystemVar systemVar = (SystemVar) sessionFactory.getCurrentSession().createCriteria(SystemVar.class).add(Restrictions.eq("name",name)).uniqueResult();
        return systemVar;
    }
}
