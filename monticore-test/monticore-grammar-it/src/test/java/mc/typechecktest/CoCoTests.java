/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest;

import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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

import static org.junit.Assert.assertTrue;

public class CoCoTests {

  ASTTCCompilationUnit check;
  ASTTCCompilationUnit bar;
  ASTTCCompilationUnit inheritanceBar;

  @BeforeClass
  public static void init(){
    Log.init();
    Log.enableFailQuick(false);
    BasicSymbolsMill.initializePrimitives();
  }

  @Before
  public void setup() throws IOException {
    TypeCheckTestParser parser = TypeCheckTestMill.parser();
    TypeCheckTestMill.globalScope().setModelPath(new ModelPath(Paths.get("src/test/resources")));

    Optional<ASTTCCompilationUnit> comp2 =  parser.parse("src/test/resources/mc/typescalculator/Bar.tc");
    assertTrue(comp2.isPresent());
    TypeCheckTestPhasedSTC stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(comp2.get());
    this.bar = comp2.get();

    Optional<ASTTCCompilationUnit> comp3 = parser.parse("src/test/resources/mc/typescalculator/InheritanceBar.tc");
    assertTrue(comp3.isPresent());
    stc = new TypeCheckTestPhasedSTC();
    stc.createFromAST(comp3.get());
    this.inheritanceBar = comp3.get();


    Optional<ASTTCCompilationUnit> comp =  parser.parse("src/test/resources/mc/typescalculator/Check.tc");
    assertTrue(comp.isPresent());
    this.check = comp.get();
    stc.createFromAST(this.check);

  }


  @Test
  public void test(){
    TypeCheckTestCoCoChecker checker = new TypeCheckTestCoCoChecker();
    checker.addCoCo(new VariableDeclarationIsCorrect());
    checker.addCoCo(new VariableAssignmentCorrectType());

    checker.checkAll(check);
    checker.checkAll(bar);
    checker.checkAll(inheritanceBar);
  }



}
