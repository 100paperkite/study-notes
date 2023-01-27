import java.util.NoSuchElementException;

public class ListNodeQueue implements Queue{

    private ListNode head;
    private ListNode tail;
    private int size;

    @Override
    public void add(int data) {
        var node = new ListNode(data);
        if (isEmpty()) {
            head = tail = node;
            size++;
            return;
        }
        tail.next = node;
        tail = tail.next;
        size++;
    }

    @Override
    public void remove() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        head = head.next;
        size--;
    }


    @Override
    public int peek() {
        if (isEmpty()){
            throw new NoSuchElementException();
        }
        return head.value;
    }

    @Override
    public boolean isEmpty() {
        return head == null && tail == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = tail = null;
    }
}
