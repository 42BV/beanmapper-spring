# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Unreleased

## [5.0.4] - 2024-04-11

### Updated

- Updated BeanMapper (4.1.5)


## [5.0.3] - 2024-04-04

### Updated

- Updated BeanMapper (4.1.4)

## [5.0.2] - 2024-03-27

### Updated

- Updated BeanMapper (4.1.3)
- Updated jackson-core (2.17.0)
- Updated jackson-databind (2.17.0)

## [5.0.0] - 2023-02-22

### Updated

- Updated BeanMapper (4.1.2)

## [4.1.0] - 2022-11-10
### Updated 
- Updated BeanMapper (4.1.0)

## [4.0.1] - 2022-09-22
### Updated
- Updated beanmapper(4.0.1)
### Fixed
- Issue [138](https://github.com/42BV/beanmapper/issues/138), **Beanmapper wrongfully assumes a HibernateProxy is there when you use an inner class**; Properly checking whether nested/inner class implements HibernateProxy fixes the problem where the HibernateAwareBeanUnproxy would attempt to return a non-existent interface.

## [4.0.0] - 2022-09-15
### Changed
- Updated to Java 17
- Updated dependencies to their most recent, stable versions
- Rewrote tests to use JUnit 5, and Mockito, rather than JUnit 4 and JMockit

## [3.1.0] - 2020-12-16
### Owasp vulnerability dependency upgrade
- Added owasp dependency check maven plugin to be able to check dependencies
- Upgraded guava- and jackson deps to resolve owasp findings
- Fixed maven surefire plugin setting to be able to run JMockit tests with Junit4
- Removed travis build config; replaced with github actions CI

## [3.0.0] - 2018-07-17
### Version upgrade
- Full upgrade to **Spring 5 / Spring Boot 2**. This version of BeanMapper Spring is no longer usable with lower versions.
- Issue [#32](https://github.com/42BV/beanmapper-spring/issues/32); **MergePair.beforeMerge must be untouched, not BeanMapped** when a MergePair is used, the ```beforeMerge``` is now retrieved and immediately detached before the target is fetched. Take note that this means that the original's proxies can no longer be resolved. This restriction does not apply to the target.

## [2.4.1] - 2018-06-20
### Version upgrade
- Adoption of underlying BeanMapper library v2.4.1

## [2.4.0] - 2018-03-28
### Added
- Added an AbstractSpringSecuredCheck, which contains the logic for fetching the security Principal and checking against the roles. This class can be used to implement LogicSecuredCheck classes. See also [#107](https://github.com/42BV/beanmapper/issues/107)
- Issue [#29](https://github.com/42BV/beanmapper-spring/issues/29); **Spring Security based implementation for @RoleSecuredCheck** Added a Spring Security implementation for the @BeanRoleSecured handler. It will compare the Principal's authorities against the required authorities. At least one match will suffice to grant access.

## [2.2.0] - 2017-11-01
### Added
- **BREAKING CHANGE** Issue [#26](https://github.com/42BV/beanmapper-spring/issues/26), **Validations for the entity are not run for Lazy targets**; the @MergedForm maps to a Lazy object, it delays the process of mapping until the time that get() is called on the Lazy object. At that time, it should work exactly the same way as a regular validation run. Forms are always validated right away. However, the final objects are only validated when direct mapping takes place. The process has been refactored so that validation on the final target is included in the delayed mapping process. Note that Lazy.get() must now deal with Exception. The pro of this approach is that it hooks onto the regular binding result handler. 
- Issue [#28](https://github.com/42BV/beanmapper-spring/issues/28), **when Lazy is used, set flushEnabled=true**; make sure to enable the global flushing after calling clear() when Lazy is used. In this case, the EntityManager is the most likely to be in a transaction context. This change works with [#90](https://github.com/42BV/beanmapper/issues/90), where the default for the BeanCollection is set to true. This means flushing will be called by default for BeanCollections operating in a Lazy "context".

## [2.1.0] - 2017-10-25
### Added
- Issue [#24](https://github.com/42BV/beanmapper-spring/issues/24), **JpaAfterClearFlusher**; a class is offered than can be used to register an EntityManager so that it can be called after BeanMapper has called clear() on a collection. This might come in handy to force the ORM to flush. One use case is to force the inversion of executed SQL statements from insert/delete to delete/insert. 

## [2.0.2] - 2017-10-18
### Fixed
- Issue [#21](https://github.com/42BV/beanmapper-spring/issues/21), **Multipart part name not used correctly**; the multipart part name was not handled correctly. Spring's multipart handler is now passed a MethodParameter which has the correct part name and also disable the parameter name explorer, so it is forced to used the overwritten part name.

## [2.0.1] - 2017-10-18
### Fixed
- Issue [#19](https://github.com/42BV/beanmapper-spring/issues/19), **Spring handles multipart forms differently**; v4.1.6 deal with the multipart form by getting the parameterType as the target class. Later Spring versions (at least from 4.3.10.RELEASE onwards), do this by checking the genericParameterType. The solution is to check for the genericParameterType. If it exists, it is overwritten for the multipart form resolution attempt.

## [2.0.0] - 2017-10-12
### Fixed
- Issue [#68](https://github.com/42BV/beanmapper/issues/68), **Change to PageableMapper**; the BeanMapper interface has changed, resulting in an internal change to PageableMapper. Check [BeanMapper changelog](https://github.com/42BV/beanmapper/blob/master/CHANGELOG.md) for more information on the changes.

## [1.0.0] - 2017-10-04
### Added
- Issue [#15](https://github.com/42BV/beanmapper-spring/issues/15), **Retain both pre-merged and merged entities**; on using the MergedForm annotation, when the class MergePair is set as a result and when the annotation field mergePairClass is set, both the original and the target class will be preserved. This allows the developer to compare the before and after situation and react accordingly. One note that must be understood; the original is not the real original (as in; the exact instance found in the database), but is mapped by BeanMapper from the fetched entity to a new, similar entity. The reason for this is that the original instance is cached by Hibernate and will be reused by the target. It cannot be preserved.
- Issue [#16](https://github.com/42BV/beanmapper-spring/issues/16), **@MergedForm must be able to read from RequestPart**; MergedForm can now read from multipart request bodies as well. When the annotation field multipart is set, the value is used to determine which part the content must be read from. Spring's RequestPartMethodArgumentResolver is reused for the process of actually reading the multipart form.
