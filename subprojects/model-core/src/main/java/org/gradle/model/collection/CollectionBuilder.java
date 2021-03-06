/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.model.collection;

import org.gradle.api.Action;
import org.gradle.api.Incubating;

/**
 * Allows the adding of items to a named collection where instantiation is managed.
 *
 * @param <T> the contract type for all items
 */
@Incubating
public interface CollectionBuilder<T> {

    /**
     * Creates an item with the given name and type T
     *
     * @param name The name.
     */
    // TODO - exception when no default type
    void create(String name);

    /**
     * Creates an item with the given name and type T.
     *
     * @param name The name.
     * @param configAction An action that initialises the item. The action is executed when the item is required.
     */
    // TODO - exception when no default type
    void create(String name, Action<? super T> configAction);

    /**
     * Creates an item with the given name and type.
     *
     * @param name The name.
     */
    // TODO - exception when type cannot be created
    <S extends T> void create(String name, Class<S> type);

    /**
     * Creates an item with the given name and type.
     *
     * @param name The name.
     * @param configAction An action that initialises the item. The action is executed when the item is required.
     */
    // TODO - exception when type cannot be created
    <S extends T> void create(String name, Class<S> type, Action<? super S> configAction);

}
