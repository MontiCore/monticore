// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;

/**
 * Whether an expression is an LValue,
 * s. {@link #isLValue(ASTExpression)}.
 * <p>
 * There is no (non-trivial) default implementation,
 * as this tends to be based on the ASTExpression in question.
 * In most cases, you want to use
 * {@link de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations}.
 */
public class LValueRelations {

  protected static LValueRelations delegate;

  // methods

  /**
   * Tests whether an expression can be considered an LValue
   * for corresponding assignment CoCos.
   * Expressions have a type and a value category.
   * In our type systems values can be categorised by whether they have
   * an identity, i.e., an address in memory, and thus can be assigned to.
   * Values that can be assigned to are lvalues,
   * examples include variables, e.g.,
   * int i = 0; // i is a variable and thus a lvalue
   * int[] is = new int[3]; is[0] = 0; // is[0] is a variable and thus a lvalue
   * Note, not every lvalue can be assigned to,
   * e.g., a final variable that has been assigned to already.
   */
  public static boolean isLValue(ASTExpression expr) {
    return getDelegate()._isLValue(expr);
  }

  protected boolean _isLValue(ASTExpression expr) {
    // per default false,
    // each language (component) has to specify which elements are LValues.
    return false;
  }

  // static delegate

  public static void init() {
    Log.trace("init default LValueRelations"
            + ", (this is most likely not what you want to initialize)",
        "TypeCheck setup"
    );
    setDelegate(new LValueRelations());
  }

  public static void reset() {
    LValueRelations.delegate = null;
  }

  protected static void setDelegate(LValueRelations newDelegate) {
    LValueRelations.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static LValueRelations getDelegate() {
    if (LValueRelations.delegate == null) {
      init();
    }
    return LValueRelations.delegate;
  }

}