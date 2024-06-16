package test.integration;

import com.example.HelloResource;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Application;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Set;

import static jakarta.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.BDDAssertions.then;

public class HelloIT {

    @BeforeAll
    static void bootstrap() {
        SeBootstrap
                // to use the actual HelloApplication class, we'd have to explicitly list the classes there...
                // auto discovery works in the container, but not in SeBootstrap
                .start(new Application() {
                    @Override public Set<Class<?>> getClasses() {
                        return Set.of(HelloResource.class);
                    }
                })
                .thenApply(instance -> {
                    BASE_URI = instance.configuration().baseUri();
                    System.out.println("Bootstrapped on " + BASE_URI);
                    return instance;
                })
                .toCompletableFuture().join();
    }

    static URI BASE_URI;
    private static final Client CLIENT = ClientBuilder.newClient();

    private final WebTarget service = CLIENT.target(BASE_URI);

    @Test
    void shouldGetGreetingResponse() {
        var response = service.path("/hello").request().get();

        then(response.getStatusInfo()).isEqualTo(OK);
        then(response.readEntity(String.class)).isEqualTo("Hello from Service");
    }
}
