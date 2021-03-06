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

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SNonEmptyList;
import net.jcip.annotations.Immutable;

/**
 * A subsection.
 */

@Immutable public final class SASubsection implements SAIDTargetContent
{
  private final SNonEmptyList<SASubsectionContent> content;
  private final OptionType<SAID>                   id;
  private final SASubsectionNumber                 number;
  private final SASubsectionTitle                  title;
  private final OptionType<String>                 type;

  /**
   * Construct a new subsection.
   *
   * @param in_number  The subsection number
   * @param in_type    The type attribute
   * @param in_id      The ID
   * @param in_title   The title
   * @param in_content The content
   */

  public SASubsection(
    final SASubsectionNumber in_number,
    final OptionType<String> in_type,
    final OptionType<SAID> in_id,
    final SASubsectionTitle in_title,
    final SNonEmptyList<SASubsectionContent> in_content)
  {
    this.number = NullCheck.notNull(in_number, "Number");
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
    final SASubsection other = (SASubsection) obj;
    return this.number.equals(other.number)
           && this.content.equals(other.content)
           && this.id.equals(other.id)
           && this.title.equals(other.title)
           && this.type.equals(other.type);
  }

  /**
   * @return The subsection content
   */

  public SNonEmptyList<SASubsectionContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The subsection ID
   */

  public OptionType<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The subsection number
   */

  public SASubsectionNumber getNumber()
  {
    return this.number;
  }

  /**
   * @return The subsection title
   */

  public SASubsectionTitle getTitle()
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
    result = (prime * result) + this.number.hashCode();
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <T> T targetContentAccept(
    final SAIDTargetContentVisitor<T> v)
    throws Exception
  {
    return v.visitSubsection(this);
  }
}
