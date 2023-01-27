public interface Queue {
    void add(int data);
    void remove();
    int peek();
    boolean isEmpty();
    int size();

    void clear();
}
