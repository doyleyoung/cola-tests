package cola.ide;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.bmsantos.core.cola.story.annotations.IdeEnabler;

public abstract class BaseColaTest {

    @IdeEnabler
    @Test
    public void iWillBeRemoved() {
        fail("This test should have not been executed");
    }
}
