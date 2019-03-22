  Collection<String> allLanguages = com.google.common.collect.Sets.newLinkedHashSet();
  allLanguages.addAll(de.monticore.lexicals.lexicals._ast.ASTConstantsLexicals.getAllLanguages());
  allLanguages.add(LANGUAGE);
  return allLanguages;