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
 * An abstract section.
 */

@Immutable public abstract class SSection
{
  private final @Nonnull Option<SSectionContents> contents;
  private final @Nonnull Option<SID>              id;
  private final @Nonnull SSectionTitle            title;
  private final @Nonnull Option<String>           type;

  protected SSection(
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SID> in_id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull Option<SSectionContents> in_contents)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.id = Constraints.constrainNotNull(in_id, "ID");
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.contents = Constraints.constrainNotNull(in_contents, "Contents");
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
    final SSection other = (SSection) obj;
    return this.contents.equals(other.contents)
      && this.id.equals(other.id)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The section contents
   */

  public final @Nonnull Option<SSectionContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The section ID
   */

  public final @Nonnull Option<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The section title
   */

  public final @Nonnull SSectionTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The section type attribute
   */

  public final @Nonnull Option<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  /**
   * Accept a section visitor.
   * 
   * @param v
   *          The visitor
   * @return The value returned by the visitor
   * @throws ConstraintError
   *           If the visitor raises {@link ConstraintError}
   * @throws Exception
   *           If the visitor raises an {@link Exception}
   * @param <A>
   *          The type of values returned by the visitor
   */

  public abstract <A> A sectionAccept(
    final @Nonnull SSectionVisitor<A> v)
    throws ConstraintError,
      Exception;
}
