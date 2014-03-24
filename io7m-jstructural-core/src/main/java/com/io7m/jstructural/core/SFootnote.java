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
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A footnote element.
 */

@Immutable public final class SFootnote implements
  SParagraphContent,
  SListItemContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new footnote.
   * 
   * @param content
   *          The footnote content.
   * @return A new footnote
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SFootnote footnote(
    final @Nonnull SNonEmptyList<SFootnoteContent> content)
    throws ConstraintError
  {
    return new SFootnote(content);
  }

  private final @Nonnull SNonEmptyList<SFootnoteContent> content;

  private SFootnote(
    final @Nonnull SNonEmptyList<SFootnoteContent> in_content)
    throws ConstraintError
  {
    this.content = Constraints.constrainNotNull(in_content, "Content");
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
    final SFootnote other = (SFootnote) obj;
    return this.content.equals(other.content);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  /**
   * @return The element content
   */

  public @Nonnull SNonEmptyList<SFootnoteContent> getContent()
  {
    return this.content;
  }

  @Override public int hashCode()
  {
    return this.content.hashCode();
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull STableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }
}
