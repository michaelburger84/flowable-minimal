package org.flowable.examples.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SayHelloWithNameDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(SayHelloWithNameDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        // Get the name from the process variables (set by the user task)
        String name = (String) execution.getVariable("userName");
        if (name == null || name.isEmpty()) {
            name = "unknown";
        }
        log.info("Hello, {} from Flowable Service Task!", name);
    }
}