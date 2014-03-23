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
 * A table column name.
 */

@Immutable public final class STableColumnName
{
  /**
   * Construct a new table column name.
   * 
   * @param text
   *          The column name
   * @return A new table column name
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull STableColumnName tableColumnName(
    final @Nonnull String text)
    throws ConstraintError
  {
    return new STableColumnName(text);
  }

  private final @Nonnull String text;

  private STableColumnName(
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
    final STableColumnName other = (STableColumnName) obj;
    return this.text.equals(other.text);
  }

  /**
   * @return The column name
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
