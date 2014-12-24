<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.amazonaws.*" %>
<%@ page import="com.amazonaws.auth.*" %>
<%@ page import="com.amazonaws.services.ec2.*" %>
<%@ page import="com.amazonaws.services.ec2.model.*" %>
<%@ page import="com.amazonaws.services.s3.*" %>
<%@ page import="com.amazonaws.services.s3.model.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.*" %>
<%@ page import="com.amazonaws.services.dynamodbv2.model.*" %>
<%@ page import="com.amazonaws.auth.profile.ProfileCredentialsProvider" %>
<%@ page import="com.amazonaws.regions.Region" %>
<%@ page import="com.amazonaws.regions.Regions" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.lang.*" %>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>
<%@ page import="com.amazonaws.auth.PropertiesCredentials" %>



<%!
    //private AmazonEC2         ec2;
    //private AmazonS3           s3;
    private AmazonDynamoDB dynamo;
 %>

<%

	AmazonDynamoDBClient dynamoDB;
	AWSCredentials credentials = null;
	
	
	//Karan key
	String ACCESS_KEY = "KEY";
    String SECRET_KEY = "KEY";
    
    
  	//credentials= new PropertiesCredentials(getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
	//dynamoDB = new AmazonDynamoDBClient(credentials);
	
	dynamoDB = new AmazonDynamoDBClient(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));

	
	Region usWest2 = Region.getRegion(Regions.US_EAST_1);
	dynamoDB.setRegion(usWest2);

  String tableName = request.getParameter("tablename");
   // String tableName = "ebola_table2_sns";

  if(tableName==null)
  {
    tableName="ebola_table2_sns";
  }

  ScanRequest scanRequest = new ScanRequest(tableName);
  ScanResult scanResult = dynamoDB.scan(scanRequest);
  List<Map<String,AttributeValue>> li = scanResult.getItems();
  String lat="",lng="";
  String tweets="";
  JSONArray data = new JSONArray();
  
  for (int i = 0; i < li.size(); i++) 
  {
	
	  			JSONObject tweet = new JSONObject();
	  			String[] temp=li.get(i).get("coordinates").getS().split(",");
	  			//System.out.println(temp);
	  			lat=temp[1];
    			lng=temp[0];
    			String score = li.get(i).get("score").getS();
    			String text = li.get(i).get("text").getS();
    			tweet.put("text", text);
    			tweet.put("score", score);
	  			tweet.put("lng", lng);
	  			tweet.put("lat", lat);

	  			data.add(tweet);
	  			
    			
    			
    			
	
	}
  System.out.println(data);
  out.print(data);
  out.flush();  

%>


<%
   
    if (request.getMethod().equals("HEAD")) return;
%>


