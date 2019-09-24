<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolFullName")}
  printer.beginObject();
  printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND, "${symbolFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME, node.getName());