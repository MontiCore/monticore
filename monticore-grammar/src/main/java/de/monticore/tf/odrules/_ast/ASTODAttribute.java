/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules._ast;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import javax.annotation.Nonnull;

/**
 * AST node for object diagram attributes used in transformation rules.
 */
public class ASTODAttribute extends ASTODAttributeTOP {

  protected  ASTODAttribute (){

  }

  protected  ASTODAttribute (de.monticore.types.mcbasictypes._ast.ASTMCType mCType,
                             String name,
                             ASTCardinality attributeCardinality,
                             de.monticore.expressions.expressionsbasis._ast.ASTExpression singleValue,
                             de.monticore.statements.mcarraystatements._ast.ASTArrayInit list)
  {
    super();
    setMCType(mCType);
    setName(name);
    setAttributeCardinality(attributeCardinality);
    setSingleValue(singleValue);
    setList(list);
    //super(mCType,name,singleValue,list);
  }

  private String sType;
  private String sValue;

  /**
   * Returns the printable attribute type without redundant empty generic brackets ({@code <>}).
   *
   * @return printable type, or an empty string if no type is present
   */
  public @Nonnull String printType() {
    // lazy calculation from ast
    if (sType == null) {
      if (isPresentMCType()) {
        sType = MCSimpleGenericTypesMill.prettyPrint(getMCType(), false);
        if(sType.endsWith("<>")) {
          sType = sType.substring(0, sType.length()-2);
        }
      }
      else {
        sType = "";
      }
    }
    return sType;
  }

  /**
   * Returns the attribute name.
   *
   * @return attribute name, never {@code null}
   */
  public @Nonnull String printName() {
    // convenient method to get data in the same way in the templates
    return name;
  }

  /**
   * Returns the printable single value of this attribute.
   *
   * @return pretty-printed value, or an empty string if no single value exists
   */
  public @Nonnull String printValue() {
    // lazy calculation from ast
    if (sValue == null) {
      if (isPresentSingleValue()) {

        StringBuilder stringbuilder = new StringBuilder();
        IndentPrinter iPrinter = new IndentPrinter(stringbuilder);
        TFExpressionFullPrettyPrinter p =new TFExpressionFullPrettyPrinter(iPrinter);

        p.prettyprint(getSingleValue());
        iPrinter.flushBuffer();
        sValue = stringbuilder.toString();
      }
      else {
        sValue = "";
      }
    }
    return sValue;
  }

  /**
   * Returns the printable list value of this attribute.
   *
   * @return pretty-printed list value, or an empty string if no list is present
   */
  public @Nonnull String printList() {
    String ret;
    if (isPresentList()) {

      StringBuilder stringbuilder = new StringBuilder();
      IndentPrinter iPrinter = new IndentPrinter(stringbuilder);
      TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(iPrinter);

      p.prettyprint(getList());
      iPrinter.flushBuffer();
      ret = stringbuilder.toString();
    }
    else {
      ret = "";
    }
  return ret;
  }

  /**
   * Checks whether the attribute cardinality allows multiple values.
   *
   * @return {@code true} for {@code *} or {@code 1..*}
   */
  public boolean isIterated() {
    return getAttributeCardinality().isMany() || getAttributeCardinality().isOneToMany();
  }

  /**
   * Checks whether the attribute cardinality is optional.
   *
   * @return {@code true} for optional cardinality
   */
  public boolean isOptional() {
    return getAttributeCardinality().isOptional();
  }
}
