import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class QueueTest<T extends Queue> {
    private T queue;
    protected abstract T createInstance();

    @BeforeEach
    void setUp(){
        this.queue = createInstance();
    }

    @Test
    void add(){
        this.queue.add(1);
        this.queue.add(2);
        assertEquals(1, queue.peek());
        this.queue.add(3);
        assertEquals(1, queue.peek());

    }
    @Test
    void remove(){
        this.queue.add(1);
        this.queue.add(2);
        assertEquals(1, queue.peek());
        this.queue.remove();
        assertEquals(2, queue.peek());
    }
    @Test
    void size(){
        this.queue.add(1);
        this.queue.add(2);
        assertEquals(2, queue.size());
        this.queue.remove();
        assertEquals(1, queue.size());
        this.queue.remove();
        assertEquals(0, queue.size());
    }
}
