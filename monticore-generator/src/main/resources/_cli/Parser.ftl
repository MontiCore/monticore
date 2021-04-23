<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "startprod", "millFullName", "parserFullName")}


try {
      ${parserFullName} parser = ${millFullName}.parser() ;
      Optional<${startprod}> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (NullPointerException | java.io.IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    return null;
