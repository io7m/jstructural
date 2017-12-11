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

import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.jlexing.core.LexicalType;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SText;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;

import java.net.URI;
import java.util.Objects;

abstract class S6ElementHandler implements LexicalType<URI>
{
  private final Locator2 locator;
  private final S6ElementHandler parent;
  private final LexicalPosition<URI> lexical;

  S6ElementHandler(
    final S6ElementHandler in_parent,
    final Locator2 in_locator)
  {
    this.parent = in_parent;
    this.locator = Objects.requireNonNull(in_locator, "Locator");
    this.lexical = S6Lexical.makeFromLocator(in_locator);
  }

  abstract void onText(
    SText<SParsed> text);

  abstract SModelType<SParsed> finishContent()
    throws SAXParseException;

  void onChildCompleted(
    final SModelType<SParsed> c)
    throws SAXParseException
  {

  }

  @Override
  public final LexicalPosition<URI> lexical()
  {
    return this.lexical;
  }

  final void onCompleted()
    throws SAXParseException
  {
    final S6ElementHandler p = this.parent;
    if (p != null) {
      p.onChildCompleted(this.finishContent());
    }
  }

  final S6ElementHandler parent()
  {
    return this.parent;
  }
}
