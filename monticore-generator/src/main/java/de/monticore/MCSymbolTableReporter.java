/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/ 
 */
package de.monticore;

import java.security.GeneralSecurityException;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter;
import de.monticore.languages.grammar.MCAttributeSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.symboltable.Symbol;


/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$, $Date$
 * @since   TODO: add version number
 *
 */
public class MCSymbolTableReporter extends SymbolTableReporter {

  /**
   * Constructor for de.monticore.MCSymbolTableReporter.
   * @param outputDir
   * @param modelName
   * @param repository
   */
  public MCSymbolTableReporter(String outputDir, String modelName, ReportingRepository repository) {
    super(outputDir, modelName, repository);
  }

  protected void reportAttributes(MCTypeSymbol sym) {
    // TODO Auto-generated method stub
    super.reportAttributes(sym);
    writeLine(getIndent() + "isAbstract = " + sym.isAbstract() + ";");
    writeLine(getIndent() + "isExternal = " + sym.isExternal() + ";");
  }

  protected void reportAttributes(MCAttributeSymbol sym) {
    // TODO Auto-generated method stub
    super.reportAttributes(sym);
    writeLine(getIndent() + "isDerived = " + sym.isDerived() + ";");
    writeLine(getIndent() + "isOrdered  = " + sym.isUnordered()+ ";");
  }

  /**
   * @see de.monticore.generating.templateengine.reporting.reporter.SymbolTableReporter#reportAttributes(de.monticore.symboltable.Symbol)
   */
  @Override
  protected void reportAttributes(Symbol sym) {
    // TODO Auto-generated method stub
    if (sym instanceof MCTypeSymbol) {
      reportAttributes((MCTypeSymbol) sym);
    } else if (sym instanceof MCAttributeSymbol) {
      reportAttributes((MCAttributeSymbol) sym); 
    }
  }
}
