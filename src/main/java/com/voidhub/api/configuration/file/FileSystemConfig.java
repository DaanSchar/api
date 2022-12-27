package com.voidhub.api.configuration.file;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.filesystem")
@Component
@NoArgsConstructor
@Getter
@Setter
public class FileSystemConfig {

    private String path;

}
