package com.mtons.mblog.modules.repository;

import com.mtons.mblog.BootApplication;
import com.mtons.mblog.modules.entity.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * pic repo test
 *

 */
@SpringBootTest(classes = BootApplication.class)
public class ResourceRepositoryTest {

    @Autowired
    ResourceRepository resourceRepository;

    @Test
    public void find0Before() {

        LocalDateTime now = LocalDateTime.now();
        String timeStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(now);
        List<Resource> beforeResources = resourceRepository.find0Before(timeStr);
        System.out.println(beforeResources);
    }
}
