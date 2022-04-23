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

package com.gdi.jfxfractals.common.task.impl;

import com.gdi.jfxfractals.common.task.IncrementalListener;
import com.gdi.jfxfractals.common.task.ProgressListener;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This {@link IncrementalListener} updates a {@link ProgressListener}.
 * The progress is sets to the current total divided by the expected maximum.
 */
public class ProgressIncrementalListener implements IncrementalListener {
	/**
	 * The delegate progress listener.
	 */
	public final ProgressListener delegate;

	/**
	 * The expected maximum.
	 */
	public final long maximum;

	/**
	 * Whether this listener will be cancelled when reaching the maximum.
	 */
	public final boolean limit;

	private final AtomicLong total = new AtomicLong();

	/**
	 * Creates a {@link ProgressIncrementalListener}.
	 *
	 * @param delegate The delegate progress listener.
	 * @param maximum  The expected maximum.
	 * @param limit    Whether this listener will be cancelled when reaching the maximum.
	 */
	public ProgressIncrementalListener(ProgressListener delegate, long maximum, boolean limit) {
		if (delegate == null)
			throw new IllegalArgumentException("delegate");

		this.delegate = delegate;
		this.maximum = maximum <= 0 ? 0 : maximum;
		this.limit = limit;

		this.delegate.setProgress(this.maximum == 0 ? ProgressListener.INDETERMINATE : 0);
	}

	@Override
	public long getTotal() {
		return this.total.get();
	}

	@Override
	public void increment(long value) {
		if (value < 0)
			throw new IllegalArgumentException("negative value");

		long result = this.total.addAndGet(value);
		if (this.maximum != 0) {
			if (result >= this.maximum) {
				this.delegate.setProgress(1);
				if (this.limit)
					this.delegate.cancel();
			} else {
				this.delegate.setProgress(result / (double) this.maximum);
			}
		}
	}

	@Override
	public boolean isCancellable() {
		return this.delegate.isCancellable();
	}

	@Override
	public void setCancellable(boolean value) {
		this.delegate.setCancellable(value);
	}

	@Override
	public boolean isCancelled() {
		return this.delegate.isCancelled();
	}

	@Override
	public void setCancelled(boolean value) {
		this.delegate.setCancelled(value);
	}
}
