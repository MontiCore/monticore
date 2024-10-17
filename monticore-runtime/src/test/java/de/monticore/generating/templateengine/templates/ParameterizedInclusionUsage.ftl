<#-- (c) https://github.com/MontiCore/monticore -->
${tc.includeArgs("SignatureWithOneParameter", "Charly")}<#-- one argument (no list) -->
${tc.includeArgs("SignatureWithThreeParameters","Charly", "30", "Aachen")}<#-- two arguments (no list) -->
${tc.includeArgs("SignatureWithManyParameters",["Charly", "30", "Aachen", "52062", "Engineer", "No friends"])}<#-- 6 arguments (list) -->