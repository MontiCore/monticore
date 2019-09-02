/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types2.SymTypeExpressionFactory.*;
import static org.junit.Assert.assertEquals;

public class TypeCheckCollectionTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCCollectionTypes.mc4
   */
  
  @Before
  public void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }
  
  // Parer used for convenience:
  MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
  
  // This is Visitor for Collection types under test:
  SynthesizeSymTypeFromMCCollectionTypes synt = new SynthesizeSymTypeFromMCCollectionTypes();
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheckBasic(synt);
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)
  
/*
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    parser = new MCCollectionTypesTestParser();
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
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder()
                                  .setMCVoidType(v).build();
    assertEquals("void", tc.symTypeFromAST(r).print());
  }

  @Test
  public void symTypeFromAST_ReturnTest3() throws IOException {
    // und nochmal einen normalen Typ:
    String s = "Person";
    ASTMCReturnType r = parser.parse_StringMCReturnType(s).get();
    assertEquals(s, tc.symTypeFromAST(r).print());
  }
*/

  // new forms of Types coming from MCCollectionType
  
  @Test
  public void symTypeFromAST_TestList() throws IOException {
    String s = "List<ka.x.Auto>";
    parser = new MCCollectionTypesTestParser();
    System.out.println("\nT XXX1 " + s);
    Optional<ASTMCType> x = parser.parse_StringMCType(s);
    System.out.println("\nT XXX1b " + x.isPresent() + x);
    System.out.println("\nT XXX1bc " + x.get().getClass());
    ASTMCListType y = (ASTMCListType)x.get();
    
    ASTMCTypeArgument z = y.getMCTypeArgument();
    
    System.out.println("\nT XXX1z1 " + z.toString());
    Optional<ASTMCType> r = z.getMCTypeOpt();
    Optional<ASTMCType> t = z.getMCTypeOpt();
    System.out.println("\nT XXX Log.findings are:::\n " + Log.getFindings() + "\n\n"); // XXX
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    System.out.println("\nT XXX1c " + asttype);
    System.out.println("\nT XXX1cPrint " + asttype.printType());
    System.out.println("\nT XXX1cName " + asttype.getName());
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
/*
  @Test
  public void symTypeFromAST_TestListQual() throws IOException {
    String s = "List<a.z.Person>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestList2() throws IOException {
    String s = "List < List < Person>>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestMapList() throws IOException {
    String s = "Map<List<Person>,int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestMapMapMap() throws IOException {
    String s = "Map<Map<A,B>,Map<Map<C,A>,Map<int,Person>>>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  @Test
  public void symTypeFromAST_TestOptional() throws IOException {
    String s = "Optional < List < Person>>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestSet() throws IOException {
    String s = "Set < Set < int>>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
*/
  
  
  // ------------------------------------------------------  Tests for Function 2
  

  
}
