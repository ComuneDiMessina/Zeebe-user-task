package it.almaviva.eai.zeebe.usertask.data.repository;


import it.almaviva.eai.zeebe.usertask.data.entity.TaskDocument;
import it.almaviva.eai.zeebe.usertask.data.mapper.ITaskMapper;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.outgoing.ITaskPort;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserTaskRepository implements ITaskPort {

    @Autowired
    private SpringDataUserTaskRepository springDataUserTaskRepository;

    @Autowired
    private ITaskMapper iTaskMapper;


    @Override
    public TaskDomain findByKey(long key) {
        Optional<TaskDocument> byId = springDataUserTaskRepository.findByKey(key);
        TaskDocument taskEntity = byId.orElseThrow(() -> new RuntimeException("[id]: "+ key));
        return iTaskMapper.map(taskEntity);
    }

    @Override
    public List<TaskDomain> findAllByAssignee(String assignee) {
        final List<TaskDocument> allByAssignee = springDataUserTaskRepository.findAllByAssignee(assignee);
        return iTaskMapper.map(allByAssignee);
    }

    @Override
    public List<TaskDomain> findAllDemandable() {
        final List<TaskDocument> allByClaimable = springDataUserTaskRepository.findAllDemandable();
        return iTaskMapper.map(allByClaimable);
    }


    @Override
    public void delete(long key) {
        //FIXME
        springDataUserTaskRepository.deleteById(key);
    }

    @Override
    public void save(TaskDomain task) {
        TaskDocument taskDocument = new TaskDocument();
        taskDocument.setKey(task.getKey());
        taskDocument.setAssignee(task.getAssignee());
        taskDocument.setCandidateGroups(task.getCandidateGroups());
        taskDocument.setCreationTime(LocalDateTime.now());
        taskDocument.setFormFields(task.getFormFields());
        taskDocument.setDescription(task.getDescription());
        taskDocument.setName(task.getName());
        taskDocument.setJsonSchema(task.getJsonSchema());
        taskDocument.setVariables(task.getVariables());
        springDataUserTaskRepository.save(taskDocument);
    }

    @Override
    public void assign(long key, String username) {
        TaskDocument taskDocument = springDataUserTaskRepository.findById(key).get();
        taskDocument.setAssignmentTime(LocalDateTime.now());
        taskDocument.setAssignee(username);
        springDataUserTaskRepository.save(taskDocument);
    }

    @Override
    public void disclaim(long key) {
        TaskDocument taskDocument = springDataUserTaskRepository.findById(key).get();
        taskDocument.setAssignmentTime(null);
        taskDocument.setAssignee(null);
        springDataUserTaskRepository.save(taskDocument);
    }

    @Override
    public List<TaskDomain> findAll() {
        final Iterable<TaskDocument> all = springDataUserTaskRepository.findAll();
        return iTaskMapper.map(all);
    }

    @Override
    public void deleteAllByWorkflowInstanceKey(long key) {

    }

    @Override
    public boolean alreadyExists(long key) {
        return false;
    }
}
