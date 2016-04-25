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

import com.io7m.jstructural.core.SDocument
import com.io7m.jstructural.core.SDocumentTitle
import com.io7m.jstructural.core.SDocumentWithParts
import com.io7m.jstructural.core.SDocumentWithSections
import com.io7m.jstructural.core.SID
import com.io7m.jstructural.core.SNonEmptyList
import com.io7m.jstructural.core.SParagraph
import com.io7m.jstructural.core.SParagraphContent
import com.io7m.jstructural.core.SPart
import com.io7m.jstructural.core.SPartTitle
import com.io7m.jstructural.core.SSection
import com.io7m.jstructural.core.SSectionTitle
import com.io7m.jstructural.core.SSectionWithParagraphs
import com.io7m.jstructural.core.SSectionWithSubsections
import com.io7m.jstructural.core.SSubsection
import com.io7m.jstructural.core.SSubsectionContent
import com.io7m.jstructural.core.SSubsectionTitle
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

    private fun emptyPart(
      e : SCLexicalType,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical, "A part must contain at least one section."))
      return SCException.SCEvaluatorException()
    }

    private fun emptyPara(
      e : SCLexicalType,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical, "A paragraph must contain at least one element."))
      return SCException.SCEvaluatorException()
    }

    private fun emptySection(
      e : SCLexicalType,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical,
        "A section must contain at least one paragraph or subsection."))
      return SCException.SCEvaluatorException()
    }

    private fun emptySubsection(
      e : SCLexicalType,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical,
        "A subsection must contain at least one paragraph or formal item."))
      return SCException.SCEvaluatorException()
    }

    private fun unexpectedEOF(
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(Optional.empty(), "Unexpected EOF"))
      return SCException.SCEvaluatorException()
    }

    private fun sectionMixedContent(
      e : SCLexicalType,
      eq : Deque<SCError>) : SCException.SCEvaluatorException {
      eq.add(SCError.SCSemanticError(e.lexical,
        "A section may contain subsections or subsection content (paragraphs, etc) but not both."))
      return SCException.SCEvaluatorException()
    }

    fun beginDocument(base_directory : Path) : SCDocumentEvaluatorType {
      LOG.trace("starting evaluation at base directory {}", base_directory)
      val stack = ArrayDeque<Path>()
      stack.push(base_directory)
      return SCDocumentEvaluator(stack)
    }
  }

  private sealed class Result<T>(val consumed_element : Boolean) {

    class ResultUnfinished<T>(
      consumed_element : Boolean) : Result<T>(consumed_element)

    class ResultFinished<T>(
      consumed_element : Boolean,
      val result : T) : Result<T>(consumed_element)
  }

  private interface EvaluatorType<T> {

    fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<T>

    fun evaluateEOF(
      error_queue : Deque<SCError>) : T

  }

  private class EvaluatorParagraph(
    private val directory_stack : Deque<Path>,
    private val paragraph : SCBlock.SCBlockParagraph) : EvaluatorType<SParagraph> {

    private val content = ArrayList<SCInline>()

    init {
      LOG.trace("start paragraph {}", this.paragraph)
    }

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SParagraph> {

      return when (e) {
        is SCElement.SCElementInline -> {
          when (e.actual) {
            is SCInline.SCInlineInclude ->
              throw UnimplementedCodeException()
            is SCInline.SCInlineLink,
            is SCInline.SCInlineVerbatim,
            is SCInline.SCInlineText,
            is SCInline.SCInlineTerm,
            is SCInline.SCInlineImage   -> {
              this.content.add(e.actual)
              Result.ResultUnfinished(consumed_element = true)
            }
          }
        }
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockImport    ->
              throw UnimplementedCodeException()

            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockPart,
            is SCBlock.SCBlockSection,
            is SCBlock.SCBlockSubsection,
            is SCBlock.SCBlockParagraph -> {
              if (this.content.isEmpty()) {
                throw emptyPara(e, eq)
              }

              LOG.trace("finished paragraph due to {}:{}", e, e.lexical)
              Assertive.require(!this.content.isEmpty())
              Result.ResultFinished(
                consumed_element = false,
                result = finishPara(eq))
            }
          }
      }
    }

    private fun finishPara(
      eq : Deque<SCError>) : SParagraph {
      Assertive.require(!this.content.isEmpty())

      val non_empty_content : SNonEmptyList<SParagraphContent> =
        SNonEmptyList.newList(this.content.map { SCInlineContent.paraContent(it) })

      return when (this.paragraph.id.isPresent) {
        true  -> {
          val current_id = this.paragraph.id.get()
          when (this.paragraph.type.isPresent) {
            true  -> SParagraph.paragraphTypedID(
              this.paragraph.type.get(),
              SID.newID(current_id.id),
              non_empty_content)
            false -> SParagraph.paragraphID(
              SID.newID(current_id.id),
              non_empty_content)
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

    override fun evaluateEOF(error_queue : Deque<SCError>) : SParagraph {
      if (this.content.isEmpty()) {
        throw emptyPara(this.paragraph, error_queue)
      }

      LOG.trace("finished paragraph due to eof")
      Assertive.require(!this.content.isEmpty())
      return finishPara(error_queue)
    }
  }

  private class EvaluatorSubsection(
    private val directory_stack : Deque<Path>,
    private val subsection : SCBlock.SCBlockSubsection) : EvaluatorType<SSubsection> {

    private var paragraph = Optional.empty<EvaluatorParagraph>()
    private val contents = ArrayList<SSubsectionContent>()

    init {
      LOG.trace("start subsection {}", subsection)
    }

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SSubsection> {

      if (this.paragraph.isPresent) {
        val r = this.paragraph.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished -> {
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          }
          is SCDocumentEvaluator.Result.ResultFinished   -> {
            this.contents.add(r.result)
            this.paragraph = Optional.empty()
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          }
        }
      }

      return evaluateSubsection(e, eq)
    }

    private fun evaluateSubsection(
      e : SCElement,
      eq : Deque<SCError>) : Result<SSubsection> {

      return when (e) {
        is SCElement.SCElementInline ->
          throw elementCannotAppearHere(SCCommandNames.BLOCK_COMMANDS, e, eq)
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockImport    -> {
              throw UnimplementedCodeException()
            }
            is SCBlock.SCBlockParagraph -> {
              Assertive.require(!this.paragraph.isPresent)
              this.paragraph = Optional.of(
                EvaluatorParagraph(this.directory_stack, e.actual))
              Result.ResultUnfinished(consumed_element = true)
            }
            is SCBlock.SCBlockSubsection,
            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockPart,
            is SCBlock.SCBlockSection   -> {
              if (this.contents.isEmpty()) {
                throw emptySubsection(e, eq)
              }

              this.paragraph = Optional.empty()

              LOG.trace("finished subsection due to {}:{}", e, e.lexical)
              Result.ResultFinished(
                consumed_element = false,
                result = finishSubsection())
            }
          }
      }
    }

    private fun finishSubsection() : SSubsection {
      Assertive.require(!this.contents.isEmpty())

      val title =
        SSubsectionTitle.subsectionTitle(SCText.concatenate(this.subsection.title.elements))
      val content = SNonEmptyList.newList(this.contents)
      return when (this.subsection.id.isPresent) {
        true  -> {
          when (this.subsection.type.isPresent) {
            true  ->
              SSubsection.subsectionTypedID(
                this.subsection.type.get(),
                SID.newID(this.subsection.id.get().id),
                title,
                content)
            false ->
              SSubsection.subsectionID(
                SID.newID(this.subsection.id.get().id), title, content)
          }
        }
        false -> {
          when (this.subsection.type.isPresent) {
            true  ->
              SSubsection.subsectionTyped(this.subsection.type.get(), title, content)
            false ->
              SSubsection.subsection(title, content)
          }
        }
      }
    }

    override fun evaluateEOF(
      error_queue : Deque<SCError>) : SSubsection {

      if (this.contents.isEmpty()) {
        throw emptySubsection(this.subsection, error_queue)
      }

      this.paragraph = Optional.empty()

      LOG.trace("finished subsection due to eof")
      return finishSubsection()
    }
  }

  private class EvaluatorSection(
    private val directory_stack : Deque<Path>,
    private val section : SCBlock.SCBlockSection) : EvaluatorType<SSection> {

    private var subsection = Optional.empty<EvaluatorSubsection>()
    private val subsections = ArrayList<SSubsection>()
    private var paragraph = Optional.empty<EvaluatorParagraph>()
    private val subsection_content = ArrayList<SSubsectionContent>()

    init {
      LOG.trace("start section {}", this.section)
    }

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SSection> {

      if (this.subsection.isPresent) {
        Assertive.require(!this.paragraph.isPresent)

        if (this.subsection_content.isNotEmpty()) {
          throw sectionMixedContent(e, eq)
        }

        Assertive.require(this.subsection_content.isEmpty())
        val r = this.subsection.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          is SCDocumentEvaluator.Result.ResultFinished   -> {
            this.subsections.add(r.result)
            this.subsection = Optional.empty()
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          }
        }
      }

      if (this.paragraph.isPresent) {
        Assertive.require(!this.subsection.isPresent)

        if (this.subsections.isNotEmpty()) {
          throw sectionMixedContent(e, eq)
        }

        Assertive.require(this.subsections.isEmpty())
        val r = this.paragraph.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          is SCDocumentEvaluator.Result.ResultFinished   -> {
            this.subsection_content.add(r.result)
            this.paragraph = Optional.empty()
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          }
        }
      }

      return evaluateSection(e, eq)
    }

    private fun evaluateSection(
      e : SCElement,
      eq : Deque<SCError>) : Result<SSection> {

      return when (e) {
        is SCElement.SCElementInline ->
          throw UnimplementedCodeException()
        is SCElement.SCElementBlock  ->
          when (e.actual) {
            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }
            is SCBlock.SCBlockParagraph  -> {
              Assertive.require(!this.paragraph.isPresent)
              Assertive.require(!this.subsection.isPresent)
              this.paragraph = Optional.of(
                EvaluatorParagraph(this.directory_stack, e.actual))
              Result.ResultUnfinished(consumed_element = true)
            }
            is SCBlock.SCBlockSubsection -> {
              Assertive.require(!this.paragraph.isPresent)
              Assertive.require(!this.subsection.isPresent)
              this.subsection = Optional.of(
                EvaluatorSubsection(this.directory_stack, e.actual))
              Result.ResultUnfinished(consumed_element = true)
            }
            is SCBlock.SCBlockDocument,
            is SCBlock.SCBlockPart,
            is SCBlock.SCBlockSection    -> {
              if (this.subsections.isEmpty() && this.subsection_content.isEmpty()) {
                throw emptySection(e, eq)
              }

              this.paragraph = Optional.empty()
              this.subsection = Optional.empty()

              LOG.trace("finished section due to {}:{}", e, e.lexical)
              Result.ResultFinished(
                consumed_element = false,
                result = finishSection())
            }
          }
      }
    }

    private fun finishSection() : SSection =
      when (this.subsections.isNotEmpty()) {
        true  -> finishSectionWithSubsections()
        false -> finishSectionWithParagraphs()
      }

    private fun finishSectionWithParagraphs() : SSectionWithParagraphs {
      Assertive.require(this.subsections.isEmpty())
      Assertive.require(!this.subsection.isPresent)
      Assertive.require(this.subsection_content.isNotEmpty())

      val title =
        SSectionTitle.sectionTitle(
          SCText.concatenate(this.section.title.elements))
      val content =
        SNonEmptyList.newList(this.subsection_content)

      return when (this.section.id.isPresent) {
        true  ->
          when (this.section.type.isPresent) {
            true  ->
              SSectionWithParagraphs.sectionTypedID(
                this.section.type.get(),
                SID.newID(this.section.id.get().id),
                title,
                content)
            false ->
              SSectionWithParagraphs.sectionID(
                SID.newID(this.section.id.get().id), title, content)
          }

        false ->
          when (this.section.type.isPresent) {
            true  -> SSectionWithParagraphs.sectionTyped(this.section.type.get(), title, content)
            false -> SSectionWithParagraphs.section(title, content)
          }
      }
    }

    private fun finishSectionWithSubsections() : SSectionWithSubsections {
      Assertive.require(this.subsection_content.isEmpty())
      Assertive.require(!this.paragraph.isPresent)
      Assertive.require(this.subsections.isNotEmpty())

      val title =
        SSectionTitle.sectionTitle(
          SCText.concatenate(this.section.title.elements))
      val content =
        SNonEmptyList.newList(this.subsections)

      return when (this.section.id.isPresent) {
        true  ->
          when (this.section.type.isPresent) {
            true  ->
              SSectionWithSubsections.sectionTypedID(
                this.section.type.get(),
                SID.newID(this.section.id.get().id),
                title,
                content)
            false ->
              SSectionWithSubsections.sectionID(
                SID.newID(this.section.id.get().id), title, content)
          }
        false ->
          when (this.section.type.isPresent) {
            true  -> SSectionWithSubsections.sectionTyped(this.section.type.get(), title, content)
            false -> SSectionWithSubsections.section(title, content)
          }
      }
    }

    override fun evaluateEOF(error_queue : Deque<SCError>) : SSection {

      if (this.subsection.isPresent) {
        val r = this.subsection.get().evaluateEOF(error_queue)
        this.subsections.add(r)
        this.subsection = Optional.empty()
      }

      if (this.paragraph.isPresent) {
        val r = this.paragraph.get().evaluateEOF(error_queue)
        this.subsection_content.add(r)
        this.paragraph = Optional.empty()
      }

      if (this.subsections.isEmpty() && this.subsection_content.isEmpty()) {
        throw emptySection(this.section, error_queue)
      }

      this.paragraph = Optional.empty()
      this.subsection = Optional.empty()

      LOG.trace("finished section due to EOF")
      return finishSection()
    }
  }

  private class EvaluatorPart(
    private val directory_stack : Deque<Path>,
    private val part : SCBlock.SCBlockPart) : EvaluatorType<SPart> {

    private var section = Optional.empty<EvaluatorSection>()
    private val sections = ArrayList<SSection>()

    init {
      LOG.trace("start part {}", part)
    }

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SPart> {

      if (this.section.isPresent) {
        val r = this.section.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      return evaluatePart(e, eq)
    }

    private fun evaluatePart(
      e : SCElement,
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
              Assertive.require(this.sections.isNotEmpty())
              Result.ResultFinished(
                consumed_element = false,
                result = finishPart(eq))
            }

            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }

            is SCBlock.SCBlockSection    -> {
              LOG.trace("begin section: {}", e.actual)
              val s = EvaluatorSection(this.directory_stack, e.actual)
              this.section = Optional.of(s)
              Result.ResultUnfinished(consumed_element = true)
            }
          }

        is SCElement.SCElementInline ->
          throw elementCannotAppearHere(listOf(
            SCCommandNames.SECTION,
            SCCommandNames.PART,
            SCCommandNames.IMPORT), e, eq)
      }
    }

    private fun finishPart(eq : Deque<SCError>) : SPart {
      Assertive.require(this.section.isPresent)
      Assertive.require(this.sections.isNotEmpty())

      this.section = Optional.empty()

      val non_empty_sections =
        SNonEmptyList.newList(this.sections)
      val concat_title =
        SPartTitle.partTitle(SCText.concatenate(part.title.elements))

      return when (this.part.id.isPresent) {
        true  -> {
          when (this.part.type.isPresent) {
            true  ->
              SPart.partTypedID(
                this.part.type.get(),
                SID.newID(this.part.id.get().id),
                concat_title,
                non_empty_sections)
            false ->
              SPart.partID(
                SID.newID(this.part.id.get().id),
                concat_title,
                non_empty_sections)
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

    override fun evaluateEOF(error_queue : Deque<SCError>) : SPart {
      throw UnimplementedCodeException()
    }
  }

  private class EvaluatorDocument(
    private val directory_stack : Deque<Path>,
    private val document : SCBlock.SCBlockDocument) : EvaluatorType<SDocument> {

    private var section = Optional.empty<EvaluatorSection>()
    private var part = Optional.empty<EvaluatorPart>()
    private val parts = ArrayList<SPart>()
    private val sections = ArrayList<SSection>()

    init {
      LOG.trace("start document {}", document)
    }

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SDocument> {

      if (this.part.isPresent) {
        Assertive.require(!this.section.isPresent)
        val r = this.part.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            throw UnimplementedCodeException()
          is SCDocumentEvaluator.Result.ResultFinished   ->
            throw UnimplementedCodeException()
        }
      }

      if (this.section.isPresent) {
        Assertive.require(!this.part.isPresent)
        val r = this.section.get().evaluate(e, eq)
        return when (r) {
          is SCDocumentEvaluator.Result.ResultUnfinished ->
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          is SCDocumentEvaluator.Result.ResultFinished   -> {
            this.sections.add(r.result)
            this.section = Optional.empty()
            Result.ResultUnfinished(consumed_element = r.consumed_element)
          }
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
                SCCommandNames.PART,
                SCCommandNames.SECTION,
                SCCommandNames.IMPORT), e, eq)
            }

            is SCBlock.SCBlockImport     -> {
              throw UnimplementedCodeException()
            }

            is SCBlock.SCBlockSection    -> {
              this.section = Optional.of(EvaluatorSection(this.directory_stack, e.actual))
              Result.ResultUnfinished(consumed_element = true)
            }

            is SCBlock.SCBlockPart       -> {
              this.part = Optional.of(EvaluatorPart(this.directory_stack, e.actual))
              Result.ResultUnfinished(consumed_element = true)
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

    override fun evaluateEOF(error_queue : Deque<SCError>) : SDocument {

      if (this.part.isPresent) {
        Assertive.require(!this.section.isPresent)
        val r = this.part.get().evaluateEOF(error_queue)
        this.parts.add(r)
        this.part = Optional.empty()
        val title = SDocumentTitle.documentTitle(SCText.concatenate(this.document.title.elements))
        val content = SNonEmptyList.newList(this.parts)
        return SDocumentWithParts.document(title, content)
      }

      if (this.section.isPresent) {
        Assertive.require(!this.part.isPresent)
        val r = this.section.get().evaluateEOF(error_queue)
        this.sections.add(r)
        this.section = Optional.empty()
        val title = SDocumentTitle.documentTitle(SCText.concatenate(this.document.title.elements))
        val content = SNonEmptyList.newList(this.sections)
        return SDocumentWithSections.document(title, content)
      }

      Assertive.require(this.parts.isEmpty())
      Assertive.require(this.sections.isEmpty())
      throw unexpectedEOF(error_queue)
    }
  }

  private class EvaluatorInitial(
    private val directory_stack : Deque<Path>) : EvaluatorType<SDocument> {

    private var document = Optional.empty<EvaluatorDocument>()

    override fun evaluate(
      e : SCElement,
      eq : Deque<SCError>) : Result<SDocument> {

      return if (this.document.isPresent) {
        this.document.get().evaluate(e, eq)
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
                this.document = Optional.of(EvaluatorDocument(directory_stack, e.actual))
                return Result.ResultUnfinished(consumed_element = true)
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

    override fun evaluateEOF(error_queue : Deque<SCError>) : SDocument {

      return if (this.document.isPresent) {
        this.document.get().evaluateEOF(error_queue)
      } else {
        throw unexpectedEOF(error_queue)
      }
    }
  }

  override fun evaluateElement(
    element : SCElement,
    error_queue : Deque<SCError>) {
    LOG.trace("evaluate {}", element)
    val r = this.evaluator.evaluate(element, error_queue)
    return when (r) {
      is SCDocumentEvaluator.Result.ResultUnfinished ->
        return if (r.consumed_element == false) {
          this.evaluateElement(element, error_queue)
        } else {

        }
      is SCDocumentEvaluator.Result.ResultFinished   ->
        throw UnimplementedCodeException()
    }
  }

  override fun evaluateEOF(error_queue : Deque<SCError>) : SDocument {
    LOG.trace("evaluate eof")
    return this.evaluator.evaluateEOF(error_queue)
  }
}
