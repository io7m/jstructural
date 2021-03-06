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
import com.io7m.jranges.Ranges;
import net.jcip.annotations.Immutable;

/**
 * A subsection number consisting of a part, section, and subsection.
 */

@Immutable public final class SASubsectionNumberPSS extends SASubsectionNumber
{
  private final int part;
  private final int section;
  private final int subsection;

  /**
   * Construct a new subsection number
   *
   * @param in_part       The part number
   * @param in_section    The section number
   * @param in_subsection The subsection number
   */

  public SASubsectionNumberPSS(
    final int in_part,
    final int in_section,
    final int in_subsection)
  {
    this.part = RangeCheck.checkIncludedInInteger(
      in_part,
      "Part number",
      Ranges.POSITIVE_INTEGER,
      "Valid part number range");
    this.section = RangeCheck.checkIncludedInInteger(
      in_section,
      "Section number",
      Ranges.POSITIVE_INTEGER,
      "Valid section number range");
    this.subsection = RangeCheck.checkIncludedInInteger(
      in_subsection,
      "Subsection number",
      Ranges.POSITIVE_INTEGER,
      "Valid subsection number range");
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
    final SASubsectionNumberPSS other = (SASubsectionNumberPSS) obj;
    if (this.part != other.part) {
      return false;
    }
    if (this.section != other.section) {
      return false;
    }
    return this.subsection == other.subsection;
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
    throws Exception
  {
    return v.visitSubsectionNumberPSS(this);
  }

  @SuppressWarnings("boxing") @Override public String subsectionNumberFormat()
  {
    final String r =
      String.format("%d.%d.%d", this.part, this.section, this.subsection);
    assert r != null;
    return r;
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
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
