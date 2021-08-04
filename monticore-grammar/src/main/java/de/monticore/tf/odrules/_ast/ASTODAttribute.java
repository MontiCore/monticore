/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules._ast;


import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;

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

  public String printType() {
    // lazy calculation from ast
    if (sType == null) {
      if (isPresentMCType()) {
        MCSimpleGenericTypesFullPrettyPrinter printer = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
        getMCType().accept(printer.getTraverser());
        sType = printer.getPrinter().getContent();
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

  public String printName() {
    // convenient method to get data in the same way in the templates
    return name;
  }

  public String printValue() {
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

  public String printList() {
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

  public boolean isIterated() {
    return getAttributeCardinality().isMany() || getAttributeCardinality().isOneToMany();
  }

  public boolean isOptional() {
    return getAttributeCardinality().isOptional();
  }
}
