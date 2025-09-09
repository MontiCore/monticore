package de.monticore.types.mcbasictypeswithbasicsymbolstest.types3;

import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mcbasictypeswithbasicsymbolstest.MCBasicTypesWithBasicSymbolsTestMill;
import de.monticore.types.mcbasictypeswithbasicsymbolstest._visitor.MCBasicTypesWithBasicSymbolsTestTraverser;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;

public class MCBasicTypesWithBasicSymbolsTestTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    MCBasicTypesWithBasicSymbolsTestTraverser traverser =
        MCBasicTypesWithBasicSymbolsTestMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();

    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setWithinTypeResolver(new WithinTypeBasicSymbolsResolver());
    visMCBasicTypes.setWithinScopeResolver(new WithinScopeBasicSymbolsResolver());
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    new MCBasicTypesWithBasicSymbolsTestTypeCheck3(traverser, type4Ast)
        .setThisAsDelegate();
  }

  protected MCBasicTypesWithBasicSymbolsTestTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast) {
    super(typeTraverser, type4Ast, new InferenceContext4Ast());
  }

}
