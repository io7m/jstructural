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

package com.io7m.jstructural.ast;

import com.io7m.immutables.styles.ImmutablesStyleType;
import com.io7m.jlexing.core.LexicalPosition;
import io.vavr.collection.Vector;
import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import java.net.URI;
import java.util.Optional;

/**
 * The type of sections that contain nested subsections.
 *
 * @param <T> The type of data associated with the AST
 */

@ImmutablesStyleType
@VavrEncodingEnabled
@Value.Immutable
public
interface SSectionWithSubsectionsType<T> extends SSectionType<T>
{
  @Value.Auxiliary
  @Value.Default
  @Override
  default LexicalPosition<URI> lexical()
  {
    return SLexicalDefaults.DEFAULT_POSITION;
  }

  @Override
  default SectionKind sectionKind()
  {
    return SectionKind.SECTION_WITH_SUBSECTIONS;
  }

  @Override
  @Value.Auxiliary
  @Value.Parameter
  T data();

  @Override
  @Value.Parameter
  Optional<STypeNameType<T>> type();

  @Override
  @Value.Parameter
  Optional<SBlockIDType<T>> id();

  @Override
  @Value.Parameter
  String title();

  @Override
  @Value.Parameter
  boolean tableOfContents();

  /**
   * @return The nested subsections
   */

  @Value.Parameter
  Vector<SSubsectionType<T>> subsections();
}