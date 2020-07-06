<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("artifactScopeSimpleName", "symbolTablePrinters")}
printer = new de.monticore.symboltable.serialization.JsonPrinter();
symbolTablePrinter = new ${delegatorVisitorFullnName}();
symbolTablePrinter.setAutomataVisitor(new automata._symboltable.AutomataSymbolTablePrinter(printer));
<#list symbolTablePrinters?keys as p>
symbolTablePrinter.set${p}Visitor(new ${symbolTablePrinters[p]}(printer));
</#list>