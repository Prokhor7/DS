import com.hazelcast.collection.IQueue;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class ConsumerDemo {
    public static void main( String[] args ) throws Exception {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        IQueue<Integer> queue = hz.getQueue( "queue" );
        while ( true ) {
            int item = queue.take();
            System.out.println( "Consumed: " + item+"; queue size: "+queue.size());
            if ( item == -1 ) {
                queue.put( -1 );
                break;
            }
            Thread.sleep( 1500 );
        }
        System.out.println( "Consumer Finished!" );
    }
}
