/* (c) https://github.com/MontiCore/monticore */
package de.monticore.runtime.junit;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Performs the tests within the scope of a language.
 * This means a freshly initialized mill for each test
 * and a Log without Findings.
 * After ech test, the language's mill is reset.
 * No Findings MUST be present after a test (see {@link MCAssertions})
 * Also includes the hooks of the {@link AbstractMCTest}
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MCLanguageTestExtension.class)
public @interface TestWithMCLanguage {
  /**
   * The Mill of a language,
   * such as {@code @TestWithMCLanguage(MyDSLMill.class)}
   * @return Class of a language's mill
   */
  Class<?> value();
}
