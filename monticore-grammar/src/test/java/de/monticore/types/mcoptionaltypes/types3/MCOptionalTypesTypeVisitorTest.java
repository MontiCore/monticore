/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcoptionaltypes.types3;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcoptionaltypes._ast.ASTMCShortOptionalType;
import de.monticore.types.mcoptionaltypestest.MCOptionalTypesTestMill;
import de.monticore.types.mcoptionaltypestest._parser.MCOptionalTypesTestParser;
import de.monticore.types.mcoptionaltypestest._symboltable.IMCOptionalTypesTestArtifactScope;
import de.monticore.types.mcstructuraltypes._ast.ASTMCUnionType;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(MCOptionalTypesTestMill.class)
public class MCOptionalTypesTypeVisitorTest {

  @BeforeEach
  public void init() {
    MCOptionalTypesTestTypeCheck3.init();
    DefsTypesForTests.setup();
  }

  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "Person?;          Optional<Person>",
      "int?;             Optional<int>",
  })
  public void testSameAsLongNotation(String shortType, String longType)
      throws IOException {
    SymTypeExpression shortSymType = calculate(shortType);
    SymTypeExpression longSymType = calculate(longType);
    assertTrue(shortSymType.deepEquals(longSymType),
        "expected " + longSymType.printFullName()
            + " but got " + shortSymType.printFullName());
  }

  // the long notation Optional<...> of MCCollectionTypes
  // does not allow generic type arguments, thus, compare the structure
  @ParameterizedTest
  @CsvSource(delimiter = ';', value = {
      "Optional<Person>?; Optional<Person>",
      "Person??;          Person?",
      "List<Person>?;     List<Person>",
  })
  public void testOptionalOfGeneric(String shortType, String innerType)
      throws IOException {
    SymTypeExpression type = calculate(shortType);
    assertTrue(type.isGenericType());
    assertEquals("Optional", type.getTypeInfo().getName());
    assertEquals(1, type.asGenericType().getArgumentList().size());
    SymTypeExpression expectedInner = calculate(innerType);
    assertTrue(type.asGenericType().getArgument(0).deepEquals(expectedInner),
        "expected argument " + expectedInner.printFullName() + " but got "
            + type.asGenericType().getArgument(0).printFullName());
  }

  @Test
  public void testArrayOfOptional() throws IOException {
    ASTMCType ast = parseAndCreateSymTab("Person?[]");
    ASTMCArrayType array = assertInstanceOf(ASTMCArrayType.class, ast);
    assertInstanceOf(ASTMCShortOptionalType.class, array.getMCType());
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    MCAssertions.assertNoFindings();
    assertTrue(type.isArrayType());
    assertTrue(type.asArrayType().getArgument().deepEquals(
        calculate("Optional<Person>")));
  }

  @Test
  public void testOptionalOfArray() throws IOException {
    ASTMCType ast = parseAndCreateSymTab("Person[]?");
    ASTMCShortOptionalType opt =
        assertInstanceOf(ASTMCShortOptionalType.class, ast);
    assertInstanceOf(ASTMCArrayType.class, opt.getMCType());
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    MCAssertions.assertNoFindings();
    assertTrue(type.isGenericType());
    assertEquals("Optional", type.getTypeInfo().getName());
    assertTrue(type.asGenericType().getArgument(0).isArrayType());
  }

  @Test
  public void testBindsStrongerThanUnion() throws IOException {
    // Person | Car?  ==  Person | Optional<Car>
    ASTMCType ast = parseAndCreateSymTab("Person | Car?");
    ASTMCUnionType union = assertInstanceOf(ASTMCUnionType.class, ast);
    assertInstanceOf(ASTMCShortOptionalType.class, union.getRight());
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    MCAssertions.assertNoFindings();
    assertTrue(type.deepEquals(calculate("Person | Optional<Car>")));
  }

  @Test
  public void testOptionalOfBracketedUnion() throws IOException {
    ASTMCType ast = parseAndCreateSymTab("(Person | Car)?");
    assertInstanceOf(ASTMCShortOptionalType.class, ast);
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    MCAssertions.assertNoFindings();
    assertTrue(type.isGenericType());
    assertTrue(type.asGenericType().getArgument(0).isUnionType());
  }

  @Test
  public void testPrettyPrint() throws IOException {
    assertEquals("Person?", parseAndCreateSymTab("Person?").printType());
    // the MCArrayTypes printer keeps the whitespace of the inner type
    // (e.g., "Person? []"), thus, whitespace is ignored here
    assertEquals("Person?[]",
        parseAndCreateSymTab("Person?[]").printType().replace(" ", ""));
    assertEquals("Person[]?",
        parseAndCreateSymTab("Person[]?").printType().replace(" ", ""));
    assertEquals("List<Person>?",
        parseAndCreateSymTab("List<Person>?").printType());
  }

  @Test
  public void testUnknownInnerType() throws IOException {
    ASTMCType ast = parseAndCreateSymTab("DoesNotExist?");
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    assertTrue(type.isObscureType());
    // the inner type cannot be found (MCBasicTypes)
    MCAssertions.assertHasFindingStartingWith("0xA0324");
    // thus, the optional cannot be calculated either
    MCAssertions.assertHasFindingStartingWith("0xE9FE0");
  }

  protected SymTypeExpression calculate(String model) throws IOException {
    ASTMCType ast = parseAndCreateSymTab(model);
    SymTypeExpression type = TypeCheck3.symTypeFromAST(ast);
    MCAssertions.assertNoFindings();
    return type;
  }

  protected ASTMCType parseAndCreateSymTab(String model) throws IOException {
    MCOptionalTypesTestParser parser = MCOptionalTypesTestMill.parser();
    Optional<ASTMCType> astOpt = parser.parse_String(model);
    MCAssertions.assertNoFindings();
    assertFalse(parser.hasErrors());
    assertTrue(astOpt.isPresent());

    IMCOptionalTypesTestArtifactScope artifactScope =
        MCOptionalTypesTestMill.scopesGenitorDelegator()
            .createFromAST(astOpt.get());
    artifactScope.setName("unimportant");
    MCAssertions.assertNoFindings();
    return astOpt.get();
  }

}
