package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class MCBasicTypesTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testPrimitiveTypesAPI() throws IOException {
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTPrimitiveType> boolOpt = mcBasicTypesParser.parse_StringPrimitiveType("boolean");
    assertTrue(boolOpt.isPresent());

    ASTPrimitiveType bool = boolOpt.get();

    Boolean isBool = bool.isBoolean();
    assertTrue(isBool);
    Boolean isByte = bool.isByte();
    assertFalse(isByte);
    Boolean isChar = bool.isChar();
    assertFalse(isChar);
    Boolean isDouble = bool.isDouble();
    assertFalse(isDouble);
    Boolean isFloat = bool.isFloat();
    assertFalse(isFloat);
    Boolean isInt = bool.isInt();
    assertFalse(isInt);
    Boolean isShort = bool.isShort();
    assertFalse(isInt);
  }


  @Test
  public void testPrimitiveTypes() {

    Class foo = boolean.class;

    String[] primitives = new String[]{"boolean", "byte", "char", "short", "int", "long",
            "float", "double"};
    try {
      for (String primitive : primitives) {
        MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
        // .parseType(primitive);

        Optional<? extends ASTType> type = mcBasicTypesParser.parse_StringPrimitiveType(primitive);

        assertNotNull(type);
        assertTrue(type.isPresent());
        assertTrue(type.get() instanceof ASTPrimitiveType);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testOwnSimpleTypes() throws IOException {

    String[] types = new String[]{"socnet.Person", "Person"};
    for (String type : types) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTType> astType = mcBasicTypesParser.parse_StringType(type);
      assertNotNull(astType);
      assertTrue(astType.isPresent());
      assertTrue(astType.get() instanceof ASTReferenceType);

    }

  }

  @Test
  public void testBuiltInTypes() throws IOException {
    String[] builtIns = new String[]{"Boolean","String","Char","Short","Integer","Float","Double"};

    for (String builtIn : builtIns) {
      MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
      Optional<ASTBuiltInType> type = mcBasicTypesParser.parse_StringBuiltInType(builtIn);
      assertNotNull(type);
      assertTrue(type.isPresent());
    }


  }

  @Test
  public void testVisitorOnTypes() throws IOException {
    TypesVisitor t = new TypesVisitor();
    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    Optional<ASTType> v = mcBasicTypesParser.parse_String("Boolean");

    v.get().accept(t);
    Assert.assertEquals("Boolean",t.content);

  }

  private class TypesVisitor implements MCBasicTypesVisitor {
    String content="";

    public void visit(ASTBooleanRereferenceType t) {
      content = "Boolean";
    }
  }

}
