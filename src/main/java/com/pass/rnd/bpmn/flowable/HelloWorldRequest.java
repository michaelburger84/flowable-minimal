package com.pass.rnd.bpmn.flowable;

import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HelloWorldRequest {
    private static final Logger log = LoggerFactory.getLogger(HelloWorldRequest.class);


    //public static final String XML_FILE = "hello-world-flowable-enriched.bpmn";
    public static final String XML_FILE = "hello-world-flowable-generated.bpmn";

    public static void main(String[] args) {
        ProcessEngine processEngine = getProcessEngine();

        if (!checkXmlFile()) return;

        // To deploy a process definition to the Flowable engine, the RepositoryService is used,
        // Using the RepositoryService, a new Deployment is created by passing the location of the XML file
        // and calling the deploy() method to actually execute it:
        RepositoryService repositoryService = getRepositoryService(processEngine);
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" exporter=\"bpmn-js (https://demo.bpmn.io)\" exporterVersion=\"18.6.1\" id=\"sid-38422fae-e03e-43a3-bef4-bd33b32041b2\" targetNamespace=\"http://bpmn.io/bpmn\"><process id=\"Process_1\" isExecutable=\"true\" name=\"Hello World Process\"><startEvent id=\"StartEvent_1y45yut\" name=\"hunger noticed\"><outgoing>SequenceFlow_0h21x7r</outgoing></startEvent><sequenceFlow id=\"SequenceFlow_0h21x7r\" sourceRef=\"StartEvent_1y45yut\" targetRef=\"Task_1hcentk\"/><serviceTask flowable:class=\"org.flowable.examples.task.HelloWorldDelegate\" id=\"Task_1hcentk\" name=\"choose recipe\"><incoming>SequenceFlow_0h21x7r</incoming><outgoing>Flow_1kfubm7</outgoing></serviceTask><endEvent id=\"Event_1sfoqr3\"><incoming>Flow_1kfubm7</incoming></endEvent><sequenceFlow id=\"Flow_1kfubm7\" sourceRef=\"Task_1hcentk\" targetRef=\"Event_1sfoqr3\"/></process><bpmndi:BPMNDiagram id=\"BpmnDiagram_1\"><bpmndi:BPMNPlane bpmnElement=\"Process_1\" id=\"BpmnPlane_1\"><bpmndi:BPMNShape bpmnElement=\"StartEvent_1y45yut\" id=\"StartEvent_1y45yut_di\"><omgdc:Bounds height=\"36\" width=\"36\" x=\"152\" y=\"102\"/><bpmndi:BPMNLabel><omgdc:Bounds height=\"14\" width=\"73\" x=\"134\" y=\"145\"/></bpmndi:BPMNLabel></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"Task_1hcentk\" id=\"Activity_18hidv9_di\"><omgdc:Bounds height=\"80\" width=\"100\" x=\"240\" y=\"80\"/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\"Event_1sfoqr3\" id=\"Event_1sfoqr3_di\"><omgdc:Bounds height=\"36\" width=\"36\" x=\"392\" y=\"102\"/></bpmndi:BPMNShape><bpmndi:BPMNEdge bpmnElement=\"SequenceFlow_0h21x7r\" id=\"SequenceFlow_0h21x7r_di\"><omgdi:waypoint x=\"188\" y=\"120\"/><omgdi:waypoint x=\"240\" y=\"120\"/></bpmndi:BPMNEdge><bpmndi:BPMNEdge bpmnElement=\"Flow_1kfubm7\" id=\"Flow_1kfubm7_di\"><omgdi:waypoint x=\"340\" y=\"120\"/><omgdi:waypoint x=\"392\" y=\"120\"/></bpmndi:BPMNEdge></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>";
        Deployment deployment = repositoryService.createDeployment()
                .addString("user.xml", text)
                //.addClasspathResource(XML_FILE)
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        log.info("Found process definition : {}", processDefinition.getName());

        // Get the RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // Start a new process instance using the process definition ID
        // The process definition ID is typically 'Process_1' in your XML
        // or you can use processDefinition.getKey() if you set a key in your BPMN.
        // For simplicity, let's use the ID from the processDefinition object directly.
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId());
        log.info("Started process instance with id '{}' and process definition id '{}'",
                processInstance.getId(), processInstance.getProcessDefinitionId());

        simulateUserTask(processEngine, processInstance);
        checkHistory(processEngine, processInstance);
    }

    private static void simulateUserTask(ProcessEngine processEngine, ProcessInstance processInstance) {
        TaskService taskService = processEngine.getTaskService(); // TaskService holen
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult(); // Den User Task finden

        log.info("Found user task: '{}' for process instance: '{}'", task.getName(), processInstance.getId());

        FormService formService = processEngine.getFormService();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        log.info("--- Form Properties for Task '{}' ---", task.getName());
        for (FormProperty formProperty : taskFormData.getFormProperties()) {
            log.info("  Form Property ID: {}", formProperty.getId());
            log.info("  Form Property Name: {}", formProperty.getName());
            log.info("  Form Property Type: {}", formProperty.getType().getName());
            log.info("  Form Property Required: {}", formProperty.isRequired());
            log.info("  Form Property Readable: {}", formProperty.isReadable());
            log.info("  Form Property Writable: {}", formProperty.isWritable());
            log.info("------------------------------------");
        }
        log.info("--- End of Form Properties ---");

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Andreas Rinner"); // Variable 'userName' setzen
        taskService.complete(task.getId(), variables);
    }

    private static void checkHistory(ProcessEngine processEngine, ProcessInstance processInstance) {
        // Since it's a simple process (Start -> Service Task -> End), it should complete immediately.
        // You can query the history service to confirm its completion.
        // (Optional, for more robust checking)
        HistoryService historyService = processEngine.getHistoryService();
        long completedInstances = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .finished()
                .count();
        if (completedInstances > 0) {
            log.info("Process instance '{}' completed successfully.", processInstance.getId());
        } else {
            log.warn("Process instance '{}' did not complete as expected.", processInstance.getId());
        }
    }

    private static boolean checkXmlFile() {
        URL resourceUrl = HelloWorldRequest.class.getClassLoader().getResource(XML_FILE);
        if (resourceUrl == null) {
            log.error("ERROR: The file '{}' was not found in the classpath. " +
                    "Please ensure it is in 'src/main/resources/' (for Maven/Gradle) or explicitly added to the classpath.", XML_FILE);
            System.exit(1); // Anwendung beenden, da die Ressource nicht gefunden wurde
            return false;
        } else {
            log.info("File '{}' found in classpath at: {}", XML_FILE, resourceUrl.toExternalForm());
        }
        return true;
    }

    private static RepositoryService getRepositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    private static ProcessEngine getProcessEngine() {
        // The 'standalone' here refers to the fact that the engine is created and used completely by itself
        // (and not, for example, in a Spring environment, where you’d use the SpringProcessEngineConfiguration
        // class instead).
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                // JDBC connection parameters to an in-memory H2 database instance
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        return cfg.buildProcessEngine();
    }

}
