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

package com.io7m.jstructural.annotated;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * A footnote element.
 */

@Immutable public final class SAFootnote implements
  SAParagraphContent,
  SAListItemContent,
  SAFootnoteContent,
  SATableCellContent
{
  private final @Nonnull SNonEmptyList<SAFootnoteContent> content;
  private final int                                       number;

  SAFootnote(
    final int in_number,
    final @Nonnull SNonEmptyList<SAFootnoteContent> in_content)
    throws ConstraintError
  {
    this.number =
      Constraints.constrainRange(
        in_number,
        0,
        Integer.MAX_VALUE,
        "Footnote number");
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
    final SAFootnote other = (SAFootnote) obj;
    if (!this.content.equals(other.content)) {
      return false;
    }
    if (this.number != other.number) {
      return false;
    }
    return true;
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SAFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  /**
   * @return The element content
   */

  public @Nonnull SNonEmptyList<SAFootnoteContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The footnote number, unique over a document
   */

  public int getNumber()
  {
    return this.number;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.number;
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SAListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SAParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull SATableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFootnote(this);
  }
}
