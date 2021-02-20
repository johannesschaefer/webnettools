package io.github.johannesschaefer.webnettools;

import org.reflections.Reflections;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

public class Producer {
    @Singleton
    @Produces
    public Reflections produceReflections() {
        return new Reflections("io.github.johannesschaefer.webnettools");
    }
}