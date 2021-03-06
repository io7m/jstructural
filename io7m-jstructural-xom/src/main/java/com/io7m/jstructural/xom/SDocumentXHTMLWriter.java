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

package com.io7m.jstructural.xom;

import java.util.SortedMap;

import nu.xom.Document;

import com.io7m.jstructural.annotated.SADocument;

/**
 * The interface supported by document writers.
 */

public interface SDocumentXHTMLWriter
{
  /**
   * Transform the given document into at least one XHTML file.
   * 
   * @param callbacks
   *          A set of callbacks for document customisation
   * @param doc
   *          The document
   * @return A list of XHTML files
   */

  SortedMap<String, Document> writeDocuments(
    final SDocumentXHTMLWriterCallbacks callbacks,
    final SADocument doc);
}
