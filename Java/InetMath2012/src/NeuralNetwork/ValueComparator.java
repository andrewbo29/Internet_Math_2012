package NeuralNetwork;

import java.util.Comparator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.12.12
 * Time: 23:42
 * To change this template use File | Settings | File Templates.
 */
public class ValueComparator implements Comparator<Integer> {

    Map<Integer, Double> base;
    public ValueComparator(Map<Integer, Double> base) {
        this.base = base;
    }

    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
