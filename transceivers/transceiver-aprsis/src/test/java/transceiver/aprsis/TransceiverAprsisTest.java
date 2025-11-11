package transceiver.aprsis;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;

@MicronautTest
class TransceiverAprsisTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks() {
        Assertions.assertTrue(true);
    }

}
