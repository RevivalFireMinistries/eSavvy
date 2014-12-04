package za.org.rfm.beans.services;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 10:44 AM
 */

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import za.org.rfm.model.Event;

import java.util.*;

/**
 * implementation of LazyDataModel
 */
public class LazyEventDataModel extends LazyDataModel<Event> {

    private List<Event> datasource;

    public LazyEventDataModel(List<Event> datasource) {
        this.datasource = datasource;
    }

    @Override
    public Event getRowData(String rowKey) {
        for(Event event : datasource) {
            if(event.getId().equals(rowKey))
                return event;
        }

        return null;
    }

    @Override
    public Object getRowKey(Event event) {
        return event.getId();
    }

    @Override
    public List<Event> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        List<Event> data = new ArrayList<Event>();

        //filter
        for(Event event : datasource) {
            boolean match = true;

            for(Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                try {
                    String filterProperty = it.next();
                    String filterValue = (String)filters.get(filterProperty);
                    String fieldValue = String.valueOf(event.getClass().getField(filterProperty).get(event));

                    if(filterValue == null || fieldValue.startsWith(filterValue)) {
                        match = true;
                    }
                    else {
                        match = false;
                        break;
                    }
                } catch(Exception e) {
                    match = false;
                }
            }

            if(match) {
                data.add(event);
            }
        }

        //sort
        if(sortField != null) {
            Collections.sort(data, new LazySorter(sortField, sortOrder));
        }

        //rowCount
        int dataSize = data.size();
        this.setRowCount(dataSize);

        //paginate
        if(dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            }
            catch(IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        }
        else {
            return data;
        }
    }
}

