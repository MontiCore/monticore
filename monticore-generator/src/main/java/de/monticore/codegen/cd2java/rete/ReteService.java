/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;

import static de.monticore.codegen.cd2java.rete.ReteConstants.FEATURES_SUFFIX;
import static de.monticore.codegen.cd2java.rete.ReteConstants.RETE_PACKAGE;

public class ReteService extends AbstractService<ReteService> {

  public ReteService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ReteService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return RETE_PACKAGE;
  }

  @Override
  protected ReteService createService(DiagramSymbol cdSymbol) {
    return createReteService(cdSymbol);
  }

  public static ReteService createReteService(DiagramSymbol cdSymbol) {
    return new ReteService(cdSymbol);
  }

  /**
   * @return the simple name of the class containing the features of the language,
   * e.g. {@code AutomataReteFeatures}
   */
  public String getFeaturesSimpleName() {
    return getCDName() + FEATURES_SUFFIX;
  }

  public String getFeaturesFullName() {
    return getFeaturesFullName(getCDSymbol());
  }

  /**
   * @return the full name of the class containing the features of the given
   * (e.g. super) language
   */
  public String getFeaturesFullName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), cdSymbol.getName() + FEATURES_SUFFIX);
  }
}
