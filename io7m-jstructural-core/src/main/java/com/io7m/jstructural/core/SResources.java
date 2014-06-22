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
import java.net.URISyntaxException;

import com.io7m.junreachable.UnreachableCodeException;

/**
 * Access to various resources used by processed documents.
 */

public final class SResources
{
  /**
   * The CSS colour file name.
   */

  public static final String CSS_COLOUR_NAME = "jstructural-2.0.0-colour.css";

  /**
   * The CSS layout file name.
   */

  public static final String CSS_LAYOUT_NAME = "jstructural-2.0.0-layout.css";

  /**
   * @return The CSS colour file
   */

  public static URI getColourCSSLocation()
  {
    try {
      final URI r =
        SResources.class.getResource(
          "/com/io7m/jstructural/" + SResources.CSS_COLOUR_NAME).toURI();
      assert r != null;
      return r;
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * @return The CSS layout file
   */

  public static URI getLayoutCSSLocation()
  {
    try {
      final URI r =
        SResources.class.getResource(
          "/com/io7m/jstructural/" + SResources.CSS_LAYOUT_NAME).toURI();
      assert r != null;
      return r;
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  private SResources()
  {
    throw new UnreachableCodeException();
  }
}
