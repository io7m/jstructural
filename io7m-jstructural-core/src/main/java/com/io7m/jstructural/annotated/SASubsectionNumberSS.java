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
 * A subsection number without a part.
 */

@Immutable public final class SASubsectionNumberSS extends SASubsectionNumber
{
  private final int section;
  private final int subsection;

  /**
   * Construct a new subsection number
   *
   * @param in_section    The section number
   * @param in_subsection The subsection number
   */

  public SASubsectionNumberSS(
    final int in_section,
    final int in_subsection)
  {
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
    final SASubsectionNumberSS other = (SASubsectionNumberSS) obj;
    if (this.section != other.section) {
      return false;
    }
    return this.subsection == other.subsection;
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
    result = (prime * result) + this.section;
    result = (prime * result) + this.subsection;
    return result;
  }

  @Override public <T> T subsectionNumberAccept(
    final SASubsectionNumberVisitor<T> v)
    throws Exception
  {
    return v.visitSubsectionNumberSS(this);
  }

  @SuppressWarnings("boxing") @Override public String subsectionNumberFormat()
  {
    final String r = String.format("%d.%d", this.section, this.subsection);
    assert r != null;
    return r;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SASubsectionNumberPSS section=");
    builder.append(this.section);
    builder.append(" subsection=");
    builder.append(this.subsection);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
