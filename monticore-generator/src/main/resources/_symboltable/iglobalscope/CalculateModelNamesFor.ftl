<#-- (c) https://github.com/MontiCore/monticore -->
  HashSet<String> names = new HashSet<>();
  names.add(name);

  // calculate all prefixes
  while (name.contains(".")) {
    name = de.se_rwth.commons.Names.getQualifier(name);
    names.add(name);
  }

  return names;