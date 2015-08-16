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
import com.io7m.jstructural.core.SSectionContents;
import net.jcip.annotations.Immutable;

import java.util.List;

/**
 * An abstract section.
 */

@Immutable public abstract class SASection implements SAIDTargetContent
{
  private final OptionType<SSectionContents> contents;
  private final List<SAFootnote>             footnotes;
  private final OptionType<SAID>             id;
  private final SASectionNumber              number;
  private final SASectionTitle               title;
  private final OptionType<String>           type;

  protected SASection(
    final SASectionNumber in_number,
    final OptionType<String> in_type,
    final OptionType<SAID> in_id,
    final SASectionTitle in_title,
    final OptionType<SSectionContents> in_contents,
    final List<SAFootnote> in_footnotes)
  {
    this.number = NullCheck.notNull(in_number, "Number");
    this.type = NullCheck.notNull(in_type, "Type");
    this.id = NullCheck.notNull(in_id, "ID");
    this.title = NullCheck.notNull(in_title, "Title");
    this.contents = NullCheck.notNull(in_contents, "Contents");
    this.footnotes = NullCheck.notNull(in_footnotes, "Footnotes");
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
    final SASection other = (SASection) obj;
    return this.contents.equals(other.contents)
           && this.number.equals(other.number)
           && this.id.equals(other.id)
           && this.title.equals(other.title)
           && this.type.equals(other.type);
  }

  /**
   * @return The section contents
   */

  public final OptionType<SSectionContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The list of footnotes for this section
   */

  public final List<SAFootnote> getFootnotes()
  {
    return this.footnotes;
  }

  /**
   * @return The section ID
   */

  public final OptionType<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The section number
   */

  public final SASectionNumber getNumber()
  {
    return this.number;
  }

  /**
   * @return The section title
   */

  public final SASectionTitle getTitle()
  {
    return this.title;
  }

  /**
   * @return The section type attribute
   */

  public final OptionType<String> getType()
  {
    return this.type;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.number.hashCode();
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  /**
   * Accept a section visitor.
   *
   * @param v   The visitor
   * @param <A> The type of values returned by the visitor
   *
   * @return The value returned by the visitor
   *
   * @throws Exception If the visitor raises an {@link Exception}
   */

  public abstract <A> A sectionAccept(
    final SASectionVisitor<A> v)
    throws Exception;
}
