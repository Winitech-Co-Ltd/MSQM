package kr.go.sqsmo.peru.data;

import java.text.Collator;
import java.util.Comparator;

/**
 * Cotejo de nacionalidad
 */
public class CountryComparator implements Comparator<CountryData> {
    private Comparator<Object> comparator;

    public CountryComparator() {
        comparator = Collator.getInstance();
    }

    public int compare(CountryData c1, CountryData c2) {
        return comparator.compare(c1.name, c2.name);
    }
}
