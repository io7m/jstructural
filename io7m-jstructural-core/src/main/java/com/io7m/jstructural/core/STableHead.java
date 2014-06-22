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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A table header.
 */

public final class STableHead
{
  /**
   * Construct a new table header.
   * 
   * @param header
   *          The header
   * @return A new table header
   */

  public static STableHead tableHead(
    final SNonEmptyList<STableColumnName> header)
  {
    return new STableHead(header);
  }

  private final SNonEmptyList<STableColumnName> header;

  private STableHead(
    final SNonEmptyList<STableColumnName> in_header)
  {
    this.header = NullCheck.notNull(in_header, "Header");
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
    final STableHead other = (STableHead) obj;
    return this.header.equals(other.header);
  }

  /**
   * @return The table column names
   */

  public SNonEmptyList<STableColumnName> getHeader()
  {
    return this.header;
  }

  @Override public int hashCode()
  {
    return this.header.hashCode();
  }

}
