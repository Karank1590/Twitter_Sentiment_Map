/*
Cloud Computing Assigment 2 - This code created threads , where each thread gets messages from SQS,parses it and calls Dynamo DB to get tweets,  calls Alchemy API & sends message to SNS
Umang Patel = ujp2001, Karan Kaul - kak2210
Columbia University,CS Department

*/

import java.util.List;
import java.util.Map.Entry;
import javax.xml.bind.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.codec.binary.Base64;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class karan_change {
	


    public static void main(String[] args) throws Exception {

    	//Umang Credentials
    	String ACCESS_KEY = "KEY";
        String SECRET_KEY = "KEY";
        
    
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        Region usWest2 = Region.getRegion(Regions.US_EAST_1);
        sqs.setRegion(usWest2);

        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon SQS");
        System.out.println("===========================================\n");

        try {
            // Create a queue
            System.out.println("Creating a new SQS queue called MyQueue.\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("assi2_queue");
            String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();


            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            receiveMessageRequest.setMaxNumberOfMessages(1);
            
            System.out.println(myQueueUrl);
            ExecutorService executor = Executors.newFixedThreadPool(5);
            while(true)
            {
            	
            	List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            	for (Message message : messages) {
            		if (message == null) {
            			Thread.sleep(5000);
            		}
            		else {
            			byte[] decodedBytes = Base64.decodeBase64(message.getBody());
            			sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl,message.getReceiptHandle()));
            			//System.out.println("DecodedBytes " + new String(decodedBytes));
                        Runnable worker = new WorkerThread(new String(decodedBytes));
                        executor.execute(worker);
                        //Thread.sleep(5000);
            		}
            	}
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
