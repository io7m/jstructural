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

import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SImageSize;
import com.io7m.jstructural.ast.SInlineAnyContentType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.net.URI;
import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6ImageHandler extends S6ElementHandler
{
  private final SImage.Builder<SParsed> image_builder;

  S6ImageHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.image_builder = SImage.builder();
    this.image_builder.setData(PARSED);
    this.image_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.image_builder.setType(
        STypeName.<SParsed>builder()
          .setData(PARSED)
          .setLexical(this.lexical())
          .setValue(am.get("type"))
          .build());
    }

    this.image_builder.setSource(URI.create(am.get("source")));

    if (am.containsKey("width") && am.containsKey("height")) {
      this.image_builder.setSize(
        SImageSize.<SParsed>builder()
          .setData(PARSED)
          .setHeight(Integer.parseUnsignedInt(am.get("height")))
          .setWidth(Integer.parseUnsignedInt(am.get("width")))
          .build());
    }
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    this.image_builder.addText(text);
  }

  @Override
  SInlineAnyContentType<SParsed> finishContent()
  {
    return this.image_builder.build();
  }
}
