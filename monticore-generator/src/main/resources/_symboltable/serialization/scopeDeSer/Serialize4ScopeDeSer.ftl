<#-- (c) https://github.com/MontiCore/monticore -->
  printer.clearBuffer();
  toSerialize.accept(symbolTablePrinter);
  return printer.getContent();
