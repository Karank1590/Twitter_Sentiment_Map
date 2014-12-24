
/*
Cloud Computing Assigment 2 - This code specifically sends message to SNS
Umang Patel = ujp2001, Karan Kaul - kak2210
Columbia University,CS Department

*/

import java.util.Date;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;


public class SNS_sender {
	String sns_msg;

	
	//karans
	static String ACCESS_KEY = "KEY";
    static String SECRET_KEY = "KEY";

    public SNS_sender(String msg)
    {
    	this.sns_msg = msg;
    	try {
			SNS_send();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    // Sender loop
    public void SNS_send() throws Exception {

        // Create a client
        AmazonSNSClient service = new AmazonSNSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));

        // Create a topic
        CreateTopicRequest createReq = new CreateTopicRequest()
            .withName("SampleTopic");
        CreateTopicResult createRes = service.createTopic(createReq);


            PublishRequest publishReq = new PublishRequest()
                .withTopicArn(createRes.getTopicArn())
                .withMessage(sns_msg);
            service.publish(publishReq);

    }
}