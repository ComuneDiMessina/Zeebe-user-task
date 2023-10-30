package it.almaviva.eai.zeebe.usertask.data.repository;

import it.almaviva.eai.zeebe.usertask.data.entity.TaskEntity;
import it.almaviva.eai.zeebe.usertask.data.mapper.ITaskMapper;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.outgoing.ITaskPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class UserTaskRepository implements ITaskPort {

    @Autowired
    private SpringDataUserTaskRepository springDataUserTaskRepository;

    @Autowired
    private ITaskMapper iTaskMapper;

    @Override
    public TaskDomain findByKey(long key) {
        Optional<TaskEntity> byId = springDataUserTaskRepository.findById(key);
        TaskEntity taskEntity = byId.orElseThrow(() -> new EntityNotFoundException("[id]: "+ key));
        return iTaskMapper.map(taskEntity);

    }

    @Override
    public List<TaskDomain> findAllByAssignee(String assignee) {
        final List<TaskEntity> allByAssignee = springDataUserTaskRepository.findAllByAssignee(assignee);
        return iTaskMapper.map(allByAssignee);
    }

    @Override
    public List<TaskDomain> findAllDemandable() {
        final List<TaskEntity> allByClaimable = springDataUserTaskRepository.findAllDemandable();
        return iTaskMapper.map(allByClaimable);
    }

    @Override
    public void delete(long key) {
        springDataUserTaskRepository.deleteById(key);
    }

    @Override
    public void save(TaskDomain task) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setKey(task.getKey());
        taskEntity.setWorkflowInstanceKey(task.getWorkflowInstanceKey());
        taskEntity.setAssignee(task.getAssignee());
        taskEntity.setCandidateGroups(task.getCandidateGroups());
        taskEntity.setCandidateUsers(task.getCandidateUsers());
        taskEntity.setCreationTime(LocalDateTime.now());
        taskEntity.setFormFields(task.getFormFields());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setName(task.getName());
        taskEntity.setJsonSchema(task.getJsonSchema());
        taskEntity.setVariables(task.getVariables());
        springDataUserTaskRepository.save(taskEntity);
    }

    @Override
    public void assign(long key, String username) {
        TaskEntity taskEntity = springDataUserTaskRepository.findById(key).get();
        taskEntity.setAssignmentTime(LocalDateTime.now());
        taskEntity.setAssignee(username);
        springDataUserTaskRepository.save(taskEntity);
    }

    @Override
    public void disclaim(long key) {
        final TaskEntity taskEntity = springDataUserTaskRepository.findById(key).get();
        taskEntity.setAssignee(null);
        taskEntity.setAssignmentTime(null);
        springDataUserTaskRepository.save(taskEntity);
    }

    @Override
    public List<TaskDomain> findAll() {
        final Iterable<TaskEntity> all = springDataUserTaskRepository.findAll();
        return iTaskMapper.map(all);
    }

    @Override
    public void deleteAllByWorkflowInstanceKey(long key) {
        springDataUserTaskRepository.deleteAllByWorkflowInstanceKey(key);
    }

    @Override
    public boolean alreadyExists(long key) {
        Optional<TaskEntity> byId = springDataUserTaskRepository.findById(key);
        return byId.isPresent();
    }
}
