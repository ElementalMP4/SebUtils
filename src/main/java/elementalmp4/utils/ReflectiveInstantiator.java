package main.java.elementalmp4.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReflectiveInstantiator<T> {

    private final List<T> instances = new ArrayList<>();

    public ReflectiveInstantiator(Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections("main.java.elementalmp4");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
        for (Class<?> clazz : classes) {
            try {
                Constructor<?> constructor = clazz.getConstructor();
                T instance = (T) constructor.newInstance();
                instances.add(instance);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<T> getInstances() {
        return instances;
    }

}
