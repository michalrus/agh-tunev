/*
 * Copyright 2013 Kuba Rakoczy, Michał Rus
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
 * 
 */

package edu.agh.tunev.ui.opengl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GLAutoDrawable;

public class Refresher {

	private final GLAutoDrawable drawable;

	private Thread thread;
	private final Lock lock;
	private final Condition condition;
	private boolean refresh;
	private final AtomicBoolean forceRefresh;

	public Refresher(GLAutoDrawable drawable) {
		forceRefresh = new AtomicBoolean(false);

		this.drawable = drawable;

		lock = new ReentrantLock();
		condition = lock.newCondition();
		refresh = false;

		thread = new Thread(new Runnable() {
			public void run() {
				loop();
			}
		});

		thread.start();
	}

	public void refresh() {
		// only refresh while not refreshing =)
		// not refreshing == awaiting
		if (lock.tryLock()) {
			try {
				refresh = true;
				condition.signalAll();
			} finally {
				lock.unlock();
			}
		}
	}

	public void forceRefresh() {
		forceRefresh.set(true);
	}

	private void loop() {
		lock.lock();
		try {
			for (;;) {
				while (!refresh)
					try {
						condition.await();
					} catch (InterruptedException e) {
					}
				refresh = false;
				drawable.display();
				if (forceRefresh.compareAndSet(true, false))
					drawable.display();
			}
		} finally {
			lock.unlock();
		}
	}

}
