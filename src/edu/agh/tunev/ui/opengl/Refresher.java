package edu.agh.tunev.ui.opengl;

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

	public Refresher(GLAutoDrawable drawable) {
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
			}
		} finally {
			lock.unlock();
		}
	}

}
