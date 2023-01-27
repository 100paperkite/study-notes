import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LinkedListTest {
    private LinkedList linkedList;

    @BeforeEach
    void beforeEach(){
        this.linkedList = new LinkedList();
    }

    @Test
    void add(){
        // [2] <-> [1] <-> [3]
        linkedList.add(new ListNode(1), 0);
        linkedList.add(new ListNode(2), 0);
        linkedList.add(new ListNode(3), 2);

        assertEquals(linkedList.get(0).value, 2);
        assertEquals(linkedList.get(1).value, 1);
        assertEquals(linkedList.get(2).value, 3);

    }
    @Test
    void remove(){
        // [1] <-> [2] <-> [3]
        linkedList.add(new ListNode(1), 0);
        linkedList.add(new ListNode(2), 1);
        linkedList.add(new ListNode(3), 2);

        assertEquals(linkedList.remove(2).value, 3);
        assertEquals(linkedList.remove(0).value, 1);
        assertEquals(linkedList.remove(0).value, 2);

    }

    @Test
    void contains(){
        linkedList.add(new ListNode(1), 0);
        var node = linkedList.get(0);
        assertTrue(linkedList.contains(node));

        linkedList.remove(0);
        assertFalse(linkedList.contains(node));

    }
}