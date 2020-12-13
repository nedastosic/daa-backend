package com.fon.neda.da.util;

import com.fon.neda.da.entity.Evaluation;

public class EvaluationDetails {
    private Evaluation evaluation;
    private int correctlyClassifiedInstances;
    private int incorrectlyClassifiedInstances;
    private String confusionMatrix;

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public int getCorrectlyClassifiedInstances() {
        return correctlyClassifiedInstances;
    }

    public void setCorrectlyClassifiedInstances(int correctlyClassifiedInstances) {
        this.correctlyClassifiedInstances = correctlyClassifiedInstances;
    }

    public int getIncorrectlyClassifiedInstances() {
        return incorrectlyClassifiedInstances;
    }

    public void setIncorrectlyClassifiedInstances(int incorrectlyClassifiedInstances) {
        this.incorrectlyClassifiedInstances = incorrectlyClassifiedInstances;
    }

    public String getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(String confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }
}
