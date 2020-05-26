/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynthesizeSymTypeFromMCCollectionTypesTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCCollectionTypes.mc4
   */
  
  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  // Parer used for convenience:
  MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
  
  // This is Visitor for Collection types under test:
  SynthesizeSymTypeFromMCCollectionTypes synt = new SynthesizeSymTypeFromMCCollectionTypes();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(synt,null);
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)
  
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

  // new forms of Types coming from MCCollectionType
  
  @Test
  public void symTypeFromAST_TestListQual() throws IOException {
    String s = "List<a.z.Person>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestListQual2() throws IOException {
    String s = "Set<Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestListQual3() throws IOException {
    String s = "Map<int,Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  @Test
  public void symTypeFromAST_TestListQual4() throws IOException {
    String s = "Set<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    assertEquals(s, tc.symTypeFromAST(asttype).print());
  }
  
  
  
}
