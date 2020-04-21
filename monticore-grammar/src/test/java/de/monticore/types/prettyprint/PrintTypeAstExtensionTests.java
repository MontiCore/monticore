/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypestest._parser.MCFullGenericTypesTestParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class PrintTypeAstExtensionTests {

  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() {
    Log.getFindings().clear();
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

        assertEquals(simpleReference.trim(), type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  @Test
  public void printTypeMethodObjectTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCObjectType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void printTypeMethodQualifiedTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = mcBasicTypesParser.parse_StringMCQualifiedType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void printTypeMethodReturnTypeTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "de.monticore.types.Prettyprint";
    try {
      Optional<ASTMCReturnType> type = mcBasicTypesParser.parse_StringMCReturnType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void printTypeMethodReturnTypeVoidTest() {
    MCBasicTypesTestParser mcBasicTypesParser= new MCBasicTypesTestParser();
    String simpleReference = "void";
    try {
      Optional<ASTMCReturnType> type = mcBasicTypesParser.parse_StringMCReturnType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void printTypeMethodTullGenericTypeTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<S>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void printTypeMethodTullGenericType2Test() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void printTypeMethodTullGenericTypeArrayTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore.types.prettyprint[]";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void printTypeMethodTSimpleGenericsArrayTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String[] types = {"Person<Konto>","java.util.List<socnet.Person<Konto>,List<boolean>>"};
    for(String simpleReference:types) {
      try {
        Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
        assertEquals(simpleReference.trim(), type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }



  @Test
  public void printTypeMethodCollectionTypesTest() {
    MCCollectionTypesTestParser parser= new MCCollectionTypesTestParser();

    String[] collectionTypes = {"List<boolean>","Optional<a.b.cd.Person>","Map<boolean,a.P>","Set<Person>"};
    for(String simpleReference:collectionTypes) {
      try {
        Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
        assertEquals(simpleReference.trim(), type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  @Test
  public void printTypeMethodTullGenericTypeWildcardExtendsTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<? extends T>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);
      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
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

      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Test
  public void printTypeMethodTullGenericTypeWildcardSuperTest() {
    MCFullGenericTypesTestParser parser= new MCFullGenericTypesTestParser();
    String simpleReference = "de.monticore<T>.types.prettyprint<? super Compareable<T>>";
    try {
      Optional<? extends ASTMCType> type = parser.parse_StringMCType(simpleReference);

      assertEquals(simpleReference.trim(),type.get().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
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

      assertEquals(simpleReference.trim(),type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
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

      assertEquals(simpleReference.trim(),type.get().printType().trim());


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
