<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Convenient method for pretty printing
-->
getPrinter().clearBuffer();
getTraverser().clearTraversedElements();
node.accept(getTraverser());

// do not overzealous strip trailing linebreaks, only trailing spaces within the last line
getPrinter().stripTrailing();
return getPrinter().getContent();
