/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import com.google.common.collect.Lists;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill;
import de.monticore.statements.testmcvardeclarationstatements._ast.ASTRootVarDeclaration;
import de.monticore.statements.testmcvardeclarationstatements._cocos.TestMCVarDeclarationStatementsCoCoChecker;
import de.monticore.statements.testmcvardeclarationstatements._visitor.TestMCVarDeclarationStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.statements.testmcvardeclarationstatements.TestMCVarDeclarationStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VarDeclarationInitializationHasCorrectTypeTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCVarDeclarationStatementsMill.reset();
    TestMCVarDeclarationStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
    addMyTypeToGlobalScope();
    addStringToGlobalScope();
  }

  protected static void addMyTypeToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("MyType")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected static void addStringToGlobalScope() {
    OOTypeSymbol type = TestMCVarDeclarationStatementsMill.oOTypeSymbolBuilder()
      .setName("String")
      .setSpannedScope(TestMCVarDeclarationStatementsMill.scope())
      .build();
    TestMCVarDeclarationStatementsMill.globalScope().add(type);
    TestMCVarDeclarationStatementsMill.globalScope().addSubScope(type.getSpannedScope());
  }

  protected ASTRootVarDeclaration parseAndBuildAST(String decl) throws IOException {
    ASTRootVarDeclaration ast = parser().parse_StringRootVarDeclaration(decl).orElseThrow();
    TestMCVarDeclarationStatementsMill.scopesGenitorDelegator().createFromAST(ast);
    TestMCVarDeclarationStatementsTraverser completerTraverser = TestMCVarDeclarationStatementsMill.traverser();
    completerTraverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    ast.accept(completerTraverser);
    // We must manually set a name for the ArtifactScope. Else we get an exception.
    ast.getEnclosingScope().setName("Foo");
    return ast;
  }

  @Test
  public void testValidMultiVarDeclaration() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

    ASTRootVarDeclaration ast = parseAndBuildAST("int a = 10, b, c = -12;");

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @Test
  public void testInvalidMultiVarDeclaration() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

    ASTRootVarDeclaration ast = parseAndBuildAST("int a = \"oh no\", b = 10, c, d = \"no no no\";");

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(VarDeclarationInitializationHasCorrectType.ERROR_CODE,
      VarDeclarationInitializationHasCorrectType.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }

  @Test
  public void testInvalidMultiVarDeclarationWithTypeReference() throws IOException {
    // Given
    TestMCVarDeclarationStatementsCoCoChecker checker = new TestMCVarDeclarationStatementsCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());

    ASTRootVarDeclaration ast = parseAndBuildAST("int a = 3, b, c = MyType, d = \"no no no\";");

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of("0xFD118", VarDeclarationInitializationHasCorrectType.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }
}
