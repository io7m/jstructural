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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * The type of file format probes.
 */

public interface SProbesType
{
  /**
   * Try to determine the format of the file at the given path.
   *
   * @param base_directory The base directory
   * @param path           The path
   *
   * @return The format, or nothing if no format could be determined
   *
   * @throws IOException On I/O errors
   */

  default Optional<SFormatDescription> probeFile(
    final Path base_directory,
    final Path path)
    throws IOException
  {
    Objects.requireNonNull(base_directory, "Base directory");
    Objects.requireNonNull(path, "Path");
    return this.probeFileWith(
      base_directory,
      path,
      () -> Files.newInputStream(path));
  }

  /**
   * Try to determine the format of the file at the given path.
   *
   * @param base_directory The base directory
   * @param path           The path
   * @param streams        A stream provider
   *
   * @return The format, or nothing if no format could be determined
   *
   * @throws IOException On I/O errors
   */

  default Optional<SFormatDescription> probeFileWith(
    final Path base_directory,
    final Path path,
    final SProbeStreamProviderType streams)
    throws IOException
  {
    Objects.requireNonNull(base_directory, "Base directory");
    Objects.requireNonNull(path, "Path");
    Objects.requireNonNull(streams, "Streams");
    return this.probeURI(base_directory, path.toUri(), streams);
  }

  /**
   * Try to determine the format of the file at the given URI.
   *
   * @param base_directory The base directory
   * @param path           The path
   * @param streams        A stream provider
   *
   * @return The format, or nothing if no format could be determined
   *
   * @throws IOException On I/O errors
   */

  Optional<SFormatDescription> probeURI(
    Path base_directory,
    URI path,
    SProbeStreamProviderType streams)
    throws IOException;
}
