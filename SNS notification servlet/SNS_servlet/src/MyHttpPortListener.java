/*
Cloud Computing Assigment 2 - This code is a servlet which takes message from SNS and stores it to Dynamo DB
Umang Patel = ujp2001, Karan Kaul - kak2210
Columbia University,CS Department

*/


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

/**
 * Servlet implementation class MyHttpPostListener
 */
public class MyHttpPortListener extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Enumeration<String> reqAttribNames;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyHttpPortListener() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Start receiving");
		String status_code = "";
		String subscribeURL = null;
		
		reqAttribNames = request.getAttributeNames();
		
		 status_code = response.getContentType();
		 
		 System.out.println("Status Code : "+status_code);
		 
		 
		 //Parsing Starts
		 
		//Get the message type header.
			String messagetype = request.getHeader("x-amz-sns-message-type");
			//If message doesn't have the message type header, don't process it.
			if (messagetype == null)
				return;

			if (messagetype.equals("Notification")) {
				
				 Scanner scan = new Scanner(request.getInputStream());
				    StringBuilder builder = new StringBuilder();
				    while (scan.hasNextLine()) {
				      builder.append(scan.nextLine());
				    }
				    InputStream bytes = new ByteArrayInputStream(builder.toString().getBytes());
				    
				    JSONParser jsonParser = new JSONParser(); 
			        JSONObject jsonObject = null;
					try {
						jsonObject = (JSONObject) jsonParser.parse(new String(builder));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        String temp=(String)jsonObject.get("Message");
			        System.out.println("Notification Message: "+ temp );
			        String recdMessage[] = temp.split("\t\t");
			        String tableName = recdMessage[0]+"_sns";
			        String tweetText = recdMessage[1];
			        String tweetCoordinates = recdMessage[2];
			        String tweetAlchemyScore = recdMessage[3];
			        String tweetId = recdMessage[4];
			        
			        //Karan key
			         String ACCESS_KEY = "KEY";
			         String SECRET_KEY = "KEY";
			        
			        
			        try {
			        	AmazonDynamoDBClient client = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
			            // Add books.
			            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			            item.put("tweet_id", new AttributeValue().withS(tweetId));
			            item.put("coordinates", new AttributeValue().withS(tweetCoordinates));
			            item.put("score", new AttributeValue().withS(tweetAlchemyScore));
			            item.put("text", new AttributeValue().withS(tweetText));
			            
			            PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
			            client.putItem(itemRequest);
			            System.out.println("Message inserted");
			            item.clear();
			        }
			        catch (Exception e){
			        	e.printStackTrace();
			        }

				
			}
	    else if (messagetype.equals("SubscriptionConfirmation"))
			{
	    	System.out.println("SubscriptionConfirmationRecd");
       
		    Scanner scan = new Scanner(request.getInputStream());
		    StringBuilder builder = new StringBuilder();
		    while (scan.hasNextLine()) {
		      builder.append(scan.nextLine());
		    }
		    System.out.println(builder);
		    InputStream bytes = new ByteArrayInputStream(builder.toString().getBytes());
	        //Map<String, String> messageMap = new ObjectMapper().readValue(bytes, Map.class);
		    JSONParser jsonParser = new JSONParser(); 
	        JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) jsonParser.parse(new String(builder));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String temp=(String)jsonObject.get("SubscribeURL");
	        System.out.println("Subscribe URL: "+ temp );

	        // Set HTTP response
	        response.setContentType("text/html");
	        response.setStatus(HttpServletResponse.SC_OK);
//	        ((Request) request).setHandled(true);
	    	
	    	status_code = "aa";
	    	Scanner sc = new Scanner(new URL(temp).openStream());
	       StringBuilder sb = new StringBuilder();
	       while (sc.hasNextLine()) {
	         sb.append(sc.nextLine());
	       }
	    }
	}
		 
		 //Parsing Ends
		 

		 
	

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		super.service(arg0, arg1);
	}

	
}
