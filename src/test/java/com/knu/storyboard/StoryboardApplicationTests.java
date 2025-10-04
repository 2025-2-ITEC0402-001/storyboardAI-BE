package com.knu.storyboard;

import com.knu.storyboard.config.MySQLTestContainerConfig;
import com.knu.storyboard.config.RedisTestContainerConfig;
import com.knu.storyboard.config.TestEnvironmentConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith({RedisTestContainerConfig.class, MySQLTestContainerConfig.class, TestEnvironmentConfig.class})
class StoryboardApplicationTests {

    @Test
    void contextLoads() {
    }

}
