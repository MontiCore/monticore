/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.base.Preconditions;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.stream.Stream;

/**
 * Another clean-up for JDK-8078641:
 * Groovy uses MethodHandle.asTypeCache which keeps a reference to the
 * "Script1.groovy" (interpreted execution script) alive,
 * which in term retains a classloader reference.
 * => The classloader of the groovy script will NOT be unloaded as long as
 * the MethodHandleImpl cache is present.
 * If the classloader of the script is an (Isolated)URLClassLoader,
 * this means all loaded classes of said classloader remain loaded.
 * --
 * This class holds the actual classloader as a reference and delegates
 * all calls to it.
 * When {@link #close()} is called, only the reference to the delegate is
 * cleared, the close call is not delegated
 *
 * @deprecated Included with se-commons-groovy 7.8.0+
 */
@Deprecated
public class DelegatingClassLoader extends ClassLoader implements Closeable {
  protected final WeakReference<ClassLoader> delegate;
  // The actual method is not stored as a reference as it otherwise might be unloaded prematurely
  protected Method delegateLoadClass;

  public DelegatingClassLoader(ClassLoader delegate) {
    this.delegate = new WeakReference<>(delegate);
    try {
      // The loadClass method is protected, but used by Groovy
      this.delegateLoadClass = ClassLoader.class.getDeclaredMethod("loadClass", String.class, boolean.class);
      this.delegateLoadClass.setAccessible(true);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    return Preconditions.checkNotNull(delegate.get()).loadClass(name);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // This method is unfortunately protected, but used by the Groovy compiler
    // Only classes exposed to groovy are loaded using this method (<1000 calls)
    try {
      return (Class<?>) this.delegateLoadClass.invoke(delegate.get(), name, resolve);
    } catch (InvocationTargetException | IllegalAccessException e) {
      if (e.getCause() instanceof ClassNotFoundException) {
        // rethrow ClassNotFoundExceptions
        throw (ClassNotFoundException) e.getCause();
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public URL getResource(String name) {
    return Preconditions.checkNotNull(delegate.get()).getResource(name);
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    return Preconditions.checkNotNull(delegate.get()).getResources(name);
  }

  @Override
  public Stream<URL> resources(String name) {
    return Preconditions.checkNotNull(delegate.get()).resources(name);
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    return Preconditions.checkNotNull(delegate.get()).getResourceAsStream(name);
  }

  @Override
  public void close() throws IOException {
    // Do not delegate the close call!
    // only clean up the reference to the delegate
    this.delegate.clear();
    this.delegateLoadClass = null;
  }
}
