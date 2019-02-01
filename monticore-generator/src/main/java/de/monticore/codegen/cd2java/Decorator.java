package de.monticore.codegen.cd2java;

@FunctionalInterface
public interface Decorator<I, R> {

  R decorate(final I ast);
}
