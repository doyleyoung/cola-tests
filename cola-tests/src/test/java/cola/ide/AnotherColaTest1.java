package cola.ide;

import com.github.bmsantos.core.cola.story.annotations.Feature;
import com.github.bmsantos.core.cola.story.annotations.Given;
import com.github.bmsantos.core.cola.story.annotations.Then;
import com.github.bmsantos.core.cola.story.annotations.When;

public class AnotherColaTest1 {

    @Feature
    private final String feature =
    "Feature: A feature\n"
        + "Scenario: A scenario\n"
        + "Give A\n"
        + "When B\n"
        + "Then C";

    @Given("A")
    public void given() {
    }

    @When("B")
    public void when() {
    }

    @Then("C")
    public void then() {
    }
}
