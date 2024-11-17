# example-ibm-mq
Projeto de exemplo para comunicação com fila IBM MQ utilizando uma implementação
JMS com CorrelationID e container JmsListener com uso de selector

## Inicialização
- Para iniciar um container do IBM MQ usar o comando abaixo
```
docker-compose -f compose-ibmmq.yaml up
```
- Para iniciar o projeto localmente usar os comandos abaixo
```
mvn clean install
```
```
mvn spring-boot:run -D spring-boot.run.arguments="--spring.profiles.active=local"
```

## Referências
### Docker - Java Spring
https://medium.com/@maravondra/ibm-mq-communication-in-docker-8f7b01034aee
https://mvnrepository.com/artifact/com.ibm.mq/mq-jms-spring-boot-starter/3.3.5
https://localhost:9443/ibmmq/console/

### Docker Compose
https://stackoverflow.com/questions/69733819/create-queues-in-ibm-mq-docker-compose
https://github.com/ibm-messaging/mq-container/blob/9.2.3/docs/usage.md#customizing-the-queue-manager-configuration

### Java Spring Boot App
https://spring.io/guides/gs/messaging-jms
https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/jms.html
https://www.javacodegeeks.com/read-and-write-in-ibm-mq-with-java-message-service.html#google_vignette
https://www.javacodegeeks.com/read-and-write-in-ibm-mq-with-java-message-service.html
https://www.ibm.com/docs/en/ibm-mq/9.1?topic=applications-creating-session-in-jms-application
https://www.ibm.com/docs/en/ibm-mq/8.0?topic=application-transacted-sessions-in-jms-applications
https://github.com/nuviosoftware/custom-ibm-mq
https://github.com/nuviosoftware/ibm-jms-client
https://github.com/nuviosoftware/ibm-jms-client2
https://levioconsulting.com/insights/a-simple-guide-to-using-ibm-mq-with-java-messaging-service/
https://www.baeldung.com/java-message-service-ibm-mq-read-write
https://intverse.medium.com/application-modernization-using-ibm-mq-connector-with-redpanda-d9615f925bd7
https://lankydan.dev/2017/06/18/using-jms-in-spring-boot
https://stackoverflow.com/questions/40654586/spring-jms-set-errorhandler-for-jmslistener-annotated-method
https://www.baeldung.com/spring-jms
https://www.yihaomen.com/article/174.html#google_vignette
https://docs.spring.io/spring-framework/docs/4.1.5.RELEASE/spring-framework-reference/html/jms.html
https://docs.spring.io/spring-integration/reference/jms.html