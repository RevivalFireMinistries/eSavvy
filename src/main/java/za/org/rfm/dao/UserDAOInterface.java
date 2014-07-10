package za.org.rfm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import za.org.rfm.model.User;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/01
 * Time: 4:08 PM
 */
public interface UserDAOInterface extends JpaRepository<User,String> {

}
