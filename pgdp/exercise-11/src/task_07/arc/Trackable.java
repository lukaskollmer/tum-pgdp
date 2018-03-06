package task_07.arc;

public interface Trackable {

    default void retain() {
        ReferencePool.shared.retain(this);
    }

    default void release() {
        ReferencePool.shared.release(this);

        if (retainCount() == 0) {
            dealloc();
        }
    }

    default int retainCount() {
        return ReferencePool.shared.getRetainCount(this);
    }

    void dealloc();
}
