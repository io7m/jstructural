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

import com.io7m.junreachable.UnreachableCodeException;
import net.jcip.annotations.Immutable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * XML schema details.
 */

@Immutable public final class SXML
{
  /**
   * The XML URI for structural documents.
   */

  public static final URI XML_URI;

  static {
    try {
      XML_URI = new URI("http://schemas.io7m.com/structural/2.1.0");
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private SXML()
  {
    throw new UnreachableCodeException();
  }
}
