package za.org.rfm.utils;

import org.apache.log4j.Logger;
import za.org.rfm.beans.Tithe;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
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

    public static String moneyFormatter(double money){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String moneyString = formatter.format(money);
        return moneyString;
    }

    public static String dateFormatter(Date date){
        SimpleDateFormat ft =
                new SimpleDateFormat ("E dd.MM.yyyy ");

        return ft.format(date);
    }

    public static Map<String, String> getCountriesMap() {
        Properties properties = new Properties();
        Map<String,String> map = new HashMap<String, String>();
        try{
            properties.load(new FileInputStream(Constants.COUNTRY_LIST_FILE));
            Iterator it = properties.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pairs = (Map.Entry)it.next();
                map.put((String)pairs.getKey(),(String)pairs.getValue());
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }
        return sortByValue(map);
    }
    public static String getCountryFriendlyName(String code){
        return getCountriesMap().get(code);
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
            System.out.println(url);
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
    public static String[] getMemberTypes(){
        return new String[]{Constants.MEMBER_TYPE_GUEST,Constants.MEMBER_TYPE_FULL_TIME};

    }

    public static String[] getServiceTypes() {
        return new String[]{Constants.SERVICE_TYPE_SUNDAY,Constants.SERVICE_TYPE_MIDWEEK};
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
}