<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symboltablePrinter")}
  ${symboltablePrinter} symbolTablePrinter = new ${symboltablePrinter}();
  toSerialize.accept(symbolTablePrinter);
  return symbolTablePrinter.getSerializedString();