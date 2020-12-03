<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegatorVisitorFullName", "symbolTablePrinters")}
  de.monticore.symboltable.serialization.JsonPrinter printer = new de.monticore.symboltable.serialization.JsonPrinter();
  ${delegatorVisitorFullName} symbolTablePrinter = new ${delegatorVisitorFullName}();
<#list symbolTablePrinters?keys as p>
  symbolTablePrinter.set${p}Visitor(new ${symbolTablePrinters[p]}(printer));
</#list>
  toSerialize.accept(symbolTablePrinter);
  return printer.getContent();
