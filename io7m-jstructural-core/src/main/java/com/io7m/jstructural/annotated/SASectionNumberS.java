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

/**
 * A section number with no other components.
 */

public final class SASectionNumberS extends SASectionNumber
{
  private final int section;

  /**
   * Construct a new section number
   *
   * @param in_section The section number
   */

  public SASectionNumberS(
    final int in_section)
  {
    this.section = RangeCheck.checkIncludedInInteger(
      in_section,
      "Section number",
      Ranges.POSITIVE_INTEGER,
      "Valid section number range");
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
    final SASectionNumberS other = (SASectionNumberS) obj;
    return this.section == other.section;
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
    result = (prime * result) + this.section;
    return result;
  }

  @Override public <T> T sectionNumberAccept(
    final SASectionNumberVisitor<T> v)
    throws Exception
  {
    return v.visitSectionNumberWithoutPart(this);
  }

  @SuppressWarnings("boxing") @Override public String sectionNumberFormat()
  {
    final String r = String.format("%d", this.section);
    assert r != null;
    return r;
  }

  @Override public <T> T segmentNumberAccept(
    final SASegmentNumberVisitor<T> v)
    throws Exception
  {
    return v.visitSectionNumber(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SASectionNumberWithoutPart section=");
    builder.append(this.section);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
