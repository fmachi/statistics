# Statistics project

I worked on this exercise focusing on the complexity requirement.
In order to respect the complexity requirement, costant time O(1) for both the endpoints, 
I tried to implement an offline mechanism I will explain better below.
 
# Write - Transactions endpoint
Each time a transaction is added, it's just added to a (thread safe) blocking queue (a linked one, adding at the tail O(1)) 
on which only one thread is listening.
This thread is responsible to add the new transaction to the in memory database (a not thread safe repository) and to 
recalculate the statistics that are set to an AtomicReference.
In order to recalculate the statistics collections stream api are used and the computation takes O(n) where n is the 
number of transactions. Anyway the transactions are streamed offline, the thread serving the incoming request is not 
waiting for that.
This thread is also responsible to recalculate the statistics when one or more transaction are expiring.
To do that transactions are purged.
At the beginning the thread is waiting for the first transaction without any timeout, than as soon as transactions 
arrive, the thread will set a timeout equal to the first moment on which one or more transactions are expiring. 
So, or a new transaction is arriving and the thread processes it or it awakes because some transactions are expired and
the statistics need to be recalculate.
The timeout is set considering the transaction with the oldest timestamp

# Reading - Statistics endpoint
when reading the statistics, an access to the AtomicReference is performed.

# Further details
I decided to build the implementation with a simple InMemoryTransactionRepository (not thread safe) that is used by the 
thread safe repository in order to have a better separation of responsibilities.
I used spring boot 2.
I tried to add a good unit test coverage. I also added an integration tests

# Things to improve

- domain objects (Statistics and Transactions) are also used as DTO exposing domain data, usually I try not to expose 
domain object but to be fast I avoid to use converters
- validation not performed on the DTO I could have used JSR 303
- the clear method into the repository is not thread safe but is not supposed to be executed by the application logic 