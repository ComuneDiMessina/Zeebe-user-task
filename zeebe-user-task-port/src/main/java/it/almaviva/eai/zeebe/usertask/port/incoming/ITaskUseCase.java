package it.almaviva.eai.zeebe.usertask.port.incoming;

import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;

import java.util.List;

public interface ITaskUseCase {
    TaskDomain findById(long key);

    void delete(TaskDomain task);

    void save(TaskDomain task);

    void assignMe(long key, String username, List<String> stringGroups);

    void assignMe(long key, String username);
    
    void disclaim(long key);

    List<TaskDomain> findAllByAssignee(String assignee);

    List<TaskDomain> findAllDemandable(String assignee, List<String> groups);

    List<TaskDomain> findAll();

    void deleteAllByWorkflowInstanceKey(long key);

    boolean alreadyExists(long key);
}
