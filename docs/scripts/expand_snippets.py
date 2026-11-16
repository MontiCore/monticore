# (c) https://github.com/MontiCore/monticore
"""
Expands all `--8<--` snippet stubs in the docs working directory in place.

Zensical does not support MkDocs `hooks:`, so the snippet hook
`docs/overrides/extensions/docsnippet.py` (snippet inclusion and rewriting of
.java/.mc4 links to GitHub/JavaDoc) is run here as a standalone pre-build step.

Usage (from the repository root, after `docs/scripts/preprocessing.sh`):
  python docs/scripts/expand_snippets.py [docs_wd]
"""
import importlib.util
import pathlib
import sys

import markdown

spec = importlib.util.spec_from_file_location("docsnippet", "docs/overrides/extensions/docsnippet.py")
docsnippet = importlib.util.module_from_spec(spec)
spec.loader.exec_module(docsnippet)

md = markdown.Markdown(extensions=[docsnippet.SnippetExtension()])
preprocessor = md.preprocessors["snippet"]

docs_dir = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "docs_wd")
count = 0
for file in docs_dir.rglob("*.md"):
    if file.is_symlink():
        continue  # symlink mode: never overwrite the original sources
    lines = file.read_text(encoding="utf-8").split("\n")
    if not any("--8<--" in line for line in lines):
        continue
    preprocessor.seen = set()
    file.write_text("\n".join(preprocessor.run(lines)), encoding="utf-8")
    count += 1
print(f"[INFO] Expanded snippets in {count} files")
