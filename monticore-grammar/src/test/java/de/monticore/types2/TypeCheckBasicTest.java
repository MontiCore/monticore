/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._ast.MCBasicTypesTestMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.types2.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeCheckBasicTest {
  
  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
    TypeCheckBasic.initTypeCheckerByMe();  // sets the Type Checker to be tested
  }
  
  // setup of objects (unchanged during tests)
  SymTypeExpression teDouble = createTypeConstant("double");
  SymTypeExpression teInt = createTypeConstant("int");
  SymTypeExpression teVarA = createTypeVariable("A");
  SymTypeExpression teVarB = createTypeVariable("B");
  SymTypeExpression teP = createObjectType("de.x.Person");
  SymTypeExpression teH = createObjectType("Human");  // on purpose: package missing
  SymTypeExpression teVoid = createTypeVoid();
  SymTypeExpression teArr1 = createArrayType(1, teH);
  SymTypeExpression teArr3 = createArrayType(3, teInt);
  SymTypeExpression teSet = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teP));
  SymTypeExpression teSetA = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teVarA));
  SymTypeExpression teMap = createGenericTypeExpression("Map", Lists.newArrayList(teInt,teP)); // no package!
  SymTypeExpression teFoo = createGenericTypeExpression("x.Foo", Lists.newArrayList(teP,teDouble,teInt,teH));
  SymTypeExpression teDeep1 = createGenericTypeExpression("java.util.Set", Lists.newArrayList(teMap));
  SymTypeExpression teDeep2 = createGenericTypeExpression("java.util.Map2", Lists.newArrayList(teInt,teDeep1));
  
  // Pasrer used for convenience:
  MCBasicTypesTestParser parser = new MCBasicTypesTestParser();
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c
  
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test2() throws IOException {
    String s = "int";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test3() throws IOException {
    String s = "A";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test4() throws IOException {
    String s = "Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_Test5() throws IOException {
    String s = "de.x.Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_VoidTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    assertEquals("void", TypeCheck.symTypeFromAST(v).print());
  }
  
  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(v).build();
    assertEquals("void", TypeCheck.symTypeFromAST(r).print());
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    // im Prinzip dassselbe via Parser:
    ASTMCReturnType r = parser.parse_StringMCReturnType("void").get();
    assertEquals("void", TypeCheck.symTypeFromAST(v).print());
  }
  
  @Test
  public void symTypeFromAST_ReturnTest3() throws IOException {
    // und nochmal einen normalen Typ:
    String s = "Person";
    ASTMCReturnType r = parser.parse_StringMCReturnType(s).get();
    assertEquals(s, TypeCheck.symTypeFromAST(r).print());
  }
  
  // ------------------------------------------------------  Tests for Function 2
  
  
  
}
