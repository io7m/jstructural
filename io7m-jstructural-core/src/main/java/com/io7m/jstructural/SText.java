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

/**
 * A text element.
 */

@Immutable public final class SText implements
  SParagraphContent,
  SLinkContent,
  SListItemContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new text element.
   * 
   * @param text
   *          The text
   * @return A new text element
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SText text(
    final @Nonnull String text)
    throws ConstraintError
  {
    return new SText(text);
  }

  private final @Nonnull String text;

  private SText(
    final @Nonnull String in_text)
    throws ConstraintError
  {
    this.text = Constraints.constrainNotNull(in_text, "Text");
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
    final SText other = (SText) obj;
    return this.text.equals(other.text);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitText(this);
  }

  /**
   * @return The term's text
   */

  public @Nonnull String getText()
  {
    return this.text;
  }

  @Override public int hashCode()
  {
    return this.text.hashCode();
  }

  @Override public <A> A linkContentAccept(
    final @Nonnull SLinkContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitText(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull STableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitText(this);
  }
}
