package org.factcast.store.pgsql.internal;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.google.common.base.Supplier;
import com.impossibl.postgres.api.jdbc.PGConnection;

import lombok.Getter;
import lombok.SneakyThrows;

/**
 * Supplies a PGConnection directly from an un-pooled DataSource
 * 
 * @author uwe.schaefer@mercateo.com
 *
 */
class EnvironmentPGConnectionSupplier implements Supplier<PGConnection>, InitializingBean {
    @Getter
    String url;

    DriverManagerDataSource dataSource;

    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    @SneakyThrows
    public PGConnection get() {
        return (PGConnection) dataSource.getConnection();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
    }

}
