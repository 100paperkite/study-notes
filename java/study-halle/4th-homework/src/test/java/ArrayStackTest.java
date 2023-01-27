import static org.junit.jupiter.api.Assertions.*;

public class ArrayStackTest extends StackTest {

    @Override
    protected Stack createInstance() {
        return new ArrayStack(10);
    }
}