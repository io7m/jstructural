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

package com.io7m.jstructural.compact

import com.io7m.jlexing.core.ImmutableLexicalPosition
import com.io7m.jstructural.core.SDocument
import com.io7m.jstructural.core.SID
import com.io7m.jstructural.core.SNonEmptyList
import com.io7m.jstructural.core.SParagraph
import com.io7m.jstructural.core.SParagraphContent
import com.io7m.jstructural.core.SPart
import com.io7m.jstructural.core.SPartTitle
import com.io7m.jstructural.core.SSection
import com.io7m.jstructural.core.SSubsection
import com.io7m.junreachable.UnimplementedCodeException
import org.slf4j.LoggerFactory
import org.valid4j.Assertive
import java.nio.file.Path
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Deque
import java.util.Optional

class SCDocumentEvaluator private constructor(
  private val directory_stack : Deque<Path>) : SCDocumentEvaluatorType {

  private val evaluator = EvaluatorInitial(directory_stack)

  companion object {
    private val LOG = LoggerFactory.getLogger(SCDocumentEvaluator::class.java)

    private fun commandSelection(cs : List<String>) : String {
      val b = StringBuilder("{")
      val max = cs.size - 1
      for (i in 0 .. max) {
        b.append(cs.get(i))
        if (i < max) {
          b.append(" | ")
        }
      }
      b.append("}")
      return b.toString()
    }

    private fun elementCannotAppearHere(
      commands : List<String>,
      e : SCElement,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      val sb = StringBuilder()
      sb.append("This element cannot appear here.")
      sb.append(System.lineSeparator())
      sb.append("  Expected: One of ")
      sb.append(commandSelection(commands))
      sb.append(System.lineSeparator())
      sb.append("  Received: ")
      sb.append(e)
      eq.add(SCError.SCSemanticError(e.lexical, sb.toString()))
      return SCException.SCEvaluatorException()
    }

    private fun duplicateID(
      current : SCID,
      r : SCIDContextDeclaration.SCIDCollision,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      val sb = StringBuilder()
      sb.append("Duplicate ID declaration.")
      sb.append(System.lineSeparator())
      sb.append("  Original: ")
      sb.append(r.original)
      sb.append(" at ")
      sb.append(r.original.lexical.orElseGet { ImmutableLexicalPosition.newPosition(0, 0) })
      sb.append(System.lineSeparator())
      eq.add(SCError.SCSemanticError(current.lexical, sb.toString()))
      return SCException.SCEvaluatorException()
    }

    private fun emptyPart(
      e : SCElement,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical, "A part must contain at least one section."))
      return SCException.SCEvaluatorException()
    }

    private fun emptyPara(
      e : SCElement,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical, "A paragraph must contain at least one element."))
      return SCException.SCEvaluatorException()
    }

    fun beginDocument(base_directory : Path) : SCDocumentEvaluatorType {
      LOG.trace("starting evaluation at base directory {}", base_directory)
      val stack = ArrayDeque<Path>()
      stack.push(base_directory)
      return SCDocumentEvaluator(stack)
    }
  }

  private sealed class Result<T> {
    class ResultUnfinished<T>() : Result<T>()
    class ResultFinished<T>(val actual : T) : Result<T>()
  }

  private interface EvaluatorType<T> {
    fun evaluate(e : SCElement, scids : SCIDContextType, eq : Deque<SCError>) : Result<T>
  }

  private class EvaluatorParagraph(
    private val directory_stack : Deque<Path>,
    private val paragraph : SCBlock.SCBlockParagraph) : EvaluatorType<SParagraph> {

    private val content = ArrayList<SCInline>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SParagraph> {

      return when (e) {
        is SCElement.SCElementInline -> {
          this.content.add(e.actual)
          Result.ResultUnfinished()
        }
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockPart,
            is SCBlock.SCBlockSection,
            is SCBlock.SCBlockSubsection,
            is SCBlock.SCBlockParagraph -> {
              if (this.content.isEmpty()) {
                throw emptyPara(e, eq)
              }

              LOG.trace("finished paragraph due to {}", e)
              Assertive.require(!this.content.isEmpty())
              Result.ResultFinished(finishPara(scids, eq))
            }
            is SCBlock.SCBlockImport    ->
              throw UnimplementedCodeException()
            is SCBlock.SCBlockDocument  ->
              throw UnimplementedCodeException()
          }
      }
    }

    private fun finishPara(
      scids : SCIDContextType,
      eq : Deque<SCError>) : SParagraph {
      Assertive.require(!this.content.isEmpty())

      val non_empty_content : SNonEmptyList<SParagraphContent> =
        SNonEmptyList.newList(this.content.map { SCInlineContent.paraContent(it) })

      return when (this.paragraph.id.isPresent) {
        true  -> {
          val current_id = this.paragraph.id.get()
          val r = scids.declare(current_id)
          return when (r) {
            is SCIDContextDeclaration.SCIDCollision ->
              throw duplicateID(current_id, r, eq)
            is SCIDContextDeclaration.SCIDCreated   -> {
              when (this.paragraph.type.isPresent) {
                true  -> SParagraph.paragraphTypedID(
                  this.paragraph.type.get(), r.id, non_empty_content)
                false -> SParagraph.paragraphID(
                  r.id, non_empty_content)
              }
            }
          }
        }
        false -> {
          return when (this.paragraph.type.isPresent) {
            true  -> SParagraph.paragraphTyped(
              this.paragraph.type.get(), non_empty_content)
            false -> SParagraph.paragraph(
              non_empty_content)
          }
        }
      }
    }
  }

  private class EvaluatorSubsection(
    private val directory_stack : Deque<Path>,
    private val section : SCBlock.SCBlockSubsection) : EvaluatorType<SSubsection> {

    private var paragraph = Optional.empty<EvaluatorParagraph>()
    private val paragraphs = ArrayList<SParagraph>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SSubsection> {
      throw UnimplementedCodeException()
    }
  }

  private class EvaluatorSection(
    private val directory_stack : Deque<Path>,
    private val section : SCBlock.SCBlockSection) : EvaluatorType<SSection> {

    private var subsection = Optional.empty<EvaluatorSubsection>()
    private val subsections = ArrayList<SSubsection>()
    private var paragraph = Optional.empty<EvaluatorParagraph>()
    private val paragraphs = ArrayList<SParagraph>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SSection> {

      if (this.subsection.isPresent) {
        Assertive.require(!this.paragraph.isPresent)
        val r = this.subsection.get().evaluate(e, scids, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      if (this.paragraph.isPresent) {
        Assertive.require(!this.subsection.isPresent)
        val r = this.paragraph.get().evaluate(e, scids, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      return evaluateSection(e, scids, eq)
    }

    private fun evaluateSection(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SSection> {

      return when (e) {
        is SCElement.SCElementInline ->
          throw elementCannotAppearHere(listOf(
            SCCommandNames.SUBSECTION,
            SCCommandNames.PARA,
            SCCommandNames.IMPORT), e, eq)
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }
            is SCBlock.SCBlockParagraph -> {
              throw UnimplementedCodeException()
            }
            is SCBlock.SCBlockSubsection -> {
              throw UnimplementedCodeException()
            }
            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockPart,
            is SCBlock.SCBlockSection ->
              throw elementCannotAppearHere(listOf(
                SCCommandNames.SUBSECTION,
                SCCommandNames.PARA,
                SCCommandNames.IMPORT), e, eq)
          }
      }
    }
  }

  private class EvaluatorPart(
    private val directory_stack : Deque<Path>,
    private val part : SCBlock.SCBlockPart) : EvaluatorType<SPart> {

    private var section = Optional.empty<EvaluatorSection>()
    private val sections = ArrayList<SSection>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SPart> {

      if (this.section.isPresent) {
        val r = this.section.get().evaluate(e, scids, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      return evaluatePart(e, scids, eq)
    }

    private fun evaluatePart(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SPart> {
      Assertive.require(!this.section.isPresent)

      return when (e) {
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockParagraph,
            is SCBlock.SCBlockSubsection -> {
              throw elementCannotAppearHere(listOf(
                SCCommandNames.SECTION,
                SCCommandNames.PART,
                SCCommandNames.IMPORT), e, eq)
            }

            is SCBlock.SCBlockPart       -> {
              if (this.sections.isEmpty()) {
                throw emptyPart(e, eq)
              }

              Assertive.require(this.section.isPresent)
              Assertive.require(!this.sections.isEmpty())
              Result.ResultFinished(finishPart(scids, eq))
            }

            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }

            is SCBlock.SCBlockSection    -> {
              LOG.trace("begin section: {}", e.actual)
              val s = EvaluatorSection(this.directory_stack, e.actual)
              this.section = Optional.of(s)
              Result.ResultUnfinished()
            }
          }

        is SCElement.SCElementInline ->
          throw elementCannotAppearHere(listOf(
            SCCommandNames.SECTION,
            SCCommandNames.PART,
            SCCommandNames.IMPORT), e, eq)
      }
    }

    private fun finishPart(
      scids : SCIDContextType,
      eq : Deque<SCError>) : SPart {
      Assertive.require(this.section.isPresent)
      Assertive.require(!this.sections.isEmpty())

      this.section = Optional.empty()

      val non_empty_sections =
        SNonEmptyList.newList(this.sections)
      val concat_title =
        SPartTitle.partTitle(SCText.concatenate(part.title.elements))

      return when (this.part.id.isPresent) {
        true  -> {
          val current_id = this.part.id.get()
          val r = scids.declare(current_id)
          return when (r) {
            is SCIDContextDeclaration.SCIDCollision ->
              throw duplicateID(current_id, r, eq)
            is SCIDContextDeclaration.SCIDCreated   -> {
              when (this.part.type.isPresent) {
                true  -> SPart.partTypedID(
                  this.part.type.get(), r.id, concat_title, non_empty_sections)
                false -> SPart.partID(r.id, concat_title, non_empty_sections)
              }
            }
          }
        }
        false -> {
          return when (this.part.type.isPresent) {
            true  -> SPart.partTyped(this.part.type.get(), concat_title, non_empty_sections)
            false -> SPart.part(concat_title, non_empty_sections)
          }
        }
      }
    }
  }

  private fun declareID(get : SCID) : SID {
    throw UnimplementedCodeException()
  }

  private class EvaluatorDocument(
    private val directory_stack : Deque<Path>,
    private val document : SCBlock.SCBlockDocument) : EvaluatorType<SDocument> {

    private var section = Optional.empty<EvaluatorSection>()
    private var part = Optional.empty<EvaluatorPart>()
    private val parts = ArrayList<SPart>()
    private val sections = ArrayList<SSection>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SDocument> {

      if (this.part.isPresent) {
        Assertive.require(!this.section.isPresent)
        val r = this.part.get().evaluate(e, scids, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      if (this.section.isPresent) {
        Assertive.require(!this.part.isPresent)
        val r = this.section.get().evaluate(e, scids, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      return evaluateDocument(e, eq)
    }

    private fun evaluateDocument(
      e : SCElement,
      eq : Deque<SCError>) : Result<SDocument> {

      Assertive.require(!this.section.isPresent)
      Assertive.require(!this.part.isPresent)

      return when (e) {
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockParagraph,
            is SCBlock.SCBlockSubsection -> {
              throw elementCannotAppearHere(listOf(
                SCCommandNames.SECTION,
                SCCommandNames.IMPORT), e, eq)
            }

            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }

            is SCBlock.SCBlockSection    -> {
              LOG.trace("begin section: {}", e.actual)
              val s = EvaluatorSection(this.directory_stack, e.actual)
              this.section = Optional.of(s)
              Result.ResultUnfinished()
            }

            is SCBlock.SCBlockPart       -> {
              LOG.trace("begin part: {}", e.actual)
              val p = EvaluatorPart(this.directory_stack, e.actual)
              this.part = Optional.of(p)
              Result.ResultUnfinished()
            }
          }

        is SCElement.SCElementInline -> {
          throw elementCannotAppearHere(listOf(
            SCCommandNames.PART,
            SCCommandNames.SECTION,
            SCCommandNames.IMPORT), e, eq)
        }
      }
    }
  }

  private class EvaluatorInitial(
    private val directory_stack : Deque<Path>) : EvaluatorType<SDocument> {

    private var document = Optional.empty<EvaluatorDocument>()

    override fun evaluate(
      e : SCElement,
      scids : SCIDContextType,
      eq : Deque<SCError>) : Result<SDocument> {

      return if (this.document.isPresent) {
        this.document.get().evaluate(e, scids, eq)
      } else {
        when (e) {
          is SCElement.SCElementInline -> {
            throw elementCannotAppearHere(listOf(
              SCCommandNames.DOCUMENT,
              SCCommandNames.IMPORT), e, eq)
          }
          is SCElement.SCElementBlock  ->
            when (e.actual) {
              is SCBlock.SCBlockImport   -> {
                throw UnimplementedCodeException()
              }
              is SCBlock.SCBlockDocument -> {
                LOG.trace("begin document: {}", e.actual)
                this.document = Optional.of(EvaluatorDocument(directory_stack, e.actual))
                return Result.ResultUnfinished()
              }
              is SCBlock.SCBlockParagraph,
              is SCBlock.SCBlockSection,
              is SCBlock.SCBlockSubsection,
              is SCBlock.SCBlockPart     -> {
                throw elementCannotAppearHere(listOf(
                  SCCommandNames.DOCUMENT,
                  SCCommandNames.IMPORT), e, eq)
              }
            }
        }
      }
    }
  }

  override fun evaluateElement(
    element : SCElement,
    scids : SCIDContextType,
    error_queue : Deque<SCError>) {
    this.evaluator.evaluate(element, scids, error_queue)
  }

  override fun evaluateEOF(
    scids : SCIDContextType,
    error_queue : Deque<SCError>) : SDocument {
    throw UnimplementedCodeException()
  }
}
