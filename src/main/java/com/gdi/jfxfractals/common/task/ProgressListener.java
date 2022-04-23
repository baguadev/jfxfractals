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

package com.gdi.jfxfractals.common.task;

import com.gdi.jfxfractals.common.task.impl.ProgressIncrementalListener;

/**
 * A {@link BaseListener} with a progress.
 */
public interface ProgressListener extends BaseListener, IncrementalListenerProvider {
	double INDETERMINATE = -1;

	/**
	 * Gets the progress.
	 * <p>
	 * &lt; 0 means indeterminate.
	 * = 0 means no progress.
	 * &ge; 1 means completed.
	 *
	 * @return The progress.
	 */
	double getProgress();

	/**
	 * Sets the progress.
	 *
	 * @param value The progress.
	 */
	void setProgress(double value);

	@Override
	default IncrementalListener limit(long total) {
		return new ProgressIncrementalListener(this, total, true);
	}

	@Override
	default IncrementalListener expect(long total) {
		return new ProgressIncrementalListener(this, total, false);
	}
}
