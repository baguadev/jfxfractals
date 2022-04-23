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

package com.gdi.jfxfractals.common.app;

import javafx.scene.Scene;
import javafx.stage.Stage;
import com.gdi.jfxfractals.common.event.EventListener;
import com.gdi.jfxfractals.common.event.EventManager;
import com.gdi.jfxfractals.common.event.ListenerRegistration;
import com.gdi.jfxfractals.common.event.app.ApplicationStateChangeEvent;
import com.gdi.jfxfractals.common.resource.ResourceManager;
import com.gdi.jfxfractals.common.util.ResourceLoader;


import java.util.concurrent.ExecutorService;

/**
 * A state of the application.
 */
public enum State {
	/**
	 * The application is reading arguments,
	 * initializing the {@link ResourceLoader}
	 * and a temporary {@link Logger}.
	 */
	CREATION,

	/**
	 * The application is initializing services:
	 * {@link LogAppender},
	 * {@link EventManager},
	 * {@link ResourceManager},
	 * {@link ExecutorService}.
	 */
	SERVICES_INIT,

	/**
	 * If the UI is enabled, the application is initializing the {@link Stage} and the {@link Scene}.
	 */
	STAGE_INIT,

	/**
	 * The application is running.
	 */
	RUNNING,

	/**
	 * The application is shutting down.
	 */
	SHUTDOWN;

	/**
	 * Creates a new {@link ListenerRegistration} listening for this state.
	 *
	 * @param listener The listener.
	 * @param order    The order.
	 * @return A new listener registration.
	 */
	public ListenerRegistration<ApplicationStateChangeEvent> newListener(EventListener<ApplicationStateChangeEvent> listener, int order) {
		return new ListenerRegistration<>(ApplicationStateChangeEvent.class, e -> {
			if (e.newState == this)
				listener.handle(e);
		}, order);
	}
}
