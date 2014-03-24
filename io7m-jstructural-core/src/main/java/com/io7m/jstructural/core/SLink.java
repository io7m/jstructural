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
 * A link to an internal document element.
 */

@Immutable public final class SLink implements
  SParagraphContent,
  SListItemContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new internal link element.
   * 
   * @param target
   *          The target
   * @param content
   *          The content in the body of the link
   * @return A new link element
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SLink link(
    final @Nonnull String target,
    final @Nonnull SNonEmptyList<SLinkContent> content)
    throws ConstraintError
  {
    return new SLink(target, content);
  }

  private final @Nonnull SNonEmptyList<SLinkContent> content;
  private final @Nonnull String                      target;

  private SLink(
    final @Nonnull String in_target,
    final @Nonnull SNonEmptyList<SLinkContent> in_content)
    throws ConstraintError
  {
    this.target = Constraints.constrainNotNull(in_target, "Target");
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
    final SLink other = (SLink) obj;
    return this.content.equals(other.content)
      && this.target.equals(other.target);
  }

  @Override public <A> A footnoteContentAccept(
    final @Nonnull SFootnoteContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitLink(this);
  }

  /**
   * @return The content of the link
   */

  public @Nonnull SNonEmptyList<SLinkContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The link target
   */

  public @Nonnull String getTarget()
  {
    return this.target;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.target.hashCode();
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final @Nonnull SListItemContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitLink(this);
  }

  @Override public <A> A paragraphContentAccept(
    final @Nonnull SParagraphContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitLink(this);
  }

  @Override public <A> A tableCellContentAccept(
    final @Nonnull STableCellContentVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitLink(this);
  }
}
