<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegatorVisitorFullName", "symbolTablePrinters")}
printer = new de.monticore.symboltable.serialization.JsonPrinter();
symbolTablePrinter = new ${delegatorVisitorFullName}();
<#list symbolTablePrinters?keys as p>
symbolTablePrinter.set${p}Visitor(new ${symbolTablePrinters[p]}(printer));
</#list>