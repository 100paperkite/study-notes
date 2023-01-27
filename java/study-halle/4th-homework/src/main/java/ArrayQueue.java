import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArrayQueue implements Queue{
    private int head;
    private int tail;
    private int[] queue;

    public ArrayQueue(int capacity) {
        this.queue = new int[capacity];
    }

    @Override
    public void add(int data) {
        // 기존에 할당된 사이즈를 초과하는 경우 두 배 늘려준다.
        if (tail >= queue.length){
            var temp = new int[this.queue.length * 2];
            System.arraycopy(queue, 0, temp, 0, queue.length);
            this.queue = temp;
        }

        queue[tail++] = data;
    }

    @Override
    public void remove() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }

        head++;
        if (head == tail) {
            head = tail = 0;
        }
    }

    @Override
    public int peek() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }

        return queue[head];
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public int size() {
        return tail - head;
    }

    @Override
    public void clear() {
        Arrays.fill(this.queue, 0);
        head = tail = 0;
    }
}
