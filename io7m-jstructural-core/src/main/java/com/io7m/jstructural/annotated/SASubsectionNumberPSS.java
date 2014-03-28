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
 * A subsection number consisting of a part, section, and subsection.
 */

public final class SASubsectionNumberPSS extends SASubsectionNumber
{
  private final int part;
  private final int section;
  private final int subsection;

  /**
   * Construct a new subsection number
   * 
   * @param in_part
   *          The part number
   * @param in_section
   *          The section number
   * @param in_subsection
   *          The subsection number
   * @throws ConstraintError
   *           If any parameter is outside of the range
   *           <code>[1, {@link Integer#MAX_VALUE}]</code>
   */

  public SASubsectionNumberPSS(
    final int in_part,
    final int in_section,
    final int in_subsection)
    throws ConstraintError
  {
    this.part =
      Constraints.constrainRange(in_part, 1, Integer.MAX_VALUE, "Part");
    this.section =
      Constraints.constrainRange(in_section, 1, Integer.MAX_VALUE, "Section");
    this.subsection =
      Constraints.constrainRange(
        in_subsection,
        1,
        Integer.MAX_VALUE,
        "Subsection");
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
    final SASubsectionNumberPSS other = (SASubsectionNumberPSS) obj;
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
    result = (prime * result) + this.part;
    result = (prime * result) + this.section;
    result = (prime * result) + this.subsection;
    return result;
  }

  @Override public <T> T subsectionNumberAccept(
    final SASubsectionNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSubsectionNumberPSS(this);
  }

  @SuppressWarnings("boxing") @Override public
    String
    subsectionNumberFormat()
  {
    return String
      .format("%d.%d.%d", this.part, this.section, this.subsection);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SASubsectionNumberPSS section=");
    builder.append(this.section);
    builder.append(" part=");
    builder.append(this.part);
    builder.append(" subsection=");
    builder.append(this.subsection);
    builder.append("]");
    return builder.toString();
  }
}
