package za.org.rfm.utils;

/**
 * User: Russel.Mupfumira
 * Date: 2013/12/12
 * Time: 12:23 AM
 */
public class Constants {

    //Hibernate config file for the test environment
    public static final String HIBERNATE_CONFIG_TEST_DATABASE = "hibernate.cfg.xml";
    public static final String COUNTRY_LIST_FILE = "countries.properties";
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_IN_ACTIVE = "InActive";
    public static final String STATUS_DELETED = "Deleted";
    public static final String STATUS_SENT = "Sent";
    public static final String STATUS_FAILED = "Failed";
    public static final String STATUS_ALL = "ALL";

    public static final double APOSTOLIC_CONTRIBUTION_PERCENTAGE = 0.35;

    //severity of message
    public static final String FACES_MSG_SEVERITY_ERROR = "ERROR";
    public static final String FACES_MSG_SEVERITY_INFO = "INFO";
    public static final String FACES_MSG_SEVERITY_WARNING = "WARNING";
    public static final String FACES_MSG_SEVERITY_FATAL = "FATAL";



    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";

    public static final String MEMBER_TYPE_GUEST = "Guest";
    public static final String MEMBER_TYPE_FULL_TIME = "Full Time";

    public static final String SERVICE_TYPE_SUNDAY = "Sunday Service";
    public static final String SERVICE_TYPE_MIDWEEK = "Midweek Service";
    public static final  int MAX_CONNECTIONS_PER_SEARCH = 10 ;

    public static final String WINSMS_USERNAME = "churchmanager";
    public static final String WINSMS_PASSWORD = "password85";

    //roles
    public static final String USER_ROLE_PASTOR = "Pastor";
    public static final String USER_ROLE_ADMINISTRATOR = "Admin";
    public static final String USER_ROLE_SUPER_ADMIN = "Super Admin";

}
