package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.Arrays;
import java.util.List;

public class BareboneListMutatorDecorator extends ListMutatorDecorator {
  public BareboneListMutatorDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
//            String.format(CLEAR, capitalizedAttributeNameWithS),
            String.format(ADD, capitalizedAttributeNameWithOutS, attributeType)
//            ,
//            String.format(ADD_ALL, capitalizedAttributeNameWithS, attributeType),
//            String.format(REMOVE, capitalizedAttributeNameWithOutS),
//            String.format(REMOVE_ALL, capitalizedAttributeNameWithS),
//            String.format(RETAIN_ALL, capitalizedAttributeNameWithS),
//            String.format(REMOVE_IF, capitalizedAttributeNameWithOutS, attributeType),
//            String.format(FOR_EACH, capitalizedAttributeNameWithS, attributeType),
//            String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType),
//            String.format(ADD_ALL_, capitalizedAttributeNameWithS, attributeType),
//            String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS),
//            String.format(SET, attributeType, capitalizedAttributeNameWithOutS, attributeType),
//            String.format(REPLACE_ALL, capitalizedAttributeNameWithS, attributeType),
//            String.format(SORT, capitalizedAttributeNameWithS, attributeType)
                        );
  }

}
