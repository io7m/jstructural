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

package com.io7m.jstructural.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.SortedMap;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import nu.xom.xinclude.BadParseAttributeException;
import nu.xom.xinclude.InclusionLoopException;
import nu.xom.xinclude.NoIncludeLocationException;
import nu.xom.xinclude.XIncludeException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.xml.sax.SAXException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SAnnotator;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SResources;
import com.io7m.jstructural.xom.SDocumentParser;
import com.io7m.jstructural.xom.SDocumentXHTMLWriterCallbacks;
import com.io7m.jstructural.xom.SDocumentXHTMLWriterMulti;
import com.io7m.jstructural.xom.SDocumentXHTMLWriterSingle;

/**
 * The <code>jstructural</code> compiler frontend.
 */

public final class JSCMain
{
  private static final @Nonnull Options OPTIONS;

  static {
    OPTIONS = JSCMain.makeOptions();
  }

  static @Nonnull Log getLog(
    final boolean debug)
  {
    final Properties p = new Properties();
    p.setProperty("com.io7m.jstructural.logs.jsc", "true");
    p.setProperty("com.io7m.jstructural.level", debug
      ? "LOG_DEBUG"
      : "LOG_INFO");
    return new Log(p, "com.io7m.jstructural", "jsc");
  }

  private static @Nonnull String getVersion()
  {
    final String pack = JSCMain.class.getPackage().getImplementationVersion();
    if (pack == null) {
      return "unavailable";
    }
    return pack;
  }

  private static SDocumentXHTMLWriterCallbacks getXHTMLWriterCallbacks()
  {
    return new SDocumentXHTMLWriterCallbacks() {
      @Override public void onBodyEnd(
        final @Nonnull Element body)
      {
        // Nothing
      }

      @Override public void onBodyStart(
        final @Nonnull Element body)
      {
        // Nothing
      }

      @Override public void onHead(
        final @Nonnull Element head)
      {
        // Nothing
      }
    };
  }

  /**
   * Main entry.
   * 
   * @param args
   *          Program arguments
   */

  public static void main(
    final String[] args)
  {
    final Log log = JSCMain.getLog(false);
    try {
      JSCMain.run(log, args);
    } catch (final Throwable x) {
      System.exit(1);
    }
  }

  private static Options makeOptions()
  {
    final Options opts = new Options();

    {
      final Option o =
        new Option("h", "help", false, "Show this help message");
      opts.addOption(o);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("check");
      OptionBuilder
        .withDescription("Parse and validate all source files, but do not produce output");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("xhtml-single");
      OptionBuilder.withDescription("Produce a single XHTML file as output");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("xhtml-multi");
      OptionBuilder.withDescription("Produce multiple XHTML files as output");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      final OptionGroup og = new OptionGroup();
      OptionBuilder.withLongOpt("version");
      OptionBuilder.withDescription("Display version");
      og.addOption(OptionBuilder.create());
      opts.addOptionGroup(og);
    }

    {
      OptionBuilder.withLongOpt("debug");
      OptionBuilder
        .withDescription("Enable debugging (debug messages, exception backtraces)");
      opts.addOption(OptionBuilder.create());
    }

    return opts;
  }

  /**
   * Run the compiler with the given command-line arguments.
   * 
   * @param log
   *          The log handle
   * @param args
   *          The arguments
   * @throws Throwable
   *           On errors
   */

  public static void run(
    final @Nonnull Log log,
    final @Nonnull String[] args)
    throws Throwable
  {
    try {
      JSCMain.runActual(log, args);
    } catch (final ParseException e) {
      log.error(e.getMessage());
      JSCMain.showHelp();
      throw e;
    } catch (final IOException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final ValidityException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final BadParseAttributeException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final InclusionLoopException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final NoIncludeLocationException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final SAXException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final ParserConfigurationException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final ParsingException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final URISyntaxException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final XIncludeException e) {
      log.error(e.getMessage());
      throw e;
    } catch (final Throwable x) {
      log.critical("bug: " + x.getMessage());
      x.printStackTrace(System.err);
      throw x;
    }
  }

  private static void copyStreams(
    final @Nonnull InputStream input,
    final @Nonnull OutputStream output)
    throws IOException
  {
    final byte[] buffer = new byte[8192];

    for (;;) {
      final int r = input.read(buffer);
      if (r == -1) {
        output.flush();
        return;
      }
      output.write(buffer, 0, r);
    }
  }

  private static void runActual(
    final @Nonnull Log log,
    final @Nonnull String[] args)
    throws ParseException,
      ValidityException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      XIncludeException,
      ConstraintError
  {
    Log logx = log;

    if (args.length == 0) {
      JSCMain.showHelp();
      return;
    }

    final PosixParser parser = new PosixParser();
    final CommandLine line = parser.parse(JSCMain.OPTIONS, args);

    if (line.hasOption("debug")) {
      logx = JSCMain.getLog(true);
    }
    if (line.hasOption("xhtml-single")) {
      JSCMain.runCommandCompileXHTMLSingle(logx, line);
      return;
    } else if (line.hasOption("xhtml-multi")) {
      JSCMain.runCommandCompileXHTMLMulti(logx, line);
      return;
    } else if (line.hasOption("check")) {
      JSCMain.runCommandCheck(logx, line);
      return;
    } else if (line.hasOption("version")) {
      JSCMain.runShowVersion(logx, line);
      return;
    } else {
      JSCMain.showHelp();
    }
  }

