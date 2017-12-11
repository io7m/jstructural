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

package com.io7m.jstructural.probe.api;

import com.io7m.jstructural.formats.SFormatDescription;
import com.io7m.jstructural.probe.spi.SPIProbeRequest;
import com.io7m.jstructural.probe.spi.SPIProbeType;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The default {@link SProbesType} implementation.
 */

public final class SProbes implements SProbesType
{
  private final ServiceLoader<SPIProbeType> loader;

  private SProbes()
  {
    this.loader = ServiceLoader.load(SPIProbeType.class);
  }

  /**
   * @return A probe interface
   */

  public static SProbesType create()
  {
    return new SProbes();
  }

  @Override
  public Optional<SFormatDescription> probeURI(
    final Path base_directory,
    final URI path,
    final SProbeStreamProviderType streams)
    throws IOException
  {
    Objects.requireNonNull(base_directory, "Base directory");
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(streams, "Streams");

    final Iterator<SPIProbeType> iter = this.loader.iterator();
    while (iter.hasNext()) {
      final SPIProbeType probe = iter.next();
      final Optional<SFormatDescription> r =
        probe.probe(SPIProbeRequest.builder()
                      .setBaseDirectory(base_directory)
                      .setUri(path)
                      .setStreams(streams::open)
                      .build());
      if (r.isPresent()) {
        return r;
      }
    }

    return Optional.empty();
  }
}
