/*
 * Copyright (c) 2017-2021 Hugo Dupanloup (Yeregorix)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gdi.jfxfractals.common.event.resource;

import com.gdi.jfxfractals.common.event.Event;
import com.gdi.jfxfractals.common.resource.ResourceModule;
import com.gdi.jfxfractals.common.resource.ResourcePack;

/**
 * Posted when a module in a pack has been inserted or removed.
 */
public class ResourceModuleChangeEvent implements Event {
	/**
	 * The pack.
	 */
	public final ResourcePack pack;

	/**
	 * The removed module.
	 */
	public final ResourceModule<?> prevModule;

	/**
	 * The inserted module.
	 */
	public final ResourceModule<?> newModule;

	/**
	 * Creates a new {@link ResourceModuleChangeEvent}.
	 *
	 * @param pack       The resource pack.
	 * @param prevModule The removed module.
	 * @param newModule  The inserted module.
	 */
	public ResourceModuleChangeEvent(ResourcePack pack, ResourceModule<?> prevModule, ResourceModule<?> newModule) {
		if (pack == null)
			throw new IllegalArgumentException("pack");
		if (prevModule == newModule)
			throw new IllegalArgumentException("No change");
		if (prevModule != null && newModule != null && prevModule.type != newModule.type)
			throw new IllegalArgumentException("Module type mismatch");

		this.pack = pack;
		this.prevModule = prevModule;
		this.newModule = newModule;
	}
}
