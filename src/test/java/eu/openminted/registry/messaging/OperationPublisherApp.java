package eu.openminted.registry.messaging;

import java.util.UUID;

import javax.jms.JMSException;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import eu.openminted.messageservice.connector.MessageServicePublisher;
import eu.openminted.messageservice.connector.TopicsRegistry;
import eu.openminted.messageservice.messages.WorkflowExecutionStatusMessage;


@Configuration
//@PropertySource(value = { "classpath:application.properties", "classpath:registry.properties"} )
public class OperationPublisherApp {
	
	static final Logger logger = Logger.getLogger(OperationPublisherApp.class);
	
	//@Value("${jms.host}")
	private static String messagesHost = "tcp://<domain>:<port>";
	
	private static String topic = TopicsRegistry.workflowsExecution;
	
	private static String userID = "0931731143127784@openminted.eu";
	private static String workflowID = "workflowID";
	
	private static String[] workflowExecutionStatus = {
        "PENDING",
        "RUNNING",
        "PAUSED",
        "FINISHED",
        "CANCELED",
        "FAILED"
	};

	public static void main(String[] args) throws JMSException{
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(
        		OperationPublisherApp.class);
  
        // Connect to Message Service
        logger.info("Creating a message posting service to  ::" + messagesHost );
		MessageServicePublisher msgServicePub = new MessageServicePublisher(messagesHost); 
		            
		//////////////////
		// Step 1 - A workflow is set to run in the workflow engine      
        WorkflowExecutionStatusMessage msgPending = new WorkflowExecutionStatusMessage(); 
        String workflowExecutionID = "WFE_ID1";//UUID.randomUUID().toString();
        msgPending.setWorkflowExecutionID(workflowExecutionID);
		msgPending.setWorkflowStatus(workflowExecutionStatus[0]);
		msgPending.setCorpusID(UUID.randomUUID().toString());
		msgPending.setUserID(userID);
		msgPending.setWorkflowID(workflowID);
		
		
        logger.info("Sending message - workflow execution :: " + msgPending.toString() );               
        // Publish message
        msgServicePub.publishMessage(topic, msgPending);
         
        
        ((AbstractApplicationContext)context).close();
  		
	}
	

}
