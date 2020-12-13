package com.fon.neda.da.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "Parameter")
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private long id;
    @Column(name="value")
    private String value;
    @ManyToOne (cascade = {CascadeType.ALL})
    @JoinColumn(name="evaluationId")
    private Evaluation evaluation;
    @ManyToOne
    @JoinColumn(name="parameterCodelistId")
    private ParameterCodelist parameterCodelist;

    public Parameter(String value, Evaluation evaluation, ParameterCodelist parameterCodelist){
        this.value = value;
        this.evaluation = evaluation;
        this.parameterCodelist = parameterCodelist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public ParameterCodelist getParameterCodelist() {
        return parameterCodelist;
    }

    public void setParameterCodelist(ParameterCodelist parameterCodelist) {
        this.parameterCodelist = parameterCodelist;
    }
}
