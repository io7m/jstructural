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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jstructural.core.SNonEmptyList;
import com.io7m.jstructural.core.SSectionContents;

/**
 * The type of sections containing paragraphs.
 */

@Immutable public final class SASectionWithParagraphs extends SASection
{
  private final @Nonnull SNonEmptyList<SASubsectionContent> subsections;

  /**
   * Construct a new section with top-level subsection content.
   * 
   * @param in_number
   *          The section number
   * @param in_type
   *          The type attribute
   * @param in_id
   *          The ID
   * @param in_title
   *          The section title
   * @param in_contents
   *          The section table of contents
   * @param in_subsections
   *          The subsection content
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SASectionWithParagraphs(
    final @Nonnull SASectionNumber in_number,
    final @Nonnull Option<String> in_type,
    final @Nonnull Option<SAID> in_id,
    final @Nonnull SASectionTitle in_title,
    final @Nonnull Option<SSectionContents> in_contents,
    final @Nonnull SNonEmptyList<SASubsectionContent> in_subsections)
    throws ConstraintError
  {
    super(in_number, in_type, in_id, in_title, in_contents);
    this.subsections =
      Constraints.constrainNotNull(in_subsections, "Subsections");
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
    final SASectionWithParagraphs other = (SASectionWithParagraphs) obj;
    return this.subsections.equals(other.subsections);
  }

  /**
   * @return The section content
   */

  public @Nonnull SNonEmptyList<SASubsectionContent> getSectionContent()
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

  @Override public <A> A sectionAccept(
    final @Nonnull SASectionVisitor<A> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSectionWithParagraphs(this);
  }

  @Override public <T> T targetContentAccept(
    final @Nonnull SAIDTargetContentVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSection(this);
  }
}
