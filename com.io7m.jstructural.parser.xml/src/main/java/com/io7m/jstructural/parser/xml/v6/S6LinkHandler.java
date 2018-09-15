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
import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SInlineLinkContentType;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6LinkHandler extends S6ElementHandler
{
  private final SLink.Builder<SParsed> link_builder;

  S6LinkHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.link_builder = SLink.builder();
    this.link_builder.setData(PARSED);
    this.link_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.link_builder.setType(
        STypeName.<SParsed>builder()
          .setData(PARSED)
          .setLexical(this.lexical())
          .setValue(am.get("type"))
          .build());
    }

    this.link_builder.setTarget(am.get("target"));
  }

  @Override
  void onChildCompleted(
    final SModelType<SParsed> c)
  {
    Preconditions.checkPrecondition(
      c,
      c instanceof SInlineLinkContentType,
      x -> "Content must be inline link content");

    this.link_builder.addContent((SInlineLinkContentType<SParsed>) c);
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    this.link_builder.addContent(text);
  }

  @Override
  SInlineAnyContentType<SParsed> finishContent()
  {
    return this.link_builder.build();
  }
}
