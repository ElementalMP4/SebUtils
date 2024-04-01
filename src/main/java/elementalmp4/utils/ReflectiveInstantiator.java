package main.java.elementalmp4.utils;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReflectiveInstantiator<T> {

    private final List<T> instances;
    private final Reflections reflections;

    public ReflectiveInstantiator(String pkg) {
        this.reflections = new Reflections(pkg);
        this.instances = new ArrayList<>();
    }

    public ReflectiveInstantiator<T> findAnnotatedClasses(Class<? extends Annotation> annotation, Class<T> typeToken) {
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(annotation);
        for (Class<?> clazz : classes) {
            try {
                Constructor<?> constructor = clazz.getConstructor();
                T instance = typeToken.cast(constructor.newInstance());
                instances.add(instance);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    public List<T> getInstances() {
        return instances;
    }

}
