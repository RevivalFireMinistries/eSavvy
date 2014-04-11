package za.org.rfm.beans.services;

/**
 * User: Russel.Mupfumira
 * Date: 2014/04/08
 * Time: 10:47 AM
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import org.primefaces.model.SortOrder;
import za.org.rfm.model.Event;

import java.util.Comparator;

public class LazySorter implements Comparator<Event> {

    private String sortField;

    private SortOrder sortOrder;

    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public int compare(Event event1, Event event2) {
        try {
            Object value1 = Event.class.getField(this.sortField).get(event1);
            Object value2 = Event.class.getField(this.sortField).get(event2);

            int value = ((Comparable)value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
