/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

public class SymbolTableGeneratorBuilder {

  private ModelingLanguageGenerator modelingLanguageGenerator;
  private ModelLoaderGenerator modelLoaderGenerator;

  private SymbolGenerator symbolGenerator;
  private ScopeGenerator scopeGenerator;

  private SymbolReferenceGenerator symbolReferenceGenerator;
  private SymbolTableCreatorGenerator symbolTableCreatorGenerator;
  private SymbolInterfaceGenerator symbolInterfaceGenerator;
  private SymbolTablePrinterGenerator symbolTablePrinterGenerator;


  public SymbolTableGenerator build() {
    if (modelingLanguageGenerator == null) {
      modelingLanguageGenerator = new CommonModelingLanguageGenerator();
    }
    if (modelLoaderGenerator == null) {
      modelLoaderGenerator = new CommonModelLoaderGenerator();
    }
    if (symbolGenerator == null) {
      symbolGenerator = new CommonSymbolGenerator();
    }
    if (scopeGenerator == null) {
      scopeGenerator = new CommonScopeGenerator();
    }
    if (symbolReferenceGenerator == null) {
      symbolReferenceGenerator = new CommonSymbolReferenceGenerator();
    }
    if (symbolTableCreatorGenerator == null) {
      symbolTableCreatorGenerator = new CommonSymbolTableCreatorGenerator();
    }
    if (symbolInterfaceGenerator == null) {
      symbolInterfaceGenerator = new CommonSymbolInterfaceGenerator();
    }
    if (symbolTablePrinterGenerator == null) {
      symbolTablePrinterGenerator = new CommonSymbolTablePrinterGenerator();
    }

    return new SymbolTableGenerator(modelingLanguageGenerator, modelLoaderGenerator,
            symbolGenerator, scopeGenerator,
            symbolReferenceGenerator, symbolTableCreatorGenerator,
            symbolInterfaceGenerator, symbolTablePrinterGenerator);
  }


  public SymbolTableGeneratorBuilder modelingLanguageGenerator(ModelingLanguageGenerator modelingLanguageGenerator) {
    this.modelingLanguageGenerator = modelingLanguageGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder modelLoaderGenerator(ModelLoaderGenerator modelLoaderGenerator) {
    this.modelLoaderGenerator = modelLoaderGenerator;
    return this;
  }

  public SymbolTableGeneratorBuilder symbolGenerator(SymbolGenerator symbolGenerator) {
    this.symbolGenerator = symbolGenerator;
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
  
  public SymbolTableGeneratorBuilder symbolTablePrinterGenerator(SymbolTablePrinterGenerator symbolTablePrinterGenerator) {
    this.symbolTablePrinterGenerator = symbolTablePrinterGenerator;
    return this;
  }

}
