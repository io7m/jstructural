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
 * A part number.
 */

public final class SAPartNumber implements SASegmentNumber
{
  private final int actual;

  SAPartNumber(
    final int in_actual)
    throws ConstraintError
  {
    this.actual =
      Constraints.constrainRange(
        in_actual,
        1,
        Integer.MAX_VALUE,
        "Part number");
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
    final SAPartNumber other = (SAPartNumber) obj;
    return this.actual == other.actual;
  }

  /**
   * @return The actual part number
   */

  public int getActual()
  {
    return this.actual;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.actual;
    return result;
  }

  @Override public <T> T segmentNumberAccept(
    final SASegmentNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitPartNumber(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAPartNumber ");
    builder.append(this.actual);
    builder.append("]");
    return builder.toString();
  }
}
