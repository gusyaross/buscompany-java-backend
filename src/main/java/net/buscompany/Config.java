package net.buscompany;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan("net.buscompany.dao")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class Config {

    @Value("${db.url}")
    private String URL;

    @Value("${db.username}")
    private String USERNAME;

    @Value("${db.password}")
    private String PASSWORD;

    @Value("${db.driverClassName}")
    private String DRIVER;

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    @Value("${mybatis.type-aliases-package}")
    private String aliasesLocations;

    @Bean
    public DataSource dataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setUrl(URL);
        dataSource.setDriver(DRIVER);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(mapperLocations));
        factoryBean.setTypeAliasesPackage(aliasesLocations);
        return factoryBean.getObject();
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}