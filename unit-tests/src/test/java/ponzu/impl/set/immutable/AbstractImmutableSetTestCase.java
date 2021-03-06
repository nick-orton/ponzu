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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;
import ponzu.api.LazyIterable;
import ponzu.api.block.function.Function;
import ponzu.api.block.function.Generator;
import ponzu.api.block.procedure.ObjectIntProcedure;
import ponzu.api.block.procedure.Procedure2;
import ponzu.api.list.MutableList;
import ponzu.api.multimap.set.ImmutableSetMultimap;
import ponzu.api.partition.set.PartitionImmutableSet;
import ponzu.api.set.ImmutableSet;
import ponzu.api.set.MutableSet;
import ponzu.api.set.UnsortedSetIterable;
import ponzu.api.set.sorted.MutableSortedSet;
import ponzu.api.tuple.Pair;
import ponzu.impl.block.factory.Comparators;
import ponzu.impl.block.factory.Functions;
import ponzu.impl.block.factory.Predicates;
import ponzu.impl.block.function.AddFunction;
import ponzu.impl.block.function.Constant;
import ponzu.impl.block.function.NegativeIntervalFunction;
import ponzu.impl.block.procedure.CollectionAddProcedure;
import ponzu.impl.factory.Lists;
import ponzu.impl.factory.Sets;
import ponzu.impl.list.Interval;
import ponzu.impl.list.mutable.FastList;
import ponzu.impl.multimap.set.UnifiedSetMultimap;
import ponzu.impl.set.mutable.UnifiedSet;
import ponzu.impl.set.sorted.mutable.TreeSortedSet;
import ponzu.impl.test.Verify;

public abstract class AbstractImmutableSetTestCase
{
    protected abstract ImmutableSet<Integer> classUnderTest();

