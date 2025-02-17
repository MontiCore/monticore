// (c) https://github.com/MontiCore/monticore
package de.monticore.ast;

/**
 * Cannot simply create a new ThreadLocal using CD4Code.
 * Thus, create this helper...
 */
public class ThreadLocalCreator {
  public static <T> ThreadLocal<T> createThreadLocal() {
    return new ThreadLocal<T>();
  }
}
