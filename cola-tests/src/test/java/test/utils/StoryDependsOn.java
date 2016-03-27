package test.utils;

import com.github.bmsantos.core.cola.story.annotations.DependsOn;
import com.github.bmsantos.core.cola.story.annotations.When;

@DependsOn(SimpleStory.class)
public class StoryDependsOn extends SimpleStory {
    public static int timesCalled = 0;

    @When("A")
    @DependsOn(SimpleStory.class)
    public void whenA() {
        super.whenA();
    }

    public int howOften() {
        // Reports the number of times that Story.class test was called.
        return timesCalled;
    }

    public static void resetTimesCount() {
        timesCalled = 0;
    }
}
