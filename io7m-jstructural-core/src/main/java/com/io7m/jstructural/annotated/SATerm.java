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
import com.io7m.jaux.functional.Option;

/**
 * A simple term.
 */

@Immutable public final class SATerm implements
  SAListItemContent,
  SAParagraphContent,
  SAFootnoteContent,
  SATableCellContent
{
  private final @Nonnull SAText         text;
  private final @Nonnull Option<String> type;

  SATerm(
    final @Nonnull SAText in_text,
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
    final SATerm other = (SATerm) obj;
    return this.text.equals(other.text) && this.type.equals(other.type);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SAFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitTerm(this);
  }

  /**
   * @return The term's text
   */

  public @Nonnull SAText getText()
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
    final @Nonnull SAListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitTerm(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SAParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitTerm(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull SATableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitTerm(this);
  }
}
