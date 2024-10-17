/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.*;
import java.util.stream.Collectors;

public class SubExprNameExtractionResult {

  /**
   * Data structure that contains the subexpressions (usually these are the subexpressions of a single composed
   * expression). Each subexpression that represents a name is saved associated with that name. Other expressions are
   * associated with an empty optional.
   */
  protected List<ExprToOptNamePair> subExpressions = new LinkedList<>();

  public void reset() {
    this.subExpressions = new LinkedList<>();
  }

  public void setSubExpressions(List<ExprToOptNamePair> subExpressions) {
    this.subExpressions = subExpressions;
  }

  /**
   * Only adds the given expression to the start of the {@link #subExpressions} list, if it does not exist there yet.
   * The expression is added associated with an empty optional, indicating that the expression is not a valid name.
   */
  public void maybeAppendInvalidExprAtStart(ASTExpression expression) {
    ExprToOptNamePair toBeAdded = ExprToOptNamePair.of(expression, Optional.empty());

    if (subExpressions.isEmpty() || !subExpressions.get(0).getExpression().equals(expression)) {
      subExpressions.add(0, toBeAdded);
    }
  }

  /**
   * If the given expression is already at the start of the {@link #subExpressions} list, then the associated name is
   * set to be {@code name}. Else, the given expression is inserted at the start of the list, associated with
   * {@code name}.
   */
  protected void putNameAtStart(ASTExpression expression, String name) {
    ExprToOptNamePair toBeAdded = ExprToOptNamePair.of(expression, Optional.of(name));

    if(subExpressions.isEmpty() || !subExpressions.get(0).getExpression().equals(expression)) {
      subExpressions.add(0, toBeAdded);
    } else {
      subExpressions.set(0, toBeAdded);
    }
  }

  /**
   * @return If all recorded {@link #subExpressions}s represent name parts, then these name parts are returned. Every
   * name part (represented as a String) is associated with the expression that defines it. On the other side, if any
   * part of the expression is not representing a name, an empty optional is returned by this method.
   */
  public Optional<List<ExprToNamePair>> getNamePartsIfValid() {
    if (resultIsValidName()) {
      return Optional.of(
        subExpressions.stream()
          .map(oldPair -> ExprToNamePair.of(oldPair.getExpression(), oldPair.getName().get()))
          .collect(Collectors.toList())
      );
    } else {
      return Optional.empty();
    }
  }

  /** @return An unmodifiable list of {@link #subExpressions}. Expressions that represent names are associated with
   * their corresponding name. Others are associated with an empty optional.
   */
  public List<ExprToOptNamePair> getNamePartsRaw() {
    return Collections.unmodifiableList(this.subExpressions);
  }

  /**
   * @return If the last part of {@link #subExpressions} represents a name, then this method returns this name. Else, an
   * empty optional is returned.
   */
  public Optional<String> getLastName() {
    if(subExpressions.isEmpty()) {
      return Optional.empty();
    } else {
      return subExpressions.get(subExpressions.size() - 1).getName();
    }
  }

  /**
   * @return Whether all {@link #subExpressions} represent names.
   */
  public boolean resultIsValidName() {
    return !subExpressions.isEmpty() && subExpressions.stream().allMatch(p -> p.getName().isPresent());
  }

  /**
   * @return A new instance with a unmodifiable {@link #subExpressions} list.
   */
  public SubExprNameExtractionResult copy() {
    SubExprNameExtractionResult copy = new SubExprNameExtractionResult();
    copy.setSubExpressions(new ArrayList<>(this.getNamePartsRaw()));

    return copy;
  }
}
