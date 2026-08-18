import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetupTest {

    //this is just for testing the project setup
    @Test
    public void test() {
        //given
        int a = 1;
        int b = 2;

        //when
        int c = a + b;

        //then
        Assertions.assertEquals(3, c);
    }
}
