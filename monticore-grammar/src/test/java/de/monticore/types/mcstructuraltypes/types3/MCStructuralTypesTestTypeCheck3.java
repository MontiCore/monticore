/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcstructuraltypes.types3;

import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mcstructuraltypes.MCStructuralTypesMill;
import de.monticore.types.mcstructuraltypes._visitor.MCStructuralTypesTraverser;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;

public class MCStructuralTypesTestTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    MCStructuralTypesTraverser traverser = MCStructuralTypesMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();

    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    MCStructuralTypesTypeVisitor visMCStructuralTypes = new MCStructuralTypesTypeVisitor();
    visMCStructuralTypes.setType4Ast(type4Ast);
    traverser.add4MCStructuralTypes(visMCStructuralTypes);

    new MCStructuralTypesTestTypeCheck3(traverser, type4Ast).setThisAsDelegate();
  }

  protected MCStructuralTypesTestTypeCheck3(ITraverser typeTraverser, Type4Ast type4Ast) {
    super(typeTraverser, type4Ast, new InferenceContext4Ast());
  }

}