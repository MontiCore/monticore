/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.mc2cd.MC2CD4CodeSymbolTableCompleter;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_STATIC_FINAL;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.rete.ReteConstants.FEATURE_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class ReteFeatureDecoratorTest extends DecoratorTestCase {

  private static final String AST_PACKAGE = "de.monticore.codegen.rete.rete._ast.";

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDClass featureClass;

  private MCTypeFacade mcTypeFacade;

  @BeforeEach
  public void setup() {
    this.mcTypeFacade = MCTypeFacade.getInstance();
    decoratedCompilationUnit = parseAndComplete("Rete");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();

    ReteFeatureDecorator decorator = new ReteFeatureDecorator(glex, new ReteService(decoratedCompilationUnit));
    this.featureClass = decorator.decorate(decoratedCompilationUnit);
  }

  /**
   * parses the class diagram and completes its symbol table like the
   * generator does, so that super types are known
   */
  private ASTCDCompilationUnit parseAndComplete(String name) {
    ASTCDCompilationUnit compilationUnit = this.parse("de", "monticore", "codegen", "rete", name);
    compilationUnit.accept(new MC2CD4CodeSymbolTableCompleter().getTraverser());
    return compilationUnit;
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClass() {
    assertEquals("ReteReteFeatures", featureClass.getName());
    assertDeepEquals(PUBLIC, featureClass.getModifier());
    assertEquals(1, featureClass.getCDConstructorList().size());
    assertDeepEquals(PROTECTED, featureClass.getCDConstructorList().get(0).getModifier());

    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * interfaces come first; attributes repeating an attribute of a super type
   * (AUTOMATON_NAME, STATE_NAME) and attributes of other generic types
   * (TABLE_ENTRIES) get no feature
   */
  @Test
  public void testFeatureNames() {
    List<String> names = featureClass.getCDAttributeList().stream().map(ASTCDAttribute::getName).toList();
    assertEquals(List.of("NAMED_NAME", "AUTOMATON_STATES", "AUTOMATON_COMMENT", "STATE_FINAL",
        "STATE_KIND", "STATE_LABELS", "FINAL_STATE_ACCEPTING", "ALL", "ALL_TRANSITIVE"), names);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFeatureTypes() {
    assertFeatureType("NAMED_NAME", "ASTNamed", "String");
    assertFeatureType("AUTOMATON_STATES", "ASTAutomaton", AST_PACKAGE + "ASTState");
    assertFeatureType("AUTOMATON_COMMENT", "ASTAutomaton", "String");
    assertFeatureType("STATE_FINAL", "ASTState", "Boolean");
    assertFeatureType("STATE_KIND", "ASTState", "Integer");
    assertFeatureType("STATE_LABELS", "ASTState", "String");
    assertFeatureType("FINAL_STATE_ACCEPTING", "ASTFinalState", "Boolean");

    assertTrue(Log.getFindings().isEmpty());
  }

  private void assertFeatureType(String constant, String owner, String valueType) {
    ASTCDAttribute attribute = getAttributeBy(constant, featureClass);
    assertDeepEquals(PUBLIC_STATIC_FINAL, attribute.getModifier());
    assertDeepEquals(mcTypeFacade.createBasicGenericTypeOf(FEATURE_TYPE, AST_PACKAGE + owner, valueType),
        attribute.getMCType());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, featureClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult<?> parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());

    String code = sb.toString();
    assertTrue(code.contains("de.monticore.rete.Feature.of(\"final\", " + AST_PACKAGE + "ASTState.class, Boolean.class)"), code);
    assertTrue(code.contains("de.monticore.rete.Feature.of(\"states\", " + AST_PACKAGE + "ASTAutomaton.class, "
        + AST_PACKAGE + "ASTState.class)"), code);
    assertTrue(code.contains("java.util.List.of(NAMED_NAME, AUTOMATON_STATES, AUTOMATON_COMMENT, STATE_FINAL, "
        + "STATE_KIND, STATE_LABELS, FINAL_STATE_ACCEPTING)"), code);

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDecorateIntoRetePackage() {
    ASTCDCompilationUnit decoratedCD = createEmptyCompilationUnit(decoratedCompilationUnit);
    new ReteFeatureDecorator(glex, new ReteService(decoratedCompilationUnit))
        .decorate(decoratedCompilationUnit, decoratedCD);

    List<ASTCDPackage> packages = decoratedCD.getCDDefinition().getCDPackagesList();
    assertEquals(1, packages.size());
    assertEquals("de.monticore.codegen.rete.rete._rete", packages.get(0).getName());
    assertEquals("ReteReteFeatures", ((ASTCDClass) packages.get(0).getCDElement(0)).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConflictingConstantNames() {
    ASTCDCompilationUnit conflict = parseAndComplete("ReteConflict");
    ASTCDClass conflictClass = new ReteFeatureDecorator(glex, new ReteService(conflict)).decorate(conflict);

    assertEquals(List.of("FOO_BAR_BAZ", "ALL", "ALL_TRANSITIVE"),
        conflictClass.getCDAttributeList().stream().map(ASTCDAttribute::getName).toList());
    assertEquals(1, Log.getFindings().size());
    Finding finding = Log.getFindings().get(0);
    assertTrue(finding.isError());
    assertTrue(finding.getMsg().startsWith("0xA5C10"), finding.getMsg());
  }

  /**
   * acronyms at the start of type names (CDClass) and attribute names derived
   * from nonterminals (cDMembers, mCType) are kept together
   */
  @Test
  public void testConstantNamesWithAcronyms() {
    ReteService reteService = new ReteService(decoratedCompilationUnit);
    assertEquals("STATE", reteService.toConstantName("State"));
    assertEquals("FINAL_STATE", reteService.toConstantName("FinalState"));
    assertEquals("CD_CLASS", reteService.toConstantName("CDClass"));
    assertEquals("CD_MEMBERS", reteService.toConstantName("cDMembers"));
    assertEquals("MC_TYPE", reteService.toConstantName("mCType"));
    assertEquals("MC_QUALIFIED_NAME", reteService.toConstantName("MCQualifiedName"));
    assertEquals("CD4_CODE_ENUM_CONSTANT", reteService.toConstantName("CD4CodeEnumConstant"));

    assertTrue(Log.getFindings().isEmpty());
  }
}
