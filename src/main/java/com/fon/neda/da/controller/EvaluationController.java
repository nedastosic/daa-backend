package com.fon.neda.da.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fon.neda.da.algorithms.*;
import com.fon.neda.da.entity.*;
import com.fon.neda.da.service.*;
import com.fon.neda.da.util.EvaluationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import weka.classifiers.Evaluation;

import java.io.BufferedReader;
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
    @Autowired
    private AlgorithmParameterCodelistService algorithmParameterCodelistService;

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
    public void myEvaluations(@PathVariable Long id) {
        evaluationService.deleteEvaluationById(id);
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return userService.findAllUsers();
    }

    @RequestMapping(path = "/datasets/{id}", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {

        Path path = Paths.get(datasetService.findDatasetById(id).getPath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        Dataset dataset = datasetService.findDatasetById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dataset.getName() + "\"")
                .body(resource);
    }


    @PostMapping(value = "/ingest", headers = "Content-Type= multipart/form-data")
    public ResponseEntity<EvaluationDetails> ingestDataFile(@RequestParam("file") MultipartFile file, @RequestParam("params") String params, @RequestParam("className") String className, @RequestParam("algorithm") int algorithm, RedirectAttributes redirectAttributes) {


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

            IAlgorithm a = AlgorithmFactory.generate(algorithm, params, file.getOriginalFilename().split("\\.")[0], className);
            Evaluation e = a.evaluate();

            evaluation.setPrecision(e.precision(1));
            evaluation.setAccuracy(e.areaUnderROC(1));
            evaluation.setRecall(e.recall(1));
            evaluation.setF1(e.fMeasure(1));
            evaluation.setAlgorithm(algorithmService.findAlgorithmById(algorithm));

            for (AlgorithmParameterCodelist apc : algorithmParameterCodelistService.findAlgorithmParameterCodelistByAlgorithmId(algorithm)) {
                ParameterCodelist parameterCodelist = parameterCodelistService.findParameterCodelistsById(apc.getParameterCodelist().getId());
                Parameter parameter = new Parameter(AlgorithmFactory.getParams(algorithm, params, parameterCodelist.getName()), evaluation, parameterCodelist);
                parameterService.save(parameter);
            }


            evaluationDetails.setEvaluation(evaluation);
            evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
            evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());
            evaluationDetails.setConfusionMatrix(e.toMatrixString());


/*
            switch (algorithm) {
                case 1:
                    ObjectMapper mapper = new ObjectMapper();
                    KNNParams knnParams = new KNNParams();
                    try {
                        knnParams = mapper.readValue(params.getBytes(), KNNParams.class);
                        System.out.println(knnParams);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    e = new KNN().knn(file.getOriginalFilename().split("\\.")[0],knnParams.k, className);
                    evaluation.setPrecision(e.precision(1));
                    evaluation.setAccuracy(e.areaUnderROC(1));
                    evaluation.setRecall(e.recall(1));
                    evaluation.setF1(e.fMeasure(1));
                    evaluation.setAlgorithm(algorithmService.findAlgorithmByName("KNN"));
                    System.out.println("Class name " + className + " " + knnParams.k);

                    ParameterCodelist parameterCodelist = parameterCodelistService.findParameterCodelistsByName("k");
                    Parameter parameter = new Parameter(Integer.toString(knnParams.k), evaluation, parameterCodelist);
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
                    evaluationDetails.setEvaluation(evaluation);
                    evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
                    evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());
                    break;

                case 4:
                    evaluationDetails = new NeuralNetwork().process(file.getOriginalFilename().split("\\.")[0]);
                    evaluation.setAlgorithm(algorithmService.findAlgorithmByName("Neural network"));

                    //evaluationDetails.setEvaluation(evaluation);
                    //evaluationDetails.setCorrectlyClassifiedInstances((int) e.correct());
                    //evaluationDetails.setIncorrectlyClassifiedInstances((int) e.incorrect());
                    break;
            }
*/

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
}
