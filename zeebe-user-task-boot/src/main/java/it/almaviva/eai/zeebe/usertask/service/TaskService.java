package it.almaviva.eai.zeebe.usertask.service;

import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.incoming.ITaskUseCase;
import it.almaviva.eai.zeebe.usertask.port.outgoing.ITaskPort;
import org.apache.commons.collections.CollectionUtils;
import org.camunda.bpm.model.xml.impl.util.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService implements ITaskUseCase {

   private final ITaskPort iTaskPort;

   public TaskService(ITaskPort iTaskPort) {
      this.iTaskPort = iTaskPort;
   }


   @Override
   public TaskDomain findById(long key) {
      return iTaskPort.findByKey(key);
   }

   @Override
   public void delete(TaskDomain task) {
      iTaskPort.delete(task.getKey());
   }

   @Override
   public void save(TaskDomain task) {
      iTaskPort.save(task);
   }

   @Override
   public void assignMe(long key, String username, List<String> groups) {
      final TaskDomain taskDomain = iTaskPort.findByKey(key);

      if(taskDomain.getAssignee() != null){
         throw new RuntimeException("Task is already assigned");
      }

      if(taskDomain.getCandidateGroups() != null && taskDomain.getCandidateUsers() != null && !CollectionUtils.containsAny(groups, StringUtil.splitCommaSeparatedList(taskDomain.getCandidateGroups()))
              && CollectionUtils.find(StringUtil.splitCommaSeparatedList(taskDomain.getCandidateUsers()), o -> o.equals(username)) == null ){
         throw new RuntimeException("You're not allowed to claim the task");
      }

      if(taskDomain.getCandidateGroups() == null && CollectionUtils.find(StringUtil.splitCommaSeparatedList(taskDomain.getCandidateUsers()), o -> o.equals(username)) == null){
         throw new RuntimeException("You're not allowed to claim the task");
      }

      if(taskDomain.getCandidateUsers() == null && !CollectionUtils.containsAny(groups, StringUtil.splitCommaSeparatedList(taskDomain.getCandidateGroups()))){
         throw new RuntimeException("You're not allowed to claim the task");
      }

      iTaskPort.assign(key, username);
   }

   @Override
   public void assignMe(long key, String username) {
      iTaskPort.assign(key, username);
   }

   @Override
   public void disclaim(long key) {
      iTaskPort.disclaim(key);
   }


   @Override
   public List<TaskDomain> findAllByAssignee(String assignee) {
      return iTaskPort.findAllByAssignee(assignee);
   }

   @Override
   public List<TaskDomain> findAllDemandable(final String assignee, final List<String> groups) {
      final List<TaskDomain> allDemandable = iTaskPort.findAllDemandable();
      if(allDemandable == null){
         return null;
      }

      allDemandable.removeIf(taskDomain -> {
         if(taskDomain.getCandidateGroups() != null && taskDomain.getCandidateUsers() != null)
         {
            return
                    !CollectionUtils.containsAny(groups, StringUtil.splitCommaSeparatedList(taskDomain.getCandidateGroups()))
                            || CollectionUtils.find(StringUtil.splitCommaSeparatedList(taskDomain.getCandidateUsers()), o -> o.equals(assignee)) == null;
         }else if(taskDomain.getCandidateGroups() != null)
         {
            return !CollectionUtils.containsAny(groups, StringUtil.splitCommaSeparatedList(taskDomain.getCandidateGroups()));
         }else
            {
            return CollectionUtils.find(StringUtil.splitCommaSeparatedList(taskDomain.getCandidateUsers()), o -> o.equals(assignee)) == null;
            }
      });

      return allDemandable;

   }

   @Override
   public List<TaskDomain> findAll() {
      return iTaskPort.findAll();
   }

   @Override
   public void deleteAllByWorkflowInstanceKey(long key) {
     iTaskPort.deleteAllByWorkflowInstanceKey(key);
   }

   @Override
   public boolean alreadyExists(long key) {
      return iTaskPort.alreadyExists(key);
   }
}
