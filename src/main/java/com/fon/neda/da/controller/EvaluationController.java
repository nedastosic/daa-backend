package com.fon.neda.da.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fon.neda.da.algorithms.*;
import com.fon.neda.da.entity.*;
import com.fon.neda.da.service.*;
import com.fon.neda.da.util.ArffConverter;
import com.fon.neda.da.util.CSVToArffConverter;
import com.fon.neda.da.util.EvaluationDetails;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.LibSVMSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class EvaluationController {
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private UserService userService;
    @Autowired
    private DatasetService datasetService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private ParameterCodelistService parameterCodelistService;
    @Autowired
    private ParameterService parameterService;

    @GetMapping("/algorithms")
    public List<Algorithm> allAlgorithms() {
        return algorithmService.findAllAlgorithms();
    }


    @GetMapping("/my_evaluations")
    public List<com.fon.neda.da.entity.Evaluation> myEvaluations() throws JsonProcessingException {
        User u = userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        List<com.fon.neda.da.entity.Evaluation> e = evaluationService.findEvaluationsByUser(u);
        return evaluationService.findEvaluationsByUser(userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
    }

    @GetMapping("/datasets")
    public List<Dataset> allDatasets() throws JsonProcessingException {
        return datasetService.findJoined();

    }

    @DeleteMapping("/evaluations/delete/{id}")
    public void deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluationById(id);
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return userService.findAllUsers();
    }

    @RequestMapping(path = "/datasets/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDataset(@PathVariable Long id) throws IOException {

        Path path = Paths.get(datasetService.findDatasetById(id).getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        Dataset dataset = datasetService.findDatasetById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataset.getName() + "\"")
                .body(resource);
    }

    @RequestMapping(path = "/datasets/preview/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> previewDataset(@PathVariable Long id) throws IOException {

        Path path = Paths.get(datasetService.findDatasetById(id).getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        Dataset dataset = datasetService.findDatasetById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataset.getName() + "\"")
                .body(resource);
    }


    @PostMapping(value = "/ingest", headers = "Content-Type= multipart/form-data")
    public ResponseEntity<EvaluationDetails> performAndSaveEvaluation(@RequestParam("file") MultipartFile file, @RequestParam("params") String params, @RequestParam("className") String className, @RequestParam("algorithm") int algorithm, RedirectAttributes redirectAttributes) {


        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No File is Present");
            return null;
        }
        com.fon.neda.da.entity.Evaluation evaluation = null;
        EvaluationDetails evaluationDetails = new EvaluationDetails();


        try {

            byte[] bytes = file.getBytes();
            //Path path = Paths.get("FOLDER TO UPLOAD TO" + file.getOriginalFilename());
            Path path = Paths.get("src/main/resources/files/" + file.getOriginalFilename());
            Files.write(path, bytes);
            User user = userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            Dataset dataset = new Dataset(file.getOriginalFilename(), path.toString(), user);
            // evaluation = new com.fon.neda.da.entity.Evaluation(user, dataset);
            dataset = datasetService.save(dataset);
            //evaluation = evaluationService.save(evaluation);

            List<List<String>> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/files/" + file.getOriginalFilename()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    records.add(Arrays.asList(values));
                }
            }

            for (List<String> s : records) {
                for (String s1 : s) {
                    System.out.print(s1 + ", ");
                }
                System.out.println();
            }

            String fileName = file.getOriginalFilename().split("\\.")[0];

            IAlgorithm a;

            CSVToArffConverter.convert(fileName);
            ArffConverter.convert(fileName, className);
            Instances rawData = (new ConverterUtils.DataSource("src/main/resources/files/" + fileName + ".arff")).getDataSet();
            rawData.setClassIndex(rawData.numAttributes() - 1);

            //One hot encoding
            NominalToBinary nominalToBinary = new NominalToBinary();
            nominalToBinary.setInputFormat(rawData);
            Instances dataAfterOneHotEncoding = Filter.useFilter(rawData, nominalToBinary);

            CSVSaver saver = new CSVSaver();
            saver.setInstances(dataAfterOneHotEncoding);

            String newFileName = fileName + "-" + System.nanoTime();
            saver.setFile(new File("src/main/resources/files/" + newFileName + ".csv"));
            saver.writeBatch();
            a = AlgorithmFactory.generate(algorithm, params, newFileName /*file.getOriginalFilename().split("\\.")[0]*/, className);
            //IAlgorithm a = AlgorithmFactory.generate(algorithm, params, newFileName /*file.getOriginalFilename().split("\\.")[0]*/, className);
            //Evaluation e = a.evaluate();

            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Used Memory before" + usedMemoryBefore);

            long startTime = System.nanoTime();
            evaluationDetails = a.evaluate();
            long endTime = System.nanoTime();
            System.out.println("Elapsed time: " + (endTime - startTime));

            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory increased:" + (usedMemoryAfter - usedMemoryBefore));

            Gson gson = new Gson();
            evaluationDetails.setHistogramData(gson.toJson(createHistogramDataFromDataset(newFileName + ".csv"/*file.getOriginalFilename()*/)));

            PrincipalComponents pca = new PrincipalComponents();
            Instances data = (new ConverterUtils.DataSource("src/main/resources/files/" + file.getOriginalFilename().split("\\.")[0] + ".arff")).getDataSet();
            pca.setCenterData(false);
            pca.buildEvaluator(data);

            double[][] pcaData = new double[data.size()][2];

            data = pca.transformedData(data);
            /*for(int i = 0; i < data.size(); i++){
                pcaData[i][0] = (data.get(i).value(0));
                //pcaData[i][1] = (data[i]).split(",")[1];
            }*/
            int i = 0;
            for (Instance instance : data) {
                pcaData[i][0] = instance.value(0);
                pcaData[i][1] = instance.value(1);
                i++;
                //pcaData[i][0] = (data.get(i).value(0));
                //pcaData[i][1] = (data[i]).split(",")[1];
            }

            for (int row = 0; row < pcaData.length; row++) {
                for (int col = 0; col < pcaData[row].length; col++) {
                    System.out.println(pcaData[row][col] + " ");
                }
                System.out.println();
            }

            evaluationDetails.setPcaResult(gson.toJson(pcaData));


            evaluation = evaluationDetails.getEvaluation();
            evaluation.setElapsedTimeInMillis((endTime - startTime) / 1000000);
            evaluation.setMemoryFootprintInBytes((usedMemoryAfter - usedMemoryBefore));
            evaluation.setAlgorithm(algorithmService.findAlgorithmById(algorithm));
            evaluation.setUser(user);
            evaluation.setDataset(dataset);
            evaluation.setTimestamp(new Timestamp(System.currentTimeMillis()));


            for (ParameterCodelist parameterCodelist : parameterCodelistService.findParameterCodelistByAlgorithmId(algorithm)) {
                Parameter parameter = new Parameter(AlgorithmFactory.getParams(algorithm, params, parameterCodelist.getName()), evaluation, parameterCodelist);
                parameterService.save(parameter);
            }

            evaluationService.save(evaluation);

            redirectAttributes.addFlashAttribute("message",
                    "File upload successful'" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(evaluationDetails);
    }

    public static HashMap<String, HashMap<Double, Integer>> createHistogramDataFromDataset(String fileName) {
        HashMap<String, HashMap<Double, Integer>> result = new HashMap<>();
        String[] columns = null;
        int i = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/files/" + fileName /*+ ".csv"*/))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineItems = line.split(",");
                if (i == 0) {
                    columns = lineItems;
                }
                for (int j = 0; j < lineItems.length; j++) {
                    if (i == 0) {
                        result.put(columns[j], new HashMap<>());
                    } else {
                        if (result.get(columns[j]).get(Double.parseDouble(lineItems[j])) == null) {
                            result.get(columns[j]).put(Double.parseDouble(lineItems[j]), 1);
                        } else {
                            int oldValue = result.get(columns[j]).get(Double.parseDouble(lineItems[j]));
                            result.get(columns[j]).put(Double.parseDouble(lineItems[j]), oldValue + 1);
                        }
                    }
                }
                i++;
            }
        } catch (Exception e) {
            // Handle any I/O problems
        }


        return result;
    }

}
