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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.Ranges;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A paragraph number consisting of a section, and paragraph.
 */

public final class SAParagraphNumberSP extends SAParagraphNumber
{
  private final int paragraph;
  private final int section;

  /**
   * Construct a new paragraph number
   *
   * @param in_section   The section number
   * @param in_paragraph The paragraph number
   */

  public SAParagraphNumberSP(
    final int in_section,
    final int in_paragraph)
  {
    this.section = RangeCheck.checkIncludedInInteger(
      in_section,
      "Section number",
      Ranges.POSITIVE_INTEGER,
      "Valid section number range");
    this.paragraph = RangeCheck.checkIncludedInInteger(
      in_paragraph,
      "Paragraph number",
      Ranges.POSITIVE_INTEGER,
      "Valid paragraph number range");
  }

  @SuppressWarnings({ "boxing", "null", "synthetic-access" }) @Override
  public int compareTo(
    final @Nullable SAParagraphNumber o)
  {
    try {
      return NullCheck.notNull(o, "Other").paragraphNumberAccept(
        new SAParagraphNumberVisitor<Integer>()
        {
          @Override public Integer visitParagraphNumberPSP(
            final SAParagraphNumberPSP p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAParagraphNumberSP.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberSP.this.paragraph, p.getParagraph());
            }
            return rsect;
          }

          @Override public Integer visitParagraphNumberPSSP(
            final SAParagraphNumberPSSP p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAParagraphNumberSP.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberSP.this.paragraph, p.getParagraph());
            }
            return rsect;
          }

          @Override public Integer visitParagraphNumberSP(
            final SAParagraphNumberSP p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAParagraphNumberSP.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberSP.this.paragraph, p.getParagraph());
            }
            return rsect;
          }

          @Override public Integer visitParagraphNumberSSP(
            final SAParagraphNumberSSP p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAParagraphNumberSP.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberSP.this.paragraph, p.getParagraph());
            }
            return rsect;
          }
        }).intValue();
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
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
    final SAParagraphNumberSP other = (SAParagraphNumberSP) obj;
    if (this.paragraph != other.paragraph) {
      return false;
    }
    return this.section == other.section;
  }

  /**
   * @return The paragraph number
   */

  @Override public int getParagraph()
  {
    return this.paragraph;
  }

  /**
   * @return The section number
   */

  public int getSection()
  {
    return this.section;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.paragraph;
    result = (prime * result) + this.section;
    return result;
  }

  @Override public <T> T paragraphNumberAccept(
    final SAParagraphNumberVisitor<T> v)
    throws Exception
  {
    return v.visitParagraphNumberSP(this);
  }

  @SuppressWarnings("boxing") @Override public String paragraphNumberFormat()
  {
    final String r = String.format("%d.%d", this.section, this.paragraph);
    assert r != null;
    return r;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAParagraphNumberSP paragraph=");
    builder.append(this.paragraph);
    builder.append(" section=");
    builder.append(this.section);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
