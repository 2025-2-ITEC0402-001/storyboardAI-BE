package com.knu.storyboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith({RedisTestContainerConfig.class, MySQLTestContainerConfig.class, TestEnvironmentConfig.class})
class StoryboardApplicationTests {

    @Test
    void contextLoads() {
    }

}
