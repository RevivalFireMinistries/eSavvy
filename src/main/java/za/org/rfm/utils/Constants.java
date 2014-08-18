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
    public static final String FOLLOW_UP_STATUS_COMING = "Coming";
    public static final String FOLLOW_UP_STATUS_AT_WORK = "Busy at Work";
    public static final String FOLLOW_UP_STATUS_NOT_FEELING_WELL = "Not Feeling Well";
    public static final String FOLLOW_UP_STATUS_PHONE_UNREACHABLE = "Phone Unreachable";
    public static final String FOLLOW_UP_STATUS_FACING_CHALLENGES = "Facing Challenges";
    public static final String FOLLOW_UP_STATUS_TRANSPORT_PROBLEM = "Transport Problem";
    public static final String FOLLOW_UP_STATUS_BUSY = "Busy";
    public static final String FOLLOW_UP_STATUS_NO_RESPONSE = "No Response";

    //email subject




    public static final double APOSTOLIC_CONTRIBUTION_PERCENTAGE = 0.35;
    public static final String REPORT_FREQUENCY_MONTHLY = "Monthly";
    public static final String REPORT_FREQUENCY_WEEKLY = "Weekly";
    public static final String REPORT_TYPE_APOSTOLIC = "Apostolic";
    public static final String REPORT_TYPE_PASTORAL = "Pastoral";





    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";

    public static final String MEMBER_TYPE_GUEST = "Guest";
    public static final String MEMBER_TYPE_FULL_TIME = "Full Time";

    public static final String SERVICE_TYPE_SUNDAY = "Sunday Service";
    public static final String REPORT_TEMPLATE_FINANCE = "Finance";
    public static final String REPORT_TEMPLATE_SERVICE = "Service";
    public static final String SERVICE_TYPE_MIDWEEK = "Midweek Service";
    public static final String ASSEMBLY_RANKING_SORT_CRITERIA_ATTENDANCE = "Attendance";
    public static final String ASSEMBLY_RANKING_SORT_CRITERIA_TITHE = "Tithe";
    public static final String ASSEMBLY_RANKING_SORT_CRITERIA_OFFERING = "Offering";



    public static final String WINSMS_USERNAME = "churchmanager";
    public static final String WINSMS_PASSWORD = "password85";

    //roles
    public static final String USER_ROLE_PASTOR = "Pastor";
    public static final String USER_ROLE_ADMINISTRATOR = "Administrator";
    public static final String USER_ROLE_SUPER_ADMIN = "Super Admin";
    //follow up actions
    public static final String ACTION_REFER_TO_PASTOR = "Refer To Pastor";
    public static final String ACTION_SEND_MOTIVATIONAL_SMS = "Send Motivational SMS";
    public static final String ACTION_NO_ACTION_REQUIRED = "No Action Required";

    public static final String[]followUpActions = {ACTION_NO_ACTION_REQUIRED,ACTION_REFER_TO_PASTOR,ACTION_SEND_MOTIVATIONAL_SMS};
    public static final  String SYSTEM_DEFAULT_EMAIL = "esavvy@rfm.org.za" ;
    public static final  String CHURCH_NAME = "church-name";
    public static final  String ESAVVY_LINK = "esavvy-link";
    public static final String NUMBER_OF_WEEKS_TO_RENDER_INACTIVE = "NUMBER OF WEEKS TO RENDER INACTIVE";
    public static final  String MEMBERS_INACTIVE = "No Longer Active";
    public static final  String MEMBERS_ACTIVE_AGAIN = "Active AGAIN";
    public static String[]roles = {USER_ROLE_ADMINISTRATOR,USER_ROLE_PASTOR,USER_ROLE_SUPER_ADMIN};
    public static String[]followUpStates = {FOLLOW_UP_STATUS_BUSY,FOLLOW_UP_STATUS_NO_RESPONSE,FOLLOW_UP_STATUS_AT_WORK,FOLLOW_UP_STATUS_COMING,FOLLOW_UP_STATUS_FACING_CHALLENGES,FOLLOW_UP_STATUS_NOT_FEELING_WELL,FOLLOW_UP_STATUS_PHONE_UNREACHABLE,FOLLOW_UP_STATUS_TRANSPORT_PROBLEM};

    public static final  String MOTIVATIONAL_SMS = "Motivational-sms";
    public static final int DEFAULT_USER_PASSWORD_SIZE = 6;
}
