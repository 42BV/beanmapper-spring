package io.beanmapper.spring.web.dynamic;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

public class DynamicBeanMapper {

    private static Map<String, Map<String, GeneratedClass>> CACHE;
    private static Integer GENERATED_CLASS_PREFIX = 0;
    
    private final BeanMapper beanMapper;
    
    private final ClassPool classPool;
    
    static {
        CACHE = new TreeMap<String, Map<String, GeneratedClass>>();
    }

    public DynamicBeanMapper(BeanMapper beanMapper) {
        this.beanMapper = beanMapper;
        this.classPool = ClassPool.getDefault();
    }

    public <S> Object map(S source, Class<?> targetClass, List<String> includeFields) throws Exception {
        if (includeFields == null || includeFields.size() == 0) {
            return beanMapper.map(source, targetClass);
        }
        return beanMapper.map(source, getOrCreateGeneratedClass(targetClass, includeFields).generatedClass);
    }

    public <S> Collection<?> map(Collection<S> sourceItems, Class<?> targetClass, List<String> includeFields) throws Exception {
        if (includeFields == null || includeFields.size() == 0) {
            return beanMapper.map(sourceItems, targetClass);
        }
        return beanMapper.map(sourceItems, getOrCreateGeneratedClass(targetClass, includeFields).generatedClass);
    }

    private GeneratedClass getOrCreateGeneratedClass(Class<?> targetClass, List<String> includeFields) throws Exception {
        Node displayFields = Node.createTree(includeFields);
        return getOrCreateGeneratedClass(targetClass.getName(), displayFields);
    }

    protected GeneratedClass getOrCreateGeneratedClass(String classInPackage, Node displayFields) throws Exception {
        Map<String, GeneratedClass> generatedClassesForClass = CACHE.get(classInPackage);
        if (generatedClassesForClass == null) {
            generatedClassesForClass = new TreeMap<String, GeneratedClass>();
            CACHE.put(classInPackage, generatedClassesForClass);
        }
        GeneratedClass generatedClass = generatedClassesForClass.get(displayFields.getKey());
        if (generatedClass == null) {
            CtClass dynamicClass = classPool.get(classInPackage);
            dynamicClass.setName(classInPackage + "Dyn" + ++GENERATED_CLASS_PREFIX);
            processClassTree(dynamicClass, displayFields);
            generatedClass = new GeneratedClass(dynamicClass);
            generatedClassesForClass.put(displayFields.getKey(), generatedClass);
        }
        return generatedClass;
    }

    private void processClassTree(CtClass dynClass, Node node) throws Exception {
        for (CtField field : dynClass.getDeclaredFields()) {
            if (node.getFields().contains(field.getName())) {
                Node fieldNode = node.getNode(field.getName());
                // Apply include filter, aka generate new dynamic class
                if (fieldNode.hasNodes() && beanMapper.isMappable(field.getType().getPackageName())) {
                    GeneratedClass nestedClass = getOrCreateGeneratedClass(field.getType().getName(), fieldNode);
                    field.setType(nestedClass.ctClass);
                } else if (field.hasAnnotation(BeanCollection.class)){
                    BeanCollection beanCollection = (BeanCollection) field.getAnnotation(BeanCollection.class);
                    Class<?> elementType = beanCollection.elementType();
                    GeneratedClass elementClass = getOrCreateGeneratedClass(elementType.getName(), fieldNode);

                    elementClass.ctClass.defrost();
                    ConstPool constPool = elementClass.ctClass.getClassFile().getConstPool();
                    AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    Annotation annot = new Annotation(BeanCollection.class.getName(), constPool);
                    annot.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
                    attr.addAnnotation(annot);
                    field.getFieldInfo().addAttribute(attr);
                    elementClass.ctClass.freeze();
                }
            } else {
                if (node.hasNodes()) {
                    // Only remove fields if there are any fields at all to remove, else assume full showing
                    dynClass.removeField(field);
                }
            }
        }
    }

}
