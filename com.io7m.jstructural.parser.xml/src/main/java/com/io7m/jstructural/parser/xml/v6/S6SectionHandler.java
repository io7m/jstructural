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

import com.io7m.jstructural.ast.SBlockContentType;
import com.io7m.jstructural.ast.SBlockID;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SSectionType;
import com.io7m.jstructural.ast.SSectionWithSections;
import com.io7m.jstructural.ast.SSectionWithSubsectionContent;
import com.io7m.jstructural.ast.SSectionWithSubsections;
import com.io7m.jstructural.ast.SSubsectionContentType;
import com.io7m.jstructural.ast.SSubsectionType;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.junreachable.UnreachableCodeException;
import io.vavr.collection.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ext.Locator2;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static com.io7m.jstructural.ast.SParsed.PARSED;

final class S6SectionHandler extends S6ElementHandler
{
  private static final Logger LOG = LoggerFactory.getLogger(S6SectionHandler.class);

  private final String title;
  private final ArrayList<SBlockContentType<SParsed>> content;
  private final Optional<STypeName<SParsed>> type;
  private final Optional<SBlockID<SParsed>> id;
  private final boolean toc;

  S6SectionHandler(
    final S6ElementHandler in_parent,
    final Attributes in_attributes,
    final Locator2 in_locator)
  {
    super(in_parent, in_locator);

    final Map<String, String> am = S6Attributes.attributeMap(in_attributes);
    this.title = am.get("title");

    this.type =
      Optional.ofNullable(am.get("type"))
        .map(text -> STypeName.of(PARSED, text));

    this.id =
      Optional.ofNullable(am.get("id"))
        .map(text -> SBlockID.of(PARSED, text));

    this.toc =
      Optional.ofNullable(am.get("toc"))
        .map(Boolean::valueOf)
        .orElse(Boolean.TRUE)
        .booleanValue();

    this.content = new ArrayList<>(32);
  }

  @Override
  void onChildCompleted(final SModelType<SParsed> c)
  {
    LOG.trace("onChildCompleted: {}", c);
    if (c instanceof SBlockContentType) {
      this.content.add((SBlockContentType<SParsed>) c);
    } else {
      throw new UnreachableCodeException();
    }
  }

  @Override
  void onText(final SText<SParsed> text)
  {
    throw new UnreachableCodeException();
  }

  @Override
  SSectionType<SParsed> finishContent()
  {
    if (this.content.isEmpty()) {
      return SSectionWithSections.of(
        PARSED, this.type, this.id, this.title, this.toc, Vector.empty());
    }

    final SBlockContentType<SParsed> first = this.content.get(0);
    switch (first.blockKind()) {
      case BLOCK_SUBSECTION_CONTENT: {
        final Vector<SSubsectionContentType<SParsed>> cc =
          Vector.ofAll(this.content).map(c -> (SSubsectionContentType<SParsed>) c);
        return SSectionWithSubsectionContent.of(
          PARSED, this.type, this.id, this.title, this.toc, cc);
      }

      case BLOCK_SUBSECTION: {
        final Vector<SSubsectionType<SParsed>> cc =
          Vector.ofAll(this.content).map(c -> (SSubsectionType<SParsed>) c);
        return SSectionWithSubsections.of(
          PARSED, this.type, this.id, this.title, this.toc, cc);
      }

      case BLOCK_SECTION: {
        final Vector<SSectionType<SParsed>> cc =
          Vector.ofAll(this.content).map(c -> (SSectionType<SParsed>) c);
        return SSectionWithSections.of(
          PARSED, this.type, this.id, this.title, this.toc, cc);
      }

      case BLOCK_DOCUMENT:
        throw new UnreachableCodeException();
    }

    throw new UnreachableCodeException();
  }
}
