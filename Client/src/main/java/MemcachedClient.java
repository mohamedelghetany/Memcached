import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class MemcachedClient {
  final static Logger LOG = Logger.getLogger(MemcachedClient.class);
  /**
   * MemcachedJavaClient program to show the usage of different functions
   * that can be performed on Memcached server with Java Client
   */
  public static void main(String[] args) {
    //initialize the SockIOPool that maintains the Memcached Server Connection Pool
    final String[] servers = {"localhost:11211"};
    final String poolName = "MemcachedClient";

    final SockIOPool pool = SockIOPool.getInstance(poolName);
    pool.setServers(servers);
    pool.setFailover(true);
    pool.setInitConn(10);
    pool.setMinConn(5);
    pool.setMaxConn(250);
    pool.setMaintSleep(30);
    pool.setNagle(false);
    pool.setSocketTO(3000);
    pool.setAliveCheck(true);
    pool.initialize();


    final MemCachedClient mcc = new MemCachedClient(poolName);
    //add some value in cache
    LOG.info("Sending set command " + mcc.set("1", "Original"));
    //Get value from cache
    LOG.info("Get from Cache: " + mcc.get("2"));
  }
}
