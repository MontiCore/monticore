/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcoptionaltypes.types3;

import de.monticore.types.mcarraytypes.types3.MCArrayTypesTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcoptionaltypestest.MCOptionalTypesTestMill;
import de.monticore.types.mcoptionaltypestest._visitor.MCOptionalTypesTestTraverser;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;

public class MCOptionalTypesTestTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    MCOptionalTypesTestTraverser traverser =
        MCOptionalTypesTestMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();

    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    MCCollectionTypesTypeVisitor visMCCollectionTypes =
        new MCCollectionTypesTypeVisitor();
    visMCCollectionTypes.setType4Ast(type4Ast);
    traverser.add4MCCollectionTypes(visMCCollectionTypes);

    MCArrayTypesTypeVisitor visMCArrayTypes = new MCArrayTypesTypeVisitor();
    visMCArrayTypes.setType4Ast(type4Ast);
    traverser.add4MCArrayTypes(visMCArrayTypes);

    MCStructuralTypesTypeVisitor visMCStructuralTypes =
        new MCStructuralTypesTypeVisitor();
    visMCStructuralTypes.setType4Ast(type4Ast);
    traverser.add4MCStructuralTypes(visMCStructuralTypes);

    MCOptionalTypesTypeVisitor visMCOptionalTypes =
        new MCOptionalTypesTypeVisitor();
    visMCOptionalTypes.setType4Ast(type4Ast);
    traverser.add4MCOptionalTypes(visMCOptionalTypes);

    new MCOptionalTypesTestTypeCheck3(traverser, type4Ast).setThisAsDelegate();
  }

  protected MCOptionalTypesTestTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast) {
    super(typeTraverser, type4Ast, new InferenceContext4Ast());
  }

}
