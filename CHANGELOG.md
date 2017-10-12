# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0] - 2017-10-12
### Fixed
- Issue [#68](https://github.com/42BV/beanmapper/issues/68), **Change to PageableMapper**; the BeanMapper interface has changed, resulting in an internal change to PageableMapper. Check [BeanMapper changelog](https://github.com/42BV/beanmapper/blob/master/CHANGELOG.md) for more information on the changes.

## [1.0.0] - 2017-10-04
### Added
- Issue [#15](https://github.com/42BV/beanmapper-spring/issues/15), **Retain both pre-merged and merged entities**; on using the MergedForm annotation, when the class MergePair is set as a result and when the annotation field mergePairClass is set, both the original and the target class will be preserved. This allows the developer to compare the before and after situation and react accordingly. One note that must be understood; the original is not the real original (as in; the exact instance found in the database), but is mapped by BeanMapper from the fetched entity to a new, similar entity. The reason for this is that the original instance is cached by Hibernate and will be reused by the target. It cannot be preserved.
- Issue [#16](https://github.com/42BV/beanmapper-spring/issues/16), **@MergedForm must be able to read from RequestPart**; MergedForm can now read from multipart request bodies as well. When the annotation field multipart is set, the value is used to determine which part the content must be read from. Spring's RequestPartMethodArgumentResolver is reused for the process of actually reading the multipart form.