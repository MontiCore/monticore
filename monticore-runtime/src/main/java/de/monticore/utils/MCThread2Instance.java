// (c) https://github.com/MontiCore/monticore
package de.monticore.utils;

import de.se_rwth.commons.logging.Log;

import java.util.function.Supplier;

/**
 * A Map which maps every thread to a different instance.
 * <p>
 * This class is to be used for static delegates,
 * to allow usage of MontiCore using multiple threads.
 * <p>
 * Accessing of the instance of another thread is deliberately not possible.
 */
public class MCThread2Instance<T> {

  protected static final String LOG_NAME = MCThread2Instance.class.getName();

  // As of writing, this cannot do anything that ThreadLocal cannot do
  // without a simple wrapper;
  // Thus, currently this simply wraps ThreadLocal.
  protected ThreadLocal<T> threadLocal;

  public MCThread2Instance() {
    this.threadLocal = new ThreadLocal<T>();
  }

  public MCThread2Instance(T initialValue) {
    this(() -> initialValue);
  }

  public MCThread2Instance(Supplier<T> initialValueSupplier) {
    this.threadLocal = ThreadLocal.withInitial(initialValueSupplier);
  }

  /**
   * @param newInstance the new instance specific to this thread.
   */
  public void set(T newInstance) {
    Log.errorIfNull(newInstance);
    getThreadLocal().set(newInstance);
  }

  /**
   * @return the instance specific to this thread.
   */
  public T get() {
    T instance = _internal_get();
    if (instance == null) {
      Log.error(
          "0x72100 internal error: "
              + "Tried to get the thread-specific instance out of "
              + MCThread2Instance.class.getName()
              + ", but no thread-specific instance has been set yet."
              + System.lineSeparator() + "Thread: " + Thread.currentThread()
      );
    }
    return instance;
  }

  // internal

  /**
   * @return thread local instance (can be null)
   */
  protected T _internal_get() {
    return getThreadLocal().get();
  }

  protected ThreadLocal<T> getThreadLocal() {
    return this.threadLocal;
  }

}
