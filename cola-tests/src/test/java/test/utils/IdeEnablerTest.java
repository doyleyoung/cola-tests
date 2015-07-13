package test.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.bmsantos.core.cola.story.annotations.IdeEnabler;

public class IdeEnablerTest {
    @IdeEnabler
    @Test
    public void shouldBeRemoved() {
        fail("This method should be removed");
    }
}
