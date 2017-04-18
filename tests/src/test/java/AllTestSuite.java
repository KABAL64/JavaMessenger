import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Набор тестов
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserTests.class, MessageTests.class
})
public class AllTestSuite {

}
