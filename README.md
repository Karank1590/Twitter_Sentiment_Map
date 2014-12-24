Web Application to Display Heat Map , Weighted Heat map and Bar graph of Tweet Sentiment
========================================================================================

Authors
-------
Umang Patel - ujp2001@columbia.edu , Karan Kaul- kak2210@columbia.edu

GitHub URL
----------
https://github.com/17patelumang/Twitter_Sentiment_map

Screencast URL
--------------
http://www.screencast.com/t/DOhmT2ew9oJ

Description
-----------
This is a web application that allows the user to select one of the following keywords – ebola, facebook, football, obama; and displays the heat map of sentiment of the tweets containing the keyword, a weighted heat map of sentiment of the tweets containing the keyword or the distribution of sentiment of the tweets.

As the web page is requested, a connection is established between the server and the DynamoDB instance, and the latitude, longitude, text and sentiment of the tweets regarding a keyword is collected from the database, serialized as a string and sent to the requesting client. At the client, the web page is loaded and the data is deserialized and is presented as any of the previously mentioned choices.

Information about a different keyword can be displayed by selecting the keyword in the dropdown list called Keyword. Additionally, the data can be displayed either as a heatmap or weighted heat map by selecting the relevant option from the relevant dropdown box. Alternatively, the tweet sentiment distribution can be displayed by following the relevant link on the web page.

Here we have used Amazon SQS and Amazon SNS service to make the application highly scalable.


Components & Flow
-----------------
### 1. Python script to acquire tweets (Code in Python Script Folder) ###  
The tweets are downloaded from twitter using the Twitter streaming API filtered by keyword and language (english). Once a tweet for a keyword is received its tweet id, text and location data (coordinates) are saved on an **Amazon DynamoDB** database. Additionally, the tweet id and the keyword it is acquired for is inserted on an **Amazon SQS Queue** for further processing.

### 2. Java Application to process tweets (Sentiment Analysis) (Code in Jar Code Folder)###
The Java application polls the Amazon SQS queue, and upon the availability of tweets on the queue, it passes the message on the queue (containing the tweet id and keyword) to one of multiple threads currently executing in a thread pool. These threads are responsible for processing the tweets. Once a message is passed to a thread, it is invisible to the remaining threads. A thread processes tweets by initially acquiring the text from the Amazon DynamoDB database based on the keyword and tweet id from the queue message. The text is then passed on for sentiment analysis using the **Alchemy API**. __We are running this code as jar  on ec2 instance , so it can be run on multiple instances__ .

Alchemy returns a sentiment score (ranging from -1 to +1) and sentiment values (positive, neutral or negative) for each text. For those tweet texts which cannot be processed, we assume a neutral sentiment (score- 0, value- neutral). These details along with the already present tweet details are pushed to an **Amazon SNS** topic.

### 3. JSP servlet serving as SNS endpoint  (Code in SNS notification servlet Folder)###
This JSP servlet serves as an HTTP endpoint, it is subscribed to the Amazon SNS topic and upon receiving an SNS notification, it inserts all the data to an Amazon DynamoDB database which serves data to the clients accessing the web application.

### 4. Web Application  (Code in Application Code Folder) ###
This is the primary web application built in JSP and accessed by the client which as mentioned in the description accesses the DynamoDB database, serializes the information and sends it to the client. The web page on the client is refreshed on a set interval to allow realtime information from incoming tweets to be presented to the user.


Libraries/SDKs/APIs
-------------------
### 1. AWS SDK ###
Used for the various Amazon Web Services utilized in the project such as DynamoDB, SQS and SNS.

### 2. Twitter API ###
Tweets and their metadata regarding certain keywords are downloaded using the Twitter streaming API.

### 3. Alchemy API ###
Used for sentiment analysis, text is passed to alchemy and a sentiment score and sentiment value is returned if the text is processed (an error otherwise).

### 4. Google Maps API ###
Popular maps API which allows us to plot a heat map based on the coordinates of the tweets.

### 5. Heatmap.js ###
A javascript library which allows the presentation of sentiment as a weighted heatmap as an overlay over the Google Maps object. Allows us to customize the heatmap based on sentiment score as well as a custom gradient to display the heatmap.

### 6. D3.js ###
Allows us to define a bar graph which presents the distribution of the sentiment of the tweet. Further, allows us to define a simple tween animation of the bars present in the bar graph when they are switched from being presented as sorted or unsorted.

### 7. Python Libraries ###

Python==2.7.6

oauth2==1.5.211

httplib2==0.8

nltk==3.0.0

boto==2.20.1

urllib2== Default package comes with python

json== Default package comes with python

ast== Default package comes with python

re== Default package comes with python

Steps to run code
-----------------

__Python Script__

(1) Create DynamoDB instance with table having columns as "coordinates", "tweet_id", "text", "created_at", "followers_count";"tweet_id" is primary Hash key and put that table name in variable "table_name" in "twitter_stream.py".

(2) Put AWS credentials in ".boto" file.

(3) Put the keyword to track from streaming Twitter API by specifying "track=" in url variable amd fill in the twitter credentials.

(4) Put the SQS queue name. Queue name we have given is "assi2_queue".

(5) run "python twitter_stream.py".


__Java Application Code__

(1) Put in the AWS credentials, and convert the project as jar and run it. AWS credentials are kept in karan_change.java, SNS_sender.java and WorkerThread.java. AlchemyAPI credentials are kept in url in WorkerThread.java. Make SNS topic name "SampleTopic".


__Servlet__

(1) Put in AWS credentials in AwsCredentials.properties and SNS topic name. Run the sevlet.

(2) On amazon in SNS console put the url given by ebs to subscribe to that topic or if you are running on localhost run "ngrok" to tunnel to your application url and give the url given by "ngrok" to subscribe.


__Application__

(1) Put in AWS credentials in index.jsp and AwsCredentials.properties

(2) Run the code on server Tomcat 6.

Bonus (Sentiment Het map & D3.js)
--------------------------------
(1) We have plotted heat map of sentiment of tweets.

(2) To show our artistic skills we are even using D3.js to plot bar graph of tweet distribution.
