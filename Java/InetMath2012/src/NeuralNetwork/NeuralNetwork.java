package NeuralNetwork;

import MakeFeatures.MakeFeatures;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Андрей
 * Date: 19.12.12
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class NeuralNetwork {

    public static final int FEATURES_NUMBER = 24;
    public static final int NUMBER_EXAMPLES = MakeFeatures.NUMBER_SESSION;
    public static final int ALLOW_EXAMPLES_NUMBER = 4000000;
    public static final int TEST_EXAMPLES_NUMBER = 738997;

    public static String fileNameSession = "D:\\Интернет-математика 2012\\features_train_new\\sessionFeature";
    public static String fileNameUser = "D:\\Интернет-математика 2012\\features_train_new\\userFeature";

    public static String fileNameTestSession = "D:\\Интернет-математика 2012\\features_test_new\\sessionFeature";
    public static String fileNameTestUser = "D:\\Интернет-математика 2012\\features_test_new\\userFeature";

    public static String fileNameResult = "D:\\Интернет-математика 2012\\result\\Result_Encog.txt";

    public static void main(String[] args) {

        File fileSession = new File(fileNameSession);
        File fileUser = new File(fileNameUser);

        File fileTestSession = new File(fileNameTestSession);
        File fileTestUser = new File(fileNameTestUser);

        File fileResult = new File(fileNameResult);

        BasicNetwork network = trainNeuralNetwork(getDataSet(fileSession, fileUser, ALLOW_EXAMPLES_NUMBER));

        predict(getDataSet(fileTestSession, fileTestUser, TEST_EXAMPLES_NUMBER), network, fileResult);


    }

    private static NeuralDataSet getDataSet(File fileSession, File fileUser, int examplesNumber) {
        BasicNeuralDataSet trainingSet = new BasicNeuralDataSet();

        try {
            FileInputStream fisSession = new FileInputStream(fileSession);
            FileInputStream fisUser = new FileInputStream(fileUser);

            Scanner scannerSession = new Scanner(fisSession);
            Scanner scannerUser = new Scanner(fisUser);

            String[] dataSession;
            String[] dataUser;
            int indexFeatures;
            int indexExamples = 0;
            double[] OUTPUT = new double[1];
            double[] DATA = new double[FEATURES_NUMBER];
            while (scannerSession.hasNextLine() && scannerUser.hasNextLine() && indexExamples < examplesNumber) {
                if (indexExamples % 100000 == 0) {
                    System.out.println(indexExamples);
                }

                dataSession = scannerSession.nextLine().split("\t");
                dataUser = scannerUser.nextLine().split("\t");

                OUTPUT[0] = Double.parseDouble(dataSession[0]);

                indexFeatures = 0;
                for (int i = 1; i < dataSession.length; ++i) {
                    DATA[indexFeatures] = Double.parseDouble(dataSession[i]);
                    ++indexFeatures;
                }
                for (int i = 0; i < dataUser.length; ++i) {
                    DATA[indexFeatures] = Double.parseDouble(dataUser[i]);
                    ++indexFeatures;
                }

                trainingSet.add(new BasicMLData(DATA), new BasicMLData(OUTPUT));

                ++indexExamples;
            }

            try {
                fisSession.close();
                fisUser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return trainingSet;
    }

    private static BasicNetwork trainNeuralNetwork(NeuralDataSet trainingSet) {
        int inputLayerNNumber = FEATURES_NUMBER;
        int hiddenLayerNNumber = 400;
        int outputLayerNNumber = 1;
        int numberIterations = 5;

        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputLayerNNumber));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenLayerNNumber));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, outputLayerNNumber));
        network.getStructure().finalizeStructure();
        network.reset();

        final Train train = new Backpropagation(network, trainingSet);

        do {
            train.iteration();
            System.out.println("Iteration " + train.getIteration() + ", Error : " + train.getError());
        } while (train.getIteration() < numberIterations && train.getError() > 0.01);

        return network;
    }

    private static void predict(NeuralDataSet testSet, BasicNetwork network, File fileResult) {
        HashMap<Integer, Double> resultMap = new HashMap<Integer, Double>();
        ValueComparator bvc = new ValueComparator(resultMap);
        TreeMap<Integer, Double> sortedResultMap = new TreeMap<Integer, Double>(bvc);

        int sessionIdTest = 7856734;
        for (MLDataPair pair : testSet) {
            final MLData output = network.compute(pair.getInput());
            resultMap.put(sessionIdTest, output.getData(0));
            ++sessionIdTest;
        }
        sortedResultMap.putAll(resultMap);

        try {
            FileOutputStream fosResult = new FileOutputStream(fileResult);
            PrintWriter pwResult = new PrintWriter(fosResult);
            for (Integer key : sortedResultMap.keySet()) {
                pwResult.println(key);
            }
            pwResult.flush();

            try {
                fosResult.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
