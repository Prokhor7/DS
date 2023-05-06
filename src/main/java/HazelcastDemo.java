import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.ObjectInputFilter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HazelcastDemo {
    public static void main(String[] args) {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        //IMap<Integer, String> map = hz.getMap("demo-backup-0");
        IMap<Integer, String> map = hz.getMap("demo-backup-2");
        //IMap<Integer, String> map = hz.getMap("demo-map");
        for (int i = 0; i <= 1000; i++) {
            map.put(i, "sqrt(" + i + ") = " + Math.sqrt(i));
        }
        //map.destroy();
        hz.shutdown();
    }
}
