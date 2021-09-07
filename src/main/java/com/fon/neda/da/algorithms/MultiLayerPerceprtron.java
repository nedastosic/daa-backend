package com.fon.neda.da.algorithms;

import com.fon.neda.da.util.EvaluationDetails;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.error.MeanSquaredError;
import org.neuroph.eval.ClassifierEvaluator;
import org.neuroph.eval.ErrorEvaluator;
import org.neuroph.eval.classification.ClassificationMetrics;
import org.neuroph.eval.classification.ConfusionMatrix;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.data.norm.MaxNormalizer;
import org.neuroph.util.data.norm.Normalizer;
import com.fon.neda.da.entity.Evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MultiLayerPerceprtron implements LearningEventListener, IAlgorithm {
    private String fileName;
    private int hiddenLayer;
    private int numberOfHiddenLayers;
    private double learningRate;
    private double maxError;
    private int maxIterations;

    private int total, correct, incorrect;

    // if output is greater then this value it is considered as malign
    private float classificationThreshold = 0.5f;

    public MultiLayerPerceprtron(String fileName, int hiddenLayer, int numberOfHiddenLayers, double learningRate, double maxError, int maxIterations) {
        this.fileName = fileName;
        this.hiddenLayer = hiddenLayer;
        this.numberOfHiddenLayers = numberOfHiddenLayers;
        this.learningRate = learningRate;
        this.maxError = maxError;
        this.maxIterations = maxIterations;
    }

    public EvaluationDetails evaluate() throws Exception {
        EvaluationDetails eval;
        //String dataSetFile = "src/main/resources/files/diabetes.csv";
        String dataSetFile = "src/main/resources/files/" + fileName + ".csv";
        BufferedReader reader = new BufferedReader(new FileReader(dataSetFile));
        String line = reader.readLine();
        String[] values = line.split(",");

        int inputsCount = values.length - 1;
        int outputsCount = 1;

        /*System.out.println("File name: " + fileName);
        System.out.println("hiddenLayer: " + hiddenLayer);
        System.out.println("numberOfHiddenLayers: " + numberOfHiddenLayers);
        System.out.println("learningRate: " + learningRate);
        System.out.println("maxError: " + maxError);
        System.out.println("maxIterations: " + maxIterations);*/


        // Create data set from file
        DataSet dataSet = DataSet.createFromFile(dataSetFile, inputsCount, outputsCount, ",", true);

        // Creatinig training set (70%) and test set (30%)
        DataSet[] trainTestSplit = dataSet.split(0.7, 0.3);
        DataSet trainingSet = trainTestSplit[0];
        DataSet testSet = trainTestSplit[1];

        // Normalizing training and test set
        Normalizer normalizer = new MaxNormalizer(trainingSet);
        normalizer.normalize(trainingSet);
        normalizer.normalize(testSet);

        //System.out.println("Creating neural network...");
        //Create MultiLayerPerceptron neural network

        int[] networkConfiguration = new int[numberOfHiddenLayers + 2];

        networkConfiguration[0] = inputsCount;
        networkConfiguration[networkConfiguration.length - 1] = outputsCount;

        for (int i = 1; i <= numberOfHiddenLayers; i++) {
            networkConfiguration[i] = hiddenLayer;
        }

        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(networkConfiguration);
        //attach listener to learning rule
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.addListener(this);

        learningRule.setLearningRate(learningRate);
        learningRule.setMaxError(maxError);
        learningRule.setMaxIterations(maxIterations);

        //System.out.println("Training network...");
        //train the network with training set
        neuralNet.learn(trainingSet);

        //System.out.println("Testing network...");
        eval = evaluate(neuralNet, testSet);

        return eval;
    }


    public void testNeuralNetwork(org.neuroph.core.NeuralNetwork neuralNet, DataSet testSet) {

        System.out.println("**********************RESULT**********************");
        for (DataSetRow testSetRow : testSet.getRows()) {
            neuralNet.setInput(testSetRow.getInput());
            neuralNet.calculate();

            // get network output
            double[] networkOutput = neuralNet.getOutput();
            int predicted = interpretOutput(networkOutput);

            // get target/desired output
            double[] desiredOutput = testSetRow.getDesiredOutput();
            int target = (int) desiredOutput[0];

            // count predictions
            countPredictions(predicted, target);
        }

        System.out.println("Total cases: " + total + ". ");
        System.out.println("Correctly predicted cases: " + correct);
        System.out.println("Incorrectly predicted cases: " + incorrect);
        double percentTotal = (correct / (double) total) * 100;
        System.out.println("Predicted correctly: " + formatDecimalNumber(percentTotal) + "%. ");
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED)) {
            double error = bp.getTotalNetworkError();
            //System.out.println("Training completed in " + bp.getCurrentIteration() + " iterations, ");
            //System.out.println("With total error: " + formatDecimalNumber(error));
        } else {
            //System.out.println("Iteration: " + bp.getCurrentIteration() + " | Network error: " + bp.getTotalNetworkError());
        }
    }

    public int interpretOutput(double[] array) {
        if (array[0] >= classificationThreshold) {
            return 1;
        } else {
            return 0;
        }
    }

    public void countPredictions(int prediction, int target) {
        if (prediction == target) {
            correct++;
        } else {
            incorrect++;
        }
        total++;
    }

    //Formating decimal number to have 3 decimal places
    public String formatDecimalNumber(double number) {
        return new BigDecimal(number).setScale(4, RoundingMode.HALF_UP).toString();
    }

    public EvaluationDetails evaluate(org.neuroph.core.NeuralNetwork neuralNet, DataSet dataSet) {
        Evaluation eval = new Evaluation();
        //System.out.println("Calculating performance indicators for neural network.");

        org.neuroph.eval.Evaluation evaluation = new org.neuroph.eval.Evaluation();
        evaluation.addEvaluator(new ErrorEvaluator(new MeanSquaredError()));

        //int[] classLabels = new int[]{0,1};
        evaluation.addEvaluator(new ClassifierEvaluator.Binary(0.5));
        evaluation.evaluate(neuralNet, dataSet);

        ClassifierEvaluator evaluator = evaluation.getEvaluator(ClassifierEvaluator.Binary.class);
        ConfusionMatrix confusionMatrix = evaluator.getResult();
        /*System.out.println("Confusion matrrix:\r\n");
        System.out.println(confusionMatrix.toString() + "\r\n\r\n");
        System.out.println("Classification metrics\r\n");*/
        ClassificationMetrics[] metrics = ClassificationMetrics.createFromMatrix(confusionMatrix);
        ClassificationMetrics.Stats average = ClassificationMetrics.average(metrics);
        /*for (ClassificationMetrics cm : metrics) {
            System.out.println(cm.toString() + "\r\n");
        }
        System.out.println(average.toString());*/

        eval.setAccuracy(average.accuracy);
        eval.setRecall(average.recall);
        eval.setF1(average.fScore);
        eval.setPrecision(average.precision);

        EvaluationDetails ed = new EvaluationDetails();
        ed.setEvaluation(eval);
        ed.setCorrectlyClassifiedInstances(confusionMatrix.getTruePositive(1) + confusionMatrix.getTrueNegative(1));
        ed.setIncorrectlyClassifiedInstances(confusionMatrix.getFalsePositive(1) + confusionMatrix.getFalseNegative(1));
        ed.setTruePositives(confusionMatrix.getTruePositive(1));
        ed.setFalsePositives(confusionMatrix.getFalsePositive(1));
        ed.setFalseNegatives(confusionMatrix.getFalseNegative(1));
        ed.setTrueNegatives(confusionMatrix.getTrueNegative(1));

        return ed;
    }


}
