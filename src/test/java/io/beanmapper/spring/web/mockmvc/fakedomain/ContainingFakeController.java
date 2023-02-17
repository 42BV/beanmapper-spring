package io.beanmapper.spring.web.mockmvc.fakedomain;

import jakarta.validation.Valid;

import io.beanmapper.BeanMapper;
import io.beanmapper.spring.web.MergedForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/containing-fake")
public class ContainingFakeController {

    @Autowired
    private ContainingFakeService containingFakeService;

    public BeanMapper beanMapper;

    @RequestMapping(method = RequestMethod.POST)
    public ContainingFakeResult create(@MergedForm(value = ContainingFakeForm.class) ContainingFake containingFake) {
        return beanMapper.map(containingFakeService.create(containingFake), ContainingFakeResult.class);
    }

    @RequestMapping(value = "validate", method = RequestMethod.POST)
    public ContainingFakeResult createValid(@Valid @MergedForm(value = ContainingFakeForm.class) ContainingFake containingFake) {
        return beanMapper.map(containingFakeService.create(containingFake), ContainingFakeResult.class);
    }

}
