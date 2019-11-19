<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("sym", "symbolFullName")}
  printer.beginObject();
  // Name and kind are part of every serialized symbol
  printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND, "${symbolFullName}");
  printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME, node.getName());
  // Serialize all relevant additional attributes (introduced by symbolRules)
  serialize${sym.getName()?cap_first}(node);