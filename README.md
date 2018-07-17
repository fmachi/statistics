# statistics

In order to respect the complexity requirement O(1), I tried to implement an
offline mechanism according to which, each time a transaction is added, it's just
added to a queue on which only one thread is listening.
This thread is responsible to add the new transaction to the in memory database
and to recalculate the statistics that are set to an AtomicReference.
This thread is also responsible to recalculate the statistics when one or more
transaction are expiring.
At the beginning the thread is waiting for the first transaction without any
timeout, than as soon as transactions are arriving, the thread will set a timeout
equal to the first moment on which one or more transactions are expiring. So,
or a new transaction is arriving or the Thread awakes because some transactions
are expired.

I decided to build the implementation with a simple InMemory Repository (not
thread safe) that is used by the thread safe.




git clone https://fmachi@github.com/fmachi/statistics.git
