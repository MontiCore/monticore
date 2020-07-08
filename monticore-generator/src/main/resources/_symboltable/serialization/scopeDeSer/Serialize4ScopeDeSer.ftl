<#-- (c) https://github.com/MontiCore/monticore -->
  printer = new de.monticore.symboltable.serialization.JsonPrinter();
  toSerialize.accept(symbolTablePrinter);
  return printer.getContent();
