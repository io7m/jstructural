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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;

/**
 * A document with sections.
 */

public final class SDocumentWithSections extends SDocument
{
  /**
   * Construct a new document with the given title, and style.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The sections
   * @return A new document
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SDocumentWithSections document(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_content)
    throws ConstraintError
  {
    final Option<SDocumentContents> some_contents = Option.none();
    final Option<SDocumentStyle> some_style = Option.none();
    return new SDocumentWithSections(
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
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SDocumentWithSections documentContents(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_content)
    throws ConstraintError
  {
    final Option<SDocumentContents> some_contents =
      Option.some(SDocumentContents.get());
    final Option<SDocumentStyle> some_style = Option.none();
    return new SDocumentWithSections(
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
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SDocumentWithSections documentStyle(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull SDocumentStyle in_style,
    final @Nonnull SNonEmptyList<SSection> in_content)
    throws ConstraintError
  {
    final Option<SDocumentContents> some_contents = Option.none();
    final Option<SDocumentStyle> some_style =
      Option.some(Constraints.constrainNotNull(in_style, "Style"));
    return new SDocumentWithSections(
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
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SDocumentWithSections documentStyleContents(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull SDocumentStyle in_style,
    final @Nonnull SNonEmptyList<SSection> in_content)
    throws ConstraintError
  {
    final Option<SDocumentContents> some_contents =
      Option.some(SDocumentContents.get());
    final Option<SDocumentStyle> some_style =
      Option.some(Constraints.constrainNotNull(in_style, "Style"));
    return new SDocumentWithSections(
      in_title,
      some_contents,
      some_style,
      in_content);
  }

  private final @Nonnull SNonEmptyList<SSection> content;

  private SDocumentWithSections(
    final @Nonnull SDocumentTitle in_title,
    final @Nonnull Option<SDocumentContents> in_contents,
    final @Nonnull Option<SDocumentStyle> in_style,
    final @Nonnull SNonEmptyList<SSection> in_content)
    throws ConstraintError
  {
    super(in_title, in_contents, in_style);
    this.content = Constraints.constrainNotNull(in_content, "Content");
  }

  @Override public <A> A documentAccept(
    final @Nonnull SDocumentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitDocumentWithSections(this);
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SDocumentWithSections other = (SDocumentWithSections) obj;
    return this.content.equals(other.content);
  }

  /**
   * @return The document sections
   */

  public @Nonnull SNonEmptyList<SSection> getContent()
  {
    return this.content;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.content.hashCode();
    return result;
  }
}
