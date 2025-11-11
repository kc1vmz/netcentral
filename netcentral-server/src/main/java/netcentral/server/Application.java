package netcentral.server;

import org.slf4j.bridge.SLF4JBridgeHandler;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;


@OpenAPIDefinition(
    info = @Info(
            title = "netcentral-server",
            version = "1.0.0"
    )
)

public class Application {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
  
        Micronaut.build(args).banner(false).start();
    }
}