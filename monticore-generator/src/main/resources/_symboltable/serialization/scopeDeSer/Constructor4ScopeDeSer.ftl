<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegatorVisitorFullName", "symbolTablePrinters")}
printer = new de.monticore.symboltable.serialization.JsonPrinter();
symbolTablePrinter = new ${delegatorVisitorFullName}();
symbolTablePrinter.setAutomataVisitor(new automata._symboltable.AutomataSymbolTablePrinter(printer));
<#list symbolTablePrinters?keys as p>
symbolTablePrinter.set${p}Visitor(new ${symbolTablePrinters[p]}(printer));
</#list>