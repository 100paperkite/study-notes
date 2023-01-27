import static org.junit.jupiter.api.Assertions.*;

class ListNodeQueueTest extends QueueTest{

    @Override
    protected Queue createInstance() {
        return new ListNodeQueue();
    }
}