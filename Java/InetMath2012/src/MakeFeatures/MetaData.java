package MakeFeatures;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 18.12.12
 * Time: 22:30
 * To change this template use File | Settings | File Templates.
 */
public class MetaData {

    public int day;
    public int userId;
    public int output;
    public int sessionNumber;

    public MetaData(int sessionNumber, int day, int userId, int output) {
        this.sessionNumber = sessionNumber;
        this.day = day;
        this.userId = userId;
        this.output = output;
    }
}
