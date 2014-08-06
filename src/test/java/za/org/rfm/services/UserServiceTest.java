package za.org.rfm.services;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.junit.Before;
import org.junit.Test;
import za.org.rfm.dao.UserDAO;
import za.org.rfm.model.User;
import za.org.rfm.service.UserService;

/**
 * Created with IntelliJ IDEA.
 * User: Russel
 * Date: 8/4/14
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceTest extends MockObjectTestCase {

    private UserDAO userDAO;
    private UserService userService;

    private Mock mockUserObject;
    @Before
    protected void setUp()throws Exception{
        userService = new UserService();

        mockUserObject = new Mock(UserDAO.class);
        userDAO = (UserDAO)mockUserObject.proxy();
        userService.setUserDAO(userDAO);
    }
    @Test
    public void testGetUser(){
        User user = new User();
        user.setUsername("russel");
        user.setPassword("password85");
        User temp = userService.getUser(user.getUsername());
        System.out.println(temp.equals(user));
    }
}
