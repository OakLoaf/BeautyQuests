package fr.skytasul.quests.api.utils.logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import fr.skytasul.quests.api.utils.messaging.DefaultErrors;

public class LoggerExpanded {

	private final @NotNull Logger logger;
	private final @NotNull ILoggerHandler handler;

	private final Map<Object, Long> times = new HashMap<>();

	public LoggerExpanded(@NotNull Logger logger, @Nullable ILoggerHandler handler) {
		this.logger = logger;
		this.handler = handler == null ? ILoggerHandler.EMPTY_LOGGER : handler;
	}

	public @NotNull ILoggerHandler getHandler() {
		return handler;
	}

	public void info(@Nullable String msg) {
		logger.log(Level.INFO, msg);
	}

	public void info(@Nullable String msg, Object... args) {
		logger.log(Level.INFO, msg, args);
	}

	public void warning(@Nullable String msg) {
		logger.log(Level.WARNING, msg);
	}

	public void warning(@Nullable String msg, Object... args) {
		logger.log(Level.WARNING, msg, args);
	}

	public void warning(@Nullable String msg, @Nullable Throwable throwable) {
		logger.log(Level.WARNING, msg, throwable);
	}

	public void warning(@Nullable String msg, @Nullable Throwable throwable, Object... args) {
		LogRecord logRecord = new LogRecord(Level.WARNING, msg);
		logRecord.setThrown(throwable);
		logRecord.setParameters(args);
		logger.log(logRecord);
	}

	public void warning(@Nullable String msg, @NotNull Object type, int seconds) {
		Long time = times.get(type);
		if (time == null || time.longValue() + seconds * 1000 < System.currentTimeMillis()) {
			logger.warning(msg);
			times.put(type, System.currentTimeMillis());
		}
	}

	public void severe(@Nullable String msg) {
		logger.log(Level.SEVERE, msg);
	}

	public void severe(@Nullable String msg, Object... args) {
		logger.log(Level.SEVERE, msg, args);
	}

	public void severe(@Nullable String msg, @Nullable Throwable throwable) {
		logger.log(Level.SEVERE, msg, throwable);
	}

	public void severe(@Nullable String msg, @Nullable Throwable throwable, Object... args) {
		LogRecord logRecord = new LogRecord(Level.SEVERE, msg);
		logRecord.setThrown(throwable);
		logRecord.setParameters(args);
		logger.log(logRecord);
	}

	public void debug(@Nullable String msg) {
		handler.write(msg, "DEBUG");
	}

	public void debug(@Nullable String msg, Object... args) {
		handler.write(MessageFormat.format(msg, args), "DEBUG");
	}

	public <T> BiConsumer<T, Throwable> logError(@Nullable Consumer<T> consumer, @Nullable String friendlyErrorMessage,
			@Nullable CommandSender sender) {
		return (object, ex) -> {
			if (ex == null) {
				if (consumer != null)
					consumer.accept(object);
			} else {
				if (ex instanceof CompletionException) {
					CompletionException exCompl = (CompletionException) ex;
					if (exCompl.getCause() != null)
						ex = exCompl.getCause();
				}

				if (sender != null)
					DefaultErrors.sendGeneric(sender, friendlyErrorMessage);
				severe(friendlyErrorMessage, ex);
			}
		};
	}

	public <T> BiConsumer<T, Throwable> logError(@Nullable String friendlyErrorMessage, @Nullable CommandSender sender) {
		return logError(null, friendlyErrorMessage, sender);
	}

	public <T> BiConsumer<T, Throwable> logError(@Nullable String friendlyErrorMessage) {
		return logError(null, friendlyErrorMessage, null);
	}

	public <T> BiConsumer<T, Throwable> logError() {
		return logError(null, null, null);
	}

}
