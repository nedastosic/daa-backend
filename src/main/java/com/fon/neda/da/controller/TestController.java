package com.fon.neda.da.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fon.neda.da.algorithms.KNN;
import com.fon.neda.da.algorithms.LogisticRegression;
import com.fon.neda.da.algorithms.NaiveBayes;
import com.fon.neda.da.entity.*;
import com.fon.neda.da.service.*;
import com.fon.neda.da.util.EvaluationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.Evaluation;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class TestController {
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
        User u  = userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        List<com.fon.neda.da.entity.Evaluation> e = evaluationService.findEvaluationsByUser(u);
        return evaluationService.findEvaluationsByUser(userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
    }

    @GetMapping("/datasets")
    public List<Dataset> allDatasets() throws JsonProcessingException {
        return datasetService.findJoined();

    }

    @DeleteMapping("/evaluations/delete/{id}")
    public void myEvaluations(@PathVariable Long id) {
        evaluationService.deleteEvaluationById(id);
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return userService.findAllUsers();
    }

    @RequestMapping(path = "/datasets/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {

       /* String path = datasetService.findDatasetById(id).getPath();

        InputStreamResource resource = new InputStreamResource(new FileInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);*/

        Path path = Paths.get(datasetService.findDatasetById(id).getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

       Dataset dataset = datasetService.findDatasetById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataset.getName() + "\"")
                .body(resource);
    }


    @PostMapping(value = "/ingest", headers = "Content-Type= multipart/form-data")
    public ResponseEntity<EvaluationDetails> ingestDataFile(@RequestParam("file") MultipartFile file, @RequestParam("k") int k, @RequestParam("className") String className, @RequestParam("algorithm") int algorithm, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No File is Present");
            return null;
        }
        com.fon.neda.da.entity.Evaluation evaluation = null;
        EvaluationDetails evaluationDetails = new EvaluationDetails();


        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            //Path path = Paths.get("FOLDER TO UPLOAD TO" + file.getOriginalFilename());
            Path path = Paths.get("src/main/resources/files/" + file.getOriginalFilename());
            Files.write(path, bytes);
            User user = userService.findByUsername(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            Dataset dataset = new Dataset(file.getOriginalFilename(), path.toString(), user);
            evaluation = new com.fon.neda.da.entity.Evaluation(user, dataset);
            dataset = datasetService.save(dataset);
            evaluation = evaluationService.save(evaluation);

            System.out.println("EVALUATION ID " + evaluation.getId());



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

            Evaluation e;

            switch (algorithm) {
                case 1:
                    System.out.println(file.getOriginalFilename().split("\\.")[0]);
                    e = new KNN().knn(file.getOriginalFilename().split("\\.")[0], k, className);
                    evaluation.setPrecision(e.precision(1));
                    evaluation.setAccuracy(e.areaUnderROC(1));
                    evaluation.setRecall(e.recall(1));
                    evaluation.setF1(e.fMeasure(1));
                    evaluation.setAlgorithm(algorithmService.findAlgorithmByName("KNN"));
                    System.out.println("Class name " + className + " " + k);

                    ParameterCodelist parameterCodelist = parameterCodelistService.findParameterCodelistsByName("k");
                    Parameter parameter = new Parameter(Integer.toString(k), evaluation, parameterCodelist);
                    parameterService.save(parameter);

                    evaluationDetails.setEvaluation(evaluation);
                    evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
                    evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());
                    evaluationDetails.setConfusionMatrix(e.toMatrixString());

                    break;
                case 2:
                    e = new NaiveBayes().naiveBayes(file.getOriginalFilename().split("\\.")[0], className);
                    evaluation.setPrecision(e.precision(1));
                    evaluation.setAccuracy(e.areaUnderROC(1));
                    evaluation.setRecall(e.recall(1));
                    evaluation.setF1(e.fMeasure(1));
                    evaluation.setAlgorithm(algorithmService.findAlgorithmByName("Naive Bayes"));
                    System.out.println("Class name " + className + " " + k);

                    evaluationDetails.setEvaluation(evaluation);
                    evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
                    evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());

                    break;
                case 3:
                    e = new LogisticRegression().logisticRegression(file.getOriginalFilename().split("\\.")[0], className);
                    evaluation.setPrecision(e.precision(1));
                    evaluation.setAccuracy(e.areaUnderROC(1));
                    evaluation.setRecall(e.recall(1));
                    evaluation.setF1(e.fMeasure(1));
                    evaluation.setAlgorithm(algorithmService.findAlgorithmByName("Logistic regression"));
                    System.out.println("Class name " + className + " " + k);

                    evaluationDetails.setEvaluation(evaluation);
                    evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
                    evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());
                    break;
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


        //return ResponseEntity.ok(evaluation);
        return ResponseEntity.ok(evaluationDetails);
    }
}