  private static @Nonnull SADocument runCommandCheck(
    final @Nonnull Log logx,
    final @Nonnull CommandLine line)
    throws ValidityException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      XIncludeException,
      ConstraintError,
      ParseException
  {
    final String[] args = line.getArgs();
    if (args.length < 1) {
      throw new ParseException("Too few arguments");
    }

    final String name = args[0];
    final File file = new File(name);

    final BufferedInputStream stream =
      new BufferedInputStream(new FileInputStream(file));

    try {
      final SDocument doc =
        SDocumentParser.fromStream(stream, file.toURI(), logx);
      return SAnnotator.document(logx, doc);
    } finally {
      stream.close();
    }
  }

  private static void runCommandCompileXHTMLMulti(
    final @Nonnull Log logx,
    final @Nonnull CommandLine line)
    throws ValidityException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      XIncludeException,
      ParseException,
      ConstraintError
  {
    final String[] args = line.getArgs();
    if (args.length < 2) {
      throw new ParseException("Too few arguments");
    }

    final File outdir = new File(args[1]);

    final SADocument doc = JSCMain.runCommandCheck(logx, line);
    final SDocumentXHTMLWriterMulti writer = new SDocumentXHTMLWriterMulti();

    final SortedMap<String, Document> results =
      writer.writeDocuments(JSCMain.getXHTMLWriterCallbacks(), doc);

    for (final String name : results.keySet()) {
      final File outfile = new File(outdir, name);
      JSCMain.writeFile(logx, outfile, results.get(name));
    }

    JSCMain.writeCSS(logx, outdir);
  }

  private static void runCommandCompileXHTMLSingle(
    final @Nonnull Log logx,
    final @Nonnull CommandLine line)
    throws ValidityException,
      BadParseAttributeException,
      InclusionLoopException,
      NoIncludeLocationException,
      SAXException,
      ParserConfigurationException,
      ParsingException,
      IOException,
      URISyntaxException,
      XIncludeException,
      ConstraintError,
      ParseException
  {
    final String[] args = line.getArgs();
    if (args.length < 2) {
      throw new ParseException("Too few arguments");
    }

    final File outdir = new File(args[1]);
    final File outfile = new File(outdir, "index.xhtml");

    final SADocument doc = JSCMain.runCommandCheck(logx, line);
    final SDocumentXHTMLWriterSingle writer =
      new SDocumentXHTMLWriterSingle();

    final SortedMap<String, Document> results =
      writer.writeDocuments(JSCMain.getXHTMLWriterCallbacks(), doc);

    JSCMain.writeFile(logx, outfile, results.get(results.firstKey()));
    JSCMain.writeCSS(logx, outdir);
  }

  @SuppressWarnings("unused") private static void runShowVersion(
    final @Nonnull Log logx,
    final @Nonnull CommandLine line)
  {
    System.out.println(JSCMain.getVersion());
  }

  private static void showHelp()
  {
    final HelpFormatter formatter = new HelpFormatter();
    final PrintWriter pw = new PrintWriter(System.err);
    final String version = JSCMain.getVersion();

    pw.println("jsc: [options] --check        file");
    pw.println("  or [options] --xhtml-single file outdir");
    pw.println("  or [options] --xhtml-multi  file outdir");
    pw.println("  or [options] --version");
    pw.println();
    formatter.printOptions(pw, 120, JSCMain.OPTIONS, 2, 4);
    pw.println();
    pw.println("  Version: " + version);
    pw.println();
    pw.flush();
  }

  private static void writeCSS(
    final @Nonnull Log log,
    final @Nonnull File outdir)
    throws IOException
  {
    final File layout = new File(outdir, "jstructural-2.0.0-layout.css");
    if (layout.exists() == false) {
      log.info("creating " + layout);

      final InputStream in =
        SResources.getLayoutCSSLocation().toURL().openStream();
      try {
        JSCMain.copyFileStream(layout, in);
      } finally {
        in.close();
      }
    }

    final File colour = new File(outdir, "jstructural-2.0.0-colour.css");
    if (colour.exists() == false) {
      log.info("creating " + colour);

      final InputStream in =
        SResources.getColourCSSLocation().toURL().openStream();
      try {
        JSCMain.copyFileStream(colour, in);
      } finally {
        in.close();
      }
    }
  }

  private static void copyFileStream(
    final @Nonnull File file,
    final @Nonnull InputStream in)
    throws FileNotFoundException,
      IOException
  {
    final FileOutputStream out = new FileOutputStream(file);
    try {
      JSCMain.copyStreams(in, out);
    } finally {
      out.close();
    }
  }

  private static void writeFile(
    final @Nonnull Log logx,
    final @Nonnull File file,
    final @Nonnull Document document)
    throws IOException
  {
    logx.info("writing " + file);

    final BufferedOutputStream stream =
      new BufferedOutputStream(new FileOutputStream(file));

    try {
      final Serializer s = new Serializer(stream);
      s.write(document);
      s.flush();
    } finally {
      stream.flush();
      stream.close();
    }
  }

  private JSCMain()
  {

  }
}
