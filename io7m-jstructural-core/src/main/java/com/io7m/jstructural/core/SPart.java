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
   */

  public static SPart part(
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SPartContents> no_contents = Option.none();
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
   */

  public static SPart partID(
    final SID id,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SPartContents> no_contents = Option.none();
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
   */

  public static SPart partTyped(
    final String type,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SPartContents> no_contents = Option.none();
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
   */

  public static SPart partTypedID(
    final String type,
    final SID id,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SPartContents> no_contents = Option.none();
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
   */

  public static SPart partWithContents(
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SPartContents> some_contents =
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
   */

  public static SPart partWithContentsID(
    final SID id,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SPartContents> some_contents =
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
   */

  public static SPart partWithContentsTyped(
    final String type,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SPartContents> some_contents =
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
   */

  public static SPart partWithContentsTypedID(
    final String type,
    final SID id,
    final SPartTitle in_title,
    final SNonEmptyList<SSection> in_sections)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SPartContents> some_contents =
      Option.some(SPartContents.get());
    return new SPart(some_type, some_id, in_title, some_contents, in_sections);
  }

  private final OptionType<SPartContents> contents;
  private final OptionType<SID>           id;
  private final SNonEmptyList<SSection>   sections;
  private final SPartTitle                title;
  private final OptionType<String>        type;

  private SPart(
    final OptionType<String> in_type,
    final OptionType<SID> in_id,
    final SPartTitle in_title,
    final OptionType<SPartContents> in_contents,
    final SNonEmptyList<SSection> in_sections)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.id = NullCheck.notNull(in_id, "ID");
    this.title = NullCheck.notNull(in_title, "Title");
    this.contents = NullCheck.notNull(in_contents, "Contents");
    this.sections = NullCheck.notNull(in_sections, "Content");
  }

  @Override public boolean equals(
    final @Nullable Object obj)
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

  public OptionType<SPartContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The part ID
   */

  public OptionType<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The part sections
   */

  public SNonEmptyList<SSection> getSections()
  {
    return this.sections;
  }

  /**
   * @return The part's title
   */

  public SPartTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The part's type attribute
   */

  public OptionType<String> getType()
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
