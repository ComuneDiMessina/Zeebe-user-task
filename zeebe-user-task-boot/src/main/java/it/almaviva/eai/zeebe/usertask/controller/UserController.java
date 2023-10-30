package it.almaviva.eai.zeebe.usertask.controller;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import it.almaviva.eai.ljsa.sdk.core.security.LjsaUser;
import it.almaviva.eai.pm.core.grpc.Group;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.incoming.ITaskUseCase;
import it.almaviva.eai.zeebe.usertask.throwable.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@EnableZeebeClient
@RestController
@Api(value= "Servizi per l'utente", authorizations =  {@Authorization(value = "X-Auth-Token"), @Authorization(value = "Bearer") })
@RequestMapping(value = "/tasks")
public class UserController {

  private final ITaskUseCase iTaskUseCase;

  private final ZeebeClientLifecycle zeebeClient;

  public UserController(ITaskUseCase iTaskUseCase, ZeebeClientLifecycle zeebeClient) {
    this.iTaskUseCase = iTaskUseCase;
    this.zeebeClient = zeebeClient;
  }

  @ResponseStatus(code = HttpStatus.OK)
  @PermitAll
  @ApiOperation(value = "Completa la lavorazione di un task", notes = "Il task viene rimosso")
  @PutMapping(path = "/{key}/complete",  consumes = MediaType.APPLICATION_JSON_VALUE)
  public void completeTask(@ApiParam(value =  "Identificativo del task", name = "key", example = "6755399441078273", required = true ) @PathVariable("key") long key, @RequestBody  Map<String, Object> taskRequest) {

    log.info("Complete Task started with key: " + key);

    final TaskDomain task = iTaskUseCase.findById(key);

    if (!task.getAssignee().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
      throw new ForbiddenException("Principal name does not match task assignee");


    String jsonSchema = task.getJsonSchema();

    JSONObject jsonObject = new JSONObject(taskRequest);
    log.debug("payload from task request: " + jsonObject);
    if(jsonSchema != null) {
      JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchema));
      Schema schema = SchemaLoader.load(rawSchema);
      schema.validate(jsonObject); // throws a ValidationException if this object is invalid
    }

    final Map<String, Object> payload = new HashMap<>(2);

    payload.put("payload", taskRequest);
    payload.put("sub", task.getAssignee());

    zeebeClient.newCompleteCommand(key).variables(payload).send()
            .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + key, throwable); });


    iTaskUseCase.delete(task);
  }


  @ResponseStatus(code = HttpStatus.OK)
  @PermitAll
  @ApiOperation(value = "Assegna un task", notes = "Il task viene assegnato all'utente autenticato")
  @PutMapping(path = "/{key}/claim", produces = MediaType.APPLICATION_JSON_VALUE)
  public void claimTask(@PathVariable("key")  @ApiParam(value =  "Identificativo del task", name = "key", example = "6755399441078273", required = true ) long key) {

    log.info("Claim Task started with key: " + key);

    LjsaUser principal = (LjsaUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    @SuppressWarnings("unchecked")
    List<String> stringGroups = convertList((List<Group>) principal.getGroups(), Group::getName);

    iTaskUseCase.assignMe(key, principal.getUsername(), stringGroups);
  }

  @ResponseStatus(code = HttpStatus.OK)
  @PermitAll
  @ApiOperation(value = "Elenco dei task assegnati", notes = "Elenco dei task assegnati e non ancora terminati")
  @GetMapping(path = "/assignee/list", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<TaskDomain> listTaskByAssignee() {
    log.info("Assigne List started ");

    final String assignee = SecurityContextHolder.getContext().getAuthentication().getName();
   
    return iTaskUseCase.findAllByAssignee(assignee);
  }

  @ResponseStatus(code = HttpStatus.OK)
  @PermitAll
  @ApiOperation(value = "Elenco dei task che possono essere presi in carico", nickname = "listDemandableTasks", notes = "Elenco dei task che l'utente autenticato può prendre in carico")
  @GetMapping(path = "/demandable/list", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<TaskDomain> listDemandableTasks() {

    log.info("Groups List started");

    LjsaUser principal = (LjsaUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    @SuppressWarnings("unchecked")
    List<String> stringGroups = convertList((List<Group>) principal.getGroups(), Group::getName);

    return iTaskUseCase.findAllDemandable(principal.getUsername(), stringGroups);

  }

  private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
    return from.stream().map(func).collect(Collectors.toList());
  }


}
