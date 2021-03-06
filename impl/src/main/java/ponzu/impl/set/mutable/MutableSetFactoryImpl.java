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

package ponzu.impl.set.mutable;

import ponzu.api.factory.set.MutableSetFactory;
import ponzu.api.set.MutableSet;

public final class MutableSetFactoryImpl implements MutableSetFactory
{
    public <T> MutableSet<T> of()
    {
        return UnifiedSet.newSet();
    }

    public <T> MutableSet<T> of(T... items)
    {
        return UnifiedSet.newSetWith(items);
    }

    public <T> MutableSet<T> ofAll(Iterable<? extends T> items)
    {
        return UnifiedSet.newSet(items);
    }
}
