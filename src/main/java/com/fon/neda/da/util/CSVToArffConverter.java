package com.fon.neda.da.util;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVToArffConverter {

    public static void convert(String fileName) throws IOException {
        CSVLoader loader1 = new CSVLoader();
        //System.out.println(fileName);
        loader1.setSource(new File("src/main/resources/files/" + fileName + ".csv"));
        Instances data = loader1.getDataSet();

        /*ArffSaver saver = new ArffSaver();
        Path fileToDeletePath = Paths.get("src/main/resources/files/" + fileName + ".arff");
        Files.delete(fileToDeletePath);
        saver.setInstances(data);
        saver.setFile(new File("src/main/resources/files/" + fileName + ".arff"));
        saver.writeBatch();*/
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/files/" + fileName + ".arff"));
        writer.write(data.toString());
        writer.flush();
        writer.close();
    }

}
