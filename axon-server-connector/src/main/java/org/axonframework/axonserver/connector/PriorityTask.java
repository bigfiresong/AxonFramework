/*
 * Copyright (c) 2010-2022. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.axonserver.connector;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A wrapper class of {@link Runnable Runnables} that adheres to a priority by implementing {@link Comparable}. Uses a
 * combination of {@code priority} and {@code index} to compare between {@code this} and other priority task instances.
 * A calculator (e.g. {@link org.axonframework.axonserver.connector.command.CommandPriorityCalculator}) defines the
 * priority of the task. This task uses the {@code index} to differentiate between tasks with the same priority,
 * ensuring the insert order is leading in those scenarios.
 *
 * @author Stefan Dragisic
 * @author Milan Savic
 * @author Allard Buijze
 * @author Steven van Beelen
 * @see org.axonframework.axonserver.connector.command.CommandPriorityCalculator
 * @see org.axonframework.axonserver.connector.query.QueryPriorityCalculator
 * @since 4.6.0
 */
public class PriorityTask implements Runnable, Comparable<PriorityTask> {

    private static final AtomicLong COUNTER = new AtomicLong(Long.MIN_VALUE);

    private final Runnable task;
    private final long priority;
    private final long index;

    /**
     * Construct a priority task.
     *
     * @param task     The {@link Runnable} that should be executed with a {@code priority}.
     * @param priority The priority of the {@code task} to execute.
     */
    public PriorityTask(Runnable task, long priority) {
        this.task = task;
        this.priority = priority;
        this.index = COUNTER.incrementAndGet();
    }

    @Override
    public void run() {
        task.run();
    }

    /**
     * Returns the priority of this task.
     *
     * @return The priority of this task.
     */
    public long priority() {
        return priority;
    }

    /**
     * Returns the index of th is task.
     *
     * @return The index of th is task.
     */
    public long index() {
        return index;
    }

    @Override
    public int compareTo(PriorityTask that) {
        int c = Long.compare(this.priority, that.priority);
        if (c != 0) {
            return -c;
        }
        return Long.compare(this.index, that.index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PriorityTask that = (PriorityTask) o;
        return priority == that.priority && index == that.index && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, priority, index);
    }

    @Override
    public String toString() {
        return "PriorityTask{" +
                "task=" + task +
                ", priority=" + priority +
                ", index=" + index +
                '}';
    }
}
