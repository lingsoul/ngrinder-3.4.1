package net.grinder.console.distribution;

import net.grinder.console.model.ConsoleProperties;
import net.grinder.messages.agent.CacheHighWaterMark;
import net.grinder.util.Directory;

import java.util.regex.Pattern;

/**
 * CacheHighWaterMak creator to avoid package protected nature of CacheParametersImplementation.class.
 */
public class CacheHighWaterMarkFactory {
	public static final Pattern DIST_PATTERN = Pattern.compile(ConsoleProperties.DEFAULT_DISTRIBUTION_FILE_FILTER_EXPRESSION);

	public static CacheHighWaterMark createCacheHighWaterMark(Directory directory, long mil) {
		return new CacheParametersImplementationEx(directory, DIST_PATTERN).createHighWaterMark(mil);
	}
}
