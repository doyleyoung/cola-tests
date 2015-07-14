package test.utils;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.github.bmsantos.core.cola.story.annotations.IdeEnabler;

public class MultipleIdeEnablerTest {
    @IdeEnabler
    @Test
    public void shouldBeRemoved1() {
        fail("This method should be removed");
    }

    @IdeEnabler
    @Test
    public void shouldBeRemoved2() {
        fail("This method should be removed");
    }
}
