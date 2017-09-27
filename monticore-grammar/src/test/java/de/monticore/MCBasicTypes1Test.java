/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
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
  TestMCBasicTypes1Parser parser = new TestMCBasicTypes1Parser() ;
  
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
    ASTQualifiedNameList ast = parser.parseString_QualifiedNameList(
     "[Bla,a.Bla,a.b.Blub21,x32b.b32x,a]" ).get();
    assertEquals(5, ast.getQualifiedNames().size());
    assertEquals("Bla", ast.getQualifiedNames().get(0).getQName());
    assertEquals("a.Bla", ast.getQualifiedNames().get(1).getQName());
    assertEquals("a.b.Blub21", ast.getQualifiedNames().get(2).getQName());
    assertEquals("x32b.b32x", ast.getQualifiedNames().get(3).getQName());
    assertEquals("a", ast.getQualifiedNames().get(4).getQName());
  }


  // --------------------------------------------------------------------
  // ImportStatement
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testImportStatement() throws IOException {
    ASTImportStatementList ast = parser.parseString_ImportStatementList(
     "[import Bla; ,import a.Bla; import a.*;]" ).get();
    assertEquals(3, ast.getImportStatements().size());
    assertEquals("Bla", ast.getImportStatements().get(0).getQName());
    assertEquals("a.Bla", ast.getImportStatements().get(1).getQName());
    assertFalse(ast.getImportStatements().get(1).isStar());
    assertEquals("a", ast.getImportStatements().get(2).getQName());
    assertTrue(ast.getImportStatements().get(2).isStar());
  }


  // --------------------------------------------------------------------
  // Type
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTTypeList ast = parser.parseString_TypeList(
     "[Bla,boolean,int,a.Person]" ).get();
    assertEquals(4, ast.getTypes().size());
  }


  // --------------------------------------------------------------------
  // PrimitiveType
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testPrimitiveType1() throws IOException {
    ASTPrimitiveTypeList ast = parser.parseString_PrimitiveTypeList(
     "[char,boolean,int,float,long,double, byte,short]" ).get();
    assertEquals(8, ast.getPrimitiveTypes().size());
    assertEquals(ASTCharType.class, ast.getPrimitiveTypes().get(0).getClass());
    assertEquals(ASTBooleanType.class, ast.getPrimitiveTypes().get(1).getClass());
    assertEquals(ASTIntType.class, ast.getPrimitiveTypes().get(2).getClass());
    assertEquals(ASTFloatType.class, ast.getPrimitiveTypes().get(3).getClass());
    assertEquals(ASTLongType.class, ast.getPrimitiveTypes().get(4).getClass());
    assertEquals(ASTDoubleType.class, ast.getPrimitiveTypes().get(5).getClass());
    assertEquals(ASTByteType.class, ast.getPrimitiveTypes().get(6).getClass());
    assertEquals(ASTShortType.class, ast.getPrimitiveTypes().get(7).getClass());
  }


  // --------------------------------------------------------------------
  // NameAsReferenceType
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNameAsReferenceType1() throws IOException {
    ASTNameAsReferenceTypeList ast = parser.parseString_NameAsReferenceTypeList(
     "[Bla,a.Bla,a.b.Blub21]" ).get();
    assertEquals(3, ast.getNameAsReferenceTypes().size());
    assertEquals("Bla", ast.getNameAsReferenceTypes().get(0).getQName());
    assertEquals("a.Bla", ast.getNameAsReferenceTypes().get(1).getQName());
    assertEquals("a.b.Blub21", ast.getNameAsReferenceTypes().get(2).getQName());
  }


  // --------------------------------------------------------------------
  // ReturnType
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testReturnType1() throws IOException {
    ASTReturnTypeList ast = parser.parseString_ReturnTypeList(
     "[Bla,a.Bla,int,void]" ).get();
    assertEquals(4, ast.getReturnTypes().size());

    Optional<ASTType> ot0= ast.getReturnTypes().get(0).getType();
    assertTrue(ot0.isPresent());
    ASTType t0 = ot0.get();
    assertEquals(ASTNameAsReferenceType.class, t0.getClass());

    Optional<ASTType> ot2= ast.getReturnTypes().get(2).getType();
    assertTrue(ot2.isPresent());
    ASTType t2 = ot2.get();
    assertEquals(ASTIntType.class, t2.getClass());

    Optional<ASTVoidType> ot3= ast.getReturnTypes().get(3).getVoidType();
    assertTrue(ot3.isPresent());
    ASTVoidType t3 = ot3.get();
    assertEquals(ASTVoidType.class, t3.getClass());

  }

}
