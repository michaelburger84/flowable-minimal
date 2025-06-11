package org.flowable.examples.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class HelloWorldDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // You could also set variables here, call external systems, etc.
        System.out.println("Hello World from Flowable Service Task!");
    }
}
