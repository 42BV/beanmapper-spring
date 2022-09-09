package io.beanmapper.spring.flusher;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;

import io.beanmapper.BeanMapper;
import io.beanmapper.config.BeanMapperBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JpaAfterClearFlusherTest {

    @Mock
    private EntityManager entityManager;

    @Test
    public void persistenceManagerFlushCalled() {
        doNothing().when(entityManager).flush();

        JpaAfterClearFlusher flusher = new JpaAfterClearFlusher(entityManager);
        CollSource source = new CollSource() {{
            items.add("A");
        }};
        CollTarget target = new CollTarget() {{
            items.add("B");
        }};
        BeanMapper beanMapper = new BeanMapperBuilder()
                .setFlushEnabled(true)
                .addAfterClearFlusher(flusher)
                .build();
        beanMapper.map(source, target);

        verify(entityManager).flush();

    }

}
