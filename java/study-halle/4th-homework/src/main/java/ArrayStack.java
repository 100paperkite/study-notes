import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayStack implements Stack {
    private final int[] stack;
    private int head = -1;

    public ArrayStack(int capacity){
        this.stack = new int[capacity];
    }

    @Override
    public void push(int data) {
        if (head >= stack.length){
            return;
        }
        stack[++head] = data;
    }

    @Override
    public int pop() {
        this.peek();
        return stack[head--];
    }


    @Override
    public int peek() {
        if (head == -1){
            throw new NoSuchElementException();
        }
        return stack[head];
    }

    @Override
    public int size() {
        return head + 1;
    }

    @Override
    public void clear() {
        Arrays.fill(stack, 0);
        this.head = -1;
    }
}
