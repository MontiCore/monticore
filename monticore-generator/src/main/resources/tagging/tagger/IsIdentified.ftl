<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature()}
travChecker.ret[0] = false;
travChecker.element = element;
elementIdentifier.accept(travChecker.traverser);
return travChecker.ret[0];
