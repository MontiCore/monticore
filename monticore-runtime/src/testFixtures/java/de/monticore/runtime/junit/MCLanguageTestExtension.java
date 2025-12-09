/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class MCLanguageTestExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    if (!(AbstractMCTest.class.isAssignableFrom(extensionContext.getRequiredTestClass()))) {
      // Call AbstractMCTest beforeEach hook
      AbstractMCTest.defaultInitAbstract();
    }

    getAnnotation(extensionContext)
            .ifPresent(a -> invokeStaticMethod(a.value(), "init"));
  }


  @Override
  public void afterEach(ExtensionContext extensionContext) {
    getAnnotation(extensionContext)
            .ifPresent(a -> invokeStaticMethod(a.value(), "reset"));

    if (!(AbstractMCTest.class.isAssignableFrom(extensionContext.getRequiredTestClass()))) {
      // Call AbstractMCTest afterEach hook
      AbstractMCTest.defaultCheckLogAfterTest();
    }
  }


  /**
   * Fetch the @{@link TestWithMCLanguage} annotation,
   * containing a reference to the relevant Mill.
   *
   * @param extensionContext the test context
   * @return optional of the annotation
   */
  protected Optional<TestWithMCLanguage> getAnnotation(ExtensionContext extensionContext) {
    return Optional.ofNullable(extensionContext.getRequiredTestClass().getAnnotation(TestWithMCLanguage.class));
  }

  /**
   * invoke a public, static method (init/reset) of a mill
   *
   * @param millClazz class of the mill
   * @param method    method name
   */
  protected void invokeStaticMethod(Class<?> millClazz, String method) {
    if (!millClazz.getName().endsWith("Mill"))
      throw new IllegalArgumentException("@TestWithMCLanguage class " + millClazz.getSimpleName() + " is not a mill!");
    try {
      // Because Mill#init and Mill#reset are static methods
      // we unfortunately have to use Class#invoke to invoke init/reset
      millClazz.getMethod(method).invoke(null);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to invoke mill method", e);
    }
  }
}
