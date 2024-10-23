package md.mirrerror;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class PrLab2Application {

    private final DataSourceProperties dataSourceProperties;

    public static void main(String[] args) {
        SpringApplication.run(PrLab2Application.class, args);
    }

    @Bean
    public DataSource dataSource() {
        PoolProperties poolProperties = new PoolProperties();

        poolProperties.setUrl(dataSourceProperties.getUrl());
        poolProperties.setDriverClassName(dataSourceProperties.getDriverClassName());
        poolProperties.setUsername(dataSourceProperties.getUsername());
        poolProperties.setPassword(dataSourceProperties.getPassword());

        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);

        return dataSource;
    }

}
