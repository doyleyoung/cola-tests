package com.github.bmsantos.core.cola.report;

import static com.github.bmsantos.core.cola.report.ColaReport.COLA_REPORT_METHOD;
import static com.github.bmsantos.core.cola.report.ColaReport.COLA_REPORT_TEMPLATE;
import static com.github.bmsantos.core.cola.utils.ColaUtils.paramEncoding;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cortx.maven.client.CortxFactory;
import org.cortx.maven.client.dsl.Cortx;
import org.junit.Before;
import org.junit.Test;

public class ColaReportITest {

    private static final String PUT_METHOD = "put";
    private static final String GET_METHOD = "get";
    private static final String POST_METHOD = "post";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ISSUE = "issue";
    private static final String STATE = "state";
    private static final String ERROR = "error";
    private static final String PASSED = "Pass";
    private static final String FAILED = "Fail";
    private static final String CONFIG_PATH_QUERY = "/context?issue=${issue}&state=${state}&error=${error}";
    private static final String CONFIG_URL = "http://localhost:7919" + CONFIG_PATH_QUERY;
    private static final String CONFIG_BODY = "{ \"issue\":\"${issue}\", \"state\":\"${state}\", \"error\":\"${error}\" }";
    private static final String EXAMPLE_ID = "12345";
    private static final String ISSUE_ARG = ISSUE + ":" + EXAMPLE_ID;
    private static final Exception ERROR_EXAMPLE = new Exception("ERROR !");

    private Properties props;
    private Map<String, String> args;
    private Cortx cortx;

    private ColaReport uut;

    @Before
    public void setUp() throws URISyntaxException {
        uut = new ColaReport();

        args = new HashMap<>();
        args.put(ISSUE, EXAMPLE_ID);
        args.put(STATE, PASSED);

        props = uut.loadProperties(null);

        cortx = CortxFactory.getCortx("localhost");
        cortx.reset();
    }

    @Test
    public void shouldLoadDefaultProperties() {
        // When
        props = uut.loadProperties(null);

        // Then
        assertThat(props.getProperty(COLA_REPORT_METHOD), is(POST_METHOD));
    }

    @Test
    public void shouldLoadSpecializedProperties() {
        // When
        props = uut.loadProperties("other");

        // Then
        assertThat(props.getProperty(COLA_REPORT_METHOD), is(GET_METHOD));
    }

    @Test
    public void shouldParseArguments() {
        // When
        final Map<String, String> parseArguments = uut.parseArguments("kind:default id:10");

        // Then
        assertThat(parseArguments.size(), is(2));
    }

    @Test
    public void shouldNotIncludeInvalidArguments() {
        // When
        final Map<String, String> parseArguments = uut.parseArguments("kind:default id:10 invalid:");

        // Then
        assertThat(parseArguments.size(), is(2));
    }

    @Test
    public void shouldPrepareGetUrl() throws Exception {
        // When
        final String result = uut.prepareUrlTemplate(CONFIG_URL, args, ERROR_EXAMPLE).toString();

        // Then
        assertThat(result, containsString(ISSUE + "=" + paramEncoding(EXAMPLE_ID)));
        assertThat(result, containsString(STATE + "=" + paramEncoding(PASSED)));
        assertThat(result, containsString(ERROR + "=" + paramEncoding(ERROR_EXAMPLE.getMessage())));
    }

