<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superGrammars")}

  Collection<String> allLanguages = new LinkedHashSet<>();
  <#list superGrammars as superGrammar>
    allLanguages.addAll(${superGrammar.getFullName()?lower_case}._ast.ASTConstants${superGrammar.getName()}.getAllLanguages());
   </#list>
  allLanguages.add(LANGUAGE);
  return allLanguages;