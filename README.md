# springboot-camel-sentry-test

This example demonstrates how to use Apache Camel and [sentry.io](https://sentry.io/welcome/) to track errors.

It uses the following integration methods provided by sentry:
- Log Appender (logback.xml)
- Java API (SentryTestRoute.java)

## Building

1. Clone this project from git repository:

   ```
   git clone https://github.com/markuslohn/springboot-camel-sentry-test.git
   ```

2. Navigate to the root folder of the cloned git repository and execute:

   ```
   mvn clean install
   ```

## Execution

### Prerequisites

1. Install and configure sentry.io as described [here](https://github.com/getsentry/onpremise). For every OpenShift project there should exists one project with the same name in sentry.io.

2. Obtain the DSN in Project Settings > Klient Keys (DNS) in sentry.io console.

### Execute in local Environment

1. Prepare all required settings in application.properties

2. Execute with

   ```
   mvn spring-boot:run -Dsentry.dsn=http://5dde06ec94984e72a0dd1832eb1bae50:205ec012ba6c48db91ccbcf65a91cd3a@192.168.200.114:9000/5 -Denvironment=dev
   ```

   **Note:** Use the Klient Keys (DNS) for the project in sentry.io.

3. Test the deployment with

   ```
   curl http://localhost:8080/camel/defaulthandler/test
   curl http://localhost:8080/camel/logginghandler/test
   ```

4. Produce an error with

   ```
   curl http://localhost:8080/camel/defaulthandler/error
   curl http://localhost:8080/camel/logginghandler/error
   ```

   Verify the created issue (FileNotFoundException) in sentry.io console.


### Execute in OpenShift

1. The following command grants 'view' access:

   ```
   oc policy add-role-to-user view --serviceaccount=default
   ```

   If the above permission is not granted, your pod may throw a message similar to the following:

   "Forbidden!Configured service account doesn't have access. Service account may have been revoked"

2. Create a ConfigMap with name `sentry.io` and a key 'SENTRY_DSN', key 'environment'. As value use the Klient Keys (DNS) for the project in sentry.io.

  ```
  oc create configmap sentry.io --from-literal=SENTRY_DSN=http://5dde06ec94984e72a0dd1832eb1bae50:205ec012ba6c48db91ccbcf65a91cd3a@192.168.200.114:9000/5 --from-literal=environment=test
  ```

3. Create a ConfigMap for this project using the following command:

   ```
   oc create configmap springboot-camel-sentry --from-file=src/main/resources/application.properties
   ```

4. Deploy the application to OpenShift

   ```
   mvn fabric8:deploy
   ```

5. Determine the URL to access the service in OpenShift. Test the deployment with

   ```
   curl http://camel-spring-boot-sentry-my-integrationservice-providers.192.168.99.100.nip.io/camel/defaulthandler/test
   curl http://camel-spring-boot-sentry-my-integrationservice-providers.192.168.99.100.nip.io/camel/logginghandler/test
   ```

6. Produce an error with

   ```
   curl http://camel-spring-boot-sentry-my-integrationservice-providers.192.168.99.100.nip.io/camel/defaulthandler/error
   curl http://camel-spring-boot-sentry-my-integrationservice-providers.192.168.99.100.nip.io/camel/logginghandler/error
   ```

   Verify the created issue (FileNotFoundException) in sentry.io console.