    @Test
    public void testEqualsAndHashCode()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        MutableSet<Integer> mutable = UnifiedSet.newSet(immutable);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Verify.assertPostSerializedEqualsAndHashCode(immutable);
        Verify.assertNotEquals(immutable, FastList.newList(mutable));
    }

    @Test
    public void testNewWith()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        Assert.assertSame(immutable, immutable.newWith(immutable.size()));
        Verify.assertSize(immutable.size() + 1, immutable.newWith(immutable.size() + 1).castToSet());
    }

    @Test
    public void testNewWithout()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        Verify.assertSize(Math.max(0, immutable.size() - 1), immutable.newWithout(immutable.size()).castToSet());
        Verify.assertSize(immutable.size(), immutable.newWithout(immutable.size() + 1).castToSet());
    }

    @Test
    public void testNewWithAll()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        ImmutableSet<Integer> withAll = set.newWithAll(UnifiedSet.newSetWith(0));
        Verify.assertNotEquals(set, withAll);
        Assert.assertEquals(UnifiedSet.newSet(set).with(0), withAll);
    }

    @Test
    public void testNewWithoutAll()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        ImmutableSet<Integer> withoutAll = set.newWithoutAll(UnifiedSet.newSet(this.classUnderTest()));
        Assert.assertEquals(Sets.immutable.<Integer>of(), withoutAll);
        ImmutableSet<Integer> largeWithoutAll = set.newWithoutAll(Interval.fromTo(101, 150));
        Assert.assertEquals(set, largeWithoutAll);
        ImmutableSet<Integer> largeWithoutAll2 = set.newWithoutAll(UnifiedSet.newSet(Interval.fromTo(151, 199)));
        Assert.assertEquals(set, largeWithoutAll2);
        ImmutableSet<Integer> largeWithoutAll3 = set.newWithoutAll(FastList.newList(Interval.fromTo(151, 199)));
        Assert.assertEquals(set, largeWithoutAll3);
    }

    @Test
    public void testContains()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        for (int i = 1; i <= set.size(); i++)
        {
            Assert.assertTrue(set.contains(i));
        }
        Assert.assertFalse(set.contains(set.size() + 1));
    }

    @Test
    public void testContainsAllArray()
    {
        Assert.assertTrue(this.classUnderTest().containsAllArguments(this.classUnderTest().toArray()));
    }

    @Test
    public void testContainsAllIterable()
    {
        Assert.assertTrue(this.classUnderTest().containsAllIterable(this.classUnderTest()));
    }

    @Test
    public void testForEach()
    {
        MutableSet<Integer> result = UnifiedSet.newSet();
        ImmutableSet<Integer> collection = this.classUnderTest();
        collection.forEach(CollectionAddProcedure.<Integer>on(result));
        Assert.assertEquals(collection, result);
    }

    @Test
    public void testForEachWith()
    {
        final MutableSet<Integer> result = UnifiedSet.newSet();
        ImmutableSet<Integer> set = this.classUnderTest();
        set.forEachWith(new Procedure2<Integer, Integer>()
        {
            public void value(Integer argument1, Integer argument2)
            {
                result.add(argument1 + argument2);
            }
        }, 0);
        Assert.assertEquals(set, result);
    }

    @Test
    public void testForEachWithIndex()
    {
        final MutableSet<Integer> result = UnifiedSet.newSet();
        ImmutableSet<Integer> integers = this.classUnderTest();
        integers.forEachWithIndex(new ObjectIntProcedure<Integer>()
        {
            public void value(Integer object, int index)
            {
                result.add(object);
            }
        });
        Assert.assertEquals(integers, result);
    }

    @Test
    public void testSelect()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.filter(Predicates.lessThan(integers.size() + 1)));
        Verify.assertIterableEmpty(integers.filter(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void testSelectWithTarget()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.filter(Predicates.lessThan(integers.size() + 1), UnifiedSet.<Integer>newSet()));
        Verify.assertEmpty(integers.filter(Predicates.greaterThan(integers.size()), FastList.<Integer>newList()));
    }

    @Test
    public void testReject()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Verify.assertIterableEmpty(integers.filterNot(Predicates.lessThan(integers.size() + 1)));
        Assert.assertEquals(integers, integers.filterNot(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void testRejectWithTarget()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Verify.assertEmpty(integers.filterNot(Predicates.lessThan(integers.size() + 1), FastList.<Integer>newList()));
        Assert.assertEquals(integers, integers.filterNot(Predicates.greaterThan(integers.size()), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void partition()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        PartitionImmutableSet<Integer> partition = integers.partition(Predicates.lessThan(integers.size() + 1));
        Assert.assertEquals(integers, partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
    }

    @Test
    public void testCollect()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.transform(Functions.getIntegerPassThru()));
    }

    @Test
    public void testCollectWithTarget()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.transform(Functions.getIntegerPassThru(), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void flatCollect()
    {
        ImmutableSet<String> actual = this.classUnderTest().flatTransform(new Function<Integer, MutableList<String>>()
        {
            public MutableList<String> valueOf(Integer integer)
            {
                return Lists.fixedSize.of(String.valueOf(integer));
            }
        });

        ImmutableSet<String> expected = this.classUnderTest().transform(Functions.getToString());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void flatCollectWithTarget()
    {
        MutableSet<String> actual = this.classUnderTest().flatTransform(new Function<Integer, MutableList<String>>()
        {
            public MutableList<String> valueOf(Integer integer)
            {
                return Lists.fixedSize.of(String.valueOf(integer));
            }
        }, UnifiedSet.<String>newSet());

        ImmutableSet<String> expected = this.classUnderTest().transform(Functions.getToString());

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void zip()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableSet.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableSet.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(immutableSet.size() - 1, null);

        ImmutableSet<Pair<Integer, Object>> pairs = immutableSet.zip(nulls);
        Assert.assertEquals(immutableSet, pairs.transform(Functions.<Integer>firstOfPair()));
        Assert.assertEquals(UnifiedSet.<Object>newSet(nulls), pairs.transform(Functions.<Object>secondOfPair()));

        ImmutableSet<Pair<Integer, Object>> pairsPlusOne = immutableSet.zip(nullsPlusOne);
        Assert.assertEquals(immutableSet, pairsPlusOne.transform(Functions.<Integer>firstOfPair()));
        Assert.assertEquals(UnifiedSet.<Object>newSet(nulls), pairsPlusOne.transform(Functions.<Object>secondOfPair()));

        ImmutableSet<Pair<Integer, Object>> pairsMinusOne = immutableSet.zip(nullsMinusOne);
        Assert.assertEquals(immutableSet.size() - 1, pairsMinusOne.size());
        Assert.assertTrue(immutableSet.containsAllIterable(pairsMinusOne.transform(Functions.<Integer>firstOfPair())));

        Assert.assertEquals(immutableSet.zip(nulls), immutableSet.zip(nulls, UnifiedSet.<Pair<Integer, Object>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        ImmutableSet<Pair<Integer, Integer>> pairs = immutableSet.zipWithIndex();

        Assert.assertEquals(immutableSet, pairs.transform(Functions.<Integer>firstOfPair()));
        Assert.assertEquals(
                Interval.zeroTo(immutableSet.size() - 1).toSet(),
                pairs.transform(Functions.<Integer>secondOfPair()));

        Assert.assertEquals(
                immutableSet.zipWithIndex(),
                immutableSet.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws()
    {
        this.classUnderTest().chunk(0);
    }

    @Test
    public void chunk_large_size()
    {
        Assert.assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10).getFirst());
        Verify.assertInstanceOf(ImmutableSet.class, this.classUnderTest().chunk(10).getFirst());
    }

    @Test
    public void testDetect()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.find(Predicates.equal(1)));
        Assert.assertNull(integers.find(Predicates.equal(integers.size() + 1)));
    }

    @Test
    public void testDetectIfNoneWithBlock()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Generator<Integer> function = new Constant<Integer>(integers.size() + 1);
        Assert.assertEquals(Integer.valueOf(1), integers.findIfNone(Predicates.equal(1), function));
        Assert.assertEquals(Integer.valueOf(integers.size() + 1), integers.findIfNone(Predicates.equal(integers.size() + 1), function));
    }

    @Test
    public void testAllSatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertTrue(integers.allSatisfy(Predicates.instanceOf(Integer.class)));
        Assert.assertFalse(integers.allSatisfy(Predicates.equal(0)));
    }

    @Test
    public void testAnySatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertFalse(integers.anySatisfy(Predicates.instanceOf(String.class)));
        Assert.assertTrue(integers.anySatisfy(Predicates.instanceOf(Integer.class)));
    }

    @Test
    public void testCount()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers.size(), integers.count(Predicates.instanceOf(Integer.class)));
        Assert.assertEquals(0, integers.count(Predicates.instanceOf(String.class)));
    }

    @Test
    public void testCollectIf()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.transformIf(Predicates.instanceOf(Integer.class),
                Functions.getIntegerPassThru()));
    }

    @Test
    public void testCollectIfWithTarget()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(integers, integers.transformIf(Predicates.instanceOf(Integer.class),
                Functions.getIntegerPassThru(), UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void testGetFirst()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(1), integers.getFirst());
    }

    @Test
    public void testGetLast()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assert.assertEquals(Integer.valueOf(integers.size()), integers.getLast());
    }

    @Test
    public void testIsEmpty()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.notEmpty());
    }

    @Test
    public void testIterator()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        final Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            Integer integer = iterator.next();
            Assert.assertEquals(i + 1, integer.intValue());
        }
        Verify.assertThrows(NoSuchElementException.class, new Runnable()
        {
            public void run()
            {
                iterator.next();
            }
        });
    }

    @Test
    public void testInjectInto()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Integer result = integers.foldLeft(0, AddFunction.INTEGER);
        Assert.assertEquals(FastList.newList(integers).foldLeft(0, AddFunction.INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void testToArray()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        Assert.assertArrayEquals(integers.toArray(), copy.toArray());
        Assert.assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void testToString()
    {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void testMakeString()
    {
        Assert.assertEquals(FastList.newList(this.classUnderTest()).makeString(), this.classUnderTest().makeString());
    }

    @Test
    public void testAppendString()
    {
        Appendable builder = new StringBuilder();
        this.classUnderTest().appendString(builder);
        Assert.assertEquals(FastList.newList(this.classUnderTest()).makeString(), builder.toString());
    }

    @Test
    public void toList()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> list = integers.toList();
        Verify.assertEqualsAndHashCode(FastList.newList(integers), list);
    }

    @Test
    public void toSortedList()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assert.assertEquals(copy.sortThis(Collections.<Integer>reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        Assert.assertEquals(copy.sortThis(), list2);
    }

    @Test
    public void toSortedListBy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableList<Integer> list = integers.toSortedListBy(Functions.getToString());
        Assert.assertEquals(this.classUnderTest().toList(), list);
    }

    @Test
    public void toSortedSet()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertListsEqual(integers.toSortedList(), set.toList());
    }

    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.<Integer>reverseOrder());
        Assert.assertEquals(integers.toSet(), set);
        Assert.assertEquals(integers.toSortedList(Comparators.<Integer>reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(Functions.getToString());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void testForLoop()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        for (Integer each : set)
        {
            Assert.assertNotNull(each);
        }
    }

    @Test
    public void testIteratorRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().iterator().remove();
            }
        });
    }

    @Test
    public void testAdd()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().add(1);
            }
        });
    }

    @Test
    public void testRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().remove(Integer.valueOf(1));
            }
        });
    }

    @Test
    public void testClear()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().clear();
            }
        });
    }

    @Test
    public void testRemoveAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().removeAll(Lists.fixedSize.of());
            }
        });
    }

    @Test
    public void testRetainAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().retainAll(Lists.fixedSize.of());
            }
        });
    }

    @Test
    public void testAddAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, new Runnable()
        {
            public void run()
            {
                AbstractImmutableSetTestCase.this.classUnderTest().castToSet().addAll(Lists.fixedSize.<Integer>of());
            }
        });
    }

    private ImmutableSet<Integer> classUnderTestWithNull()
    {
        return this.classUnderTest().filterNot(Predicates.equal(1)).newWith(null);
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws()
    {
        this.classUnderTestWithNull().min(Comparators.naturalOrder());
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws()
    {
        this.classUnderTestWithNull().max(Comparators.naturalOrder());
    }

    @Test
    public void min()
    {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().min(Comparators.naturalOrder()));
    }

    @Test
    public void max()
    {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Comparators.naturalOrder())));
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws_without_comparator()
    {
        this.classUnderTestWithNull().min();
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws_without_comparator()
    {
        this.classUnderTestWithNull().max();
    }

    @Test
    public void min_without_comparator()
    {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().min());
    }

    @Test
    public void max_without_comparator()
    {
        Assert.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().max());
    }

    @Test
    public void minBy()
    {
        Assert.assertEquals(Integer.valueOf(1), this.classUnderTest().minBy(Functions.getToString()));
    }

    @Test
    public void maxBy()
    {
        Assert.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(Functions.getToString()));
    }

    @Test
    public void groupBy()
    {
        ImmutableSet<Integer> undertest = this.classUnderTest();
        ImmutableSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.<Integer>getPassThru());
        UnifiedSetMultimap<Integer, Integer> expected = UnifiedSet.newSet(undertest).groupBy(Functions.<Integer>getPassThru());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByEach()
    {
        ImmutableSet<Integer> undertest = this.classUnderTest();
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        ImmutableSetMultimap<Integer, Integer> actual = undertest.groupByEach(function);
        UnifiedSetMultimap<Integer, Integer> expected = UnifiedSet.newSet(undertest).groupByEach(function);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByWithTarget()
    {
        ImmutableSet<Integer> undertest = this.classUnderTest();
        UnifiedSetMultimap<Integer, Integer> actual = undertest.groupBy(Functions.<Integer>getPassThru(), UnifiedSetMultimap.<Integer, Integer>newMultimap());
        UnifiedSetMultimap<Integer, Integer> expected = UnifiedSet.newSet(undertest).groupBy(Functions.<Integer>getPassThru());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void groupByEachWithTarget()
    {
        ImmutableSet<Integer> undertest = this.classUnderTest();
        NegativeIntervalFunction function = new NegativeIntervalFunction();
        UnifiedSetMultimap<Integer, Integer> actual = undertest.groupByEach(function, UnifiedSetMultimap.<Integer, Integer>newMultimap());
        UnifiedSetMultimap<Integer, Integer> expected = UnifiedSet.newSet(undertest).groupByEach(function);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void union()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        ImmutableSet<String> union = set.union(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(set.size() + 3, union);
        Assert.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).transform(Functions.getToString())));
        Verify.assertContainsAll(union, "a", "b", "c");

        Assert.assertEquals(set, set.union(UnifiedSet.newSetWith("1")));
    }

    @Test
    public void unionInto()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        MutableSet<String> union = set.unionInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(set.size() + 3, union);
        Assert.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).transform(Functions.getToString())));
        Verify.assertContainsAll(union, "a", "b", "c");

        Assert.assertEquals(set, set.unionInto(UnifiedSet.newSetWith("1"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void intersect()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        ImmutableSet<String> intersect = set.intersect(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(1, intersect);
        Assert.assertEquals(UnifiedSet.newSetWith("1"), intersect);

        Verify.assertIterableEmpty(set.intersect(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void intersectInto()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        MutableSet<String> intersect = set.intersectInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(1, intersect);
        Assert.assertEquals(UnifiedSet.newSetWith("1"), intersect);

        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void difference()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        ImmutableSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Assert.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assert.assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void differenceInto()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.<String>newSet());
        Assert.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assert.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void symmetricDifference()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        ImmutableSet<String> difference = set.symmetricDifference(UnifiedSet.newSetWith("2", "3", "4", "5", "not present"));
        Verify.assertContains("1", difference);
        Assert.assertTrue(difference.containsAllIterable(Interval.fromTo(set.size() + 1, 5).transform(Functions.getToString())));
        for (int i = 2; i <= set.size(); i++)
        {
            Verify.assertNotContains(String.valueOf(i), difference);
        }

        Verify.assertSize(set.size() + 1, set.symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void symmetricDifferenceInto()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        MutableSet<String> difference = set.symmetricDifferenceInto(
                UnifiedSet.newSetWith("2", "3", "4", "5", "not present"),
                UnifiedSet.<String>newSet());
        Verify.assertContains("1", difference);
        Assert.assertTrue(difference.containsAllIterable(Interval.fromTo(set.size() + 1, 5).transform(Functions.getToString())));
        for (int i = 2; i <= set.size(); i++)
        {
            Verify.assertNotContains(String.valueOf(i), difference);
        }

        Verify.assertSize(
                set.size() + 1,
                set.symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void isSubsetOf()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        Assert.assertTrue(set.isSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
    }

    @Test
    public void isProperSubsetOf()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        Assert.assertTrue(set.isProperSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
        Assert.assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        ImmutableSet<UnsortedSetIterable<String>> powerSet = set.powerSet();
        Verify.assertSize((int) StrictMath.pow(2, set.size()), powerSet);
        Verify.assertContains(UnifiedSet.<String>newSet(), powerSet);
        Verify.assertContains(set, powerSet);
        Verify.assertInstanceOf(ImmutableSet.class, powerSet.getFirst());
        Verify.assertInstanceOf(ImmutableSet.class, powerSet.getLast());
    }

    @Test
    public void cartesianProduct()
    {
        ImmutableSet<String> set = this.classUnderTest().transform(Functions.getToString());
        LazyIterable<Pair<String, String>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSetWith("One", "Two"));
        Verify.assertIterableSize(set.size() * 2, cartesianProduct);
        Assert.assertEquals(
                set,
                cartesianProduct
                        .filter(Predicates.attributeEqual(Functions.<String>secondOfPair(), "One"))
                        .transform(Functions.<String>firstOfPair()).toSet());
    }
}
