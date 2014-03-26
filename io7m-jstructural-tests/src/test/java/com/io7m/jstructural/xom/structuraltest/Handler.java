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

package com.io7m.jstructural.xom.structuraltest;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.io7m.jstructural.xom.SDocumentParserTest;

/**
 * A custom URL handler that allows loading files from test resources.
 * 
 * This means that XIncludes using structuraltest:// can be resolved to files
 * in the test resources.
 */

public final class Handler extends URLStreamHandler
{
  @Override protected URLConnection openConnection(
    final URL u)
    throws IOException
  {
    final StringBuilder b = new StringBuilder();
    b.append("/");
    b.append(u.getHost());
    b.append(u.getFile());

    System.err.println("structuraltest: handler: file: " + b.toString());
    final URL r = SDocumentParserTest.class.getResource(b.toString());
    System.err.println("structuraltest: handler: url: " + r);

    return r.openConnection();
  }
}
