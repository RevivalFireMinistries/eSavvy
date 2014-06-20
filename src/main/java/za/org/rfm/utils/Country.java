package za.org.rfm.utils;

import lombok.Getter;
import lombok.Setter;

import java.text.Collator;
import java.util.Comparator;

/**
 * User: Russel.Mupfumira
 * Date: 2014/06/11
 * Time: 12:52 PM
 */
@Getter
@Setter
public class Country {
    private String iso;

    private String code;

    public String name;

    Country(String iso, String code, String name) {
        this.iso = iso;
        this.code = code;
        this.name = name;
    }

    public String toString() {
        return iso + " - " + code + " - " + name.toUpperCase();
    }
}
class CountryComparator implements Comparator<Country> {
    private Comparator comparator;

    CountryComparator() {
        comparator = Collator.getInstance();
    }

    public int compare(Country o1, Country o2) {
        return comparator.compare(o1.name, o2.name);
    }
}
