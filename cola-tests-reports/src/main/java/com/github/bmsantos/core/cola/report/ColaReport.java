package com.github.bmsantos.core.cola.report;

import static com.github.bmsantos.core.cola.utils.ColaUtils.isSet;
import static com.github.bmsantos.core.cola.utils.ColaUtils.paramEncoding;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.github.bmsantos.core.cola.config.ConfigurationManager;

public class ColaReport implements Report {
    private static final String STATE_ARG = "state";

    private static Logger log = getLogger(ColaReport.class);

    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";
    private static final String REPORT_KIND = "kind";

    public static final String COLA_REPORT_URL = "cola.tests.report.url";
    public static final String COLA_REPORT_METHOD = "cola.tests.report.method";
    public static final String COLA_REPORT_TEMPLATE = "cola.tests.report.body.template";
    public static final String COLA_REPORT_CONTENT_TYPE = "cola.tests.report.body.content-type";
    public static final String COLA_REPORT_USERNAME = "cola.tests.report.username";
    public static final String COLA_REPORT_PASSWORD = "cola.tests.report.password";
    public static final String COLA_REPORT_CODE_FAIL = "cola.tests.report.code.fail";
    public static final String COLA_REPORT_CODE_PASS = "cola.tests.report.code.pass";

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public void report(final String parameters, final Throwable error) {
        try {
            final Map<String, String> args = parseArguments(parameters);
            final Properties props = loadProperties(args.get(REPORT_KIND));

            final String url = props.getProperty(COLA_REPORT_URL);
            if (!isSet(url)) {
                return;
            }

            if (isSet(error)) {
                args.put(STATE_ARG, props.containsKey(COLA_REPORT_CODE_FAIL) ? props.getProperty(COLA_REPORT_CODE_FAIL) : "FAILED");
            } else {
                args.put(STATE_ARG, props.containsKey(COLA_REPORT_CODE_PASS) ? props.getProperty(COLA_REPORT_CODE_PASS) : "PASSED");
            }

            final String protocol = url.toLowerCase();
            if (protocol.startsWith(HTTP_PROTOCOL) || protocol.startsWith(HTTPS_PROTOCOL)) {
                reportUsingHttp(url, args, props, error);
            } else {
                log.info("COLA Tests Reports only support HTTP or HTTPS reporting.");
            }
        } catch (final Throwable t) {
            log.error("Failed to submit report.", t.getMessage());
        }
    }

    public void reportUsingHttp(final String url, final Map<String, String> args, final Properties props,
        final Throwable error)
            throws Exception {
        final String method = props.getProperty(COLA_REPORT_METHOD);
        if (!isSet(method)) {
            log.error("Cola Reports require HTTP method (get/put/post).");
            return;
        }

        HttpRequestBase request;
        switch (method.toUpperCase()) {
        case HttpGet.METHOD_NAME: {
            request = prepareGetRequest(url, args, error);
            break;
        }
        case HttpPost.METHOD_NAME: {
            request = preparePostRequest(url, args, props, error);
            break;
        }
        case HttpPut.METHOD_NAME: {
            request = preparePutRequest(url, args, props, error);
            break;
        }
        default: {
            log.error("Invalid HTTP method \"" + method + "\".");
            return;
        }
        }

        final HttpClient httpclient = createHttpClient(props);
        final HttpResponse response = httpclient.execute(request);
        EntityUtils.consume(response.getEntity());
    }

    private CloseableHttpClient createHttpClient(final Properties props) {
        final HttpClientBuilder builder = HttpClientBuilder.create();

        final String username = props.getProperty(COLA_REPORT_USERNAME);
        final String password = props.getProperty(COLA_REPORT_PASSWORD);

        if (isSet(username) && isSet(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }

        return builder.build();
    }

    private HttpGet prepareGetRequest(final String url, final Map<String, String> args,
        final Throwable error)
            throws Exception {
        return new HttpGet(prepareUrlTemplate(url, args, error));
    }

    private HttpPost preparePostRequest(final String url, final Map<String, String> args, final Properties props,
        final Throwable error)
            throws Exception {

        final HttpPost httpPost = new HttpPost(prepareUrlTemplate(url, args, error));

        final String template = props.getProperty(COLA_REPORT_TEMPLATE);
        final String contentType = props.getProperty(COLA_REPORT_CONTENT_TYPE);

        if (isSet(template) && isSet(contentType)) {
            httpPost.setEntity(new StringEntity(prepareBodyTemplate(template, args, error)));
            httpPost.setHeader(CONTENT_TYPE, contentType);
        }

        return httpPost;
    }

    private HttpPut preparePutRequest(final String url, final Map<String, String> args, final Properties props,
        final Throwable error)
            throws Exception {
        final HttpPut httpPut = new HttpPut(prepareUrlTemplate(url, args, error));

        final String template = props.getProperty(COLA_REPORT_TEMPLATE);
        final String contentType = props.getProperty(COLA_REPORT_CONTENT_TYPE);

        if (isSet(template) && isSet(contentType)) {
            httpPut.setEntity(new StringEntity(prepareBodyTemplate(template, args, error)));
            httpPut.setHeader(CONTENT_TYPE, contentType);
        }

        return httpPut;
    }

    public String prepareBodyTemplate(final String template, final Map<String, String> args, final Throwable error)
        throws Exception {
        String result = replaceOnTemplate(template, args, error, false);

        String stackTrace = "none";
        if (isSet(error)) {
            final StringWriter sw = new StringWriter();
            error.printStackTrace(new PrintWriter(sw));
            stackTrace = sw.toString();
        }
        result = result.replace("${stackTrace}", stackTrace);

        return result.toString();
    }

    public URI prepareUrlTemplate(final String url, final Map<String, String> args, final Throwable error)
        throws Exception {
        String result = url;

        result = replaceOnTemplate(url, args, error, true);

        return new URI(result.toString());
    }

    private String replaceOnTemplate(final String template, final Map<String, String> args, final Throwable error,
        final boolean encode)
            throws UnsupportedEncodingException {
        String result = template;

        for (final String key : args.keySet()) {
            result = result.replace("${" + key + "}", encode ? paramEncoding(args.get(key)) : args.get(key));
        }

        String message = "none";
        if (isSet(error)) {
            message = encode ? paramEncoding(error.getMessage()) : error.getMessage();
        }
        result = result.replace("${error}", message);
        return result;
    }

    public Map<String, String> parseArguments(final String parameters) {
        final Map<String, String> args = new HashMap<>();
        if (!isSet(parameters)) {
            return args;
        }

        for (final String parameter : parameters.trim().split(" ")) {
            final String[] value = parameter.split(":");
            if (value.length == 2) {
                args.put(value[0], value[1]);
            }
        }

        return args;
    }

    public Properties loadProperties(final String name) {
        final Properties props = new Properties();

        final String descriminator = isSet(name) && !name.equals("report") ? "-" + name : "";
        try (final InputStream in = ConfigurationManager.class.getResourceAsStream("/cola-tests-report"
            + descriminator + ".properties")) {
            props.load(in);
        } catch (final Exception e) {
            log.error("Failed to load cola-reports properties file.");
        }

        return props;
    }
}