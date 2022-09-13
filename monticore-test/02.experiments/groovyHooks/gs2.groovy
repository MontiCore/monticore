/* (c) https://github.com/MontiCore/monticore */

import de.monticore.cd.facade.CDMethodFacade
import de.monticore.cd.codegen.CD2JavaTemplates
import de.monticore.generating.templateengine.StringHookPoint

Log.info("Execute script for groovy hook point 2.", "GS2")

// get args
glex = args[0]
decoratedCD = args[2]

// log cd name
cdName = decoratedCD.get(0).getCDDefinition().getName()
Log.info("Class diagram name: " + cdName, "GS2")

// manipulate AST CD
facade = CDMethodFacade.getInstance()
m = facade.createMethodByDefinition("public int countStates();")
glex.replaceTemplate(CD2JavaTemplates.EMPTY_BODY, m, new StringHookPoint("return states.size();"))
decoratedCD.get(0).getCDDefinition().getCDElementList().get(0).getCDElementList().get(0).addCDMember(m)