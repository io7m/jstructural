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
 * A table row.
 */

@Immutable public final class STableRow
{
  /**
   * Construct a new table row.
   * 
   * @param columns
   *          The columns
   * @return A new table row
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull STableRow tableRow(
    final @Nonnull SNonEmptyList<STableCell> columns)
    throws ConstraintError
  {
    return new STableRow(columns);
  }

  private final @Nonnull SNonEmptyList<STableCell> columns;

  private STableRow(
    final @Nonnull SNonEmptyList<STableCell> in_columns)
    throws ConstraintError
  {
    this.columns = Constraints.constrainNotNull(in_columns, "Columns");
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
    final STableRow other = (STableRow) obj;
    return this.columns.equals(other.columns);
  }

  /**
   * @return The table columns
   */

  public @Nonnull SNonEmptyList<STableCell> getColumns()
  {
    return this.columns;
  }

  @Override public int hashCode()
  {
    return this.columns.hashCode();
  }

}
