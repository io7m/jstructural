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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

/**
 * A formal item title.
 */

@Immutable public final class SAFormalItemTitle
{
  private final String             actual;
  private final SAFormalItemNumber number;

  SAFormalItemTitle(
    final SAFormalItemNumber in_number,
    final String in_actual)
  {
    this.number = NullCheck.notNull(in_number, "Number");
    this.actual = NullCheck.notNull(in_actual, "Actual");
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
    final SAFormalItemTitle other = (SAFormalItemTitle) obj;
    return this.actual.equals(other.actual) && this.number.equals(other.number);
  }

  /**
   * @return The text
   */

  public String getActual()
  {
    return this.actual;
  }

  /**
   * @return The formal item number (note that formal items share numbers with
   * paragraphs)
   */

  public SAFormalItemNumber getNumber()
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
    builder.append("[SAFormalItemTitle actual=");
    builder.append(this.actual);
    builder.append(" number=");
    builder.append(this.number);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
