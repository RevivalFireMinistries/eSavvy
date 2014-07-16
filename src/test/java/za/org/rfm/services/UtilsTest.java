package za.org.rfm.services;

import org.junit.Assert;
import org.junit.Test;
import za.org.rfm.model.User;
import za.org.rfm.utils.Constants;
import za.org.rfm.utils.Role;
import za.org.rfm.utils.Utils;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/15
 * Time: 12:09 PM
 */
public class UtilsTest {
    @Test
    public void testEmptyCollection() {
        User user = new User();
        user.setRole(Constants.USER_ROLE_PASTOR);
        Assert.assertEquals(true, Utils.isAuthorised(user, Role.Pastor));
    }
}
