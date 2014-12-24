/*
Cloud Computing Assigment 2 - Contains method to call Alchemy API , contains method to create threads
Umang Patel = ujp2001, Karan Kaul - kak2210
Columbia University,CS Department

*/


import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.auth.PropertiesCredentials;




public class WorkerThread implements Runnable {

    private String message;
    private AmazonEC2         ec2;
    private AmazonS3           s3;
    private AmazonDynamoDB dynamo;
    private AmazonDynamoDBClient dynamoDB;
    
    //function to call Alchemny API
    public String AlchemyAPI(String tweet) throws IOException
    {
	String return_string ="";
    	String a=URLEncoder.encode(tweet, "UTF-8");
   	
    	String url_test="http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=ALCHEMY_KEY&&sentiment=1&showSourceText=1&text="+a+"&outputMode=json";
        URL url = new URL(url_test);
 
        //make connection
        URLConnection urlc = url.openConnection();

        //use post mode
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);

        //send query
        PrintStream ps = new PrintStream(urlc.getOutputStream());
      //  ps.print(query);
        ps.close();

        //get result
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        
        
        
        String json_text="";
        
        String l = null;
        while ((l=br.readLine())!=null) 
        {
        	json_text=json_text+l+"\n";
            //System.out.println(l);
           
        }
        br.close();
     
        

        try { 
        
        			JSONParser jsonParser = new JSONParser();	
        			JSONObject jsonObject = (JSONObject) jsonParser.parse(json_text);
        			String temp=(String)jsonObject.get("status");
        			if(temp.equals("ERROR"))
        			{
        				//System.out.println("No output!!");
        				return "not_parsed,-9999";
        			}
        			else{
        				
        						
        						
        						JSONObject jsonObject1 = (JSONObject)jsonObject.get("docSentiment");
        						String type=(String) jsonObject1.get("type");
        						return_string+=type+",";
        						//System.out.println(type);
        			
        						String score=(String) jsonObject1.get("score");
        						if(score!=null)
        						{
        							return_string+=score;
        							//System.out.println(score);
        						}
        						else
        						{
        							return_string+="0";
        						}
        						
        				}	
        		}
        catch (ParseException e) {  
        	   e.printStackTrace();  
        	  } 
        
         return return_string;

  
    	
    }

    //Code to create worker thread
    public WorkerThread(String message){
    	String tweet_id;
    	String[] msg_arr=message.split("\n");
    	String tableName=msg_arr[0];
    	
    	//Umang Key
    	String ACCESS_KEY = "KEY";
        String SECRET_KEY = "KEY";
    	
    	try{

    	dynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
    	Region usWest2 = Region.getRegion(Regions.US_EAST_1);
    	dynamoDB.setRegion(usWest2);
    	}
    	catch(Exception e)
    	{
    		
    		System.out.println(e.toString());
    		
    	}
    	
    	for (int i=0;i<msg_arr.length;i++)
    	{
    		if(i==0)
    		{
    			tableName=msg_arr[0];
    		}
    		else
    		{
    		tweet_id=msg_arr[i];
    		
    			try{
    				String d_str=dyanmoQuery(tableName,tweet_id);
    				String[] d_arr=d_str.split("\t\t"); 
    				String alchemy_return=AlchemyAPI(d_arr[0]);
    				System.out.println(alchemy_return);
   				SNS_sender s = new SNS_sender(tableName+"\t\t"+d_str+"\t\t"+alchemy_return+"\t\t"+tweet_id);
    				
    				System.out.println("SQS --> Sentiment Analysis (Alchemy API) --> SNS");
    				System.out.println("------------------------------------------------");
    				
    				}
    			catch(Exception e)
    			{
    				System.out.print(e.toString());
    			}
    		}
    		

    		

    	}
    	
    	
        this.message = message;
    }
    
    //Method to Query Dynamo DB 
    public String dyanmoQuery(String tableName, String tweetId){
    		
    	Condition hashkeycon = new Condition()
    	.withComparisonOperator(ComparisonOperator.EQ)
    	.withAttributeValueList(new AttributeValue().withS(tweetId));

    	Map<String, Condition> keyCon = new HashMap<String, Condition>();
    	keyCon.put("tweet_id", hashkeycon);

    	QueryRequest qr = new QueryRequest()
    	.withTableName(tableName)
    	.withKeyConditions(keyCon);

    		QueryResult queryResult = dynamoDB.query(qr);


    	  List<Map<String,AttributeValue>> li = queryResult.getItems();

    	    
      	  return li.get(0).get("text").getS()+"\t\t"+li.get(0).get("coordinates").getS() ; //+ "\t" + li.get(0).get("tweet_id").getS();
    }

    @Override
    public void run() {
    }
}
