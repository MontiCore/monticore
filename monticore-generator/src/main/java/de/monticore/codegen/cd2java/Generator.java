package de.monticore.codegen.cd2java;

@FunctionalInterface
public interface Generator<I, R> {

  R generate(final I ast);
}
