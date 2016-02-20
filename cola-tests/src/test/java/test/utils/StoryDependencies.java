package test.utils;

import com.github.bmsantos.core.cola.story.annotations.Dependencies;
import com.github.bmsantos.core.cola.story.annotations.DependsOn;
import com.github.bmsantos.core.cola.story.annotations.When;

@Dependencies({
  @DependsOn(Story.class),
  @DependsOn(value = Story.class, methods = "dependenciesTest")
})
public class StoryDependencies extends Story {
    public static int timesCalled = 0;

    @When("A")
    @Dependencies({
      @DependsOn(Story.class),
      @DependsOn(value = Story.class, methods = "dependenciesTest")
    })
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
