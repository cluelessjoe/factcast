package org.factcast.client.cache;

import org.factcast.core.FactCast;
import org.factcast.core.store.FactStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class that provides a CachingFactCast by wrapping a
 * FactCast instance.
 * 
 * @author <uwe.schaefer@mercateo.com>
 *
 */
@Configuration
public class CachingFactCastConfiguration {

    @Bean
    public CachingFactCast cachingFactCast(FactCast fc, CachingFactLookup fl) {
        return new CachingFactCast(fc, fl);
    }

    @Bean
    public CachingFactLookup cachingFactLookup(FactStore store) {
        return new CachingFactLookup(store);
    }
}
