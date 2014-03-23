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
 * A table header.
 */

@Immutable public final class STableHead
{
  /**
   * Construct a new table header.
   * 
   * @param header
   *          The header
   * @return A new table header
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull STableHead tableHead(
    final @Nonnull SNonEmptyList<STableColumnName> header)
    throws ConstraintError
  {
    return new STableHead(header);
  }

  private final @Nonnull SNonEmptyList<STableColumnName> header;

  private STableHead(
    final @Nonnull SNonEmptyList<STableColumnName> in_header)
    throws ConstraintError
  {
    this.header = Constraints.constrainNotNull(in_header, "Header");
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
    final STableHead other = (STableHead) obj;
    return this.header.equals(other.header);
  }

  /**
   * @return The table column names
   */

  public @Nonnull SNonEmptyList<STableColumnName> getHeader()
  {
    return this.header;
  }

  @Override public int hashCode()
  {
    return this.header.hashCode();
  }

}
