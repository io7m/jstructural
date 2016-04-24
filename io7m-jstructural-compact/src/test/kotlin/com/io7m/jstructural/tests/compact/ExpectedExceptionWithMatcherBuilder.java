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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.matchers.JUnitMatchers.isThrowable;

public final class ExpectedExceptionWithMatcherBuilder
{
  static final class MatcherPlusExec extends BaseMatcher<Object>
  {
    private final Runnable exec;
    private final Matcher<?> matcher;

    MatcherPlusExec(
      final Runnable exec,
      final Matcher<?> matcher)
    {
      this.exec = exec;
      this.matcher = matcher;
    }

    @Override
    public boolean matches(final Object item)
    {
      if (this.matcher.matches(item)) {
        this.exec.run();
        return true;
      }
      return false;
    }

    @Override
    public void describeTo(final Description description)
    {
      this.matcher.describeTo(description);
    }
  }

  private final List<MatcherPlusExec> fMatchers =
    new ArrayList<MatcherPlusExec>();

  void add(final Matcher<?> matcher) {
    this.fMatchers.add(new MatcherPlusExec(() -> {}, matcher));
  }

  boolean expectsThrowable() {
    return !this.fMatchers.isEmpty();
  }

  Matcher<Throwable> build() {
    return isThrowable(this.allOfTheMatchers());
  }

  private Matcher<Throwable> allOfTheMatchers() {
    if (this.fMatchers.size() == 1) {
      return this.cast(this.fMatchers.get(0));
    }
    return allOf(this.castedMatchers());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<Matcher<? super Throwable>> castedMatchers() {
    return this.fMatchers.stream().map(mpe -> (Matcher<? super Throwable>) mpe).collect(
      Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Matcher<Throwable> cast(final Matcher<?> singleMatcher) {
    return (Matcher<Throwable>) singleMatcher;
  }

  void add(
    final Matcher<?> objectMatcher,
    final Runnable func)
  {
    this.fMatchers.add(new MatcherPlusExec(func, objectMatcher));
  }
}
