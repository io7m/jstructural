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

package com.io7m.jstructural;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A table summary.
 */

@Immutable public final class STableSummary
{
  /**
   * Construct a new table summary.
   * 
   * @param text
   *          The summary text
   * @return A new table summary
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull STableSummary tableSummary(
    final @Nonnull String text)
    throws ConstraintError
  {
    return new STableSummary(text);
  }

  private final @Nonnull String text;

  private STableSummary(
    final @Nonnull String in_text)
    throws ConstraintError
  {
    this.text = Constraints.constrainNotNull(in_text, "Text");
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
    final STableSummary other = (STableSummary) obj;
    return this.text.equals(other.text);
  }

  /**
   * @return The summary text
   */

  public @Nonnull String getText()
  {
    return this.text;
  }

  @Override public int hashCode()
  {
    return this.text.hashCode();
  }
}
