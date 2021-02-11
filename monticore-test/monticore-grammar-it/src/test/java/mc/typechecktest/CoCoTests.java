/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.ITypesCalculator;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest._ast.ASTTCCompilationUnit;
import mc.typechecktest._cocos.TypeCheckTestCoCoChecker;
import mc.typechecktest._cocos.VariableAssignmentCorrectType;
import mc.typechecktest._cocos.VariableDeclarationIsCorrect;
import mc.typechecktest._parser.TypeCheckTestParser;
import mc.typechecktest._symboltable.TypeCheckTestPhasedSTC;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CoCoTests {

  private ASTTCCompilationUnit check;
  private ASTTCCompilationUnit bar;
  private ASTTCCompilationUnit inheritanceBar;
  private ASTTCCompilationUnit staticAbstractOOMethods;
  private ASTTCCompilationUnit staticAbstractOOFields;

  @BeforeClass
  public static void init(){
    Log.init();
    Log.enableFailQuick(false);
    BasicSymbolsMill.initializePrimitives();
  }

  @Before
  public void setup() throws IOException {
    TypeCheckTestMill.globalScope().clear();
    TypeCheckTestParser parser = TypeCheckTestMill.parser();
    TypeCheckTestMill.globalScope().setModelPath(new ModelPath(Paths.get("src/test/resources")));

    Optional<ASTTCCompilationUnit> bar =  parser.parse("src/test/resources/mc/typescalculator/Bar.tc");
    assertTrue(bar.isPresent());
    TypeCheckTestPhasedSTC stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(bar.get());
    this.bar = bar.get();

    Optional<ASTTCCompilationUnit> inheritanceBar = parser.parse("src/test/resources/mc/typescalculator/InheritanceBar.tc");
    assertTrue(inheritanceBar.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(inheritanceBar.get());
    this.inheritanceBar = inheritanceBar.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOMethods = parser.parse("src/test/resources/mc/typescalculator/StaticAbstractOOMethods.tc");
    assertTrue(staticAbstractOOMethods.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOMethods.get());
    this.staticAbstractOOMethods = staticAbstractOOMethods.get();

    Optional<ASTTCCompilationUnit> staticAbstractOOFields = parser.parse("src/test/resources/mc/typescalculator/StaticAbstractOOFields.tc");
    assertTrue(staticAbstractOOFields.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(staticAbstractOOFields.get());
    this.staticAbstractOOFields = staticAbstractOOFields.get();


    Optional<ASTTCCompilationUnit> comp =  parser.parse("src/test/resources/mc/typescalculator/Check.tc");
    assertTrue(comp.isPresent());
    this.check = comp.get();
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(this.check);
  }

  @Test
  public void testValidModelsOO(){
    //test for oo tc
    ISynthesize synthesize = new SynthesizeSymTypeFromTypeCheckTest();
    ITypesCalculator typesCalculator = new DeriveSymTypeFromTypeCheckTest();
    TypeCheck tc = new TypeCheck(synthesize, typesCalculator);
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));

    checker.checkAll(check);
    checker.checkAll(bar);
    checker.checkAll(inheritanceBar);
  }

  @Test
  public void testValidModelsAbstract(){
    //test for abstract tc
    ISynthesize synthesize = new SynthesizeSymTypeFromTypeCheckTest();
    ITypesCalculator typesCalculator = new DeriveSymTypeFromTypeCheckTestAbstract();
    TypeCheck tc = new TypeCheck(synthesize, typesCalculator);
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));

    checker.checkAll(check);
    checker.checkAll(bar);
    checker.checkAll(inheritanceBar);
  }

  @Test
  public void testStaticAbstractOOMethods(){
    testAbstractWorksButOODoesNot("0xA0239", staticAbstractOOMethods);
  }

  @Test
  public void testStaticAbstractOOFields(){
    testAbstractWorksButOODoesNot("0xA0237", staticAbstractOOFields);
  }

  protected void testAbstractWorksButOODoesNot(String errorCode, ASTTCCompilationUnit comp){
    //abstract tc
    Log.clearFindings();
    ISynthesize synthesize = new SynthesizeSymTypeFromTypeCheckTest();
    ITypesCalculator typesCalculatorAbs = new DeriveSymTypeFromTypeCheckTestAbstract();
    TypeCheck tc = new TypeCheck(synthesize, typesCalculatorAbs);
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));

    checker.checkAll(comp);
    assertEquals(0, Log.getFindingsCount());

    //oo tc
    ITypesCalculator typesCalculatorOO = new DeriveSymTypeFromTypeCheckTest();
    tc = new TypeCheck(synthesize, typesCalculatorOO);
    checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect(tc));
    checker.addCoCo(new VariableAssignmentCorrectType(tc));

    try{
      checker.checkAll(comp);
    }catch(Exception e){
      //do nothing here, just catch the exception for further testing
    }
    assertEquals(1, Log.getFindingsCount());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(errorCode));
  }



}
