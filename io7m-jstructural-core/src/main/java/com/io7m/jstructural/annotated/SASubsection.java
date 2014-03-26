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
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * A subsection.
 */

@Immutable public final class SASubsection implements SAIDTargetContent
{
  private final @Nonnull SNonEmptyList<SASubsectionContent> content;
  private final @Nonnull Option<SAID>                       id;
  private final @Nonnull SASubsectionTitle                  title;
  private final @Nonnull Option<String>                     type;

  /**
   * Construct a new subsection.
   * 
   * @param in_number
   *          The subsection number
   * @param in_type
   *          The type attribute
   * @param in_id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SASubsection(
    final @Nonnull SASubsectionNumber in_number,
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SAID> in_id,
    final @Nonnull SASubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SASubsectionContent> in_content)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.id = Constraints.constrainNotNull(in_id, "ID");
    this.title = Constraints.constrainNotNull(in_title, "Title");
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
    final SASubsection other = (SASubsection) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The subsection content
   */

  public @Nonnull SNonEmptyList<SASubsectionContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The subsection ID
   */

  public @Nonnull Option<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The subsection title
   */

  public @Nonnull SASubsectionTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The subsection type attribute
   */

  public @Nonnull Option<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <T> T targetContentAccept(
    final @Nonnull SAIDTargetContentVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSubsection(this);
  }
}
