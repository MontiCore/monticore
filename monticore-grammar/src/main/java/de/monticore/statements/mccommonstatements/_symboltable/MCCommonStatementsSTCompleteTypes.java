package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsVisitor;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

public class MCCommonStatementsSTCompleteTypes implements MCCommonStatementsVisitor2 {

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(getSynthesizer(synFromFull));
    return synFromFull.getResult().orElse(new SymTypeOfNull());
  }

  private MCFullGenericTypesTraverser getSynthesizer(SynthesizeSymTypeFromMCFullGenericTypes synFromFull){
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();

    MCFullGenericTypesTraverser traverser = MCFullGenericTypesMill.traverser();
    traverser.add4MCFullGenericTypes(synFromFull);
    traverser.setMCFullGenericTypesHandler(synFromFull);
    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
    return traverser;
  }


}
