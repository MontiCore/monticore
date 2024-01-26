// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._util.CommonExpressionsTypeDispatcher;
import de.monticore.expressions.commonexpressions._util.ICommonExpressionsTypeDispatcher;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the type of expressions and type identifiers,
 * as such they do not need to be calculated again.
 * get the type of an expression (e.g., "1+2") using
 * {@link Type4Ast#getTypeOfExpression}.
 * get the type of a type identifier (e.g., "int") using
 * {@link Type4Ast#getTypeOfTypeIdentifier}.
 * The getters marked with "Partial" and the setters
 * are used by the type traverser filling the map.
 */
public class Type4Ast {

  protected static final String LOG_NAME = "Type4Ast";

  /**
   * the actual map from expression to types,
   * strictly seperated from the map for type identifiers
   * we use ASTNode to support non-ASTExpression Nodes (e.g., literals)
   * however, we do NOT support non-expression ASTNodes,
   * e.g. in MyClass.myMethod() -> the "MyClass" is not an expression by itself
   */
  protected Map<ASTNode, SymTypeExpression> expr2type;

  protected Map<ASTNode, SymTypeExpression> getExpression2Type() {
    return expr2type;
  }

  /**
   * the actual map from type identifier to types,
   * strictly seperated from the map for expressions
   * we use ASTNode to support non-ASTMCType Nodes (e.g. qualified Names)
   * however, we do NOT support expression ASTNodes,
   * e.g. other qualified names that represent a variable, rather than a type
   */
  protected Map<ASTNode, SymTypeExpression> typeID2type;

  protected Map<ASTNode, SymTypeExpression> getTypeIdentifier2Type() {
    return typeID2type;
  }

  public Type4Ast() {
    reset();
  }

  public void reset() {
    expr2type = new HashMap<>();
    typeID2type = new HashMap<>();
  }

  /**
   * whether a type has been calculated for the expression
   */
  public boolean hasTypeOfExpression(ASTExpression astExpr) {
    return internal_hasTypeOfExpression((ASTNode) astExpr);
  }

  /**
   * whether a type has been calculated for the literal
   */
  public boolean hasTypeOfExpression(ASTLiteral astLit) {
    return internal_hasTypeOfExpression((ASTNode) astLit);
  }

  protected boolean internal_hasTypeOfExpression(ASTNode node) {
    if (!getExpression2Type().containsKey(node)) {
      return false;
    }
    return !getExpression2Type().get(node).isObscureType();
  }

  public boolean hasTypeOfTypeIdentifier(ASTMCType mcType) {
    return internal_hasTypeOfTypeIdentifier((ASTNode) mcType);
  }

  public boolean hasTypeOfTypeIdentifier(ASTMCReturnType mcReturnType) {
    return internal_hasTypeOfTypeIdentifier((ASTNode) mcReturnType);
  }

  public boolean hasTypeOfTypeIdentifier(ASTMCQualifiedName mcQName) {
    return internal_hasTypeOfTypeIdentifier((ASTNode) mcQName);
  }

  public boolean hasTypeOfTypeIdentifier(ASTMCTypeArgument mcTypeArg) {
    return internal_hasTypeOfTypeIdentifier((ASTNode) mcTypeArg);
  }

  public boolean hasTypeOfTypeIdentifierForName(ASTExpression nameExpr) {
    if (!isQNameExpr(nameExpr)) {
      Log.error("0xFD4B4 internal error: "
              + "expected a qualified name, "
              + "this is not an issue with the model, "
              + "the wrong internal method was called.",
          nameExpr.get_SourcePositionStart(),
          nameExpr.get_SourcePositionEnd()
      );
    }
    return internal_hasTypeOfTypeIdentifier((ASTNode) nameExpr);
  }

  /**
   * whether a type has been calculated for the type identifier
   */
  protected boolean internal_hasTypeOfTypeIdentifier(ASTNode node) {
    if (!getTypeIdentifier2Type().containsKey(node)) {
      return false;
    }
    return !getTypeIdentifier2Type().get(node).isObscureType();
  }

  /**
   * gets the type information of the expression
   */
  public SymTypeExpression getTypeOfExpression(ASTExpression astExpr) {
    return internal_getTypeOfExpression((ASTNode) astExpr);
  }

  /**
   * gets the type information of the literal
   */
  public SymTypeExpression getTypeOfExpression(ASTLiteral astLit) {
    return internal_getTypeOfExpression((ASTNode) astLit);
  }

  protected SymTypeExpression internal_getTypeOfExpression(ASTNode node) {
    if (internal_hasTypeOfExpression(node)) {
      return getExpression2Type().get(node);
    }
    Log.error("0xFD791 type of expression unknown but requested",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
    return SymTypeExpressionFactory.createObscureType();
  }

  public SymTypeExpression getPartialTypeOfExpr(ASTExpression astExpr) {
    return internal_getPartialTypeOfExpr((ASTNode) astExpr);
  }

  public SymTypeExpression getPartialTypeOfExpr(ASTLiteral astLit) {
    return internal_getPartialTypeOfExpr((ASTNode) astLit);
  }

  /**
   * returns potentially partial type information of the expression
   * internally used by type deriver
   */
  protected SymTypeExpression internal_getPartialTypeOfExpr(ASTNode node) {
    if (!getExpression2Type().containsKey(node)) {
      Log.error("0x7C001 internal error: type information expected"
              + " but not present.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return SymTypeExpressionFactory.createObscureType();
    }
    return getExpression2Type().get(node);
  }

  public SymTypeExpression getTypeOfTypeIdentifier(ASTMCType mcType) {
    return internal_getTypeOfTypeIdentifier((ASTNode) mcType);
  }

  public SymTypeExpression getTypeOfTypeIdentifier(ASTMCReturnType mcReturnType) {
    return internal_getTypeOfTypeIdentifier((ASTNode) mcReturnType);
  }

  public SymTypeExpression getTypeOfTypeIdentifier(ASTMCQualifiedName qName) {
    return internal_getTypeOfTypeIdentifier((ASTNode) qName);
  }

  public SymTypeExpression getTypeOfTypeIdentifier(ASTMCTypeArgument typeArg) {
    return internal_getTypeOfTypeIdentifier((ASTNode) typeArg);
  }

  /**
   * gets the type of the type identifier if it has been calculated
   */
  protected SymTypeExpression internal_getTypeOfTypeIdentifier(ASTNode node) {
    if (internal_hasTypeOfTypeIdentifier(node)) {
      return getTypeIdentifier2Type().get(node);
    }
    Log.error("0xFD792 type of type identifier unknown but requested",
        node.get_SourcePositionStart(),
        node.get_SourcePositionEnd()
    );
    return SymTypeExpressionFactory.createObscureType();
  }

  public SymTypeExpression getPartialTypeOfTypeId(ASTMCType mcType) {
    return internal_getPartialTypeOfTypeId((ASTNode) mcType);
  }

  public SymTypeExpression getPartialTypeOfTypeId(ASTMCReturnType mcReturnType) {
    return internal_getPartialTypeOfTypeId((ASTNode) mcReturnType);
  }

  public SymTypeExpression getPartialTypeOfTypeId(ASTMCQualifiedName qName) {
    return internal_getPartialTypeOfTypeId((ASTNode) qName);
  }

  public SymTypeExpression getPartialTypeOfTypeId(ASTMCTypeArgument typeArg) {
    return internal_getPartialTypeOfTypeId((ASTNode) typeArg);
  }

  /**
   * A special case for specific MCQualifiedNames
   * s. {@link Type4Ast#setTypeOfTypeIdentifierForName(
   *ASTFieldAccessExpression, SymTypeExpression)}
   */
  public SymTypeExpression getPartialTypeOfTypeIdForName(ASTExpression nameExpr) {
    if (!isQNameExpr(nameExpr)) {
      Log.error("0xFD4B5 internal error: "
              + "expected a qualified name, "
              + "this is not an issue with the model, "
              + "the wrong internal method was called",
          nameExpr.get_SourcePositionStart(),
          nameExpr.get_SourcePositionEnd()
      );
    }
    return internal_getPartialTypeOfTypeId((ASTNode) nameExpr);
  }

  /**
   * @deprecated do not use, only here until fix in grammar
   */
  @Deprecated
  public SymTypeExpression internal_getPartialTypeOfTypeId2(ASTNode node) {
    return internal_getPartialTypeOfTypeId(node);
  }

  /**
   * returns potentially partial type information of the type identifier
   * used by type deriver
   */
  protected SymTypeExpression internal_getPartialTypeOfTypeId(ASTNode node) {
    if (!getTypeIdentifier2Type().containsKey(node)) {
      Log.error("0xFD799 internal error: type information expected"
              + " but not present.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
      return SymTypeExpressionFactory.createObscureType();
    }
    return getTypeIdentifier2Type().get(node);
  }

  /**
   * sets the type information of the expression,
   * information may be partial
   */
  public void setTypeOfExpression(
      ASTExpression astExpr,
      SymTypeExpression typeExpr
  ) {
    internal_setTypeOfExpression((ASTNode) astExpr, typeExpr);
  }

  /**
   * sets the type information of the literal,
   * information may be partial
   */
  public void setTypeOfExpression(
      ASTLiteral astLit,
      SymTypeExpression typeExpr
  ) {
    internal_setTypeOfExpression((ASTNode) astLit, typeExpr);
  }

  protected void internal_setTypeOfExpression(
      ASTNode node,
      SymTypeExpression typeExpr
  ) {
    if (internal_hasTypeOfExpression(node)) {
      Log.trace(node2InfoString(node)
              + ": had the expression type "
              + internal_getTypeOfExpression(node).printFullName(),
          LOG_NAME
      );
    }
    Log.trace(node2InfoString(node)
            + ": expression type is "
            + typeExpr.printFullName(),
        LOG_NAME
    );
    getExpression2Type().put(node, typeExpr);
  }

  public void setTypeOfTypeIdentifier(
      ASTMCType mcType,
      SymTypeExpression type
  ) {
    internal_setTypeOfTypeIdentifier((ASTNode) mcType, type);
  }

  public void setTypeOfTypeIdentifier(
      ASTMCReturnType mcReturnType,
      SymTypeExpression type
  ) {
    internal_setTypeOfTypeIdentifier((ASTNode) mcReturnType, type);
  }

  public void setTypeOfTypeIdentifier(
      ASTMCQualifiedName qName,
      SymTypeExpression type
  ) {
    internal_setTypeOfTypeIdentifier((ASTNode) qName, type);
  }

  /**
   * a special case of the MCQualifiedName
   * s. {@link Type4Ast#setTypeOfTypeIdentifierForName(
   *ASTNameExpression, SymTypeExpression)}
   */
  public void setTypeOfTypeIdentifierForName(
      ASTFieldAccessExpression qName,
      SymTypeExpression type) {
    internal_setTypeOfTypeIdentifier((ASTNode) qName, type);
  }

  /**
   * a special case of the MCQualifiedName
   * <p>
   * this is only used in cases there the type identifier
   * 1. was parsed as an expression (instead of a MCQualifiedName) and
   * 2. the AST can not be transformed to accommodate without
   * non-conservative grammar changes
   * <p>
   * In our Java-esque languages, this usually happens before ".",
   * e.g., C in C.staticVar, C.staticMethod() C.this.var, C.super.method(), ...
   * As the type id has been parsed as an expression,
   * and nothing further is known without reflection,
   * getting the type information cannot be type safe
   * ("type safe" as in (partially) ensuring correct usage by the type checker),
   * thus the uniqueness of the methods' names.
   * note that (technically) not even ASTExpression applicable enough,
   * due to some grammar extension points
   * The getter is {@link Type4Ast#getPartialTypeOfTypeIdForName(ASTExpression)}
   */
  public void setTypeOfTypeIdentifierForName(
      ASTNameExpression name,
      SymTypeExpression type) {
    internal_setTypeOfTypeIdentifier((ASTNode) name, type);
  }

  public void setTypeOfTypeIdentifier(
      ASTMCTypeArgument typeArg,
      SymTypeExpression type) {
    internal_setTypeOfTypeIdentifier((ASTNode) typeArg, type);
  }

  /**
   * @deprecated do not use, remove after fix of grammars
   */
  @Deprecated
  public void internal_setTypeOfTypeIdentifier2(
      ASTNode node,
      SymTypeExpression type) {
    internal_setTypeOfTypeIdentifier(node, type);
  }

  /**
   * sets the type information of the type identifier,
   * information may be partial
   */
  protected void internal_setTypeOfTypeIdentifier(
      ASTNode node,
      SymTypeExpression typeExpr) {
    if (internal_hasTypeOfTypeIdentifier(node)) {
      Log.trace(node2InfoString(node)
              + ": had the type id "
              + internal_getTypeOfTypeIdentifier(node).printFullName(),
          LOG_NAME
      );
    }
    Log.trace(node2InfoString(node)
            + ": type id is "
            + typeExpr.printFullName(),
        LOG_NAME
    );
    getTypeIdentifier2Type().put(node, typeExpr);
  }

  // Helper

  /**
   * whether the expression represents a qualified name
   */
  protected boolean isQNameExpr(ASTExpression expr) {
    ICommonExpressionsTypeDispatcher typeDispatcher =
        CommonExpressionsMill.typeDispatcher();
    if (typeDispatcher.isASTNameExpression(expr)) {
      return true;
    }
    else if (typeDispatcher.isASTFieldAccessExpression(expr)) {
      return isQNameExpr(
          typeDispatcher.asASTFieldAccessExpression(expr).getExpression()
      );
    }
    else {
      return false;
    }
  }

  /**
   * helps with logging.
   */
  protected String node2InfoString(ASTNode node) {
    // may be moved from here, if required somewhere else as well
    // as multiple expressions can start at the same position,
    // we need to know the end position as well
    // even better would be access to the model source the positions refer to...
    // based on SourcePosition::toString
    StringBuilder result = new StringBuilder();
    if (node.isPresent_SourcePositionStart() &&
        node.isPresent_SourcePositionEnd()) {
      SourcePosition startPos = node.get_SourcePositionStart();
      SourcePosition endPos = node.get_SourcePositionEnd();
      if (startPos.getFileName().isPresent()) {
        result.append(FilenameUtils.getName(startPos.getFileName().get()));
        result.append(":");
      }
      result.append("<" + startPos.getLine() + "," + startPos.getColumn() + ">");
      result.append("-");
      result.append("<" + endPos.getLine() + "," + endPos.getColumn() + ">");
    }
    else {
      result.append("unknown position");
    }
    return result.toString();
  }
}
