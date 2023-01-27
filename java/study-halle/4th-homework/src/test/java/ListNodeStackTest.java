import static org.junit.jupiter.api.Assertions.*;

public class ListNodeStackTest extends StackTest{

    @Override
    protected Stack createInstance() {
        return new ListNodeStack();
    }
}