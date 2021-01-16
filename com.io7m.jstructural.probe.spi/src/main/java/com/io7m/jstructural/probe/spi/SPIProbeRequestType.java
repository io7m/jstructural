/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.probe.spi;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.nio.file.Path;

import static org.immutables.value.Value.Immutable;

/**
 * A request to probe the format of a given file.
 */

@ImmutablesStyleType
@Immutable
public interface SPIProbeRequestType
{
  /**
   * Specify the base directory for parsing. Parsers are not allowed to access
   * files or directories in any ancestor of this directory.
   *
   * @return The base directory against which other relative filenames will be
   * resolved
   */

  @Value.Parameter
  Path baseDirectory();

  /**
   * @return The URI of the file being probed, for diagnostic purposes
   */

  @Value.Parameter
  URI uri();

  /**
   * @return A supplier of input streams for the file {@link #uri()}
   */

  @Value.Parameter
  SPIProbeStreamProviderType streams();
}