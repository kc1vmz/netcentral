package netcentral.transceiver.aprsis;

import org.slf4j.bridge.SLF4JBridgeHandler;

import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
  
        Micronaut.build(args).banner(false).start();
    }
}