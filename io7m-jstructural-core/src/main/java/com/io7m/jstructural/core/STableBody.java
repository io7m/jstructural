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

package com.io7m.jstructural.core;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A table body.
 */

@Immutable public final class STableBody
{
  /**
   * Construct a new table body.
   * 
   * @param rows
   *          The rows
   * @return A new table rows
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull STableBody tableBody(
    final @Nonnull SNonEmptyList<STableRow> rows)
    throws ConstraintError
  {
    return new STableBody(rows);
  }

  private final @Nonnull SNonEmptyList<STableRow> rows;

  private STableBody(
    final @Nonnull SNonEmptyList<STableRow> in_rows)
    throws ConstraintError
  {
    this.rows = Constraints.constrainNotNull(in_rows, "Rows");
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
    final STableBody other = (STableBody) obj;
    return this.rows.equals(other.rows);
  }

  /**
   * @return The table rows
   */

  public @Nonnull SNonEmptyList<STableRow> getRows()
  {
    return this.rows;
  }

  @Override public int hashCode()
  {
    return this.rows.hashCode();
  }

}
