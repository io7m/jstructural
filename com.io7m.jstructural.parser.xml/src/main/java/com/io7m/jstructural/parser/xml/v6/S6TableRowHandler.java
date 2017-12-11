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
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.STableCell;
import com.io7m.jstructural.ast.STableRow;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.junreachable.UnreachableCodeException;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6TableRowHandler extends S6ElementHandler
{
  private final STableRow.Builder<SParsed> row_builder;

  S6TableRowHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.row_builder = STableRow.builder();
    this.row_builder.setData(PARSED);
    this.row_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.row_builder.setType(
        STypeName.<SParsed>builder()
          .setData(PARSED)
          .setLexical(this.lexical())
          .setValue(am.get("type"))
          .build());
    }
  }

  @Override
  void onChildCompleted(final SModelType<SParsed> c)
  {
    Preconditions.checkPrecondition(
      c,
      c instanceof STableCell,
      x -> "Content must be table cell");

    this.row_builder.addCells((STableCell<SParsed>) c);
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    throw new UnreachableCodeException();
  }

  @Override
  STableRow<SParsed> finishContent()
  {
    return this.row_builder.build();
  }
}
