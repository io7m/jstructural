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
 * An ID attribute, unique over a document.
 */

@Immutable public final class SAID
{
  private final String actual;

  /**
   * Construct a new ID.
   *
   * @param in_actual The ID text
   */

  public SAID(
    final String in_actual)
  {
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
    final SAID other = (SAID) obj;
    return this.actual.equals(other.actual);
  }

  /**
   * @return The ID text
   */

  public String getActual()
  {
    return this.actual;
  }

  @Override public int hashCode()
  {
    return this.actual.hashCode();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAID ");
    builder.append(this.actual);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
