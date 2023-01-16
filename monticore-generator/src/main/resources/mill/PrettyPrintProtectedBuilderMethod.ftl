<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("fullPrettyPrinterClass")}
  if (fullPrettyPrinter == null) {
    fullPrettyPrinter = new ${fullPrettyPrinterClass}(new de.monticore.prettyprint.IndentPrinter(), false);
  }
  fullPrettyPrinter.setPrintComments(printComments);
  return fullPrettyPrinter.prettyprint(node);
