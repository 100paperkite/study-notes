import java.util.NoSuchElementException;

public class ListNodeStack implements Stack{
    private ListNode head;
    private int size;

    @Override
    public void push(int data) {
        var node = new ListNode(data);
        node.next = head;
        head = node;

        size++;
    }

    @Override
    public int pop() {
        var result = this.peek();
        head = head.next;
        size--;

        return result;
    }

    @Override
    public int peek() {
        if (head == null){
            throw new NoSuchElementException();
        }
        return head.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = null;
    }
}
