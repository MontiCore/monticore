/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypestest.MCFullGenericTypesTestMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrintTypeAstExtensionTests {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCFullGenericTypesTestMill.reset();
    MCFullGenericTypesTestMill.init();
  }


  @Test
  public void printTypeMethodPrimitiveBooleanTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();

    String[] primitives = {"boolean","byte","short","int","char","float","long","double"};

    for(String simpleReference:primitives) {
      try {
        Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);

        for (Finding f : Log.getFindings()) {
          System.out.println(f.getMsg());
        }

        Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodObjectTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCObjectType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodQualifiedTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodReturnTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.Prettyprint";
    try {
      Optional<ASTMCReturnType> type = mcBasicTypesParser.parse_StringMCReturnType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodReturnTypeVoidTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "void";
    try {
      Optional<ASTMCReturnType> type = mcBasicTypesParser.parse_StringMCReturnType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodTullGenericTypeTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<S>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodTullGenericType2Test() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodTSimpleGenericsArrayTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String[] types = {"Person<Konto>","java.util.List<socnet.Person<Konto>,List<boolean>>"};
    for(String simpleReference:types) {
      try {
        Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
        Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }



  @Test
  public void printTypeMethodCollectionTypesTest() {
    MCCollectionTypesTestParser parser= new MCCollectionTypesTestParser();

    String[] collectionTypes = {"List<boolean>","Optional<a.b.cd.Person>","Map<boolean,a.P>","Set<Person>"};
    for(String simpleReference:collectionTypes) {
      try {
        Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
        Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodTullGenericTypeWildcardExtendsTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<? extends T>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodTullGenericTypeExtendsTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<? extends T>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);

      for(Finding f : Log.getFindings()) {
        System.out.println(f.getMsg());
      }

      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void printTypeMethodTullGenericTypeWildcardSuperTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<? super Compareable<T>>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);

      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodImportStatementTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "import a.b.C;";
    try {
      Optional<ASTMCImportStatement> type = parser.parse_StringMCImportStatement(simpleReference);

      for(Finding f : Log.getFindings()) {
        System.out.println(f.getMsg());
      }

      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void printTypeMethodStarImportStatementTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "import a.b.c.*;";
    try {
      Optional<ASTMCImportStatement> type = parser.parse_StringMCImportStatement(simpleReference);

      for(Finding f : Log.getFindings()) {
        System.out.println(f.getMsg());
      }

      Assertions.assertEquals(simpleReference.trim(), type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
