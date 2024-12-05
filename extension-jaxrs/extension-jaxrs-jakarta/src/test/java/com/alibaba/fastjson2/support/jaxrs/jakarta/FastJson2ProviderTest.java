package com.alibaba.fastjson2.support.jaxrs.jakarta;

import com.alibaba.fastjson2.support.jaxrs.jakarta.model.User;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import lombok.SneakyThrows;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 张治保
 * @since 2024/10/16
 */
public class FastJson2ProviderTest
        extends JerseyTest {
    private static final FastJson2Provider fastJson2Provider = new FastJson2Provider();

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(JaxRsResource.class)
                .register(fastJson2Provider, MessageBodyReader.class, MessageBodyWriter.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(fastJson2Provider, MessageBodyReader.class, MessageBodyWriter.class);
    }

    @Test
    @SneakyThrows
    void testGet() {
        String json = successAndGet(
                target().path("/test/get")
                        .request()
                        .get()
        );
        JSONAssert.assertEquals("{\"name\":\"fastjson2\",\"age\":0}", json, true);
    }

    @SneakyThrows
    @Test
    void testGetPath() {
        //MODULE_ORDER: One does not simply declare modules!
        String json = successAndGet(
                target().path("/test/get/hello-fastjson2")
                        .request()
                        .get()
        );
        JSONAssert.assertEquals("{\"name\":\"hello-fastjson2\",\"age\":0}", json, true);
    }

    @Test
    @SneakyThrows
    void testPost() {
        User user = new User()
                .setName("fastjson2")
                .setAge(1);
        String json = successAndGet(
                target().path("/test/post")
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.json(user))
        );
        JSONAssert.assertEquals("{\"name\":\"fastjson2\",\"age\":1}", json, true);
    }

    public static String successAndGet(Response response) {
        try (response) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            return response.readEntity(String.class);
        }
    }
}