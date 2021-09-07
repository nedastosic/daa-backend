package com.fon.neda.da;

import com.fon.neda.da.algorithms.*;
import com.fon.neda.da.entity.Algorithm;
import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import com.fon.neda.da.util.EvaluationDetails;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@SpringBootTest
class DataAnalysisApplicationTests {

    @Test
    void performAlgorithmsTest() throws Exception {
        String datasetsDirectoryPath = "src/main/resources/files/datasets";
        File dir = new File(datasetsDirectoryPath);
        File[] directoryListing = dir.listFiles();
        long usedMemoryBefore;
        long usedMemoryAfter;
        long startTime;
        long endTime;
        Runtime runtime = Runtime.getRuntime();

        if (directoryListing != null) {

            try (PrintWriter writer = new PrintWriter(new File("results.csv"))) {
                StringBuilder sb = new StringBuilder();

                for (File subdirectory : directoryListing) {
                    //System.out.println("folder: " + subdirectory.getName());
                    File file = new File(datasetsDirectoryPath + "/" + subdirectory.getName());
                    File[] directoryListing1 = file.listFiles();
                    for (File dataset : directoryListing1) {
                        // IMPORTANT:  ******************* REMOVE .arff AND -mlp.csv FILES *******************
                        //System.out.println("\t" + dataset.getName());
                        String fileName = "datasets/" + subdirectory.getName() + "/" + (dataset.getName().split(".csv"))[0];
                        CSVToArffConverter.convert(fileName);


                        ArffConverter.convert(fileName, "Class");
                        Instances rawData = (new ConverterUtils.DataSource("src/main/resources/files/" + fileName + ".arff")).getDataSet();
                        rawData.setClassIndex(rawData.numAttributes() - 1);
                        NominalToBinary nominalToBinary = new NominalToBinary();
                        nominalToBinary.setInputFormat(rawData);
                        Instances dataAfterOneHotEncoding = Filter.useFilter(rawData, nominalToBinary);
                        CSVSaver saver = new CSVSaver();
                        saver.setInstances(dataAfterOneHotEncoding);

                        String newFileName = "datasets/" + subdirectory.getName() + "/" + (dataset.getName().split(".csv"))[0] + "-mlp";
                        saver.setFile(new File("src/main/resources/files/" + newFileName + ".csv"));
                        saver.writeBatch();

                        MultiLayerPerceprtron mlp = (MultiLayerPerceprtron) AlgorithmFactory.generate(4, "{\"hiddenLayer\":\"64\",\"numberOfHiddenLayers\":\"2\",\"learningRate\":\"0.5\",\"maxError\":\"0.01\",\"maxIterations\":\"100\"}", newFileName, "Class");
                        System.gc();
                        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                        startTime = System.nanoTime();
                        EvaluationDetails evaluationDetailsMLP = mlp.evaluate();
                        endTime = System.nanoTime();
                        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                        sb.append("Multilayer perceptrone");
                        sb.append("," + subdirectory.getName());
                        sb.append("," + dataset.getName());
                        sb.append("," + evaluationDetailsMLP.getCorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsMLP.getIncorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsMLP.getEvaluation().getAccuracy());
                        sb.append("," + evaluationDetailsMLP.getEvaluation().getPrecision());
                        sb.append("," + evaluationDetailsMLP.getEvaluation().getRecall());
                        sb.append("," + evaluationDetailsMLP.getEvaluation().getF1());
                        sb.append("," + ((endTime - startTime) / 1000000));
                        sb.append("," + (usedMemoryAfter - usedMemoryBefore));
                        sb.append("\n");

                        KNN knn = (KNN) AlgorithmFactory.generate(1, "{\"k\":\"3\"}", fileName, "Class");
                        System.gc();
                        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                        startTime = System.nanoTime();
                        EvaluationDetails evaluationDetailsKNN = knn.evaluate();
                        endTime = System.nanoTime();
                        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                        sb.append("KNN");
                        sb.append("," + subdirectory.getName());
                        sb.append("," + dataset.getName());
                        sb.append("," + evaluationDetailsKNN.getCorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsKNN.getIncorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsKNN.getEvaluation().getAccuracy());
                        sb.append("," + evaluationDetailsKNN.getEvaluation().getPrecision());
                        sb.append("," + evaluationDetailsKNN.getEvaluation().getRecall());
                        sb.append("," + evaluationDetailsKNN.getEvaluation().getF1());
                        sb.append("," + ((endTime - startTime) / 1000000));
                        sb.append("," + (usedMemoryAfter - usedMemoryBefore));
                        sb.append("\n");

                        DecisionTree decisionTree = (DecisionTree) AlgorithmFactory.generate(2, null, fileName, "Class");
                        System.gc();
                        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                        startTime = System.nanoTime();
                        EvaluationDetails evaluationDetailsDT = decisionTree.evaluate();
                        endTime = System.nanoTime();
                        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                        sb.append("Decision tree");
                        sb.append("," + subdirectory.getName());
                        sb.append("," + dataset.getName());
                        sb.append("," + evaluationDetailsDT.getCorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsDT.getIncorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsDT.getEvaluation().getAccuracy());
                        sb.append("," + evaluationDetailsDT.getEvaluation().getPrecision());
                        sb.append("," + evaluationDetailsDT.getEvaluation().getRecall());
                        sb.append("," + evaluationDetailsDT.getEvaluation().getF1());
                        sb.append("," + ((endTime - startTime) / 1000000));
                        sb.append("," + (usedMemoryAfter - usedMemoryBefore));
                        sb.append("\n");

                        LogisticRegression logisticRegression = (LogisticRegression) AlgorithmFactory.generate(3, null, fileName, "Class");
                        System.gc();
                        usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                        startTime = System.nanoTime();
                        EvaluationDetails evaluationDetailsLR = logisticRegression.evaluate();
                        endTime = System.nanoTime();
                        usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                        sb.append("Logistic regression");
                        sb.append("," + subdirectory.getName());
                        sb.append("," + dataset.getName());
                        sb.append("," + evaluationDetailsLR.getCorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsLR.getIncorrectlyClassifiedInstances());
                        sb.append("," + evaluationDetailsLR.getEvaluation().getAccuracy());
                        sb.append("," + evaluationDetailsLR.getEvaluation().getPrecision());
                        sb.append("," + evaluationDetailsLR.getEvaluation().getRecall());
                        sb.append("," + evaluationDetailsLR.getEvaluation().getF1());
                        sb.append("," + ((endTime - startTime) / 1000000));
                        sb.append("," + (usedMemoryAfter - usedMemoryBefore));
                        sb.append("\n");

                    }

                }


                writer.write(sb.toString());

            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }

            /*for (File subdirectory : directoryListing) {
                //System.out.println("folder: " + subdirectory.getName());
                File file = new File(datasetsDirectoryPath + "/" + subdirectory.getName());
                File[] directoryListing1 = file.listFiles();
                for (File dataset : directoryListing1) {
                    // IMPORTANT:  ******************* REMOVE .arff AND -mlp.csv FILES *******************
                    //System.out.println("\t" + dataset.getName());
                    String fileName = "datasets/" + subdirectory.getName() + "/" + (dataset.getName().split(".csv"))[0];
                    CSVToArffConverter.convert(fileName);


                    ArffConverter.convert(fileName, "Class");
                    Instances rawData = (new ConverterUtils.DataSource("src/main/resources/files/" + fileName + ".arff")).getDataSet();
                    rawData.setClassIndex(rawData.numAttributes() - 1);
                    NominalToBinary nominalToBinary = new NominalToBinary();
                    nominalToBinary.setInputFormat(rawData);
                    Instances dataAfterOneHotEncoding = Filter.useFilter(rawData, nominalToBinary);
                    CSVSaver saver = new CSVSaver();
                    saver.setInstances(dataAfterOneHotEncoding);

                    String newFileName = "datasets/" + subdirectory.getName() + "/" + (dataset.getName().split(".csv"))[0] + "-mlp";
                    saver.setFile(new File("src/main/resources/files/" + newFileName + ".csv"));
                    saver.writeBatch();

                    MultiLayerPerceprtron mlp = (MultiLayerPerceprtron) AlgorithmFactory.generate(4, "{\"hiddenLayer\":\"64\",\"numberOfHiddenLayers\":\"2\",\"learningRate\":\"0.5\",\"maxError\":\"0.01\",\"maxIterations\":\"100\"}", newFileName, "Class");
                    System.gc();
                    usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                    startTime = System.nanoTime();
                    EvaluationDetails evaluationDetailsMLP = mlp.evaluate();
                    endTime = System.nanoTime();
                    usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                    System.out.println("Category: " + subdirectory.getName() + "; Dataset: " + dataset.getName() + "; Algorithm: Multilayer Perceptrone; numberOfHiddenLayers : 2; learningRate : 0.5; maxError:0.1; maxIterations:10000" +
                            "; Correctly classified instances: " + evaluationDetailsMLP.getCorrectlyClassifiedInstances() + "; Incorrectly classified instances: "
                            + evaluationDetailsMLP.getIncorrectlyClassifiedInstances() + "; Accuracy: " + evaluationDetailsMLP.getEvaluation().getAccuracy()
                            + "; Precision: " + evaluationDetailsMLP.getEvaluation().getPrecision() + "; Recall: " + evaluationDetailsMLP.getEvaluation().getRecall()
                            + "; F1: " + evaluationDetailsMLP.getEvaluation().getF1() + "; Elapsed time in millis: " + ((endTime - startTime) / 1000000)
                            + "; Memory in bytes: " + (usedMemoryAfter - usedMemoryBefore));

                    KNN knn = (KNN) AlgorithmFactory.generate(1, "{\"k\":\"3\"}", fileName, "Class");
                    System.gc();
                    usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                    startTime = System.nanoTime();
                    EvaluationDetails evaluationDetailsKNN = knn.evaluate();
                    endTime = System.nanoTime();
                    usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                    //System.out.println("\t" + evaluationDetailsKNN.getCorrectlyClassifiedInstances() + " " + evaluationDetailsKNN.getIncorrectlyClassifiedInstances());
                    System.out.println("Category: " + subdirectory.getName() + "; Dataset: " + dataset.getName() + "; Algorithm: KNN; k=3 " +
                            "; Correctly classified instances: " + evaluationDetailsKNN.getCorrectlyClassifiedInstances() + "; Incorrectly classified instances: "
                            + evaluationDetailsKNN.getIncorrectlyClassifiedInstances() + "; Accuracy: " + evaluationDetailsKNN.getEvaluation().getAccuracy()
                            + "; Precision: " + evaluationDetailsKNN.getEvaluation().getPrecision() + "; Recall: " + evaluationDetailsKNN.getEvaluation().getRecall()
                            + "; F1: " + evaluationDetailsKNN.getEvaluation().getF1() + "; Elapsed time in millis: " + ((endTime - startTime) / 1000000)
                            + "; Memory in bytes: " + (usedMemoryAfter - usedMemoryBefore));

                    DecisionTree decisionTree = (DecisionTree) AlgorithmFactory.generate(2, null, fileName, "Class");
                    System.gc();
                    usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                    startTime = System.nanoTime();
                    EvaluationDetails evaluationDetailsDT = decisionTree.evaluate();
                    endTime = System.nanoTime();
                    usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                    System.out.println("Category: " + subdirectory.getName() + "; Dataset: " + dataset.getName() + "; Algorithm: Decision Tree " +
                            "; Correctly classified instances: " + evaluationDetailsDT.getCorrectlyClassifiedInstances() + "; Incorrectly classified instances: "
                            + evaluationDetailsDT.getIncorrectlyClassifiedInstances() + "; Accuracy: " + evaluationDetailsDT.getEvaluation().getAccuracy()
                            + "; Precision: " + evaluationDetailsDT.getEvaluation().getPrecision() + "; Recall: " + evaluationDetailsDT.getEvaluation().getRecall()
                            + "; F1: " + evaluationDetailsDT.getEvaluation().getF1() + "; Elapsed time in millis: " + ((endTime - startTime) / 1000000)
                            + "; Memory in bytes: " + (usedMemoryAfter - usedMemoryBefore));

                    LogisticRegression logisticRegression = (LogisticRegression) AlgorithmFactory.generate(3, null, fileName, "Class");
                    System.gc();
                    usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                    startTime = System.nanoTime();
                    EvaluationDetails evaluationDetailsLR = logisticRegression.evaluate();
                    endTime = System.nanoTime();
                    usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                    System.out.println("Category: " + subdirectory.getName() + "; Dataset: " + dataset.getName() + "; Algorithm: Logistic Regression " +
                            "; Correctly classified instances: " + evaluationDetailsDT.getCorrectlyClassifiedInstances() + "; Incorrectly classified instances: "
                            + evaluationDetailsLR.getIncorrectlyClassifiedInstances() + "; Accuracy: " + evaluationDetailsLR.getEvaluation().getAccuracy()
                            + "; Precision: " + evaluationDetailsLR.getEvaluation().getPrecision() + "; Recall: " + evaluationDetailsLR.getEvaluation().getRecall()
                            + "; F1: " + evaluationDetailsLR.getEvaluation().getF1() + "; Elapsed time in millis: " + ((endTime - startTime) / 1000000)
                            + "; Memory in bytes: " + (usedMemoryAfter - usedMemoryBefore));

                }

            }*/
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }
    }

    @Test
    public static void main(String[] args) {

        try (PrintWriter writer = new PrintWriter(new File("test.csv"))) {

            StringBuilder sb = new StringBuilder();
            sb.append("id,");
            sb.append(',');
            sb.append("Name");
            sb.append('\n');

            sb.append("1");
            sb.append(',');
            sb.append("Prashant Ghimire");
            sb.append('\n');

            writer.write(sb.toString());

            System.out.println("done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}