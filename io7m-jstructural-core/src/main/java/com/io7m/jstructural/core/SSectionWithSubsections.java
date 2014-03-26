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
import com.io7m.jaux.functional.Option;

/**
 * The type of sections containing paragraphs.
 */

@Immutable public final class SSectionWithSubsections extends SSection
{
  /**
   * Create a section with the given title and content.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections section(
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    final Option<SSectionContents> no_contents = Option.none();
    return new SSectionWithSubsections(
      no_type,
      no_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given id, title, and content.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionID(
    final @Nonnull SID id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SSectionContents> no_contents = Option.none();
    return new SSectionWithSubsections(
      no_type,
      some_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given type, title, and content.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionTyped(
    final @Nonnull String type,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> no_id = Option.none();
    final Option<SSectionContents> no_contents = Option.none();
    return new SSectionWithSubsections(
      some_type,
      no_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given type, id, title, and content.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SSectionContents> no_contents = Option.none();
    return new SSectionWithSubsections(
      some_type,
      some_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given title, content, and with a table of
   * contents.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionWithContents(
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> no_id = Option.none();
    final Option<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithSubsections(
      no_type,
      no_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given id, title, content, and with a table of
   * contents.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionWithContentsID(
    final @Nonnull SID id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> no_type = Option.none();
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithSubsections(
      no_type,
      some_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given type, title, content, and with a table of
   * contents.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionWithContentsTyped(
    final @Nonnull String type,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> no_id = Option.none();
    final Option<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithSubsections(
      some_type,
      no_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given type, id, title, content, and with a
   * table of contents.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The content
   * @return A new section
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SSectionWithSubsections sectionWithContentsTypedID(
    final @Nonnull String type,
    final @Nonnull SID id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    final Option<String> some_type =
      Option.some(Constraints.constrainNotNull(type, "Type"));
    final Option<SID> some_id =
      Option.some(Constraints.constrainNotNull(id, "ID"));
    final Option<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithSubsections(
      some_type,
      some_id,
      in_title,
      some_contents,
      in_content);
  }

  private final @Nonnull SNonEmptyList<SSubsection> subsections;

  private SSectionWithSubsections(
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SID> in_id,
    final @Nonnull SSectionTitle in_title,
    final @Nonnull Option<SSectionContents> in_contents,
    final @Nonnull SNonEmptyList<SSubsection> in_content)
    throws ConstraintError
  {
    super(in_type, in_id, in_title, in_contents);
    this.subsections =
      Constraints.constrainNotNull(in_content, "Subsections");
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SSectionWithSubsections other = (SSectionWithSubsections) obj;
    return this.subsections.equals(other.subsections);
  }

  /**
   * @return The section content
   */

  public @Nonnull SNonEmptyList<SSubsection> getSubsections()
  {
    return this.subsections;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = (prime * result) + this.subsections.hashCode();
    return result;
  }

  @Override public <S> S sectionAccept(
    final @Nonnull SSectionVisitor<S> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSectionWithSubsections(this);
  }
}
