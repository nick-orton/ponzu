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

package ponzu.impl.bag.immutable;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import ponzu.api.bag.Bag;
import ponzu.api.bag.ImmutableBag;
import ponzu.api.bag.MutableBag;
import ponzu.api.block.function.Function;
import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.block.procedure.Procedure;
import ponzu.api.block.procedure.Procedure2;
import ponzu.api.list.MutableList;
import ponzu.api.multimap.bag.ImmutableBagMultimap;
import ponzu.impl.block.factory.Comparators;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.block.factory.Predicates;
import ponzu.impl.block.function.AddFunction;
import ponzu.impl.block.function.Constant;
import ponzu.impl.factory.Bags;
import ponzu.impl.factory.Lists;
import ponzu.impl.factory.Maps;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.map.mutable.UnifiedMap;
import ponzu.impl.multimap.bag.HashBagMultimap;
import ponzu.impl.set.mutable.UnifiedSet;
import ponzu.impl.test.Verify;

public class ImmutableSingletonBagTest extends ImmutableBagTestCase
{
    private static final String VAL = "1";
    private static final String NOT_VAL = "2";

    @Override
    protected ImmutableBag<String> newBag()
    {
        return new ImmutableSingletonBag<String>(VAL);
    }

    private ImmutableBag<String> newBagWithNull()
    {
        return new ImmutableSingletonBag<String>(null);
    }

