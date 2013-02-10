package MakeFeatures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 18.12.12
 * Time: 22:38
 * To change this template use File | Settings | File Templates.
 */
public class Query {

    public int timePassed;
    public int batchNumber;
    public ArrayList<Integer> documents;

    public Query(int timePassed, int batchNumber, ArrayList<Integer> documents) {
        this.timePassed = timePassed;
        this.batchNumber = batchNumber;
        this.documents = documents;
    }
}
