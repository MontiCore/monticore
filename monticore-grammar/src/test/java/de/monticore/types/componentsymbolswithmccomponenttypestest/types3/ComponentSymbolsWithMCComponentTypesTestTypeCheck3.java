/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.componentsymbolswithmccomponenttypestest.types3;

import de.monticore.types.componentsymbolswithmccomponenttypestest.ComponentSymbolsWithMCComponentTypesTestMill;
import de.monticore.types.componentsymbolswithmccomponenttypestest._visitor.ComponentSymbolsWithMCComponentTypesTestTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mccomponenttypes.types3.CompType4Ast;
import de.monticore.types.mccomponenttypes.types3.MCComponentTypesTypeCheck3;
import de.monticore.types.mccomponenttypes.types3.MCComponentTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.visitor.ITraverser;

public class ComponentSymbolsWithMCComponentTypesTestTypeCheck3
    extends MCComponentTypesTypeCheck3 {

  public static void init() {
    ComponentSymbolsWithMCComponentTypesTestTraverser traverser =
        ComponentSymbolsWithMCComponentTypesTestMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();
    CompType4Ast compType4Ast = new CompType4Ast();

    MCBasicTypesTypeVisitor basicTypes = new MCBasicTypesTypeVisitor();
    basicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(basicTypes);

    MCCollectionTypesTypeVisitor collectionTypes =
        new MCCollectionTypesTypeVisitor();
    collectionTypes.setType4Ast(type4Ast);
    traverser.add4MCCollectionTypes(collectionTypes);

    MCSimpleGenericTypesTypeVisitor genericTypes =
        new MCSimpleGenericTypesTypeVisitor();
    genericTypes.setType4Ast(type4Ast);
    traverser.add4MCSimpleGenericTypes(genericTypes);

    MCComponentTypesTypeVisitor componentTypes =
        new MCComponentTypesTypeVisitor();
    componentTypes.setType4Ast(type4Ast);
    componentTypes.setCompType4Ast(compType4Ast);
    traverser.add4MCComponentTypes(componentTypes);
    traverser.setMCComponentTypesHandler(componentTypes);

    new ComponentSymbolsWithMCComponentTypesTestTypeCheck3(
        traverser, type4Ast, compType4Ast).setThisAsDelegate();
  }

  protected ComponentSymbolsWithMCComponentTypesTestTypeCheck3(
      ITraverser traverser,
      Type4Ast type4Ast,
      CompType4Ast compType4Ast) {
    super(traverser, type4Ast, new InferenceContext4Ast(), compType4Ast);
  }
}
