# High level design 

Most MemCached uses HashTable a long with a LinkedList for eviction policy. Making a design like this highly concurrent isn't easy becasue for every operation eviction LinkedList has to be updated. 

Concurrency can be achieved by using lock around the entire LinkedList for every cache operation that requires updating it. Soon we will end up with serial access and degraded performance. 

One way to overcome that is by breaking the in memory HashTable into multiple smaller HashTables and use the key (or part of the key) + Hash Function for routing (Finding out which HashTable a given key belongs to). Partitioning the HashTable will give you a better performance than having one big HashTable with one LinkedList but we will have to deal with hot-spotting 

Another idea I had that is inspired by Object Storage Append Only Transaction Logs is that any operation is written into transaction log (TX File) and another set of threads (can be one thread) will take care of applying these transactions to the main DataStore. The advantage is that client threads are not blocked for a long time, just the required time to append to the TX file then return.

Append Only Transaction Logs inspired my cache design. Instead of updating the cache and eviction LinkedList right away, we will update the Cache and add an event (EvictionMessage) to a bus (EvictionBus) and return. Then another thread will fetch messages form EvictionBus and update eviction LinkedList

Here is a sequence diagram for GET command for example.

![image-20200421152038114](./GET_Command.png)

The rest of the command will have a similar flow! 

# Design implementation details
In my design I aimed for separating Client Protocol parsing from the actual Cache Store. The advantage of that is 
* Easily adopt new protocols
* Easily swap Cache implementation while still using the same protocol

So there are two packages under **'Server'** folder 
* **Protocol**
  
    * Which encapsulate all Netty Server details
    * Encode User input 
    * Decode Result         
    
    To support different commands I used **Command Pattern**. Each command encapsulate all the details needed to decode and execute this command. That makes adding new commands easy. 

* **Cache** 

  The actual Cache implementation. This layer doesn't know anything about Netty or the actual Protocol format 

  

# Monitoring 

Currently Server monitoring is limitted. We only have a thread that runs every X mins (default is 1 min) and will "report"* CacheStats. 

CacheStats currenly has, HitCount, MissCount, EvictionCount 

`[CacheStats-StatsReporter] cache.CacheStats$StatsReporter  - CacheStats - Hit Count: 0, Miss Count: 0, Evicted: 0`

*"Report" in the current system means Log, but that can be changed to publish these KPIs to another service and setup alerting around it 

# V2 and the future

* Add eviction by memory pressure a long with number of keys 

* [In Progress] Performance tweeking and finding the bottelnecks 

* [Future] Add more monitoring 

* [Future] Add cache partitioning to my design, basically partition the cache to multiple caches and each cach will have its own eviction worker. 

  

