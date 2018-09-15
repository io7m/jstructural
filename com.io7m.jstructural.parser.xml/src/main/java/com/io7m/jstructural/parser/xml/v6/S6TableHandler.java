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
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.STable;
import com.io7m.jstructural.ast.STableBody;
import com.io7m.jstructural.ast.STableBodyType;
import com.io7m.jstructural.ast.STableHeaderType;
import com.io7m.jstructural.ast.STableRowType;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.junreachable.UnreachableCodeException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;

import java.net.URI;
import java.util.Map;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6TableHandler extends S6ElementHandler
{
  private final STable.Builder<SParsed> table_builder;
  private final STableBody.Builder<SParsed> table_body_builder;
  private STableBodyType<SParsed> body;
  private STableHeaderType<SParsed> head;

  S6TableHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);
    this.table_builder = STable.builder();
    this.table_builder.setData(PARSED);
    this.table_builder.setLexical(this.lexical());

    this.table_body_builder = STableBody.builder();
    this.table_body_builder.setData(PARSED);
    this.table_body_builder.setLexical(this.lexical());

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    if (am.containsKey("type")) {
      this.table_builder.setType(
        STypeName.<SParsed>builder()
          .setData(PARSED)
          .setLexical(this.lexical())
          .setValue(am.get("type"))
          .build());
    }
  }

  @Override
  void onChildCompleted(
    final SModelType<SParsed> c)
    throws SAXParseException
  {
    if (c instanceof STableHeaderType) {
      this.head = (STableHeaderType<SParsed>) c;
      this.table_builder.setHeader(this.head);
      return;
    }

    if (c instanceof STableBodyType) {
      this.body = (STableBodyType<SParsed>) c;
      this.table_builder.setBody(this.body);
      return;
    }

    if (c instanceof STableRowType) {
      final STableRowType<SParsed> row =
        (STableRowType<SParsed>) c;

      this.checkRow(row);
      this.table_body_builder.addRows(row);
      return;
    }

    throw new UnreachableCodeException();
  }

  private void checkRow(
    final STableRowType<SParsed> row)
    throws SAXParseException
  {
    if (this.head != null) {
      final int row_expected = this.head.names().size();
      final int row_size = row.cells().size();
      if (row_size != row_expected) {
        final LexicalPosition<URI> lex = this.lexical();
        throw new SAXParseException(
          new StringBuilder(128)
            .append(
              "Number of columns in table row does not match the number declared in the table header.")
            .append(System.lineSeparator())
            .append("  Expected: ")
            .append(row_expected)
            .append(" columns")
            .append(System.lineSeparator())
            .append("  Received: ")
            .append(row_size)
            .append(" columns")
            .append(System.lineSeparator())
            .toString(),
          null,
          lex.file().map(URI::toString).orElse(null),
          lex.line(),
          lex.column());
      }
    }
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    throw new UnreachableCodeException();
  }

  @Override
  STable<SParsed> finishContent()
    throws SAXParseException
  {
    if (this.head == null) {
      Preconditions.checkPrecondition(
        this.body,
        this.body == null,
        x -> "Body must be null");

      this.body = this.table_body_builder.build();
      this.table_builder.setBody(this.body);
    }

    for (final STableRowType<SParsed> row : this.body.rows()) {
      this.checkRow(row);
    }
    return this.table_builder.build();
  }
}
