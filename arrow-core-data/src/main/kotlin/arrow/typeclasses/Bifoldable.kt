package arrow.typeclasses

import arrow.Kind2
import arrow.core.Eval
import arrow.core.identity

/**
 * ank_macro_hierarchy(arrow.typeclasses.Bifoldable)
 */
interface Bifoldable<F> {

  fun <A, B, C> Kind2<F, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C

  fun <A, B, C> Kind2<F, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C>

  fun <A, B, C> Kind2<F, A, B>.bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C) = MN.run {
    bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
  }

  /**
   * You might have a case where both sides of a bifoldable are of the same type and you have no bias towards one side.
   * One such case might be defaulting in case of error.
   * Collapse offers precisely that: Getting whichever of the values contained in the foldable for consumption.
   *
   * Example:
   *
   * ```kotlin:ank:playground
   * import arrow.core.extensions.either.bifoldable.*
   *
   * data class Fail(val message: String)
   * //sampleStart
   * fun somethingThatMighFail(): Either<Fail, String> = Fail("Something broke").left()
   * val attempt: Either<Fail, String> = somethingThatMighFail()
   * val defaulted: Either<String, String> = attempt.mapLeft { "Failed with message: ${it.message}" }
   * val collapsedValue: String = defaulted.collapse(String.monoid())
   * //sampleEnd
   * fun main() {
   *   println("The collapsed value is: $collapsedValue")
   * }
   * ```
   */
  fun <A, B : A> Kind2<F, A, B>.collapse(MN: Monoid<A>): A =
    bifoldMap(MN, ::identity, ::identity)
}
