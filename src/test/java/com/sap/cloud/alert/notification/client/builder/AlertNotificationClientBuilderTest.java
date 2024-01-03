package com.sap.cloud.alert.notification.client.builder;

import com.sap.cloud.alert.notification.client.IAlertNotificationClient;
import com.sap.cloud.alert.notification.client.IRetryPolicy;
import com.sap.cloud.alert.notification.client.ServiceRegion;
import com.sap.cloud.alert.notification.client.exceptions.ClientRequestException;
import com.sap.cloud.alert.notification.client.internal.AlertNotificationClient;
import com.sap.cloud.alert.notification.client.internal.BasicAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.OAuthAuthorizationHeader;
import com.sap.cloud.alert.notification.client.internal.SimpleRetryPolicy;
import com.sap.cloud.alert.notification.client.model.AlertNotificationServiceBinding;
import com.sap.cloud.alert.notification.client.model.DestinationServiceBinding;
import org.apache.http.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.sap.cloud.alert.notification.client.TestUtils.*;
import static org.apache.http.impl.client.HttpClients.createDefault;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlertNotificationClientBuilderTest {

    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final String TEST_CERTIFICATE = "-----BEGIN CERTIFICATE-----\nMIIFwjCCA6qgAwIBAgIRAJEJ6qwdQRbTeiKhUcDLNh4wDQYJKoZIhvcNAQELBQAw\ngYAxCzAJBgNVBAYTAkRFMRQwEgYDVQQHDAtFVTEwLUNhbmFyeTEPMA0GA1UECgwG\nU0FQIFNFMSMwIQYDVQQLDBpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czElMCMG\nA1UEAwwcU0FQIENsb3VkIFBsYXRmb3JtIENsaWVudCBDQTAeFw0yMjA5MTkwNzM0\nNDBaFw0yMzA5MTkwODM0NDBaMIGnMQswCQYDVQQGEwJERTEPMA0GA1UEChMGU0FQ\nIFNFMSMwIQYDVQQLExpTQVAgQ2xvdWQgUGxhdGZvcm0gQ2xpZW50czEPMA0GA1UE\nCxMGQ2FuYXJ5MRAwDgYDVQQLEwdzYXAtdWFhMRAwDgYDVQQHEwdzYXAtdWFhMS0w\nKwYDVQQDEyQzZDc0NjFkYy1hOTkwLTQ0NWMtODA3OC0zMDlmOTM3NmY5MDgwggEi\nMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDLg1TJHDGYXGFj8fU4agx57EZ5\nSyH86PG2GtvBcpcBII1y727NqpWn9HXFO2GdQqvzyN3ApU2b/tqDqqfUxdjbfOn8\nPywlmQrPB35R53InN/6tiFP5YQ3XRdquNXWs29864/m3r5WwUcYarwEBqEEwkOga\nlwbCZwk9Dn4z9FsT+/RtWKEcSoFwye0blNK2Yg3rxvt8Y8js8vszbtalQr61Q/9V\nW5Cf+6zS7sHciSF1ccdl894DPEE5vz+yq5r3jfrC63E60egyTYi7W6+B5R9yE3Xd\nwJxN9j5WKZHIICzR6BW+UD2K2neWw/aX/3I315bkOloNT7tiyne7b2+Sgzq5AgMB\nAAGjggEMMIIBCDAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFMwghFuHFYs3+X6Hsck+\nw2+VBG6RMB0GA1UdDgQWBBRajDFtHBL1GYr1szPHXzggOeTxozAOBgNVHQ8BAf8E\nBAMCBaAwEwYDVR0lBAwwCgYIKwYBBQUHAwIwgZUGA1UdHwSBjTCBijCBh6CBhKCB\ngYZ/aHR0cDovL3NhcC1jbG91ZC1wbGF0Zm9ybS1jbGllbnQtY2EtZXUxMC1jYW5h\ncnktY3Jscy5zMy5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbS9jcmwvZjhhYmU1\nNGUtMTk1MS00NzBlLWFlMmQtZGU0MGMxNjMzNDFjLmNybDANBgkqhkiG9w0BAQsF\nAAOCAgEAGP4FkEtboIKBsda4DM3caKALOzMJM3Sk7zSbl5ryWcSzBpoTWB1mSGuh\nZct0+wWT8fWzNhKuMNyVMc2ih3HNSgmmnn0RUT0PmgZcy90EX6E7ynnyiOqcTtwS\nECylUSXsER1pq9BoQNQ5OGWE1PHFOgxTIeke//yY7S19NpbwQpPm/y+07rOW6T3z\nfUl83NVpIWffpK8jx1i3itpN6yTzieR9RrPYI6FaLoHCNCgZE8BEHHhJlS/CRTyT\nB1wO7qaiDMMeAaragXEInFJldOkWqzaPhptcISMcglp7sGKbNHT88e5piumCe1M+\n8XmRYbOSTObnsIIcNAqlk3w33s2qE85jRlodApXh/Tw4urhN78449ou3pLmwobM0\ny0xE4mOPfSUJvl801JeylHjO82PjZvR/TKurPSpL/fgkfFnMPiikIF3fiby/cxwj\n9dl6E3P5KUbpd7m4Ovno1AN4jCcD4NNZ4PDHaKvB4rZOE4myEujteSXrfDmigpLC\nuBVvfOc7gNEKM2oOq04e/ocwrWTSKfgO3wQCd0PuyMRlf5Z5/oeXogL0Ors8rXnq\n23RdVUTQn+CMPvSfzoFiXbf+6qHFjZtmZlFvTYZ8QsnLbtP1E5OVAqwiNCvAZK9n\nPXEDh5hMmzfz9KYiDB/mZSWIWitB/6u5iUWpsIQ1bJ2Pq42rfB0=\n-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\n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\n2JX4S0blcGoU+np5\n-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\nMIIFZjCCA06gAwIBAgIQGHcPvmUGa79M6pM42bGFYjANBgkqhkiG9w0BAQsFADBN\nMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYxDzANBgNVBAoMBlNBUCBT\nRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwHhcNMTkwMjEzMTExOTM2WhcN\nMzkwMjEzMTEyNjMyWjBNMQswCQYDVQQGEwJERTERMA8GA1UEBwwIV2FsbGRvcmYx\nDzANBgNVBAoMBlNBUCBTRTEaMBgGA1UEAwwRU0FQIENsb3VkIFJvb3QgQ0EwggIi\nMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQChbHLXJoe/zFag6fB3IcN3d3HT\nY14nSkEZIuUzYs7B96GFxQi0T/2s971JFiLfB4KaCG+UcG3dLXf1H/wewq8ahArh\nFTsu4UR71ePUQiYlk/G68EFSy2zWYAJliXJS5k0DFMIWHD1lbSjCF3gPVJSUKf+v\nHmWD5e9vcuiPBlSCaEnSeimYRhg0ITmi3RJ4Wu7H0Xp7tDd5z4HUKuyi9XRinfvG\nkPALiBaX01QRC51cixmo0rhVe7qsNh7WDnLNBZeA0kkxNhLKDl8J6fQHKDdDEzmZ\nKhK5KxL5p5YIZWZ8eEdNRoYRMXR0PxmHvRanzRvSVlXSbfqxaKlORfJJ1ah1bRNt\no0ngAQchTghsrRuf3Qh/2Kn29IuBy4bjKR9CdNLxGrClvX/q26rUUlz6A3lbXbwJ\nEHSRnendRfEiia+xfZD+NG2oZW0IdTXSqkCbnBnign+uxGH5ECjuLEtvtUx6i9Ae\nxAvK2FqIuud+AchqiZBKzmQAhUjKUoACzNP2Bx2zgJOeB0BqGvf6aldG0n2hYxJF\n8Xssc8TBlwvAqtiubP/UxJJPs+IHqU+zjm7KdP6dM2sbE+J9O3n8DzOP0SDyEmWU\nUCwnmoPOQlq1z6fH9ghcp9bDdbh6adXM8I+SUYUcfvupOzBU7rWHxDCXld/24tpI\nFA7FRzHwKXqMSjwtBQIDAQABo0IwQDAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/\nBAUwAwEB/zAdBgNVHQ4EFgQUHLxmKw7KjUufjZNxqQ/KZ0ZpEyIwDQYJKoZIhvcN\nAQELBQADggIBABdSKQsh3EfVoqplSIx6X43y2Pp+kHZLtEsRWMzgO5LhYy2/Fvel\neRBw/XEiB5iKuEGhxHz/Gqe0gZixw3SsHB1Q464EbGT4tPQ2UiMhiiDho9hVe6tX\nqX1FhrhycAD1xHIxMxQP/buX9s9arFZauZrpw/Jj4tGp7aEj4hypWpO9tzjdBthy\n5vXSviU8L2HyiQpVND/Rp+dNJmVYTiFLuULRY28QbikgFO2xp9s4RNkDBnbDeTrT\nCKWcVsmlZLPJJQZm0n2p8CvoeAsKzIULT9YSbEEBwmeqRlmbUaoT/rUGoobSFcrP\njrBg66y5hA2w7S3tDH0GjMpRu16b2u0hYQocUDuMlyhrkhsO+Qtqkz1ubwHCJ8PA\nRJw6zYl9VeBtgI5F69AEJdkAgYfvPw5DJipgVuQDSv7ezi6ZcI75939ENGjSyLVy\n4SuP99G7DuItG008T8AYFUHAM2h/yskVyvoZ8+gZx54TC9aY9gPIKyX++4bHv5BC\nqbEdU46N05R+AIBW2KvWozQkjhSQCbzcp6DHXLoZINI6y0WOImzXrvLUSIm4CBaj\n6MTXInIkmitdURnmpxTxLva5Kbng/u20u5ylIQKqpcD8HWX97lLVbmbnPkbpKxo+\nLvHPhNDM3rMsLu06agF4JTbO8ANYtWQTx0PVrZKJu+8fcIaUp7MVBIVZ\n-----END CERTIFICATE-----\n";
    private static final String TEST_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEAy4NUyRwxmFxhY/H1OGoMeexGeUsh/OjxthrbwXKXASCNcu9u\nzaqVp/R1xTthnUKr88jdwKVNm/7ag6qn1MXY23zp/D8sJZkKzwd+UedyJzf+rYhT\n+WEN10XarjV1rNvfOuP5t6+VsFHGGq8BAahBMJDoGpcGwmcJPQ5+M/RbE/v0bVih\nHEqBcMntG5TStmIN68b7fGPI7PL7M27WpUK+tUP/VVuQn/us0u7B3IkhdXHHZfPe\nAzxBOb8/squa9436wutxOtHoMk2Iu1uvgeUfchN13cCcTfY+VimRyCAs0egVvlA9\nitp3lsP2l/9yN9eW5DpaDU+7Ysp3u29vkoM6uQIDAQABAoIBAAQaJJZdLB6/FfN2\nyBOYB6JZgd4mBoxbgavDBNT0Y1jReCht7RTWMGgPRGRyWvhZhK/IR7f/XP4c9/iZ\ncwKV7lYWQX0lJNWpLQ+ZPhGWkbV6qnaC/fZT33yMWukSCMowMGK2f60VK/Y+37hf\n+Kw44P+CnDsU+jzm6MfDSAXyEffoHgLvCEUbh5oK7+4dry8cI+V2nosZeKn/WZ7t\nSChqRHtDnbYkdGhOH+Ac+rKxTZPSua0ylNEtQUVeRaM3xk29EIsWqj6GxNSD8oFL\n3vXgmq72xIC94dTSeCJNf17Jm7niDQXZn4IMNGjPXtyONJwARF0wPngGhwzgS6RT\nkdYhyMUCgYEA7MxMYqDoTWRm34QktyGjljCWZ+jLpv6SlX/9zZ+HnzxmphF9ZtQa\nE2ck4DED/XGnKHBr965T5iLdwGWXEYHeYHaZxPxMYlILsULmjdeoCDj2GvYAWEf7\nnNPxpWap/zrlJipBbj5GdYVME1L5ezzavpVKKDs4KBIlK+J203JqDJMCgYEA3AQR\nPkLVwpGcnWAWhHZzFMevs/6QntYmVV5irZxrTOkNOF3/Y5fXfLGVA0J7+ugFPkWE\naJtvJhAvydD5HHLmx2wPhmrlxM2yPMbe5HmuWccTxM+laUnxJMAcFKJi5KDwJ/2+\nUQKa5WJSjKiepDDizlYvPl1t/gQ5cnxMGleYtwMCgYB7HbVRSku9gUgjSjc0p96/\nxb9NgzHvP0jDReqVsC35UpQkH8/NWNW95NC9Z36llSPN2LWp7w9cBiC5WZhz18vg\n54kHbA3iyLmfjiME+G066TK7zc9cFwDxBxkKYBhexSZC85FVWjeT/pwRKADiXD92\n+3O0+yU1YEnHSVVylngg1QKBgH0hTxFC/8H2AMW8tXHG0DK8UyCiomvDze91i9fD\ng38teJhbVXm2DRddBCvjbxHHTdwZu3GnHTLft94nHNbiPoCi472GJIGmnz1Tucbl\nsZRb1dF0a1YTeLN3E0FlDauMIKoN9WSrf58AKYTYDcnCB+xkNeBZUMpMasPDD6FX\nuoIzAoGBAJj/fIUcL/g0J2bkRKr1wfOiYd8PC6QtZHlYXQQmXlKuelUXBh2ECIcg\nYB/llhtO3YfJxPIskbTfkOTs7fL8GbOq4wcYnW6uAl7m61YVrzNQY0F5D6PvSfd8\nlJJ5M7cgg93YtwKPkkC054F/DegN/aBzsLHZkE78nms4dPdeVYdh\n-----END RSA PRIVATE KEY-----\n";
    private static final URI TEST_OAUTH_SERVICE_URI = URI.create("https://nowhere.com");

    private static final Long TEST_INVALIDATION_TIME = 1L;

    private HttpClient testHttpClient;
    private IRetryPolicy testRetryPolicy;
    private ServiceRegion testServiceRegion;
    private AlertNotificationClientBuilder classUnderTest;
    private AlertNotificationServiceBinding alertNotificationServiceBinding;
    private DestinationServiceBinding destinationServiceBinding;

    @BeforeEach
    public void setUp() {
        testServiceRegion = ServiceRegion.EU10;
        testHttpClient = mock(HttpClient.class);
        testRetryPolicy = new SimpleRetryPolicy();
        classUnderTest = new AlertNotificationClientBuilder(testHttpClient);
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        destinationServiceBinding = new DestinationServiceBinding(TEST_SERVICE_URI, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_CLIENT_ID, TEST_CLIENT_SECRET).getValue(), createdClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatBasicAuthenticationIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        alertNotificationServiceBinding = new AlertNotificationServiceBinding(TEST_SERVICE_URI, null, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(new BasicAuthorizationHeader(TEST_CLIENT_ID, TEST_CLIENT_SECRET).getValue(), createdClient.getAuthorizationHeader().getValue());
    }

    @Test
    public void givenThatOAuthAuthenticationIsUsed_whenBuildIsCalled_thenCorrectAffectedClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion)
                .withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_OAUTH_SERVICE_URI).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatOAuthAuthenticationIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatOAuthAuthenticationWithCertificateIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withAuthentication(TEST_CERTIFICATE, TEST_PRIVATE_KEY).withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatOAuthAuthenticationWithCertificateIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion)
                .withAuthentication(TEST_CERTIFICATE, TEST_PRIVATE_KEY, TEST_OAUTH_SERVICE_URI, TEST_CLIENT_ID).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void givenThatAuthenticationWithCertificateIsUsed_whenBuildFromServiceBindingIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withCertificate(TEST_CERTIFICATE, TEST_PRIVATE_KEY).withRetryPolicy(testRetryPolicy).buildFromServiceBinding(alertNotificationServiceBinding);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
    }

    @Test
    public void givenThatAuthenticationWithCertificateIsUsed_whenBuildIsCalled_thenCorrectClientIsCreated() {
        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).withServiceRegion(testServiceRegion)
                .withCertificate(TEST_CERTIFICATE, TEST_PRIVATE_KEY).build();

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(testServiceRegion, createdClient.getServiceRegion());
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withBasicCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(BasicAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withOauthCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse())
                .thenReturn(createOauthAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertEquals(OAuthAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withCertificateCredentials_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createCertificateAuthenticationResponse())
                .thenReturn(createCertificateAuthenticationResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(((SimpleRetryPolicy) testRetryPolicy).getMaxRetries(), ((SimpleRetryPolicy) createdClient.getRetryPolicy()).getMaxRetries());
        assertNull(createdClient.getAuthorizationHeader());
    }

    @Test
    public void whenBuildFromDestinationServiceBindingIsCalled_withInvalidationTimeConfigured_thenCorrectClientIsCreated() throws Exception {
        when(testHttpClient.execute(any()))
                .thenReturn(createOAuthHttpResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse())
                .thenReturn(createBasicAuthenticationHeaderResponse());

        AlertNotificationClient createdClient = classUnderTest.withRetryPolicy(testRetryPolicy).buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, TEST_INVALIDATION_TIME);

        assertEquals(testHttpClient, createdClient.getHttpClient());
        assertEquals(createdClient.getServiceRegion(), TEST_DESTINATION_SERVICE_REGION);
        assertEquals(BasicAuthorizationHeader.class, createdClient.getAuthorizationHeader().getClass());
        assertCorrectInvalidationTime(createdClient, TEST_INVALIDATION_TIME);
    }

    @Test
    public void givenInvalidationTimeIsNegative_whenBuildFromDestinationServiceBindingIsCalled_thenExceptionIsThrown() {
        assertThrows(ClientRequestException.class, () -> classUnderTest.buildFromDestinationBinding(destinationServiceBinding, TEST_DESTINATION_NAME, -1L));
    }

    @Test
    public void givenThatInvalidHttpClientIsGiven_whenBuilderIsCreated_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            new AlertNotificationClientBuilder(null);
        });
    }

    @Test
    public void givenThatWithHttpClientIsUsed_whenBuilderIsCreated_thenCreateClientUsingProvidedHttpClient() {
        assertEquals(new AlertNotificationClientBuilder().withServiceRegion(testServiceRegion).withHttpClient(testHttpClient).build().getHttpClient(), testHttpClient);
    }

    @Test
    public void givenNoHttpClientIsGiven_whenBuilderIsCreated_thenCreateUsingDefaultHttpClient() {
        assertNotNull(new AlertNotificationClientBuilder().withServiceRegion(testServiceRegion).build().getHttpClient());
    }

    @Test
    public void givenThatNoServiceRegionIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest.withServiceRegion(null).withRetryPolicy(testRetryPolicy).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();
        });
    }

    @Test
    public void givenThatNullRetryPolicyIsGiven_whenBuildIsCalled_thenExceptionIsThrown() {
        assertThrows(NullPointerException.class, () -> {
            classUnderTest.withRetryPolicy(null).withServiceRegion(testServiceRegion).withAuthentication(TEST_CLIENT_ID, TEST_CLIENT_SECRET).build();
        });
    }

    private void assertCorrectInvalidationTime(IAlertNotificationClient client, Long expectedInvalidationTime) {
        Long invalidationTime = extractSuperClassFieldValue(client, "invalidationTime");
        assertEquals(invalidationTime, expectedInvalidationTime);
    }
}
