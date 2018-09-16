/*
 * Copyright © 2018 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.tests.compiler.api;

import com.io7m.jstructural.ast.SBlockContentType;
import com.io7m.jstructural.ast.SContentNumbers;
import com.io7m.jstructural.ast.SContentType;
import com.io7m.jstructural.ast.SDocument;
import com.io7m.jstructural.ast.SFootnote;
import com.io7m.jstructural.ast.SFootnoteReference;
import com.io7m.jstructural.ast.SFormalItem;
import com.io7m.jstructural.ast.SFormalItemReference;
import com.io7m.jstructural.ast.SImage;
import com.io7m.jstructural.ast.SLink;
import com.io7m.jstructural.ast.SLinkExternal;
import com.io7m.jstructural.ast.SListOrdered;
import com.io7m.jstructural.ast.SListUnordered;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParagraph;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SSectionType;
import com.io7m.jstructural.ast.SSectionWithSections;
import com.io7m.jstructural.ast.SSectionWithSubsectionContent;
import com.io7m.jstructural.ast.SSectionWithSubsections;
import com.io7m.jstructural.ast.SSubsection;
import com.io7m.jstructural.ast.SSubsectionType;
import com.io7m.jstructural.ast.STerm;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.ast.STextType;
import com.io7m.jstructural.ast.SVerbatim;
import com.io7m.jstructural.compiler.api.SCompilationTaskType;
import com.io7m.jstructural.compiler.api.SCompileError;
import com.io7m.jstructural.compiler.api.SCompiledLocalType;
import com.io7m.jstructural.compiler.api.SCompilerType;
import com.io7m.jstructural.parser.spi.SPIParserRequest;
import com.io7m.jstructural.parser.spi.SPIParserType;
import com.io7m.jstructural.parser.spi.SParseError;
import com.io7m.jstructural.parser.xml.SXMLParserProvider;
import com.io7m.jstructural.tests.TestMemoryFilesystemExtension;
import com.io7m.jstructural.tests.parser.xml.SParserXMLTest;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@ExtendWith(TestMemoryFilesystemExtension.class)
public abstract class SCompilerContract
{
  private static final Logger LOG = LoggerFactory.getLogger(SCompilerContract.class);

  private static SDocument<SParsed> document(
    final FileSystem fs,
    final String file)
    throws Exception
  {
    final Path path = copyResourceToMemoryFS(fs, file);
    final SPIParserType parser = createParser(fs, file);
    final Validation<Seq<SParseError>, SContentType<SParsed>> result = parser.parse();
    if (result.isValid()) {
      return (SDocument<SParsed>) result.get();
    }
    result.getError().forEach(error -> {
      LOG.error("error: {}", error);
    });
    throw new IOException("Document is not valid");
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
    final String path = "/com/io7m/jstructural/tests/parser/xml/v6/compiler/" + file;
    final InputStream stream = SParserXMLTest.class.getResourceAsStream(path);
    if (stream == null) {
      throw new NoSuchFileException(path);
    }
    return stream;
  }

  private static SDocument<SCompiledLocalType> checkResults(
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results)
  {
    if (results.isValid()) {
      return results.get();
    }

    results.getError().forEach(error -> LOG.error("error: {}", error));
    throw new AssertionFailedError("Failed!");
  }

  public abstract SCompilerType compiler();

  @Test
  public void testDocumentNoSections(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-empty.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections0(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-0.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");
    assertIsParentOf(document_compiled, document_compiled);

    final SSectionType<SCompiledLocalType> section_1 = document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SSectionType<SCompiledLocalType> section_2 = document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SSectionType<SCompiledLocalType> section_3 = document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections1(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-1.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");
    assertIsParentOf(document_compiled, document_compiled);

    final SSectionWithSections<SCompiledLocalType> section_1 =
      (SSectionWithSections<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SSectionType<SCompiledLocalType> section_1_1 = section_1.sections().get(0);
    assertContentNumberIsEqualTo(section_1_1, "1.1");
    assertIsParentOf(section_1, section_1_1);

    final SSectionType<SCompiledLocalType> section_1_2 = section_1.sections().get(1);
    assertContentNumberIsEqualTo(section_1_2, "1.2");
    assertIsParentOf(section_1, section_1_2);

    final SSectionType<SCompiledLocalType> section_1_3 = section_1.sections().get(2);
    assertContentNumberIsEqualTo(section_1_3, "1.3");
    assertIsParentOf(section_1, section_1_3);

    final SSectionWithSections<SCompiledLocalType> section_2 =
      (SSectionWithSections<SCompiledLocalType>) document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SSectionType<SCompiledLocalType> section_2_1 = section_2.sections().get(0);
    assertContentNumberIsEqualTo(section_2_1, "2.1");
    assertIsParentOf(section_2, section_2_1);

    final SSectionType<SCompiledLocalType> section_2_2 = section_2.sections().get(1);
    assertContentNumberIsEqualTo(section_2_2, "2.2");
    assertIsParentOf(section_2, section_2_2);

    final SSectionType<SCompiledLocalType> section_2_3 = section_2.sections().get(2);
    assertContentNumberIsEqualTo(section_2_3, "2.3");
    assertIsParentOf(section_2, section_2_3);

    final SSectionWithSections<SCompiledLocalType> section_3 =
      (SSectionWithSections<SCompiledLocalType>) document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    final SSectionType<SCompiledLocalType> section_3_1 = section_3.sections().get(0);
    assertContentNumberIsEqualTo(section_3_1, "3.1");
    assertIsParentOf(section_3, section_3_1);

    final SSectionType<SCompiledLocalType> section_3_2 = section_3.sections().get(1);
    assertContentNumberIsEqualTo(section_3_2, "3.2");
    assertIsParentOf(section_3, section_3_2);

    final SSectionType<SCompiledLocalType> section_3_3 = section_3.sections().get(2);
    assertContentNumberIsEqualTo(section_3_3, "3.3");
    assertIsParentOf(section_3, section_3_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections2(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-2.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSubsections<SCompiledLocalType> section_1 =
      (SSectionWithSubsections<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SSubsectionType<SCompiledLocalType> section_1_1 = section_1.subsections().get(0);
    assertContentNumberIsEqualTo(section_1_1, "1.1");
    assertIsParentOf(section_1, section_1_1);

    final SSubsectionType<SCompiledLocalType> section_1_2 = section_1.subsections().get(1);
    assertContentNumberIsEqualTo(section_1_2, "1.2");
    assertIsParentOf(section_1, section_1_2);

    final SSubsectionType<SCompiledLocalType> section_1_3 = section_1.subsections().get(2);
    assertContentNumberIsEqualTo(section_1_3, "1.3");
    assertIsParentOf(section_1, section_1_3);

    final SSectionWithSubsections<SCompiledLocalType> section_2 =
      (SSectionWithSubsections<SCompiledLocalType>) document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SSubsectionType<SCompiledLocalType> section_2_1 = section_2.subsections().get(0);
    assertContentNumberIsEqualTo(section_2_1, "2.1");
    assertIsParentOf(section_2, section_2_1);

    final SSubsectionType<SCompiledLocalType> section_2_2 = section_2.subsections().get(1);
    assertContentNumberIsEqualTo(section_2_2, "2.2");
    assertIsParentOf(section_2, section_2_2);

    final SSubsectionType<SCompiledLocalType> section_2_3 = section_2.subsections().get(2);
    assertContentNumberIsEqualTo(section_2_3, "2.3");
    assertIsParentOf(section_2, section_2_3);

    final SSectionWithSubsections<SCompiledLocalType> section_3 =
      (SSectionWithSubsections<SCompiledLocalType>) document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    final SSubsectionType<SCompiledLocalType> section_3_1 = section_3.subsections().get(0);
    assertContentNumberIsEqualTo(section_3_1, "3.1");
    assertIsParentOf(section_3, section_3_1);

    final SSubsectionType<SCompiledLocalType> section_3_2 = section_3.subsections().get(1);
    assertContentNumberIsEqualTo(section_3_2, "3.2");
    assertIsParentOf(section_3, section_3_2);

    final SSubsectionType<SCompiledLocalType> section_3_3 = section_3.subsections().get(2);
    assertContentNumberIsEqualTo(section_3_3, "3.3");
    assertIsParentOf(section_3, section_3_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections3(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-3.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSubsectionContent<SCompiledLocalType> section_1 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SParagraph<SCompiledLocalType> para_1_1 =
      (SParagraph<SCompiledLocalType>) section_1.content().get(0);
    assertContentNumberIsEqualTo(para_1_1, "1.1");
    assertIsParentOf(section_1, para_1_1);

    final SParagraph<SCompiledLocalType> para_1_2 =
      (SParagraph<SCompiledLocalType>) section_1.content().get(1);
    assertContentNumberIsEqualTo(para_1_2, "1.2");
    assertIsParentOf(section_1, para_1_2);

    final SParagraph<SCompiledLocalType> para_1_3 =
      (SParagraph<SCompiledLocalType>) section_1.content().get(2);
    assertContentNumberIsEqualTo(para_1_3, "1.3");
    assertIsParentOf(section_1, para_1_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_2 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SParagraph<SCompiledLocalType> para_2_1 =
      (SParagraph<SCompiledLocalType>) section_2.content().get(0);
    assertContentNumberIsEqualTo(para_2_1, "2.1");
    assertIsParentOf(section_2, para_2_1);

    final SParagraph<SCompiledLocalType> para_2_2 =
      (SParagraph<SCompiledLocalType>) section_2.content().get(1);
    assertContentNumberIsEqualTo(para_2_2, "2.2");
    assertIsParentOf(section_2, para_2_2);

    final SParagraph<SCompiledLocalType> para_2_3 =
      (SParagraph<SCompiledLocalType>) section_2.content().get(2);
    assertContentNumberIsEqualTo(para_2_3, "2.3");
    assertIsParentOf(section_2, para_2_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_3 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    final SParagraph<SCompiledLocalType> para_3_1 =
      (SParagraph<SCompiledLocalType>) section_3.content().get(0);
    assertContentNumberIsEqualTo(para_3_1, "3.1");
    assertIsParentOf(section_3, para_3_1);

    final SParagraph<SCompiledLocalType> para_3_2 =
      (SParagraph<SCompiledLocalType>) section_3.content().get(1);
    assertContentNumberIsEqualTo(para_3_2, "3.2");
    assertIsParentOf(section_3, para_3_2);

    final SParagraph<SCompiledLocalType> para_3_3 =
      (SParagraph<SCompiledLocalType>) section_3.content().get(2);
    assertContentNumberIsEqualTo(para_3_3, "3.3");
    assertIsParentOf(section_3, para_3_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections4(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-4.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSubsectionContent<SCompiledLocalType> section_1 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SFormalItem<SCompiledLocalType> formal_1_1 =
      (SFormalItem<SCompiledLocalType>) section_1.content().get(0);
    assertContentNumberIsEqualTo(formal_1_1, "1.1");
    assertIsParentOf(section_1, formal_1_1);

    final SFormalItem<SCompiledLocalType> formal_1_2 =
      (SFormalItem<SCompiledLocalType>) section_1.content().get(1);
    assertContentNumberIsEqualTo(formal_1_2, "1.2");
    assertIsParentOf(section_1, formal_1_2);

    final SFormalItem<SCompiledLocalType> formal_1_3 =
      (SFormalItem<SCompiledLocalType>) section_1.content().get(2);
    assertContentNumberIsEqualTo(formal_1_3, "1.3");
    assertIsParentOf(section_1, formal_1_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_2 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SFormalItem<SCompiledLocalType> formal_2_1 =
      (SFormalItem<SCompiledLocalType>) section_2.content().get(0);
    assertContentNumberIsEqualTo(formal_2_1, "2.1");
    assertIsParentOf(section_2, formal_2_1);

    final SFormalItem<SCompiledLocalType> formal_2_2 =
      (SFormalItem<SCompiledLocalType>) section_2.content().get(1);
    assertContentNumberIsEqualTo(formal_2_2, "2.2");
    assertIsParentOf(section_2, formal_2_2);

    final SFormalItem<SCompiledLocalType> formal_2_3 =
      (SFormalItem<SCompiledLocalType>) section_2.content().get(2);
    assertContentNumberIsEqualTo(formal_2_3, "2.3");
    assertIsParentOf(section_2, formal_2_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_3 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    final SFormalItem<SCompiledLocalType> formal_3_1 =
      (SFormalItem<SCompiledLocalType>) section_3.content().get(0);
    assertContentNumberIsEqualTo(formal_3_1, "3.1");
    assertIsParentOf(section_3, formal_3_1);

    final SFormalItem<SCompiledLocalType> formal_3_2 =
      (SFormalItem<SCompiledLocalType>) section_3.content().get(1);
    assertContentNumberIsEqualTo(formal_3_2, "3.2");
    assertIsParentOf(section_3, formal_3_2);

    final SFormalItem<SCompiledLocalType> formal_3_3 =
      (SFormalItem<SCompiledLocalType>) section_3.content().get(2);
    assertContentNumberIsEqualTo(formal_3_3, "3.3");
    assertIsParentOf(section_3, formal_3_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections5(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-5.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSubsectionContent<SCompiledLocalType> section_1 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SFootnote<SCompiledLocalType> foot_1_1 =
      (SFootnote<SCompiledLocalType>) section_1.content().get(0);
    assertContentNumberIsEqualTo(foot_1_1, "1.1");
    assertIsParentOf(section_1, foot_1_1);

    final SFootnote<SCompiledLocalType> foot_1_2 =
      (SFootnote<SCompiledLocalType>) section_1.content().get(1);
    assertContentNumberIsEqualTo(foot_1_2, "1.2");
    assertIsParentOf(section_1, foot_1_2);

    final SFootnote<SCompiledLocalType> foot_1_3 =
      (SFootnote<SCompiledLocalType>) section_1.content().get(2);
    assertContentNumberIsEqualTo(foot_1_3, "1.3");
    assertIsParentOf(section_1, foot_1_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_2 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(1);
    assertContentNumberIsEqualTo(section_2, "2");
    assertIsParentOf(document_compiled, section_2);

    final SFootnote<SCompiledLocalType> foot_2_1 =
      (SFootnote<SCompiledLocalType>) section_2.content().get(0);
    assertContentNumberIsEqualTo(foot_2_1, "2.1");
    assertIsParentOf(section_2, foot_2_1);

    final SFootnote<SCompiledLocalType> foot_2_2 =
      (SFootnote<SCompiledLocalType>) section_2.content().get(1);
    assertContentNumberIsEqualTo(foot_2_2, "2.2");
    assertIsParentOf(section_2, foot_2_2);

    final SFootnote<SCompiledLocalType> foot_2_3 =
      (SFootnote<SCompiledLocalType>) section_2.content().get(2);
    assertContentNumberIsEqualTo(foot_2_3, "2.3");
    assertIsParentOf(section_2, foot_2_3);

    final SSectionWithSubsectionContent<SCompiledLocalType> section_3 =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(2);
    assertContentNumberIsEqualTo(section_3, "3");
    assertIsParentOf(document_compiled, section_3);

    final SFootnote<SCompiledLocalType> foot_3_1 =
      (SFootnote<SCompiledLocalType>) section_3.content().get(0);
    assertContentNumberIsEqualTo(foot_3_1, "3.1");
    assertIsParentOf(section_3, foot_3_1);

    final SFootnote<SCompiledLocalType> foot_3_2 =
      (SFootnote<SCompiledLocalType>) section_3.content().get(1);
    assertContentNumberIsEqualTo(foot_3_2, "3.2");
    assertIsParentOf(section_3, foot_3_2);

    final SFootnote<SCompiledLocalType> foot_3_3 =
      (SFootnote<SCompiledLocalType>) section_3.content().get(2);
    assertContentNumberIsEqualTo(foot_3_3, "3.3");
    assertIsParentOf(section_3, foot_3_3);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentSections6(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-sections-6.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSections<SCompiledLocalType> section_1 =
      (SSectionWithSections<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(section_1, "1");
    assertIsParentOf(document_compiled, section_1);

    final SSectionWithSections<SCompiledLocalType> section_1_1 =
      (SSectionWithSections<SCompiledLocalType>) section_1.sections().get(0);
    assertContentNumberIsEqualTo(section_1_1, "1.1");
    assertIsParentOf(section_1, section_1_1);

    final SSectionWithSubsections<SCompiledLocalType> section_1_1_1 =
      (SSectionWithSubsections<SCompiledLocalType>) section_1_1.sections().get(0);
    assertContentNumberIsEqualTo(section_1_1_1, "1.1.1");
    assertIsParentOf(section_1_1, section_1_1_1);

    final SSubsection<SCompiledLocalType> section_1_1_1_1 =
      (SSubsection<SCompiledLocalType>) section_1_1_1.subsections().get(0);
    assertContentNumberIsEqualTo(section_1_1_1_1, "1.1.1.1");
    assertIsParentOf(section_1_1_1, section_1_1_1_1);

    final SParagraph<SCompiledLocalType> para =
      (SParagraph<SCompiledLocalType>) section_1_1_1_1.content().get(0);
    assertContentNumberIsEqualTo(para, "1.1.1.1.1");
    assertIsParentOf(section_1_1_1_1, para);

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentParagraphContent0(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-paragraph-content-0.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();
    final SDocument<SCompiledLocalType> document_compiled = checkResults(results);

    assertContentNumberIsEqualTo(document_compiled, "0");

    final SSectionWithSubsectionContent<SCompiledLocalType> s =
      (SSectionWithSubsectionContent<SCompiledLocalType>) document_compiled.sections().get(0);
    assertContentNumberIsEqualTo(s, "1");

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(0);
      assertContentNumberIsEqualTo(para, "1.1");

      {
        final SText<SCompiledLocalType> text =
          (SText<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, text);
        assertContentNumberIsEqualTo(text, "1.1.1");
        Assertions.assertEquals("Hello.", text.text());
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(1);
      assertContentNumberIsEqualTo(para, "1.2");

      {
        final STerm<SCompiledLocalType> term =
          (STerm<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, term);
        assertContentNumberIsEqualTo(term, "1.2.1");
        final STextType<SCompiledLocalType> text = term.text().get(0);
        Assertions.assertEquals("Hello.", text.text());
        assertIsParentOf(para, text);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(2);
      assertContentNumberIsEqualTo(para, "1.3");

      {
        final STerm<SCompiledLocalType> term =
          (STerm<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, term);
        assertContentNumberIsEqualTo(term, "1.3.1");
        final STextType<SCompiledLocalType> text = term.text().get(0);
        Assertions.assertEquals("Hello.", text.text());
        assertIsParentOf(para, text);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(3);
      assertContentNumberIsEqualTo(para, "1.4");

      {
        final SImage<SCompiledLocalType> link =
          (SImage<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, link);
        assertContentNumberIsEqualTo(link, "1.4.1");
        final STextType<SCompiledLocalType> text = link.text().get(0);
        Assertions.assertEquals("Hello.", text.text());
        assertIsParentOf(para, text);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(4);
      assertContentNumberIsEqualTo(para, "1.5");

      {
        final SLink<SCompiledLocalType> link =
          (SLink<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, link);
        assertContentNumberIsEqualTo(link, "1.5.1");
        final STextType<SCompiledLocalType> text =
          (STextType<SCompiledLocalType>) link.content().get(0);
        Assertions.assertEquals("Hello.", text.text());
        assertIsParentOf(para, text);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(5);
      assertContentNumberIsEqualTo(para, "1.6");

      {
        final SLinkExternal<SCompiledLocalType> link =
          (SLinkExternal<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, link);
        assertContentNumberIsEqualTo(link, "1.6.1");
        final STextType<SCompiledLocalType> text =
          (STextType<SCompiledLocalType>) link.content().get(0);
        Assertions.assertEquals("Hello.", text.text());
        assertIsParentOf(para, text);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(6);
      assertContentNumberIsEqualTo(para, "1.7");

      {
        final SLink<SCompiledLocalType> link =
          (SLink<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, link);
        assertContentNumberIsEqualTo(link, "1.7.1");
        final SImage<SCompiledLocalType> image =
          (SImage<SCompiledLocalType>) link.content().get(0);
        assertIsParentOf(para, image);
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(7);
      assertContentNumberIsEqualTo(para, "1.8");

      {
        final SFootnoteReference<SCompiledLocalType> ref =
          (SFootnoteReference<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, ref);
        assertContentNumberIsEqualTo(ref, "1.8.1");
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(8);
      assertContentNumberIsEqualTo(para, "1.9");

      {
        final SFormalItemReference<SCompiledLocalType> ref =
          (SFormalItemReference<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, ref);
        assertContentNumberIsEqualTo(ref, "1.9.1");
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(9);
      assertContentNumberIsEqualTo(para, "1.10");

      {
        final SVerbatim<SCompiledLocalType> verb =
          (SVerbatim<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, verb);
        assertContentNumberIsEqualTo(verb, "1.10.1");
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(10);
      assertContentNumberIsEqualTo(para, "1.11");

      {
        final SListOrdered<SCompiledLocalType> list =
          (SListOrdered<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, list);
        assertContentNumberIsEqualTo(list, "1.11.1");
      }
    }

    {
      final SParagraph<SCompiledLocalType> para =
        (SParagraph<SCompiledLocalType>) s.content().get(11);
      assertContentNumberIsEqualTo(para, "1.12");

      {
        final SListUnordered<SCompiledLocalType> list =
          (SListUnordered<SCompiledLocalType>) para.content().get(0);
        assertIsParentOf(para, list);
        assertContentNumberIsEqualTo(list, "1.12.1");
      }
    }

    Assertions.assertEquals(document_parsed, document_compiled);
  }

  @Test
  public void testDocumentTableInvalid0(
    final FileSystem fs)
    throws Exception
  {
    final SCompilerType compiler = this.compiler();
    final SDocument<SParsed> document_parsed = document(fs, "document-table-invalid-0.xml");
    final SCompilationTaskType task = compiler.createTask(document_parsed);
    final Validation<Seq<SCompileError>, SDocument<SCompiledLocalType>> results = task.run();

    Assertions.assertTrue(results.isInvalid());
    Assertions.assertEquals(3, results.getError().size());
    Assertions.assertTrue(
      results.getError()
        .forAll(error -> error.message().contains("Number of columns in table row")));
  }

  private static void assertContentNumberIsEqualTo(
    final SModelType<SCompiledLocalType> element,
    final String number_text)
  {
    Assertions.assertEquals(SContentNumbers.parse(number_text), element.data().number());
  }

  private static void assertIsParentOf(
    final SBlockContentType<SCompiledLocalType> parent,
    final SModelType<SCompiledLocalType> item)
  {
    Assertions.assertEquals(parent, item.data().parent());
  }
}
