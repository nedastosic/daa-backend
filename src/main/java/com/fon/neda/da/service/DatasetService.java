package com.fon.neda.da.service;

import com.fon.neda.da.entity.Dataset;
import com.fon.neda.da.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@ComponentScan
public class DatasetService {

    @Autowired
    DatasetRepository datasetRepository;

    public Dataset save(Dataset dataset){
        return datasetRepository.save(dataset);
    }

    public List<Dataset> findJoined(){
        return datasetRepository.findJoined();
    }

    public Dataset findDatasetById(Long id){
        return datasetRepository.findDatasetById(id);
    }
}
