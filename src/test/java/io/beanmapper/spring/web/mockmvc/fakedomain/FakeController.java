package io.beanmapper.spring.web.mockmvc.fakedomain;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.MergedForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fake")
public class FakeController {

    @Autowired
    private FakeService fakeService;

    @Autowired
    private BeanMapper beanMapper;

    @RequestMapping(value = "/{fake}", method = RequestMethod.GET)
    public FakeResult read(@PathVariable Fake fake) {
        return beanMapper.map(fakeService.read(fake), FakeResult.class);
    }

    @RequestMapping(method = RequestMethod.POST)
    public FakeResult create(@MergedForm(value = FakeForm.class) Fake fake) {
        return beanMapper.map(fakeService.create(fake), FakeResult.class);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public FakeResult update(@MergedForm(mergeId = "id", value = FakeForm.class) Fake fake) {
        return beanMapper.map(fakeService.update(fake), FakeResult.class);
    }

    @RequestMapping(value = "/{fake}", method = RequestMethod.DELETE)
    public FakeResult delete(@PathVariable Fake fake) {
        return beanMapper.map(fakeService.delete(fake), FakeResult.class);
    }

}
