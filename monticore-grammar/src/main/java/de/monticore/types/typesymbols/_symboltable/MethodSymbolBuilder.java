// (c) https://github.com/MontiCore/monticore

package de.monticore.types.typesymbols._symboltable;

import java.util.ArrayList;

/**
 * Open issue #2386
 * To be removed when solved
 *
 * TOP class avoid null paramter attribute
 */
public class MethodSymbolBuilder extends MethodSymbolBuilderTOP {
  protected MethodSymbolBuilder() {

    this.parameter = new ArrayList<>();

  }

}
