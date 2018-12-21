package de.monticore.types;

import de.monticore.types.mcbasicgenericstypes._ast.*;
import de.monticore.types.mcbasicgenericstypestest._parser.MCBasicGenericsTypesTestParser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedReferenceType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mccustomgenerictypestest._parser.MCCustomGenericTypesTestParser;
import de.monticore.types.mcgenerictypestest._parser.MCGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlwaysTheSameASTTest {

  private MCBasicTypesTestParser basicTypesTestParser;
  private MCBasicGenericsTypesTestParser basicGenericsTypesTestParser;
  private MCCustomGenericTypesTestParser customGenericTypesTestParser;
  private MCGenericTypesTestParser genericTypesTestParser;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp(){
    this.basicGenericsTypesTestParser = new MCBasicGenericsTypesTestParser();
    this.basicTypesTestParser = new MCBasicTypesTestParser();
    this.customGenericTypesTestParser = new MCCustomGenericTypesTestParser();
    this.genericTypesTestParser = new MCGenericTypesTestParser();
  }

  @Test
  public void testMCListType() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericReferenceType> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCGenericReferenceType(list);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericReferenceType> customAst = customGenericTypesTestParser.parse_StringMCGenericReferenceType(list);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericReferenceType> genericAST = genericTypesTestParser.parse_StringMCGenericReferenceType(list);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCListType);
    
    ASTMCListType basicGenericList =(ASTMCListType) basicGenericAst.get();
    ASTMCListType customList =(ASTMCListType) customAst.get();
    ASTMCListType genericList =(ASTMCListType) genericAST.get();
    
    assertTrue(basicGenericList.deepEquals(customList));
    assertTrue(basicGenericList.deepEquals(genericList));
    assertTrue(genericList.deepEquals(customList));
  }

  @Test
  public void testMCMapType() throws IOException {
    String map = "Map<Integer, String>";

    Optional<ASTMCGenericReferenceType> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCGenericReferenceType(map);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericReferenceType> customAst = customGenericTypesTestParser.parse_StringMCGenericReferenceType(map);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericReferenceType> genericAST = genericTypesTestParser.parse_StringMCGenericReferenceType(map);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCMapType);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCOptionalType() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericReferenceType> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCGenericReferenceType(optional);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericReferenceType> customAst = customGenericTypesTestParser.parse_StringMCGenericReferenceType(optional);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericReferenceType> genericAST = genericTypesTestParser.parse_StringMCGenericReferenceType(optional);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCSetType() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericReferenceType> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCGenericReferenceType(set);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericReferenceType> customAst = customGenericTypesTestParser.parse_StringMCGenericReferenceType(set);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericReferenceType> genericAST = genericTypesTestParser.parse_StringMCGenericReferenceType(set);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCSetType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCTypeArgument> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCBasicTypeArgument);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCCustomTypeArgument() throws IOException {
    String type = "List<a.b.C>";

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCCustomTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCCustomTypeArgument);

    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCQualifiedReferenceType() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCType> basicAST = basicTypesTestParser.parse_StringMCType(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicAST.isPresent());
    assertTrue(basicAST.get() instanceof ASTMCQualifiedReferenceType);

    Optional<ASTMCType> basicGenericAst = basicGenericsTypesTestParser.parse_StringMCType(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCQualifiedReferenceType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCQualifiedReferenceType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(type);
    assertFalse(basicGenericsTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCQualifiedReferenceType);

    assertTrue(basicAST.get().deepEquals(customAst.get()));
    assertTrue(basicAST.get().deepEquals(basicGenericAst.get()));
    assertTrue(basicAST.get().deepEquals(genericAST.get()));
    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

}
