package MakeFeatures;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 14.12.12
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class MakeFeatures {

    public static final int NUMBER_SESSION = 7856733;

//    public static String fileName = "D:\\Интернет-математика 2012\\dataset\\train";

//    public static String fileNameSession = "D:\\Интернет-математика 2012\\features_train_new\\sessionFeature";
//    public static String fileNameUser = "D:\\Интернет-математика 2012\\features_train_new\\userFeature";

    public static String fileName = "D:\\Интернет-математика 2012\\dataset\\test";

    public static String fileNameSession = "D:\\Интернет-математика 2012\\features_test_new\\sessionFeature";
    public static String fileNameUser = "D:\\Интернет-математика 2012\\features_test_new\\userFeature";

    public static String[] previousData;

    public static LinkedHashMap<Integer, UserData> usersMap = new LinkedHashMap<Integer, UserData>();

    public static void main(String[] args) {

        File fileData = new File(fileName);
        File fileSession = new File(fileNameSession);
        File fileUser = new File(fileNameUser);

        try {
            FileInputStream fisData = new FileInputStream(fileData);

            FileOutputStream fosSession = new FileOutputStream(fileSession);
            FileOutputStream fosUser = new FileOutputStream(fileUser);
            try {
                PrintWriter pwSession = new PrintWriter(fosSession);
                PrintWriter pwUser = new PrintWriter(fosUser);

                Scanner scanner = new Scanner(fisData);
                previousData = scanner.nextLine().split("\t");
//                for (int sessionId = 0; sessionId < NUMBER_SESSION; ++sessionId) {
//                    processSession(sessionId, scanner, pwSession);
////                    pwSession.flush();
//                }
                for (int sessionId = 7856734; sessionId <= 8595730; ++sessionId) {
                    processSession(sessionId, scanner, pwSession);
//                    pwSession.flush();
                }
                pwSession.flush();
                fosSession.close();

                processUsers(pwUser);
                pwUser.flush();
                fosUser.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fisData.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void processSession(int sessionId, Scanner scanner, PrintWriter pwSession) {
        if (sessionId % 100000 == 0) {
            System.out.println(sessionId);
        }

        MetaData metaData = null;
        ArrayList<Query> queryList = new ArrayList<Query>();
        ArrayList<Click> clickList = new ArrayList<Click>();
        ArrayList<Switch> switchList = new ArrayList<Switch>();

        LinkedList<Integer> timePassed = new LinkedList<Integer>();
        int lastTime = 0;

        String[] data = previousData;

        while (Integer.parseInt(data[0]) == sessionId && scanner.hasNextLine()) {
            if (data[2].equals("M")) {
                int y;
                if (data.length < 5) {
                    y = 0;
                } else {
                    if (data[4].equals("N")) {
                        y = 0;
                    } else {
                        y = 1;
                    }
                }
                metaData = new MetaData(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[3]), y);
            } else if (data[2].equals("Q")) {
                int docNumber = data.length - 4;
                ArrayList<Integer> documentList = new ArrayList<Integer>(docNumber);
                for (int i = 4; i < data.length; ++i) {
                    documentList.add(Integer.parseInt(data[i]));
                }
                Query query = new Query(Integer.parseInt(data[1]), Integer.parseInt(data[3]), documentList);
                queryList.add(query);
                timePassed.add(query.timePassed - lastTime);
                lastTime = query.timePassed;
            } else if (data[2].equals("C")) {
                Click click = new Click(Integer.parseInt(data[1]), Integer.parseInt(data[3]), Integer.parseInt(data[4]));
                clickList.add(click);
                timePassed.add(click.timePassed - lastTime);
                lastTime = click.timePassed;
            } else if (data[2].equals("S")) {
                Switch sw = new Switch(Integer.parseInt(data[1]));
                switchList.add(sw);
                timePassed.add(sw.timePassed - lastTime);
                lastTime = sw.timePassed;
            }

            data = scanner.nextLine().split("\t");
        }

        previousData = data;

        String sessionFeatures = getSessionFeatures(metaData, queryList, clickList, switchList, timePassed);
        pwSession.println(sessionFeatures);
    }

    public static String getSessionFeatures(MetaData metaData, ArrayList<Query> queryList, ArrayList<Click> clickList, ArrayList<Switch> switchList,
                                            LinkedList<Integer> timePassed) {
        StringBuilder session = new StringBuilder();

        int OUTPUT = metaData.output;

        int SESSION_TIME = getSessionTime(queryList, clickList, switchList);
        int NUMBER_Q = queryList.size();
        int NUMBER_C = clickList.size();
        int NUMBER_S = switchList.size();
        int NUMBER_QC = 0;
        if (!clickList.isEmpty()) {
            NUMBER_QC = clickList.get(clickList.size() - 1).batchNumber + 1;
        }
        double MEAN_TIME_QCS = getMeanTimeQCS(timePassed);
        double MEAN_TIME_QC = getMeanTimeQC(queryList, clickList);
        double MEAN_TIME_CC = getMeanTimeCC(clickList);
        double MEAN_TIME_QQ = getMeanTimeQQ(queryList);
        double PROPORTION_QQ = getProportionQQ(queryList, clickList);
        double MEAN_CLICK_DOCUMENTS = getMeanClickDocuments(queryList, clickList);

        if (usersMap.containsKey(metaData.userId)) {
            UserData userData = usersMap.get(metaData.userId);
            userData.sessions.add(metaData.sessionNumber);
            if (!userData.numDays.contains(metaData.day)) {
                userData.numDays.add(metaData.day);
            }
            userData.sumTime += SESSION_TIME;
            userData.sumNumberQ += NUMBER_Q;
            userData.sumNumberC += NUMBER_C;
            userData.sumNumberS += NUMBER_S;
            userData.sumNumberQC += NUMBER_QC;
            userData.sumMeanTimeQCS += MEAN_TIME_QCS;
            userData.sumMeanTimeQC += MEAN_TIME_QC;
            userData.sumMeanTimeCC += MEAN_TIME_CC;
            userData.sumMeanTimeQQ += MEAN_TIME_QQ;
            userData.sumProportionQQ += PROPORTION_QQ;
            userData.sumMeanClickDocuments += MEAN_CLICK_DOCUMENTS;
        } else {
            LinkedList<Integer> sessions = new LinkedList<Integer>();
            sessions.add(metaData.sessionNumber);
            LinkedList<Integer> numDays = new LinkedList<Integer>();
            numDays.add(metaData.day);
            UserData userData = new UserData(sessions, numDays, SESSION_TIME, NUMBER_Q, NUMBER_C, NUMBER_S, NUMBER_QC, MEAN_TIME_QCS,
                    MEAN_TIME_QC, MEAN_TIME_CC, MEAN_TIME_QQ, PROPORTION_QQ, MEAN_CLICK_DOCUMENTS);
            usersMap.put(metaData.userId, userData);
        }

        session.append(OUTPUT).append("\t");
        session.append(SESSION_TIME).append("\t");
        session.append(NUMBER_Q).append("\t");
        session.append(NUMBER_C).append("\t");
        session.append(NUMBER_S).append("\t");
        session.append(NUMBER_QC).append("\t");
        session.append(MEAN_TIME_QCS).append("\t");
        session.append(MEAN_TIME_QC).append("\t");
        session.append(MEAN_TIME_CC).append("\t");
        session.append(MEAN_TIME_QQ).append("\t");
        session.append(PROPORTION_QQ).append("\t");
        session.append(MEAN_CLICK_DOCUMENTS);

        return session.toString();
    }

    public static void processUsers(PrintWriter pwUser) {
        for (UserData userData : usersMap.values()) {
            int SESSION_NUMBER = userData.sessions.size();
            int DAYS_NUMBER = userData.numDays.size();
            double MEAN_TIME = userData.sumTime / SESSION_NUMBER;
            double MEAN_NUMBER_Q = userData.sumNumberQ / SESSION_NUMBER;
            double MEAN_NUMBER_C = userData.sumNumberC / SESSION_NUMBER;
            double MEAN_NUMBER_S = userData.sumNumberS / SESSION_NUMBER;
            double MEAN_NUMBER_QC = userData.sumNumberQC / SESSION_NUMBER;
            double MEAN_TIME_QCS = userData.sumMeanTimeQCS / SESSION_NUMBER;
            double MEAN_TIME_QC = userData.sumMeanTimeQC / SESSION_NUMBER;
            double MEAN_TIME_CC = userData.sumMeanTimeCC / SESSION_NUMBER;
            double MEAN_TIME_QQ = userData.sumMeanTimeQQ / SESSION_NUMBER;
            double MEAN_PROPORTION_QQ = userData.sumProportionQQ / SESSION_NUMBER;
            double MEAN_CLICK_DOCUMENT = userData.sumMeanClickDocuments / SESSION_NUMBER;

            StringBuilder session = new StringBuilder();
            session.append(SESSION_NUMBER).append("\t");
            session.append(DAYS_NUMBER).append("\t");
            session.append(MEAN_TIME).append("\t");
            session.append(MEAN_NUMBER_Q).append("\t");
            session.append(MEAN_NUMBER_C).append("\t");
            session.append(MEAN_NUMBER_S).append("\t");
            session.append(MEAN_NUMBER_QC).append("\t");
            session.append(MEAN_TIME_QCS).append("\t");
            session.append(MEAN_TIME_QC).append("\t");
            session.append(MEAN_TIME_CC).append("\t");
            session.append(MEAN_TIME_QQ).append("\t");
            session.append(MEAN_PROPORTION_QQ).append("\t");
            session.append(MEAN_CLICK_DOCUMENT).append("\t");

            for (int i = 0; i < SESSION_NUMBER; ++i) {
                pwUser.println(session.toString());
            }
        }
    }

    private static double getProportionQQ(ArrayList<Query> queryList, ArrayList<Click> clickList) {
        if (queryList.isEmpty()) {
            return 0;
        }
        int lastClickBatchNumber = -1;
        double numberQQ = 0;
        int num;
        for (Click click : clickList) {
            num = click.batchNumber - lastClickBatchNumber;
            if (num > 1) {
                numberQQ += num - 1;
            }
            lastClickBatchNumber = click.batchNumber;
        }
        return (numberQQ / queryList.size());
    }

    private static double getMeanTimeQQ(ArrayList<Query> queryList) {
        int lastTimeQQ = 0;
        int size = 0;
        double timePassedSumQQ = 0;
        for (Query aQueryList : queryList) {
            timePassedSumQQ += aQueryList.timePassed - lastTimeQQ;
            ++size;
            lastTimeQQ = aQueryList.timePassed;
        }

        if (size != 0) {
            return (timePassedSumQQ / size);
        }
        return 0;
    }

    private static double getMeanTimeCC(ArrayList<Click> clickList) {
        int lastTimeCC = 0;
        int size = 0;
        double timePassedSumCC = 0;
        for (Click aClickList : clickList) {
            timePassedSumCC += aClickList.timePassed - lastTimeCC;
            ++size;
            lastTimeCC = aClickList.timePassed;
        }

        if (size != 0) {
            return (timePassedSumCC / size);
        }
        return 0;
    }

    private static double getMeanTimeQC(ArrayList<Query> queryList, ArrayList<Click> clickList) {
        if (clickList.isEmpty() || queryList.isEmpty()) {
            return 0;
        }

        int indexC = 0;
        double timePassedSumQC = 0;
        int size = 0;
        for (Query query : queryList) {
            if (indexC < clickList.size() && query.batchNumber == clickList.get(indexC).batchNumber) {
                timePassedSumQC += clickList.get(indexC).timePassed - query.timePassed;
                ++size;
                ++indexC;
            }
            while (indexC < clickList.size() && query.batchNumber == clickList.get(indexC).batchNumber) {
                ++indexC;
            }
        }

        if (size != 0) {
            return (timePassedSumQC / size);
        }
        return 0;

    }

    private static double getMeanTimeQCS(LinkedList<Integer> timePassed) {
        if (timePassed.isEmpty()) {
            return 0;
        }
        double timePassedSum = 0;
        for (Integer time : timePassed) {
            timePassedSum += time;
        }
        return (timePassedSum / (timePassed.size()));
    }

    private static int getSessionTime(ArrayList<Query> queryList, ArrayList<Click> clickList, ArrayList<Switch> switchList) {
        int sessionTime;

        int lastQueryTime = 0;
        if (!queryList.isEmpty()) {
            lastQueryTime = queryList.get(queryList.size() - 1).timePassed;
        }
        int lastClickTime = 0;
        if (!clickList.isEmpty()) {
            lastClickTime = clickList.get(clickList.size() - 1).timePassed;
        }

        if (lastQueryTime > lastClickTime) {
            sessionTime = lastQueryTime;
        } else {
            sessionTime = lastClickTime;
        }
        if (!switchList.isEmpty() && switchList.get(switchList.size() - 1).timePassed > sessionTime) {
            sessionTime = switchList.get(switchList.size() - 1).timePassed;
        }

        return sessionTime;
    }

    private static double getMeanClickDocuments(ArrayList<Query> queryList, ArrayList<Click> clickList) {
        if (clickList.isEmpty() || queryList.isEmpty()) {
            return 0;
        }

        double clickDocumentsSum = 0;
        int size = 0;
        int number = 1;
        int indexC = 0;
        Query previousQuery = queryList.get(0);
        while (indexC < clickList.size() && previousQuery.batchNumber == clickList.get(indexC).batchNumber) {
            ++indexC;
            ++size;
        }
        for (int i = 1; i < queryList.size(); ++i) {
            Query query = queryList.get(i);
            if (query.documents.equals(previousQuery.documents)) {
                ++number;
                while (indexC < clickList.size() && query.batchNumber == clickList.get(indexC).batchNumber) {
                    ++indexC;
                    ++size;
                }
            } else {
                clickDocumentsSum += size;
                size = 0;
                number = 1;
            }
            previousQuery = query;
        }

        return (clickDocumentsSum / number);
    }

}
