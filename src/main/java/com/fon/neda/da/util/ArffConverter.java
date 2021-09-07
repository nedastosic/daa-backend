package com.fon.neda.da.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArffConverter {

    public static void convert(String fileName, String className) throws IOException {
        int classIndex = 0;
        List<String> classes = new ArrayList<>();
        List<String> uniqueClasses = new ArrayList<>();
        String result = "";

        List<List<String>> records = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(new FileReader("src/main/resources/files/" + fileName + ".csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        for (List<String> s : records) {
            if (records.get(0) == s){
                classIndex = s.indexOf(className);
                continue;
            }
            classes.add(s.get(classIndex));
        }

        for (String s : classes) {
            if (!uniqueClasses.contains(s)) {
                uniqueClasses.add(s);
            }
        }


        for (String s : uniqueClasses) {
            if (!s.equals(uniqueClasses.get(uniqueClasses.size() - 1))) {
                result += s + ",";
            } else {
                result += s;
            }
        }

        Path path = Paths.get("src/main/resources/files/" + fileName + ".arff");
        Charset charset = StandardCharsets.UTF_8;

        String content = Files.readString(path, charset);
        content = content.replaceAll(className + " numeric", className + " {" + result + "}");
        Files.write(path, content.getBytes(charset));

    }

    public static void main(String[] args) throws IOException {
        ArffConverter.convert("heart.arff", "target");
    }
}
