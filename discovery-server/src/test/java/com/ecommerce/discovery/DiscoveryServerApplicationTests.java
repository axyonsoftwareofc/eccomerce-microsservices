// discovery-server/src/test/java/com/ecommerce/discovery/DiscoveryServerApplicationTests.java
package com.ecommerce.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
        "spring.cloud.discovery.enabled=false"
})
class DiscoveryServerApplicationTests {

    @Test
    void contextLoads() {
        // Context loads successfully
    }
}