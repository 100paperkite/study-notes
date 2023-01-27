/**
 * doubly linked-list
 */
public class LinkedList {
    ListNode head;
    ListNode tail;
    int size;

    public ListNode get(int position){
        ListNode cur = head;
        while (position-- > 0){
            cur = cur.next;
        }
        return cur;
    }


    public void add(ListNode nodeToAdd, int position){
        if (head == null){
            head = tail = nodeToAdd;
        }
        else if (position == 0){
            head.prev = nodeToAdd;
            nodeToAdd.next = head;
            head = nodeToAdd;
        }
        else if (position == size){
            tail.next = nodeToAdd;
            nodeToAdd.prev = tail;
            tail = nodeToAdd;
        }
        else {
            var target = get(position);
            nodeToAdd.prev = target.prev;
            target.prev.next = nodeToAdd;
            target.prev = nodeToAdd;
            nodeToAdd.next = target;
        }
        size++;
    }

    public ListNode remove(int positionToRemove){
        ListNode removed = null;

        if (head == null){
            return null;
        }
        else if (size == 1){
            removed = head;
            head = tail = null;
        }
        else if (positionToRemove == 0){
            removed = head;
            head.next.prev = null;
            head = head.next;
        }
        else if (positionToRemove == size - 1){
            removed = tail;
            tail.prev.next = null;
            tail = tail.prev;
        }
        else {
            removed = get(positionToRemove);
            removed.next.prev = removed.prev;
            removed.prev.next = removed.next;

        }
        size--;
        return removed;
    }

    public boolean contains(ListNode nodeToCheck){
        ListNode cur = head;
        while (cur != null){
            if (cur == nodeToCheck){
                return true;
            }
            cur = cur.next;
        }
        return false;
    }
}
