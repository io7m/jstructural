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

/**
 * A section number with no other components.
 */

public final class SASectionNumberS extends SASectionNumber
{
  private final int section;

  /**
   * Construct a new section number
   * 
   * @param in_section
   *          The section number
   * @throws ConstraintError
   *           If any parameter is outside of the range
   *           <code>[1, {@link Integer#MAX_VALUE}]</code>
   */

  public SASectionNumberS(
    final int in_section)
    throws ConstraintError
  {
    this.section =
      Constraints.constrainRange(in_section, 1, Integer.MAX_VALUE, "Section");
  }

  /**
   * @return The section number
   */

  public int getSection()
  {
    return this.section;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SASectionNumberWithoutPart section=");
    builder.append(this.section);
    builder.append("]");
    return builder.toString();
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.section;
    return result;
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
    final SASectionNumberS other = (SASectionNumberS) obj;
    if (this.section != other.section) {
      return false;
    }
    return true;
  }

  @Override <T> T sectionNumberAccept(
    final @Nonnull SASectionNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitSectionNumberWithoutPart(this);
  }
}
