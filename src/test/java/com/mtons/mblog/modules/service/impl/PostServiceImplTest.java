package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.BootApplication;
import com.mtons.mblog.modules.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * post service test
 *

 */
@SpringBootTest(classes = BootApplication.class)
public class PostServiceImplTest {

    @Autowired
    PostServiceImpl postService;

    @Test
    public void cleanPostPic() {
    }
}
