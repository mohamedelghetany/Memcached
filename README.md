# Memcached Server
Simple Java / Netty Memcached server


The server supports these commands. 
* GET 
* SET 
* ADD
* REPLACE

Command syntax is based on Memcached ASCII protocol. For more information refer to https://github.com/memcached/memcached/blob/master/doc/protocol.txt

## Prerequisites

1. java8 [link](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)
2. maven [link](https://maven.apache.org/install.html)

## Packages

**Server**: Simple Netty based Server
**Client**: Extremely simple client. It is based on `com.whalin` lib. I am using it for <u>**basic smoketesting**</u>. For load testing please check below

## Build 

From Server folder run the following maven command: `mvn install`

## Running 

From Server folder run the following maven command: `mvn exec:java`

## Changing Log Level
To enable server debug logs set `level="debug"` in [log4j2.xml](./server/src/main/resources/log4j2.xml)
You will need to rebuild the server : `mvn install`
Note: `level="debug"` is very verbose so setting log level to `debug` will have negative effet on performance.

## Design 

Please refer to [Docs Folder](./Server/docs)

## Testing/ Load testing

I used **YCSB** for benchmarking. For more information, refer to  
https://github.com/brianfrankcooper/YCSB/wiki/Running-a-Workload
https://github.com/brianfrankcooper/YCSB/tree/master/memcached

## Benchmark result

|          | My Server                                                    | Memcached                                                    |
| :------- | :----------------------------------------------------------- | ------------------------------------------------------------ |
|          |                                                              |                                                              |
| **Load** | [OVERALL], RunTime(ms), 86905<br/>[OVERALL], Throughput(ops/sec), 11506.817789540302<br/>[TOTAL_GCS_PS_Scavenge], Count, 449<br/>[TOTAL_GC_TIME_PS_Scavenge], Time(ms), 290<br/>[TOTAL_GC_TIME_%_PS_Scavenge], Time(%), 0.33369771589666875<br/>[TOTAL_GCS_PS_MarkSweep], Count, 0<br/>[TOTAL_GC_TIME_PS_MarkSweep], Time(ms), 0<br/>[TOTAL_GC_TIME_%_PS_MarkSweep], Time(%), 0.0<br/>[TOTAL_GCs], Count, 449<br/>[TOTAL_GC_TIME], Time(ms), 290<br/>[TOTAL_GC_TIME_%], Time(%), 0.33369771589666875<br/>[CLEANUP], Operations, 1<br/>[CLEANUP], AverageLatency(us), 3.0007296E7<br/>[CLEANUP], MinLatency(us), 29999104<br/>[CLEANUP], MaxLatency(us), 30015487<br/>[CLEANUP], 95thPercentileLatency(us), 30015487<br/>[CLEANUP], 99thPercentileLatency(us), 30015487<br/>[INSERT], Operations, 1000000<br/>[INSERT], AverageLatency(us), 55.269037<br/>[INSERT], MinLatency(us), 40<br/>[INSERT], MaxLatency(us), 500479<br/>[INSERT], 95thPercentileLatency(us), 61<br/>[INSERT], 99thPercentileLatency(us), 103<br/>[INSERT], Return=OK, 1000000 | [OVERALL], RunTime(ms), 66718<br/>[OVERALL], Throughput(ops/sec), 14988.458886657274<br/>[TOTAL_GCS_PS_Scavenge], Count, 449<br/>[TOTAL_GC_TIME_PS_Scavenge], Time(ms), 268<br/>[TOTAL_GC_TIME_%_PS_Scavenge], Time(%), 0.401690698162415<br/>[TOTAL_GCS_PS_MarkSweep], Count, 0<br/>[TOTAL_GC_TIME_PS_MarkSweep], Time(ms), 0<br/>[TOTAL_GC_TIME_%_PS_MarkSweep], Time(%), 0.0<br/>[TOTAL_GCs], Count, 449<br/>[TOTAL_GC_TIME], Time(ms), 268<br/>[TOTAL_GC_TIME_%], Time(%), 0.401690698162415<br/>[CLEANUP], Operations, 1<br/>[CLEANUP], AverageLatency(us), 2643.0<br/>[CLEANUP], MinLatency(us), 2642<br/>[CLEANUP], MaxLatency(us), 2643<br/>[CLEANUP], 95thPercentileLatency(us), 2643<br/>[CLEANUP], 99thPercentileLatency(us), 2643<br/>[INSERT], Operations, 1000000<br/>[INSERT], AverageLatency(us), 65.204713<br/>[INSERT], MinLatency(us), 42<br/>[INSERT], MaxLatency(us), 103167<br/>[INSERT], 95thPercentileLatency(us), 90<br/>[INSERT], 99thPercentileLatency(us), 138<br/>[INSERT], Return=OK, 1000000 |
| **Run**  |                                                              |                                                              |

**My Server** 

*./bin/ycsb load memcached -s -P workloads/workloada -p "memcached.hosts=127.0.0.1" -p recordcount=1000000* 

    [OVERALL], RunTime(ms), 86905
    [OVERALL], Throughput(ops/sec), 11506.817789540302
    [TOTAL_GCS_PS_Scavenge], Count, 449
    [TOTAL_GC_TIME_PS_Scavenge], Time(ms), 290
    [TOTAL_GC_TIME_%_PS_Scavenge], Time(%), 0.33369771589666875
    [TOTAL_GCS_PS_MarkSweep], Count, 0
    [TOTAL_GC_TIME_PS_MarkSweep], Time(ms), 0
    [TOTAL_GC_TIME_%_PS_MarkSweep], Time(%), 0.0
    [TOTAL_GCs], Count, 449
    [TOTAL_GC_TIME], Time(ms), 290
    [TOTAL_GC_TIME_%], Time(%), 0.33369771589666875
    [CLEANUP], Operations, 1
    [CLEANUP], AverageLatency(us), 3.0007296E7
    [CLEANUP], MinLatency(us), 29999104
    [CLEANUP], MaxLatency(us), 30015487
    [CLEANUP], 95thPercentileLatency(us), 30015487
    [CLEANUP], 99thPercentileLatency(us), 30015487
    [INSERT], Operations, 1000000
    [INSERT], AverageLatency(us), 55.269037
    [INSERT], MinLatency(us), 40
    [INSERT], MaxLatency(us), 500479
    [INSERT], 95thPercentileLatency(us), 61
    [INSERT], 99thPercentileLatency(us), 103
    [INSERT], Return=OK, 1000000 

**MemCached**

*./bin/ycsb load memcached -s -P workloads/workloada -p "memcached.hosts=127.0.0.1" -p recordcount=1000000*

    [OVERALL], RunTime(ms), 66718
    [OVERALL], Throughput(ops/sec), 14988.458886657274
    [TOTAL_GCS_PS_Scavenge], Count, 449
    [TOTAL_GC_TIME_PS_Scavenge], Time(ms), 268
    [TOTAL_GC_TIME_%_PS_Scavenge], Time(%), 0.401690698162415
    [TOTAL_GCS_PS_MarkSweep], Count, 0
    [TOTAL_GC_TIME_PS_MarkSweep], Time(ms), 0
    [TOTAL_GC_TIME_%_PS_MarkSweep], Time(%), 0.0
    [TOTAL_GCs], Count, 449
    [TOTAL_GC_TIME], Time(ms), 268
    [TOTAL_GC_TIME_%], Time(%), 0.401690698162415
    [CLEANUP], Operations, 1
    [CLEANUP], AverageLatency(us), 2643.0
    [CLEANUP], MinLatency(us), 2642
    [CLEANUP], MaxLatency(us), 2643
    [CLEANUP], 95thPercentileLatency(us), 2643
    [CLEANUP], 99thPercentileLatency(us), 2643
    [INSERT], Operations, 1000000
    [INSERT], AverageLatency(us), 65.204713
    [INSERT], MinLatency(us), 42
    [INSERT], MaxLatency(us), 103167
    [INSERT], 95thPercentileLatency(us), 90
    [INSERT], 99thPercentileLatency(us), 138
    [INSERT], Return=OK, 1000000