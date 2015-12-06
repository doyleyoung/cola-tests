package test.utils;

import com.github.bmsantos.core.cola.story.annotations.DependsOn;
import com.github.bmsantos.core.cola.story.annotations.When;

@DependsOn(Story.class)
public class StoryDependsOn extends Story {
    public static int timesCalled = 0;

    @When("A")
    @DependsOn(Story.class)
    public void whenA() {
        super.whenA();
    }

    public int howOften() {
        // Reports the number that Story.class test was called.
        return timesCalled;
    }

    public static void resetTimesCount() {
        timesCalled = 0;
    }
}
