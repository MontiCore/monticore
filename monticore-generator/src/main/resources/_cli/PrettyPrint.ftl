<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("fullPrettyPrinter")}
  ${fullPrettyPrinter} prettyPrinter = new ${fullPrettyPrinter}(new de.monticore.prettyprint.IndentPrinter());
  String printed = prettyPrinter.prettyprint(ast);
  de.monticore.io.FileReaderWriter.storeInFile(java.nio.file.Paths.get(file), printed);