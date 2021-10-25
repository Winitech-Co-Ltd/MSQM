package kr.go.sqsm.peru.data;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Cotejo de nacionalidad
 */
public class CountryComparator implements Comparator<HashMap<String,String>> {
    private Comparator<Object> comparator;

    public CountryComparator() {
        comparator = Collator.getInstance();
    }

    public int compare(CountryData c1, CountryData c2) {
        return comparator.compare(c1.name, c2.name);
    }

    @Override
    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
        Set key = o1.keySet();
        Iterator mIterator = key.iterator();
        String map_key = (String) mIterator.next();
        Set key2 = o2.keySet();
        Iterator mIterator2 = key2.iterator();
        String map_key2 = (String) mIterator2.next();
        return comparator.compare(map_key, map_key2);

    }
}
