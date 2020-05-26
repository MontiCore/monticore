/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynthesizeSymTypeFromMCBasicTypesTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * Basic configuration, i.e.
   * i.e. for
   *    expressions/ExpressionsBasis.mc4
   *    literals/MCLiteralsBasis.mc4
   *    types/MCBasicTypes.mc4
   */
  
  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  // Parer used for convenience:
  MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(new SynthesizeSymTypeFromMCBasicTypes(),null);
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c
  
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test2() throws IOException {
    String s = "int";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test3() throws IOException {
    String s = "A";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test4() throws IOException {
    String s = "Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test5() throws IOException {
    String s = "de.x.Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_VoidTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    assertEquals("void", tc.symTypeFromAST(v).print());
  }
  
  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(v).build();
    assertEquals("void", tc.symTypeFromAST(r).print());
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    // im Prinzip dassselbe via Parser:
    ASTMCReturnType r = parser.parse_StringMCReturnType("void").get();
    assertEquals("void", tc.symTypeFromAST(r).print());
  }
  
  @Test
  public void symTypeFromAST_ReturnTest3() throws IOException {
    // und nochmal einen normalen Typ:
    String s = "Person";
    ASTMCReturnType r = parser.parse_StringMCReturnType(s).get();
    assertEquals(s, tc.symTypeFromAST(r).print());
  }
  
  // ------------------------------------------------------  Tests for Function 2
  
  
  
}
