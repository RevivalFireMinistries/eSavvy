package za.org.rfm.controllers;

import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Russel on 2014-12-04.
 */
@ManagedBean(name = "mobile")
@ViewScoped
public class MobileNavigationController {

    private String conditions;
    private String city;
    private String unit = "c";
    private Map<String,String> cities;

    private static final Logger logger = Logger.getLogger(MobileNavigationController.class.getName());

    @PostConstruct
    public void init() {
        System.out.println("...now running the init mobile....");
        cities = new LinkedHashMap<String, String>();
        cities.put("Istanbul", "TUXX0014");
        cities.put("Barcelona", "SPXX0015");
        cities.put("London", "UKXX0085");
        cities.put("New York", "USNY0996");
        cities.put("Paris", "FRXX2071");
        cities.put("Rome", "ITXX0067");
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getConditions() {
        return conditions;
    }
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Map<String, String> getCities() {
        return cities;
    }

    public void retrieveConditions() {
       /* try {


            conditions = value.split("<a href")[0];
        } catch (Exception e) {
            logger.severe(e.getMessage());
            conditions = "Unable to retrieve weather forecast at the moment.";
        }*/
        System.out.println("...getting conditions....");
    }

    public String saveSettings() {
        conditions = null;
        return "pm:main";
    }
}
