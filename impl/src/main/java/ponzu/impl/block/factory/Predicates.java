/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ponzu.impl.block.factory;

import java.util.Arrays;

import ponzu.api.block.function.Function;
import ponzu.api.block.predicate.Predicate;
import ponzu.impl.utility.Iterate;

/**
 * The Predicates class can be used to build common Predicates to be used by methods such
 * as find, filter, and filterNot on the Iterate, MapIterate, and ArrayIterate classes.  Predicates supports
 * equals, not equals, less than, greater than, less than or equal to, greater than or equal to, in, not in
 * and, or, and several other Predicate type operations.
 */
public abstract class Predicates<T>
        implements Predicate<T>
{
    private static final long serialVersionUID = 1L;
    private static final Predicates<Object> ALWAYS_TRUE = new AlwaysTrue();
    private static final Predicates<Object> ALWAYS_FALSE = new AlwaysFalse();
    private static final Predicates<Object> IS_NULL = new IsNull();
    private static final Predicates<Object> NOT_NULL = new NotNull();

    public static <T> Predicates<T> adapt(Predicate<T> predicate)
    {
        return new PredicateAdapter<T>(predicate);
    }

    public static <T> Predicate<T> synchronizedEach(Predicate<T> predicate)
    {
        return new SynchronizedPredicate<T>(predicate);
    }

    public static <T> Predicates<T> or(Iterable<? extends Predicate<? super T>> predicates)
    {
        return new OrIterablePredicate<T>(predicates);
    }

    public static <T> Predicates<T> or(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
    {
        return new OrPredicate<T>(predicate1, predicate2);
    }

    public static <T> Predicates<T> or(Predicate<? super T>... predicates)
    {
        return new OrIterablePredicate<T>(Arrays.asList(predicates));
    }

    public static <T> Predicates<T> and(Iterable<? extends Predicate<? super T>> predicates)
    {
        return new AndIterablePredicate<T>(predicates);
    }

    public static <T> Predicates<T> and(Predicate<? super T> predicate1, Predicate<? super T> predicate2)
    {
        return new AndPredicate<T>(predicate1, predicate2);
    }

    public static <T> Predicates<T> and(Predicate<? super T>... predicates)
    {
        return new AndIterablePredicate<T>(Arrays.asList(predicates));
    }

    public static <T> Predicates<T> not(Predicate<T> predicate)
    {
        return new NotPredicate<T>(predicate);
    }

    public Predicates<T> not()
    {
        return Predicates.not(this);
    }

    public static <T> Predicates<T> neither(Predicate<? super T> operation1, Predicate<? super T> operation2)
    {
        return new NeitherPredicate<T>(operation1, operation2);
    }

    public static <T> Predicates<T> noneOf(Predicate<? super T>... operations)
    {
        return new NoneOfIterablePredicate<T>(Arrays.asList(operations));
    }

    public static <T> Predicates<T> noneOf(Iterable<? extends Predicate<? super T>> operations)
    {
        return new NoneOfIterablePredicate<T>(operations);
    }

    /**
     * Tests for equality.
     */
    public static Predicates<Object> equal(Object object)
    {
        if (object == null)
        {
            return Predicates.isNull();
        }
        return new EqualPredicate(object);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, inclusive
     * of the from and to values.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusive(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusive<T>(from, to);
    }

    private static void failIfDifferentTypes(Object from, Object to)
    {
        if (!from.getClass().equals(to.getClass()))
        {
            throw new IllegalArgumentException("Trying to do a between comparison with two different types "
                    + from.getClass()
                    + ':'
                    + to.getClass());
        }
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, exclusive
     * of the from and to values.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenExclusive(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenExclusive<T>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, inclusive
     * of the from and exclusive from the to value.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusiveFrom(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusiveFrom<T>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is within the range, exclusive
     * of the from and inclusive of the to value.
     */
    public static <T extends Comparable<? super T>> Predicates<T> betweenInclusiveTo(T from, T to)
    {
        Predicates.failIfDifferentTypes(from, to);
        return new BetweenInclusiveTo<T>(from, to);
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is contained in the iterable.
     * This will clearly work faster if the specified iterable is a Set.
     */
    public static Predicates<Object> in(Iterable<?> iterable)
    {
        return new InPredicate(iterable);
    }

    public static Predicates<Object> in(Object[] array)
    {
        return new InPredicate(Arrays.asList(array));
    }

    /**
     * Creates a predicate which returns true if an attribute selected from an object passed to accept method
     * is contained in the iterable.  This will clearly work faster if the specified iterable is a Set.
     */
    public static <T> Predicates<T> attributeIn(
            Function<? super T, ?> function,
            Iterable<?> iterable)
    {
        return new AttributePredicate<T, Object>(function, Predicates.in(iterable));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusive(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<T, V>(function, Predicates.betweenInclusive(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenExclusive(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<T, V>(function, Predicates.betweenExclusive(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusiveFrom(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<T, V>(function, Predicates.betweenInclusiveFrom(from, to));
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeBetweenInclusiveTo(
            Function<? super T, ? extends V> function,
            V from,
            V to)
    {
        return new AttributePredicate<T, V>(function, Predicates.betweenInclusiveTo(from, to));
    }

    /**
     * Creates a predicate which returns true if an object passed to accept method is not contained in
     * the iterable.  This will clearly work faster if the specified iterable is a Set.
     */
    public static Predicates<Object> notIn(Iterable<?> iterable)
    {
        return new NotInPredicate(iterable);
    }

    public static Predicates<Object> notIn(Object[] array)
    {
        return new NotInPredicate(Arrays.asList(array));
    }

    /**
     * Creates a predicate which returns true if an attribute selected from an object passed to accept method
     * is not contained in the iterable.  This will clearly work faster if the specified iterable is a Set.
     */
    public static <T> Predicates<T> attributeNotIn(
            Function<? super T, ?> function,
            Iterable<?> iterable)
    {
        return new AttributePredicate<T, Object>(function, Predicates.notIn(iterable));
    }

    public static <T extends Comparable<? super T>> Predicates<T> lessThan(T object)
    {
        return new LessThanPredicate<T>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeLessThan(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<T, V>(function, new LessThanPredicate<V>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> lessThanOrEqualTo(T object)
    {
        return new LessThanOrEqualPredicate<T>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeLessThanOrEqualTo(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<T, V>(function, new LessThanOrEqualPredicate<V>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> greaterThan(T object)
    {
        return new GreaterThanPredicate<T>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeGreaterThan(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<T, V>(function, new GreaterThanPredicate<V>(object));
    }

    public static <T extends Comparable<? super T>> Predicates<T> greaterThanOrEqualTo(T object)
    {
        return new GreaterThanOrEqualPredicate<T>(object);
    }

    public static <T, V extends Comparable<? super V>> Predicates<T> attributeGreaterThanOrEqualTo(
            Function<? super T, ? extends V> function,
            V object)
    {
        return new AttributePredicate<T, V>(function, new GreaterThanOrEqualPredicate<V>(object));
    }

    public static <T, V> Predicates<T> attributePredicate(
            Function<? super T, ? extends V> function,
            Predicate<? super V> predicate)
    {
        return new AttributePredicate<T, V>(function, predicate);
    }

    public static <T> Predicates<T> attributeEqual(
            Function<? super T, ?> function,
            Object object)
    {
        return new AttributePredicate<T, Object>(function, Predicates.equal(object));
    }

    public static <T> Predicates<Iterable<T>> anySatisfy(Predicate<? super T> predicate)
    {
        return new AnySatisfy<T>(predicate);
    }

    public static <T> Predicates<Iterable<T>> allSatisfy(Predicate<? super T> predicate)
    {
        return new AllSatisfy<T>(predicate);
    }

    public static <T> Predicates<Iterable<T>> noneSatisfy(Predicate<? super T> predicate)
    {
        return new AllSatisfy<T>(Predicates.not(predicate));
    }

    public static <T, V> Predicates<T> attributeAnySatisfy(
            Function<? super T, ? extends Iterable<V>> function,
            Predicate<? super V> predicate)
    {
        return Predicates.attributePredicate(function, Predicates.<V>anySatisfy(predicate));
    }

    public static <T, V> Predicates<T> attributeAllSatisfy(
            Function<? super T, ? extends Iterable<V>> function,
            Predicate<? super V> predicate)
    {
        return Predicates.attributePredicate(function, Predicates.<V>allSatisfy(predicate));
    }

    public static Predicates<Object> notEqual(Object object)
    {
        if (object == null)
        {
            return Predicates.notNull();
        }
        return new NotEqualPredicate(object);
    }

    public static <T> Predicates<T> ifTrue(Function<? super T, Boolean> function)
    {
        return new AttributeTrue<T>(function);
    }

    public static <T> Predicates<T> ifFalse(Function<? super T, Boolean> function)
    {
        return new AttributeFalse<T>(function);
    }

    public static <T> Predicates<T> attributeNotEqual(
            Function<? super T, ?> function,
            Object object)
    {
        return new AttributePredicate<T, Object>(function, Predicates.notEqual(object));
    }

    public static Predicates<Object> isNull()
    {
        return IS_NULL;
    }

    public static <T> Predicates<T> attributeIsNull(Function<? super T, ?> function)
    {
        return new AttributePredicate<T, Object>(function, Predicates.isNull());
    }

    public static Predicates<Object> notNull()
    {
        return NOT_NULL;
    }

    public static <T> Predicates<T> attributeNotNull(Function<? super T, ?> function)
    {
        return new AttributePredicate<T, Object>(function, Predicates.notNull());
    }

    public static Predicates<Object> sameAs(Object object)
    {
        return new IdentityPredicate(object);
    }

    public static Predicates<Object> notSameAs(Object object)
    {
        return new NotIdentityPredicate(object);
    }

    public static Predicates<Object> instanceOf(Class<?> clazz)
    {
        return new InstanceOfPredicate(clazz);
    }

    public static Predicates<Object> assignableFrom(Class<?> clazz)
    {
        return new AssignableFromPredicate(clazz);
    }

    public static Predicates<Object> notInstanceOf(Class<?> clazz)
    {
        return new NotInstanceOfPredicate(clazz);
    }

    public static Predicates<Object> alwaysTrue()
    {
        return ALWAYS_TRUE;
    }

    public static Predicates<Object> alwaysFalse()
    {
        return ALWAYS_FALSE;
    }

    public Predicates<T> and(Predicate<? super T> op)
    {
        return Predicates.<T>and(this, op);
    }

    public Predicates<T> or(Predicate<? super T> op)
    {
        return Predicates.<T>or(this, op);
    }

    private static final class PredicateAdapter<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private PredicateAdapter(Predicate<T> newPredicate)
        {
            this.predicate = newPredicate;
        }

        public boolean accept(T o)
        {
            return this.predicate.accept(o);
        }

        @Override
        public String toString()
        {
            return "Predicates.adapt(" + this.predicate + ')';
        }
    }

    protected static class AttributePredicate<T, V> extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final Function<? super T, ? extends V> function;
        protected final Predicate<? super V> predicate;

        protected AttributePredicate(
                Function<? super T, ? extends V> newFunction,
                Predicate<? super V> newPredicate)
        {
            this.function = newFunction;
            this.predicate = newPredicate;
        }

        public boolean accept(T anObject)
        {
            return this.predicate.accept(this.function.valueOf(anObject));
        }

        @Override
        public String toString()
        {
            return "Predicates.attributePredicate("
                    + this.function
                    + ", "
                    + this.predicate
                    + ')';
        }
    }

    private static class FalseEquals implements Predicate<Boolean>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Boolean anObject)
        {
            return Boolean.FALSE.equals(anObject);
        }
    }

    private static class TrueEquals implements Predicate<Boolean>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Boolean anObject)
        {
            return Boolean.TRUE.equals(anObject);
        }
    }

    private static final class AttributeFalse<T> extends AttributePredicate<T, Boolean>
    {
        private static final long serialVersionUID = 1L;
        private static final FalseEquals FALSE_EQUALS = new FalseEquals();

        private AttributeFalse(Function<? super T, Boolean> newFunction)
        {
            super(newFunction, FALSE_EQUALS);
        }

        @Override
        public String toString()
        {
            return "Predicates.ifFalse(" + this.function + ')';
        }
    }

    private static final class AttributeTrue<T> extends AttributePredicate<T, Boolean>
    {
        private static final long serialVersionUID = 1L;
        private static final TrueEquals TRUE_EQUALS = new TrueEquals();

        private AttributeTrue(Function<? super T, Boolean> newFunction)
        {
            super(newFunction, TRUE_EQUALS);
        }

        @Override
        public String toString()
        {
            return "Predicates.ifTrue(" + this.function + ')';
        }
    }

    public static class AnySatisfy<T> extends Predicates<Iterable<T>>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> predicate;

        public AnySatisfy(Predicate<? super T> predicate)
        {
            this.predicate = predicate;
        }

        public boolean accept(Iterable<T> iterable)
        {
            return Iterate.anySatisfy(iterable, this.predicate);
        }
    }

    public static class AllSatisfy<T> extends Predicates<Iterable<T>>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> predicate;

        public AllSatisfy(Predicate<? super T> predicate)
        {
            this.predicate = predicate;
        }

        public boolean accept(Iterable<T> iterable)
        {
            return Iterate.allSatisfy(iterable, this.predicate);
        }
    }

    private abstract static class CompareToPredicate<T extends Comparable<? super T>>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T compareTo;

        private CompareToPredicate(T newCompareTo)
        {
            this.compareTo = newCompareTo;
        }
    }

    protected static class LessThanPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected LessThanPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) < 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.lessThan(" + this.compareTo + ')';
        }
    }

    protected abstract static class RangePredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;
        protected final T compareFrom;

        protected RangePredicate(T newCompareFrom, T newCompareTo)
        {
            super(newCompareTo);
            this.compareFrom = newCompareFrom;
        }
    }

    private static final class BetweenInclusive<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusive(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) >= 0 && o.compareTo(this.compareTo) <= 0;
        }
    }

    private static final class BetweenInclusiveTo<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusiveTo(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) > 0 && o.compareTo(this.compareTo) <= 0;
        }
    }

    private static final class BetweenInclusiveFrom<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenInclusiveFrom(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) >= 0 && o.compareTo(this.compareTo) < 0;
        }
    }

    private static final class BetweenExclusive<T extends Comparable<? super T>>
            extends RangePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private BetweenExclusive(T newCompareFrom, T newCompareTo)
        {
            super(newCompareFrom, newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareFrom) > 0 && o.compareTo(this.compareTo) < 0;
        }
    }

    protected static class LessThanOrEqualPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected LessThanOrEqualPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) <= 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.lessThanOrEqualTo(" + this.compareTo + ')';
        }
    }

    protected static class GreaterThanPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected GreaterThanPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) > 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.greaterThan(" + this.compareTo + ')';
        }
    }

    protected static class GreaterThanOrEqualPredicate<T extends Comparable<? super T>>
            extends CompareToPredicate<T>
    {
        private static final long serialVersionUID = 1L;

        protected GreaterThanOrEqualPredicate(T newCompareTo)
        {
            super(newCompareTo);
        }

        public boolean accept(T o)
        {
            return o.compareTo(this.compareTo) >= 0;
        }

        @Override
        public String toString()
        {
            return "Predicates.greaterThanOrEqualTo(" + this.compareTo + ')';
        }
    }

    private static final class AndIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private AndIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "and";
        }

        public boolean accept(final T anObject)
        {
            Predicate<Predicate<? super T>> predicate = new Predicate<Predicate<? super T>>()
            {
                public boolean accept(Predicate<? super T> aPredicate)
                {
                    return aPredicate.accept(anObject);
                }
            };
            return Iterate.allSatisfy(this.predicates, predicate);
        }
    }

    private static final class OrIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private OrIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "or";
        }

        public boolean accept(final T anObject)
        {
            Predicate<Predicate<? super T>> predicate = new Predicate<Predicate<? super T>>()
            {
                public boolean accept(Predicate<? super T> aPredicate)
                {
                    return aPredicate.accept(anObject);
                }
            };
            return Iterate.anySatisfy(this.predicates, predicate);
        }
    }

    private static final class NoneOfIterablePredicate<T>
            extends AbstractIterablePredicate<T>
    {
        private static final long serialVersionUID = 1L;

        private NoneOfIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            super(predicates);
        }

        @Override
        protected String getTypeName()
        {
            return "noneOf";
        }

        public boolean accept(final T anObject)
        {
            Predicate<Predicate<? super T>> predicate = new Predicate<Predicate<? super T>>()
            {
                public boolean accept(Predicate<? super T> aPredicate)
                {
                    return !aPredicate.accept(anObject);
                }
            };
            return Iterate.allSatisfy(this.predicates, predicate);
        }
    }

    private abstract static class AbstractIterablePredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        protected final Iterable<? extends Predicate<? super T>> predicates;

        private AbstractIterablePredicate(Iterable<? extends Predicate<? super T>> predicates)
        {
            this.predicates = predicates;
        }

        protected abstract String getTypeName();

        @Override
        public String toString()
        {
            return "Predicates." + this.getTypeName() + '(' + this.predicates + ')';
        }
    }

    private static final class AndPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private AndPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        public boolean accept(T anObject)
        {
            return this.left.accept(anObject) && this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return this.left + ".and(" + this.right + ')';
        }
    }

    private static final class NeitherPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private NeitherPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        public boolean accept(T anObject)
        {
            return !this.left.accept(anObject) && !this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.neither(" + this.left + ", " + this.right + ')';
        }
    }

    private static final class OrPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<? super T> left;
        private final Predicate<? super T> right;

        private OrPredicate(Predicate<? super T> one, Predicate<? super T> two)
        {
            this.left = one;
            this.right = two;
        }

        public boolean accept(T anObject)
        {
            return this.left.accept(anObject) || this.right.accept(anObject);
        }

        @Override
        public String toString()
        {
            return this.left + ".or(" + this.right + ')';
        }
    }

    private static final class NotPredicate<T>
            extends Predicates<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private NotPredicate(Predicate<T> newPredicate)
        {
            this.predicate = newPredicate;
        }

        public boolean accept(T anObject)
        {
            return !this.predicate.accept(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.not(" + this.predicate + ')';
        }
    }

    private static final class EqualPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object compareObject;

        private EqualPredicate(Object newCompareObject)
        {
            this.compareObject = newCompareObject;
        }

        public boolean accept(Object anObject)
        {
            return this.compareObject.equals(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.equal(" + this.compareObject + ')';
        }
    }

    private static final class InPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Iterable<?> iterable;

        private InPredicate(Iterable<?> iterable)
        {
            this.iterable = iterable;
        }

        public boolean accept(Object anObject)
        {
            return Iterate.contains(this.iterable, anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.in(" + this.iterable + ')';
        }
    }

    private static final class NotInPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Iterable<?> iterable;

        private NotInPredicate(Iterable<?> iterable)
        {
            this.iterable = iterable;
        }

        public boolean accept(Object anObject)
        {
            return !Iterate.contains(this.iterable, anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notIn(" + this.iterable + ')';
        }
    }

    private static final class NotEqualPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object compareObject;

        private NotEqualPredicate(Object newCompareObject)
        {
            this.compareObject = newCompareObject;
        }

        public boolean accept(Object anObject)
        {
            return !this.compareObject.equals(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notEqual(" + this.compareObject + ')';
        }
    }

    private static final class IsNull
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Object anObject)
        {
            return anObject == null;
        }

        @Override
        public String toString()
        {
            return "Predicates.isNull()";
        }
    }

    private static final class NotNull
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Object anObject)
        {
            return anObject != null;
        }

        @Override
        public String toString()
        {
            return "Predicates.notNull()";
        }
    }

    private static final class AssignableFromPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private AssignableFromPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        public boolean accept(Object anObject)
        {
            return this.clazz.isAssignableFrom(anObject.getClass());
        }

        @Override
        public String toString()
        {
            return "Predicates.assignableFrom(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class InstanceOfPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private InstanceOfPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        public boolean accept(Object anObject)
        {
            return this.clazz.isInstance(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.instanceOf(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class NotInstanceOfPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Class<?> clazz;

        private NotInstanceOfPredicate(Class<?> newClass)
        {
            this.clazz = newClass;
        }

        public boolean accept(Object anObject)
        {
            return !this.clazz.isInstance(anObject);
        }

        @Override
        public String toString()
        {
            return "Predicates.notInstanceOf(" + this.clazz.getName() + ".class)";
        }
    }

    private static final class AlwaysTrue
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Object anObject)
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Predicates.alwaysTrue()";
        }
    }

    private static final class AlwaysFalse
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;

        public boolean accept(Object anObject)
        {
            return false;
        }

        @Override
        public String toString()
        {
            return "Predicates.alwaysFalse()";
        }
    }

    private static final class IdentityPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object twin;

        private IdentityPredicate(Object object)
        {
            this.twin = object;
        }

        public boolean accept(Object anObject)
        {
            return this.twin == anObject;
        }

        @Override
        public String toString()
        {
            return "Predicates.sameAs(" + this.twin + ')';
        }
    }

    private static final class NotIdentityPredicate
            extends Predicates<Object>
    {
        private static final long serialVersionUID = 1L;
        private final Object twin;

        private NotIdentityPredicate(Object object)
        {
            this.twin = object;
        }

        public boolean accept(Object anObject)
        {
            return this.twin != anObject;
        }

        @Override
        public String toString()
        {
            return "Predicates.notSameAs(" + this.twin + ')';
        }
    }

    private static final class SynchronizedPredicate<T> implements Predicate<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;

        private SynchronizedPredicate(Predicate<T> predicate)
        {
            this.predicate = predicate;
        }

        public boolean accept(T each)
        {
            synchronized (each)
            {
                return this.predicate.accept(each);
            }
        }
    }
}
