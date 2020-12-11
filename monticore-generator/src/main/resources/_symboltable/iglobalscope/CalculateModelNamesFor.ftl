<#-- (c) https://github.com/MontiCore/monticore -->
  if (name.contains(".")) {
    return com.google.common.collect.Sets.newHashSet(de.se_rwth.common.Names.getQualifier(name), name);
  }
  return com.google.common.collect.Sets.newHashSet(name);