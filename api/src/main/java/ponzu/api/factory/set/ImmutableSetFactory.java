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

package ponzu.api.factory.set;

import ponzu.api.set.ImmutableSet;

public interface ImmutableSetFactory
{
    <T> ImmutableSet<T> of();

    <T> ImmutableSet<T> of(T one);

    <T> ImmutableSet<T> of(T one, T two);

    <T> ImmutableSet<T> of(T one, T two, T three);

    <T> ImmutableSet<T> of(T one, T two, T three, T four);

    <T> ImmutableSet<T> of(T... items);

    <T> ImmutableSet<T> ofAll(Iterable<? extends T> items);
}
