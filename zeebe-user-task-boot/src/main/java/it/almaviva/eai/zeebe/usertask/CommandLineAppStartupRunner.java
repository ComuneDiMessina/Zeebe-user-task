package it.almaviva.eai.zeebe.usertask;

import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final ZeebeClientLifecycle client;

    public CommandLineAppStartupRunner(ZeebeClientLifecycle client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        if (!client.isRunning()) {
            return;
        }

        final ProcessInstanceEvent event = client
                .newCreateInstanceCommand()
                .bpmnProcessId("pizza_shack")
                .latestVersion()
                .send()
                .join();

        log.info("started instance for workflowKey='{}', bpmnProcessId='{}', version='{}' with workflowInstanceKey='{}'",
                event.getProcessDefinitionKey(), event.getBpmnProcessId(), event.getVersion(), event.getProcessInstanceKey());
    }
}
