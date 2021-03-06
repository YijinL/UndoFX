package org.fxmisc.undo;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.fxmisc.undo.impl.ChangeQueue;
import org.fxmisc.undo.impl.FixedSizeChangeQueue;
import org.fxmisc.undo.impl.UndoManagerImpl;
import org.fxmisc.undo.impl.UnlimitedChangeQueue;
import org.fxmisc.undo.impl.ZeroSizeChangeQueue;
import org.reactfx.EventStream;

public interface UndoManagerFactory {

    <C> UndoManager create(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply);

    <C> UndoManager create(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply,
            BiFunction<C, C, Optional<C>> merge);

    public static <C> UndoManager unlimitedHistoryUndoManager(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply) {
        ChangeQueue<C> queue = new UnlimitedChangeQueue<C>();
        BiFunction<C, C, Optional<C>> merge = (c1, c2) -> Optional.empty();
        return new UndoManagerImpl<>(queue, invert, apply, merge, changeStream);
    }

    public static <C> UndoManager unlimitedHistoryUndoManager(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply,
            BiFunction<C, C, Optional<C>> merge) {
        ChangeQueue<C> queue = new UnlimitedChangeQueue<C>();
        return new UndoManagerImpl<>(queue, invert, apply, merge, changeStream);
    }

    public static UndoManagerFactory unlimitedHistoryFactory() {
        return new UndoManagerFactory() {
            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply) {
                return unlimitedHistoryUndoManager(changeStream, invert, apply);
            }

            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply,
                    BiFunction<C, C, Optional<C>> merge) {
                return unlimitedHistoryUndoManager(changeStream, invert, apply, merge);
            }
        };
    }

    public static <C> UndoManager fixedSizeHistoryUndoManager(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply,
            int capacity) {
        ChangeQueue<C> queue = new FixedSizeChangeQueue<C>(capacity);
        BiFunction<C, C, Optional<C>> merge = (c1, c2) -> Optional.empty();
        return new UndoManagerImpl<>(queue, invert, apply, merge, changeStream);
    }

    public static <C> UndoManager fixedSizeHistoryUndoManager(
            EventStream<C> changeStream,
            Function<? super C, ? extends C> invert,
            Consumer<C> apply,
            BiFunction<C, C, Optional<C>> merge,
            int capacity) {
        ChangeQueue<C> queue = new FixedSizeChangeQueue<C>(capacity);
        return new UndoManagerImpl<>(queue, invert, apply, merge, changeStream);
    }

    public static UndoManagerFactory fixedSizeHistoryFactory(int capacity) {
        return new UndoManagerFactory() {
            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply) {
                return fixedSizeHistoryUndoManager(changeStream, invert, apply, capacity);
            }

            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply,
                    BiFunction<C, C, Optional<C>> merge) {
                return fixedSizeHistoryUndoManager(changeStream, invert, apply, merge, capacity);
            }
        };
    }

    public static <C> UndoManager zeroHistoryUndoManager(EventStream<C> changeStream) {
        ChangeQueue<C> queue = new ZeroSizeChangeQueue<>();
        return new UndoManagerImpl<>(queue, c -> c, c -> {}, (c1, c2) -> Optional.empty(), changeStream);
    }

    public static UndoManagerFactory zeroHistoryFactory() {
        return new UndoManagerFactory() {
            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply) {
                return zeroHistoryUndoManager(changeStream);
            }

            @Override
            public <C> UndoManager create(
                    EventStream<C> changeStream,
                    Function<? super C, ? extends C> invert,
                    Consumer<C> apply,
                    BiFunction<C, C, Optional<C>> merge) {
                return zeroHistoryUndoManager(changeStream);
            }
        };
    }
}
