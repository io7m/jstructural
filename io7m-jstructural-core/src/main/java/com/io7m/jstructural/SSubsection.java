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
 * A subsection.
 */

@Immutable public final class SSubsection
{
  /**
   * Create a subsection with the given title and content.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new subsection
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSubsection subsection(
    final @Nonnull SSubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsectionContent> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    return new SSubsection(no_type, no_id, in_title, in_content);
  }

  /**
   * Create a subsection with the given id, title, and content.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new subsection
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSubsection subsectionID(
    final @Nonnull SID id,
    final @Nonnull SSubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsectionContent> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    return new SSubsection(no_type, some_id, in_title, in_content);
  }

  /**
   * Create a subsection with the given type, title, and content.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new subsection
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSubsection subsectionTyped(
    final @Nonnull String type,
    final @Nonnull SSubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsectionContent> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> no_id = Option.none();
    return new SSubsection(some_type, no_id, in_title, in_content);
  }

  /**
   * Create a subsection with the given type, id, title, and content.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new subsection
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSubsection subsectionTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SSubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsectionContent> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    return new SSubsection(some_type, some_id, in_title, in_content);
  }

  private final @Nonnull SNonEmptyList<SSubsectionContent> content;
  private final @Nonnull Option<SID>                       id;
  private final @Nonnull SSubsectionTitle                  title;
  private final @Nonnull Option<String>                    type;

  private SSubsection(
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SID> in_id,
    final @Nonnull SSubsectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsectionContent> in_content)
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
    final SSubsection other = (SSubsection) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The subsection content
   */

  public @Nonnull SNonEmptyList<SSubsectionContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The subsection ID
   */

  public @Nonnull Option<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The subsection title
   */

  public @Nonnull SSubsectionTitle getTitle()
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
}
