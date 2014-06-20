package za.org.rfm.converter;

import org.springframework.stereotype.Component;
import za.org.rfm.utils.Country;
import za.org.rfm.utils.Utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.List;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/11
 * Time: 1:03 PM
 */
@Component
@FacesConverter(forClass = Country.class,value="countryConverter")
public class CountryConverter implements Converter {
    List<Country> countryList;

    public List<Country> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {

            if(submittedValue.trim().equals("")){
                return null;
            }else{

                countryList = Utils.getAllCountries();
                for(Country country: countryList){
                    if(country.getCode() == submittedValue ){
                        return country;
                    }
                }
                return null;
            }

    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Country) value).getCode());
        }
    }

}
