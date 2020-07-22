// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;

import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;

/**
 * Visitor for Derivation of SymType from MCSimpleGenericTypes
 * i.e. for
 * types/MCSimpleGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCSimpleGenericTypes extends SynthesizeSymTypeFromMCCollectionTypes
    implements MCSimpleGenericTypesVisitor, ISynthesize {

  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  public SynthesizeSymTypeFromMCSimpleGenericTypes(){
    super();
  }

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCSimpleGenericTypesVisitor realThis = this;

  @Override
  public void setRealThis(MCSimpleGenericTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }

  @Override
  public MCSimpleGenericTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end

  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void traverse(ASTMCBasicGenericType genericType) {

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (ASTMCTypeArgument arg : genericType.getMCTypeArgumentsList()) {
      if (null != arg) {
        arg.accept(getRealThis());
      }

      if (!typeCheckResult.isPresentCurrentResult()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
            + " Probably TypeCheck mis-configured.");
      }
      arguments.add(typeCheckResult.getCurrentResult());
    }
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(genericType.printWithoutTypeArguments());
    loader.setEnclosingScope(getScope(genericType.getEnclosingScope()));
    SymTypeExpression tex = SymTypeExpressionFactory.createGenerics(
        loader, arguments);
    typeCheckResult.setCurrentResult(tex);
  }

  /**
   * There are several forms of qualified Types possible:
   * ** Object-types
   * ** Boxed primitives, such as "java.lang.Boolean"
   * ** Type Variables
   * Primitives, like "int", void, null are not possible here.
   * This are the qualified Types that may occur.
   * <p>
   * To distinguish these kinds, we use the symbol that the ASTMCQualifiedType identifies
   *
   * @param qType
   */
  @Override
  public void endVisit(ASTMCQualifiedType qType) {

    // TODO TODO ! This implementation is incomplete, it does only create Object-Types, but the
    // type could also be a boxed Primitive or an Type Variable!
    // We need the SymbolTable to distinguish this stuff
    // PS: that also applies to other Visitors.
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()));
    loader.setEnclosingScope(getScope(qType.getEnclosingScope()));
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeObject(loader));
  }

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(qName.getQName());
    loader.setEnclosingScope(getScope(qName.getEnclosingScope()));
    SymTypeOfObject oType = createTypeObject(loader);
    typeCheckResult.setCurrentResult(oType);
  }

}
