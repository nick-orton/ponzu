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

package ponzu.impl.set.immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import ponzu.api.block.function.Function;
import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.block.procedure.Procedure;
import ponzu.api.block.procedure.Procedure2;
import ponzu.api.set.ImmutableSet;
import ponzu.api.tuple.Pair;
import ponzu.impl.EmptyIterator;
import ponzu.impl.factory.Sets;
import net.jcip.annotations.Immutable;

/**
 * This is a zero element {@link ImmutableSet} which is created by calling the Sets.immutable.of() method.
 */
@Immutable
final class ImmutableEmptySet<T>
        extends AbstractImmutableSet<T>
        implements Serializable
{
    static final ImmutableSet<?> INSTANCE = new ImmutableEmptySet();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        return other instanceof Set && ((Set<?>) other).isEmpty();
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    private Object readResolve()
    {
        return INSTANCE;
    }

    @Override
    public ImmutableSet<T> newWith(T element)
    {
        return Sets.immutable.of(element);
    }

    @Override
    public ImmutableSet<T> newWithAll(Iterable<? extends T> elements)
    {
        return Sets.immutable.ofAll(elements);
    }

    @Override
    public ImmutableSet<T> newWithout(T element)
    {
        return this;
    }

    @Override
    public ImmutableSet<T> newWithoutAll(Iterable<? extends T> elements)
    {
        return this;
    }

    public int size()
    {
        return 0;
    }

    @Override
    public boolean contains(Object obj)
    {
        return false;
    }

    public void forEach(Procedure<? super T> procedure)
    {
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
    }

    @Override
    public T getFirst()
    {
        return null;
    }

    @Override
    public T getLast()
    {
        return null;
    }

    @Override
    public Iterator<T> iterator()
    {
        return EmptyIterator.getInstance();
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        throw new NoSuchElementException();
    }

    @Override
    public T min()
    {
        throw new NoSuchElementException();
    }

    @Override
    public T max()
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        throw new NoSuchElementException();
    }

    @Override
    public <S> ImmutableSet<Pair<T, S>> zip(Iterable<S> that)
    {
        return Sets.immutable.of();
    }

    @Override
    public <S, R extends Collection<Pair<T, S>>> R zip(Iterable<S> that, R target)
    {
        return target;
    }

    @Override
    public ImmutableSet<Pair<T, Integer>> zipWithIndex()
    {
        return Sets.immutable.of();
    }

    @Override
    public <R extends Collection<Pair<T, Integer>>> R zipWithIndex(R target)
    {
        return target;
    }
}
