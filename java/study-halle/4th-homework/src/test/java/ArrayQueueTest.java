class ArrayQueueTest extends QueueTest {

    @Override
    protected Queue createInstance() {
        return new ArrayQueue(10);
    }
}