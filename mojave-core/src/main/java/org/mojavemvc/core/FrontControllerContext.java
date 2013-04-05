/*
 * Copyright (C) 2011-2013 Mojavemvc.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojavemvc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * And instance of this class is thread-safe. 
 * 
 * @author Luis Antunes
 */
public class FrontControllerContext implements ControllerContext {

    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }
}
