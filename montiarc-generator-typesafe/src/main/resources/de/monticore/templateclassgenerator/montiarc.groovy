package de.monticore.templateclassgenerator

// M1: configuration object "_configuration" prepared externally
info("--------------------------------")
info("Template Classes Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, out)
info("--------------------------------")
info("Template Classes Generator END")
info("--------------------------------")
