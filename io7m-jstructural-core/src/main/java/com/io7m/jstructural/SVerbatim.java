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

package com.io7m.jstructural;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;

/**
 * A verbatim text element.
 */

@Immutable public final class SVerbatim implements
  SFootnoteContent,
  SListItemContent,
  SParagraphContent,
  SFormalItemContent,
  STableCellContent
{
  /**
   * Construct a new verbatim element with the given text.
   * 
   * @param text
   *          The text
   * @return A new term
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SVerbatim verbatim(
    final @Nonnull String text)
    throws ConstraintError
  {
    final Option<String> none = Option.none();
    return new SVerbatim(text, none);
  }

  /**
   * Construct a new verbatim element with the given text and type attribute.
   * 
   * @param text
   *          The text
   * @param type
   *          The type attribute
   * @return A new term
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SVerbatim verbatimTyped(
    final @Nonnull String text,
    final @Nonnull String type)
    throws ConstraintError
  {
    final Option<String> some =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    return new SVerbatim(text, some);
  }

  private final @Nonnull String         text;
  private final @Nonnull Option<String> type;

  private SVerbatim(
    final @Nonnull String in_text,
    final @Nonnull Option<String> in_type)
    throws ConstraintError
  {
    this.text = Constraints.constrainNotNull(in_text, "Text");
    this.type = Constraints.constrainNotNull(in_type, "Type");
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SVerbatim other = (SVerbatim) obj;
    return this.text.equals(other.text) && this.type.equals(other.type);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitVerbatim(this);
  }

  @Override public <A> A formalItemContentAccept(
    final @Nonnull SFormalItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitVerbatim(this);
  }

  /**
   * @return The term's text
   */

  public @Nonnull String getText()
  {
    return this.text;
  }

  /**
   * @return The term's type attribute
   */

  public @Nonnull Option<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.text.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitVerbatim(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitVerbatim(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull STableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitVerbatim(this);
  }
}
