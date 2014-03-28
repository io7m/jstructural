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

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;

/**
 * A paragraph number consisting of a part, section, and paragraph.
 */

public final class SAParagraphNumberPSP extends SAParagraphNumber
{
  private final int paragraph;
  private final int part;
  private final int section;

  /**
   * Construct a new paragraph number
   * 
   * @param in_part
   *          The part number
   * @param in_section
   *          The section number
   * @param in_paragraph
   *          The paragraph number
   * @throws ConstraintError
   *           If any parameter is outside of the range
   *           <code>[1, {@link Integer#MAX_VALUE}]</code>
   */

  public SAParagraphNumberPSP(
    final int in_part,
    final int in_section,
    final int in_paragraph)
    throws ConstraintError
  {
    this.part =
      Constraints.constrainRange(in_part, 1, Integer.MAX_VALUE, "Part");
    this.section =
      Constraints.constrainRange(in_section, 1, Integer.MAX_VALUE, "Section");
    this.paragraph =
      Constraints.constrainRange(
        in_paragraph,
        1,
        Integer.MAX_VALUE,
        "Paragraph");
  }

  @SuppressWarnings({ "boxing", "synthetic-access" }) @Override public
    int
    compareTo(
      final SAParagraphNumber o)
  {
    try {
      return o.paragraphNumberAccept(new SAParagraphNumberVisitor<Integer>() {
        @Override public Integer visitParagraphNumberPSP(
          final @Nonnull SAParagraphNumberPSP p)
          throws ConstraintError,
            Exception
        {
          final int rpart =
            Integer.compare(SAParagraphNumberPSP.this.part, p.part);
          if (rpart == 0) {
            final int rsect =
              Integer.compare(SAParagraphNumberPSP.this.section, p.section);
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberPSP.this.paragraph,
                p.paragraph);
            }
            return rsect;
          }
          return rpart;
        }

        @Override public Integer visitParagraphNumberPSSP(
          final @Nonnull SAParagraphNumberPSSP p)
          throws ConstraintError,
            Exception
        {
          final int rpart =
            Integer.compare(SAParagraphNumberPSP.this.part, p.getPart());
          if (rpart == 0) {
            final int rsect =
              Integer.compare(
                SAParagraphNumberPSP.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAParagraphNumberPSP.this.paragraph,
                p.getParagraph());
            }
            return rsect;
          }
          return rpart;
        }

        @Override public Integer visitParagraphNumberSP(
          final @Nonnull SAParagraphNumberSP p)
          throws ConstraintError,
            Exception
        {
          final int rsect =
            Integer.compare(SAParagraphNumberPSP.this.section, p.getSection());
          if (rsect == 0) {
            return Integer.compare(
              SAParagraphNumberPSP.this.paragraph,
              p.getParagraph());
          }
          return rsect;
        }

        @Override public Integer visitParagraphNumberSSP(
          final @Nonnull SAParagraphNumberSSP p)
          throws ConstraintError,
            Exception
        {
          final int rsect =
            Integer.compare(SAParagraphNumberPSP.this.section, p.getSection());
          if (rsect == 0) {
            return Integer.compare(
              SAParagraphNumberPSP.this.paragraph,
              p.getParagraph());
          }
          return rsect;
        }
      })
        .intValue();
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
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
    final SAParagraphNumberPSP other = (SAParagraphNumberPSP) obj;
    if (this.paragraph != other.paragraph) {
      return false;
    }
    if (this.part != other.part) {
      return false;
    }
    if (this.section != other.section) {
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

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.paragraph;
    result = (prime * result) + this.part;
    result = (prime * result) + this.section;
    return result;
  }

  @Override public <T> T paragraphNumberAccept(
    final SAParagraphNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitParagraphNumberPSP(this);
  }

  @SuppressWarnings("boxing") @Override public String paragraphNumberFormat()
  {
    return String.format("%d.%d.%d", this.part, this.section, this.paragraph);
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
    builder.append("]");
    return builder.toString();
  }
}
