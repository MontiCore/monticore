/* (c) https://github.com/MontiCore/monticore */

package de.monticore.templateclassgenerator

// M1: configuration object "_configuration" prepared externally
info("--------------------------------")
info("Template Classes Generator")
info("--------------------------------")
debug("Model path     : " + templatepath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(templatepath, out)
info("--------------------------------")
info("Template Classes Generator END")
info("--------------------------------")
