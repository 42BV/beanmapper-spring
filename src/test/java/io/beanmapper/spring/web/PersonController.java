/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.spring.web;

import io.beanmapper.spring.Lazy;
import io.beanmapper.spring.model.Person;
import io.beanmapper.spring.model.PersonForm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/person")
public class PersonController {
    
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Person create(@MergedForm(PersonForm.class) Person person) {
        return person;
    }
    
    @RequestMapping(value = "/query-param", method = RequestMethod.PUT)
    @ResponseBody
    public Person updateMergeIdInQueryParam(@MergedForm(value = PersonForm.class, mergeId = "id") Person person) {
        return person;
    }
    
    @RequestMapping(value = "/{id}/no-patch", method = RequestMethod.PUT)
    @ResponseBody
    public Person updateNoPatch(@MergedForm(value = PersonForm.class, mergeId = "id") Person person) {
        return person;
    }

    @RequestMapping(value = "/{id}/patch", method = RequestMethod.PUT)
    @ResponseBody
    public Person updatePatch(@MergedForm(value = PersonForm.class, patch = true, mergeId = "id") Person person) {
        return person;
    }

    @RequestMapping(value = "/{id}/lazy", method = RequestMethod.PUT)
    @ResponseBody
    public Person updateLazy(@MergedForm(value = PersonForm.class, mergeId = "id") Lazy<Person> person) {
        return person.get();
    }

    @RequestMapping(value = "/{id}/pair", method = RequestMethod.PUT)
    @ResponseBody
    public MergePair<Person> updateReturnPair(@MergedForm(value = PersonForm.class, mergeId = "id", mergePairClass = Person.class) MergePair personPair) {
        return personPair;
    }

}
