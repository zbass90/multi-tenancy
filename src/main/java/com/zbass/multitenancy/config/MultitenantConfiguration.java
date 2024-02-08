package com.zbass.multitenancy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class MultitenantConfiguration {

    @Value("${defaultTenant}")
    private String defaultTenant;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        Map<Object, Object> resolvedDataSources = new HashMap<>(loadDataSources());
        dataSource.setTargetDataSources(resolvedDataSources);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(defaultTenant));
        return dataSource;
    }

    private Map<Object, DataSource> loadDataSources() {
        Map<Object, DataSource> resolvedDataSources = new HashMap<>();
        File[] files = Paths.get("allTenants").toFile().listFiles();

        if (files != null) {
            for (File propertyFile : files) {
                Properties tenantProperties = new Properties();
                DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

                try {
                    tenantProperties.load(new FileInputStream(propertyFile));
                    String tenantId = tenantProperties.getProperty("name");

                    dataSourceBuilder.driverClassName(tenantProperties.getProperty("datasource.driver-class-name"));
                    dataSourceBuilder.username(tenantProperties.getProperty("datasource.username"));
                    dataSourceBuilder.password(tenantProperties.getProperty("datasource.password"));
                    dataSourceBuilder.url(tenantProperties.getProperty("datasource.url"));

                    resolvedDataSources.put(tenantId, dataSourceBuilder.build());
                } catch (IOException exp) {
                    throw new RuntimeException("Problem in tenant datasource:" + exp);
                }
            }
        } else {
            throw new RuntimeException("No property files found in 'allTenants' directory");
        }

        return resolvedDataSources;
    }
}