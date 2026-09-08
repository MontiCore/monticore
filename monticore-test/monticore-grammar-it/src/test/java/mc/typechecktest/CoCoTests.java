/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typechecktest._ast.ASTTCCompilationUnit;
import mc.typechecktest._cocos.TypeCheckTestCoCoChecker;
import mc.typechecktest._cocos.VariableAssignmentCorrectType;
import mc.typechecktest._cocos.VariableDeclarationIsCorrect;
import mc.typechecktest._symboltable.TypeCheckTestPhasedSTC;
import mc.typechecktest.types3.TypeCheckTestTypeCheck3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoCoTests {

  private ASTTCCompilationUnit check;
  private ASTTCCompilationUnit bar;
  private ASTTCCompilationUnit inheritanceBar;
  private ASTTCCompilationUnit staticAbstractOOMethods;
  private ASTTCCompilationUnit staticAbstractOOFields;
  private ASTTCCompilationUnit wrongAssignment;
  private ASTTCCompilationUnit complicatedWrongAssignment;
  private ASTTCCompilationUnit complicatedCorrectAssignment;
  private ASTTCCompilationUnit inheritedCannotUseStaticFromSuper;

  @BeforeAll
  public static void init(){
    TypeCheckTestMill.init();
  }

  @BeforeEach
  public void setup() throws IOException {
    LogStub.init();
    Log.enableFailQuick(false);

    TypeCheckTestTypeCheck3.init();
    TypeCheckTestMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
    TypeSymbol string = TypeCheckTestMill
        .typeSymbolBuilder()
        .setName("String")
        .setEnclosingScope(TypeCheckTestMill.globalScope())
        .build();
    string.setSpannedScope(TypeCheckTestMill.scope());
    TypeCheckTestMill.globalScope().add(string);
    TypeCheckTestMill.globalScope().setSymbolPath(new MCPath(Paths.get("src/test/resources")));

    var parser = TypeCheckTestMill.parser();
    Optional<ASTTCCompilationUnit> bar = parser
        .parse("src/test/resources/mc/typescalculator/valid/Bar.tc");
    assertTrue(bar.isPresent());
    TypeCheckTestPhasedSTC stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(bar.get());
    this.bar = bar.get();

    Optional<ASTTCCompilationUnit> inheritanceBar = parser
        .parse("src/test/resources/mc/typescalculator/valid/InheritanceBar.tc");
    assertTrue(inheritanceBar.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(inheritanceBar.get());
    this.inheritanceBar = inheritanceBar.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOMethods = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/StaticAbstractOOMethods.tc");
    assertTrue(staticAbstractOOMethods.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOMethods.get());
    this.staticAbstractOOMethods = staticAbstractOOMethods.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOFields = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/StaticAbstractOOFields.tc");
    assertTrue(staticAbstractOOFields.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOFields.get());
    this.staticAbstractOOFields = staticAbstractOOFields.get();

    Optional<ASTTCCompilationUnit> check = parser
        .parse("src/test/resources/mc/typescalculator/valid/Check.tc");
    assertTrue(check.isPresent());
    this.check = check.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(check.get());

    Optional<ASTTCCompilationUnit> wrongAssignment = parser
        .parse("src/test/resources/mc/typescalculator/invalid/WrongAssignment.tc");
    assertTrue(wrongAssignment.isPresent());
    this.wrongAssignment = wrongAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(wrongAssignment.get());

    Optional<ASTTCCompilationUnit> complicatedWrongAssignment = parser
        .parse("src/test/resources/mc/typescalculator/invalid/ComplicatedWrongAssignment.tc");
    assertTrue(complicatedWrongAssignment.isPresent());
    this.complicatedWrongAssignment = complicatedWrongAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(complicatedWrongAssignment.get());

    Optional<ASTTCCompilationUnit> complicatedCorrectAssignment = parser
        .parse("src/test/resources/mc/typescalculator/valid/ComplicatedCorrectAssignment.tc");
    assertTrue(complicatedCorrectAssignment.isPresent());
    this.complicatedCorrectAssignment = complicatedCorrectAssignment.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(complicatedCorrectAssignment.get());

    Optional<ASTTCCompilationUnit> inheritedCannotUseStaticFromSuper = parser
        .parse("src/test/resources/mc/typescalculator/inbetween/InheritedCannotUseStaticFromSuper.tc");
    assertTrue(inheritedCannotUseStaticFromSuper.isPresent());
    this.inheritedCannotUseStaticFromSuper = inheritedCannotUseStaticFromSuper.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(inheritedCannotUseStaticFromSuper.get());
  }

  @Test
  public void testValidCheck(){
    testValid(check);
  }

  @Test
  public void testValidBar(){
    testValid(bar);
  }

  @Test
  public void testValidInheritanceBar(){
    testValid(inheritanceBar);
  }

  @Test
  public void testStaticAbstractOOMethods(){
    testInvalid("0xF736F", staticAbstractOOMethods);
  }

  @Test
  public void testStaticAbstractOOFields(){
    testInvalid("0xF736F", staticAbstractOOFields);
  }

  @Test
  public void testInheritedCannotUseStaticFromSuper(){
    testValid(inheritedCannotUseStaticFromSuper);
  }

  @Test
  public void testComplicatedCorrectAssignment(){
    testValid(complicatedCorrectAssignment);
  }

  @Test
  public void testComplicatedWrongAssignment(){
    testInvalid("0xB0163", complicatedWrongAssignment);
  }

  @Test
  public void testWrongAssignment(){
    testInvalid("0xA0457", wrongAssignment);
  }

  protected void testInvalid(String errorCode, ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getChecker();
    try{
      checker.checkAll(comp);
    }catch(Exception e){
      //do nothing here, just catch the exception for further testing
    }
    assertTrue(Log.getFindingsCount()>=1);
    assertTrue(Log.getFindings().stream().anyMatch(f -> f.getMsg().startsWith(errorCode)));
  }

  protected void testValid(ASTTCCompilationUnit comp){
    Log.clearFindings();
    TypeCheckTestCoCoChecker checker = getChecker();
    checker.checkAll(comp);
    assertEquals(0, Log.getFindingsCount());
  }

  protected TypeCheckTestCoCoChecker getChecker(){
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect());
    checker.addCoCo(new VariableAssignmentCorrectType());
    return checker;
  }

}