package task_07.arc;

import java.util.HashMap;
import java.util.Map;

public class ReferencePool {

    public static ReferencePool shared = new ReferencePool();

    private Map<Trackable, Integer> retainCounts = new HashMap<>();

    private ReferencePool() {}


    public void retain(Trackable obj) {
        retainCounts.put(obj, retainCounts.getOrDefault(obj, 0) + 1);
    }

    public void release(Trackable obj) {
        if (!retainCounts.containsKey(obj)) return;

        retainCounts.put(obj, retainCounts.get(obj) - 1);

        if (retainCounts.get(obj) == 0) {
            retainCounts.remove(obj);
        }
    }


    public int getRetainCount(Trackable obj) {
        return retainCounts.getOrDefault(obj, 0);
    }
}