    @Test
    public void shouldReportInQueryUsingHttpGet() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_METHOD, GET_METHOD);
        final String expectedPathAndQuery = buildUri(CONFIG_PATH_QUERY);

        // When
        uut.reportUsingHttp(CONFIG_URL, args, props, ERROR_EXAMPLE);

        // Then
        assertThat(cortx.verify().get(expectedPathAndQuery).wasCalled(), is(true));
    }

    @Test
    public void shouldReportInQueryUsingHttpPost() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_METHOD, POST_METHOD);
        final String expectedPathAndQuery = buildUri(CONFIG_PATH_QUERY);

        // When
        uut.reportUsingHttp(CONFIG_URL, args, props, ERROR_EXAMPLE);

        // Then
        assertThat(cortx.verify().post(expectedPathAndQuery).wasCalled(), is(true));
    }

    @Test
    public void shouldReportInQueryUsingHttpPut() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_METHOD, PUT_METHOD);
        final String expectedUrl = buildUri(CONFIG_URL);

        // When
        uut.reportUsingHttp(CONFIG_URL, args, props, ERROR_EXAMPLE);

        // Then
        assertThat(cortx.verify().put(expectedUrl).wasCalled(), is(true));
    }

    @Test
    public void shouldReportInBodyUsingHttpPost() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_METHOD, POST_METHOD);
        props.setProperty(COLA_REPORT_TEMPLATE, CONFIG_BODY);
        final String expectedBody = buildBody(CONFIG_BODY);
        final String expectedPathAndQuery = buildUri(CONFIG_PATH_QUERY);

        // When
        uut.reportUsingHttp(CONFIG_URL, args, props, ERROR_EXAMPLE);

        // Then
        assertThat(
            cortx.verify().post(expectedPathAndQuery).withHeader(CONTENT_TYPE, APPLICATION_JSON).withBody(expectedBody)
            .wasCalled(), is(true));
    }

    @Test
    public void shouldReportInBodyUsingHttpPut() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_METHOD, PUT_METHOD);
        props.setProperty(COLA_REPORT_TEMPLATE, CONFIG_BODY);
        final String expectedPathAndQuery = buildUri(CONFIG_PATH_QUERY);
        final String expectedBody = buildBody(CONFIG_BODY);

        // When
        uut.reportUsingHttp(CONFIG_URL, args, props, ERROR_EXAMPLE);

        // Then
        assertThat(
            cortx.verify().put(expectedPathAndQuery).withHeader(CONTENT_TYPE, APPLICATION_JSON).withBody(expectedBody)
            .wasCalled(), is(true));
    }

    @Test
    public void shouldReportPass() throws Exception {
        // Given
        props.setProperty(COLA_REPORT_TEMPLATE, CONFIG_BODY);
        final String expectedPathAndQuery = buildUriWithNoError(CONFIG_PATH_QUERY);
        final String expectedBody = buildBodyWithNoError(CONFIG_BODY);

        // When
        uut.report(ISSUE_ARG, null);

        // Then
        assertThat(
            cortx.verify().post(expectedPathAndQuery).withHeader(CONTENT_TYPE, APPLICATION_JSON).withBody(expectedBody)
            .wasCalled(), is(true));
    }

    @Test
    public void shouldReportFail() throws Exception {
        // Given
        args.put(STATE, FAILED);
        props.setProperty(COLA_REPORT_TEMPLATE, CONFIG_BODY);
        final String expectedPathAndQuery = buildUri(CONFIG_PATH_QUERY);
        final String expectedBody = buildBody(CONFIG_BODY);

        // When
        uut.report(ISSUE_ARG, ERROR_EXAMPLE);

        // Then
        assertThat(
            cortx.verify().post(expectedPathAndQuery).withHeader(CONTENT_TYPE, APPLICATION_JSON).withBody(expectedBody)
            .wasCalled(), is(true));
    }

    private String buildUri(final String template) throws UnsupportedEncodingException {
        return template.replace("${" + ISSUE + "}", paramEncoding(EXAMPLE_ID))
            .replace("${state}", paramEncoding(args.get(STATE)))
            .replace("${error}", paramEncoding(ERROR_EXAMPLE.getMessage()));
    }

    private String buildUriWithNoError(final String template) throws UnsupportedEncodingException {
        return template.replace("${" + ISSUE + "}", paramEncoding(EXAMPLE_ID))
            .replace("${state}", paramEncoding(args.get(STATE))).replace("${error}", "none");
    }

    private String buildBody(final String template) throws UnsupportedEncodingException {
        return template.replace("${" + ISSUE + "}", EXAMPLE_ID).replace("${state}", args.get(STATE))
            .replace("${error}", ERROR_EXAMPLE.getMessage());
    }

    private String buildBodyWithNoError(final String template) throws UnsupportedEncodingException {
        return template.replace("${" + ISSUE + "}", EXAMPLE_ID).replace("${state}", args.get(STATE))
            .replace("${error}", "none");
    }
}