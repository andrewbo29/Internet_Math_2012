package MakeFeatures;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 18.12.12
 * Time: 22:47
 * To change this template use File | Settings | File Templates.
 */
public class Click {

    public int timePassed;
    public int batchNumber;
    public int documentNumber;

    public Click(int timePassed, int batchNumber, int documentNumber) {
        this.timePassed = timePassed;
        this.batchNumber = batchNumber;
        this.documentNumber = documentNumber;
    }
}
