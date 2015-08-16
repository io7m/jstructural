/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.core;

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

/**
 * A document with parts.
 */

@Immutable public final class SDocumentWithParts extends SDocument
{
  /**
   * Construct a new document with the given title, and style.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The sections
   * @return A new document
   */

  public static SDocumentWithParts document(
    final SDocumentTitle in_title,
    final SNonEmptyList<SPart> in_content)
  {
    final OptionType<SDocumentContents> some_contents = Option.none();
    final OptionType<SDocumentStyle> some_style = Option.none();
    return new SDocumentWithParts(
      in_title,
      some_contents,
      some_style,
      in_content);
  }

  /**
   * Construct a new document with the given title, and table of contents.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The sections
   * @return A new document
   */

  public static SDocumentWithParts documentContents(
    final SDocumentTitle in_title,
    final SNonEmptyList<SPart> in_content)
  {
    final OptionType<SDocumentContents> some_contents =
      Option.some(SDocumentContents.get());
    final OptionType<SDocumentStyle> some_style = Option.none();
    return new SDocumentWithParts(
      in_title,
      some_contents,
      some_style,
      in_content);
  }

  /**
   * Construct a new document with the given title.
   * 
   * @param in_title
   *          The title
   * @param in_style
   *          The style
   * @param in_content
   *          The sections
   * @return A new document
   */

  public static SDocumentWithParts documentStyle(
    final SDocumentTitle in_title,
    final SDocumentStyle in_style,
    final SNonEmptyList<SPart> in_content)
  {
    final OptionType<SDocumentContents> some_contents = Option.none();
    final OptionType<SDocumentStyle> some_style =
      Option.some(NullCheck.notNull(in_style, "Style"));
    return new SDocumentWithParts(
      in_title,
      some_contents,
      some_style,
      in_content);
  }

  /**
   * Construct a new document with the given title, style, and table of
   * contents.
   * 
   * @param in_title
   *          The title
   * @param in_style
   *          The style
   * @param in_content
   *          The sections
   * @return A new document
   */

  public static SDocumentWithParts documentStyleContents(
    final SDocumentTitle in_title,
    final SDocumentStyle in_style,
    final SNonEmptyList<SPart> in_content)
  {
    final OptionType<SDocumentContents> some_contents =
      Option.some(SDocumentContents.get());
    final OptionType<SDocumentStyle> some_style =
      Option.some(NullCheck.notNull(in_style, "Style"));
    return new SDocumentWithParts(
      in_title,
      some_contents,
      some_style,
      in_content);
  }

  private final SNonEmptyList<SPart> parts;

  private SDocumentWithParts(
    final SDocumentTitle in_title,
    final OptionType<SDocumentContents> in_contents,
    final OptionType<SDocumentStyle> in_style,
    final SNonEmptyList<SPart> in_content)
  {
    super(in_title, in_contents, in_style);
    this.parts = NullCheck.notNull(in_content, "Parts");
  }

  @Override public <D> D documentAccept(
    final SDocumentVisitor<D> v)
    throws Exception
  {
    return v.visitDocumentWithParts(this);
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SDocumentWithParts other = (SDocumentWithParts) obj;
    return this.parts.equals(other.parts);
  }

  /**
   * @return The document sections
   */

  public SNonEmptyList<SPart> getParts()
  {
    return this.parts;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.parts.hashCode();
    return result;
  }
}
