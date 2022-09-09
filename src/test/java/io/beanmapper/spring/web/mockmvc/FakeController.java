package io.beanmapper.spring.web.mockmvc;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.MergedForm;
import io.beanmapper.spring.web.mockmvc.fakedomain.Fake;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeForm;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeResult;
import io.beanmapper.spring.web.mockmvc.fakedomain.FakeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fake")
public class FakeController {

    @Autowired
    private FakeService fakeService;

    // Made public due to issues
    public BeanMapper beanMapper;

    @GetMapping(value = "/{fake}")
    public FakeResult read(@PathVariable Fake fake) {
        return beanMapper.map(fakeService.read(fake), FakeResult.class);
    }

    @PostMapping
    public FakeResult create(@MergedForm(value = FakeForm.class) Fake fake) {
        return beanMapper.map(fakeService.create(fake), FakeResult.class);
    }

    @PutMapping(value = "/{id}")
    public FakeResult update(@MergedForm(mergeId = "id", value = FakeForm.class) Fake fake) {
        return beanMapper.map(fakeService.update(fake), FakeResult.class);
    }

    @DeleteMapping(value = "/{fake}")
    public FakeResult delete(@PathVariable Fake fake) {
        return beanMapper.map(fakeService.delete(fake), FakeResult.class);
    }

}
