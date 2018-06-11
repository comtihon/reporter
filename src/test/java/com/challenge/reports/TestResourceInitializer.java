package com.challenge.reports;

import com.challenge.reports.service.ResourceInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Profile("test")
@Component
public class TestResourceInitializer extends ResourceInitializer {
    @Value("${resource.path}")
    private String resourcePath;
    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    @EventListener
    public void onApplicationEvent(ApplicationStartedEvent event) throws IOException {
        Stream.of("2018_01_report.csv", "2018_02_report.csv")
                .map(path -> Paths.get(resourcePath, path).toString())
                .map(path -> resourceLoader.getResource(path))
                .forEach(path -> {
                    try {
                        super.loadObjectList(path.getFile().getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        log.info("All files were processed");
    }
}
