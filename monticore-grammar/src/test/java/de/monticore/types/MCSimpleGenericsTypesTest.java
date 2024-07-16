/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypestest.MCSimpleGenericTypesTestMill;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class MCSimpleGenericsTypesTest {

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCSimpleGenericTypesTestMill.reset();
    MCSimpleGenericTypesTestMill.init();
  }

  @Test
  public void testCustomGenericsTypes() throws IOException {
    String[] types = new String[]{"List<List<b.B>>","socnet.Person<socnet.Person<B>, SecondaryParam>"};

    for (String testType : types) {
      System.out.println("Teste "+testType);
      MCSimpleGenericTypesTestParser mcBasicTypesParser = new MCSimpleGenericTypesTestParser();

      Optional<ASTMCType> type = mcBasicTypesParser.parse_StringMCType(testType);

      Assertions.assertNotNull(type);
      Assertions.assertTrue(type.isPresent());
      Assertions.assertTrue(type.get() instanceof ASTMCObjectType);
      System.out.println(type.get().getClass());

      ASTMCObjectType t = (ASTMCObjectType) type.get();
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<Integer>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCListType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCListTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.List<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Map<Integer, String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCMapType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Map<java.util.List<Integer>, String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCMapTypeValid3() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.HashMap<String,java.util.List<String>>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Optional<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCOptionalType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCOptionalTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Optional<Set<String>>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("Set<String>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCSetType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCSetTypeValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("java.util.Set<List<String>>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicGenericType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCTypeArgumentValid2() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCGenericType> type = parser.parse_StringMCGenericType("List<A>");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(type);
    Assertions.assertTrue(type.isPresent());
    Assertions.assertTrue(type.get() instanceof ASTMCListType);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMCComplexReferenceTypeInvalid() throws IOException {
    //not defined in that grammar, only in MCGenericsTypes
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCType> type = parser.parse_StringMCType("java.util.List<A>.Set<C>.some.Collection<B>");
    Assertions.assertTrue(parser.hasErrors());
    Assertions.assertFalse(type.isPresent());
  }

  @Test
  public void testPrintTypeWithoutTypeArguments() throws IOException {
    MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
    Optional<ASTMCBasicGenericType> basicGenericType = parser.parse_StringMCBasicGenericType("a.B<C, D>");
    Optional<ASTMCGenericType> genericType = parser.parse_StringMCGenericType("a.B<C, D>");
    Assertions.assertTrue(genericType.isPresent());
    Assertions.assertTrue(basicGenericType.isPresent());
    Assertions.assertEquals("a.B", basicGenericType.get().printWithoutTypeArguments());
    Assertions.assertEquals("a.B", genericType.get().printWithoutTypeArguments());
    Assertions.assertFalse(parser.hasErrors());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
