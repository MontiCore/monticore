/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.testcommonexpressions.TestCommonExpressionsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SubExprNameExtractionResultTest {

  @Before
  public void setup() {
    TestCommonExpressionsMill.init();
    LogStub.init();         // replace log by a side effect free variant
    Log.enableFailQuick(false);
  }

  protected static ASTNameExpression buildNameExpression(String name) {
    return TestCommonExpressionsMill.nameExpressionBuilder().setName(name).build();
  }

  @Test
  public void resetCreatesNewList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    extractionResult.putNameAtStart(buildNameExpression("a"), "a");
    extractionResult.maybeAppendInvalidExprAtStart(buildNameExpression("b"));
    List<ExprToOptNamePair> subExprListBefore = extractionResult.getNamePartsRaw();

    // When
    extractionResult.reset();
    extractionResult.putNameAtStart(buildNameExpression("c"), "c");

    // Then
    List<ExprToOptNamePair> subExprListAfter = extractionResult.getNamePartsRaw();
    Assert.assertEquals(1, subExprListAfter.size());
    Assert.assertNotEquals(subExprListBefore, subExprListAfter);
  }

  @Test
  public void testSetSubExpressions() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    List<ExprToOptNamePair> subExprList = new LinkedList<>();
    ASTNameExpression nameToInsert = buildNameExpression("a");

    // When
    extractionResult.setSubExpressions(subExprList);
    subExprList.add(0, ExprToOptNamePair.of(nameToInsert, Optional.of("a")));

    // Then
    List<ExprToOptNamePair> returnedSubExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(1, returnedSubExprList.size());
    Assert.assertEquals(nameToInsert, returnedSubExprList.get(0).getExpression());
    Assert.assertEquals("a", returnedSubExprList.get(0).getName().orElse("no name inserted"));
    Assert.assertEquals(subExprList, returnedSubExprList);
  }

  @Test
  public void maybeAppendInvalidSubExprToEmptyList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression expr = buildNameExpression("a");

    // When
    extractionResult.maybeAppendInvalidExprAtStart(expr);

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(1, subExprList.size());
    Assert.assertEquals(expr, subExprList.get(0).getExpression());
    Assert.assertFalse(subExprList.get(0).getName().isPresent());
  }

  @Test
  public void maybeAppendInvalidSubExprToFilledListContainingDifferentValidNameExpressions() {
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression oldExpr = buildNameExpression("a");
    ASTNameExpression newExpr = buildNameExpression("b");
    extractionResult.putNameAtStart(oldExpr, "a");

    // When
    extractionResult.maybeAppendInvalidExprAtStart(newExpr);

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(2, subExprList.size());

    Assert.assertEquals(newExpr, subExprList.get(0).getExpression());
    Assert.assertFalse(subExprList.get(0).getName().isPresent());

    Assert.assertEquals(oldExpr, subExprList.get(1).getExpression());
    Assert.assertEquals("a", subExprList.get(1).getName().orElse("no name inserted"));
  }

  @Test
  public void maybeAppendInvalidSubExprToFilledListContainingDifferentInvalidNameExpressions() {
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression oldExpr = buildNameExpression("a");
    extractionResult.maybeAppendInvalidExprAtStart(oldExpr);

    // When
    ASTNameExpression newExpr = buildNameExpression("b");
    extractionResult.maybeAppendInvalidExprAtStart(newExpr);

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(2, subExprList.size());

    Assert.assertEquals(newExpr, subExprList.get(0).getExpression());
    Assert.assertFalse(subExprList.get(0).getName().isPresent());

    Assert.assertEquals(oldExpr, subExprList.get(1).getExpression());
    Assert.assertFalse(subExprList.get(1).getName().isPresent());
  }

  @Test
  public void maybeAppendInvalidSubExprDoesNotOverwriteSameExpression() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression expr = buildNameExpression("a");
    extractionResult.putNameAtStart(expr, "a");

    // When
    extractionResult.maybeAppendInvalidExprAtStart(expr);

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(1, subExprList.size());
    Assert.assertEquals(expr, subExprList.get(0).getExpression());
    Assert.assertEquals("a", subExprList.get(0).getName().orElse("no name inserted"));
  }

  @Test
  public void putNameAtStartOfEmptyList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression expr = buildNameExpression("a");

    // When
    extractionResult.putNameAtStart(expr, "a");

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(1, subExprList.size());
    Assert.assertEquals(expr, subExprList.get(0).getExpression());
    Assert.assertEquals("a", subExprList.get(0).getName().orElse("no name inserted"));
  }

  @Test
  public void putNameAtStartOfFilledListContainingDifferentValidExpressions() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression oldExpr = buildNameExpression("a");

    extractionResult.putNameAtStart(oldExpr, "a");

    // When
    ASTNameExpression newExpr = buildNameExpression("b");
    extractionResult.putNameAtStart(newExpr, "b");

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(2, subExprList.size());

    Assert.assertEquals(newExpr, subExprList.get(0).getExpression());
    Assert.assertEquals("b", subExprList.get(0).getName().orElse("no name inserted"));

    Assert.assertEquals(oldExpr, subExprList.get(1).getExpression());
    Assert.assertEquals("a", subExprList.get(1).getName().orElse("no name inserted"));
  }

  @Test
  public void putNameAtStartOverwritesSameExpressionThatHasNoNameYet() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression dummyExprNotToAlter = buildNameExpression("dummy already in list");
    ASTNameExpression expr = buildNameExpression("b");
    extractionResult.maybeAppendInvalidExprAtStart(dummyExprNotToAlter);
    extractionResult.maybeAppendInvalidExprAtStart(expr);

    // When
    extractionResult.putNameAtStart(expr, "b");

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(2, subExprList.size());

    Assert.assertEquals(expr, subExprList.get(0).getExpression());
    Assert.assertEquals("b", subExprList.get(0).getName().orElse("no name inserted"));

    Assert.assertEquals(dummyExprNotToAlter, subExprList.get(1).getExpression());
    Assert.assertFalse(subExprList.get(1).getName().isPresent());
  }

  @Test
  public void putNameAtStartOverwritesSameExpressionThatAlreadyHasAName() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression dummyExprNotToAlter = buildNameExpression("dummy already in list");
    ASTNameExpression expr = buildNameExpression("b");
    extractionResult.maybeAppendInvalidExprAtStart(dummyExprNotToAlter);
    extractionResult.putNameAtStart(expr, "nameToBeOverwritten");

    // When
    extractionResult.putNameAtStart(expr, "b");

    // Then
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();
    Assert.assertEquals(2, subExprList.size());

    Assert.assertEquals(expr, subExprList.get(0).getExpression());
    Assert.assertEquals("b", subExprList.get(0).getName().orElse("no name inserted"));

    Assert.assertEquals(dummyExprNotToAlter, subExprList.get(1).getExpression());
    Assert.assertFalse(subExprList.get(1).getName().isPresent());
  }

  @Test
  public void getNamePartsIfValidOnEmptyNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    // When
    Optional<List<ExprToNamePair>> subExprList = extractionResult.getNamePartsIfValid();

    // Then
    Assert.assertFalse(subExprList.isPresent());
  }

  @Test
  public void getNamePartsIfValidOnFilledInvalidNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");
    ASTNameExpression cExpr = buildNameExpression("c");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.maybeAppendInvalidExprAtStart(bExpr);
    extractionResult.putNameAtStart(cExpr, "c");

    // When
    Optional<List<ExprToNamePair>> subExprList = extractionResult.getNamePartsIfValid();

    // Then
    Assert.assertFalse(subExprList.isPresent());
  }

  @Test
  public void getNamePartsIfValidOnValidNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.putNameAtStart(bExpr, "b");

    // When
    Optional<List<ExprToNamePair>> subExprList = extractionResult.getNamePartsIfValid();

    // Then
    Assert.assertTrue(subExprList.isPresent());
    Assert.assertEquals(2, subExprList.get().size());

    Assert.assertEquals(bExpr, subExprList.get().get(0).getExpression());
    Assert.assertEquals("b", subExprList.get().get(0).getName());

    Assert.assertEquals(aExpr, subExprList.get().get(1).getExpression());
    Assert.assertEquals("a", subExprList.get().get(1).getName());
  }

  @Test
  public void getNamePartsRawOnEmptyList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    // When
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();

    // Then
    Assert.assertTrue(subExprList.isEmpty());
  }

  @Test
  public void getNamePartsRawOnFilledList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");
    ASTNameExpression cExpr = buildNameExpression("c");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.maybeAppendInvalidExprAtStart(bExpr);
    extractionResult.putNameAtStart(cExpr, "c");

    // When
    List<ExprToOptNamePair> subExprList = extractionResult.getNamePartsRaw();

    // Then
    Assert.assertEquals(3, subExprList.size());

    Assert.assertEquals(cExpr, subExprList.get(0).getExpression());
    Assert.assertEquals(bExpr, subExprList.get(1).getExpression());
    Assert.assertEquals(aExpr, subExprList.get(2).getExpression());

    Assert.assertEquals("c", subExprList.get(0).getName().orElse("no name was inserted"));
    Assert.assertFalse(subExprList.get(1).getName().isPresent());
    Assert.assertEquals("a", subExprList.get(2).getName().orElse("no name was inserted"));
  }

  @Test
  public void getLastNameOnEmptyList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    // When
    Optional<String> lastName = extractionResult.getLastName();

    // Then
    Assert.assertFalse(lastName.isPresent());
  }

  @Test
  public void getLastNameOnAbsentName() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");

    extractionResult.maybeAppendInvalidExprAtStart(aExpr);
    extractionResult.putNameAtStart(bExpr, "b");

    // When
    Optional<String> lastName = extractionResult.getLastName();

    // Then
    Assert.assertFalse(lastName.isPresent());
  }

  @Test
  public void getLastNameOnValidLastName() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.maybeAppendInvalidExprAtStart(bExpr);

    // When
    Optional<String> lastName = extractionResult.getLastName();

    // Then
    Assert.assertTrue(lastName.isPresent());
    Assert.assertEquals("a", lastName.get());
  }

  @Test
  public void resultIsValidNameOnEmptyNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    // When
    boolean resultIsValidName = extractionResult.resultIsValidName();

    // Then
    Assert.assertFalse(resultIsValidName);
  }

  @Test
  public void resultIsValidNameOnInvalidNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();
    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");
    ASTNameExpression cExpr = buildNameExpression("c");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.maybeAppendInvalidExprAtStart(bExpr);
    extractionResult.putNameAtStart(cExpr, "c");

    // When
    boolean resultIsValidName = extractionResult.resultIsValidName();

    // Then
    Assert.assertFalse(resultIsValidName);
  }

  @Test
  public void resultIsValidOnValidNameList() {
    // Given
    SubExprNameExtractionResult extractionResult = new SubExprNameExtractionResult();

    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");

    extractionResult.putNameAtStart(aExpr, "a");
    extractionResult.putNameAtStart(bExpr, "b");

    // When
    boolean resultIsValidName = extractionResult.resultIsValidName();

    // Then
    Assert.assertTrue(resultIsValidName);
  }

  @Test
  public void testCopy() {
    // Given
    SubExprNameExtractionResult originalResult = new SubExprNameExtractionResult();
    ASTNameExpression aExpr = buildNameExpression("a");
    ASTNameExpression bExpr = buildNameExpression("b");
    ASTNameExpression cExpr = buildNameExpression("c");

    originalResult.putNameAtStart(aExpr, "a");
    originalResult.maybeAppendInvalidExprAtStart(bExpr);
    originalResult.putNameAtStart(cExpr, "c");

    // When
    SubExprNameExtractionResult copiedResult = originalResult.copy();
    originalResult.reset();
    ASTNameExpression dExpr = buildNameExpression("d");
    originalResult.putNameAtStart(dExpr, "d");

    // Then
    List<ExprToOptNamePair> originalExpressions = originalResult.getNamePartsRaw();
    List<ExprToOptNamePair> copiedSubExpressions = copiedResult.getNamePartsRaw();
    Assert.assertNotEquals(originalExpressions, copiedSubExpressions);

    Assert.assertEquals(3, copiedSubExpressions.size());
    Assert.assertEquals(cExpr, copiedSubExpressions.get(0).getExpression());
    Assert.assertEquals(bExpr, copiedSubExpressions.get(1).getExpression());
    Assert.assertEquals(aExpr, copiedSubExpressions.get(2).getExpression());

    Assert.assertEquals(1, originalExpressions.size());
    Assert.assertEquals(dExpr, originalExpressions.get(0).getExpression());
  }
}
