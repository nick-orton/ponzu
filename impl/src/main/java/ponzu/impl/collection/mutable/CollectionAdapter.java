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

package ponzu.impl.collection.mutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import ponzu.api.collection.ImmutableCollection;
import ponzu.api.collection.MutableCollection;
import ponzu.api.list.MutableList;
import ponzu.api.set.MutableSet;
import ponzu.impl.block.factory.Comparators;
import ponzu.impl.block.procedure.CollectionAddProcedure;
import ponzu.impl.block.procedure.CollectionRemoveProcedure;
import ponzu.impl.factory.Lists;
import ponzu.impl.factory.Sets;
import ponzu.impl.list.mutable.ArrayListAdapter;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.list.mutable.ListAdapter;
import ponzu.impl.list.mutable.RandomAccessListAdapter;
import ponzu.impl.set.mutable.SetAdapter;
import ponzu.impl.set.mutable.UnifiedSet;
import ponzu.impl.utility.ArrayIterate;
import ponzu.impl.utility.Iterate;

/**
 * This class provides a MutableCollection interface wrapper around a JDK Collections Collection interface instance.  All
 * of the MutableCollection interface methods are supported in addition to the JDK Collection interface methods.
 * <p/>
 * To create a new instance that wraps a collection with the MutableSet interface, use the {@link #wrapSet(Iterable)}
 * factory method.  To create a new instance that wraps a collection with the MutableList interface, use the
 * {@link #wrapList(Iterable)} factory method.  To wrap a collection with the MutableCollection interface alone, use
 * the {@link #adapt(Collection)} factory method.
 */
public final class CollectionAdapter<T>
        extends AbstractCollectionAdapter<T>
        implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Collection<T> delegate;

    public CollectionAdapter(Collection<T> newDelegate)
    {
        this.delegate = newDelegate;
    }

    @Override
    protected Collection<T> getDelegate()
    {
        return this.delegate;
    }

    public MutableCollection<T> asUnmodifiable()
    {
        return UnmodifiableMutableCollection.of(this);
    }

    public MutableCollection<T> asSynchronized()
    {
        return SynchronizedMutableCollection.of(this);
    }

    public ImmutableCollection<T> toImmutable()
    {
        return this.delegate instanceof Set
                ? Sets.immutable.ofAll(this.delegate)
                : Lists.immutable.ofAll(this.delegate);
    }

    public static <E> MutableSet<E> wrapSet(Iterable<E> iterable)
    {
        if (iterable instanceof MutableSet)
        {
            return (MutableSet<E>) iterable;
        }
        if (iterable instanceof Set)
        {
            return SetAdapter.adapt((Set<E>) iterable);
        }
        return UnifiedSet.newSet(iterable);
    }

    public static <E> MutableList<E> wrapList(Iterable<E> iterable)
    {
        if (iterable instanceof MutableList)
        {
            return (MutableList<E>) iterable;
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListAdapter.adapt((ArrayList<E>) iterable);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListAdapter.adapt((List<E>) iterable);
        }
        if (iterable instanceof List)
        {
            return ListAdapter.adapt((List<E>) iterable);
        }
        return FastList.newList(iterable);
    }

    public static <E> MutableCollection<E> adapt(Collection<E> collection)
    {
        if (collection instanceof MutableCollection)
        {
            return (MutableCollection<E>) collection;
        }
        if (collection instanceof List)
        {
            return CollectionAdapter.wrapList(collection);
        }
        if (collection instanceof Set)
        {
            return SetAdapter.adapt((Set<E>) collection);
        }
        return new CollectionAdapter<E>(collection);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        CollectionAdapter<?> that = (CollectionAdapter<?>) o;
        return Comparators.nullSafeEquals(this.delegate, that.delegate);
    }

    @Override
    public int hashCode()
    {
        return this.delegate.hashCode();
    }

    public CollectionAdapter<T> with(T... elements)
    {
        ArrayIterate.forEach(elements, new CollectionAddProcedure<T>(this.delegate));
        return this;
    }

    public CollectionAdapter<T> with(T element)
    {
        this.delegate.add(element);
        return this;
    }

    public CollectionAdapter<T> without(T element)
    {
        this.delegate.remove(element);
        return this;
    }

    public CollectionAdapter<T> withAll(Iterable<? extends T> elements)
    {
        Iterate.forEach(elements, new CollectionAddProcedure<T>(this.delegate));
        return this;
    }

    public CollectionAdapter<T> withoutAll(Iterable<? extends T> elements)
    {
        Iterate.forEach(elements, new CollectionRemoveProcedure<T>(this.delegate));
        return this;
    }

    /**
     * @deprecated use {@link FastList#newList()} or {@link UnifiedSet#newSet()} instead
     */
    @Deprecated
    public MutableCollection<T> newEmpty()
    {
        if (this.delegate instanceof Set)
        {
            return UnifiedSet.newSet();
        }
        return Lists.mutable.of();
    }
}
