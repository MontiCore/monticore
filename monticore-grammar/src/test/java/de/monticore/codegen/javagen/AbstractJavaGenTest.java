// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.CodeGenerator;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsUtil;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements.codegen.javagen.ExpressionsAndStatementsJavaGenerator;
import de.se_rwth.commons.logging.LogStub;
import jdk.jshell.JShell;
import jdk.jshell.Snippet;
import jdk.jshell.SnippetEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractJavaGenTest extends AbstractMCTest {

  // enable for experimenting:
  // it will print the generated code before evaluation
  protected static final boolean printGeneratedCode = false;

  protected JShell jshell;

  CodeGenerator generator;

  @BeforeEach
  void beforeEach() {
    LogStub.initPlusLog();
    ExpressionsAndStatementsUtil.init();
    generator = new ExpressionsAndStatementsJavaGenerator();
    jshell = JShell.create();
    String[] classpaths = System.getProperty("java.class.path")
        .split(File.pathSeparator);
    for (String classpath : classpaths) {
      getJShell().addToClasspath(classpath);
    }
  }

  @AfterEach
  void cleanUp() {
    jshell.close();
    jshell = null;
  }

  protected void addClassPathEntry(Class<?> clazz) {
    try {
      CodeSource codeSource = clazz
          .getProtectionDomain()
          .getCodeSource();
      if (codeSource == null) {
        return;
      }
      Path classPath = Paths.get(codeSource.getLocation().toURI());
      BasicSymbolsMill.globalScope().getSymbolPath().addEntry(classPath);
    }
    catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  protected JShell getJShell() {
    return jshell;
  }

  protected void checkValue(String behaviorModelStr, Object expectedValue) {
    // setup
    ASTBehaviorInput ast = ExpressionsAndStatementsUtil
        .getPreparedAST(behaviorModelStr);
    String javaReturnType = ast.isPresentExpression() ?
        convert2JavaType(normalize(typeOf(ast.getExpression()))) :
        "void";
    assertNoFindings();
    String javaStatementsStr = generateCode(ast);
    assertNoFindings();
    String javaMethodStr =
        javaReturnType
            + " systemUnderTest() {" + System.lineSeparator()
            + javaStatementsStr
            + "}";
    if (printGeneratedCode) {
      System.out.println("***** Generated Code *****");
      System.out.println(javaMethodStr);
      System.out.println("**************************");
    }

    // method definition
    List<SnippetEvent> events = getJShell().eval(javaMethodStr);
    assertEquals(1, events.size(),
        "More/Less than exactly one events where triggered by the evaluation."
    );
    SnippetEvent event = events.get(0);
    assertEquals(Snippet.Status.VALID, event.status(),
        "JShell failed:" + System.lineSeparator()
            + printDiags(event.snippet()) + System.lineSeparator()
            + "Model:" + System.lineSeparator() + javaMethodStr
    );

    // method call
    events = getJShell().eval("systemUnderTest()");
    assertEquals(1, events.size(),
        "More/Less than exactly one events where triggered by the evaluation."
    );
    event = events.getFirst();
    assertEquals(Snippet.Status.VALID, event.status(),
        "JShell failed:" + System.lineSeparator()
            + printDiags(event.snippet()) + System.lineSeparator()
            + "Model:" + System.lineSeparator() + javaMethodStr
    );
    String value = event.value();

    // edge case to support Strings
    if (expectedValue instanceof String expectedStr) {
      expectedValue = "\"" + expectedStr + "\"";
    }
    assertEquals(expectedValue.toString(), value,
        "The evaluated Java code does not match the expected output."
            + " Model:" + System.lineSeparator() + javaMethodStr
    );
  }

  /**
   * produces Java code given a model.
   *
   * @param node the node to generate code from
   * @return the generated Java code
   */
  protected String generateCode(ASTNode node) {
    return generator.generateCode(node);
  }

  protected String printDiags(Snippet snippet) {
    return getJShell().diagnostics(snippet)
        .map(diag ->
            "Diag<" + diag.getStartPosition()
                + ", " + diag.getEndPosition() + ">: "
                + diag.getMessage(null)
        )
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
