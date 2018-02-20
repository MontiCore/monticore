/* (c) https://github.com/MontiCore/monticore */

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

  private SymbolMillGenerator symbolMillGenerator;

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
    if (symbolMillGenerator == null) {
      symbolMillGenerator = new CommonSymbolMillGenerator();
    }

    return new SymbolTableGenerator(modelingLanguageGenerator, modelLoaderGenerator,
        modelNameCalculatorGenerator, resolvingFilterGenerator, symbolGenerator,
        symbolKindGenerator, scopeSpanningSymbolGenerator, symbolReferenceGenerator,
        symbolTableCreatorGenerator, symbolMillGenerator);
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

  public SymbolTableGeneratorBuilder symbolMillGenerator(SymbolMillGenerator symbolMillGenerator) {
    this.symbolMillGenerator = symbolMillGenerator;
    return this;
  }

}
