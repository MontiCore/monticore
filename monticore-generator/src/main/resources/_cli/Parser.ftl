<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "startprod")}


try {
      ${grammarname}Parser parser = new ${grammarname}Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model); //change

      if (!parser.hasErrors() && optAutomaton.isPresent()) { //change
        return optAutomaton.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    return null;
  }