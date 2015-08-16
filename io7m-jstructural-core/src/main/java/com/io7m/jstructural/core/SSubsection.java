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
   */

  public static SSubsection subsection(
    final SSubsectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> no_id = Option.none();
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
   */

  public static SSubsection subsectionID(
    final SID id,
    final SSubsectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> no_type = Option.none();
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
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
   */

  public static SSubsection subsectionTyped(
    final String type,
    final SSubsectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> no_id = Option.none();
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
   */

  public static SSubsection subsectionTypedID(
    final String type,
    final SID id,
    final SSubsectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    final OptionType<String> some_type =
      Option.some(NullCheck.notNull(type, "Type"));
    final OptionType<SID> some_id = Option.some(NullCheck.notNull(id, "ID"));
    return new SSubsection(some_type, some_id, in_title, in_content);
  }

  private final SNonEmptyList<SSubsectionContent> content;
  private final OptionType<SID>                   id;
  private final SSubsectionTitle                  title;
  private final OptionType<String>                type;

  private SSubsection(
    final OptionType<String> in_type,
    final OptionType<SID> in_id,
    final SSubsectionTitle in_title,
    final SNonEmptyList<SSubsectionContent> in_content)
  {
    this.type = NullCheck.notNull(in_type, "Type");
    this.id = NullCheck.notNull(in_id, "ID");
    this.title = NullCheck.notNull(in_title, "Title");
    this.content = NullCheck.notNull(in_content, "Content");
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
    final SSubsection other = (SSubsection) obj;
    return this.content.equals(other.content)
      && this.id.equals(other.id)
      && this.title.equals(other.title)
      && this.type.equals(other.type);
  }

  /**
   * @return The subsection content
   */

  public SNonEmptyList<SSubsectionContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The subsection ID
   */

  public OptionType<SID> getID()
  {
    return this.id;
  }

  /**
   * @return The subsection title
   */

  public SSubsectionTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The subsection type attribute
   */

  public OptionType<String> getType()
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
