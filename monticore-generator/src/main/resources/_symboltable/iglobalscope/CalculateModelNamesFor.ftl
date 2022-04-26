<#-- (c) https://github.com/MontiCore/monticore -->
  HashSet<String> names = new HashSet<>();
  names.add(name);

  if (name.contains(".")) {
    names.add(de.se_rwth.commons.Names.getQualifier(name));
  }

  return names;