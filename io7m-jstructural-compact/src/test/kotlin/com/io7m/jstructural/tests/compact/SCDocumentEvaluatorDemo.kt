/*
 * Copyright © 2016 <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.tests.compact

import com.io7m.jeucreader.UnicodeCharacterReader
import com.io7m.jlexing.core.ImmutableLexicalPosition
import com.io7m.jstructural.annotated.SAnnotator
import com.io7m.jstructural.compact.SCDocumentEvaluator
import com.io7m.jstructural.compact.SCError
import com.io7m.jstructural.compact.SCException
import com.io7m.jstructural.compact.SCExpression
import com.io7m.jstructural.compact.SCParser
import com.io7m.jstructural.core.SDocument
import com.io7m.jsx.lexer.JSXLexer
import com.io7m.jsx.lexer.JSXLexerConfiguration
import com.io7m.jsx.parser.JSXParser
import com.io7m.jsx.parser.JSXParserConfiguration
import com.io7m.jsx.parser.JSXParserException
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.file.Paths
import java.util.ArrayDeque
import java.util.Optional

fun main(args : Array<String>) {

  val lcb = JSXLexerConfiguration.newBuilder();
  lcb.setNewlinesInQuotedStrings(true);
  lcb.setSquareBrackets(true)
  val lc = lcb.build();

  val r = UnicodeCharacterReader.newReader(getReader(args));
  val lex = JSXLexer.newLexer(lc, r);

  val pcb = JSXParserConfiguration.newBuilder();
  pcb.preserveLexicalInformation(true);
  val pc = pcb.build();
  val p = JSXParser.newParser(pc, lex);
  val scp = SCParser()
  val eq = ArrayDeque<SCError>()
  val de = SCDocumentEvaluator.beginDocument(
    base_directory = Paths.get("").toAbsolutePath())

  var document = Optional.empty<SDocument>()
  var eof = false
  while (!eof) {
    try {
      eq.clear()
      val e = p.parseExpressionOrEOF()
      if (e.isPresent) {
        val b = scp.parse(SCExpression.of(e.get()), eq)
        de.evaluateElement(b, eq)
      } else {
        eof = true
        document = Optional.of(de.evaluateEOF(eq))
        break
      }
    } catch (x : SCException.SCParseException) {
      for (e in eq) {
        System.err.println(
          "error: parse error: "
            + e.lexical.orElseGet { ImmutableLexicalPosition.newPosition(0, 0) }
            + ": "
            + e.message);
      }
    } catch (x : SCException.SCEvaluatorException) {
      for (e in eq) {
        System.err.println(
          "error: evaluation error: "
            + e.lexical.orElseGet { ImmutableLexicalPosition.newPosition(0, 0) }
            + ": "
            + e.message);
      }
    } catch (x : JSXParserException) {
      System.err.println(
        "error: parse error: "
          + x.lexicalInformation
          + ": "
          + x.message);
    }
  }

  if (document.isPresent) {
    val da = SAnnotator.document(document.get())
  }
}

private fun getReader(args : Array<String>) : Reader {
  if (args.size > 0) {
    return InputStreamReader(FileInputStream(args[0]));
  }
  return InputStreamReader(System.`in`);
}
