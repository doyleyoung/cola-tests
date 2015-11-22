package com.github.bmsantos.core.cola.formatter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.ExamplesTableRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ProjectionValuesTest {

    private static final List<Comment> NO_COMMENTS = emptyList();

    @Mock
    private Examples examples;

    private ProjectionValues uut;

    @Before
    public void setUp() {
        initMocks(this);

        final ExamplesTableRow header = new ExamplesTableRow(NO_COMMENTS, asList("beers"), 1, "1");
        final ExamplesTableRow value = new ExamplesTableRow(NO_COMMENTS, asList("100"), 1, "1");
        when(examples.getRows()).thenReturn(asList(header, value));

        uut = new ProjectionValues(examples);
    }

    @Test
    public void shouldAllowProjection() {
        // When
        final boolean result = uut.canDoProjections();

        // Then
        assertThat(result, equalTo(true));
    }

    @Test
    public void shouldHaveProjections() {
        // When
        final int result = uut.size();

        // Then
        assertThat(result, equalTo(1));
    }

    @Test
    public void shouldDoProjectionForGivenRow() {
        // When
        final String json = uut.doRowProjection(0);

        // Then
        assertThat(json, equalTo("{\"beers\":\"100\"}"));
    }
}
