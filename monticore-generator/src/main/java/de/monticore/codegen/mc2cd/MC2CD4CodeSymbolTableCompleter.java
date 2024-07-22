/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._visitor.CD4CodeTraverser;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCSGT4Grammar;

public class MC2CD4CodeSymbolTableCompleter {
  protected CD4CodeTraverser traverser;


  public MC2CD4CodeSymbolTableCompleter() {
    this.traverser = CD4CodeMill.traverser();

    //New!
    FullSynthesizeFromMCSGT4Grammar synthesize = new FullSynthesizeFromMCSGT4Grammar();

    final CDBasisSymbolTableCompleter cDBasisVisitor =
        new MC2CDBasisSymbolTableCompleter(synthesize);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor =
        new CDAssociationSymbolTableCompleter(synthesize);
    traverser.add4CDAssociation(cDAssociationVisitor);
    traverser.setCDAssociationHandler(cDAssociationVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor =
        new CDInterfaceAndEnumSymbolTableCompleter(synthesize);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor =
        new CD4CodeBasisSymbolTableCompleter(synthesize);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
    traverser.add4CDBasis(cd4CodeBasisVisitor);
  }

  public CD4CodeTraverser getTraverser() {
    return traverser;
  }
}
