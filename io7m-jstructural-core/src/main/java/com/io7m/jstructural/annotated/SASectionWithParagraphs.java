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
import com.io7m.jstructural.core.SSectionContents;
import net.jcip.annotations.Immutable;

import java.util.List;

/**
 * The type of sections containing paragraphs.
 */

@Immutable public final class SASectionWithParagraphs extends SASection
{
  private final SNonEmptyList<SASubsectionContent> subsections;

  /**
   * Construct a new section with top-level subsection content.
   *
   * @param in_number      The section number
   * @param in_type        The type attribute
   * @param in_id          The ID
   * @param in_title       The section title
   * @param in_contents    The section table of contents
   * @param in_subsections The subsection content
   * @param in_footnotes   The footnotes
   */

  public SASectionWithParagraphs(
    final SASectionNumber in_number,
    final OptionType<String> in_type,
    final OptionType<SAID> in_id,
    final SASectionTitle in_title,
    final OptionType<SSectionContents> in_contents,
    final SNonEmptyList<SASubsectionContent> in_subsections,
    final List<SAFootnote> in_footnotes)
  {
    super(in_number, in_type, in_id, in_title, in_contents, in_footnotes);
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
    final SASectionWithParagraphs other = (SASectionWithParagraphs) obj;
    return this.subsections.equals(other.subsections);
  }

  /**
   * @return The section content
   */

  public SNonEmptyList<SASubsectionContent> getSectionContent()
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
    final SASectionVisitor<A> v)
    throws Exception
  {
    return v.visitSectionWithParagraphs(this);
  }

  @Override public <T> T targetContentAccept(
    final SAIDTargetContentVisitor<T> v)
    throws Exception
  {
    return v.visitSection(this);
  }
}
