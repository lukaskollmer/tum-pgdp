package task_09.arc;


public interface Trackable {

    // we have to nest this in a class because static variables in interfaces are final
    public static class Options {
        public static boolean DISABLED = false;
    }

    static void retainIfPossible(Object obj) {
        if (obj instanceof Trackable) {
            ((Trackable) obj).retain();
        }
    }

    static void releaseIfPossible(Object obj) {
        if (obj instanceof Trackable) {
            ((Trackable) obj).release();
        }
    }



    default void retain() {
        if (Options.DISABLED) return;

        ReferencePool.shared.retain(this);
    }

    default void release() {
        if (Options.DISABLED) return;

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
