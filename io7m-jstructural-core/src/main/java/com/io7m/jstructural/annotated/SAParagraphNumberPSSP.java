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

import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A paragraph number consisting of a part, section, subsection, and
 * paragraph.
 */

public final class SAParagraphNumberPSSP extends SAParagraphNumber
{
  private final int paragraph;
  private final int part;
  private final int section;
  private final int subsection;

  /**
   * Construct a new paragraph number
   * 
   * @param in_part
   *          The part number
   * @param in_section
   *          The section number
   * @param in_subsection
   *          The subsection number
   * @param in_paragraph
   *          The paragraph number
   */

  public SAParagraphNumberPSSP(
    final int in_part,
    final int in_section,
    final int in_subsection,
    final int in_paragraph)
  {
    this.part =
      (int) RangeCheck.checkIncludedIn(
        in_part,
        "Part number",
        RangeCheck.POSITIVE_INTEGER,
        "Valid part number range");
    this.section =
      (int) RangeCheck.checkIncludedIn(
        in_section,
        "Section number",
        RangeCheck.POSITIVE_INTEGER,
        "Valid section number range");
    this.subsection =
      (int) RangeCheck.checkIncludedIn(
        in_subsection,
        "Subsection number",
        RangeCheck.POSITIVE_INTEGER,
        "Valid subsection number range");
    this.paragraph =
      (int) RangeCheck.checkIncludedIn(
        in_paragraph,
        "Paragraph number",
        RangeCheck.POSITIVE_INTEGER,
        "Valid paragraph number range");
  }

  @SuppressWarnings({ "boxing", "null", "synthetic-access" }) @Override public
    int
    compareTo(
      final SAParagraphNumber o)
  {
    try {
      return o.paragraphNumberAccept(new SAParagraphNumberVisitor<Integer>() {
        @Override public Integer visitParagraphNumberPSP(
          final SAParagraphNumberPSP p)
          throws Exception
        {
          final int rpart =
            Integer.compare(SAParagraphNumberPSSP.this.part, p.getPart());
          if (rpart == 0) {
            final int rsect =
              Integer.compare(
                SAParagraphNumberPSSP.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberPSSP.this.paragraph,
                p.getParagraph());
            }
            return rsect;
          }
          return rpart;
        }

        @Override public Integer visitParagraphNumberPSSP(
          final SAParagraphNumberPSSP p)
          throws Exception
        {
          final int rpart =
            Integer.compare(SAParagraphNumberPSSP.this.part, p.getPart());
          if (rpart == 0) {
            final int rsect =
              Integer.compare(
                SAParagraphNumberPSSP.this.section,
                p.getSection());
            if (rsect == 0) {
              final int rsubs =
                Integer.compare(
                  SAParagraphNumberPSSP.this.subsection,
                  p.subsection);
              if (rsubs == 0) {
                return Integer.compare(
                  SAParagraphNumberPSSP.this.paragraph,
                  p.getParagraph());
              }
              return rsubs;
            }
            return rsect;
          }
          return rpart;
        }

        @Override public Integer visitParagraphNumberSP(
          final SAParagraphNumberSP p)
          throws Exception
        {
          final int rsect =
            Integer.compare(
              SAParagraphNumberPSSP.this.section,
              p.getSection());
          if (rsect == 0) {
            return Integer.compare(
              SAParagraphNumberPSSP.this.paragraph,
              p.getParagraph());
          }
          return rsect;
        }

        @Override public Integer visitParagraphNumberSSP(
          final SAParagraphNumberSSP p)
          throws Exception
        {
          final int rsect =
            Integer.compare(
              SAParagraphNumberPSSP.this.section,
              p.getSection());
          if (rsect == 0) {
            final int rsubs =
              Integer.compare(
                SAParagraphNumberPSSP.this.subsection,
                p.getSubsection());
            if (rsubs == 0) {
              return Integer.compare(
                SAParagraphNumberPSSP.this.paragraph,
                p.getParagraph());
            }
            return rsubs;
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
    final SAParagraphNumberPSSP other = (SAParagraphNumberPSSP) obj;
    if (this.paragraph != other.paragraph) {
      return false;
    }
    if (this.part != other.part) {
      return false;
    }
    if (this.section != other.section) {
      return false;
    }
    if (this.subsection != other.subsection) {
      return false;
    }
    return true;
  }

  /**
   * @return The paragraph number
   */

  @Override public int getParagraph()
  {
    return this.paragraph;
  }

  /**
   * @return The part number
   */

  public int getPart()
  {
    return this.part;
  }

  /**
   * @return The section number
   */

  public int getSection()
  {
    return this.section;
  }

  /**
   * @return The subsection number
   */

  public int getSubsection()
  {
    return this.subsection;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.paragraph;
    result = (prime * result) + this.part;
    result = (prime * result) + this.section;
    result = (prime * result) + this.subsection;
    return result;
  }

  @Override public <T> T paragraphNumberAccept(
    final SAParagraphNumberVisitor<T> v)
    throws Exception
  {
    return v.visitParagraphNumberPSSP(this);
  }

  @SuppressWarnings("boxing") @Override public String paragraphNumberFormat()
  {
    final String r =
      String.format(
        "%d.%d.%d.%d",
        this.part,
        this.section,
        this.subsection,
        this.paragraph);
    assert r != null;
    return r;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAParagraphNumberPSSP paragraph=");
    builder.append(this.paragraph);
    builder.append(" part=");
    builder.append(this.part);
    builder.append(" section=");
    builder.append(this.section);
    builder.append(" subsection=");
    builder.append(this.subsection);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
