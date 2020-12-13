package com.fon.neda.da.repository;


import com.fon.neda.da.entity.Dataset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends CrudRepository<Dataset, Long> {
        @Query(
                value = "SELECT * FROM  dataset d join user u on d.userId = u.id",
                nativeQuery = true)
        List<Dataset> findJoined();

        Dataset findDatasetById(Long id);
}
