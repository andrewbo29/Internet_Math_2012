package MakeFeatures;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.12.12
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class UserData {

    public List<Integer> sessions;
    public List<Integer> numDays;
    public double sumTime;
    public double sumNumberQ;
    public double sumNumberC;
    public double sumNumberS;
    public double sumNumberQC;
    public double sumMeanTimeQCS;
    public double sumMeanTimeQC;
    public double sumMeanTimeCC;
    public double sumMeanTimeQQ;
    public double sumProportionQQ;
    public double sumMeanClickDocuments;

    public UserData(List<Integer> sessions, List<Integer> numDays, double sumTime, double sumNumberQ, double sumNumberC,
                    double sumNumberS, double sumNumberQC, double sumMeanTimeQCS, double sumMeanTimeQC, double sumMeanTimeCC,
                    double sumMeanTimeQQ, double sumProportionQQ, double sumMeanClickDocuments) {
        this.sessions = sessions;
        this.numDays = numDays;
        this.sumTime = sumTime;
        this.sumNumberQ = sumNumberQ;
        this.sumNumberC = sumNumberC;
        this.sumNumberS = sumNumberS;
        this.sumNumberQC = sumNumberQC;
        this.sumMeanTimeQCS = sumMeanTimeQCS;
        this.sumMeanTimeQC = sumMeanTimeQC;
        this.sumMeanTimeCC = sumMeanTimeCC;
        this.sumMeanTimeQQ = sumMeanTimeQQ;
        this.sumProportionQQ = sumProportionQQ;
        this.sumMeanClickDocuments = sumMeanClickDocuments;
    }
}

