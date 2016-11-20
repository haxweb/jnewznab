package haxweb.jnewznab.nntp.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ResourcePool<Resource> {
	protected final BlockingQueue<Resource> pool;
    protected final ReentrantLock lock = new ReentrantLock();
    protected int createdObjects = 0;
    protected int size;

    protected ResourcePool(int size) {
        this(size, true);
    }

    protected ResourcePool(int size, Boolean dynamicCreation) {
        // Enable the fairness; otherwise, some threads may wait forever.
        pool = new ArrayBlockingQueue<>(size, true);
        this.size = size;
        if (!dynamicCreation) {
            lock.lock();
        }
    }

    public Resource acquire() throws Exception {
        if (!lock.isLocked()) {
            if (lock.tryLock()) {
                try {
                    ++createdObjects;
                    return createObject();
                } finally {
                    if (createdObjects < size) lock.unlock();
                }
            }
        }
        return pool.take();
    }

    public void recycle(Resource resource) throws Exception {
        // Will throws Exception when the queue is full,
        // but it should never happen.
        pool.add(resource);
    }

    public void createPool() {
        if (lock.isLocked()) {
            for (int i = 0; i < size; ++i) {
                pool.add(createObject());
                createdObjects++;
            }
        }
    }

    protected abstract Resource createObject();
}