/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.sourcemap;

import de.monticore.ast.ASTNodeMock;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.generating.templateengine.reporting.commons.DefaultReportEventHandler;
import de.monticore.generating.templateengine.reporting.commons.ReportManager;
import de.monticore.generating.templateengine.reporting.reporter.TemplateSourceMappingReporter;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.sourcemap.DecodedMapping;
import de.monticore.sourcemap.DecodedSource;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SourceMapCompressTest extends AbstractMCTest {

  private final List<DecodedMapping> capturedTemplateMappings = new ArrayList<>();
  private final List<DecodedMapping> capturedAstMappings = new ArrayList<>();

  @BeforeEach
  public void setupReporting() {
    capturedTemplateMappings.clear();
    capturedAstMappings.clear();
    SourceMapCalculator.reset();

    ReportManager.ReportManagerFactory factory = modelName -> {
      ReportManager reports = new ReportManager("target/sourcemap-compress-test");
      reports.addReportEventHandler(new TemplateSourceMappingReporter("target/sourcemap-compress-test", false));
      reports.addReportEventHandler(new DefaultReportEventHandler() {
        @Override
        public void reportTemplateSourceMapping(List<DecodedMapping> mapping) {
          capturedTemplateMappings.addAll(mapping);
        }

        @Override
        public void reportASTSourceMapping(List<DecodedMapping> mapping) {
          capturedAstMappings.addAll(mapping);
        }
      });
      return reports;
    };

    Reporting.init("target/sourcemap-compress-test", factory);
    Reporting.on("CompressTestModel");
  }

  @AfterEach
  public void tearDownReporting() {
    Reporting.off();
    Reporting.clearReportHooks();
    SourceMapCalculator.reset();
  }

  private String runTemplate(String qualifiedTemplateName, SimpleHash data) {
    GeneratorSetup setup = new GeneratorSetup();
    Configuration config = setup.getConfig();

    FreeMarkerTemplateEngine engine = new FreeMarkerTemplateEngine(config);
    Template template = engine.loadTemplate(qualifiedTemplateName);

    StringBuilder buffer = new StringBuilder();
    SourceMapCalculator.pushTemplate(template);
    engine.run(buffer, data, template);
    SourceMapCalculator.popTemplate(template);

    return buffer.toString();
  }

  @Test
  public void testCompressSingleLineMethodHeaderMapping() {
    SimpleHash data = new SimpleHash(new DefaultObjectWrapperBuilder(GeneratorSetup.FREEMARKER_VERSION).build());
    data.put(TemplateController.AST, ASTNodeMock.INSTANCE);
    data.put("modifier", "public");
    data.put("hasTypeParams", false);
    data.put("typeParams", "");
    data.put("returnType", "void");
    data.put("name", "myMethod");
    data.put("params", "int x");
    data.put("hasThrows", false);
    data.put("throwsDecl", "");
    data.put("isAbstract", false);

    String output = runTemplate("de.monticore.generating.templates.CompressMethod", data);
    assertEquals("public void myMethod (int x) {\n    // empty body\n}", output.stripTrailing());

    URL templateUrl = getClass().getClassLoader().getResource("de/monticore/generating/templates/CompressMethod.ftl");
    assertNotNull(templateUrl);
    DecodedSource src = new DecodedSource(templateUrl);

    List<DecodedMapping> expected = List.of(
        new DecodedMapping(0, 0, src, 2, 2, null),   // ${modifier} -> "public"
        new DecodedMapping(0, 7, src, 4, 2, null),   // ${returnType} -> "void"
        new DecodedMapping(0, 12, src, 5, 2, null),  // ${name} -> "myMethod"
        new DecodedMapping(0, 21, src, 5, 9, null),  // "\n  (" -> "("
        new DecodedMapping(0, 22, src, 6, 3, null),  // ${params} -> "int x"
        new DecodedMapping(0, 27, src, 6, 12, null), // ")\n" -> ")"
        new DecodedMapping(0, 28, src, 9, 29, null), // " {\n" -> " {"
        new DecodedMapping(1, 0, src, 10, 0, null),  // "    // empty body\n"
        new DecodedMapping(2, 0, src, 11, 0, null)   // "}\n"
    );

    assertEquals(expected, capturedTemplateMappings);
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testCompressSingleLineAbstractWithTypeParamsAndThrows() {
    SimpleHash data = new SimpleHash(new DefaultObjectWrapperBuilder(GeneratorSetup.FREEMARKER_VERSION).build());
    data.put(TemplateController.AST, ASTNodeMock.INSTANCE);
    data.put("modifier", "public abstract");
    data.put("hasTypeParams", true);
    data.put("typeParams", "<T>");
    data.put("returnType", "T");
    data.put("name", "compute");
    data.put("params", "T input");
    data.put("hasThrows", true);
    data.put("throwsDecl", "throws IOException");
    data.put("isAbstract", true);

    String output = runTemplate("de.monticore.generating.templates.CompressMethod", data);
    String normalizedOutput = output.replace("\r\n", "\n");
    assertEquals("public abstract <T> T compute (T input) throws IOException;", normalizedOutput.stripTrailing());

    URL templateUrl = getClass().getClassLoader().getResource("de/monticore/generating/templates/CompressMethod.ftl");
    assertNotNull(templateUrl);
    DecodedSource src = new DecodedSource(templateUrl);

    List<DecodedMapping> expected = List.of(
        new DecodedMapping(0, 0, src, 2, 2, null),
        new DecodedMapping(0, 16, src, 3, 21, null),
        new DecodedMapping(0, 20, src, 4, 2, null),
        new DecodedMapping(0, 22, src, 5, 2, null),
        new DecodedMapping(0, 30, src, 5, 9, null),
        new DecodedMapping(0, 31, src, 6, 3, null),
        new DecodedMapping(0, 38, src, 6, 12, null),
        new DecodedMapping(0, 40, src, 7, 16, null),
        new DecodedMapping(0, 58, src, 9, 21, null)
    );

    assertEquals(expected, capturedTemplateMappings);
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testCompressMultiLineMapping() {
    SimpleHash data = new SimpleHash(new DefaultObjectWrapperBuilder(GeneratorSetup.FREEMARKER_VERSION).build());
    data.put(TemplateController.AST, ASTNodeMock.INSTANCE);
    data.put("first", "alpha");
    data.put("second", "beta");
    data.put("third", "gamma");

    String output = runTemplate("de.monticore.generating.templates.CompressMultiLine", data);
    String normalizedOutput = output.replace("\r\n", "\n");
    assertEquals("alpha\nbetagamma", normalizedOutput.stripTrailing());

    URL templateUrl = getClass().getClassLoader().getResource("de/monticore/generating/templates/CompressMultiLine.ftl");
    assertNotNull(templateUrl);
    DecodedSource src = new DecodedSource(templateUrl);

    List<DecodedMapping> expected = List.of(
        new DecodedMapping(0, 0, src, 2, 2, null),
        new DecodedMapping(1, 0, src, 3, 2, null),
        new DecodedMapping(1, 4, src, 6, 2, null)
    );

    assertEquals(expected, capturedTemplateMappings);
    MCAssertions.assertNoFindings();
  }
}
