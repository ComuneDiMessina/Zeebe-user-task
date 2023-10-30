package it.almaviva.eai.zeebe.usertask.port.outgoing;


import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;

import java.util.List;

public interface ITaskPort {

    TaskDomain findByKey(long key);

    List<TaskDomain> findAllByAssignee(String assignee);

    List<TaskDomain> findAllDemandable();

    void delete(long key);

    void save(TaskDomain task);

    void assign(long key, String username);

    void disclaim(long key);

    List<TaskDomain> findAll();

    void deleteAllByWorkflowInstanceKey(long key);

  boolean alreadyExists(long key);
}
