/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements._symboltable;

import de.monticore.statements.mccommonstatements._ast.ASTFormalParameter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

import java.util.Deque;

@Deprecated
public   class MCCommonStatementsSymbolTableCreator extends MCCommonStatementsSymbolTableCreatorTOP {

  public MCCommonStatementsSymbolTableCreator() {
  }

  public MCCommonStatementsSymbolTableCreator(
      IMCCommonStatementsScope enclosingScope) {
    super(enclosingScope);
  }

  public MCCommonStatementsSymbolTableCreator(
      Deque<? extends IMCCommonStatementsScope> scopeStack) {
    super(scopeStack);
  }

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
    traverser.addMCFullGenericTypesVisitor(synFromFull);
    traverser.setMCFullGenericTypesHandler(synFromFull);
    traverser.addMCSimpleGenericTypesVisitor(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.addMCCollectionTypesVisitor(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.addMCBasicTypesVisitor(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);
    return traverser;
  }

}
