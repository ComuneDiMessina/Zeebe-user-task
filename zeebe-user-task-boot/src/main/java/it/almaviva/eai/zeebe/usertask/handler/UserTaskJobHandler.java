package it.almaviva.eai.zeebe.usertask.handler;


import com.hubspot.jinjava.Jinjava;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.incoming.ITaskUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;


@Component
@Slf4j
public class UserTaskJobHandler implements JobHandler, Flow.Publisher<TaskDomain> {

  @Autowired
  private ITaskUseCase iTaskUseCase;


  @Autowired
  private SimpMessagingTemplate webSocket;


  public static final String ZEEBE_ATTRIBUTE_ASSIGNEE = "assignee";
  public static final String ZEEBE_ATTRIBUTE_CANDIDATE_GROUPS = "candidateGroups";
  public static final String ZEEBE_ATTRIBUTE_CANDIDATE_USERS = "candidateUsers";
  public static final String ZEEBE_ATTRIBUTE_VISIBILITY = "visibility";

  private final SubmissionPublisher<TaskDomain> publisher = new SubmissionPublisher<>();


  @Override
  @ZeebeWorker
  public void handle(JobClient client, ActivatedJob job) {

    final TaskDomain taskDomain = new TaskDomain();

    final long key = job.getKey();

    if(iTaskUseCase.alreadyExists(key)){
      return;
    }


    taskDomain.setKey(key);
    taskDomain.setWorkflowInstanceKey(job.getProcessInstanceKey());
    taskDomain.setCreationTime(LocalDateTime.now());
    taskDomain.setVariables(job.getVariables());

    final Map<String, String> customHeaders = job.getCustomHeaders();
    final Map<String, Object> variables = job.getVariablesAsMap();

    final String name = customHeaders.getOrDefault("name", job.getElementId());
    taskDomain.setName(name);

    final String description = customHeaders.get("description");
    taskDomain.setDescription(description);

    final String formFields = customHeaders.get("formFields");
    taskDomain.setFormFields(formFields);

    final String jsonSchema = customHeaders.get("jsonSchema");
    taskDomain.setJsonSchema(jsonSchema);

    Jinjava jinjava = new Jinjava();

    final String assignee = customHeaders.getOrDefault(ZEEBE_ATTRIBUTE_ASSIGNEE, (String) variables.get(ZEEBE_ATTRIBUTE_ASSIGNEE));

    if (assignee != null) {
      Optional<String> opt1 = Optional.of(jinjava.render(assignee, variables));
      taskDomain.setAssignee(opt1.filter(s -> !s.isEmpty()).orElse(null));
      taskDomain.setAssignmentTime(LocalDateTime.now());
    }


    final String candidateGroups = customHeaders.getOrDefault(ZEEBE_ATTRIBUTE_CANDIDATE_GROUPS, (String) variables.get(ZEEBE_ATTRIBUTE_CANDIDATE_GROUPS));

    if (candidateGroups != null) {
      Optional<String> opt2 = Optional.of(jinjava.render(candidateGroups, variables));
      taskDomain.setCandidateGroups(opt2.filter(s -> !s.isEmpty()).orElse(null));
    }


    final String candidateUsers = customHeaders.getOrDefault(ZEEBE_ATTRIBUTE_CANDIDATE_USERS, (String) variables.get(ZEEBE_ATTRIBUTE_CANDIDATE_USERS));
    if (candidateUsers != null) {
      Optional<String> opt3 = Optional.of(jinjava.render(candidateUsers, variables));
      taskDomain.setCandidateUsers(opt3.filter(s -> !s.isEmpty()).orElse(null));
    }

    final String visibility = customHeaders.getOrDefault(ZEEBE_ATTRIBUTE_VISIBILITY, (String) variables.get(ZEEBE_ATTRIBUTE_VISIBILITY));
    if(visibility != null){
      Optional<String> optVis = Optional.of(jinjava.render(visibility, variables));
      taskDomain.setVisibility(optVis.filter(s -> !s.isEmpty()).orElse(null));
    }


    iTaskUseCase.save(taskDomain);
    publisher.submit(taskDomain);
    webSocket.convertAndSend("/notifications/tasks", "new task");
  }


  @Override
  public void subscribe(Flow.Subscriber<? super TaskDomain> subscriber) {
    publisher.subscribe(subscriber);
  }


}
