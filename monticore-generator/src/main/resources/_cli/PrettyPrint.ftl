<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millName")}
  String printed = ${millName}.prettyPrint(ast, true);
  de.monticore.io.FileReaderWriter.storeInFile(java.nio.file.Paths.get(file), printed);