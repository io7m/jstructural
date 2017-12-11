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

import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.STableCell;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6TableCellHandler extends S6ElementHandler
{
  private final STableCell.Builder<SParsed> cell_builder;

  S6TableCellHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.cell_builder = STableCell.builder();
    this.cell_builder.setData(PARSED);
    this.cell_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.cell_builder.setType(
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
    this.cell_builder.addContent(text);
  }

  @Override
  STableCell<SParsed> finishContent()
  {
    return this.cell_builder.build();
  }
}
