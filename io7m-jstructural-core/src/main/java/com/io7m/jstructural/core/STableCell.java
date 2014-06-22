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
 * A table cell.
 */

public final class STableCell
{
  /**
   * Construct a new table cell.
   * 
   * @param content
   *          The table cell content
   * @return A new table cell
   */

  public static STableCell tableCell(
    final SNonEmptyList<STableCellContent> content)
  {
    return new STableCell(content);
  }

  private final SNonEmptyList<STableCellContent> content;

  private STableCell(
    final SNonEmptyList<STableCellContent> in_content)
  {
    this.content = NullCheck.notNull(in_content, "Content");
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
    final STableCell other = (STableCell) obj;
    return this.content.equals(other.content);
  }

  /**
   * @return The table cell content
   */

  public SNonEmptyList<STableCellContent> getContent()
  {
    return this.content;
  }

  @Override public int hashCode()
  {
    return this.content.hashCode();
  }
}
