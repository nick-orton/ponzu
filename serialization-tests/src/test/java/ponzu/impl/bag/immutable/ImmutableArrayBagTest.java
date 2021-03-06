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

import ponzu.impl.test.Verify;
import org.junit.Test;

public class ImmutableArrayBagTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                "rO0ABXNyAFljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5iYWcuaW1tdXRhYmxlLkFic3RyYWN0SW1t\n"
                        + "dXRhYmxlQmFnJEltbXV0YWJsZUJhZ1NlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3BAAA\n"
                        + "AAJ0AANPbmV3BAAAAAF0AANUd293BAAAAAF4",
                ImmutableArrayBag.newBagWith("One", "Two"));
    }
}
