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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParagraph;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.Map;
import java.util.Optional;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6ParagraphHandler extends S6ElementHandler
{
  private final SParagraph.Builder<SParsed> paragraph_builder;

  S6ParagraphHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);

    this.paragraph_builder = SParagraph.builder();

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    this.paragraph_builder.setData(PARSED);
    this.paragraph_builder.setType(
      Optional.ofNullable(am.get("type")).map(text -> STypeName.of(PARSED, text)));
    this.paragraph_builder.setId(
      Optional.ofNullable(am.get("id")).map(text -> SBlockID.of(PARSED, text)));
  }

  @Override
  void onChildCompleted(final SModelType<SParsed> c)
  {
    Preconditions.checkPrecondition(
      c,
      c instanceof SInlineAnyContentType,
      x -> "Content must be inline content");

    this.paragraph_builder.addContent((SInlineAnyContentType<SParsed>) c);
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    this.paragraph_builder.addContent(text);
  }

  @Override
  SParagraph<SParsed> finishContent()
  {
    return this.paragraph_builder.build();
  }
}