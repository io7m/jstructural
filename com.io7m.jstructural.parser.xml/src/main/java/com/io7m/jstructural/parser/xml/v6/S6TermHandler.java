/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jstructural.parser.xml.v6;

import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6TermHandler extends S6ElementHandler
{
  private final STerm.Builder<SParsed> term_builder;

  S6TermHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.term_builder = STerm.builder();
    this.term_builder.setData(PARSED);
    this.term_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.term_builder.setType(
        STypeName.<SParsed>builder()
          .setData(PARSED)
          .setLexical(this.lexical())
          .setValue(am.get("type"))
          .build());
    }
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    this.term_builder.addText(text);
  }

  @Override
  SInlineAnyContentType<SParsed> finishContent()
  {
    return this.term_builder.build();
  }
}