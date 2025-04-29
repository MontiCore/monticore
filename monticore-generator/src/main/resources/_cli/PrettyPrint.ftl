<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mill")}
  String printed = ${mill}.prettyPrint(ast, true);
  de.monticore.io.FileReaderWriter.storeInFile(java.nio.file.Paths.get(file), printed);