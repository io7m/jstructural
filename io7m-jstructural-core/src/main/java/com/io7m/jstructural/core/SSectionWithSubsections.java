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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

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
   */

  public static SSectionWithSubsections section(
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> no_contents = Option.none();
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
   */

  public static SSectionWithSubsections sectionID(
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> no_contents = Option.none();
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
   */

  public static SSectionWithSubsections sectionTyped(
    final String type,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> no_contents = Option.none();
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
   */

  public static SSectionWithSubsections sectionTypedID(
    final String type,
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> no_contents = Option.none();
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
   */

  public static SSectionWithSubsections sectionWithContents(
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> some_contents =
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
   */

  public static SSectionWithSubsections sectionWithContentsID(
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> some_contents =
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
   */

  public static SSectionWithSubsections sectionWithContentsTyped(
    final String type,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> some_contents =
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
   */

  public static SSectionWithSubsections sectionWithContentsTypedID(
    final String type,
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsection> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithSubsections(
      some_type,
      some_id,
      in_title,
      some_contents,
      in_content);
  }

  private final SNonEmptyList<SSubsection> subsections;

  private SSectionWithSubsections(
    final OptionType<String> in_type,
    final OptionType<SID> in_id,
    final SSectionTitle in_title,
    final OptionType<SSectionContents> in_contents,
    final SNonEmptyList<SSubsection> in_content)
  {
    super(in_type, in_id, in_title, in_contents);
    this.subsections = NullCheck.notNull(in_content, "Subsections");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (obj == null) {
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

  public SNonEmptyList<SSubsection> getSubsections()
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
    final SSectionVisitor<S> v)
    throws Exception
  {
    return v.visitSectionWithSubsections(this);
  }
}
