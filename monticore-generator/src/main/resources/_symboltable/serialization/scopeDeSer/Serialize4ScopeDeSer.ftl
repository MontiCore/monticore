<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("s2j")}
${s2j} s2j = new ${s2j}();
toSerialize.accept(s2j.getTraverser());
return s2j.getJsonPrinter().getContent();
