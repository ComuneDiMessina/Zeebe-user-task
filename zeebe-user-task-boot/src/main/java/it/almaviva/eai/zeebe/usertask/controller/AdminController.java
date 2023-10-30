package it.almaviva.eai.zeebe.usertask.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import it.almaviva.eai.zeebe.usertask.domain.TaskDomain;
import it.almaviva.eai.zeebe.usertask.port.incoming.ITaskUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Slf4j
@RestController
@Api( value = "Servizi per l'amministratore", authorizations =  {@Authorization(value = "X-Auth-Token"), @Authorization(value = "Bearer") })
@RequestMapping("/admin/tasks")
public class AdminController {


    private final ITaskUseCase iTaskUseCase;

    public AdminController(ITaskUseCase iTaskUseCase) {
        this.iTaskUseCase = iTaskUseCase;
    }


    @RolesAllowed("ZEEBE_MANAGER")
    @PutMapping(path = "/{key}/changeclaim")
    @ApiOperation(value = "Assegna il task all'utente amministratore", notes = "L'amministratore può richiedere qualsiasi task anche un task assegnato")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void changeClaimTask(@PathVariable("key") @ApiParam(value =  "Identificativo del task", name = "key", example = "6755399441078273", required = true )  long key) {
    	log.info("Change Claim Task started with key: " + key);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        iTaskUseCase.assignMe(key, principal.getUsername());
  }

    @RolesAllowed("ZEEBE_MANAGER")
    @PutMapping(path = "/{key}/disclaim")
    @ApiOperation(value = "Annulla il claim di un task", notes = "Il task torna libero e può essere richiesto da un utente")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void disclaimTask(@PathVariable("key") @ApiParam(value =  "Identificativo del task", name = "key", example = "6755399441078273", required = true )  long key) {
        log.info("Disclaim Task started with key: " + key);
        iTaskUseCase.disclaim(key);
    }

    @RolesAllowed("ZEEBE_MANAGER")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Restituisce la lista dei task", notes = "La lista contiene i task assegnati e non")
    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskDomain> listTasks() {
        log.info("All Task List started");
        return iTaskUseCase.findAll();
    }

    @RolesAllowed("ZEEBE_MANAGER")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    @ApiOperation(value = "Elimina tutti i task di una istanza di workflow", notes = "L'operazione è irreversibile")
    @DeleteMapping(path = "/workflows/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTasks(@PathVariable("key") @ApiParam(value =  "Identificativo del task", name = "key", example = "6755399441078273", required = true )  long key) {
        log.info("deleteTasks for wk instance with key: "+key);
         iTaskUseCase.deleteAllByWorkflowInstanceKey(key);
    }


}