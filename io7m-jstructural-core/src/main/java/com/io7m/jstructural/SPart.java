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
 * A document part.
 */

@Immutable public final class SPart
{
  /**
   * Create a part with the given title and content.
   * 
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart part(
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    final Option<SPartContents> no_contents = Option.none();
    return new SPart(no_type, no_id, in_title, no_contents, in_sections);
  }

  /**
   * Create a part with the given id, title, and content.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partID(
    final @Nonnull SID id,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SPartContents> no_contents = Option.none();
    return new SPart(no_type, some_id, in_title, no_contents, in_sections);
  }

  /**
   * Create a part with the given type, title, and content.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partTyped(
    final @Nonnull String type,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> no_id = Option.none();
    final Option<SPartContents> no_contents = Option.none();
    return new SPart(some_type, no_id, in_title, no_contents, in_sections);
  }

  /**
   * Create a part with the given type, id, title, and content.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SPartContents> no_contents = Option.none();
    return new SPart(some_type, some_id, in_title, no_contents, in_sections);
  }

  /**
   * Create a part with the given title, content, and with a table of
   * contents.
   * 
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partWithContents(
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    final Option<SPartContents> some_contents =
      Option.some(SPartContents.get());
    return new SPart(no_type, no_id, in_title, some_contents, in_sections);
  }

  /**
   * Create a part with the given id, title, content, and with a table of
   * contents.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partWithContentsID(
    final @Nonnull SID id,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SPartContents> some_contents =
      Option.some(SPartContents.get());
    return new SPart(no_type, some_id, in_title, some_contents, in_sections);
  }

  /**
   * Create a part with the given type, title, content, and with a table of
   * contents.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partWithContentsTyped(
    final @Nonnull String type,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> no_id = Option.none();
    final Option<SPartContents> some_contents =
      Option.some(SPartContents.get());
    return new SPart(some_type, no_id, in_title, some_contents, in_sections);
  }

  /**
   * Create a part with the given type, id, title, content, and with a table
   * of contents.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_sections
   *          The content
   * @return A new part
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SPart partWithContentsTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SPartTitle in_title,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SPartContents> some_contents =
      Option.some(SPartContents.get());
    return new SPart(some_type, some_id, in_title, some_contents, in_sections);
  }

  private final @Nonnull Option<SPartContents>   contents;
  private final @Nonnull Option<SID>             id;
  private final @Nonnull SNonEmptyList<SSection> sections;
  private final @Nonnull SPartTitle              title;
  private final @Nonnull Option<String>          type;

  private SPart(
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SID> in_id,
    final @Nonnull SPartTitle in_title,
    final @Nonnull Option<SPartContents> in_contents,
    final @Nonnull SNonEmptyList<SSection> in_sections)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.id = Constraints.constrainNotNull(in_id, "ID");
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.contents = Constraints.constrainNotNull(in_contents, "Contents");
    this.sections = Constraints.constrainNotNull(in_sections, "Content");
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
    final SPart other = (SPart) obj;
    return this.contents.equals(other.contents)
      && this.id.equals(other.id)
      && this.sections.equals(other.sections)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The part's table of contents
   */

  public @Nonnull Option<SPartContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The part ID
   */

  public @Nonnull Option<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The part sections
   */

  public @Nonnull SNonEmptyList<SSection> getSections()
  {
    return this.sections;
  }

  /**
   * @return The part's title
   */

  public @Nonnull SPartTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The part's type attribute
   */

  public @Nonnull Option<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.sections.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }
}
