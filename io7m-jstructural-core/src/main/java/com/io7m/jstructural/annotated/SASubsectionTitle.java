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
 * A subsection title.
 */

@Immutable public final class SASubsectionTitle
{
  private final @Nonnull String             actual;
  private final @Nonnull SASubsectionNumber number;

  SASubsectionTitle(
    final @Nonnull SASubsectionNumber in_number,
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
    final SASubsectionTitle other = (SASubsectionTitle) obj;
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
   * @return The subsection number
   */

  public @Nonnull SASubsectionNumber getNumber()
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
    builder.append("[SASubsectionTitle actual=");
    builder.append(this.actual);
    builder.append(" number=");
    builder.append(this.number);
    builder.append("]");
    return builder.toString();
  }
}
