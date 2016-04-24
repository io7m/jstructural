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

package com.io7m.jstructural.tests.compact;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

public final class ExpectedExceptionWith implements TestRule
{
  private final ExpectedExceptionWithMatcherBuilder fMatcherBuilder =
    new ExpectedExceptionWithMatcherBuilder();
  private boolean handleAssumptionViolatedExceptions = false;
  private boolean handleAssertionErrors = false;

  private ExpectedExceptionWith()
  {
  }

  /**
   * @return a Rule that expects no exception to be thrown (identical to
   * behavior without this Rule)
   */
  public static ExpectedExceptionWith none()
  {
    return new ExpectedExceptionWith();
  }

  public ExpectedExceptionWith handleAssertionErrors()
  {
    this.handleAssertionErrors = true;
    return this;
  }

  public ExpectedExceptionWith handleAssumptionViolatedExceptions()
  {
    this.handleAssumptionViolatedExceptions = true;
    return this;
  }

  @Override
  public Statement apply(
    final Statement base,
    final org.junit.runner.Description description)
  {
    return new ExpectedExceptionStatement(base);
  }

  /**
   * Adds {@code matcher} to the list of requirements for any thrown exception.
   */

  public void expect(final Matcher<?> matcher)
  {
    this.fMatcherBuilder.add(matcher);
  }

  /**
   * Adds to the list of requirements for any thrown exception that it should be
   * an instance of {@code type}
   */

  public void expect(final Class<? extends Throwable> type)
  {
    this.expect(instanceOf(type));
  }

  /**
   * Adds to the list of requirements for any thrown exception that it should
   * <em>contain</em> string {@code substring}
   */

  public void expectMessage(final String substring)
  {
    this.expectMessage(containsString(substring));
  }

  /**
   * Adds {@code matcher} to the list of requirements for the message returned
   * from any thrown exception.
   */

  public void expectMessage(final Matcher<String> matcher)
  {
    this.expect(hasMessage(matcher));
  }

  /**
   * Adds {@code matcher} to the list of requirements for the cause of any
   * thrown exception.
   */
  public void expectCause(final Matcher<? extends Throwable> expectedCause)
  {
    this.expect(hasCause(expectedCause));
  }

  private void failDueToMissingException()
    throws AssertionError
  {
    final String expectation =
      StringDescription.toString(this.fMatcherBuilder.build());
    fail("Expected test to throw " + expectation);
  }

  private void optionallyHandleException(
    final Throwable e,
    final boolean handleException)
    throws Throwable
  {
    if (handleException) {
      this.handleException(e);
    } else {
      throw e;
    }
  }

  private void handleException(final Throwable e)
    throws Throwable
  {
    if (this.fMatcherBuilder.expectsThrowable()) {
      assertThat(e, this.fMatcherBuilder.build());
    } else {
      throw e;
    }
  }

  public void expectWith(
    final Class<?> type,
    final Runnable func)
  {
    this.fMatcherBuilder.add(instanceOf(type), func);
  }

  private class ExpectedExceptionStatement extends Statement
  {
    private final Statement fNext;

    public ExpectedExceptionStatement(final Statement base)
    {
      this.fNext = base;
    }

    @Override
    public void evaluate()
      throws Throwable
    {
      try {
        this.fNext.evaluate();
        if (ExpectedExceptionWith.this.fMatcherBuilder.expectsThrowable()) {
          ExpectedExceptionWith.this.failDueToMissingException();
        }
      } catch (final AssumptionViolatedException e) {
        ExpectedExceptionWith.this.optionallyHandleException(
          e, ExpectedExceptionWith.this.handleAssumptionViolatedExceptions);
      } catch (final AssertionError e) {
        ExpectedExceptionWith.this.optionallyHandleException(
          e, ExpectedExceptionWith.this.handleAssertionErrors);
      } catch (final Throwable e) {
        ExpectedExceptionWith.this.handleException(e);
      }
    }
  }
}
