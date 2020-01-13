# Nasdaq Cloud Data Service (NCDS) Dockerfiles

## Building
Run docker build in project home directory 
    
```
docker build -f docker/Dockerfile . -t sdk-app --no-cache
```
 
## Running Locally Built Images

Replace client id(`{clinet-id-value}`) and client secret(`{client-secret-value}`) provided during on-boarding from Nasdaq team. Also, provide the password (`{trsustore-pass}`) for java truststore.

```
docker run -e "OAUTH_CLIENT_ID={clinet-id-value}" -e "OAUTH_CLIENT_SECRET={client-secret-value} -e "JAVAX_NET_SSL_TRUSTSTOREPASSWORD={trsustore-pass}" sdk-app:latest
```
 
 User can pass arguments to run the application with specific commands
 ```
 -opt -- Provide the operation you want to perform \n" +
   "        * TOP - View the top nnn records in the Topic/Stream\n"+
   "        * SCHEMA - Display the Schema for the topic\n"+
   "        * METRICS - Display the Metrics for the topic\n"+
   "        * TOPICS - List the eligible topics for the client\n"+
   "        * GETMSG - Get one example message for the\n"+
   "        * CONTSTREAM   - Retrieve continuous stream  \n"+
   "        * HELP - help \n"+
 "-topic -- Provide topic for selected option         --- REQUIRED for TOP,SCHEMA,METRICS and GETMSG \n"+
 "-n -- Provide number of messages to retrieve        --- REQUIRED for TOP \n"+
 "-msgName -- Provide name of message based on schema --- REQUIRED for GETMSG \n"+
 ```
 
 Example to get `TOP 10` messages from GIDS stream
 
 ```
 docker run -e "OAUTH_CLIENT_ID={clinet-id-value}" -e "OAUTH_CLIENT_SECRET={client-secret-value} -e "JAVAX_NET_SSL_TRUSTSTOREPASSWORD={trsustore-pass}" sdk-app:latest -opt TOP -n 10 -topic GIDS
```

     