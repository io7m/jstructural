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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;
import net.jcip.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

/**
 * A document part.
 */

@Immutable public final class SAPart implements SAIDTargetContent
{
  private final OptionType<SPartContents>       contents;
  private final OptionType<SAID>                id;
  private final SAPartNumber                    number;
  private final Map<SASectionNumber, SASection> numbered_sections;
  private final SNonEmptyList<SASection>        sections;
  private final SAPartTitle                     title;
  private final OptionType<String>              type;

  /**
   * Construct a new part.
   *
   * @param in_number   The part number
   * @param in_type     The type attribute
   * @param in_id       The part ID
   * @param in_title    The part title
   * @param in_contents The table of contents
   * @param in_sections The sections
   */

  public SAPart(
    final SAPartNumber in_number,
    final OptionType<String> in_type,
    final OptionType<SAID> in_id,
    final SAPartTitle in_title,
    final OptionType<SPartContents> in_contents,
    final SNonEmptyList<SASection> in_sections)
  {
    this.number = NullCheck.notNull(in_number, "Part number");
    this.type = NullCheck.notNull(in_type, "Type");
    this.id = NullCheck.notNull(in_id, "ID");
    this.title = NullCheck.notNull(in_title, "Title");
    this.contents = NullCheck.notNull(in_contents, "Contents");
    this.sections = NullCheck.notNull(in_sections, "Content");

    this.numbered_sections = new HashMap<SASectionNumber, SASection>();
    for (final SASection s : this.sections.getElements()) {
      assert this.numbered_sections.containsKey(s.getNumber()) == false;
      this.numbered_sections.put(s.getNumber(), s);
    }
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
    final SAPart other = (SAPart) obj;
    return this.number.equals(other.number)
           && this.contents.equals(other.contents)
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

  public OptionType<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The part number
   */

  public SAPartNumber getNumber()
  {
    return this.number;
  }

  /**
   * The section with the given number.
   *
   * @param n The number
   *
   * @return The section, if any
   */

  public OptionType<SASection> getSection(
    final SASectionNumber n)
  {
    NullCheck.notNull(n, "Number");
    if (this.numbered_sections.containsKey(n)) {
      final SASection r = this.numbered_sections.get(n);
      assert r != null;
      return Option.some(r);
    }
    return Option.none();
  }

  /**
   * @return The part sections
   */

  public SNonEmptyList<SASection> getSections()
  {
    return this.sections;
  }

  /**
   * @return The part's title
   */

  public SAPartTitle getTitle()
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
    result = (prime * result) + this.number.hashCode();
    result = (prime * result) + this.contents.hashCode();
    result = (prime * result) + this.id.hashCode();
    result = (prime * result) + this.sections.hashCode();
    result = (prime * result) + this.title.hashCode();
    result = (prime * result) + this.type.hashCode();
    return result;
  }

  @Override public <T> T targetContentAccept(
    final SAIDTargetContentVisitor<T> v)
    throws Exception
  {
    return v.visitPart(this);
  }
}
