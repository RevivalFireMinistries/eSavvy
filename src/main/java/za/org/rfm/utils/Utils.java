package za.org.rfm.utils;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import za.org.rfm.beans.Tithe;
import za.org.rfm.model.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: Russel.Mupfumira
 * Date: 2013/12/14
 * Time: 12:14 AM
 */
public class Utils {

    public static Logger logger = Logger.getLogger(Utils.class);
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.messages_en_ZA", Locale.getDefault());
    public static List<String> getStates(){
        List<String> states = Arrays.asList(Constants.STATUS_ACTIVE,
         Constants.STATUS_IN_ACTIVE, Constants.STATUS_DELETED);
        return states;
    }

    public static List<String> getGender(){
        List<String> genders = Arrays.asList(Constants.GENDER_MALE,
                Constants.GENDER_FEMALE);
        return genders;
    }

    public static String moneyFormatter(double money,Locale locale){
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        String moneyString = formatter.format(money);
        return moneyString;
    }
    public static void capitaliseUser(User user){
        user.setFirstName(capitaliseFirstLetter(user.getFirstName()));
        user.setLastName(capitaliseFirstLetter(user.getLastName()));
    }
    public static void capitaliseMember(Member member){
        member.setFirstName(capitaliseFirstLetter(member.getFirstName()));
        member.setLastName(capitaliseFirstLetter(member.getLastName()));
    }
    public static String capitaliseFirstLetter(String original){
        return original.length() == 0 ? original : original.substring(0, 1).toUpperCase() + original.substring(1);
    }
    public static String dateFormatter(Date date){
        SimpleDateFormat ft =
                new SimpleDateFormat ("E dd.MM.yyyy ");

        return ft.format(date);
    }
    public static Country getSelectedCountry(String code){
        for(Country c : getAllCountries()){
           if(c.getCode().equalsIgnoreCase(code)){
               return c;
           }
        }
        return null;
    }
    public static String getResource(String key,Object...args){
        return (new NonStaticMessageFormat(resourceBundle.getString(key),args).getMsg());
    }
    public static List<Country> getAllCountries(){
        List<Country> countries = new ArrayList<Country>();
        Locale[] locales = Locale.getAvailableLocales();
        System.out.println(locales.length);
        for (Locale locale : locales) {
            try{
                String iso = locale.getISO3Country();
                String code = locale.getCountry();
                String name = locale.getDisplayCountry();
                String lang = locale.getLanguage();
                if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {
                    countries.add(new Country(iso, code, name,lang));
                }
            }catch (Exception e){
                continue;
            }
        }

        Collections.sort(countries, new CountryComparator());
        return  countries;
    }
    public static Locale getCountryLocale(String code){
        List<Country> countries = new ArrayList<Country>();
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            try{
                logger.debug(locale.getCountry()+" : "+code);
              if(locale.getCountry().equalsIgnoreCase(code)){
                  logger.debug("Locale found..."+locale.getDisplayCountry());
                  return locale;
              }
            }catch (Exception e){
                continue;
            }
        }
        logger.warn(" Failed to find a locale: using system default : " + Locale.getDefault().getDisplayCountry());
        return Locale.getDefault();
    }
    public static Map<String, String> getCountriesMap() {

        Map<String,String> map = new HashMap<String, String>();
        try{

            Iterator it = getAllCountries().iterator();
            while(it.hasNext()){
               Country country = (Country)it.next();
                map.put(country.getCode(),country.getName());
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return sortByValue(map);
    }
    public static Map<Long,String> getVarsAsMap(List<SystemVar> vars){
        Map<Long,String> map = new HashMap<Long, String>();
        for(SystemVar var : vars){
            map.put(var.getId(),var.getName());
        }
        return map;
    }
    public static Map<Long,String> getAssembliesAsMap(List<Assembly> assemblyList){
        Map<Long,String> map = new HashMap<Long, String>();
        for(Assembly assembly : assemblyList){
            map.put(assembly.getAssemblyid(),assembly.getName());
        }
        return map;
    }
    public static String getCountryFriendlyName(String code){
        return getCountriesMap().get(code);
    }
    private static LocalDate  calcNextDay(LocalDate d, int weekday) {
        return (d.getDayOfWeek() > weekday)?d.withDayOfWeek(weekday):d.minusWeeks(1).withDayOfWeek(weekday);
    }

    public static DateRange calcLastMonthDateRange(Date day) {
        LocalDate date = new LocalDate(day);
        //first go back one month
        LocalDate lastMonth = date.minusMonths(1);
        //now get the fist and last day of this month
        return new DateRange(lastMonth.dayOfMonth().withMinimumValue().toDate(),lastMonth.dayOfMonth().withMaximumValue().toDate());

    }
    public static Date calcLastSunday(Date date) {
        LocalDate lastSunday = calcNextDay(LocalDate.fromDateFields(date), DateTimeConstants.SUNDAY);
        return lastSunday.toDate();
    }
    public static boolean sendSMS(String phone, String message,boolean test){
        if(test)
            return true;
        try {
            String urlstring = "http://www.winsms.co.za/api/batchmessage.asp?user="+ Constants.WINSMS_USERNAME+"&password="+ Constants.WINSMS_PASSWORD+"&message=" +
                    message+"&Numbers="+phone+";";
            URL url = new URL(urlstring);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            logger.info("Sending sms...");
            logger.info(url);
            HttpURLConnection wincon = (HttpURLConnection)url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    wincon.getInputStream()));
            String inputLine;
            int x = 0;
            while ((inputLine = in.readLine()) != null) {
                logger.info("Response : "+inputLine);
                x++;
            }
            in.close();
            logger.info("Done!");
            //now check for failure
            if("FAIL".toLowerCase().contains(inputLine.toLowerCase())){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }
    }
    public static <K, V extends Comparable<? super V>> Map<K, V>
    sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    public static String generateID()
    {
        long current= System.currentTimeMillis();
        return "TXN"+current++;
    }

    public static void addFacesMessage( String message,FacesMessage.Severity severity){
        FacesMessage msg =  null;
        if(severity != null){
          msg =  new FacesMessage(severity,message,null);
        }else{
            msg =  new FacesMessage(message);
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);

    }
    public static double mean(double[] m) {
        double sum = 0;
        for (int i = 0; i < m.length; i++) {
            sum += m[i];
        }
        return sum / m.length;
    }
    public static String[] getMemberTypes(){
        return new String[]{Constants.MEMBER_TYPE_GUEST,Constants.MEMBER_TYPE_FULL_TIME};

    }

    public static String[] getServiceTypes() {
        return new String[]{Constants.SERVICE_TYPE_SUNDAY,Constants.SERVICE_TYPE_MIDWEEK};
    }

    public static void generateUserName(User user,int x) {
        String username = user.getFirstName().charAt(0) + user.getLastName();
        if(x > 0) //only  append the integer x  its not the first attempt -
        username +=x;
        user.setUsername(username.toLowerCase());
    }

    public static boolean txnExists(List<Tithe> titheList, Tithe tithe) {
       for(Tithe t : titheList){
           if(t.getId() == tithe.getId() && t.getAmount() == tithe.getAmount() ){
               if(t.getTxnDate().compareTo(tithe.getTxnDate()) == 0){
                   return true;
               }

        }

       }
        return false;
    }

    public static String generateRandomPassword(int length){
        StringBuffer sb = new StringBuffer();
        for (int x = 0; x < length; x++)
        {
            sb.append((char)((int)(Math.random()*26)+97));
        }
        return sb.toString().toLowerCase();
    }

    public static boolean isAuthorised(User user,Role role){

       for(UserRole userRole: user.getUserRoles()){
           if(userRole.getRole().getName().equalsIgnoreCase(Role.Apostle.name())){
               return true;
           }
           if(userRole.getRole().getName().equalsIgnoreCase(role.name())){
               return true;
           }
       }
       return false;
    }

   public static List<String> getGroupsAsStringList(){
       List<String> list = new ArrayList<String>();
       for(Group group : Group.values()){
              list.add(group.name());
       }
       return list;
   }
    public static List<Group> getGroupsAsList(){
        List<Group> list = new ArrayList<Group>();
        for(Group group : Group.values()){
            list.add(group);
        }
        return list;
    }
    public static void sendBulkSMS(List<Member> smsList,String sms) {
        //TODO:need to use the bulk sms send api for winsms
          for(Member member: smsList){
              member.sendSMS(sms,true);
          }
    }




}