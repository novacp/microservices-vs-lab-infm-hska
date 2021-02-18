This is the full solution for the VS Lab. Feel free to give a star if it helped you. 

Content:
microservice implementation of EShop: https://github.com/mavogel/hska-vis-legacy
 using Spring Boot, Spring Security, OAuth2, JWT, Zuul, Eureka, Hystrix, Ribbon, Redis, MySQL, users, categories, products, catalog... The whole stack
 is in accordance to what the lecture teaches you. There are no automated tests yet.
 
We are using docker-compose for deployment as well as maven for building. Take the following steps to 
run the app:

- In root folder (vs_lab), run: mvn clean install -DskipTests 
- In each folder of "catalog|categories|eureka|gateway|products|users" run "mvn clean install -DskipTests -Pdocker-build
- To build the legacy app docker container, go to "hska-eshop" and run "docker build -t vs-lab/hska-eshop -f docker/Dockerfile ."
- go to "vs_lab" and run "docker-compose up -d". Sometimes, you need to start infrastructure components first.

Open features (not mandatory):
- We are using the Password Grant Type, for Registration, you could, or maybe should, use the Client Credentials Type. This is 
not implemented yet. The downside of not using a grant type on registration (anonymous) is that any client could request that endpoint
without client authentication. If you can argue it, it should not be a problem, anyway it should be pretty easy to implement,
feel free to create a pull request for that.
- Error Handling in Client: this could be optimized, as sometimes, you will see ugly stracktrace error, e.g. when logging in with false credentials. 
I assume this is due to our custom (empty) error handler or triggering addActionError in Controller Actions of hska-eshop. Also, feel free to create a PR,
don't have time to investigate internal error handling of an old struts client stack.
-> if it blocks you, write an issue and I might fix it.

Anyway, hope you get an idea on how to make your implementation work.