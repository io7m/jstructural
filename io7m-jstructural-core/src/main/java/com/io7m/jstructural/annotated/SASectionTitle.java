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
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A section title.
 */

@Immutable public final class SASectionTitle
{
  private final @Nonnull String          actual;
  private final @Nonnull SASectionNumber number;

  /**
   * Construct a new section title.
   * 
   * @param in_number
   *          The section number
   * @param in_actual
   *          The section title
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public SASectionTitle(
    final @Nonnull SASectionNumber in_number,
    final @Nonnull String in_actual)
    throws ConstraintError
  {
    this.number = Constraints.constrainNotNull(in_number, "Number");
    this.actual = Constraints.constrainNotNull(in_actual, "Actual");
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
    final SASectionTitle other = (SASectionTitle) obj;
    return this.actual.equals(other.actual)
      && this.number.equals(other.number);
  }

  /**
   * @return The text
   */

  public @Nonnull String getActual()
  {
    return this.actual;
  }

  /**
   * @return The number
   */

  public @Nonnull SASectionNumber getNumber()
  {
    return this.number;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.actual.hashCode();
    result = (prime * result) + this.number.hashCode();
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SASectionTitle actual=");
    builder.append(this.actual);
    builder.append(" number=");
    builder.append(this.number);
    builder.append("]");
    return builder.toString();
  }
}
