package it.almaviva.eai.zeebe.usertask.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "TaskDomain")
public class TaskDomain {

    @ApiModelProperty(required = true, value = "Identificativo del task ", example = "6755399441078273")
    private long key;

    @ApiModelProperty(required = true, value = "Identificativo istanza del workflow", example = "6755399441078207")
    private long workflowInstanceKey;

    @ApiModelProperty(required = true, value = "Data di creazione del task", example = "2020-10-08T16:42:29.575444")
    private LocalDateTime creationTime;

    @ApiModelProperty(required = true, value = "Data di presa in carico del task", example = "2020-10-08T16:42:29.575444")
    private LocalDateTime assignmentTime;

    @ApiModelProperty(required = true, value = "Nome del task ", example = "ServiceTask_10l06gt")
    private String name;

    @ApiModelProperty(required = true, value = "Variabili del task ", example = "{\"dirPath\":\"/opt/data/Impleme/CaseFile_c9acd250-47b5-40ae-af7f-addd07048091/tmp\",\"idCaseFile\":\"c9acd250-47b5-40ae-af7f-addd07048091\",\"idRegistry\":\"834\",\"pathAttachments\":\"\",\"pathCaseFilePDF\":\"\"}")
    private String variables;

    private String formFields;

    private String description;

    @ApiModelProperty(required = true, value = "JSON Schema", notes = "Se presente è utilizzato per validare il payload inviato nella chiama POST /complete", example = "{ \"$schema\": \"http://json-schema.org/draft-07/schema#\", \"title\": \"User Task\", \"description\": \"Payload\", \"type\": \"object\", \"properties\": { \"evaluation\": { \"description\": \"Il campo amount va valorizzato solo quando è necessario da parte dell’operatore comunale di rendere PAGABILE la pratica\", \"enum\": [ \"CHIUDIBILE\", \"PAGABILE\", \"INTEGRABILE\" ] }, \"message\": { \"description\": \"Messaggio libero inputato dall’operatore\", \"type\": \"string\" }, \"amount\": { \"type\": \"number\", \"description\": \"Importo da pagare\" } }, \"dependencies\": { \"amount\": { \"required\": [ \"evaluation\" ] } }, \"oneOf\": [ { \"properties\": { \"evaluation\": { \"const\": \"PAGABILE\" } }, \"required\": [ \"amount\" ] }, { \"properties\": { \"evaluation\": { \"const\": \"CHIUDIBILE\" } } }, { \"properties\": { \"evaluation\": { \"const\": \"INTEGRABILE\" } } } ] }")
    private String jsonSchema;

    @ApiModelProperty(required = true, value = "Assegnatario del task ", example = "m.rossi")
    private String assignee;

    @ApiModelProperty(required = true, value = "Elenco dei Gruppi utente che possono richiedere il task ", example = "OPERATORE, AMMINISTRATORE")
    private String candidateGroups;

    @ApiModelProperty(required = true, value = "Elenco degl utenti che possono richiedere il task ", example = "m.rossi, r.bianchi")
    private String candidateUsers;

    private String visibility;

}
