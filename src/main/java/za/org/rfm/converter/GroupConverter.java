package za.org.rfm.converter;

/**
 * User: Russel.Mupfumira
 * Date: 2014/07/09
 * Time: 10:02 AM
 */

import org.springframework.stereotype.Component;
import za.org.rfm.utils.Country;
import za.org.rfm.utils.Group;
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
@FacesConverter(forClass = Country.class,value="groupConverter")
public class GroupConverter implements Converter {
    List<Group> groupList;

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedValue) {
         Integer val = Integer.valueOf(submittedValue);
        if(submittedValue.trim().equals("")){
            return null;
        }else{

            groupList = Utils.getGroupsAsList();
            for(Group group: groupList){
                if(group.ordinal() == val ){
                    return group;
                }
            }
            return null;
        }

    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            return String.valueOf(((Group) value).ordinal());
        }
    }

}

