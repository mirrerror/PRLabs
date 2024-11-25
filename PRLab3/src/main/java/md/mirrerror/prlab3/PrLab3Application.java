package md.mirrerror.prlab3;

import lombok.RequiredArgsConstructor;
import md.mirrerror.prlab3.raft.Node;
import md.mirrerror.prlab3.utils.EmailManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
public class PrLab3Application {

    private final DataSourceProperties dataSourceProperties;
    private final Node node;

    public static void main(String[] args) {
        EmailManager emailManager = new EmailManager();
        emailManager.sendEmail();

        SpringApplication.run(PrLab3Application.class, args);
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
