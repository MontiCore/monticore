package de.monticore.types;


import de.monticore.types.mcbasicgenericstypes._ast.*;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;
import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicGenericsTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testBasicGenericsTypes() throws IOException {
    Class foo = boolean.class;
    String[] types = new String[]{"List<a.A>","Optional<String>","Set<String>","Map<String,String>","List<socnet.Person>"};
    for (String testType : types) {
      MCBasicGenericsTypesTestParser mcBasicTypesParser = new MCBasicGenericsTypesTestParser();
      // .parseType(primitive);

      Optional<ASTMCType> type = mcBasicTypesParser.parse_String(testType);

      assertNotNull(type);
      assertTrue(type.isPresent());
      assertTrue(type.get() instanceof ASTMCReferenceType);

      ASTMCReferenceType t = (ASTMCReferenceType) type.get();
      t.accept( new MCBasicGenericsTypesVisitor() {
        public void visit(ASTMCListType t) {
          assertTrue(true);
          t.getMCTypeArgument().accept(new MCBasicGenericsTypesVisitor() {
            @Override
            public void visit(ASTMCType node) {
              if (!(node instanceof ASTMCQualifiedReferenceType)) {
                fail("Found not String");
              }
            }
          });
        }
      });
    }
  }


  private class CheckTypeVisitor implements MCBasicGenericsTypesVisitor {

  }

  @Test
  public void testMCListTypeValid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("List<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCListType);
  }

  @Test
  public void testMCListTypeInvalid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.List<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testMCMapTypeValid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Map<Integer, String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCMapType);
  }

  @Test
  public void testMCMapTypeInvalid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Map<Integer, String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }


  @Test
  public void testMCOptionalTypeValid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Optional<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCOptionalType);
  }

  @Test
  public void testMCOptionalTypeInvalid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Optional<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }


  @Test
  public void testMCSetTypeValid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("Set<String>");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCSetType);
  }

  @Test
  public void testMCSetTypeInvalid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCGenericReferenceType> type = parser.parse_StringMCGenericReferenceType("java.util.Set<String>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }

  @Test
  public void testMCTypeArgumentValid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("a.b.c");
    assertFalse(parser.hasErrors());
    assertNotNull(type);
    assertTrue(type.isPresent());
    assertTrue(type.get() instanceof ASTMCBasicTypeArgument);
  }

  @Test
  public void testMCTypeArgumentInvalid() throws IOException {
    MCBasicGenericsTypesTestParser parser = new MCBasicGenericsTypesTestParser();
    Optional<ASTMCTypeArgument> type = parser.parse_StringMCTypeArgument("List<A>");
    assertTrue(parser.hasErrors());
    assertFalse(type.isPresent());
  }
}
