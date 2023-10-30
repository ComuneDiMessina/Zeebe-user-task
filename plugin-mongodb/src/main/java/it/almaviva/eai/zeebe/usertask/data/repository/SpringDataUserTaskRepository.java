package it.almaviva.eai.zeebe.usertask.data.repository;


import it.almaviva.eai.zeebe.usertask.data.entity.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataUserTaskRepository extends MongoRepository<TaskDocument, Long> {


  List<TaskDocument> findAllByAssignee(String assignee);

  Optional<TaskDocument> findByKey(long key);

  @Query("'assignee': null")
  List<TaskDocument> findAllDemandable();

}
