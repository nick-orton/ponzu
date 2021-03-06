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

package ponzu.impl.parallel;

import ponzu.api.block.procedure.Procedure;
import ponzu.impl.list.Interval;
import ponzu.impl.math.IntegerSum;
import ponzu.impl.math.LongSum;
import ponzu.impl.math.Sum;
import ponzu.impl.math.SumCombiner;
import ponzu.impl.math.SumProcedure;
import ponzu.impl.test.Verify;
import ponzu.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

public class ParallelArrayIterateTest
{
    @Test
    public void parallelForEach()
    {
        Sum sum1 = new IntegerSum(0);
        Integer[] array1 = this.createIntegerArray(16);
        ParallelArrayIterate.forEach(array1, new SumProcedure<Integer>(sum1), new SumCombiner<Integer>(sum1), 1, array1.length / 2);
        Assert.assertEquals(16, sum1.getValue());

        Sum sum2 = new IntegerSum(0);
        Integer[] array2 = this.createIntegerArray(7);
        ParallelArrayIterate.forEach(array2, new SumProcedure<Integer>(sum2), new SumCombiner<Integer>(sum2));
        Assert.assertEquals(7, sum2.getValue());

        Sum sum3 = new IntegerSum(0);
        Integer[] array3 = this.createIntegerArray(15);
        ParallelArrayIterate.forEach(array3, new SumProcedure<Integer>(sum3), new SumCombiner<Integer>(sum3), 1, array3.length / 2);
        Assert.assertEquals(15, sum3.getValue());

        Sum sum4 = new IntegerSum(0);
        Integer[] array4 = this.createIntegerArray(35);
        ParallelArrayIterate.forEach(array4, new SumProcedure<Integer>(sum4), new SumCombiner<Integer>(sum4));
        Assert.assertEquals(35, sum4.getValue());

        Sum sum5 = new IntegerSum(0);
        Integer[] array5 = this.createIntegerArray(40);
        ParallelArrayIterate.forEach(array5, new SumProcedure<Integer>(sum5), new SumCombiner<Integer>(sum5), 1, array5.length / 2);
        Assert.assertEquals(40, sum5.getValue());
    }

    private Integer[] createIntegerArray(int size)
    {
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++)
        {
            array[i] = 1;
        }
        return array;
    }

    @Test
    public void parallelForEachException()
    {
        Verify.assertThrows(RuntimeException.class, new Runnable()
        {
            public void run()
            {
                ParallelArrayIterate.forEach(
                        Interval.zeroTo(5).toArray(),
                        new PassThruProcedureFactory<Procedure<Object>>(new Procedure<Object>()
                        {
                            public void value(Object object)
                            {
                                throw new RuntimeException("Thread death on its way!");
                            }
                        }),
                        new PassThruCombiner<Procedure<Object>>(),
                        1,
                        5);
            }
        });
    }

    private Sum parallelSum(Object[] array, Sum parallelSum)
    {
        ParallelArrayIterate.forEach(array, new SumProcedure<Object>(parallelSum), new SumCombiner<Object>(parallelSum));
        return parallelSum;
    }

    private void basicTestParallelSums(final Object[] array, final Sum parallelSum1, final Sum parallelSum2)
            throws InterruptedException
    {
        Thread thread1 = new Thread(new Runnable()
        {
            public void run()
            {
                ParallelArrayIterateTest.this.parallelSum(array, parallelSum1);
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable()
        {
            public void run()
            {
                ParallelArrayIterateTest.this.parallelSum(array, parallelSum2);
            }
        });
        thread2.start();
        thread1.join();
        thread2.join();
    }

    @Test
    public void parallelForEachPerformanceOneThread()
    {
        Object[] array = Interval.zeroTo(100).toArray();
        Sum parallelSum = new LongSum(0);
        this.parallelSum(array, parallelSum);
        Sum linearSum = new LongSum(0);
        this.linearSum(array, linearSum);
        Assert.assertEquals(parallelSum, linearSum);
    }

    @Test
    public void parallelForEachPerformanceTwoThreads() throws InterruptedException
    {
        Object[] array = Interval.zeroTo(100).toArray();
        Sum parallelSum1 = new LongSum(0);
        Sum parallelSum2 = new LongSum(0);
        this.basicTestParallelSums(array, parallelSum1, parallelSum2);
        Sum linearSum1 = new LongSum(0);
        Sum linearSum2 = new LongSum(0);
        this.basicTestLinearSums(array, linearSum1, linearSum2);
        Assert.assertEquals(parallelSum1, linearSum1);
        Assert.assertEquals(parallelSum2, linearSum2);
    }

    private void basicTestLinearSums(
            final Object[] array,
            final Sum linearSum1,
            final Sum linearSum2) throws InterruptedException
    {
        Thread thread1 = new Thread(new Runnable()
        {
            public void run()
            {
                ParallelArrayIterateTest.this.linearSum(array, linearSum1);
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable()
        {
            public void run()
            {
                ParallelArrayIterateTest.this.linearSum(array, linearSum2);
            }
        });
        thread2.start();
        thread1.join();
        thread2.join();
    }

    private void linearSum(Object[] array, Sum linearSum)
    {
        ArrayIterate.forEach(array, new SumProcedure<Object>(linearSum));
    }
}
