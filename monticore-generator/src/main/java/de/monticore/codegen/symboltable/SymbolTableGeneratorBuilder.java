/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.symboltable;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class SymbolTableGeneratorBuilder {

  private ModelingLanguageGenerator modelingLanguageGenerator;
  private ModelLoaderGenerator modelLoaderGenerator;
  private ModelNameCalculatorGenerator modelNameCalculatorGenerator;
  private ResolvingFilterGenerator resolvingFilterGenerator;

  private SymbolGenerator symbolGenerator;
  private SymbolKindGenerator symbolKindGenerator;
  private ScopeSpanningSymbolGenerator scopeSpanningSymbolGenerator;

  private SymbolReferenceGenerator symbolReferenceGenerator;
  private SymbolTableCreatorGenerator symbolTableCreatorGenerator;

  public SymbolTableGenerator build() {
    if (modelingLanguageGenerator == null) {
      modelingLanguageGenerator = new CommonModelingLanguageGenerator();
    }
    if (modelLoaderGenerator == null) {
      modelLoaderGenerator = new CommonModelLoaderGenerator();
    }
    if (modelNameCalculatorGenerator == null) {
      modelNameCalculatorGenerator = new CommonModelNameCalculatorGenerator();
    }
    if (resolvingFilterGenerator == null) {
      resolvingFilterGenerator = new CommonResolvingFilterGenerator();
    }
    if (symbolGenerator == null) {
      symbolGenerator = new CommonSymbolGenerator();
    }
    if (symbolKindGenerator == null) {
      symbolKindGenerator = new CommonSymbolKindGenerator();
    }
    if (scopeSpanningSymbolGenerator == null) {
      scopeSpanningSymbolGenerator = new CommonScopeSpanningSymbolGenerator();
    }
    if (symbolReferenceGenerator == null) {
      symbolReferenceGenerator = new CommonSymbolReferenceGenerator();
    }
    if ( symbolTableCreatorGenerator == null) {
      symbolTableCreatorGenerator = new CommonSymbolTableCreatorGenerator();
    }

    return new SymbolTableGenerator(modelingLanguageGenerator, modelLoaderGenerator,
        modelNameCalculatorGenerator, resolvingFilterGenerator, symbolGenerator,
        symbolKindGenerator, scopeSpanningSymbolGenerator, symbolReferenceGenerator,
        symbolTableCreatorGenerator);
  }


  public SymbolTableGeneratorBuilder modelingLanguageGenerator(ModelingLanguageGenerator modelingLanguageGenerator) {
    this.modelingLanguageGenerator = modelingLanguageGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder modelLoaderGenerator(ModelLoaderGenerator modelLoaderGenerator) {
    this.modelLoaderGenerator = modelLoaderGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder modelNameCalculatorGenerator(ModelNameCalculatorGenerator modelNameCalculatorGenerator) {
    this.modelNameCalculatorGenerator = modelNameCalculatorGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder resolvingFilterGenerator(ResolvingFilterGenerator resolvingFilterGenerator) {
    this.resolvingFilterGenerator = resolvingFilterGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder symbolGenerator(SymbolGenerator symbolGenerator) {
    this.symbolGenerator = symbolGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder symbolKindGenerator(SymbolKindGenerator symbolKindGenerator) {
    this.symbolKindGenerator = symbolKindGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder scopeSpanningSymbolGenerator(ScopeSpanningSymbolGenerator scopeSpanningSymbolGenerator) {
    this.scopeSpanningSymbolGenerator = scopeSpanningSymbolGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder symbolReferenceGenerator(SymbolReferenceGenerator symbolReferenceGenerator) {
    this.symbolReferenceGenerator = symbolReferenceGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder symbolTableCreatorGenerator(SymbolTableCreatorGenerator symbolTableCreatorGenerator) {
    this.symbolTableCreatorGenerator = symbolTableCreatorGenerator;
    return this;
  }

}
