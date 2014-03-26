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

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

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
   * @param in_section
   *          The section number
   * @param in_paragraph
   *          The paragraph number
   * @throws ConstraintError
   *           If any parameter is outside of the range
   *           <code>[1, {@link Integer#MAX_VALUE}]</code>
   */

  public SAParagraphNumberSP(
    final int in_section,
    final int in_paragraph)
    throws ConstraintError
  {
    this.section =
      Constraints.constrainRange(in_section, 1, Integer.MAX_VALUE, "Section");
    this.paragraph =
      Constraints.constrainRange(
        in_paragraph,
        1,
        Integer.MAX_VALUE,
        "Paragraph");
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
    final SAParagraphNumberSP other = (SAParagraphNumberSP) obj;
    if (this.paragraph != other.paragraph) {
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

  public int getParagraph()
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

  @Override <T> T paragraphNumberAccept(
    final SAParagraphNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitParagraphNumberSP(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAParagraphNumberSP paragraph=");
    builder.append(this.paragraph);
    builder.append(" section=");
    builder.append(this.section);
    builder.append("]");
    return builder.toString();
  }
}
