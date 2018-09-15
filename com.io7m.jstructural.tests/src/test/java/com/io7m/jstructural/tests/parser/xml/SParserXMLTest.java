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

package com.io7m.jstructural.tests.parser.xml;

import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SImageSize;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SLinkExternal;
import com.io7m.jstructural.ast.SListOrdered;
import com.io7m.jstructural.ast.SListUnordered;
import com.io7m.jstructural.ast.SContentType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.STable;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STypeName;
import com.io7m.jstructural.ast.SVerbatim;
import com.io7m.jstructural.formats.SFormatDescription;
import com.io7m.jstructural.parser.spi.SPIParserRequest;
import com.io7m.jstructural.parser.spi.SPIParserType;
import com.io7m.jstructural.parser.spi.SParseError;
import com.io7m.jstructural.parser.xml.SXMLParserProvider;
import com.io7m.jstructural.probe.spi.SPIProbeRequest;
import com.io7m.jstructural.tests.TestMemoryFilesystemExtension;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

import static com.io7m.jstructural.ast.SParsed.PARSED;

@SuppressWarnings("unchecked")
@ExtendWith(TestMemoryFilesystemExtension.class)
public final class SParserXMLTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SParserXMLTest.class);

  private static <T> T assertValid(
    final Validation<Seq<SParseError>, T> v)
  {
    Assertions.assertTrue(v.isValid(), "Result is valid");
    return v.get();
  }

  private static <A, B> B assertTypeOf(
    final A x,
    final Class<B> c)
  {
    Assertions.assertTrue(
      x.getClass().isAssignableFrom(c),
      x + " must be of type " + c);
    return (B) x;
  }

  private static SPIParserType createParser(
    final FileSystem fs,
    final String file)
    throws Exception
  {
    final SXMLParserProvider parsers = new SXMLParserProvider();
    final Path root = fs.getRootDirectories().iterator().next();
    final Path memfs_path = copyResourceToMemoryFS(fs, file);

    final SPIParserType pp =
      parsers.create(
        SPIParserRequest.builder()
          .setFile(memfs_path.toUri())
          .setBaseDirectory(root)
          .setStream(Files.newInputStream(memfs_path))
          .build());

    return () -> {
      final Validation<Seq<SParseError>, SContentType<SParsed>> r = pp.parse();
      if (r.isValid()) {
        LOG.debug("valid: {}", r.get());
      } else {
        r.getError().forEach(e -> LOG.error("error: {}", e));
      }
      return r;
    };
  }

  private static Path copyResourceToMemoryFS(
    final FileSystem fs,
    final String file)
    throws IOException
  {
    final Path root = fs.getRootDirectories().iterator().next();
    final Path memfs_path;
    try (final InputStream stream = resource(file)) {
      memfs_path = root.resolve(file);
      try (OutputStream output = Files.newOutputStream(memfs_path)) {
        stream.transferTo(output);
      }
    }
    return memfs_path;
  }

  private static InputStream resource(
    final String file)
    throws IOException
  {
    final String path = "/com/io7m/jstructural/tests/parser/xml/v6/" + file;
    final InputStream stream = SParserXMLTest.class.getResourceAsStream(path);
    if (stream == null) {
      throw new NoSuchFileException(path);
    }
    return stream;
  }

  @Test
  public void testProbeValid0(
    final FileSystem fs)
    throws Exception
  {
    final SXMLParserProvider parsers = new SXMLParserProvider();
    final Path path = copyResourceToMemoryFS(fs, "term0.xml");

    final Optional<SFormatDescription> r =
      parsers.probe(
        SPIProbeRequest.builder()
          .setBaseDirectory(fs.getRootDirectories().iterator().next())
          .setUri(path.toUri())
          .setStreams(() -> Files.newInputStream(path))
          .build());

    Assertions.assertTrue(r.isPresent());

    final SFormatDescription f = r.get();
    Assertions.assertEquals("xml", f.name());
    Assertions.assertEquals("com.io7m.structural", f.vendor());
    Assertions.assertEquals(6, f.versionMajor());
    Assertions.assertEquals(0, f.versionMinor());
  }

  @Test
  public void testProbeInvalid0(
    final FileSystem fs)
    throws Exception
  {
    final SXMLParserProvider parsers = new SXMLParserProvider();
    final Path path = copyResourceToMemoryFS(fs, "wrong_schema0.xml");

    final Optional<SFormatDescription> r =
      parsers.probe(
        SPIProbeRequest.builder()
          .setBaseDirectory(fs.getRootDirectories().iterator().next())
          .setUri(path.toUri())
          .setStreams(() -> Files.newInputStream(path))
          .build());

    Assertions.assertFalse(r.isPresent());
  }

  @Test
  public void testProbeInvalid1(
    final FileSystem fs)
    throws Exception
  {
    final SXMLParserProvider parsers = new SXMLParserProvider();
    final Path path = copyResourceToMemoryFS(fs, "wrong_schema1.xml");

    final Optional<SFormatDescription> r =
      parsers.probe(
        SPIProbeRequest.builder()
          .setBaseDirectory(fs.getRootDirectories().iterator().next())
          .setUri(path.toUri())
          .setStreams(() -> Files.newInputStream(path))
          .build());

    Assertions.assertFalse(r.isPresent());
  }

  @Test
  public void testWrongSchema0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "wrong_schema0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testWrongSchema1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "wrong_schema1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testTerm0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "term0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final STerm<SParsed> c = assertTypeOf(assertValid(r), STerm.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals("Text", c.text().get(0).text());
  }

  @Test
  public void testTerm1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "term1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final STerm<SParsed> c = assertTypeOf(assertValid(r), STerm.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("Text", c.text().get(0).text());
  }

  @Test
  public void testTermInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "term_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testVerbatim0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "verbatim0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SVerbatim<SParsed> c = assertTypeOf(assertValid(r), SVerbatim.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals("    A    B    C    ", c.text().text());
  }

  @Test
  public void testVerbatim1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "verbatim1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SVerbatim<SParsed> c = assertTypeOf(assertValid(r), SVerbatim.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("    A    B    C    ", c.text().text());
  }

  @Test
  public void testVerbatimInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "verbatim_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testImage0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "image0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SImage<SParsed> c = assertTypeOf(assertValid(r), SImage.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(Optional.empty(), c.size());
    Assertions.assertEquals("Text", c.text().get(0).text());
  }

  @Test
  public void testImage1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "image1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SImage<SParsed> c = assertTypeOf(assertValid(r), SImage.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(Optional.empty(), c.size());
    Assertions.assertEquals("Text", c.text().get(0).text());
  }

  @Test
  public void testImage2(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "image2.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SImage<SParsed> c = assertTypeOf(assertValid(r), SImage.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(SImageSize.of(PARSED, 23, 34), c.size().get());
    Assertions.assertEquals("Text", c.text().get(0).text());
  }

  @Test
  public void testImageInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "image_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testFootnoteReference0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "footnote-ref0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SFootnoteReference<SParsed> c =
      assertTypeOf(assertValid(r), SFootnoteReference.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testFootnoteReference1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "footnote-ref1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SFootnoteReference<SParsed> c =
      assertTypeOf(assertValid(r), SFootnoteReference.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testFootnoteReferenceInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "footnote-ref_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testFormalItemReference0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "formal-item-ref0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SFormalItemReference<SParsed> c =
      assertTypeOf(assertValid(r), SFormalItemReference.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testFormalItemReference1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "formal-item-ref1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SFormalItemReference<SParsed> c =
      assertTypeOf(assertValid(r), SFormalItemReference.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testFormalItemReferenceInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(
      fs,
      "formal-item-ref_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testLink0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLink<SParsed> c = assertTypeOf(assertValid(r), SLink.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testLink1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLink<SParsed> c = assertTypeOf(assertValid(r), SLink.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("abc", c.target());
  }

  @Test
  public void testLink2(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link2.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLink<SParsed> c = assertTypeOf(assertValid(r), SLink.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals("abc", c.target());
    Assertions.assertEquals(3, c.content().size());

    final SText<SParsed> cc0 = assertTypeOf(c.content().get(0), SText.class);
    final SImage<SParsed> cc1 = assertTypeOf(c.content().get(1), SImage.class);
    final SText<SParsed> cc2 = assertTypeOf(c.content().get(2), SText.class);
    Assertions.assertEquals(URI.create("abc.png"), cc1.source());
  }

  @Test
  public void testLinkInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testLinkExternal0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link_external0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLinkExternal<SParsed> c = assertTypeOf(
      assertValid(r),
      SLinkExternal.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(URI.create("abc"), c.target());
  }

  @Test
  public void testLinkExternal1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link_external1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLinkExternal<SParsed> c = assertTypeOf(
      assertValid(r),
      SLinkExternal.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(URI.create("abc"), c.target());
  }

  @Test
  public void testLinkExternal2(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link_external2.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SLinkExternal<SParsed> c = assertTypeOf(
      assertValid(r),
      SLinkExternal.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(URI.create("abc"), c.target());
    Assertions.assertEquals(3, c.content().size());

    final SText<SParsed> cc0 = assertTypeOf(c.content().get(0), SText.class);
    final SImage<SParsed> cc1 = assertTypeOf(c.content().get(1), SImage.class);
    final SText<SParsed> cc2 = assertTypeOf(c.content().get(2), SText.class);
    Assertions.assertEquals(URI.create("abc.png"), cc1.source());
  }

  @Test
  public void testLinkExternalInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "link_external_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testListOrdered0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "list_ordered0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SListOrdered<SParsed> c =
      assertTypeOf(assertValid(r), SListOrdered.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(3, c.items().size());

    final SText<SParsed> cc0 =
      assertTypeOf(c.items().get(0).content().get(0), SText.class);
    final STerm<SParsed> cc1 =
      assertTypeOf(c.items().get(1).content().get(0), STerm.class);
    final SImage<SParsed> cc2 =
      assertTypeOf(c.items().get(2).content().get(0), SImage.class);
  }

  @Test
  public void testListOrdered1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "list_ordered1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SListOrdered<SParsed> c =
      assertTypeOf(assertValid(r), SListOrdered.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(3, c.items().size());

    final SText<SParsed> cc0 =
      assertTypeOf(c.items().get(0).content().get(0), SText.class);
    final STerm<SParsed> cc1 =
      assertTypeOf(c.items().get(1).content().get(0), STerm.class);
    final SImage<SParsed> cc2 =
      assertTypeOf(c.items().get(2).content().get(0), SImage.class);
  }

  @Test
  public void testListOrderedInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "list_ordered_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testListUnordered0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "list_unordered0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SListUnordered<SParsed> c =
      assertTypeOf(assertValid(r), SListUnordered.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(3, c.items().size());

    final SText<SParsed> cc0 =
      assertTypeOf(c.items().get(0).content().get(0), SText.class);
    final STerm<SParsed> cc1 =
      assertTypeOf(c.items().get(1).content().get(0), STerm.class);
    final SImage<SParsed> cc2 =
      assertTypeOf(c.items().get(2).content().get(0), SImage.class);
  }

  @Test
  public void testListUnordered1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "list_unordered1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final SListUnordered<SParsed> c =
      assertTypeOf(assertValid(r), SListUnordered.class);
    Assertions.assertEquals(Optional.of(STypeName.of(PARSED, "xyz")), c.type());
    Assertions.assertEquals(3, c.items().size());

    final SText<SParsed> cc0 =
      assertTypeOf(c.items().get(0).content().get(0), SText.class);
    final STerm<SParsed> cc1 =
      assertTypeOf(c.items().get(1).content().get(0), STerm.class);
    final SImage<SParsed> cc2 =
      assertTypeOf(c.items().get(2).content().get(0), SImage.class);
  }

  @Test
  public void testListUnorderedInvalid(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(
      fs,
      "list_unordered_invalid.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testTable0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "table0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final STable<SParsed> c = assertTypeOf(assertValid(r), STable.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(3, c.body().rows().size());
  }

  @Test
  public void testTable1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "table1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();

    final STable<SParsed> c = assertTypeOf(assertValid(r), STable.class);
    Assertions.assertFalse(c.type().isPresent());
    Assertions.assertEquals(3, c.body().rows().size());
  }

  @Test
  public void testTableInvalid0(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "table_invalid0.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }

  @Test
  public void testTableInvalid1(
    final FileSystem fs)
    throws Exception
  {
    final SPIParserType p = createParser(fs, "table_invalid1.xml");
    final Validation<Seq<SParseError>, SContentType<SParsed>> r = p.parse();
    Assertions.assertTrue(r.isInvalid());
  }
}