    @Override
    protected int numKeys()
    {
        return 1;
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        ImmutableSingletonBag<Integer> immutable = new ImmutableSingletonBag<Integer>(1);
        Bag<Integer> mutable = Bags.mutable.of(1);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Verify.assertNotEquals(immutable, FastList.newList(mutable));
        Verify.assertNotEquals(immutable, Bags.mutable.of(1, 1));
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(1, 1), immutable.toMapOfItemToCount());
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();
        Assert.assertTrue(this.newBag().allSatisfy(Predicates.alwaysTrue()));
        Assert.assertFalse(this.newBag().allSatisfy(Predicates.alwaysFalse()));
    }

    @Override
    @Test
    public void injectInto()
    {
        super.injectInto();
        Assert.assertEquals(1, new ImmutableSingletonBag<Integer>(1).foldLeft(0, AddFunction.INTEGER).intValue());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        Assert.assertEquals(FastList.newListWith(VAL), this.newBag().toList());
    }

    @Override
    @Test
    public void toSortedList()
    {
        super.toSortedList();

        Assert.assertEquals(FastList.newListWith(VAL), this.newBag().toSortedList());
    }

    @Test
    public void toSortedListWithComparator()
    {
        Assert.assertEquals(FastList.newListWith(VAL), this.newBag().toSortedList(null));
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();

        Assert.assertEquals(UnifiedSet.newSetWith(VAL), this.newBag().toSet());
    }

    @Override
    @Test
    public void toBag()
    {
        super.toBag();

        Assert.assertEquals(Bags.mutable.of(VAL), this.newBag().toBag());
    }

    @Override
    @Test
    public void toMap()
    {
        super.toMap();

        Assert.assertEquals(
                Maps.fixedSize.of(String.class, VAL),
                this.newBag().toMap(Functions.getToClass(), Functions.getToString()));
    }

    @Test
    public void toArrayGivenArray()
    {
        Assert.assertArrayEquals(new String[]{VAL}, this.newBag().toArray(new String[1]));
        Assert.assertArrayEquals(new String[]{VAL}, this.newBag().toArray(new String[0]));
        Assert.assertArrayEquals(new String[]{VAL, null}, this.newBag().toArray(new String[2]));
    }

    @Test
    @Override
    public void min_null_throws()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().min(Comparators.naturalOrder());
    }

    @Test
    @Override
    public void max_null_throws()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().max(Comparators.naturalOrder());
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().max();
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Collections with one element should not throw to emulate the JDK Collections behavior
        this.newBagWithNull().min();
    }

    @Override
    @Test
    public void testNewWith()
    {
        super.testNewWith();
        Assert.assertEquals(Bags.immutable.of(VAL, NOT_VAL), this.newBag().newWith(NOT_VAL));
    }

    @Override
    @Test
    public void testNewWithout()
    {
        super.testNewWithout();
        Assert.assertEquals(Bags.immutable.of(VAL), this.newBag().newWithout(NOT_VAL));
        Assert.assertEquals(Bags.immutable.of(), this.newBag().newWithout(VAL));
    }

    @Override
    @Test
    public void testNewWithAll()
    {
        super.testNewWithAll();
        Assert.assertEquals(Bags.immutable.of(VAL, NOT_VAL, "c"), this.newBag().newWithAll(FastList.newListWith(NOT_VAL, "c")));
    }

    @Override
    @Test
    public void testNewWithoutAll()
    {
        super.testNewWithoutAll();
        Assert.assertEquals(Bags.immutable.of(VAL), this.newBag().newWithoutAll(FastList.newListWith(NOT_VAL)));
        Assert.assertEquals(Bags.immutable.of(), this.newBag().newWithoutAll(FastList.newListWith(VAL, NOT_VAL)));
        Assert.assertEquals(Bags.immutable.of(), this.newBag().newWithoutAll(FastList.newListWith(VAL)));
    }

    @Override
    @Test
    public void testSize()
    {
        Verify.assertIterableSize(1, this.newBag());
    }

    @Override
    @Test
    public void testIsEmpty()
    {
        super.testIsEmpty();
        Assert.assertFalse(this.newBag().isEmpty());
    }

    @Test
    public void testNotEmpty()
    {
        Assert.assertTrue(this.newBag().notEmpty());
    }

    @Override
    @Test
    public void testGetFirst()
    {
        super.testGetFirst();
        Assert.assertEquals(VAL, this.newBag().getFirst());
    }

    @Override
    @Test
    public void testGetLast()
    {
        super.testGetLast();
        Assert.assertEquals(VAL, this.newBag().getLast());
    }

    @Override
    @Test
    public void testContains()
    {
        super.testContains();
        Assert.assertTrue(this.newBag().contains(VAL));
        Assert.assertFalse(this.newBag().contains(NOT_VAL));
    }

    @Override
    @Test
    public void testContainsAllIterable()
    {
        super.testContainsAllIterable();
        Assert.assertTrue(this.newBag().containsAllIterable(FastList.newListWith()));
        Assert.assertTrue(this.newBag().containsAllIterable(FastList.newListWith(VAL)));
        Assert.assertFalse(this.newBag().containsAllIterable(FastList.newListWith(NOT_VAL)));
        Assert.assertFalse(this.newBag().containsAllIterable(FastList.newListWith(42)));
        Assert.assertFalse(this.newBag().containsAllIterable(FastList.newListWith(VAL, NOT_VAL)));
    }

    @Test
    public void testContainsAllArguments()
    {
        Assert.assertTrue(this.newBag().containsAllArguments());
        Assert.assertTrue(this.newBag().containsAllArguments(VAL));
        Assert.assertFalse(this.newBag().containsAllArguments(NOT_VAL));
        Assert.assertFalse(this.newBag().containsAllArguments(42));
        Assert.assertFalse(this.newBag().containsAllArguments(VAL, NOT_VAL));
    }

    @Override
    @Test
    public void testSelectWithTarget()
    {
        super.testSelectWithTarget();
        MutableList<String> target = Lists.mutable.of();
        this.newBag().filter(Predicates.alwaysFalse(), target);
        Verify.assertEmpty(target);
        this.newBag().filter(Predicates.alwaysTrue(), target);
        Verify.assertContains(VAL, target);
    }

    @Override
    @Test
    public void testRejectWithTarget()
    {
        super.testRejectWithTarget();
        MutableList<String> target = Lists.mutable.of();
        this.newBag().filterNot(Predicates.alwaysTrue(), target);
        Verify.assertEmpty(target);
        this.newBag().filterNot(Predicates.alwaysFalse(), target);
        Verify.assertContains(VAL, target);
    }

    @Override
    @Test
    public void testCollect()
    {
        super.testCollect();
        Assert.assertEquals(Bags.immutable.of(VAL), this.newBag().transform(Functions.getToString()));
    }

    @Override
    @Test
    public void testCollectWithTarget()
    {
        super.testCollectWithTarget();
        MutableList<Class<?>> target = Lists.mutable.of();
        this.newBag().transform(Functions.getToClass(), target);
        Verify.assertContains(String.class, target);
    }

    @Override
    @Test
    public void testCollectIf()
    {
        super.testCollectIf();
        Assert.assertEquals(Bags.immutable.of(String.class), this.newBag().transformIf(Predicates.alwaysTrue(), Functions.getToClass()));
        Assert.assertEquals(Bags.immutable.of(), this.newBag().transformIf(Predicates.alwaysFalse(), Functions.getToClass()));
    }

    @Override
    @Test
    public void testCollectIfWithTarget()
    {
        super.testCollectIfWithTarget();
        MutableList<Class<?>> target = Lists.mutable.of();
        this.newBag().transformIf(Predicates.alwaysFalse(), Functions.getToClass(), target);
        Verify.assertEmpty(target);
        this.newBag().transformIf(Predicates.alwaysTrue(), Functions.getToClass(), target);
        Verify.assertContains(String.class, target);
    }

    @Override
    @Test
    public void flatCollect()
    {
        super.flatCollect();
        ImmutableBag<Integer> result = this.newBag().flatTransform(new Function<String, Iterable<Integer>>()
        {
            public Iterable<Integer> valueOf(String object)
            {
                return Bags.mutable.of(1, 2, 3, 4, 5);
            }
        });
        Assert.assertEquals(Bags.immutable.of(1, 2, 3, 4, 5), result);
    }

    @Override
    @Test
    public void flatCollectWithTarget()
    {
        super.flatCollectWithTarget();
        MutableBag<Integer> target = Bags.mutable.of();
        MutableBag<Integer> result = this.newBag().flatTransform(new Function<String, Iterable<Integer>>()
        {
            public Iterable<Integer> valueOf(String object)
            {
                return Bags.mutable.of(1, 2, 3, 4, 5);
            }
        }, target);
        Assert.assertEquals(Bags.mutable.of(1, 2, 3, 4, 5), result);
    }

    @Override
    @Test
    public void testDetect()
    {
        super.testDetect();
        Assert.assertEquals(VAL, this.newBag().find(Predicates.alwaysTrue()));
        Assert.assertNull(this.newBag().find(Predicates.alwaysFalse()));
    }

    @Test
    public void testDetectIfNone()
    {
        Assert.assertEquals(VAL, this.newBag().findIfNone(Predicates.alwaysTrue(), new Constant<String>(NOT_VAL)));

        Assert.assertEquals(NOT_VAL, this.newBag().findIfNone(Predicates.alwaysFalse(), new Constant<String>(NOT_VAL)));
    }

    @Override
    @Test
    public void testCount()
    {
        super.testCount();
        Assert.assertEquals(1, this.newBag().count(Predicates.alwaysTrue()));
        Assert.assertEquals(0, this.newBag().count(Predicates.alwaysFalse()));
    }

    @Override
    @Test
    public void testAnySatisfy()
    {
        super.testAnySatisfy();
        Assert.assertTrue(this.newBag().anySatisfy(Predicates.alwaysTrue()));
        Assert.assertFalse(this.newBag().anySatisfy(Predicates.alwaysFalse()));
    }

    @Test
    public void testGroupBy()
    {
        ImmutableBagMultimap<Class<?>, String> result = this.newBag().groupBy(Functions.getToClass());
        Assert.assertEquals(VAL, result.get(String.class).getFirst());
    }

    @Test
    public void testGroupByWithTarget()
    {
        HashBagMultimap<Class<?>, String> target = HashBagMultimap.newMultimap();
        this.newBag().groupBy(Functions.getToClass(), target);
        Assert.assertEquals(VAL, target.get(String.class).getFirst());
    }

    @Test
    public void testOccurrencesOf()
    {
        Assert.assertEquals(1, this.newBag().occurrencesOf(VAL));
        Assert.assertEquals(0, this.newBag().occurrencesOf(NOT_VAL));
    }

    @Test
    public void testForEachWithOccurrences()
    {
        final Object[] results = new Object[2];
        this.newBag().forEachWithOccurrences(new ObjectIntProcedure<String>()
        {
            public void value(String each, int index)
            {
                results[0] = each;
                results[1] = index;
            }
        });
        Assert.assertEquals(VAL, results[0]);
        Assert.assertEquals(1, results[1]);
    }

    @Override
    @Test
    public void toMapOfItemToCount()
    {
        super.toMapOfItemToCount();

        Assert.assertEquals(Maps.fixedSize.of(VAL, 1), this.newBag().toMapOfItemToCount());
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();

        ImmutableBag<String> immutableBag = this.newBag();
        Assert.assertSame(immutableBag, immutableBag.toImmutable());
    }

    @Override
    @Test
    public void testForEach()
    {
        super.testForEach();
        final Object[] results = new Object[1];
        this.newBag().forEach(new Procedure<String>()
        {
            public void value(String each)
            {
                results[0] = each;
            }
        });
        Assert.assertEquals(VAL, results[0]);
    }

    @Override
    @Test
    public void testForEachWithIndex()
    {
        super.testForEachWithIndex();
        final Object[] results = new Object[2];
        this.newBag().forEachWithIndex(new ObjectIntProcedure<String>()
        {
            public void value(String each, int index)
            {
                results[0] = each;
                results[1] = index;
            }
        });
        Assert.assertEquals(VAL, results[0]);
        Assert.assertEquals(0, results[1]);
    }

    @Override
    @Test
    public void testForEachWith()
    {
        super.testForEachWith();
        final Object[] results = new Object[2];
        this.newBag().forEachWith(new Procedure2<String, Object>()
        {
            public void value(String each, Object index)
            {
                results[0] = each;
                results[1] = index;
            }
        }, "second");
        Assert.assertEquals(VAL, results[0]);
        Assert.assertEquals("second", results[1]);
    }

    @Override
    @Test
    public void testIterator()
    {
        super.testIterator();
        Iterator<String> iterator = this.newBag().iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(VAL, iterator.next());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testSizeDistinct()
    {
        Assert.assertEquals(1, this.newBag().sizeDistinct());
    }
}
