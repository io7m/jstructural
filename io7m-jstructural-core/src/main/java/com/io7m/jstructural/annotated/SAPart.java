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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SPartContents;

/**
 * A document part.
 */

@Immutable public final class SAPart implements SAIDTargetContent
{
  private final @Nonnull Option<SPartContents>           contents;
  private final @Nonnull Option<SAID>                    id;
  private final @Nonnull SAPartNumber                    number;
  private final @Nonnull Map<SASectionNumber, SASection> numbered_sections;
  private final @Nonnull SNonEmptyList<SASection>        sections;
  private final @Nonnull SAPartTitle                     title;
  private final @Nonnull Option<String>                  type;

  /**
   * Construct a new part.
   * 
   * @param in_number
   *          The part number
   * @param in_type
   *          The type attribute
   * @param in_id
   *          The part ID
   * @param in_title
   *          The part title
   * @param in_contents
   *          The table of contents
   * @param in_sections
   *          The sections
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SAPart(
    final @Nonnull SAPartNumber in_number,
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SAID> in_id,
    final @Nonnull SAPartTitle in_title,
    final @Nonnull Option<SPartContents> in_contents,
    final @Nonnull SNonEmptyList<SASection> in_sections)
    throws ConstraintError
  {
    this.number = Constraints.constrainNotNull(in_number, "Part number");
    this.type = Constraints.constrainNotNull(in_type, "Type");
    this.id = Constraints.constrainNotNull(in_id, "ID");
    this.title = Constraints.constrainNotNull(in_title, "Title");
    this.contents = Constraints.constrainNotNull(in_contents, "Contents");
    this.sections = Constraints.constrainNotNull(in_sections, "Content");

    this.numbered_sections = new HashMap<SASectionNumber, SASection>();
    for (final SASection s : this.sections.getElements()) {
      assert this.numbered_sections.containsKey(s.getNumber()) == false;
      this.numbered_sections.put(s.getNumber(), s);
    }
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

  public @Nonnull Option<SPartContents> getContents()
  {
    return this.contents;
  }

  /**
   * @return The part ID
   */

  public @Nonnull Option<SAID> getID()
  {
    return this.id;
  }

  /**
   * @return The part number
   */

  public @Nonnull SAPartNumber getNumber()
  {
    return this.number;
  }

  /**
   * The section with the given number.
   * 
   * @param n
   *          The number
   * @return The section, if any
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public @Nonnull Option<SASection> getSection(
    final @Nonnull SASectionNumber n)
    throws ConstraintError
  {
    Constraints.constrainNotNull(n, "Number");
    if (this.numbered_sections.containsKey(n)) {
      return Option.some(this.numbered_sections.get(n));
    }
    return Option.none();
  }

  /**
   * @return The part sections
   */

  public @Nonnull SNonEmptyList<SASection> getSections()
  {
    return this.sections;
  }

  /**
   * @return The part's title
   */

  public @Nonnull SAPartTitle getTitle()
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
    throws ConstraintError,
      Exception
  {
    return v.visitPart(this);
  }
}
