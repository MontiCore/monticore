package de.monticore.types;

import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
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
  private MCCollectionTypesTestParser mcCollectionTypesTestParser;
  private MCCustomGenericTypesTestParser customGenericTypesTestParser;
  private MCGenericTypesTestParser genericTypesTestParser;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp(){
    this.mcCollectionTypesTestParser = new MCCollectionTypesTestParser();
    this.basicTypesTestParser = new MCBasicTypesTestParser();
    this.customGenericTypesTestParser = new MCCustomGenericTypesTestParser();
    this.genericTypesTestParser = new MCGenericTypesTestParser();
  }

  @Test
  public void testMCListType() throws IOException {
    String list = "List<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCListType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(list);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
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

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCMapType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(map);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCMapType);

    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCOptionalType() throws IOException {
    String optional = "Optional<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCOptionalType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(optional);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCOptionalType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCSetType() throws IOException {
    String set = "Set<String>";

    Optional<ASTMCGenericType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> customAst = customGenericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCSetType);

    Optional<ASTMCGenericType> genericAST = genericTypesTestParser.parse_StringMCGenericType(set);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCSetType);


    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCBasicTypeArgument() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCTypeArgument> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> customAst = customGenericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCBasicTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
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
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCCustomTypeArgument);

    Optional<ASTMCTypeArgument> genericAST = genericTypesTestParser.parse_StringMCTypeArgument(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCCustomTypeArgument);

    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

  @Test
  public void testMCQualifiedType() throws IOException {
    String type = "de.monticore.ASTNode";

    Optional<ASTMCType> basicAST = basicTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicAST.isPresent());
    assertTrue(basicAST.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> basicGenericAst = mcCollectionTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(basicGenericAst.isPresent());
    assertTrue(basicGenericAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> customAst = customGenericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(customAst.isPresent());
    assertTrue(customAst.get() instanceof ASTMCQualifiedType);

    Optional<ASTMCType> genericAST = genericTypesTestParser.parse_StringMCType(type);
    assertFalse(mcCollectionTypesTestParser.hasErrors());
    assertTrue(genericAST.isPresent());
    assertTrue(genericAST.get() instanceof ASTMCQualifiedType);

    assertTrue(basicAST.get().deepEquals(customAst.get()));
    assertTrue(basicAST.get().deepEquals(basicGenericAst.get()));
    assertTrue(basicAST.get().deepEquals(genericAST.get()));
    assertTrue(basicGenericAst.get().deepEquals(customAst.get()));
    assertTrue(basicGenericAst.get().deepEquals(genericAST.get()));
    assertTrue(genericAST.get().deepEquals(customAst.get()));
  }

}
