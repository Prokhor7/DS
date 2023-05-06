import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class ProducerDemo {
    public static void main( String[] args ) throws Exception {
        Config config = new Config();
        QueueConfig queueConfig = config.getQueueConfig("queue");
        queueConfig.setMaxSize(10);
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
        IQueue<Integer> queue = hz.getQueue( "queue" );
        for ( int k = 1; k < 41; k++ ) {
            queue.put( k );
            System.out.println( "Producing: " + k +"; queue size: "+queue.size());
            Thread.sleep(1000);
        }
        queue.put( -1 );
        System.out.println( "Producer Finished!" );
    }
}
