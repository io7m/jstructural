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

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * A document style.
 */

@Immutable public final class SDocumentStyle
{
  /**
   * Construct a document style.
   * 
   * @param uri
   *          The style URI
   * @return A new document style
   * @throws ConstraintError
   *           If any parameter is <code>null</code>
   */

  public static @Nonnull SDocumentStyle documentStyle(
    final @Nonnull URI uri)
    throws ConstraintError
  {
    return new SDocumentStyle(uri);
  }

  private final @Nonnull URI uri;

  private SDocumentStyle(
    final @Nonnull URI in_uri)
    throws ConstraintError
  {
    this.uri = Constraints.constrainNotNull(in_uri, "URI");
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
    final SDocumentStyle other = (SDocumentStyle) obj;
    return this.uri.equals(other.uri);
  }

  /**
   * @return The style URI
   */

  public @Nonnull URI getActual()
  {
    return this.uri;
  }

  @Override public int hashCode()
  {
    return this.uri.hashCode();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SDocumentStyle ");
    builder.append(this.uri);
    builder.append("]");
    return builder.toString();
  }
}
