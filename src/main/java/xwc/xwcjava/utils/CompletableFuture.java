package xwc.xwcjava.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * a simple CompletableFuture implementation to support Java 7
 * @param <T>
 */
public class CompletableFuture<T> implements Future<T> {
    private enum State {
        RUNNING, CANCELED, DONE
    }

    private volatile State state = State.RUNNING;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        state = State.CANCELED;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return state == State.CANCELED;
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    private static Object NIL = new Object();

    private volatile AtomicReference<Object> result = new AtomicReference<Object>(NIL);

    private boolean isNilResult(Object value) {
        return value == NIL;
    }

    private final boolean completeValue(T t) {
        boolean triggered = result.compareAndSet(result.get(), t);
        if(triggered) {
            try {
                lock.lock();
                state = State.DONE;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
        return triggered;
    }

    public boolean complete(T value) {
        return completeValue(value);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(100, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new ExecutionException("timeout", e);
        }
    }

    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(state == State.RUNNING) {
            try {
                lock.lock();
                condition.await(timeout, unit);
            } finally {
                lock.unlock();
            }
        }
        if(state==State.DONE) {
            Object value = result.get();
            if(isNilResult(value)) {
                return null;
            } else {
                return (T) value;
            }
        }
        if(state == State.CANCELED) {
            throw new ExecutionException("future canceled", new RuntimeException());
        }
        throw new TimeoutException();
    }
}
