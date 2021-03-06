package com.fon.neda.da.util;

import com.fon.neda.da.entity.Evaluation;

import java.util.HashMap;

public class EvaluationDetails {
    private Evaluation evaluation;
    private int correctlyClassifiedInstances;
    private int incorrectlyClassifiedInstances;
    private int truePositives;
    private int falsePositives;
    private int falseNegatives;
    private int trueNegatives;
    private String histogramData;
    private String pcaResult;

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

    public int getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(int truePositives) {
        this.truePositives = truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(int falsePositives) {
        this.falsePositives = falsePositives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(int falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public void setTrueNegatives(int trueNegatives) {
        this.trueNegatives = trueNegatives;
    }

    public String getHistogramData() {
        return histogramData;
    }

    public void setHistogramData(String histogramData) {
        this.histogramData = histogramData;
    }

    public void setPcaResult(String pcaResult) {
        this.pcaResult = pcaResult;
    }
    public String getPcaResult() {
        return this.pcaResult;
    }
}
