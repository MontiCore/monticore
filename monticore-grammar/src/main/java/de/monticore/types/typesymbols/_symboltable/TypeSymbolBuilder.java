// (c) https://github.com/MontiCore/monticore

// (c) https://github.com/MontiCore/monticore

package de.monticore.types.typesymbols._symboltable;

import java.util.ArrayList;

/**
 * Open issue #2386
 * To be removed when solved
 *
 * TOP class avoid null field attribute
 */
public class TypeSymbolBuilder extends TypeSymbolBuilderTOP {
  protected TypeSymbolBuilder() {

    this.fields = new ArrayList<>();
    this.superTypes = new ArrayList<>();
    this.methods = new ArrayList<>();
    this.typeParameters = new ArrayList<>();

  }

}
