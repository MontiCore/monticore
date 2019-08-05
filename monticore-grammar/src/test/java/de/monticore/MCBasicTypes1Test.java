/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mcbasictypes1._ast.ASTBooleanType;
import de.monticore.mcbasictypes1._ast.ASTByteType;
import de.monticore.mcbasictypes1._ast.ASTCharType;
import de.monticore.mcbasictypes1._ast.ASTDoubleType;
import de.monticore.mcbasictypes1._ast.ASTFloatType;
import de.monticore.mcbasictypes1._ast.ASTIntType;
import de.monticore.mcbasictypes1._ast.ASTLongType;
import de.monticore.mcbasictypes1._ast.ASTNameAsReferenceType;
import de.monticore.mcbasictypes1._ast.ASTShortType;
import de.monticore.mcbasictypes1._ast.ASTType;
import de.monticore.mcbasictypes1._ast.ASTVoidType;
import de.monticore.testmcbasictypes1._ast.ASTImportStatementList;
import de.monticore.testmcbasictypes1._ast.ASTNameAsReferenceTypeList;
import de.monticore.testmcbasictypes1._ast.ASTPrimitiveTypeList;
import de.monticore.testmcbasictypes1._ast.ASTQualifiedNameList;
import de.monticore.testmcbasictypes1._ast.ASTReturnTypeList;
import de.monticore.testmcbasictypes1._ast.ASTTypeList;
import de.monticore.testmcbasictypes1._parser.TestMCBasicTypes1Parser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class MCBasicTypes1Test {
  
  // setup the language infrastructure
  TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser();
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // QualifiedName
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testQualifiedName1() throws IOException {
    ASTQualifiedNameList ast = parser.parse_StringQualifiedNameList(
        "[Bla,a.Bla,a.b.Blub21,x32b.b32x,a]").get();
    assertEquals(5, ast.getQualifiedNameList().size());
    assertEquals("Bla", ast.getQualifiedNameList().get(0).getQName());
    assertEquals("a.Bla", ast.getQualifiedNameList().get(1).getQName());
    assertEquals("a.b.Blub21", ast.getQualifiedNameList().get(2).getQName());
    assertEquals("x32b.b32x", ast.getQualifiedNameList().get(3).getQName());
    assertEquals("a", ast.getQualifiedNameList().get(4).getQName());
  }
  
  // --------------------------------------------------------------------
  // ImportStatement
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testImportStatement() throws IOException {
    ASTImportStatementList ast = parser.parse_StringImportStatementList(
        "[import Bla; ,import a.Bla; import a.*;]").get();
    assertEquals(3, ast.getImportStatementList().size());
    assertEquals("Bla", ast.getImportStatementList().get(0).getQName());
    assertEquals("a.Bla", ast.getImportStatementList().get(1).getQName());
    assertFalse(ast.getImportStatementList().get(1).isStar());
    assertEquals("a", ast.getImportStatementList().get(2).getQName());
    assertTrue(ast.getImportStatementList().get(2).isStar());
  }
  
  // --------------------------------------------------------------------
  // Type
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTTypeList ast = parser.parse_StringTypeList(
        "[Bla,boolean,int,a.Person]").get();
    assertEquals(4, ast.getTypeList().size());
  }
  
  // --------------------------------------------------------------------
  // PrimitiveType
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testPrimitiveType1() throws IOException {
    ASTPrimitiveTypeList ast = parser.parse_StringPrimitiveTypeList(
        "[char,boolean,int,float,long,double, byte,short]").get();
    assertEquals(8, ast.getPrimitiveTypeList().size());
    assertEquals(ASTCharType.class, ast.getPrimitiveTypeList().get(0).getClass());
    assertEquals(ASTBooleanType.class, ast.getPrimitiveTypeList().get(1).getClass());
    assertEquals(ASTIntType.class, ast.getPrimitiveTypeList().get(2).getClass());
    assertEquals(ASTFloatType.class, ast.getPrimitiveTypeList().get(3).getClass());
    assertEquals(ASTLongType.class, ast.getPrimitiveTypeList().get(4).getClass());
    assertEquals(ASTDoubleType.class, ast.getPrimitiveTypeList().get(5).getClass());
    assertEquals(ASTByteType.class, ast.getPrimitiveTypeList().get(6).getClass());
    assertEquals(ASTShortType.class, ast.getPrimitiveTypeList().get(7).getClass());
  }
  
  // --------------------------------------------------------------------
  // NameAsReferenceType
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testNameAsReferenceType1() throws IOException {
    ASTNameAsReferenceTypeList ast = parser.parse_StringNameAsReferenceTypeList(
        "[Bla,a.Bla,a.b.Blub21]").get();
    assertEquals(3, ast.getNameAsReferenceTypeList().size());
    assertEquals("Bla", ast.getNameAsReferenceTypeList().get(0).getQName());
    assertEquals("a.Bla", ast.getNameAsReferenceTypeList().get(1).getQName());
    assertEquals("a.b.Blub21", ast.getNameAsReferenceTypeList().get(2).getQName());
  }
  
  // --------------------------------------------------------------------
  // ReturnType
  // --------------------------------------------------------------------
  
  // --------------------------------------------------------------------
  @Test
  public void testReturnType1() throws IOException {
    ASTReturnTypeList ast = parser.parse_StringReturnTypeList(
        "[Bla,a.Bla,int,void]").get();
    assertEquals(4, ast.getReturnTypeList().size());
    
    Optional<ASTType> ot0 = ast.getReturnTypeList().get(0).getTypeOpt();
    assertTrue(ot0.isPresent());
    ASTType t0 = ot0.get();
    assertEquals(ASTNameAsReferenceType.class, t0.getClass());
    
    Optional<ASTType> ot2 = ast.getReturnTypeList().get(2).getTypeOpt();
    assertTrue(ot2.isPresent());
    ASTType t2 = ot2.get();
    assertEquals(ASTIntType.class, t2.getClass());
    
    Optional<ASTVoidType> ot3 = ast.getReturnTypeList().get(3).getVoidTypeOpt();
    assertTrue(ot3.isPresent());
    ASTVoidType t3 = ot3.get();
    assertEquals(ASTVoidType.class, t3.getClass());
    
  }
  
}
