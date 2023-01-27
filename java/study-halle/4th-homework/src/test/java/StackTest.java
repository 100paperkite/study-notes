import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class StackTest<T extends Stack> {
    private T stack;
    protected abstract T createInstance();

    @BeforeEach
    void setUp(){
        this.stack = createInstance();
    }

    @Test
    void push() {
        stack.push(1);
        stack.push(2);

        assertEquals(2, stack.peek());
        stack.pop();
        assertEquals(1, stack.peek());
    }

    @Test
    void pop() {
        stack.push(1);
        stack.push(2);
        assertEquals(2,stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void size(){
        stack.push(1);
        assertEquals(1, stack.size());
        stack.push(1);
        stack.push(1);
        assertEquals(3, stack.size());
        stack.pop();
        assertEquals(2, stack.size());

    }

    @Test
    void raiseExceptionIfEmptyPop(){
        assertThrows(NoSuchElementException.class, () -> stack.pop());
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }
}