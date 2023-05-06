import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.*;

public class HazelcastMember {
    public static void main(String[] args){
        Config config = new Config();
        //MapConfig mapConfig = config.getMapConfig("demo-backup-0")
        //                            .setBackupCount(0);
        MapConfig mapConfig = config.getMapConfig("demo-backup-2")
                                    .setBackupCount(2);

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config);
    }
}
