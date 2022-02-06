# About

These are examples of applications to run in a serverless environment at GCP

# Description

## code-as-a-function

This is the minimal app to run as a Cloud Function at GCP


## springboot-as-a-function

This is an example of  minimal Spring Boot app to run as a Cloud Function at GCP

# Local testing

```
export MAIN_CLASS=functions.WordCounterApp
export PROJECT_ID=<project_id>
mvn -pl springboot-as-a-function function:run
```

In other console invoke the following command:

```
./send_local_message.sh "hello big world"
```

and see how Function processes the event (must count all 3 words in message). In real situation the message from Pub/Sub topic will be forwarded to app.

# Deployment

1. Create a new Pub/Sub topic to be linked to Function and a topic to publish the results to

```
gcloud config set project <project_id>
gcloud pubsub topics create cf-trigger
gcloud pubsub topics create cf-results
```

2. Build the app by running:

```
mvn -pl springboot-as-a-function package
```
This command will build in the target/deploy directory the JAR file to deploy to GCP

3. Deploy the JAR that you created by running

```
gcloud functions deploy sbaaf-gcp-background \
--entry-point org.springframework.cloud.function.adapter.gcp.GcfJarLauncher \
--runtime java11 \
--trigger-topic cf-trigger \
--source ./springboot-as-a-function/target/deploy \
--set-env-vars PROJECT_ID=<project_id>
--memory 512MB
```
where the parameter `--trigger-topic` which topic will trigger the function invocation when new messages are published to it.

In some cases this command may be need to enabling service [cloudfunctions.googleapis.com] on project. The deployment normally takes 1-3 mins.
The deployed function is available at `https://console.cloud.google.com/functions/list?project=project_id` 

4. Invoke the background function by publishing a message to topic, the outcome must be the same as in Local testing:

```
gcloud pubsub topics publish cf-trigger --message="hello big world"
```


# References

[1] https://docs.spring.io/spring-cloud-function/docs/current/reference/html/gcp.html

[2] https://github.com/spring-cloud/spring-cloud-function

[3]