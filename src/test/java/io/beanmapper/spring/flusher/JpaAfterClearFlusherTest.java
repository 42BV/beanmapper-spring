package io.beanmapper.spring.flusher;

import javax.persistence.EntityManager;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;
import mockit.Mocked;
import mockit.StrictExpectations;

import org.junit.Test;

public class JpaAfterClearFlusherTest {

    @Mocked
    private EntityManager entityManager;

    @Test
    public void persistenceManagerFlushCalled() {
        new StrictExpectations(){{
            entityManager.flush();
        }};
        JpaAfterClearFlusher flusher = new JpaAfterClearFlusher(entityManager);
        CollSource source = new CollSource() {{
            items.add("A");
        }};
        CollTarget target = new CollTarget() {{
            items.add("B");
        }};
        BeanMapper beanMapper = new BeanMapperBuilder()
                .addAfterClearFlusher(flusher)
                .build();
        beanMapper.map(source, target);
    }

}
