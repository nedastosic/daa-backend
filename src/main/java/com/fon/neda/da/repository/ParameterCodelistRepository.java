package com.fon.neda.da.repository;

import com.fon.neda.da.entity.ParameterCodelist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterCodelistRepository extends CrudRepository<ParameterCodelist, Long> {

    ParameterCodelist findParameterCodelistsByName(String name);

    ParameterCodelist findParameterCodelistsById(long id);

}
