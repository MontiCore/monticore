package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class TypeCD2JavaTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  @Before
  public void setUp() {
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator decorator = new TypeCD2JavaDecorator();
    decorator.decorate(cdCompilationUnit);
  }

  @Test
  public void testTypeNamesSplittet() {
    //that names = ["java", "util", "List"] and names != ["java.util.List"]
    for (ASTCDClass astcdClass : cdCompilationUnit.getCDDefinition().getCDClassList()) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        if (astcdAttribute.getType() instanceof ASTSimpleReferenceType) {
          ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) astcdAttribute.getType();
          assertTrue(simpleReferenceType.getNameList().stream().noneMatch((s) -> s.contains(".")));
        }
      }
    }
  }

  @Test
  public void testTypeJavaConformList() {
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getType();
    assertFalse(simpleReferenceType.isEmptyNames());
    assertEquals(3, simpleReferenceType.sizeNames());
    assertEquals("java", simpleReferenceType.getName(0));
    assertEquals("util", simpleReferenceType.getName(1));
    assertEquals("List", simpleReferenceType.getName(2));
  }

  @Test
  public void testTypeJavaConformASTPackage() {
    //test that for AST classes the package is now java conform
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(1).getType();
    assertTrue(simpleReferenceType.isPresentTypeArguments());
    assertEquals(1, simpleReferenceType.getTypeArguments().sizeTypeArguments());
    assertTrue(simpleReferenceType.getTypeArguments().getTypeArgument(0) instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType typeArgument = (ASTSimpleReferenceType) simpleReferenceType.getTypeArguments().getTypeArgument(0);
    assertFalse(typeArgument.isEmptyNames());
    assertEquals(3, typeArgument.sizeNames());
    assertEquals("automaton", typeArgument.getName(0));
    assertEquals("_ast", typeArgument.getName(1));
    assertEquals("ASTState", typeArgument.getName(2));
  }

  @Test
  public void testStringType() {
    //test that types like String are not changed
    assertTrue(cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(0).getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) cdCompilationUnit.getCDDefinition().getCDClass(0).getCDAttribute(0).getType();
    assertFalse(simpleReferenceType.isEmptyNames());
    assertEquals(1, simpleReferenceType.sizeNames());
    assertEquals("String", simpleReferenceType.getName(0));
  }
}
