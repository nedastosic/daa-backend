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
@Table(name = "AlgorithmParameterCodelist")
public class AlgorithmParameterCodelist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "algorithmId")
    private Algorithm algorithm;
    @ManyToOne
    @JoinColumn(name = "parameterCodelistId")
    private ParameterCodelist parameterCodelist;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public ParameterCodelist getParameterCodelist() {
        return parameterCodelist;
    }

    public void setParameterCodelist(ParameterCodelist parameterCodelist) {
        this.parameterCodelist = parameterCodelist;
    }
}
