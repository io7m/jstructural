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

package com.io7m.jstructural.maven_plugin;

import com.io7m.jstructural.tools.JSCMain;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which generates XHTML documentation from the given documentation
 * sources.
 */

@Mojo(name = "transform", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public final class JStructuralMojo extends AbstractMojo
{
  /**
   * The pagination type.
   */

  @Parameter(name = "pagination", required = true)
  private XHTMLPagination pagination = XHTMLPagination.XHTML_MULTI;

  /**
   * The input document. All other files referenced from this document are
   * resolved relative to the document's location.
   */

  @Parameter(name = "documentFile", required = true)
  private String documentFile;

  /**
   * The directory that will be used to contain generated XHTML files.
   */

  @Parameter(name = "outputDirectory", required = true)
  private String outputDirectory;

  /**
   * An XML file containing branding for the generated documents.
   */

  @Parameter(name = "brandFile", required = true)
  private String brandFile;

  /**
   * Parameter to allow skipping of the generation.
   */

  @Parameter( property = "jstructural.skip", defaultValue = "false")
  private boolean skip;

  /**
   * Construct a plugin mojo.
   */

  public JStructuralMojo()
  {

  }

  @Override public void execute()
    throws MojoExecutionException
  {
    try {
      if (this.documentFile == null) {
        throw new IllegalArgumentException("input document not specified");
      }
      if (this.outputDirectory == null) {
        throw new IllegalArgumentException("output directory not specified");
      }
      if (this.pagination == null) {
        throw new IllegalArgumentException("pagination type not specified");
      }

      final Log log = this.getLog();
      log.info("Transform document   : " + this.documentFile);
      log.info("Transform directory  : " + this.outputDirectory);
      log.info("Transform brand      : " + this.brandFile);
      log.info("Transform pagination : " + this.pagination);
      log.info("Skipping             : " + this.skip);

      if (this.skip) {
        return;
      }

      final List<String> args = new ArrayList<String>();
      switch (this.pagination) {
        case XHTML_SINGLE: {
          args.add("--xhtml-single");
          break;
        }
        case XHTML_MULTI: {
          args.add("--xhtml-multi");
          break;
        }
      }
      args.add(this.documentFile);
      args.add(this.outputDirectory);

      if (this.brandFile != null) {
        args.add("--xhtml-body-start");
        args.add(this.brandFile);
      }

      final String[] args_array = new String[args.size()];
      for (int index = 0; index < args_array.length; ++index) {
        args_array[index] = args.get(index);
      }

      log.debug("executing: " + args);

      JSCMain.run(args_array);

    } catch (final FileNotFoundException e) {
      throw new MojoExecutionException("File not found", e);
    } catch (final IOException e) {
      throw new MojoExecutionException("I/O error", e);
    } catch (final Throwable e) {
      throw new MojoExecutionException("Transform error", e);
    }
  }
}
