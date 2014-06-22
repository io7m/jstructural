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

/**
 * The type of sections containing paragraphs.
 */

public final class SSectionWithParagraphs extends SSection
{
  /**
   * Create a section with the given title and section content.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs section(
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> no_contents = Option.none();
    return new SSectionWithParagraphs(
      no_type,
      no_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given id, title, and section content.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionID(
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> no_contents = Option.none();
    return new SSectionWithParagraphs(
      no_type,
      some_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given type, title, and section content.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionTyped(
    final String type,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> no_contents = Option.none();
    return new SSectionWithParagraphs(
      some_type,
      no_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given type, id, title, and section content.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionTypedID(
    final String type,
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> no_contents = Option.none();
    return new SSectionWithParagraphs(
      some_type,
      some_id,
      in_title,
      no_contents,
      in_content);
  }

  /**
   * Create a section with the given title, section content, and with a table
   * of contents.
   * 
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionWithContents(
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithParagraphs(
      no_type,
      no_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given id, title, section content, and with a
   * table of contents.
   * 
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionWithContentsID(
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithParagraphs(
      no_type,
      some_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given type, title, section content, and with a
   * table of contents.
   * 
   * @param type
   *          The type
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionWithContentsTyped(
    final String type,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
    final OptionType<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithParagraphs(
      some_type,
      no_id,
      in_title,
      some_contents,
      in_content);
  }

  /**
   * Create a section with the given type, id, title, section content, and
   * with a table of contents.
   * 
   * @param type
   *          The type
   * @param id
   *          The ID
   * @param in_title
   *          The title
   * @param in_content
   *          The section content
   * @return A new section
   */

  public static SSectionWithParagraphs sectionWithContentsTypedID(
    final String type,
    final SID id,
    final SSectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    final OptionType<SSectionContents> some_contents =
      Option.some(SSectionContents.get());
    return new SSectionWithParagraphs(
      some_type,
      some_id,
      in_title,
      some_contents,
      in_content);
  }

  private final SNonEmptyList<SSubsectionContent> subsections;

  private SSectionWithParagraphs(
    final OptionType<String> in_type,
    final OptionType<SID> in_id,
    final SSectionTitle in_title,
    final OptionType<SSectionContents> in_contents,
    final SNonEmptyList<SSubsectionContent> in_subsections)
  {
    super(in_type, in_id, in_title, in_contents);
    this.subsections = NullCheck.notNull(in_subsections, "Subsections");
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
    final SSectionWithParagraphs other = (SSectionWithParagraphs) obj;
    return this.subsections.equals(other.subsections);
  }

  /**
   * @return The section content
   */

  public SNonEmptyList<SSubsectionContent> getSectionContent()
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
    return v.visitSectionWithParagraphs(this);
  }
}
