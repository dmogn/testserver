# testserver
Kenkou challenge test

Blocking connections on both sides were used. Because we have single event source (one thread and one source) and haven't storage resources for event queues for slow clients. 
Second thread is used for client connections initialization cycle.

Аdditional libraries: Lombok for data structures.

Advantages of the solution:
* simplicity and transparent logic.

Disadvantages of the solution: 
* slow or unresponsive client can block the entire system.

More advanced soution should has async client-syde connections and event queue per client.


Usage:

cd testserver

mvn clean install

mvn exec:java -Dexec.mainClass="de.kenkou.jobtest.server.Server"
