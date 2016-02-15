package io.beanmapper.spring.web.mockmvc;

import mockit.Deencapsulation;
import mockit.Delegate;
import org.springframework.data.domain.Persistable;

public class ReturnPassedArgument<T extends Persistable> implements Delegate<T> {

    private Long setIdIfNew;

    public ReturnPassedArgument(Long setIdIfNew) {
        this.setIdIfNew = setIdIfNew;
    }

    public ReturnPassedArgument() {}

    T delegate(T persistable) {
        if (setIdIfNew != null && persistable.isNew()) {
            Deencapsulation.setField(persistable, "id", 42L);
        }
        return persistable;
    }

}
