package de.monticore.generating.templateengine.freemarker.alias;

import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IncludeAlias extends Alias {

  public IncludeAlias() {
    super("include");
  }

  @Override
  public Object exec(List arguments) throws TemplateModelException {
    assert arguments.size() == 1;
    Object arg = Objects.requireNonNull(arguments.get(0));

    if(arg instanceof SimpleScalar){
      return getTc().include(arg.toString());
    } else if(arg instanceof SimpleSequence){
        SimpleSequence simpleSequence = (SimpleSequence) arg;
        List<String> args = new ArrayList<>();
        for(int i = 0; i < simpleSequence.size(); i++){
          args.add(simpleSequence.get(i).toString());
        }
        return getTc().include(args);
    }

    throw new TemplateModelException("No method found for argument of type " + arg.getClass());
  }
}
