from pymdownx import snippets

"""
Snippet ---8<---.

pymdownx.snippet
Inject snippets

------
Extended pymdownx.snippet: We add some additional replacements:
# - .java relative link -> GitHub source + JavaDoc link (if found)
# - .mc4 relative link  -> GitHub source
Original source is located:
https://github.com/facelessuser/pymdown-extensions/blob/f64422f87c05031a8c8d62b1988bf76e8f65f27f/pymdownx/snippets.py
-------

MIT license.

Copyright (c) 2017 Isaac Muse <isaacmuse@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.
"""
class SnippetExtension(snippets.SnippetExtension):

    def extendMarkdown(self, md):
        """Register the extension."""
        self.md = md
        md.registerExtension(self)
        config = self.getConfigs()
        snippet = MCSnippetPreprocessor(config, md)
        md.preprocessors.register(snippet, "snippet", 32)

# add this snippet extension here
def on_config(config, **kwargs):
    config.markdown_extensions.append(SnippetExtension())


# We have to copy the parse_snippets method to add our own hook point
class MCSnippetPreprocessor(snippets.SnippetPreprocessor):

    # A processing step to modify links
    def process_snippet(self, s_lines, snippet):
        import re
        from pathlib import Path
        base_path = Path(snippet).parent  # directory in which the snippet resides
        cwd = Path().resolve()

        # Pattern to match anchors to relative files [anchor]: url
        file_pattern = r'(?!https?:\/\/)([^)]+\.(java|mc4))'
        pattern_anchor = re.compile(r'\[([a-zA-Z0-9_]+)\]: ' + file_pattern)
        # collect all used link-targets
        relative_anchors = {m[0]: m for line in s_lines for m in pattern_anchor.findall(line)}

        # Pattern to match relative markdown links: [text](url)
        pattern_rel_link = re.compile(r'\[([^\]]+)\]\(' + file_pattern + r'\)')
        # Pattern to match relative markdown links: [text][anchor]
        pattern_anchor_link = re.compile(r'\[([^\]]+)\]\[([a-zA-Z0-9_]+)\]')

        # Replace functions
        def anchor_replacer(match): # [text][anchor]
            text = match.group(1)  # the text within the link
            a_name = match.group(2)  # the name of the anchor
            if a_name not in relative_anchors:
                return match.group(0)  # keep original
            anchor = relative_anchors[a_name]
            return replace_link(text, anchor[1], anchor[2])

        def link_replacer(match): # [text](url) links
            text = match.group(1)  # the text within the link
            url = match.group(2)  # the url including everything
            file_ext = match.group(3)  # the url including everything
            return replace_link(text, url, file_ext)

        def replace_link(text, url, file_ext):
            # which file does the url point to?
            resolved_path = (base_path / url).resolve().relative_to(cwd)
            # step 1: construct a GitHub link
            github_link = f"[:material-github:](https://github.com/MontiCore/monticore/blob/dev/{resolved_path} \"View file on GitHub\")"
            # step 2: construct a JavaDocs link
            parts = resolved_path.parts
            project = parts[0]  # the project, i.e. monticore-grammar, monticore-runtime, etc
            source_set = parts[2]  # main or testFixtures
            # we have links to non main/testFixtures java files -> no javadoc link
            if source_set in ['main', 'testFixtures'] and project in ['monticore-grammar',
                                                                      'monticore-runtime'] and file_ext == 'java':
                # find the correct file location
                javadoc_task = 'javadoc' if source_set == 'main' else 'testFixturesJavadoc'
                back_to_root = '../' * (len(base_path.relative_to(cwd).parts))
                file = '/'.join(parts[4:])[:-len(".java")]
                javadoc_link = f"[:material-file-document:]({back_to_root}{project}/{javadoc_task}/{file}.html \"View JavaDoc\")"
            else:
                javadoc_link = ''
            return f"{text} <sup>{github_link} {javadoc_link}</sup>"

        # Apply replacement
        return [pattern_anchor_link.sub(anchor_replacer, pattern_rel_link.sub(link_replacer, md_text)) for md_text in
                s_lines]


    # the following code is copied 1-to-1 (just with the addition of one hook)
    """Handle snippets in Markdown content. - a copy of """
    def parse_snippets(self, lines, file_name=None, is_url=False, is_section=False):
        """Parse snippets snippet."""

        if file_name:
            # Track this file.
            self.seen.add(file_name)

        new_lines = []
        inline = False
        block = False
        for line in lines:
            # Check for snippets on line
            inline = False
            m = self.RE_ALL_SNIPPETS.match(line)
            if m:
                if m.group('escape'):
                    # The snippet has been escaped, replace first `;` and continue.
                    new_lines.append(line.replace(';', '', 1))
                    continue

                if block and m.group('inline_marker'):
                    # Don't use inline notation directly under a block.
                    # It's okay if inline is used again in sub file though.
                    continue

                elif m.group('inline_marker'):
                    # Inline
                    inline = True

                else:
                    # Block
                    block = not block
                    continue

            elif not block:
                if not is_section:
                    # Check for section line, if present remove, if escaped, reformat it
                    m2 = self.RE_SNIPPET_SECTION.match(line)
                    if m2 and m2.group('escape'):
                        line = (
                                m2.group('pre') + m2.group('escape').replace(';', '', 1) + m2.group('inline_marker') +
                                m2.group('section') + m2.group('post')
                        )
                        m2 = None

                    # Found a section that must be removed
                    if m2 is not None:
                        continue

                # Not in snippet, and we didn't find an inline,
                # so just a normal line
                new_lines.append(line)
                continue

            if block and not inline:
                # We are in a block and we didn't just find a nested inline
                # So check if a block path
                m = self.RE_SNIPPET.match(line)

            if m:
                # Get spaces and snippet path.  Remove quotes if inline.
                space = m.group('space').expandtabs(self.tab_length)
                path = m.group('snippet')[1:-1].strip() if inline else m.group('snippet').strip()

                if not inline:
                    # Block path handling
                    if not path:
                        # Empty path line, insert a blank line
                        new_lines.append('')
                        continue

                # Ignore commented out lines
                if path.startswith(';'):
                    continue

                # Get line numbers (if specified)
                end = []
                start = []
                section = None
                m = self.RE_SNIPPET_FILE.match(path)
                path = '' if m is None else m.group(1).strip()
                # Looks like we have an empty file and only lines specified
                if not path:
                    if self.check_paths:
                        raise snippets.SnippetMissingError(f"Snippet at path '{path}' could not be found")
                    else:
                        continue
                if m.group(2):
                    for nums in m.group(2)[1:].split(','):
                        span = nums.split(':')
                        st = int(span[0]) if span[0] else None
                        start.append(st if st is None or st < 0 else max(0, st - 1))
                        en = int(span[1]) if len(span) > 1 and span[1] else None
                        end.append(en)
                elif m.group(3):
                    section = m.group(3)[1:]

                # Ignore path links if we are in external, downloaded content
                is_link = path.lower().startswith(('https://', 'http://'))
                if is_url and not is_link:
                    continue

                # If this is a link, and we are allowing URLs, set `url` to true.
                # Make sure we don't process `path` as a local file reference.
                url = self.url_download and is_link
                snippet = self.get_snippet_path(path) if not url else path

                if snippet:

                    # This is in the stack and we don't want an infinite loop!
                    if snippet in self.seen:
                        continue

                    if not url:
                        # Read file content
                        with open(snippet, 'r', encoding=self.encoding) as f:
                            last = False
                            s_lines = []
                            for l in f:
                                last = l.endswith(('\r', '\n'))
                                s_lines.append(l.strip('\r\n'))
                            if last:
                                s_lines.append('')
                    else:
                        # Read URL content
                        try:
                            s_lines = self.download(snippet)
                        except snippets.SnippetMissingError:
                            if self.check_paths:
                                raise
                            s_lines = []

                    if s_lines:
                        total = len(s_lines)
                        if start and end:
                            final_lines = []
                            for sel in zip(start, end):
                                s_start = snippets.util.clamp(total + sel[0], 0, total) if sel[0] and sel[0] < 0 else sel[0]
                                s_end = snippets.util.clamp(total + 1 + sel[1], 0, total) if sel[1] and sel[1] < 0 else sel[1]
                                final_lines.extend(s_lines[slice(s_start, s_end, None)])
                            s_lines = self.dedent(final_lines) if self.dedent_subsections else final_lines
                        elif section:
                            s_lines = self.extract_section(section, s_lines)

                    # BEGIN MODIFICATION: Call hook point
                    if s_lines:
                        s_lines = self.process_snippet(s_lines, snippet)
                    # END MODIFICATION

                    # Process lines looking for more snippets
                    new_lines.extend(
                        [
                            space + l2 for l2 in self.parse_snippets(
                            s_lines,
                            snippet,
                            is_url=url,
                            is_section=section is not None
                        )
                        ]
                    )

                elif self.check_paths:
                    raise snippets.SnippetMissingError(f"Snippet at path '{path}' could not be found")

        # Pop the current file name out of the cache
        if file_name:
            self.seen.remove(file_name)

        return new_lines
