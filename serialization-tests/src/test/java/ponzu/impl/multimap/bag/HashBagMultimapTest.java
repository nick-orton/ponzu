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

package ponzu.impl.multimap.bag;

import ponzu.api.multimap.MutableMultimap;
import ponzu.impl.multimap.MutableMultimapSerializationTestCase;

public class HashBagMultimapTest
        extends MutableMultimapSerializationTestCase
{
    @Override
    protected MutableMultimap<String, String> createEmpty()
    {
        return HashBagMultimap.newMultimap();
    }

    @Override
    protected String getSerializedForm()
    {
        return "rO0ABXNyADRjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tdWx0aW1hcC5iYWcuSGFzaEJhZ011bHRp\n"
                + "bWFwAAAAAAAAAAEMAAB4cHcEAAAAAnQAAUF3BAAAAAJxAH4AAncEAAAAAXQAAUJ3BAAAAAJxAH4A\n"
                + "A3cEAAAAAXEAfgACdwQAAAABeA==";
    }
}
