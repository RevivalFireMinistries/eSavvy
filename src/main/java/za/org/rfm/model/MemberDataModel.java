package za.org.rfm.model;

import org.primefaces.model.SelectableDataModel;

import javax.faces.model.ListDataModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Russel.Mupfumira
 * Date: 2014/03/26
 * Time: 1:19 PM
 */
public class MemberDataModel extends ListDataModel<Member> implements SelectableDataModel<Member>, Serializable {

    public MemberDataModel() {
        super();
    }

    public MemberDataModel(List<Member> list) {
        super(list);
    }

    @Override
    public Object getRowKey(Member member) {
        return member.getId();
    }

    @Override
    public Member getRowData(String s) {
       List<Member> members = (List<Member>)getWrappedData();
       for(Member member: members){
           if(member.getId() == Long.parseLong(s)){
               return member;
           }
       }
        return null;
    }
}
